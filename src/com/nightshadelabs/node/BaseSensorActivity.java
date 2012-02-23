package com.nightshadelabs.node;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class BaseSensorActivity extends Activity {


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
	 }
	
	@Override
	protected void onResume() {
    	super.onResume();
    	
    	// Get local Bluetooth adapter
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		// If BT is not on, show connect screen
        if (!mBluetoothAdapter.isEnabled()) {
        	Intent i = new Intent(this,BluetoothActivity.class);					
        	startActivity(i);
        }
                
        //stop all active sensors starting new connection        
		
	}

}
