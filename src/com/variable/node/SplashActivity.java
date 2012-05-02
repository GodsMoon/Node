package com.variable.node;

import com.variable.node.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SplashActivity extends Activity {

	Context context;
	private BluetoothService BTService;
	private boolean shouldRecieveMessages;
	
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash); 
        
        context = this;
               
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		// Get local Bluetooth adapter
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			setStatus(R.string.no_bluetooth);          
		}
		
		// If BT is not on, show connect screen
        if (!mBluetoothAdapter.isEnabled()) {
        	Intent i = new Intent(context,BluetoothActivity.class);					
        	startActivity(i);
        // Otherwise, connect to BT servie
        } else {
        	connectToBluetooth();        	
        }
        
        shouldRecieveMessages = true;
		
	}
	
	private void connectToBluetooth() {
		
		Node app = (Node)getApplication();
    	BTService = app.getBTService(mHandler);  
		
        if (BTService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (BTService.getState() == BluetoothService.STATE_NONE) {
            	// Start the Bluetooth services
            	BTService.start();
            }
        }
	}

	// The Handler that gets information back from the BluetoothService
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(shouldRecieveMessages)
			{
				if(msg.what == Node.MESSAGE_STATE_CHANGE){
	
					//if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
					switch (msg.arg1) {
					case BluetoothService.STATE_CONNECTED:
						
						//Foward along to the Main Activity if everything looks good
						setStatus(R.string.title_connected);
						Intent i = new Intent(context,MainActivity.class);	
						startActivity(i);									
						finish();
						
						break;
						
					case BluetoothService.STATE_CONNECTING:
						setStatus(R.string.title_connecting);
						break;
					}
	
				}
				else if(msg.what == Node.MESSAGE_ERROR){
	
					//Open Bluetooth Activity to assist in connecting Node
					setStatus(R.string.no_node);				
					Intent i = new Intent(context, BluetoothActivity.class);	
					startActivity(i);
	
				}    		
			}
		}
	};	

	protected void setStatus(int stringID) {

		Toast.makeText(this, getString(stringID), Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onStop() {		
		super.onStop();
		
		shouldRecieveMessages = false;
	}
	
}
