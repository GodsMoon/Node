package com.variable.node;

import com.variable.node.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.provider.Settings;

public class BluetoothActivity extends Activity {

	Activity context;
	private BluetoothService BTService;
	private BluetoothAdapter mBluetoothAdapter;

	private static final int REQUEST_ENABLE_BT = 0;	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth);

		context = this;

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported on this device
		if (mBluetoothAdapter == null) {
			setStatus(R.string.no_bluetooth);          
		}

		Button connect = (Button)findViewById(R.id.connect);

		connect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// If BT is not on, request that it be enabled.
				// setupChat() will then be called during onActivityResult
				if (!mBluetoothAdapter.isEnabled()) {
					Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
					// Otherwise, setup the chat session
				} else {
					connectToBluetooth();        	
				}

			}
		});        
	}

	private void connectToBluetooth() {

		Node app = (Node)getApplication();
		BTService = app.getBTService(mHandler);  

		BTService.start(); 

	}

	// The Handler that gets information back from the BluetoothService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == Node.MESSAGE_STATE_CHANGE){

				//if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothService.STATE_CONNECTED:

					finish();
					
					break;

				case BluetoothService.STATE_CONNECTING:
					setStatus(R.string.title_connecting);
					break;
				}

			}
			else if(msg.what == Node.MESSAGE_ERROR){

				setStatus(R.string.no_node);
				
				Intent serverIntent = new Intent(context, DeviceListActivity.class);
				startActivity(serverIntent);

			}    		
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//if(D) Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {

		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a BT session
				connectToBluetooth();     
			} else {
				// User did not enable Bluetooth or an error occurred

				setStatus(R.string.enable_bt);
			}
		}
	}

	protected void setStatus(int stringID) {
		Toast.makeText(this, getString(stringID), Toast.LENGTH_SHORT).show();

	}

}
