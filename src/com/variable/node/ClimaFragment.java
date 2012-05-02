package com.variable.node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.variable.node.R;
import com.variable.node.NodeSensor.Weather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ClimaFragment extends Fragment {

    private static final String TAG = "ClimaActivity";
    private static final boolean D = true;
	
	private BluetoothService BTService = null;
	NodeSensor sensor;
	
	private TextView humidity;
	private TextView barometric;
	private TextView temperature;
	
	private static final int MAX_POINTS = 50;
	
	//Tempurature graph vars 
	private XYMultipleSeriesDataset tempDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer tempRenderer = new XYMultipleSeriesRenderer();
	private XYSeries tempSeries;
	private GraphicalView tempChartView;
	private Double tempMax = 0d;
	private Double tempMin = 0d;
	private List<Double> tempArray;
	
	//Humidity graph vars 
	private XYMultipleSeriesDataset humiDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer humiRenderer = new XYMultipleSeriesRenderer();
	private XYSeries humiSeries;
	private GraphicalView humiChartView;
	private Double humiMax = 0d;
	private Double humiMin = 0d;
	private List<Double> humiArray;
	
	//Pressure graph vars 
	private XYMultipleSeriesDataset presDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer presRenderer = new XYMultipleSeriesRenderer();
	private XYSeries presSeries;
	private GraphicalView presChartView;
	private Double presMax = 0d;
	private Double presMin = 0d;
	private List<Double> presArray;

	Node app;
	private Activity context;
	
	
	/** Called when the fragment is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {

        ScrollView view = (ScrollView) inflater.inflate(R.layout.clima, container, false);
                             
        context = getActivity();  
        ((MainActivity) context).setFooter(R.id.footer_sensor);
        
        RelativeLayout row1 = (RelativeLayout)view.findViewById(R.id.temp); 
        RelativeLayout row2 = (RelativeLayout)view.findViewById(R.id.humi); 
        RelativeLayout row3 = (RelativeLayout)view.findViewById(R.id.baro);
        
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_top_in);
        row1.startAnimation(animation);
        row2.startAnimation(animation);
        row3.startAnimation(animation);
        
        humidity = (TextView)view.findViewById(R.id.humidity); 
        barometric = (TextView)view.findViewById(R.id.barometric); 
        temperature = (TextView)view.findViewById(R.id.temperature); 
               
        
        app = (Node) context.getApplication();
        sensor = app.getSensor();
        
      //Tempurature
        LinearLayout chart1 = (LinearLayout) view.findViewById(R.id.chart1);
        
        tempRenderer = Node.getGraphStyle(tempRenderer, context);
        
        tempChartView = ChartFactory.getLineChartView(context, tempDataset, tempRenderer);

        chart1.addView(tempChartView, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        tempChartView.repaint();
        
        tempSeries = new XYSeries("temp");
        tempDataset.addSeries(tempSeries);
        XYSeriesRenderer renderer1 = new XYSeriesRenderer();
        tempRenderer.addSeriesRenderer(renderer1);
        //renderer.setPointStyle(PointStyle.CIRCLE);
        //renderer.setFillPoints(true);
        renderer1.setLineWidth(4);
        renderer1.setColor(Color.RED);

        tempArray = new ArrayList<Double>();
        
        //Humidity
        LinearLayout chart2 = (LinearLayout) view.findViewById(R.id.chart2);
        
        humiRenderer = Node.getGraphStyle(humiRenderer, context);
        
        humiChartView = ChartFactory.getLineChartView(context, humiDataset, humiRenderer);
        
        chart2.addView(humiChartView, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        humiChartView.repaint();
        
        humiSeries = new XYSeries("humi");
        humiDataset.addSeries(humiSeries);
        XYSeriesRenderer renderer2 = new XYSeriesRenderer();
        humiRenderer.addSeriesRenderer(renderer2);
        //renderer.setPointStyle(PointStyle.CIRCLE);
        //renderer.setFillPoints(true);
        renderer2.setLineWidth(4);
        renderer2.setColor(Color.RED);

        humiArray = new ArrayList<Double>();
        
      	//Pressure
        LinearLayout chart3 = (LinearLayout) view.findViewById(R.id.chart3);
        
        presRenderer = Node.getGraphStyle(presRenderer, context);
        
        presChartView = ChartFactory.getLineChartView(context, presDataset, presRenderer);
        
        chart3.addView(presChartView, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        presChartView.repaint();
        
        presSeries = new XYSeries("pres");
        presDataset.addSeries(presSeries);
        XYSeriesRenderer renderer3 = new XYSeriesRenderer();
        presRenderer.addSeriesRenderer(renderer3);
        //renderer.setPointStyle(PointStyle.CIRCLE);
        //renderer.setFillPoints(true);
        renderer3.setLineWidth(4);
        renderer3.setColor(Color.RED);

        presArray = new ArrayList<Double>();
        
        return view;
    }
	
	@Override
	public void onResume() {
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
        
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
    
    private void updateUI() {
    	
    	Weather weather = sensor.getLatestWeatherObject();

         if(weather != null)
         {
        	 humidity.setText(weather.getHumidity().toString()); 
        	 barometric.setText(weather.getBarometricKPA().toString()); 
        	 temperature.setText(weather.getTemperatureF().toString()); 
        	 
        	 if(weather.getTemperatureF() != 0)
        	 {
        		 
            	 tempArray.add(weather.getTemperatureF());
            	 
            	 if(tempArray.size()>=MAX_POINTS)
            	 {
            		tempArray.remove(tempArray.size()-MAX_POINTS);           		
            	 }
            	 
            	 tempSeries.clear();
            	 int j = 0; // zero base our index regardless of position
            	 for(Double point :tempArray)
            	 {
            		 tempSeries.add(j, point);
            		 j++;
            		 
            	 }
        		 
            	 Double value = weather.getTemperatureF();
            	            
            	 if( tempMax - tempMin < 1 )
	             {
	            	 tempMax = value+1;
	            	 tempMin = value-1;
	            	 
	            	 tempRenderer.setYAxisMin(tempMin);
	            	 tempRenderer.setYAxisMax(tempMax);
	             }	 
            	 else{
            		 if(value < tempMin)
		             {
		            	 tempMin = value;
		            	 tempRenderer.setYAxisMin(tempMin);
		             }
		             if(value > tempMax)
		             {
		            	 tempMax = value;
		            	 tempRenderer.setYAxisMax(tempMax);
		             }
            	 }
            	 
	             tempChartView.repaint();
        	 }
        	 
        	 if(weather.getHumidity() != 0)
        	 {
        		 
            	 humiArray.add(weather.getHumidity());
            	 
            	 if(humiArray.size()>=MAX_POINTS)
            	 {
            		 humiArray.remove(humiArray.size()-MAX_POINTS);           		
            	 }
            	 
            	 humiSeries.clear();
            	 int j = 0; // zero base our index regardless of position
            	 for(Double point :humiArray)
            	 {
            		 humiSeries.add(j, point);
            		 j++;
            		 
            	 }
        		 
            	 Double value = weather.getHumidity();
            	             
	             if(value < humiMin)
	             {
	            	 humiMin = value;
	            	 humiRenderer.setYAxisMin(humiMin);
	             }
	             if(value > humiMax)
	             {
	            	 humiMax = value;
	            	 humiRenderer.setYAxisMax(humiMax);
	             }
	             if(humiMin == humiMax)
	             {
            		 humiMax = value+1;
	            	 humiMin = value-1;
	            	 
	            	 humiRenderer.setYAxisMin(humiMin);
	            	 humiRenderer.setYAxisMax(humiMax);
	             }	 

	             humiChartView.repaint();
        	 }
        	 

        	 if(weather.getHumidity() != 0)
        	 {
        		 
            	 presArray.add(weather.getBarometricKPA());
            	 
            	 if(presArray.size()>=MAX_POINTS)
            	 {
            		 presArray.remove(presArray.size()-MAX_POINTS);           		
            	 }
            	 
            	 presSeries.clear();
            	 int j = 0; // zero base our index regardless of position
            	 for(Double point :presArray)
            	 {
            		 presSeries.add(j, point);
            		 j++;
            		 
            	 }
        		 
            	 Double value = weather.getBarometricKPA();
            	              
	             if(value < presMin)
	             {
	            	 presMin = value;
	            	 presRenderer.setYAxisMin(presMin);
	             }
	             if(value > presMax)
	             {
	            	 presMax = value;
	            	 presRenderer.setYAxisMax(presMax);
	             }
	             if(presMin == presMax)
	             {
            		 presMax = value+1;
            		 presMin = value-1;
            		 
            		 presRenderer.setYAxisMin(presMin);
            		 presRenderer.setYAxisMax(presMax);
	             }	

	             presChartView.repaint();
        	 }
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
                	//BTService.setReadContinually(true);
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
