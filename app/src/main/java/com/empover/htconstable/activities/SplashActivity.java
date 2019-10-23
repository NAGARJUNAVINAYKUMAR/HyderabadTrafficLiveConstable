package com.empover.htconstable.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.empover.htconstable.AlertDialogManager;
import com.empover.htconstable.ConnectionDetector;
import com.empover.htconstable.R;

public class SplashActivity extends Activity {
	
	// GCM reg Id
	private String regId;
	
	// Device ID
	public static String device_id;
	
	// Internet detector
	ConnectionDetector cd;
	
	// Asyntask
	AsyncTask<Void, Void, Void> mRegisterTask;
	
	// alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		int SPLASH_TIME_OUT = 3000;
		new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
		
		
		/*regId = Utility.getRegid(getApplicationContext());
		if(regId==null){
			cd = new ConnectionDetector(getApplicationContext());
			if (cd.isConnectingToInternet()) {
				// Check if GCM configuration is set
				if (SERVER_URL != null || SENDER_ID != null || SERVER_URL.length() > 0
						|| SENDER_ID.length() > 0) {
					// Make sure the device has the proper dependencies.
					GCMRegistrar.checkDevice(this);

					// Make sure the manifest was properly set - comment out this line
					// while developing the app, then uncomment it when it's ready.
					GCMRegistrar.checkManifest(this);
					
					// Get GCM registration id
					final String regId = GCMRegistrar.getRegistrationId(this);
					if(regId.equals("")){
						// Registration is not present, register now with GCM			
						GCMRegistrar.register(this, SENDER_ID);
					}
					
					// Get Device ID
					device_id = Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
					
					
					
					
				}else {
					// GCM sernder id / server url is missing
					alert.showAlertDialog(SplashActivity.this, "Configuration Error!",
							"Please set your Server URL and GCM Sender ID", false);
				}
			}else {
				// Internet Connection is not present
				alert.showAlertDialog(SplashActivity.this,
						"Internet Connection Error",
						"Please connect to working Internet connection", false);
			}
			
		}*/
		
		
	}

}
