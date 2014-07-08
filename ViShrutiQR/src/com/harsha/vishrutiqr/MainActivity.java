package com.harsha.vishrutiqr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
public class MainActivity extends Activity {
	ArrayList<String> results;
	String filename1;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		results = new ArrayList<String>();
		try {
		Button b1 = (Button)findViewById(R.id.b1);
        b1.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            }
        });
		} 
		catch (ActivityNotFoundException anfe) {
            Log.e("onCreate", "Scanner Not Found", anfe);
		}
		 catch(OutOfMemoryError r)
		    {
		    	r.printStackTrace();
		    }
        
	}
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    super.onActivityResult(requestCode, resultCode, intent);
try{
	    if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                // Handle successful scan
                results.add(contents);
                Intent i = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_FORMATS", "PRODUCT_MODE,CODE_39,CODE_93,CODE_128,DATA_MATRIX,ITF");
                startActivityForResult(intent, 0);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(), results +"", Toast.LENGTH_SHORT).show();
                int mapnumber = 0;
                SharedPreferences mPrefs = getSharedPreferences("IDvalue", 0);    
                SharedPreferences.Editor e = mPrefs.edit();
                int defaultvalue = mPrefs.getInt("count_key", mapnumber);
                ++defaultvalue;
                mPrefs.edit().putInt("count_key", defaultvalue).commit();
                mapnumber = mPrefs.getInt("count_key", mapnumber);
                String filename =  "Path" + String.valueOf(mapnumber) + ".csv";
                filename1 = "Path" + String.valueOf(mapnumber);       
                File root = Environment.getExternalStorageDirectory();
                File csvfile = new File(root, filename);
                writetocsv(csvfile);
                SharedPreferences myPrefs = getSharedPreferences("IDvalue1", 0);    
                SharedPreferences.Editor edit = myPrefs.edit();
                myPrefs.edit().putString("path_"+ mapnumber, filename1).commit();
                String test = myPrefs.getString("path_" + mapnumber, "0");
                Toast.makeText(getApplicationContext(), test,
     		    Toast.LENGTH_LONG).show();
                
       
                
                finish();
                
            }
            }
	    }
            catch(OutOfMemoryError r)
            {
            	r.printStackTrace();
            }
	}
    void writetocsv(File csvfile){
    try {
		FileWriter writer = new FileWriter(csvfile);
		String header = "ID, QRCode, TimeStamp";
		writer.append(header);
		writer.append('\n');
		for(int i = 0; i < results.size(); i++)
		{
			writer.append((char) (i+65));
			writer.append(',');
			writer.append(results.get(i));
			writer.append(',');
			SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy;hh:mm:ss");
			//java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
			String date = s.format(new Date());
			writer.append(date);
			writer.append('\n');
		}
		writer.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
    	e.printStackTrace();
	}
    catch(OutOfMemoryError r)
    {
    	r.printStackTrace();
    }
  
    }
    @Override
    protected void onDestroy () 
    {
        super.onDestroy();
    	Intent pathintent = new Intent(this, buildUserPath.class);
        startActivity(pathintent);
    }
   /* void addpathnames(int mapnumber)
    {
    sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    Editor populatespinner = sharedpreferences.edit();
         populatespinner.putString("path_"+ mapnumber, filename1);
         
         Intent pathintent = new Intent(this, buildUserPath.class);
         startActivity(pathintent);*/
    }