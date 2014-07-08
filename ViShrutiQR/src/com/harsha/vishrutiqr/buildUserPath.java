package com.harsha.vishrutiqr;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;

public abstract class buildUserPath extends Activity implements OnItemSelectedListener
{
	String startPoint, destination, finalfilename, f;
	ArrayList<String> pathnames;
	public static ArrayList<String> finalpath;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sourcedestination);
		SharedPreferences myPrefs = getSharedPreferences("IDvalue1",0);
		pathnames = new ArrayList<String>();
        int j = 1;
		while(true)
		{
			f  = myPrefs.getString("path_"+ j, null);
			if(f != null)
				{
				pathnames.add(f);
				j++;
				}
			else{
				break;
			}
		}
		 Spinner spinner = (Spinner) findViewById(R.id.dropdown);
		ArrayAdapter<String> adp = new ArrayAdapter<String>(buildUserPath.this,android.R.layout.simple_spinner_item, pathnames);
	     adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     spinner.setAdapter(adp);
	     spinner.setOnItemSelectedListener(this);

	     
		EditText inputTxt = (EditText) findViewById(R.id.Source);
		startPoint = inputTxt.getText().toString();
		EditText inputTxt1 = (EditText) findViewById(R.id.Destination);
		destination = inputTxt1.getText().toString();
	}

		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int pos, long id)
		{
			String selectedpath = String.valueOf(parent.getItemAtPosition(pos));
			finalfilename = selectedpath + ".csv";
			Toast.makeText(getApplicationContext(), finalfilename,
		    Toast.LENGTH_LONG).show();
		}
		
		


		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "Please select a path",
					   Toast.LENGTH_LONG).show();
		}
		
		

		final ArrayList<String[]> readCsv (Context context)
		{
			  ArrayList<String[]> finalpath = new ArrayList<String[]>();
			  AssetManager assetManager = context.getAssets();

			  try {
				String path = Environment.getExternalStorageDirectory().getAbsolutePath();
				String filepath = path + finalfilename;
				InputStream csvStream = assetManager.open(filepath);
			    InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
			    CSVReader csvReader = new CSVReader(csvStreamReader);
			    String[] line;
			    int startPointInt = Integer.parseInt(startPoint);
			    int destinationInt = Integer.parseInt(destination);
			    //calculate total number of lines excluding header
			    csvReader.readNext(); //throw away the header
			    int count = 0;
			    while ((line = csvReader.readNext()) != null )
			    {
			    	count = count + 1;
			    }
			    //new count excluding the unwanted landmarks
			    int count1 = count - (count - startPointInt);
			    //throw away unwanted landmarks
			    for(int i = 1; i < startPointInt; i++)
			    {
			    	csvReader.readNext();
			    }
			    
			    while (count1 > destinationInt) {
			      finalpath.add(line);
			      count1++;
			    }
			  } catch (IOException e) {
			    e.printStackTrace();
			  }
			  	catch (NullPointerException r)
			  	{
			  		StringWriter errors = new StringWriter();
				    r.printStackTrace(new PrintWriter(errors));
				    Log.w("My_Tag", errors.toString());
			  	}
			  return finalpath;
			}
		
		}
	