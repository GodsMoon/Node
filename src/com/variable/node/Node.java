package com.variable.node;

import org.achartengine.renderer.XYMultipleSeriesRenderer;

import com.variable.node.R;


import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint.Align;
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
			BTService.addHandler(mHandler);
		}
		
		return BTService;
	}
	
	public static XYMultipleSeriesRenderer getGraphStyle(XYMultipleSeriesRenderer mRenderer, Context mContext)
	{
		mRenderer.setApplyBackgroundColor(false);

        mRenderer.setShowLegend(false);
        mRenderer.setShowLabels(true);
        mRenderer.setYLabelsAlign(Align.RIGHT);
        mRenderer.setShowGrid(true);
        mRenderer.setGridColor(Color.WHITE);
        
        //find an appropriate size for the graph text
        Float textSize = mContext.getResources().getDimension(R.dimen.graph_text_size);
        
        mRenderer.setLabelsTextSize(textSize);
        mRenderer.setYLabelsColor(0, Color.WHITE); // this line will probably change with an update library
        mRenderer.setShowXLabels(false);
        mRenderer.setXLabels(0);
        //mRenderer.setShowGridX(false); //line above works better
        
        mRenderer.setYLabels(2);

        mRenderer.setApplyBackgroundColor(false);
        mRenderer.setZoomEnabled(false);
        mRenderer.setPanEnabled(false);
        
        
        //find an appropriate padding for graph labels (other values don't matter)
        int labelPadding = mContext.getResources().getDimensionPixelOffset(R.dimen.graph_margin_left);
        
        mRenderer.setMargins(new int[] { 30, labelPadding, 15, 0 });

        //mRenderer.setMarginsColor(Color.argb(0, 50, 50, 50)); //transparent
        mRenderer.setZoomButtonsVisible(false);
        
        return mRenderer;
	}

	
	public static boolean isTablet(Context context) {
	    return (context.getResources().getConfiguration().screenLayout
	            & Configuration.SCREENLAYOUT_SIZE_MASK)
	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

}
