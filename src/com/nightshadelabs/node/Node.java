package com.nightshadelabs.node;

import org.achartengine.renderer.XYMultipleSeriesRenderer;


import android.app.Application;
import android.graphics.Color;
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
	
	public static XYMultipleSeriesRenderer getGraphStyle(XYMultipleSeriesRenderer mRenderer)
	{
		mRenderer.setApplyBackgroundColor(false);
        //mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
        //mRenderer.setAxisTitleTextSize(20f);
        //mRenderer.setLabelsColor(Color.CYAN);
        mRenderer.setShowLegend(false);
        mRenderer.setShowLabels(true);
        mRenderer.setShowGrid(true);
        mRenderer.setGridColor(Color.WHITE);
        mRenderer.setLabelsTextSize(20f);
        mRenderer.setLabelsColor(Color.WHITE);
        mRenderer.setXLabels(0);
        mRenderer.setYLabels(4);
        mRenderer.setShowAxes(true);
        mRenderer.setAxesColor(Color.WHITE);
        //mRenderer.setShowGridY(true);
        mRenderer.setApplyBackgroundColor(false);
        mRenderer.setZoomEnabled(false);
        mRenderer.setPanEnabled(false);
        
        mRenderer.setLegendHeight(0);

        //mRenderer.setChartTitleTextSize(20);
        //mRenderer.setLabelsTextSize(15);
        //mRenderer.setLegendTextSize(15);
        //mRenderer.setMargins(new int[] { 0, 10, 0, 0 });

        mRenderer.setMarginsColor(Color.argb(0, 50, 50, 50)); //transparent
        mRenderer.setZoomButtonsVisible(false);
        //mRenderer.setPointSize(20);
        
        return mRenderer;
	}

	
	

}
