package com.example.armark_vishruti;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;





















import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSV;
import au.com.bytecode.opencsv.CSVReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;




public class WRONG_TURNS extends Activity implements SensorEventListener 

{
	

    private final static String TAG = "StepDetector";
    private float   mLimit = (float)5.33;
    private float   mLastValues[] = new float[3*2];
    private float   mScale[] = new float[2];
    private float   mYOffset;
    int count=0, f_no=0;
    float x=0,y=0,z=0;
    int h=0;
    float sum=0;
    float avg=(float)5.33;
    int step;
    int no_of_row=0;
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
    
    
    double PI=180.0;
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
    
    
    ArrayList<String> a1 ;
    
    File root = Environment.getExternalStorageDirectory();
    File csvfile = new File(root, "Dataaccelo.csv");
    File diff_csvfile = new File(root, "diff.csv");
    String outputfile = "Dataaccelo.csv";

    
    public void StepDetector() {
       int h = 480; // TODO: remove this constant
        mYOffset = h * 0.5f;
        mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }
    

   // public void setSensitivity(float sensitivity) {
     //   mLimit = sensitivity; // 1.97  2.96  4.44  6.66  10.00  15.00  22.50  33.75  50.62
    //}
    
   
    
    
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView = new TextView(this);
        setContentView(textView);

        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        manager.registerListener(this, accelerometer,SensorManager.SENSOR_DELAY_UI);
        writediff(diff_csvfile);
        readcsv(diff_csvfile);
        

    }

    @Override
    public void onSensorChanged(SensorEvent event) 
    
    {
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
                     
                   
                     //count+=1;
                     float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                     if (direction == - mLastDirections[k]) {
                         // Direction changed
                         int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
                         mLastExtremes[extType][k] = mLastValues[k];
                         float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);
                         
                         
                       mLimit = avg;
                        
                         

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
                                 final float alpha = (float) 0.8 ;
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
                         readcsv(diff_csvfile);
                         
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
           // for(int i = 0; i < 11; i++)
            //{
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
               // writer.append(String.valueOf( mLastDiff[0]));
                //writer.append(',');
                //writer.append(String.valueOf( avg));
                //writer.append(',');
               
              
                
            //}
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
        	
            FileWriter writer = new FileWriter(diff_csvfile,true);
            
         
                writer.append(String.valueOf(mLastDiff[0]));
               
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
  
    
    void readcsv(File diff_csvfile){
    
        
        try {
        	File dir = Environment.getExternalStorageDirectory();
        	File yourFile = new File(dir, "diff.csv");
        	CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(yourFile)));
            String[] line = null;

           

           

            while((line = reader.readNext())!=null){
            	
            	list.add(line);
            	no_of_row++;
               
              
            }
            
            reader.close();
        }
            catch (IOException e) {
                e.printStackTrace();
            }
        
        
        for (int i = 0; i < no_of_row; i++){
        	String line = list.get(i)[0];
        	
        	float total= Float.valueOf(line);
        	sum=sum+total;
       
       }
        avg=(sum/no_of_row);
       // Toast.makeText(getApplicationContext(), "avg" +avg ,Toast.LENGTH_SHORT).show();
       
    }
/*
        try {
        	File dir = Environment.getExternalStorageDirectory();
        	File yourFile = new File(dir, "Dataaccelo.csv");
        	CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(yourFile)));

            for(;;) {
                next = reader.readNext();
                if(next != null) {
                    list.add(next);
                } else {
                    break;
                }
               
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        list.get(3);
       */
    
    	 
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // nothing to do here
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
  