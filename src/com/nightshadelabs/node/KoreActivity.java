package com.nightshadelabs.node;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

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

import com.nightshadelabs.node.NodeSensor.Accelerometer;
import com.nightshadelabs.node.NodeSensor.Gyroscope;
import com.nightshadelabs.node.NodeSensor.Magnetometer;

public class KoreActivity extends BaseSensorActivity {

	 // Debugging
    private static final String TAG = "KoreActivity";
    private static final boolean D = true;
	
	Context context;
	//Member object for the BT services
	private BluetoothService BTService = null;
	
	private TextView accelaX;
	private TextView accelaY;
	private TextView accelaZ;
	
	private TextView magnaX;
	private TextView magnaY;
	private TextView magnaZ;
	
	private TextView gyraA;
	private TextView gyraB;
	private TextView gyraG;
	
	private static final int MAX_POINTS = 200;
	
	//Accela graph vars 
	private XYMultipleSeriesDataset accDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer accRenderer = new XYMultipleSeriesRenderer();
	private XYSeries accXSeries;
	private XYSeries accYSeries;
	private XYSeries accZSeries;
	private List<Double> accXArray = new ArrayList<Double>();
	private List<Double> accYArray = new ArrayList<Double>();
	private List<Double> accZArray = new ArrayList<Double>();
	private GraphicalView accChartView;
	private Double accMax = 0d;
	private Double accMin = 0d;
	
	
	//Magna graph vars 
	private XYMultipleSeriesDataset magDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer magRenderer = new XYMultipleSeriesRenderer();
	private XYSeries magXSeries;
	private XYSeries magYSeries;
	private XYSeries magZSeries;
	private List<Double> magXArray = new ArrayList<Double>();
	private List<Double> magYArray = new ArrayList<Double>();
	private List<Double> magZArray = new ArrayList<Double>();
	private GraphicalView magChartView;
	private Double magMax = 0d;
	private Double magMin = 0d;
	
	
	//Gyra graph vars 
	private XYMultipleSeriesDataset gyrDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer gyrRenderer = new XYMultipleSeriesRenderer();
	private XYSeries gyrASeries;
	private XYSeries gyrBSeries;
	private XYSeries gyrGSeries;
	private List<Double> gyrAArray = new ArrayList<Double>();
	private List<Double> gyrBArray = new ArrayList<Double>();
	private List<Double> gyrGArray = new ArrayList<Double>();
	private GraphicalView gyrChartView;
	private Double gyrMax = 0d;
	private Double gyrMin = 0d;
	
	
	NodeSensor sensor;
	Node app;	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kore);
      
        context = this;      
        
        accelaX = (TextView)findViewById(R.id.accelaX); 
        accelaY = (TextView)findViewById(R.id.accelaY); 
        accelaZ = (TextView)findViewById(R.id.accelaZ); 
        
        magnaX = (TextView)findViewById(R.id.magnaX);
        magnaY = (TextView)findViewById(R.id.magnaY);
        magnaZ = (TextView)findViewById(R.id.magnaZ);
        
        gyraA = (TextView)findViewById(R.id.gyraA); 
        gyraB = (TextView)findViewById(R.id.gyraB); 
        gyraG = (TextView)findViewById(R.id.gyraG); 
        
        app = (Node)getApplication();
        sensor = app.getSensor();
        
        //Accelerometer
        LinearLayout chart1 = (LinearLayout) findViewById(R.id.chart1);
        
        accRenderer = Node.getGraphStyle(accRenderer);
        
        accChartView = ChartFactory.getLineChartView(this, accDataset, accRenderer);

        chart1.addView(accChartView, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        accChartView.repaint();
        
        accXSeries = new XYSeries("accX");
        accDataset.addSeries(accXSeries);
        XYSeriesRenderer rendererRed = new XYSeriesRenderer();
        //renderer.setPointStyle(PointStyle.CIRCLE);
        //renderer.setFillPoints(true);
        rendererRed.setLineWidth(4);
        rendererRed.setColor(Color.RED);
        accRenderer.addSeriesRenderer(rendererRed);
        
        accYSeries = new XYSeries("accY");
        accDataset.addSeries(accYSeries);
        XYSeriesRenderer rendererBlue = new XYSeriesRenderer();
        //renderer.setPointStyle(PointStyle.CIRCLE);
        //renderer.setFillPoints(true);
        rendererBlue.setLineWidth(4);
        rendererBlue.setColor(Color.BLUE);
        accRenderer.addSeriesRenderer(rendererBlue);
        
        accZSeries = new XYSeries("accZ");
        accDataset.addSeries(accZSeries);
        XYSeriesRenderer rendererGreen = new XYSeriesRenderer();
        //renderer.setPointStyle(PointStyle.CIRCLE);
        //renderer.setFillPoints(true);
        rendererGreen.setLineWidth(4);
        rendererGreen.setColor(Color.GREEN);
        accRenderer.addSeriesRenderer(rendererGreen);
        
        //Magnetometer
        LinearLayout chart2 = (LinearLayout) findViewById(R.id.chart2);
        
        magRenderer = Node.getGraphStyle(magRenderer);
        
        magChartView = ChartFactory.getLineChartView(this, magDataset, magRenderer);
        
        chart2.addView(magChartView, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        magChartView.repaint();
        
        magXSeries = new XYSeries("magX");
        magDataset.addSeries(magXSeries);
        magRenderer.addSeriesRenderer(rendererRed);
        
        magYSeries = new XYSeries("magY");
        magDataset.addSeries(magYSeries);
        magRenderer.addSeriesRenderer(rendererBlue);
        
        magZSeries = new XYSeries("magZ");
        magDataset.addSeries(magZSeries);
        magRenderer.addSeriesRenderer(rendererGreen);
        
      	//Gyroscope
        LinearLayout chart3 = (LinearLayout) findViewById(R.id.chart3);
        
        gyrRenderer = Node.getGraphStyle(gyrRenderer);
        
        gyrChartView = ChartFactory.getLineChartView(this, gyrDataset, gyrRenderer);
        
        chart3.addView(gyrChartView, new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));
        gyrChartView.repaint();
        
        gyrASeries = new XYSeries("gyrA");
        gyrDataset.addSeries(gyrASeries);
        gyrRenderer.addSeriesRenderer(rendererRed);
        
        gyrBSeries = new XYSeries("gyrB");
        gyrDataset.addSeries(gyrBSeries);
        gyrRenderer.addSeriesRenderer(rendererBlue);
        
        gyrGSeries = new XYSeries("gyrG");
        gyrDataset.addSeries(gyrGSeries);
        gyrRenderer.addSeriesRenderer(rendererGreen);
        
    }

    @Override
	protected void onResume() {
    	super.onResume();
    	
    	BTService = app.getBTService(mHandler);    	
    	
    	
    	// Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
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
        
        BTService.stopAllCoreSensors();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // Stop the Bluetooth services
        //if (BTService != null) BTService.stop();
        
        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
    
    private void updateUI() {
         
         Accelerometer accel = sensor.getLatestAccelerometerObject();
         if(accel != null)
         {
             accelaX.setText("X " +accel.x); 
             accelaY.setText("Y " +accel.y); 
             accelaZ.setText("Z " +accel.z); 
             
             accXArray.add(accel.getX());
             accYArray.add(accel.getY());
             accZArray.add(accel.getZ());
        	 
        	 if(accXArray.size()>=MAX_POINTS)
        	 {
        		accXArray.remove(accXArray.size()-MAX_POINTS);  
        		accYArray.remove(accYArray.size()-MAX_POINTS);  
        		accZArray.remove(accZArray.size()-MAX_POINTS);  
        	 }
        	 
        	 accXSeries.clear();
        	 accYSeries.clear();
        	 accZSeries.clear();
        	 
        	 int j = 0; // zero base our index regardless of position
        	 for(Double point :accXArray)
        	 {
        		 accXSeries.add(j, point);
        		 j++;
        	 }
        	 
        	 j = 0;
        	 for(Double point :accYArray)
        	 {
        		 accYSeries.add(j, point);
        		 j++;
        	 }
        	 
        	 j = 0;
        	 for(Double point :accZArray)
        	 {
        		 accZSeries.add(j, point);
        		 j++;
        	 }
    		 
        	 Double maxValue = Math.max(Math.max(accel.getX(), accel.getY()), accel.getZ());
        	 Double minValue = Math.min(Math.min(accel.getX(), accel.getY()), accel.getZ());
        	            
             if(minValue < accMin)
             {
            	 accMin = minValue;
            	 accRenderer.setYAxisMin(accMin);
             }
             if(maxValue > accMax)
             {
            	 accMax = maxValue;
            	 accRenderer.setYAxisMax(accMax);
             }
             if(accMin == accMax)
             {
            	 accMax = maxValue+1;
            	 accMin = minValue-1;
            	 
            	 accRenderer.setYAxisMin(accMin);
            	 accRenderer.setYAxisMax(accMax);
             }	 

             accChartView.repaint();  
         }   
         
         Magnetometer magnet = sensor.getLatestMagnetometerObject();
         if(magnet != null)
         {
        	 magnaX.setText("X " +magnet.x); 
        	 magnaY.setText("Y " +magnet.y); 
        	 magnaZ.setText("Z " +magnet.z); 
        	 
        	 magXArray.add(magnet.getX());
        	 magYArray.add(magnet.getY());
        	 magZArray.add(magnet.getZ());
        	 
        	 if(magXArray.size()>=MAX_POINTS)
        	 {
        		magXArray.remove(magXArray.size()-MAX_POINTS);  
        		magYArray.remove(magYArray.size()-MAX_POINTS);  
        		magZArray.remove(magZArray.size()-MAX_POINTS);  
        	 }
        	 
        	 magXSeries.clear();
        	 magYSeries.clear();
        	 magZSeries.clear();
        	 
        	 int j = 0; // zero base our index regardless of position
        	 for(Double point :magXArray)
        	 {
        		 magXSeries.add(j, point);
        		 j++;
        	 }
        	 
        	 j = 0;
        	 for(Double point :magYArray)
        	 {
        		 magYSeries.add(j, point);
        		 j++;
        	 }
        	 
        	 j = 0;
        	 for(Double point :magZArray)
        	 {
        		 magZSeries.add(j, point);
        		 j++;
        	 }
    		 
        	 Double maxValue = Math.max(Math.max(magnet.getX(), magnet.getY()), magnet.getZ());
        	 Double minValue = Math.min(Math.min(magnet.getX(), magnet.getY()), magnet.getZ());
        	             
             if(minValue < magMin)
             {
            	 magMin = minValue;
            	 magRenderer.setYAxisMin(magMin);
             }
             if(maxValue > magMax)
             {
            	 magMax = maxValue;
            	 magRenderer.setYAxisMax(magMax);
             }
             if(magMin == magMax)
             {
            	 magMin = minValue-1;
            	 magMax = maxValue+1;
            	 
            	 magRenderer.setYAxisMin(magMin);
            	 magRenderer.setYAxisMax(magMax);
             }	 

             magChartView.repaint();
         } 
         
         Gyroscope gyro = sensor.getLatestGyroscopeObject();
         if(gyro != null)
         {
        	 gyraA.setText("A " +gyro.a); 
        	 gyraB.setText("B " +gyro.b); 
        	 gyraG.setText("G " +gyro.g); 
        	 
        	 gyrAArray.add(gyro.getA());
        	 gyrBArray.add(gyro.getB());
        	 gyrGArray.add(gyro.getG());
        	 
        	 if(gyrAArray.size()>=MAX_POINTS)
        	 {
        		gyrAArray.remove(gyrAArray.size()-MAX_POINTS);  
        		gyrBArray.remove(gyrBArray.size()-MAX_POINTS);  
        		gyrGArray.remove(gyrGArray.size()-MAX_POINTS);  
        	 }
        	 
        	 gyrASeries.clear();
        	 gyrBSeries.clear();
        	 gyrGSeries.clear();
        	 
        	 int j = 0; // zero base our index regardless of position
        	 for(Double point :gyrAArray)
        	 {
        		 gyrASeries.add(j, point);
        		 j++;
        	 }
        	 
        	 j = 0;
        	 for(Double point :gyrBArray)
        	 {
        		 gyrBSeries.add(j, point);
        		 j++;
        	 }
        	 
        	 j = 0;
        	 for(Double point :gyrGArray)
        	 {
        		 gyrGSeries.add(j, point);
        		 j++;
        	 }
    		 
        	 Double maxValue = Math.max(Math.max(gyro.getA(), gyro.getB()), gyro.getG());
        	 Double minValue = Math.min(Math.min(gyro.getA(), gyro.getB()), gyro.getG());
        	             
             if(minValue < gyrMin)
             {
            	 gyrMin = minValue;
            	 gyrRenderer.setYAxisMin(gyrMin);
             }
             if(maxValue > gyrMax)
             {
            	 gyrMax = maxValue;
            	 gyrRenderer.setYAxisMax(gyrMax);
             }
             if(gyrMin == gyrMax)
             {
            	 gyrMin = minValue-1;
            	 gyrMax = maxValue+1;
            	 
            	 gyrRenderer.setYAxisMin(gyrMin);
            	 gyrRenderer.setYAxisMax(gyrMax);
             }	 

             gyrChartView.repaint();
        	       	 
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
                	BTService.startAllCoreSensors();                	
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
