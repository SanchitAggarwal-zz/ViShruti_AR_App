package com.example.armark_vishruti;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import com.metaio.sdk.ARViewActivity;
import com.metaio.sdk.MetaioDebug;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.TrackingValues;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.metaio.sdk.jni.Vector3d;
import com.metaio.tools.io.AssetsManager;

public class ARActivity extends ARViewActivity {

	private String mTrakingFile;
	private IGeometry mMan;
	private MediaPlayer buzz;
	
	 int cosId , cosIdz;
	@Override
	protected int getGUILayout() 
	{
		// TODO Auto-generated method stub
		
		return R.layout.ar_view;
		
	}

	@Override
	protected IMetaioSDKCallback getMetaioSDKCallbackHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void loadContents() {
		// TODO Auto-generated method stub
		mTrakingFile = AssetsManager.getAssetPath(getApplicationContext(), "TrackingData_Marker.xml");
		boolean result = metaioSDK.setTrackingConfiguration(mTrakingFile); 
		MetaioDebug.log("detected marker  : " + result); 
		

		
		//String modalPath = AssetsManager.getAssetPath("Assets1/metaioman.md2");
		
		String modalPath = AssetsManager.getAssetPath(getApplicationContext(), "metaioman.md2");
		
		
		if(modalPath!=null)
		{
			
			 mMan = metaioSDK.createGeometry(modalPath);
			 
			 if(mMan!=null)
			 {
				// Set geometry properties
				 
				 	
				 
				 mMan.setScale(new Vector3d(2.0f, 2.0f, 2.0f));
					MetaioDebug.log(" loaded image "+modalPath);
				 
				 
					//mMan.setCoordinateSystemID(1);
					// TrackingValuesVector i = metaioSDK.getTrackingValues();
					 //int id = onTrackingEvent(i);
					/* for(int j=1;j<=id;j++)
					 {		 
					 mMan.setCoordinateSystemID(j);
					 mMan.setScale(new Vector3d(2.0f, 2.0f, 2.0f));
					 }*/
					//mMan.setCoordinateSystemID(1);
					//mMan.setCoordinateSystemID(2);
					
					//mMan.setVisible(true);
					
				}
				else
					MetaioDebug.log(Log.ERROR, "Error loading image: "+modalPath);
			 
		}
		
		
			
		
	}
	

	public void onDrawFrame() 
	{
		super.onDrawFrame();
		
		if (metaioSDK != null)
		{
			// get all detected poses/targets
			TrackingValuesVector poses = metaioSDK.getTrackingValues();
			
			//if we have detected one, attach our metaio man to this coordinate system Id
			if (poses.size() != 0)
			{	mMan.setCoordinateSystemID(poses.get(0).getCoordinateSystemID());
			    buzz = MediaPlayer.create(this, R.raw.buzzer); 
				buzz.start();
			
			}
			
				
		}
	}
	
	
	
	
	/* public int onTrackingEvent(TrackingValuesVector trackingValues) {

        Log.d(ACTIVITY_SERVICE, "ARActivity - onTrackingEvent");
        Log.d(ACTIVITY_SERVICE, "Tama–o del trackingValues = " + trackingValues.size());

        if (trackingValues.size() > 0)
        {

            Log.d(ACTIVITY_SERVICE, "METAIO FOUND SOMETHING");
            for(int i = 0; i < trackingValues.size(); i++)
            {
                //cosID
                //String cosName = trackingValues.get(i).getCosName();
                cosId = trackingValues.get(i).getCoordinateSystemID();
                if( cosId != cosIdz ) {

                    Log.d(ACTIVITY_SERVICE, "Detectado: " + cosId);
                    cosIdz = cosId; // To check it later.
                }

            }
        } else {

            Log.d(ACTIVITY_SERVICE, "Valor de trackingValues == 0");
            Log.d(ACTIVITY_SERVICE, "Tama–o del trackingValues = " + trackingValues.size());

            cosIdz = -1;

            //trackinLostText(); // Tracking lost!

        }
        
        return cosIdz;
        

    }*/
	

	/*public void onGoButtonClick(View v)
	{
		mTrakingFile = AssetsManager.getAssetPath(getApplicationContext(), "TrackingData_Marker.xml");
		MetaioDebug.log("Tracking Config path = "+mTrakingFile);
		
		boolean result = metaioSDK.setTrackingConfiguration(mTrakingFile); 
		MetaioDebug.log("Id Marker tracking data loaded: " + result); 
		mMan.setScale(new Vector3d(2f, 2f, 2f));
		
	}*/
	
	@Override
	protected void onGeometryTouched(IGeometry geometry) {
		// TODO Auto-generated method stub

	}

}
