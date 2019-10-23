package com.empover.htconstable.activities;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

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

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.empover.htconstable.AlertDialogManager;
import com.empover.htconstable.CommonUtilities;
import com.empover.htconstable.ConnectionDetector;
import com.empover.htconstable.R;
import com.empover.htconstable.constants.Constants;
import com.empover.htconstable.dtos.ComplaintsDTO;
import com.empover.htconstable.dtos.ResolveCategoryDTO;
import com.empover.htconstable.dtos.ResolveDTO;
import com.empover.htconstable.model.LoginJSONModel;
import com.empover.htconstable.utility.Utility;
import com.google.gson.Gson;

public class MainActivity extends Activity {
	
	private List<ComplaintsDTO> complaintsList = new ArrayList<ComplaintsDTO>();
	private ComplaintsDTO complaintsDTO;
	
	private ListView complaintsListView;
	private LinearLayout listHeader_layout;
	private TextView noDataTextLable;
	private Button logoutBtn;
	
	private List<ResolveCategoryDTO> resolveCategoryList = new ArrayList<ResolveCategoryDTO>();
	private ResolveCategoryDTO categoryDTO;
	
	private List<String> resolveSpinnerList = new ArrayList<String>();
	
	private AlertDialog dialog;
	
	private ComplaintsAdapter adapter;
	
	
	
	// Asyntask
	AsyncTask<Void, Void, Void> mRegisterTask; 
	
	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();
	
	// Connection detector
	ConnectionDetector cd;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ActionBar actionBar = getActionBar();
		//actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_nav_bg));
//		actionBar.setDisplayHomeAsUpEnabled(true);
		complaintsListView = (ListView) findViewById(R.id.complaints_list);
		listHeader_layout = (LinearLayout) findViewById(R.id.list_header_layout);
		noDataTextLable = (TextView) findViewById(R.id.no_data);
		logoutBtn = (Button) findViewById(R.id.btnLogout);
		
		new MyAsync().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
		new ResolveCategoryAsync().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
		adapter = new ComplaintsAdapter(complaintsList);
		complaintsListView.setAdapter(adapter);
		
		complaintsListView.setOnItemClickListener(new ListItemClick());
		
		logoutBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				alertDialog();
			}
		});
		
	}
	
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
		return true;
	}
	
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			refreshList();
			break;

		case android.R.id.home:
			alertDialog();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void refreshList(){
		new MyAsync().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
	}
	
	private class MyAsync extends AsyncTask<String, String, String>{
		
		private ProgressDialog dialog;
		
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainActivity.this);
			dialog.setTitle("Fetching data");
			dialog.setMessage("Please wait...");
			dialog.show();
			
		}

		@Override
		protected String doInBackground(String... params) {
			String responseString=null;
			HttpURLConnection conn = null;
	        HttpClient httpclient=null;
	        LoginJSONModel jsonModel = new LoginJSONModel();
	        jsonModel.setLoginId(Utility.getUsername(getApplicationContext()));
	        Gson gson = new Gson();
	        String json = gson.toJson(jsonModel);
			
			try {
	        	
	        	HttpParams httpParameters = new BasicHttpParams();
	            int timeoutConnection = 30000;
	            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	            int timeoutSocket = 30000;
	            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	            httpclient = new DefaultHttpClient(httpParameters);
	            HttpPost httppost = new HttpPost(CommonUtilities.NEWCOMPLAINTS);
	            Log.d("json request", json);
	            StringEntity entity = new StringEntity(json);
	            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	            httppost.setEntity(entity);
	            HttpResponse response = httpclient.execute(httppost);
	            responseString = Utility.convertStreamToString(response.getEntity().getContent());
	        	Log.d("complaints response", responseString);
	        }
			catch(Exception e){
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
//			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
			if(result!=null){
				JSONObject responseJsonObject = null;
				try {
					responseJsonObject = new JSONObject(result);
					if(responseJsonObject.getInt("code")==200){
						if(responseJsonObject.getString("message").equals("success")){
							complaintsList = Utility.complaintsJSON(result);
							if(complaintsList!=null){
								if(complaintsList.size()==0){
									listHeader_layout.setVisibility(View.GONE);
									noDataTextLable.setVisibility(View.VISIBLE);
								}else{
									listHeader_layout.setVisibility(View.VISIBLE);
									noDataTextLable.setVisibility(View.GONE);
								}
								ComplaintsAdapter adapter = new ComplaintsAdapter(complaintsList);
								complaintsListView.setAdapter(adapter);
								adapter.notifyDataSetChanged();
							}
						}
					}else if (responseJsonObject.getInt("code")==100) {
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			dialog.dismiss();
		}
	}
	
	private class ComplaintsAdapter extends BaseAdapter{
		private List<ComplaintsDTO> complaintsList;
		private ComplaintsDTO complaintsDTO;
		public ComplaintsAdapter(List<ComplaintsDTO> list) {
			complaintsList = list;
		}
		
		@Override
		public int getCount() {
			return complaintsList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			complaintsDTO = (ComplaintsDTO) complaintsList.get(position);
			View view =convertView;
			ViewHolder holder;
			if(view==null){
				view = getLayoutInflater().inflate(R.layout.complaint_list_item, null);
				holder = new ViewHolder();
				holder.type = (TextView) view.findViewById(R.id.list_item_type);
				holder.vehicle_no = (TextView) view.findViewById(R.id.list_item_vehicle_no);
				holder.mobile_no = (TextView) view.findViewById(R.id.list_item_mobile_no);
				holder.mapBtn = (LinearLayout) view.findViewById(R.id.list_item_map_img);
				holder.map_icon = (ImageView) view.findViewById(R.id.list_item_map_img1);
				view.setTag(holder);
				if(complaintsDTO.getComplainStatus().equals("1")){
					view.setBackgroundResource(R.drawable.list_item_bg_pressed);
					holder.map_icon.setBackgroundResource(R.drawable.map_icon_f);
				}else{
					view.setBackgroundResource(R.drawable.list_item_bg_normal);
					holder.map_icon.setBackgroundResource(R.drawable.map_icon);
				}
			}else {
				holder = (ViewHolder) view.getTag();
			}
			
				holder.type.setText(complaintsDTO.getType());
				holder.vehicle_no.setText(complaintsDTO.getVehicleNo());
				holder.mobile_no.setText(complaintsDTO.getMobileNo());
				holder.map_icon.setOnClickListener(new MapBtnClick(position));
				
			return view;
		}
		
	}
	
	private static class ViewHolder{
		private TextView type;
		private TextView vehicle_no;
		private TextView mobile_no;
		private LinearLayout mapBtn;
		private ImageView map_icon;
	}
	
	private class MapBtnClick implements OnClickListener{
		int position;
		public MapBtnClick(int position) {
			this.position = position;
		}
		
		@Override
		public void onClick(View v) {
			Toast.makeText(getApplicationContext(), "Clicked on position "+position, Toast.LENGTH_LONG).show();
			
			complaintsDTO = complaintsList.get(position);
			
			Intent intent = new Intent(MainActivity.this,MapActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(Constants.KEY_LATITUDE, complaintsDTO.getLat());
			bundle.putString(Constants.KEY_LONGITUDE, complaintsDTO.getLang());
			bundle.putString(Constants.KEY_ADDRESS, complaintsDTO.getAddress());
			intent.putExtras(bundle);
			startActivity(intent);
		}
		
		
	}
	
	private class ListItemClick implements OnItemClickListener{

		private Button accept;
		private Button cancel;
		private String json;
		private ResolveDTO resolveDTO = new ResolveDTO();
		private TextView complaintType;
		private TextView vehicleNo;
		private TextView mobileNo;
		private Spinner resolveType;
		private EditText comments;
		private TextView complaintId;
		private ImageView callBtn;
		
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position,
				long arg3) {
			complaintsDTO = complaintsList.get(position);
			
			if(complaintsDTO.getComplainStatus().equals("0")){
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				
				LayoutInflater inflater = getLayoutInflater();

			    builder.setView(inflater.inflate(R.layout.custom_alert, null));
			    dialog = builder.create();
			    
//				dialog.setContentView(R.layout.custom_alert);
//				dialog.setTitle("Complaint Id - "+complaintsDTO.getId());
				dialog.show();
				accept = (Button) dialog.findViewById(R.id.alert_acceptBtn);
				cancel = (Button) dialog.findViewById(R.id.alert_cancelBtn);
				
				complaintId = (TextView) dialog.findViewById(R.id.accept_complainId);
				complaintType = (TextView) dialog.findViewById(R.id.alert_complType);
				vehicleNo = (TextView) dialog.findViewById(R.id.alert_vehicleNo);
				mobileNo = (TextView) dialog.findViewById(R.id.alert_mobileNo);
				callBtn = (ImageView) dialog.findViewById(R.id.accept_dailBtn);
				
				complaintId.setText(complaintsDTO.getId());
				complaintType.setText(complaintsDTO.getType());
				vehicleNo.setText(complaintsDTO.getVehicleNo());
				mobileNo.setText(complaintsDTO.getMobileNo());
				callBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_CALL);
					    intent.setData(Uri.parse("tel:" +complaintsDTO.getMobileNo()));
					    startActivity(intent);
						
					}
				});
				
				accept.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						resolveDTO.setAutoComplaintsId(complaintsDTO.getId());
						resolveDTO.setLoginId(Utility.getUsername(MainActivity.this));
						resolveDTO.setStatus(1);
						Gson gson = new Gson();
						json = gson.toJson(resolveDTO);
						if(new ConnectionDetector(getApplicationContext()).isConnectingToInternet()){
							new StatusUpdateAsync(CommonUtilities.ACCEPT_COMPLAINT, json).execute();
						}else {
							alert.showAlertDialog(MainActivity.this,
									"Internet Connection Error",
									"Please connect to working Internet connection", false);
						}
						
						dialog.dismiss();
					}
				});
				
				cancel.setOnClickListener(new android.view.View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				
			}else if(complaintsDTO.getComplainStatus().equals("1")){

				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				
			    LayoutInflater inflater = getLayoutInflater();

			    builder.setView(inflater.inflate(R.layout.resolve_alert, null));
			     dialog = builder.create();
			    
//				dialog.setContentView(R.layout.resolve_alert);
//				dialog.setTitle("Complaint Id - "+complaintsDTO.getId());
				dialog.show();
				accept = (Button) dialog.findViewById(R.id.resolve_resolveBtn);
				cancel = (Button) dialog.findViewById(R.id.resolve_cancelBtn);
				
				complaintType = (TextView) dialog.findViewById(R.id.resolve_type);
				vehicleNo = (TextView) dialog.findViewById(R.id.resolve_vehicleNo);
				mobileNo = (TextView) dialog.findViewById(R.id.resolve_mobileNo);
				resolveType = (Spinner) dialog.findViewById(R.id.resolve_resolveType);
				comments = (EditText) dialog.findViewById(R.id.resolve_comments);
				complaintId = (TextView) dialog.findViewById(R.id.resolve_complainId);
				callBtn = (ImageView) dialog.findViewById(R.id.resolve_dailBtn);
				
				complaintId.setText(complaintsDTO.getId());
				complaintType.setText(complaintsDTO.getType());
				vehicleNo.setText(complaintsDTO.getVehicleNo());
				mobileNo.setText(complaintsDTO.getMobileNo());
				
				
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item,resolveSpinnerList);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				resolveType.setAdapter(adapter);
				
				callBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_CALL);
					    intent.setData(Uri.parse("tel:" +complaintsDTO.getMobileNo()));
					    startActivity(intent);
						
					}
				});
				
				accept.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						resolveDTO.setAutoComplaintsId(complaintsDTO.getId());
						resolveDTO.setComments(comments.getText().toString());
						resolveDTO.setLoginId(Utility.getUsername(MainActivity.this));
						resolveDTO.setStatus(2);
						
						resolveDTO.setComplaintResolveCategoryId(resolveCategoryList.get(resolveType.getSelectedItemPosition()).getId());
						Gson gson = new Gson();
						json = gson.toJson(resolveDTO);
						if(new ConnectionDetector(getApplicationContext()).isConnectingToInternet()){
							new StatusUpdateAsync(CommonUtilities.RESOLVE_COMPLAINT, json).execute();
						}else {
							alert.showAlertDialog(MainActivity.this,
									"Internet Connection Error",
									"Please connect to working Internet connection", false);
						}
						dialog.dismiss();
					}
				});
				
				cancel.setOnClickListener(new android.view.View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				
			
				
				
			}
			
		}
		
	}
	
	private class StatusUpdateAsync extends AsyncTask<Void, Void, String>{
		private String json;
		private String url;
		private ProgressDialog dialog;
		public StatusUpdateAsync(String url, String jsonData) {
			json = jsonData;
			this.url = url;
		}
		
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainActivity.this);
			dialog.setTitle("Updating data");
			dialog.setMessage("Please wait...");
			dialog.show();
			
		}
		
		@Override
		protected String doInBackground(Void... params) {
			String responseString=null;
			HttpURLConnection conn = null;
	        HttpClient httpclient=null;
	        
	        try{
			HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 10000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket = 10000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(url);
            Log.d("json request", json);
            StringEntity entity = new StringEntity(json);
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);
            responseString = Utility.convertStreamToString(response.getEntity().getContent());
        	Log.d("status response", responseString);
        	return responseString;
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
			finally {
	            if (conn != null) {
	                conn.disconnect();
	            }
	        }
			
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(result!=null){
				JSONObject responseJsonObject = null;
				try {
					responseJsonObject = new JSONObject(result);
					if(responseJsonObject.getInt("code")==200){
						if(responseJsonObject.getString("message").equals("success")){
							complaintsList = Utility.complaintsJSON(result);
							if(complaintsList!=null){
								if(complaintsList.size()==0){
									listHeader_layout.setVisibility(View.GONE);
									noDataTextLable.setVisibility(View.VISIBLE);
								}else{
									listHeader_layout.setVisibility(View.VISIBLE);
									noDataTextLable.setVisibility(View.GONE);
								}
								ComplaintsAdapter adapter = new ComplaintsAdapter(complaintsList);
								complaintsListView.setAdapter(adapter);
								adapter.notifyDataSetChanged();
							}
						}
					}else if (responseJsonObject.getInt("code")==100) {
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if(dialog!=null){
				dialog.dismiss();
			}
		}	
	}
	
	private class ResolveCategoryAsync extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			String responseString=null;
			HttpURLConnection conn = null;
	        HttpClient httpclient=null;
	        
	        try{
			HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 3000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket = 3000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            httpclient = new DefaultHttpClient(httpParameters);
            HttpPost httppost = new HttpPost(CommonUtilities.RESOLVE_CATEGORY);
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
			if(result!=null && result.length()>0){
				resolveCategoryList = Utility.categoryJSON(result);
				if(resolveCategoryList!=null && resolveCategoryList.size()>0){
					for(int i=0;i<resolveCategoryList.size();i++){
						categoryDTO = resolveCategoryList.get(i);
						resolveSpinnerList.add(categoryDTO.getName());
					}
				}
			}else {
				Toast.makeText(MainActivity.this, "Response Category list Emplty", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void alertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Alert");
		builder.setMessage("Are you sure you want to exit?")
				.setCancelable(false)
				.setPositiveButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				})
				.setNegativeButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								finish();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
}
