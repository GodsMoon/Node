package com.nightshadelabs.node;

import android.app.Activity;
import android.app.Application;
import android.os.Handler;

public class Node extends Application{

	// Message types sent from the Activity Handlers
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_ERROR = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	private NodeSensor sensor = null;
	private BluetoothService BTService;

	public NodeSensor getSensor(){
		if(sensor == null)
			sensor = new NodeSensor();

		return sensor;
	}
	
	public BluetoothService getBTService(Handler mHandler){
		if(BTService == null)
		{
			BTService = new BluetoothService(this, mHandler);
		}
		else{
			BTService.stopAllSensors(); //make sure all sensors are stopped before we start new ones
			BTService.setHandler(mHandler);
		}
		
		return BTService;
	}

	
	

}
