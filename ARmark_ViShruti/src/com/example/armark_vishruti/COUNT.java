package com.example.armark_vishruti;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;

public class COUNT extends Activity implements SensorEventListener {
	
	 private final static String TAG = "StepDetector";
	    private float   mLimit = (float)5.33;
	    private float   mLastValues[] = new float[3*2];
	    private float   mScale[] = new float[2];
	    private float   mYOffset;
	    int count=0, f_no=0;
	    float x=0,y=0,z=0;
	    int h=0;
	    float sum=0;
	    float avg=(float)2.6;
	    int step;
	    long no_of_row=20000;
	    float new_avg=(float)2.6;
	    int value;
	    List<String[]> list = new ArrayList<String[]>();

	    
	    private float   mLastDirections[] = new float[3*2];
	    private float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
	    private float   mLastDiff[] = new float[3*2];
	    
	   
	   
	    private int     mLastMatch = -1;
	    
	   // private ArrayList<StepListener> mStepListeners = new ArrayList<StepListener>();
		

		TextView textView;
	    StringBuilder builder = new StringBuilder();

	    

	   float [] history = new float[3];
	    float [] gravity = new float[3];
	    long lastUpdate = 0;
	    float threshold = 0;
	    float xChange1 , yChange1 , zChange1;
	    String [] dir = {"NONE","NONE","NONE"};
	    double ZXchange, ZXchange1;
	    double XYchange, XYchange1;
	    double YZchange, YZchange1;
	    double ZYchange, ZYchange1;
	    double XZchange, XZchange1;
	    double YXchange, YXchange1;
	    
	    
	   /* double PI=180.0;
	    double straight_angle=30, right_left_angle=30 , diagonal_angle=15;
	    double straight_max_right = (PI/2)-straight_angle;
	    double straight_max_left = (PI/2)+straight_angle;
	    double right_min = right_left_angle;
	    double right_max = (2*PI)-right_left_angle;
	    double left_max = PI+right_left_angle;
	    double left_min = PI-right_left_angle;
	    double diagonal_right_min=(PI/4)+diagonal_angle;
	    double diagonal_right_max=(PI/4)-diagonal_angle;
	    double diagonal_left_min=(PI/2)+straight_angle;
	    double diagonal_left_max= PI-right_left_angle;
	    */
	    public static final int TYPE_STEP_DETECTOR = 0;
	    
	    
	    ArrayList<String> a1 ;
	    
	    File root = Environment.getExternalStorageDirectory();
	    File csvfile;
	    File diff_csvfile = new File(root, "diff.csv");
	    String outputfile = "Dataaccelo.csv";
	    
	    
	 
	    
	    private SensorManager mSensorManager = null;
		
	    // angular speeds from gyro
	    private float[] gyro = new float[3];
	 
	    // rotation matrix from gyro data
	    private float[] gyroMatrix = new float[9];
	 
	    // orientation angles from gyro matrix
	    private float[] gyroOrientation = new float[3];
	 
	    // magnetic field vector
	    private float[] magnet = new float[3];
	 
	    // accelerometer vector
	    private float[] accel = new float[3];
	 
	    // orientation angles from accel and magnet
	    private float[] accMagOrientation = new float[3];
	 
	    // final orientation angles from sensor fusion
	    private float[] fusedOrientation = new float[3];
	 
	    // accelerometer and magnetometer based rotation matrix
	    private float[] rotationMatrix = new float[9];
	    
	    public static final float EPSILON = 0.000000001f;
	    private static final float NS2S = 1.0f / 1000000000.0f;
		private float timestamp;
		private boolean initState = true;
	    
		public static final int TIME_CONSTANT = 30;
		public static final float FILTER_COEFFICIENT = 0.98f;
		private Timer fuseTimer = new Timer();
		
		// The following members are only for displaying the sensor output.
		public Handler mHandler;
		private RadioGroup mRadioGroup;
		private TextView mAzimuthView;
		private TextView mPitchView;
		private TextView mRollView;
		private int radioSelection;
		DecimalFormat d = new DecimalFormat("#.##");

	   
	    
	    
	    public void StepDetector() {
	       int h = 480; // TODO: remove this constant
	        mYOffset = h * 0.5f;
	        mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
	        mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
	    }
	   
	    
	    
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        textView = new TextView(this);
	        setContentView(textView);
	        
	        int detailnumber = 0;
            SharedPreferences mdetail = getSharedPreferences("IDvalue", 0);    
            SharedPreferences.Editor e = mdetail.edit();
            value = mdetail.getInt("count_detail", detailnumber);
            ++value;
            mdetail.edit().putInt("count_detail", value).commit();
            String filename = "Participant" + String.valueOf(value) + ".csv";
            csvfile = new File(root, filename);
            
            //writediff(diff_csvfile);
	        readcsv(diff_csvfile);

	      /*  SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	        Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
	        manager.registerListener(this, accelerometer,SensorManager.SENSOR_DELAY_UI); */
	       
	        
	        
	        gyroOrientation[0] = 0.0f;
	        gyroOrientation[1] = 0.0f;
	        gyroOrientation[2] = 0.0f;
	 
	        // initialise gyroMatrix with identity matrix
	        gyroMatrix[0] = 1.0f; gyroMatrix[1] = 0.0f; gyroMatrix[2] = 0.0f;
	        gyroMatrix[3] = 0.0f; gyroMatrix[4] = 1.0f; gyroMatrix[5] = 0.0f;
	        gyroMatrix[6] = 0.0f; gyroMatrix[7] = 0.0f; gyroMatrix[8] = 1.0f;
	 
	        // get sensorManager and initialise sensor listeners
	        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
	        initListeners();
	        
	        // wait for one second until gyroscope and magnetometer/accelerometer
	        // data is initialised then scedule the complementary filter task
	        fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(),
	                                      1000, TIME_CONSTANT);
	        
	       
	       
	        
	        
	        

	    }
	    
	    public void initListeners(){
	        mSensorManager.registerListener(this,
	            mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
	            SensorManager.SENSOR_DELAY_FASTEST);
	     
	        mSensorManager.registerListener(this,
	            mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
	            SensorManager.SENSOR_DELAY_FASTEST);
	     
	        mSensorManager.registerListener(this,
	            mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
	            SensorManager.SENSOR_DELAY_FASTEST);
	    }


	    @Override
	    public void onSensorChanged(SensorEvent event) 
	    
	    {
	    	
	    	switch(event.sensor.getType()) {
		    case Sensor.TYPE_ACCELEROMETER:
		        // copy new accelerometer data into accel array and calculate orientation
		        System.arraycopy(event.values, 0, accel, 0, 3);
		        calculateAccMagOrientation();
		        break;
		 
		    case Sensor.TYPE_GYROSCOPE:
		        // process gyro data
		        gyroFunction(event);
		        break;
		 
		    case Sensor.TYPE_MAGNETIC_FIELD:
		        // copy new magnetometer data into magnet array
		        System.arraycopy(event.values, 0, magnet, 0, 3);
		        break;
		    }
	    	 Sensor sensor = event.sensor; 
	    	 
	    	 
	    	  step=0;
	         synchronized (this) {
	             if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
	             }
	             else {
	            	 
	            	
	            	 
	                 int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;
	            	 
	                 if (j == 1) {
	                	 StepDetector();
	                     float vSum = 0;
	                     for (int i=0 ; i<3 ; i++) {
	                         final float v = mYOffset + event.values[i] * mScale[j];
	                         vSum += v;
	                     }
	                     int k = 0;
	                     float v = vSum / 3;
	                     double x1 = event.values[0];
	                     double y1 = event.values[1];
	                     double z1 = event.values[2];
	                     
	                     double accelationSquareRoot = Math.sqrt((x1*x1+y1*y1+z1*z1));
	                     
	                     
	                     float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
	                     if (direction == - mLastDirections[k]) {
	                    	 
	                         
	                    	 // Direction changed
	                         int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
	                         mLastExtremes[extType][k] = mLastValues[k];
	                         float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);
	                         
	                         no_of_row++;
	                         
	                         new_avg= (((no_of_row-1)*new_avg)+diff)/no_of_row;
	                         
	                         mLimit = new_avg;
	                        
	                         

	                         if (diff > mLimit) {
	                        	 
	                        	
	                             
	                             boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);
	                             boolean isPreviousLargeEnough = mLastDiff[k] > (diff/3);
	                             boolean isNotContra = (mLastMatch != 1 - extType);
	                             
	                             
	                             if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
	                                 Log.i(TAG, "step");
	                                 
	                             
	                                // Toast.makeText(getApplicationContext(), "STEP DETECTED",Toast.LENGTH_SHORT).show();
	                                 count++;
	                                 step =1;
	                                 
	                                 //To remove gravity effect
	                                 final float alpha = (float) 0.95 ;
	                             	gravity[0] = (float) (alpha * gravity[0] + (1 - alpha) * event.values[0]);
	                             	gravity[1] = (float) (alpha * gravity[1] + (1 - alpha) * event.values[1]);
	                             	gravity[2] = (float) (alpha * gravity[2] + (1 - alpha) * event.values[2]);
	                             	
	                             	//The sensor values
	                             	x=event.values[0];
	                             	y=event.values[1];
	                             	z=event.values[2];
	                             	
	                             	 //To get the changing co-ordinate
	                             	  xChange1 = history[0] - (event.values[0] - gravity[0]);
	                                  yChange1 = history[1] - (event.values[1] - gravity[1]);
	                                  zChange1 = history[2] - (event.values[2] - gravity[2]);
	                                  
	                                  
	                                  
	                                  
	                               
	                                  //to get the changing angle

	                                  ZXchange = Math.atan2(zChange1, xChange1);
	                                  ZXchange1 = ZXchange * (180.0 / Math.PI); // convert to degrees
	                                  ZXchange1 = (ZXchange1 > 0.0 ? ZXchange1 : (360.0 + ZXchange1)); // correct discontinuity
	                                 
	                                  ZYchange = Math.atan2(zChange1, yChange1);
	                                  ZYchange1 = ZYchange * (180.0 / Math.PI); // convert to degrees
	                                  ZYchange1 = (ZYchange1 > 0.0 ? ZYchange1 : (360.0 + ZYchange1));
	                                  

	                                  XYchange = Math.atan2(xChange1, yChange1);
	                                  XYchange1 = XYchange * (180.0 / Math.PI); // convert to degrees
	                                  XYchange1 = (XYchange1 > 0.0 ? XYchange1 : (360.0 + XYchange1));
	                                  

	                                  XZchange = Math.atan2(xChange1, zChange1);
	                                  XZchange1 = XZchange * (180.0 / Math.PI); // convert to degrees
	                                  XZchange1 = (XZchange1 > 0.0 ? XZchange1 : (360.0 + XZchange1));
	                                  

	                                  YZchange = Math.atan2(yChange1, zChange1);
	                                  YZchange1 = YZchange * (180.0 / Math.PI); // convert to degrees
	                                  YZchange1 = (YZchange1 > 0.0 ? YZchange1 : (360.0 + YZchange1));
	                                   

	                                  YXchange = Math.atan2(yChange1, xChange1);
	                                  YXchange1 = YXchange * (180.0 / Math.PI); // convert to degrees
	                                  YXchange1 = (YXchange1 > 0.0 ? YXchange1 : (360.0 + YXchange1));
	                                  
	                                  //To assign the old values
	                                   history[0] = (event.values[0]-gravity[0]);
	                                   history[1] = (event.values[1]-gravity[1]);
	                                   history[2] = (event.values[2]-gravity[2]);
	                                   
	                                   
	                                  
	                                  
	                                       	 
	                                   //To check the direction
	                                   
	                               /*    if( accelationSquareRoot > 1)
	                                   {  	   
	                                   
	                                   if (ZXchange1 >  straight_max_right && ZXchange1 <  straight_max_left)
	                                   {
	                                	   dir[0]="STRAIGHT";
	                                   }
	                                   else if(ZXchange1 > diagonal_right_max && ZXchange1 < diagonal_right_min )
	                                   {
	                                	   dir[0]="DIAGONAL RIGHT";
	                                   }
	                                   else if(ZXchange1 > right_max && ZXchange1 < right_min )
	                                   {
	                                	   dir[0]="RIGHT";
	                                   }
	                                   else if(ZXchange1 > diagonal_left_min && ZXchange1 < diagonal_left_max )
	                                   {
	                                	   dir[0]="DIAGONAL LEFT";
	                                   }
	                                   else if(ZXchange1 > left_min && ZXchange1 < left_max )
	                                   {
	                                	   dir[0]="LEFT";
	                                   }
	                                   
	                                   }
	                                 */  
	                                   
	                                   //To write values of ZXchange in file
	                                   
	                                   writetocsv(csvfile);
	                                   
	                                 
	                                                             
	                                 mLastMatch = extType;
	                             }
	                             else {
	                                 mLastMatch = -1;
	                             }
	                         }
	                         mLastDiff[k] = diff;
	                        
	                         //To write values of ZXchange in file
	                         
	                        // writetocsv(csvfile);
	                         writediff(diff_csvfile);
	                         //readcsv(diff_csvfile);
	                         
	                        
	                         
	                     }
	                     mLastDirections[k] = direction;
	                     mLastValues[k] = v;
	                 }
	             
	         
	             
	         builder.setLength(0);
	        
	         builder.append("no_of_steps: ");
	         builder.append(count);
	         
	         
	         builder.append(" ZX: ");
	         builder.append(ZXchange1);
	         
	         
	         builder.append(" DIRECTION: ");
	         builder.append(dir[0]);
       
	         builder.append(" ----DIRECTION_NORTH :  ");
	         builder.append(fusedOrientation[0] * 180/Math.PI) ;
	        
	         
	         textView.setText(builder.toString());
	             }
	             
	         }
	         
	         
	         
	       
	       
	      }
	    
	         
	    void writetocsv(File csvfile){
	        try {
	        	   
	               
	               FileWriter writer = new FileWriter(csvfile,true);
	               String header;
	           // File outputfile = new File(getExternalStorageDirectory(), "Dataaccelo.csv" );
	            
	               
	               
	               if (h==0)
	            
	               {
	            	
	            	   header = "S.NO.,  DIRECTION , X , Y , Z ,ZX-ANGLE, ZY-ANGLE , XY-ANGLE , XZ-ANGLE , YZ-ANGLE , YX-ANGLE";
	            	
	            	   writer.append(header);
	            	   writer.append('\n');
	            	   h++;
	               }
	           
	               	writer.append(String.valueOf(count));
	               	writer.append(',');
	                writer.append(dir[0]);
	                writer.append(',');
	                writer.append(String.valueOf(x));
	                writer.append(',');
	                writer.append(String.valueOf(y));
	                writer.append(',');
	                writer.append(String.valueOf(z));
	                writer.append(',');
	                writer.append(String.valueOf(ZXchange1));
	                writer.append(',');
	                writer.append(String.valueOf( ZYchange1)); 
	                writer.append(',');
	                writer.append(String.valueOf( XYchange1));
	                writer.append(',');
	                writer.append(String.valueOf( XZchange1));
	                writer.append(',');
	                writer.append(String.valueOf( YZchange1));
	                writer.append(',');
	                writer.append(String.valueOf( YXchange));
	                writer.append('\n');
	           
	            writer.close();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        catch (NullPointerException r){
	            StringWriter errors = new StringWriter();
	            r.printStackTrace(new PrintWriter(errors));
	            Log.w("My_Tag", errors.toString());
	        }
	        
	    }
	  
	   
	    void writediff(File diff_csvfile){
	        try {
	        	
	            FileWriter writer = new FileWriter(diff_csvfile);
	            
	         
	                writer.write(String.valueOf(new_avg));
	                writer.write(',');
	                writer.write(String.valueOf(no_of_row));
	               
	               // writer.append('\n');
	                

	            writer.close();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        catch (NullPointerException r){
	            StringWriter errors = new StringWriter();
	            r.printStackTrace(new PrintWriter(errors));
	            Log.w("My_Tag", errors.toString());
	        }
	        
	    }
	  
	    
	    void readcsv(File diff_csvfile){
	    	
	    	int row=0;
	    
	        
	        try {
	        	File dir = Environment.getExternalStorageDirectory();
	        	File yourFile = new File(dir, "diff.csv");
	        	CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(yourFile)));
	            String[] line = null;

	           

	           

	            while((line = reader.readNext())!=null){
	            	
	            	list.add(line);
	            	row++;
	               
	              
	            }
	            
	            reader.close();
	        }
	            catch (IOException e) {
	                e.printStackTrace();
	            }
	        
	        
	        for (int i = 0; i < row; i++){
	        	String line = list.get(i)[0];
	        	String diff_no = list.get(i)[1];
	        	
	        	new_avg= Float.valueOf(line);
	        	no_of_row = Long.valueOf(diff_no);
	        	//sum=sum+total;
	       
	       }
	        //avg=(sum/no_of_row);
	        Toast.makeText(getApplicationContext(), "avg=" +new_avg ,Toast.LENGTH_SHORT).show();
	       
	    }
	    	 
	    
	    @Override
	    public void onAccuracyChanged(Sensor sensor, int accuracy) {
	        // nothing to do here
	    }

	



//calculates orientation angles from accelerometer and magnetometer output
	public void calculateAccMagOrientation() {
	    if(SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet)) {
	        SensorManager.getOrientation(rotationMatrix, accMagOrientation);
	    }
	}
	
	// This function is borrowed from the Android reference
	// at http://developer.android.com/reference/android/hardware/SensorEvent.html#values
	// It calculates a rotation vector from the gyroscope angular speed values.
 private void getRotationVectorFromGyro(float[] gyroValues,
         float[] deltaRotationVector,
         float timeFactor)
	{
		float[] normValues = new float[3];
		
		// Calculate the angular speed of the sample
		float omegaMagnitude =
		(float)Math.sqrt(gyroValues[0] * gyroValues[0] +
		gyroValues[1] * gyroValues[1] +
		gyroValues[2] * gyroValues[2]);
		
		// Normalize the rotation vector if it's big enough to get the axis
		if(omegaMagnitude > EPSILON) {
		normValues[0] = gyroValues[0] / omegaMagnitude;
		normValues[1] = gyroValues[1] / omegaMagnitude;
		normValues[2] = gyroValues[2] / omegaMagnitude;
		}
		
		// Integrate around this axis with the angular speed by the timestep
		// in order to get a delta rotation from this sample over the timestep
		// We will convert this axis-angle representation of the delta rotation
		// into a quaternion before turning it into the rotation matrix.
		float thetaOverTwo = omegaMagnitude * timeFactor;
		float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
		float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
		deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
		deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
		deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
		deltaRotationVector[3] = cosThetaOverTwo;
	}
	
 // This function performs the integration of the gyroscope data.
 // It writes the gyroscope based orientation into gyroOrientation.
 public void gyroFunction(SensorEvent event) {
     // don't start until first accelerometer/magnetometer orientation has been acquired
     if (accMagOrientation == null)
         return;
  
     // initialisation of the gyroscope based rotation matrix
     if(initState) {
         float[] initMatrix = new float[9];
         initMatrix = getRotationMatrixFromOrientation(accMagOrientation);
         float[] test = new float[3];
         SensorManager.getOrientation(initMatrix, test);
         gyroMatrix = matrixMultiplication(gyroMatrix, initMatrix);
         initState = false;
     }
  
     // copy the new gyro values into the gyro array
     // convert the raw gyro data into a rotation vector
     float[] deltaVector = new float[4];
     if(timestamp != 0) {
         final float dT = (event.timestamp - timestamp) * NS2S;
     System.arraycopy(event.values, 0, gyro, 0, 3);
     getRotationVectorFromGyro(gyro, deltaVector, dT / 2.0f);
     }
  
     // measurement done, save current time for next interval
     timestamp = event.timestamp;
  
     // convert rotation vector into rotation matrix
     float[] deltaMatrix = new float[9];
     SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);
  
     // apply the new rotation interval on the gyroscope based rotation matrix
     gyroMatrix = matrixMultiplication(gyroMatrix, deltaMatrix);
  
     // get the gyroscope based orientation from the rotation matrix
     SensorManager.getOrientation(gyroMatrix, gyroOrientation);
 }
 
 private float[] getRotationMatrixFromOrientation(float[] o) {
     float[] xM = new float[9];
     float[] yM = new float[9];
     float[] zM = new float[9];
  
     float sinX = (float)Math.sin(o[1]);
     float cosX = (float)Math.cos(o[1]);
     float sinY = (float)Math.sin(o[2]);
     float cosY = (float)Math.cos(o[2]);
     float sinZ = (float)Math.sin(o[0]);
     float cosZ = (float)Math.cos(o[0]);
  
     // rotation about x-axis (pitch)
     xM[0] = 1.0f; xM[1] = 0.0f; xM[2] = 0.0f;
     xM[3] = 0.0f; xM[4] = cosX; xM[5] = sinX;
     xM[6] = 0.0f; xM[7] = -sinX; xM[8] = cosX;
  
     // rotation about y-axis (roll)
     yM[0] = cosY; yM[1] = 0.0f; yM[2] = sinY;
     yM[3] = 0.0f; yM[4] = 1.0f; yM[5] = 0.0f;
     yM[6] = -sinY; yM[7] = 0.0f; yM[8] = cosY;
  
     // rotation about z-axis (azimuth)
     zM[0] = cosZ; zM[1] = sinZ; zM[2] = 0.0f;
     zM[3] = -sinZ; zM[4] = cosZ; zM[5] = 0.0f;
     zM[6] = 0.0f; zM[7] = 0.0f; zM[8] = 1.0f;
  
     // rotation order is y, x, z (roll, pitch, azimuth)
     float[] resultMatrix = matrixMultiplication(xM, yM);
     resultMatrix = matrixMultiplication(zM, resultMatrix);
     return resultMatrix;
 }
 
 private float[] matrixMultiplication(float[] A, float[] B) {
     float[] result = new float[9];
  
     result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
     result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
     result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];
  
     result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
     result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
     result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];
  
     result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
     result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
     result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];
  
     return result;
 }
 
 class calculateFusedOrientationTask extends TimerTask {
     public void run() {
         float oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;
         
         /*
          * Fix for 179° <--> -179° transition problem:
          * Check whether one of the two orientation angles (gyro or accMag) is negative while the other one is positive.
          * If so, add 360° (2 * math.PI) to the negative value, perform the sensor fusion, and remove the 360° from the result
          * if it is greater than 180°. This stabilizes the output in positive-to-negative-transition cases.
          */
         
         // azimuth
         if (gyroOrientation[0] < -0.5 * Math.PI && accMagOrientation[0] > 0.0) {
         	fusedOrientation[0] = (float) (FILTER_COEFFICIENT * (gyroOrientation[0] + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation[0]);
     		fusedOrientation[0] -= (fusedOrientation[0] > Math.PI) ? 2.0 * Math.PI : 0;
         }
         else if (accMagOrientation[0] < -0.5 * Math.PI && gyroOrientation[0] > 0.0) {
         	fusedOrientation[0] = (float) (FILTER_COEFFICIENT * gyroOrientation[0] + oneMinusCoeff * (accMagOrientation[0] + 2.0 * Math.PI));
         	fusedOrientation[0] -= (fusedOrientation[0] > Math.PI)? 2.0 * Math.PI : 0;
         }
         else {
         	fusedOrientation[0] = FILTER_COEFFICIENT * gyroOrientation[0] + oneMinusCoeff * accMagOrientation[0];
         }
         
         // pitch
         if (gyroOrientation[1] < -0.5 * Math.PI && accMagOrientation[1] > 0.0) {
         	fusedOrientation[1] = (float) (FILTER_COEFFICIENT * (gyroOrientation[1] + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation[1]);
     		fusedOrientation[1] -= (fusedOrientation[1] > Math.PI) ? 2.0 * Math.PI : 0;
         }
         else if (accMagOrientation[1] < -0.5 * Math.PI && gyroOrientation[1] > 0.0) {
         	fusedOrientation[1] = (float) (FILTER_COEFFICIENT * gyroOrientation[1] + oneMinusCoeff * (accMagOrientation[1] + 2.0 * Math.PI));
         	fusedOrientation[1] -= (fusedOrientation[1] > Math.PI)? 2.0 * Math.PI : 0;
         }
         else {
         	fusedOrientation[1] = FILTER_COEFFICIENT * gyroOrientation[1] + oneMinusCoeff * accMagOrientation[1];
         }
         
         // roll
         if (gyroOrientation[2] < -0.5 * Math.PI && accMagOrientation[2] > 0.0) {
         	fusedOrientation[2] = (float) (FILTER_COEFFICIENT * (gyroOrientation[2] + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientation[2]);
     		fusedOrientation[2] -= (fusedOrientation[2] > Math.PI) ? 2.0 * Math.PI : 0;
         }
         else if (accMagOrientation[2] < -0.5 * Math.PI && gyroOrientation[2] > 0.0) {
         	fusedOrientation[2] = (float) (FILTER_COEFFICIENT * gyroOrientation[2] + oneMinusCoeff * (accMagOrientation[2] + 2.0 * Math.PI));
         	fusedOrientation[2] -= (fusedOrientation[2] > Math.PI)? 2.0 * Math.PI : 0;
         }
         else {
         	fusedOrientation[2] = FILTER_COEFFICIENT * gyroOrientation[2] + oneMinusCoeff * accMagOrientation[2];
         }
  
         // overwrite gyro matrix and orientation with fused orientation
         // to comensate gyro drift
         gyroMatrix = getRotationMatrixFromOrientation(fusedOrientation);
         System.arraycopy(fusedOrientation, 0, gyroOrientation, 0, 3);
         
         
         // update sensor output in GUI
        // mHandler.post(updateOreintationDisplayTask);
     }
 
 }
 }
 










	    	
	    /*	long timestamp = System.currentTimeMillis();
	    
	    	final float alpha = (float) 0.8 ;
	    	gravity[0] = (float) (alpha * gravity[0] + (1 - alpha) * event.values[0]);
	    	gravity[1] = (float) (alpha * gravity[1] + (1 - alpha) * event.values[1]);
	    	gravity[2] = (float) (alpha * gravity[2] + (1 - alpha) * event.values[2]);

	    	

	    	/*try {
				Thread.sleep(8);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	
	    	long curTime = System.currentTimeMillis();
	    	 
	        if ((curTime - lastUpdate) > 1000) {
	            
	            lastUpdate = curTime;
	   	
	        float xChange1 = history[0] - (event.values[0] - gravity[0]);
	        float yChange1 = history[1] - (event.values[1] - gravity[1]);
	              zChange1 = history[2] - (event.values[2] - gravity[2]);

	         LRchange = Math.atan2(zChange1, xChange1);
	       // double FBchange = Math.atan2(zChange1, xChange1);
	         LRchange1 = LRchange * (180.0 / Math.PI); // convert to degrees
	        //LRchange1 = (LRchange1 > 0.0 ? LRchange1 : (360.0 + LRchange1)); // correct discontinuity
	        
	        
	        
	       
	          history[0] = (event.values[0]-gravity[0]);
	          history[1] = (event.values[1]-gravity[1]);
	          history[2] = (event.values[2]-gravity[2]);
	        
	        }
	        
	        
	        
	        double a = Math.atan(1);
	        double a1 = a * (180.0 / Math.PI);
	        double b = Math.atan(-1);
	        double b1 = b * (180.0 / Math.PI);
	        
	        if (  LRchange1 > a1 ) {
	            direction[0] = "RIGHT";
	          }
	          else if (b1 < LRchange1 && LRchange1 < a1 ){
	             direction[0] = "STRAIGHT";
	            }
	          else if (LRchange1 < b1){
	            direction[0] = "LEFT";
	          }
	          
	         // if (yChange > 1){
	             // direction[1] = "DOWN";
	            //}
	           // else if (yChange < -1){
	              //direction[1] = "UP";
	            //}
	          
	          if (zChange1 > 2.5){
	              direction[2] = "FRONT";
	            }
	            else if (zChange1 < -2){
	              direction[2] = "BACK";
	            }

	          
	        
	        builder.setLength(0);
	        builder.append("x: ");
	        builder.append(direction[0]);
	     // builder.append(" y: ");
	    //  builder.append(direction[1]);
	        builder.append(" z: ");
	        builder.append(direction[2]);

	        textView.setText(builder.toString());

	        
	       
	        
	       // history[0] = (event.values[0]-gravity[0]);
	        //history[1] = (event.values[1]-gravity[1]);
	        ///history[2] = (event.values[2]-gravity[2]);

	     


	        String filename = "acceledata.csv";
	        
	        
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	        String currentDateandTime = sdf.format(new Date(timestamp));
	        Log.d("Time",currentDateandTime);

	        String res=String.valueOf(currentDateandTime+"#"+event.values[0])+"#"+String.valueOf(event.values[1])+"#"+String.valueOf(event.values[2]);

	        Log.d("test", res);
	        
	        String[] entries = res.split("#"); // array of your values
	        
	        
	        
	        
	        String columnString =   "\"S.NO\",\"TIME_STAMP\",\"X\",\"Y\",\"Z\",\"THRESHOLD\"";
	       // String dataString   =   "\"" + currentDateandTime  +"\",\"" + event.values[0] + "\",\"" +  event.values[1] + "\",\"" +  event.values[2] + "\",\"" + threshold + "\"";
	        //String combinedString = columnString + "\n" + dataString;

	        
	        File file   = null;
	        File root   = Environment.getExternalStorageDirectory();
	        if (root.canWrite()){
	            File dir    =   new File (root.getAbsolutePath() + "/Data");
	             dir.mkdirs();
	             file   =   new File(dir, "Dataaccelo.csv");
	             FileWriter fr = null;
	             BufferedWriter br = null;
	            // for(int i=1; i< entries.length ;i++)
	             //{
	            	
	             
	            	 try {
	                     //to append to file, you need to initialize FileWriter using below constructor
	                     fr = new FileWriter(file,true);
	                     br = new BufferedWriter(fr);
	                     br.write(columnString);
	                     for(int i = 0; i<entries.length;i++){
	                         String dataString   =   "\"" + i +"\",\"" + currentDateandTime  +"\",\"" + event.values[0] + "\",\"" +  event.values[1] + "\",\"" +  event.values[2] + "\",\"" + threshold + "\"";

	                         br.newLine();
	                         //you can use write or append method
	                         br.write(dataString);
	                     }
	                     
	                 } catch (IOException e) {
	                     e.printStackTrace();
	                 }finally{
	                     try {
	                         br.close();
	                         fr.close();
	                     } catch (IOException e) {
	                         e.printStackTrace();
	                     }
	                 }
	             
	        }
	        
	    }
	        
	        
	           /*  CSVWriter writer = null;
	        try 
	        {
	            //Log.d("check","pasla");
	            //Environment.getExternalStorageDirectory().getPath();
	        	  //writer = new CSVWriter(new FileWriter("C:/Temp/newfile3.csv"), '\t');
	        String[] entries = res.split("#"); // array of your values

	        writer.writeNext(entries); 
	        //FileWriter
	        writer.close();
	        } 
	        catch (IOException e)
	        {
	        //error
	        }*/
	                   
	    
	        
	//}

	
	
	


