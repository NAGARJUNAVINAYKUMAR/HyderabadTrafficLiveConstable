package com.empover.htconstable.activities;

import static com.empover.htconstable.CommonUtilities.SENDER_ID;
import static com.empover.htconstable.CommonUtilities.SERVER_URL;

import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.empover.htconstable.AlertDialogManager;
import com.empover.htconstable.CommonUtilities;
import com.empover.htconstable.ConnectionDetector;
import com.empover.htconstable.R;
import com.empover.htconstable.ServerUtilities;
import com.empover.htconstable.model.LoginJSONModel;
import com.empover.htconstable.utility.Utility;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class LoginActivity extends Activity {
	// alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	
	// Internet detector
	ConnectionDetector cd;
	
	// UI elements
	private EditText txtUserName;
	private EditText txtPassword;
	private CheckBox mCheckBox;
	
	// Register button
	Button btnLogin;
	
	// GCM reg Id
	public static String regId="";
	
	// Device ID
	public static String device_id="";
	
	// Asyntask
	AsyncTask<Void, Void, Void> mRegisterTask;
	
	//register response
	String response;
	
    public static String username;
	public static String password;
	public static boolean isSave;
	
	private String notificationcode;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		CommonUtilities.mLoginActivity = LoginActivity.this;
		txtUserName = (EditText) findViewById(R.id.login_userName);
		txtPassword = (EditText) findViewById(R.id.login_password);
		mCheckBox = (CheckBox) findViewById(R.id.login_checkBox);
		
		btnLogin = (Button) findViewById(R.id.btnLogin);
		
		if(Utility.getPassword(LoginActivity.this)!=null){
			if(Utility.getUsername(LoginActivity.this)!=null)
				txtUserName.setText(Utility.getUsername(LoginActivity.this));
				txtPassword.setText(Utility.getPassword(LoginActivity.this));
				mCheckBox.setChecked(true);
		}
			
		
		/*
		 * Click event on Login button
		 * */
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				username = txtUserName.getText().toString().trim();
				password = txtPassword.getText().toString().trim();
				isSave = mCheckBox.isChecked();
				if(!isSave){
					Utility.setPassword(LoginActivity.this, null);
				}
				
				if(username.length()>0 && password.length()>0)
				{
					cd = new ConnectionDetector(getApplicationContext());
					if(cd.isConnectingToInternet())
					{
//						new LoginAsync(username, password, regId, device_id, isSave).execute();
						
						if((device_id = Utility.getDeviceid(getApplicationContext()))==null)
						{
							device_id = Secure.getString(getApplicationContext().getContentResolver(),Secure.ANDROID_ID);
							if(device_id!=null && device_id.length()>0)
								Utility.setDeviceId(getApplicationContext(), device_id);
						}
						regId = Utility.getRegid(getApplicationContext());
						//regId = "APKsf93485h95f8hg348gyhv48g3h5gh";
						
						if(regId!=null && regId.length()>0)
						{
							new LoginAsync(username, password, regId, device_id, isSave).execute();
							
						}else{
							Log.i("LoginActivity", "registerWithGCM res = "+registerWithGCM());
						}
						
						
						
						
						/*if (regId==null) {
							
							if(registerWithGCM()){
								response = registerWithServer();
								if(response!=null && response.equalsIgnoreCase("success"))
								{
									if(username.trim().length() > 0 && password.trim().length() > 0){
										Utility.setUsername(getApplicationContext(), username);
										Intent intent = new Intent(LoginActivity.this, MainActivity.class);
										startActivity(intent);
										finish();
								}
							}else{
								alert.showAlertDialog(LoginActivity.this, "Login Error!", "Please enter Valid details", false);
							}
						}
						}else {
							response = registerWithServer();
							if(response!=null && response.equalsIgnoreCase("success"))
							{
								if(username.trim().length() > 0 && password.trim().length() > 0){
									Utility.setUsername(getApplicationContext(), username);
									Intent intent = new Intent(LoginActivity.this, MainActivity.class);
									startActivity(intent);
									finish();
								}
							}else{
								alert.showAlertDialog(LoginActivity.this, "Login Error!", "Please enter Valid details", false);
							}
					}*/
				}else {
					// Internet Connection is not present
					alert.showAlertDialog(LoginActivity.this,
							"Internet Connection Error",
							"Please connect to working Internet connection", false);
				}
			}else {
				alert.showAlertDialog(LoginActivity.this, "Login Error!", "Please enter Valid details", false);
			}
			}
		});
		
	}
	
	private boolean registerWithGCM() {
		// Check if GCM configuration is set
		if (SERVER_URL != null || SENDER_ID != null || SERVER_URL.length() > 0
				|| SENDER_ID.length() > 0) {
			// Make sure the device has the proper dependencies.
			GCMRegistrar.checkDevice(LoginActivity.this);

			// Make sure the manifest was properly set - comment out this line
			// while developing the app, then uncomment it when it's ready.
			GCMRegistrar.checkManifest(LoginActivity.this);
			
			// Get GCM registration id
			final String regId = GCMRegistrar.getRegistrationId(LoginActivity.this);
			
			if(regId.equals("")){
				// Registration is not present, register now with GCM
				GCMRegistrar.register(LoginActivity.this, SENDER_ID);
			}
			return true;
		}else {
			// GCM sernder id / server url is missing
			alert.showAlertDialog(LoginActivity.this, "Configuration Error!",
					"Please set your Server URL and GCM Sender ID", false);
		}
		return false;
	}
	
	private String registerWithServer(){
		if (!GCMRegistrar.isRegisteredOnServer(this)) {
			// Try to register again, but not in the UI thread.
			// It's also necessary to cancel the thread onDestroy(),
			// hence the use of AsyncTask instead of a raw thread.
			final Context context = this;
			mRegisterTask = new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					// Register on our server
					// On server creates a new user
					response = ServerUtilities.register(context, username, password, regId, device_id);
					return null;
				}
				@Override
				protected void onPostExecute(Void result) {
					mRegisterTask = null;
				}
			};
			mRegisterTask.execute();
		}else {
			response = "success";
		}
		
		return response;
	}
	
	public class LoginAsync extends AsyncTask<Void, Void, String>{
		private ProgressDialog dialog;
		
		private String username;
		private String password;
		private String regId;
		private String deviceId;
		private boolean isSave;
		private LoginJSONModel jsonModel = new LoginJSONModel();
		
		public LoginAsync(String username, String password, String regId, String deviceId, boolean isSave) {
			this.username = username;
			this.password = password;
			this.regId = regId;
			this.deviceId = deviceId;
			this.isSave = isSave;
		}

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(LoginActivity.this);
			dialog.setTitle("Login");
			dialog.setMessage("Please wait...");
			dialog.setCancelable(false);
			dialog.show();
			
		}
		
		@Override
		protected String doInBackground(Void... params) {
			
			String responseString=null;
			HttpURLConnection conn = null;
	        HttpClient httpclient=null;
	        
	        try{
			HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 6000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket = 6000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(CommonUtilities.SERVER_URL);
            
            jsonModel.setDeviceId(deviceId);
            jsonModel.setLoginId(username);
            jsonModel.setPassword(password);
            jsonModel.setRegistrationId(regId);
            
            Gson gson = new Gson();
            String json = gson.toJson(jsonModel);
            
            Log.d("json request", json);
            StringEntity entity = new StringEntity(json);
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);
            responseString = Utility.convertStreamToString(response.getEntity().getContent());
        	Log.d("status response", responseString);
			}catch(Exception e){
				e.printStackTrace();
			}
			finally {
	            if (conn != null) {
	                conn.disconnect();
	            }
	        }
			
			return responseString;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(dialog!=null){
				dialog.dismiss();
			}
			JSONObject jsonObject;
			if(result!=null && result.length()>0){
				try{
					jsonObject = new JSONObject(result);
					if(jsonObject.getInt("code")==200){
						if(jsonObject.getString("message").equals("success")){
							Utility.setUsername(getApplicationContext(), username);
							if(isSave){
								Utility.setPassword(LoginActivity.this, password);
							}
							
							Intent intent = new Intent(LoginActivity.this, MainActivity.class);
							startActivity(intent);
							finish();
						}else {
							alert.showAlertDialog(LoginActivity.this, "Login Error!", "Please enter Valid details", false);
						}
					}else if(jsonObject.getInt("code")==300){
						alert.showAlertDialog(LoginActivity.this, "Login Error!", "Please enter Valid details", false);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				Toast.makeText(LoginActivity.this, "Network problem", Toast.LENGTH_SHORT).show();
			}
			
			
		}
	}
	
	
}
