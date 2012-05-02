package com.variable.node;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.variable.node.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class LumaFragment extends Fragment {

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
	
	//False will be UP state. True will be DOWN state
	private Boolean buttonStateArray[] = new Boolean[8];
	
	private Button buttonPattern1;
	private Button buttonPattern2;
	private Button buttonPattern3;
	
	private Button buttonOn;
	private Button buttonOff;
	private Button buttonLED;
	
	private Boolean[] stateArray = new Boolean[9];
	private Boolean allOn = false;
	private static int NONE = 0;
	private static int ALL = 9;
	
	Node app;
	
	private OnClickListener onButtonClickListener;
	private Activity  context;
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	
	public static String PATTERN1 = "PATTERN1";
	public static String PATTERN2 = "PATTERN2";
	public static String PATTERN3 = "PATTERN3";
	public static String PATTERN1_DEFAULT = "10000001";
	public static String PATTERN2_DEFAULT = "01100110";
	public static String PATTERN3_DEFAULT = "00011000";	
	
	
	private String patternKey = "";
	private Timer timer;
	private TimerTask refresher;

	private int currentPattern = 1;
	
	 /** Called when the fragment is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
                             
		ScrollView view = (ScrollView) inflater.inflate(R.layout.luma, container, false);

        context = getActivity();  
        
        ((MainActivity) context).setFooter(R.id.footer_sensor);
        
        RelativeLayout row1 = (RelativeLayout)view.findViewById(R.id.box); 
        
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_top_in);
        row1.startAnimation(animation);
        
        button1 = (Button)view.findViewById(R.id.button1); 
        button2 = (Button)view.findViewById(R.id.button2); 
        button3 = (Button)view.findViewById(R.id.button3); 
        button4 = (Button)view.findViewById(R.id.button4); 
        button5 = (Button)view.findViewById(R.id.button5); 
        button6 = (Button)view.findViewById(R.id.button6); 
        button7 = (Button)view.findViewById(R.id.button7); 
        button8 = (Button)view.findViewById(R.id.button8); 
        
        //set all states to UP. True = down state
        Arrays.fill(buttonStateArray, Boolean.FALSE);
        
        buttonPattern1 = (Button)view.findViewById(R.id.buttonPattern1); 
        buttonPattern2 = (Button)view.findViewById(R.id.buttonPattern2); 
        buttonPattern3 = (Button)view.findViewById(R.id.buttonPattern3); 
        
        buttonOn = (Button)view.findViewById(R.id.buttonOn);
        buttonOff = (Button)view.findViewById(R.id.buttonOff);
        buttonLED = (Button)view.findViewById(R.id.buttonLED);
        

        onButtonClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				switch(v.getId()){
				
					/*case R.id.button1:						
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
						break;*/
					case R.id.buttonPattern1:
					case R.id.buttonPattern2:
					case R.id.buttonPattern3:
						askForPattern((v.getId())); //send buttonPattern ID
						break;
					case R.id.buttonOn:
						updateLED("11111111");					
						break;
					case R.id.buttonOff:
						updateLED("00000000");
						stopLEDPatternSequence();
						break;
					case R.id.buttonLED:
						/*if(allOn == false)
						{
							
							//reScheduleTimer();	
							allOn = true;
						}
						else{
							//stopLEDPatternSequence();
							allOn = false;
						}*/
						setManualPattern();
						break;
				}
				updateButtonState(v.getId());
				
			}			
		};
		
		button1.setOnClickListener(onButtonClickListener);
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
               
        
        app = (Node)context.getApplication();
        sensor = app.getSensor();
        
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		editor = preferences.edit();
		
		return view;
           
    }
	
	protected void setManualPattern() {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<buttonStateArray.length;i++)
		{
			if(buttonStateArray[i] == true)
				sb.append("1");
			else
				sb.append("0");
		}
	    
		updateLED(sb.toString());
	}

	protected void updateButtonState(int id) {
		
		switch(id){
		
		case R.id.button1:						
			if(buttonStateArray[0] == false)
			{
				button1.setBackgroundResource(R.drawable.button_1_down_state);
				buttonStateArray[0] = true;
			}else{
				button1.setBackgroundResource(R.drawable.button_1);
				buttonStateArray[0] = false;
			}
			break;
		case R.id.button2:
			if(buttonStateArray[1] == false)
			{
				button2.setBackgroundResource(R.drawable.button_2_down_state);
				buttonStateArray[1] = true;
			}else{
				button2.setBackgroundResource(R.drawable.button_2);
				buttonStateArray[1] = false;
			}
			break;
		case R.id.button3:
			if(buttonStateArray[2] == false)
			{
				button3.setBackgroundResource(R.drawable.button_3_down_state);
				buttonStateArray[2] = true;
			}else{
				button3.setBackgroundResource(R.drawable.button_3);
				buttonStateArray[2] = false;
			}
			break;
		case R.id.button4:
			if(buttonStateArray[3] == false)
			{
				button4.setBackgroundResource(R.drawable.button_4_down_state);
				buttonStateArray[3] = true;
			}else{
				button4.setBackgroundResource(R.drawable.button_4);
				buttonStateArray[3] = false;
			}
			break;
		case R.id.button5:
			if(buttonStateArray[4] == false)
			{
				button5.setBackgroundResource(R.drawable.button_5_down_state);
				buttonStateArray[4] = true;
			}else{
				button5.setBackgroundResource(R.drawable.button_5);
				buttonStateArray[4] = false;
			}
			break;
		case R.id.button6:
			if(buttonStateArray[5] == false)
			{
				button6.setBackgroundResource(R.drawable.button_6_down_state);
				buttonStateArray[5] = true;
			}else{
				button6.setBackgroundResource(R.drawable.button_6);
				buttonStateArray[5] = false;
			}
			break;
		case R.id.button7:
			if(buttonStateArray[6] == false)
			{
				button7.setBackgroundResource(R.drawable.button_7_down_state);
				buttonStateArray[6] = true;
			}else{
				button7.setBackgroundResource(R.drawable.button_7);
				buttonStateArray[6] = false;
			}
			break;
		case R.id.button8:
			if(buttonStateArray[7] == false)
			{
				button8.setBackgroundResource(R.drawable.button_8_down_state);
				buttonStateArray[7] = true;
			}else{
				button8.setBackgroundResource(R.drawable.button_8);
				buttonStateArray[7] = false;
			}
			break;
		default:
			//set all states to UP. True = down state
	        Arrays.fill(buttonStateArray, Boolean.FALSE);
	        button1.setBackgroundResource(R.drawable.button_1);
	        button2.setBackgroundResource(R.drawable.button_2);
	        button3.setBackgroundResource(R.drawable.button_3);
	        button4.setBackgroundResource(R.drawable.button_4);
	        button5.setBackgroundResource(R.drawable.button_5);
	        button6.setBackgroundResource(R.drawable.button_6);
	        button7.setBackgroundResource(R.drawable.button_7);
	        button8.setBackgroundResource(R.drawable.button_8);
			break;
		
	}
		
	}

	@Override
	public void onResume() {
    	super.onResume();
    	
    	BTService = app.getBTService(mHandler);  
    	
    	// Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (BTService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (BTService.getState() == BluetoothService.STATE_NONE) {
              // Start the Bluetooth chat services
            	BTService.start(); // don't need to read, only write
            }
            
        }
        
        updateLED("00000000");// turn off all the lights to start with
		
	}
	
	//Schedule the first time, reSchedule after stopped
	public void reScheduleTimer() {
		
		timer = new Timer();    
        refresher = new MyTimerTask(); 
        // first event immediately,  following after 1/2 seconds each
        timer.scheduleAtFixedRate(refresher, 0, 500); 
	}
	
	private class MyTimerTask extends TimerTask {
		  @Override
		  public void run() {
			  startLEDPatternSequence();
		  }
		}
	
	protected void startLEDPatternSequence() {
		
		if(currentPattern == 1){
			updateLED(preferences.getString(PATTERN1, PATTERN1_DEFAULT));
			currentPattern++;
		}else if(currentPattern == 2){
			updateLED(preferences.getString(PATTERN2, PATTERN2_DEFAULT));
			currentPattern++;
		}else if(currentPattern == 3){
			updateLED(preferences.getString(PATTERN3, PATTERN3_DEFAULT));
			currentPattern = 1;
		}			
		
	}	
	
	private void stopLEDPatternSequence() {
	    	
    	if(timer != null)
        	timer.cancel();
		updateLED("00000000");		
	}
		

	private void askForPattern(final int id) {				
		
		final Dialog dialog = new Dialog(context);

		dialog.setContentView(R.layout.pattern_dialog);
		
		final EditText input = (EditText) dialog.findViewById(R.id.input);
		Button ok = (Button) dialog.findViewById(R.id.ok);
		Button cancel = (Button) dialog.findViewById(R.id.cancel);
		
		cancel.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				dialog.dismiss();			
			}
		});
		
		String previousSave = "";
		
		switch(id){
				
		case R.id.buttonPattern1:
			dialog.setTitle("Pattern 1");
			patternKey = PATTERN1;		
			previousSave = preferences.getString(patternKey, PATTERN1_DEFAULT);
			break;
		case R.id.buttonPattern2:
			dialog.setTitle("Pattern 2");
			patternKey = PATTERN2;
			previousSave = preferences.getString(patternKey, PATTERN2_DEFAULT);
			break;
		case R.id.buttonPattern3:
			dialog.setTitle("Pattern 3");
			patternKey = PATTERN3;
			previousSave = preferences.getString(patternKey, PATTERN3_DEFAULT);
			break;
		
		}
		
		input.setText(previousSave);
		
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(inputIsGood(input.getText().toString()))
				{
					editor.putString(patternKey, input.getText().toString());
					editor.commit();
					dialog.dismiss();	
					
					updateLED(input.getText().toString());
					//Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
											
				}				
				
			}
		});
		
		dialog.show();
		
	}

	//returns true if input is only 1's or 0's. Otherwise false.
	protected boolean inputIsGood(String input) {
		
		int len = input.length();
		
		if(len != 8){
			Toast.makeText(context, "Must be 8 digits long.", Toast.LENGTH_LONG).show();
			return false;
		}
		
	    for(int i=0;i<len;i++) {
	        char c = input.charAt(i);
	        // Test 1's and 0's
	        if('0'==c || c=='1') continue;	       
	        // If we get here, we had an invalid char, fail right away
	        Toast.makeText(context, "Only 1 and 0 are allowed.", Toast.LENGTH_LONG).show();
	        return false;
	    }
	    // All seen chars were valid, succeed
	    return true;
	}

    @Override
    public void onStop() {
        super.onStop();
        
        stopLEDPatternSequence();
        
        if(D) Log.e(TAG, "-- ON STOP --");
    }  

	@Override
    public void onDestroy() {
        super.onDestroy();
        
        // Stop the Bluetooth services
        //if (BTService != null) BTService.stop();
        
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
