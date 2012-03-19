package com.nightshadelabs.node;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private org.achartengine.model.XYSeries mCurrentSeries;
	private XYSeriesRenderer mCurrentRenderer;
	private GraphicalView mChartView;
	
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
        
        
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
        mRenderer.setApplyBackgroundColor(false);
        //mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
        mRenderer.setAxisTitleTextSize(0);
        //mRenderer.setLabelsColor(Color.CYAN);
        mRenderer.setShowLegend(false);
        mRenderer.setShowLabels(true);
        mRenderer.setShowGrid(true);
        mRenderer.setShowAxes(false);
        mRenderer.setXLabels(0);
        mRenderer.setYLabels(4);
        mRenderer.setApplyBackgroundColor(false);
        mRenderer.setZoomEnabled(false);
        mRenderer.setPanEnabled(false);
        //mRenderer.setChartTitleTextSize(20);
        //mRenderer.setLabelsTextSize(15);
        //mRenderer.setLegendTextSize(15);
        //mRenderer.setMargins(new int[] { 0, 10, 0, 0 });

        mRenderer.setMarginsColor(Color.argb(0, 50, 50, 50)); //transparent
        mRenderer.setZoomButtonsVisible(false);
        //mRenderer.setPointSize(20);
        mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
        
        layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        mChartView.repaint();
        
        XYSeries series = new XYSeries("temp");
        mDataset.addSeries(series);
        mCurrentSeries = series;
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);
        //renderer.setPointStyle(PointStyle.CIRCLE);
        //renderer.setFillPoints(true);
        renderer.setLineWidth(4);
        renderer.setColor(Color.RED);
        mCurrentRenderer = renderer;
        
        mCurrentSeries.add(0, 0);
        mCurrentSeries.add(1, 5);
        mCurrentSeries.add(2, 6);
        mCurrentSeries.add(3, 6);
        mCurrentSeries.add(4, 3);
        
        mCurrentSeries.clear();
        
        mCurrentSeries.add(0, 0);
        mCurrentSeries.add(1, 5);
        
        mChartView.repaint();
        
        
     // Initialize our XYPlot reference:
      /*  XYPlot mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
 
        // Create two arrays of y-values to plot:
        Number[] series1Numbers = {1, 8, 5, 2, 7, 4};
        Number[] series2Numbers = {4, 6, 3, 8, 2, 10};
        
        mySimpleXYPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        mySimpleXYPlot.getGraphWidget().getGridLinePaint().setColor(Color.WHITE);
        //mySimpleXYPlot.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
        //mySimpleXYPlot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        //mySimpleXYPlot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
        //mySimpleXYPlot.getGraphWidget().setMarginRight(5);
 
        //mySimpleXYPlot.setBorderStyle(null, null, null);
        //mySimpleXYPlot.getBorderPaint().setStrokeWidth(1);
        //mySimpleXYPlot.getBorderPaint().setAntiAlias(false);
        //mySimpleXYPlot.getBorderPaint().setColor(Color.WHITE);
 
        // Turn the above arrays into XYSeries:
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                "");                             // Set the display title of the series
 
        // Same as above, for series2
        XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, 
                "Series2");
 
        // Create a formatter to use for drawing a series using LineAndPointRenderer:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(
                Color.rgb(0, 200, 0),                   // line color
                Color.rgb(0, 200, 0),                   // point color
                null);              // fill color (optional)
 
        
        // Add series1 to the xyplot:
        mySimpleXYPlot.addSeries(series1, series1Format);
 
        // Same as above, with series2:
        //mySimpleXYPlot.addSeries(series2, new LineAndPointFormatter(Color.rgb(0, 0, 200), Color.rgb(0, 0, 100),
                //Color.rgb(150, 150, 190)));
 
 
        // Reduce the number of range labels
        mySimpleXYPlot.setTicksPerRangeLabel(1);
 
        // By default, AndroidPlot displays developer guides to aid in laying out your plot.
        // To get rid of them call disableAllMarkup():
        mySimpleXYPlot.disableAllMarkup();
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
