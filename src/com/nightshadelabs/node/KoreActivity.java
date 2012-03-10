package com.nightshadelabs.node;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.nightshadelabs.node.NodeSensor.Accelerometer;
import com.nightshadelabs.node.NodeSensor.Gyroscope;
import com.nightshadelabs.node.NodeSensor.Magnetometer;

public class KoreActivity extends BaseSensorActivity {

	 // Debugging
    private static final String TAG = "KoreActivity";
    private static final boolean D = true;
	
	Context context;
	//Member object for the BT services
	private BluetoothService BTService = null;
	
	private TextView accelaX;
	private TextView accelaY;
	private TextView accelaZ;
	
	private TextView magnaX;
	private TextView magnaY;
	private TextView magnaZ;
	
	private TextView gyraA;
	private TextView gyraB;
	private TextView gyraG;
	
	NodeSensor sensor;
	Node app;	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kore);
      
        context = this;      
        
        accelaX = (TextView)findViewById(R.id.accelaX); 
        accelaY = (TextView)findViewById(R.id.accelaY); 
        accelaZ = (TextView)findViewById(R.id.accelaZ); 
        
        magnaX = (TextView)findViewById(R.id.magnaX);
        magnaY = (TextView)findViewById(R.id.magnaY);
        magnaZ = (TextView)findViewById(R.id.magnaZ);
        
        gyraA = (TextView)findViewById(R.id.gyraA); 
        gyraB = (TextView)findViewById(R.id.gyraB); 
        gyraG = (TextView)findViewById(R.id.gyraG); 
        
        app = (Node)getApplication();
        sensor = app.getSensor();
        
    }

    @Override
	protected void onResume() {
    	super.onResume();
    	
    	BTService = app.getBTService(mHandler);    	
    	
    	
    	// Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (BTService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (BTService.getState() == BluetoothService.STATE_NONE) {
              // Start the Bluetooth chat services
            	BTService.start(); 
            }

        }
		
	}

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
        
        BTService.stopAllCoreSensors();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // Stop the Bluetooth services
        //if (BTService != null) BTService.stop();
        
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
    
    private void updateUI() {
         
         Accelerometer accel = sensor.getLatestAccelerometerObject();
         if(accel != null)
         {
             accelaX.setText("X " +accel.x); 
             accelaY.setText("Y " +accel.y); 
             accelaZ.setText("Z " +accel.z); 
             
         }   
         
         Magnetometer magnet = sensor.getLatestMagnetometerObject();
         if(magnet != null)
         {
        	 magnaX.setText("X " +magnet.x); 
        	 magnaY.setText("Y " +magnet.y); 
        	 magnaZ.setText("Z " +magnet.z); 
         } 
         
         Gyroscope gyro = sensor.getLatestGyroscopeObject();
         if(gyro != null)
         {
        	 gyraA.setText("A " +gyro.a); 
        	 gyraB.setText("B " +gyro.b); 
        	 gyraG.setText("G " +gyro.g); 
         } 
                           
    }
    
   
   
	// The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case Node.MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                
                if(msg.arg1 ==  BluetoothService.STATE_CONNECTED) {
                	
                	//BTService.setReadContinually(true);
                	BTService.startAllCoreSensors();                	
                }               
                
                break;           
            case  Node.MESSAGE_READ:
            
            	updateUI();            
                break;            
            case  Node.MESSAGE_ERROR:
            	Intent i = new Intent(context,BluetoothActivity.class);	
				startActivity(i);
                break;
            }
        }
    };           
    
}
