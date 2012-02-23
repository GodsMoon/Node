package com.nightshadelabs.node;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends Activity {
	
	Context context;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        context = this;
        
        Button kore = (Button)findViewById(R.id.koreButton);
        Button clima = (Button)findViewById(R.id.climaButton);
        Button luma = (Button)findViewById(R.id.lumaButton);
        
        kore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(context,KoreActivity.class);				
				startActivity(i);
				
			}
		});
        
        clima.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(context,ClimaActivity.class);				
				startActivity(i);
				
			}
		});
        
        luma.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent i = new Intent(context,LumaActivity.class);				
				startActivity(i);
				
			}
		});
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		// Get local Bluetooth adapter
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		// If BT is not on, show connect screen
        if (!mBluetoothAdapter.isEnabled()) {
        	Intent i = new Intent(context,BluetoothActivity.class);					
        	startActivity(i);
        }
	}
    
}