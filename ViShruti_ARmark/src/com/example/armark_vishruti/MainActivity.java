package com.example.armark_vishruti;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import com.metaio.sdk.MetaioDebug;
import com.metaio.tools.io.AssetsManager;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
//import android.webkit.WebSettings;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	Button save;
	ArrayList<String> adddetails = new ArrayList<String>();
	EditText txt,txt1,txt2;
	String getname,getage,getsex;
	File root = Environment.getExternalStorageDirectory();
	File detail_csvfile = new File(root, "detail.csv");
	
	AssetsExtracter mTask;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txt = (EditText)findViewById(R.id.name);
		txt1 = (EditText)findViewById(R.id.age);
		txt2 = (EditText)findViewById(R.id.sex);
		save = (Button)findViewById(R.id.savebutton);
		save.setOnClickListener( new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				 getname = txt.getText().toString();
				 getage = txt1.getText().toString();
				 getsex = txt2.getText().toString();
				
				if(getname== null){
					 Toast.makeText(getApplicationContext(), "no name"  ,Toast.LENGTH_SHORT).show();
				}
				if(getage == null){
					 Toast.makeText(getApplicationContext(), "no age" ,Toast.LENGTH_SHORT).show();
				}
				if(getsex == null){
					 Toast.makeText(getApplicationContext(), "no sex entered"  ,Toast.LENGTH_SHORT).show();
				}
				if(getname == null && getage==null && getsex == null){
				
					 Toast.makeText(getApplicationContext(), "All fields should be filled" ,Toast.LENGTH_SHORT).show();
					
				}else{
					 Toast.makeText(getApplicationContext(), "saving details"  ,Toast.LENGTH_SHORT).show();
					 writedetails(detail_csvfile);
					 if (savedInstanceState == null) {
							getSupportFragmentManager().beginTransaction()
									.add(R.id.container, new PlaceholderFragment()).commit();
						}
						
						mTask = new AssetsExtracter();
						mTask.execute(0);
						
						Intent Intent = new Intent(getApplicationContext(),ARActivity.class);
						startActivity(Intent);
						
						Intent Intent1 = new Intent(getApplicationContext(),COUNT.class);
						startActivity(Intent1);
					
				}
				
				
			}
			
		});	
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	

	private class AssetsExtracter extends AsyncTask<Integer, Integer, Boolean>
	{	
		@Override
		protected Boolean doInBackground(Integer... params) 
		{
			try 
			{
				// Extract all assets and overwrite existing files if debug build
				AssetsManager.extractAllAssets(getApplicationContext(), BuildConfig.DEBUG);
			} 
			catch (IOException e) 
			{
				MetaioDebug.printStackTrace(Log.ERROR, e);
				return false;
			}

			return true;
		}
		
		
		
	}
	
    
	    void writedetails(File csvfile){
	        try {
	        	 int s_no = 0;
	               SharedPreferences ms_no = getSharedPreferences("values_no", 0);    
	               SharedPreferences.Editor e = ms_no.edit();
	               int s_no_value = ms_no.getInt("count_s_no", s_no);
	               ++s_no_value;
	               ms_no.edit().putInt("count_s_no", s_no_value).commit();
	        	
	            FileWriter writer = new FileWriter(csvfile,true);
	            String header;
	           // File outputfile = new File(getExternalStorageDirectory(), "Dataaccelo.csv" );
	           
	           // for(int i = 0; i < 11; i++)
	            //{
	            	writer.append(String.valueOf(s_no_value));
	            	writer.append(',');
	                writer.append(String.valueOf(getname));
	                writer.append(',');
	               
	                writer.append(String.valueOf(getage));
	                writer.append(',');
	                writer.append(String.valueOf(getsex));
	               
	                writer.append('\n');
	               
	               
	              
	                
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

}
