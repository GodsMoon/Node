package com.nightshadelabs.node;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LumaActivity extends BaseSensorActivity {

	private static final String TAG = "LumaActivity";
    private static final boolean D = true;
	
	private BluetoothService BTService = null;
	NodeSensor sensor;
	
	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	private Button button5;
	private Button button6;
	private Button button7;
	private Button button8;
	
	private Button buttonPattern1;
	private Button buttonPattern2;
	private Button buttonPattern3;
	
	private Button buttonOn;
	private Button buttonOff;
	private Button buttonLED;
	
	Node app;
	
	private OnClickListener onButtonClickListener;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.luma);
                             
        
        button1 = (Button)findViewById(R.id.button1); 
        button2 = (Button)findViewById(R.id.button2); 
        button3 = (Button)findViewById(R.id.button3); 
        button4 = (Button)findViewById(R.id.button4); 
        button5 = (Button)findViewById(R.id.button5); 
        button6 = (Button)findViewById(R.id.button6); 
        button7 = (Button)findViewById(R.id.button7); 
        button8 = (Button)findViewById(R.id.button8); 
        
        buttonPattern1 = (Button)findViewById(R.id.buttonPattern1); 
        buttonPattern2 = (Button)findViewById(R.id.buttonPattern2); 
        buttonPattern3 = (Button)findViewById(R.id.buttonPattern3); 
        
        buttonOn = (Button)findViewById(R.id.buttonOn);
        buttonOff = (Button)findViewById(R.id.buttonOff);
        buttonLED = (Button)findViewById(R.id.buttonLED);
        
        //button1.setOnClickListener(onButtonClickListener);
        button2.setOnClickListener(onButtonClickListener);        
        button3.setOnClickListener(onButtonClickListener);
        button4.setOnClickListener(onButtonClickListener);
        button5.setOnClickListener(onButtonClickListener);
        button6.setOnClickListener(onButtonClickListener);
        button7.setOnClickListener(onButtonClickListener);
        button8.setOnClickListener(onButtonClickListener);
        
        buttonPattern1.setOnClickListener(onButtonClickListener);
        buttonPattern2.setOnClickListener(onButtonClickListener);
        buttonPattern3.setOnClickListener(onButtonClickListener);
        
        buttonOn.setOnClickListener(onButtonClickListener);
        buttonOff.setOnClickListener(onButtonClickListener);
        buttonLED.setOnClickListener(onButtonClickListener);
        
        
        button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				updateLED("00000001");
				
			}
		});
        
        onButtonClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				switch(v.getId()){
				
					case R.id.button1:
						updateLED("00000001");
						break;
					case R.id.button2:
						updateLED("00000010");
						break;
					case R.id.button3:
						updateLED("00000100");
						break;
					case R.id.button4:
						updateLED("00001000");
						break;
					case R.id.button5:
						updateLED("00010000");
						break;
					case R.id.button6:
						updateLED("00100000");
						break;
					case R.id.button7:
						updateLED("01000000");
						break;
					case R.id.button8:
						updateLED("10000000");
						break;
					case R.id.buttonPattern1:
						break;
					case R.id.buttonPattern2:
						break;
					case R.id.buttonPattern3:
						break;
					case R.id.buttonOn:
						updateLED("11111111");
						break;
					case R.id.buttonOff:
						updateLED("00000000");
						break;
					case R.id.buttonLED:
						break;
				}
				
			}
		};
               
        
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // Stop the Bluetooth services
        if (BTService != null) BTService.stop();
        
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
    
    private void updateLED(String led) {
    	
    	String message = "LED:";			
		
		String str = message + led + "\n";
		
		byte[] mBuffer = str.getBytes();			

		
		BTService.write(mBuffer);

    }
   
	// The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case Node.MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                    //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                	//BTService.startWeather();
                	
                    break;
                case BluetoothService.STATE_CONNECTING:
                    //setStatus(R.string.title_connecting);
                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                    //setStatus(R.string.title_not_connected);
                    break;
                }
                break;
            case  Node.MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                //mConversationArrayAdapter.add( writeMessage); //"Me:  " +
                break;
            case  Node.MESSAGE_READ:
                    
                break;            
            case  Node.MESSAGE_ERROR:
                //Toast.makeText(getApplicationContext(), msg.getData().getString(Node.TOAST),Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
}
