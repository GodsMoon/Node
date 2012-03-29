package com.nightshadelabs.node;

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

import com.nightshadelabs.node.NodeSensor.Weather;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ClimaActivity extends BaseSensorActivity {

    private static final String TAG = "ClimaActivity";
    private static final boolean D = true;
	
	private BluetoothService BTService = null;
	NodeSensor sensor;
	
	private TextView humidity;
	private TextView barometric;
	private TextView temperature;
	
	private static final int MAX_POINTS = 200;
	
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
        
      //Tempurature
        LinearLayout chart1 = (LinearLayout) findViewById(R.id.chart1);
        
        tempRenderer = Node.getGraphStyle(tempRenderer);
        
        tempChartView = ChartFactory.getLineChartView(this, tempDataset, tempRenderer);

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
        LinearLayout chart2 = (LinearLayout) findViewById(R.id.chart2);
        
        humiRenderer = Node.getGraphStyle(humiRenderer);
        
        humiChartView = ChartFactory.getLineChartView(this, humiDataset, humiRenderer);
        
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
        LinearLayout chart3 = (LinearLayout) findViewById(R.id.chart3);
        
        presRenderer = Node.getGraphStyle(presRenderer);
        
        presChartView = ChartFactory.getLineChartView(this, presDataset, presRenderer);
        
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
        
        /*List<double[]> x = new ArrayList<double[]>();
        for (int i = 0; i < 4; i++) {
          x.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
        }
        List<double[]> values = new ArrayList<double[]>();
        values.add(new double[] { 12.3, 12.5, 13.8, 16.8, 20.4, 24.4, 26.4, 26.1, 23.6, 20.3, 17.2,
            13.9 });
        values.add(new double[] { 10, 10, 12, 15, 20, 24, 26, 26, 23, 18, 14, 11 });
        values.add(new double[] { 5, 5.3, 8, 12, 17, 22, 24.2, 24, 19, 15, 9, 6 });
        values.add(new double[] { 9, 10, 11, 15, 19, 23, 26, 25, 22, 18, 13, 10 });
        int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW };
        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND,
            PointStyle.TRIANGLE, PointStyle.SQUARE };
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
        int length = renderer.getSeriesRendererCount();
        for (int i = 0; i < length; i++) {
          ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
        }
        setChartSettings(renderer, "Average temperature", "Month", "Temperature", 0.5, 12.5, -10, 40,
            Color.LTGRAY, Color.LTGRAY);
        */
        
        
        
        
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
	             if(tempMin == tempMax)
	             {
	            	 tempMax = value+1;
	            	 tempMin = value-1;
	            	 
	            	 tempRenderer.setYAxisMin(tempMin);
	            	 tempRenderer.setYAxisMax(tempMax);
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
