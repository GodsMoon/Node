package com.nightshadelabs.node;

import com.nightshadelabs.node.NodeSensor.Weather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class ClimaActivity extends BaseSensorActivity {

    private static final String TAG = "ClimaActivity";
    private static final boolean D = true;
	
	private BluetoothService BTService = null;
	NodeSensor sensor;
	
	private TextView humidity;
	private TextView barometric;
	private TextView temperature;
	
	Node app;
	private Context context;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clima);
                             
        context = this;      
        
        humidity = (TextView)findViewById(R.id.humidity); 
        barometric = (TextView)findViewById(R.id.barometric); 
        temperature = (TextView)findViewById(R.id.temperature); 
               
        
        app = (Node)getApplication();
        sensor = app.getSensor();
    }
	
	@Override
	protected void onResume() {
    	super.onResume();
    	
    	BTService = app.getBTService(mHandler);  
    	
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
        BTService.stopWeather();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // Stop the Bluetooth services
        //if (BTService != null) BTService.stop();
        
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
    
    private void updateUI() {
    	
    	Weather weather = sensor.getLatestWeatherObject();

         if(weather != null)
         {
        	 humidity.setText(weather.getHumidity().toString()); 
        	 barometric.setText(weather.getBarometricKPA().toString()); 
        	 temperature.setText(weather.getTemperatureF().toString()); 
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
                	
                	BTService.startWeather();
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
