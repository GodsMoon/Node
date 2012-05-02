/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Adapted from http://developer.android.com/resources/samples/BluetoothChat/index.html by David Shellabarger


package com.variable.node;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.variable.node.NodeSensor;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothService {
    // Debugging
    private static final String TAG = "BluetoothService";
    private static final boolean D = false;

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothSecure";

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard Bluetooth UUID for serial connections
    
    //private static final String NODE_DEVICE_ADDRESS = "00:06:66:43:47:16";//00:06:66:43:47:16
    private static final String NODE_DEVICE_ADDRESS = "00:06:66:43:44:F9";//00:06:66:43:47:16
    public static final String NODE_DEVICE_ADDRESS_KEY = "NODE_DEVICE_ADDRESS_KEY";

    // Member fields
    private final BluetoothAdapter mAdapter;
    private Handler mHandler;
    private List<Handler> mHandlerArray = new ArrayList<Handler>();
    private AcceptThread mSecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    private NodeSensor mSensor;
    private SharedPreferences preferences;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    /**
     * Constructor. Prepares a new Bluetooth session.
     * @param application  The Application object
     * @param handler  A Handler to send messages back to the UI Activity
     */
    public BluetoothService(Node n, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandlerArray.add(handler);
        mHandler = handler;
        
        preferences = PreferenceManager.getDefaultSharedPreferences((Context)n);
        
        mSensor = n.getSensor();
    }
    
    public synchronized void addHandler(Handler handler) {
    	mHandler = handler;
    	mHandlerArray.add(handler);
    	setState(mState); //resend state to new handler
    }

    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
        
        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(Node.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the BT service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void start() {
        if (D) Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        setState(STATE_LISTEN);

        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread();
            mSecureAcceptThread.start();
        }
        
        //BluetoothDevice device = mAdapter.getRemoteDevice(NODE_DEVICE_ADDRESS);
        String address = preferences.getString(NODE_DEVICE_ADDRESS_KEY, NODE_DEVICE_ADDRESS);
        BluetoothDevice device = mAdapter.getRemoteDevice(address);
        connect(device);
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        if (D) Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread because we only want to connect to one device
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        
        stopAllSensors();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(Node.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Node.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Node.MESSAGE_ERROR);
        Bundle bundle = new Bundle();
        bundle.putString(Node.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        
        setState(STATE_NONE);

        // Start the service over to restart listening mode
        //BluetoothService.this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Node.MESSAGE_ERROR);
        Bundle bundle = new Bundle();
        bundle.putString(Node.TOAST, "Device connection was lost");
        msg.setData(bundle);
        //mHandler.sendMessage(msg);

        setState(STATE_NONE);
        // Start the service over to restart listening mode
        //BluetoothService.this.start();
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,MY_UUID_SECURE);

            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (D) Log.d(TAG, "Socket Type: " + mSocketType +
                    "BEGIN mAcceptThread" + this);
            setName("AcceptThread" + mSocketType);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (mState) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            // Situation normal. Start the connected thread.
                            connected(socket, socket.getRemoteDevice());
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            // Either not ready or already connected. Terminate new socket.
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(TAG, "Could not close unwanted socket", e);
                            }
                            break;
                        }
                    }
                }
            }
            if (D) Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);

        }

        public void cancel() {
            if (D) Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;            

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {

                    tmp = device.createRfcommSocketToServiceRecord( MY_UUID_SECURE);

            } catch (IOException e) {
                Log.e(TAG, "Socket: create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread ");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                	
                	try //read in increments for less lag and cleaner outputs
    				{
    					Thread.sleep((long) 500); // accumulate some data before reading for easier log reading
    					
    				} catch (InterruptedException e)
    				{
    					e.printStackTrace();
    				}
                	
                	
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    
                    mSensor.addData(buffer, bytes);
                    
                    String readMessage = new String(buffer, 0,bytes);
                    //Log.e("BTDataRead", readMessage); //raw data from Node

                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(Node.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    // Start the service over to restart listening mode
                    //BluetoothService.this.start();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(Node.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    public void startAllCoreSensors() {
    	 startAccelerometer();
    	 startMagnetometer();
    	 startGyroscope();
	}
    
    public void stopAllCoreSensors() {
    	stopAccelerometer();
    	stopMagnetometer();
    	stopGyroscope();
	}
    
    public void stopAllSensors(){ //TODO add more sensors here as they become available 
    	stopAllCoreSensors();
    	stopWeather();
    }
    
    public static String ACCELEROMETER_ON = "ACC1\n";
    public static String ACCELEROMETER_OFF = "ACC0\n";
    
	public void startAccelerometer() {

         byte[] msgBuffer = ACCELEROMETER_ON.getBytes();
         
         if(mConnectedThread != null)
        	 mConnectedThread.write(msgBuffer);
	}
	
	public void stopAccelerometer() {

        byte[] msgBuffer = ACCELEROMETER_OFF.getBytes();
		
        if(mConnectedThread != null)
        	mConnectedThread.write(msgBuffer);
	}
	
	public static String MAGNETOMETER_ON = "MAG1\n";
    public static String MAGNETOMETER_OFF = "MAG0\n";
    
	public void startMagnetometer() {

         byte[] msgBuffer = MAGNETOMETER_ON.getBytes();
		
         if(mConnectedThread != null)
        	 mConnectedThread.write(msgBuffer);
	}
	
	public void stopMagnetometer() {

        byte[] msgBuffer = MAGNETOMETER_OFF.getBytes();
		
        if(mConnectedThread != null)
        	mConnectedThread.write(msgBuffer);
	}
	
	
	public static String GYROSCOPE_ON = "GYR1\n";
    public static String GYROSCOPE_OFF = "GYR0\n";
    
	public void startGyroscope() {

         byte[] msgBuffer = GYROSCOPE_ON.getBytes();
		
         if(mConnectedThread != null)
        	 mConnectedThread.write(msgBuffer);
	}
	
	public void stopGyroscope() {

        byte[] msgBuffer = GYROSCOPE_OFF.getBytes();
		
        if(mConnectedThread != null)
        	mConnectedThread.write(msgBuffer);
	}
	
	public static String WEATHER_ON = "WEATHER1\n";
    public static String WEATHER_OFF = "WEATHER0\n";
    
	public void startWeather() {

         byte[] msgBuffer = WEATHER_ON.getBytes();
		
         if(mConnectedThread != null)
        	 mConnectedThread.write(msgBuffer);
	}
	
	public void stopWeather() {

        byte[] msgBuffer = WEATHER_OFF.getBytes();
		
        if(mConnectedThread != null)
        	mConnectedThread.write(msgBuffer);
	}
}
