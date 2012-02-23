package com.nightshadelabs.node;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
 
public class BluetoothTest extends Activity {
       
        private static final String TAG = "THINBTCLIENT";
        private static final boolean D = true;
        private BluetoothAdapter mBluetoothAdapter = null;
        private BluetoothSocket btSocket = null;
        private OutputStream outStream = null;
        private InputStream inStream = null;
        // Well known SPP UUID (will *probably* map to
        // RFCOMM channel 1 (default) if not in use);
        // see comments in onResume().
        private static final UUID MY_UUID =
                        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
 
        // ==> hardcode your server's MAC address here <==
        private static String address = "00:06:66:43:47:16"; // keyboard 00:06:66:43:47:0E --- Node 00:06:66:43:47:16
 
        private TextView tv;
        
        /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.blue);
                
               tv = (TextView) findViewById(R.id.textView1);
 
                if (D)
                        Log.e(TAG, "+++ ON CREATE +++");
 
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                        Toast.makeText(this,
                                "Bluetooth is not available.",
                                Toast.LENGTH_LONG).show();
                        finish();
                        return;
                }
 
                if (!mBluetoothAdapter.isEnabled()) {
                        Toast.makeText(this,
                                "Please enable your BT and re-run this program.",
                                Toast.LENGTH_LONG).show();
                        finish();
                        return;
                }
 
                if (D)
                        Log.e(TAG, "+++ DONE IN ON CREATE, GOT LOCAL BT ADAPTER +++");
        }
 
        @Override
        public void onStart() {
                super.onStart();
                if (D)
                        Log.e(TAG, "++ ON START ++");
        }
 
        @Override
        public void onResume() {
                super.onResume();
 
                if (D) {
                        Log.e(TAG, "+ ON RESUME +");
                        Log.e(TAG, "+ ABOUT TO ATTEMPT CLIENT CONNECT +");
                }
 
                // When this returns, it will 'know' about the server,
                // via it's MAC address.
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                
                Set<BluetoothDevice> btds = mBluetoothAdapter.getBondedDevices();
                
                for(BluetoothDevice btd : btds)
                {
                	Log.e(TAG, "Device Name: "+ btd.getAddress() +" : "+btd.getName());
                }
 
                // We need two things before we can successfully connect
                // (authentication issues aside): a MAC address, which we
                // already have, and an RFCOMM channel.
                // Because RFCOMM channels (aka ports) are limited in
                // number, Android doesn't allow you to use them directly;
                // instead you request a RFCOMM mapping based on a service
                // ID. In our case, we will use the well-known SPP Service
                // ID. This ID is in UUID (GUID to you Microsofties)
                // format. Given the UUID, Android will handle the
                // mapping for you. Generally, this will return RFCOMM 1,
                // but not always; it depends what other BlueTooth services
                // are in use on your Android device.
                try {
                        btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                } catch (IOException e) {
                        Log.e(TAG, "ON RESUME: Socket creation failed.", e);
                }
 
                // Discovery may be going on, e.g., if you're running a
                // 'scan for devices' search from your handset's Bluetooth
                // settings, so we call cancelDiscovery(). It doesn't hurt
                // to call it, but it might hurt not to... discovery is a
                // heavyweight process; you don't want it in progress when
                // a connection attempt is made.
                mBluetoothAdapter.cancelDiscovery();
 
                // Blocking connect, for a simple client nothing else can
                // happen until a successful connection is made, so we
                // don't care if it blocks.
                try {
                        btSocket.connect();
                        Log.e(TAG, "ON RESUME: BT connection established, data transfer link open.");
                } catch (IOException e) {
                        try {
                                btSocket.close();
                        } catch (IOException e2) {
                                Log.e(TAG,
                                        "ON RESUME: Unable to close socket during connection failure", e2);
                        }
                }
 
                // Create a data stream so we can talk to server.
                if (D)
                        Log.e(TAG, "+ ABOUT TO SAY SOMETHING TO SERVER +");
 
                try {
                        outStream = btSocket.getOutputStream();
                        inStream = btSocket.getInputStream();
                } catch (IOException e) {
                        Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
                }
 
                String message = "ACC1\n";
                byte[] msgBuffer = message.getBytes();
                
                //msgBuffer[0] = (char) ('c' - 'a' + '\001');
                
                /*try {
                        outStream.write(msgBuffer);
                       
                } catch (IOException e) {
                        Log.e(TAG, "ON RESUME: Exception during write.", e);
                }*/
                                
                // Start the thread to manage the connection and perform transmissions
                ConnectedThread mConnectedThread = new ConnectedThread(btSocket);
                mConnectedThread.start();
                
                mConnectedThread.setTv(tv);
                
                mConnectedThread.write(msgBuffer);
               
        }
               
        @Override
        public void onPause() {
                super.onPause();
 
                if (D)
                        Log.e(TAG, "- ON PAUSE -");
 
                if (outStream != null) {
                        try {
                                outStream.flush();
                        } catch (IOException e) {
                                Log.e(TAG, "ON PAUSE: Couldn't flush output stream.", e);
                        }
                }
 
                try     {
                        btSocket.close();
                } catch (IOException e2) {
                        Log.e(TAG, "ON PAUSE: Unable to close socket.", e2);
                }
        }
 
        @Override
        public void onStop() {
                super.onStop();
                if (D)
                        Log.e(TAG, "-- ON STOP --");
        }
 
        @Override
        public void onDestroy() {
                super.onDestroy();
                if (D)
                        Log.e(TAG, "--- ON DESTROY ---");
        }
        
        
        /**
         * This thread runs during a connection with a remote device.
         * It handles all incoming and outgoing transmissions.
         */
        private class ConnectedThread extends Thread {
            private final BluetoothSocket mmSocket;
            private final InputStream mmInStream;
            private final OutputStream mmOutStream;
            private TextView mTv = null;

            public ConnectedThread(BluetoothSocket socket) {
                Log.d(TAG, "create ConnectedThread");
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
                        // Read from the InputStream
                        bytes = mmInStream.read(buffer,0,25);

                        //mEmulatorView.write(buffer, bytes);
                        // Send the obtained bytes to the UI Activity
                        //mHandler.obtainMessage(BlueTerm.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                        
                        String readMessage = new String(buffer, 0, bytes);
                        
                        Log.e(TAG, "readMessage "+ readMessage);
                        
                        String a = buffer.toString();
                        a = "";
                    } catch (IOException e) {
                        Log.e(TAG, "disconnected", e);
                        //connectionLost();
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
                    //mHandler.obtainMessage(BlueTerm.MESSAGE_WRITE, buffer.length, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "Exception during write", e);
                }
            }

            public void setTv(TextView tv)
            {
            	mTv = tv;
            }
            
            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "close() of connect socket failed", e);
                }
            }
        }
}

