package com.nightshadelabs.node;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import android.widget.TextView;

public class NodeSensor{
		ByteArrayOutputStream baos = null;

		public NodeSensor(){
			baos = new ByteArrayOutputStream();
		}


		public void addData(byte[] buffer, int bytes ){
			baos.write(buffer, 0,  bytes);
		}
		
		private String getLatestDataString(String dataType) {
			String fullBOAS =  baos.toString();
			try{
				int lastLine = fullBOAS.lastIndexOf("\n"); // last complete line

				int lastDataType = fullBOAS.lastIndexOf(dataType, lastLine); // last complete line

				String lastChunk = fullBOAS.substring(lastDataType, lastLine); // could include more data than just ACC, MAG or GYR

				String[] lastData = lastChunk.split("\n"); //lastData[0] will now always contain the last ACC, MAG or GYR

				return lastData[0];
			}catch(StringIndexOutOfBoundsException e){
				return "";
			}
		}

		public String getLatestAccelerometerString() {
			
			return getLatestDataString("ACC:");		
		}
		
		public String getLatestMagnetometerString() {
			
			return getLatestDataString("MAG:");		
		}
		
		public String getLatestGyroscopeString() {
			
			return getLatestDataString("GYR:");
		}
		
		/*LED:'byte'	-to apply byte value to the 8 LEDs

		BATT?		-to get battery status*/
		
		public Weather getLatestWeatherObject() {
			
			boolean foundT0, foundT1, foundP0, foundP1, foundH;
			foundT0 = foundT1 = foundP0 = foundP1 = foundH = false;
			
			String fullBOAS =  baos.toString();
			try{
				String[] weatherParts = fullBOAS.split("\n");
				if(fullBOAS.contains("T0:") &&
						fullBOAS.contains("T1:") &&
						fullBOAS.contains("P0:") &&
						fullBOAS.contains("P1:") &&
						fullBOAS.contains("Humi:")) // has each part
				{					
					Weather weather = new Weather();
					for(int i=weatherParts.length-2; i>0; i--) //loop backwards through until we find our elements (ignore last line as it might be a partial)
					{
						String thisPart =  weatherParts[i];
						
						if(thisPart.startsWith("T0:"))
						{
							weather.temperature0 = thisPart.substring(3, thisPart.length()-2); // replace("T0:", "").replace("C", "")
							if(weather.temperature0.length() != 0)
								foundT0 = true;
						}
						else if(thisPart.startsWith("T1:"))
						{
							weather.temperature1 = thisPart.substring(3, thisPart.length()-2);
							if(weather.temperature1.length() != 0)
								foundT1 = true;
						}
						else if(thisPart.startsWith("P0:"))
						{
							weather.barometric0 = thisPart.substring(3, thisPart.length()-3);
							if(weather.barometric0.length() != 0)
								foundP0 = true;
						}
						else if(thisPart.startsWith("P1:"))
						{
							weather.barometric1 = thisPart.substring(3, thisPart.length()-3);
							if(weather.barometric1.length() != 0)
								foundP1 = true;
						}
						else if(thisPart.startsWith("Humi:"))
						{
							weather.humidity = thisPart.substring(5, thisPart.length()-2);							
							if(weather.humidity.length() != 0)
								foundH = true;
						}
						
						if(foundT0 == true && foundT1 == true && foundP0 == true && foundP1 == true && foundH == true)
							break;

					}			
					
					System.console();
	
					return weather;
				}
				else
					return null;
			}catch(StringIndexOutOfBoundsException e){
				return null;
			}
		}		
		

		public Accelerometer getLatestAccelerometerObject(){

			try{
				String fullAccelerometerString = getLatestAccelerometerString();			

				String accelerometerData =  fullAccelerometerString.substring(4, fullAccelerometerString.length()); // chop off ACC:
	
				String[] accelerometerParts = accelerometerData.split(",");
	
				Accelerometer accel = new Accelerometer();
				accel.x = accelerometerParts[0];
				accel.y = accelerometerParts[1];
				accel.z = accelerometerParts[2];
	
				return accel;
			
			}catch(StringIndexOutOfBoundsException e){
				return null;
			}
		}
		
		public Magnetometer getLatestMagnetometerObject(){

			try{
				String fullMagnetometerString = getLatestMagnetometerString();			

				String magnetometerData =  fullMagnetometerString.substring(4, fullMagnetometerString.length()); // chop off MAG:
	
				String[] magnetometerParts = magnetometerData.split(",");
	
				Magnetometer magnet = new Magnetometer();
				magnet.x = magnetometerParts[0];
				magnet.y = magnetometerParts[1];
				magnet.z = magnetometerParts[2];
	
				return magnet;
			
			}catch(StringIndexOutOfBoundsException e){
				return null;
			}
		}
		
		public Gyroscope getLatestGyroscopeObject(){

			try{
				String fullGyroscopeString = getLatestGyroscopeString();			

				String gyroscopeData =  fullGyroscopeString.substring(4, fullGyroscopeString.length()); // chop off ACC:
	
				String[] gyroscopeParts = gyroscopeData.split(",");
	
				Gyroscope gyro = new Gyroscope();
				gyro.a = gyroscopeParts[0];
				gyro.b = gyroscopeParts[1];
				gyro.g = gyroscopeParts[2];
	
				return gyro;
			
			}catch(StringIndexOutOfBoundsException e){
				return null;
			}
		}


		public class Accelerometer extends NodeSensor{
			String x;
			String y;
			String z;
			
			public Double getX(){
				return Double.valueOf(x);
			}
			
			public Double getY(){
				return Double.valueOf(y);
			}
			
			public Double getZ(){
				return Double.valueOf(z);
			}

		}
		
		public class Magnetometer extends NodeSensor{
			String x;
			String y;
			String z;

			public Double getX(){
				return Double.valueOf(x);
			}
			
			public Double getY(){
				return Double.valueOf(y);
			}
			
			public Double getZ(){
				return Double.valueOf(z);
			}
		}
		
		public class Gyroscope extends NodeSensor{
			String a;
			String b;
			String g;
			
			public Double getA(){
				return Double.valueOf(a);
			}
			
			public Double getB(){
				return Double.valueOf(b);
			}
			
			public Double getG(){
				return Double.valueOf(g);
			}

		}
		
		public class Weather extends NodeSensor{
			String humidity;
			String barometric0;
			String barometric1;
			String temperature0;
			String temperature1;
			
			/** Get Temperature in Fahrenheit from weather sensor (T0).
			 * 
			 * @return Temperature or Null
			 */
			public Double getTemperatureF()
			{								
				Double celcius = Double.valueOf(temperature0);
				
				Double fahrenheit = ((celcius * 9 / 5) + 32);								
				
				return round(fahrenheit, 2, BigDecimal.ROUND_HALF_EVEN); //round to 2 decimals because of division.
			}
			
			public Double getTemperatureC()
			{				
				Double celcius = Double.valueOf(temperature0);

				return celcius;
			}
			
			public Double getBarometricKPA()
			{				
				if(barometric0 != null){
					Double celcius = Double.valueOf(barometric0);
					
					return celcius/1000;
				}
				return null;
			}
			
			public Double getHumidity()
			{				
				try{
					return Double.valueOf(humidity);
				}
				catch(NullPointerException e)
				{
					return (double) 0;
				}
			}

		}
		
		public static double round(double unrounded, int precision, int roundingMode)
		{
		    BigDecimal bd = new BigDecimal(unrounded);
		    BigDecimal rounded = bd.setScale(precision, roundingMode);
		    return rounded.doubleValue();
		}
	}