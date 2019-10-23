package com.empover.htconstable.utility;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.empover.htconstable.dtos.ComplainTravelByIdDTO;
import com.empover.htconstable.dtos.ComplaintsDTO;
import com.empover.htconstable.dtos.ResolveCategoryDTO;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Utility {

	static final String GCMREGID="regid"; 
	static final String DEVICEID="deviceid";
	static final String USERNAME ="username";
	static final String PASSWORD ="password";
	
	public Utility() {
	}

	public static String getRegid(Context context){
		SharedPreferences preferences = context.getSharedPreferences(GCMREGID, 0);
		return preferences.getString(GCMREGID, null);
	}
	
	public static void setRegId(Context context, String regId){
		SharedPreferences preferences = context.getSharedPreferences(GCMREGID, 0);
        Editor editor = preferences.edit();
        editor.putString(GCMREGID, regId);
        editor.commit();
	}
	
	public static void setDeviceId(Context context, String deviceId){
		SharedPreferences preferences = context.getSharedPreferences(GCMREGID, 0);
        Editor editor = preferences.edit();
        editor.putString(DEVICEID, deviceId);
        editor.commit();
	}
	
	public static String getDeviceid(Context context){
		SharedPreferences preferences = context.getSharedPreferences(GCMREGID, 0);
		return preferences.getString(DEVICEID, null);
	}
	
	public static String getUsername(Context context){
		SharedPreferences preferences = context.getSharedPreferences(GCMREGID, 0);
		return preferences.getString(USERNAME, null);
	}
	
	public static void setUsername(Context context, String userName){
		SharedPreferences preferences = context.getSharedPreferences(GCMREGID, 0);
        Editor editor = preferences.edit();
        editor.putString(USERNAME, userName);
        editor.commit();
	}
	
	public static String getPassword(Context context){
		SharedPreferences preferences = context.getSharedPreferences(GCMREGID, 0);
		return preferences.getString(PASSWORD, null);
	}
	
	public static void setPassword(Context context, String password){
		SharedPreferences preferences = context.getSharedPreferences(GCMREGID, 0);
        Editor editor = preferences.edit();
        editor.putString(PASSWORD, password);
        editor.commit();
	}
	
	public static String convertStreamToString(InputStream is)
    {
	    StringBuilder sb = new StringBuilder();
	    if(is!=null){
	    	try{
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        String line = null;
	        while ((line = reader.readLine()) != null)
	        {
	            sb.append(line);
	        }
	        reader.close();
	    	}
	    	catch(Exception e){
	    		
	    	}
		}
        return sb.toString();
    }
	
	
	public static List<ComplaintsDTO> complaintsJSON(String jsonString){
		List<ComplaintsDTO> complaintsList = new ArrayList<ComplaintsDTO>();
		ComplaintsDTO complaintsDTO;
		
		JSONObject responseJsonObject;
		JSONArray complaintsJsonArray=null;
		JSONObject complaintJsonObject=null;
		ComplainTravelByIdDTO complainTravelByDTO = new ComplainTravelByIdDTO();
		JSONObject complaintTBJsonObject;
		try {
			responseJsonObject = new JSONObject(jsonString);
					complaintsJsonArray = responseJsonObject.getJSONArray("response");
					for(int i=0;i<complaintsJsonArray.length();i++){
						complaintJsonObject = complaintsJsonArray.getJSONObject(i);
						complaintsDTO = new ComplaintsDTO();
						complaintsDTO.setId(complaintJsonObject.getString("id"));
						complaintsDTO.setCreatedDate(complaintJsonObject.getString("createdDate"));
						complaintsDTO.setUpdatedDate(complaintJsonObject.getString("updatedDate"));
						complaintsDTO.setType(complaintJsonObject.getString("type"));
						complaintsDTO.setVehicleNo(complaintJsonObject.getString("vehicleNo"));
						complaintsDTO.setDriverName(complaintJsonObject.getString("driverName"));
						complaintsDTO.setComments(complaintJsonObject.getString("comments"));
						complaintsDTO.setName(complaintJsonObject.getString("name"));
						complaintsDTO.setEmail(complaintJsonObject.getString("email"));
						complaintsDTO.setMobileNo(complaintJsonObject.getString("mobileNo"));
						complaintsDTO.setStatus(complaintJsonObject.getString("status"));
						complaintsDTO.setCode(complaintJsonObject.getString("code"));
						complaintsDTO.setAttend(complaintJsonObject.getString("attend"));
						complaintsDTO.setSatishfy(complaintJsonObject.getString("satishfy"));
						complaintsDTO.setLat(complaintJsonObject.getString("lat"));
						complaintsDTO.setLang(complaintJsonObject.getString("lang"));
						complaintsDTO.setComplainStatus(complaintJsonObject.getString("complainStatus"));
						complaintsDTO.setDeviceId(complaintJsonObject.getString("deviceId"));
						complaintsDTO.setAddress(complaintJsonObject.getString("address"));
						complaintsDTO.setNearByPs(complaintJsonObject.getString("nearByPs"));
						complaintsDTO.setComplainTravelById(complaintJsonObject.getString("complainTravelById"));
						
						String complainTravelBy = complaintJsonObject.getString("complaintTravelBy");
					/*	if(complainTravelBy!=null && !"null".equals(complainTravelBy))
						{
							complaintTBJsonObject =new JSONObject(complainTravelBy);
							complainTravelByDTO.setId(complaintTBJsonObject.getString("id"));
							complainTravelByDTO.setName(complaintTBJsonObject.getString("name"));
							complainTravelByDTO.setDescription(complaintTBJsonObject.getString("description"));
							complaintsDTO.setComplainTravelBy(complainTravelByDTO);
						}*/
						
						complaintsList.add(complaintsDTO);
					}
					
			return complaintsList;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<ResolveCategoryDTO> categoryJSON(String jsonString){
		List<ResolveCategoryDTO> categoryList = new ArrayList<ResolveCategoryDTO>();
		ResolveCategoryDTO categoryDTO;
		
		JSONObject responseJsonObject;
		JSONArray categoryJsonArray=null;
		JSONObject categoryJsonObject=null;
		
		try {
			responseJsonObject = new JSONObject(jsonString);
			if(responseJsonObject.getInt("code")==200){
				if(responseJsonObject.getString("message").equals("success")){
					categoryJsonArray = responseJsonObject.getJSONArray("response");
					for(int i=0;i<categoryJsonArray.length();i++){
						categoryJsonObject = categoryJsonArray.getJSONObject(i);
						categoryDTO = new ResolveCategoryDTO();
						categoryDTO.setId(categoryJsonObject.getInt("id"));
						categoryDTO.setName(categoryJsonObject.getString("name"));
						categoryDTO.setDescription(categoryJsonObject.getString("description"));
						categoryList.add(categoryDTO);
					}
					return categoryList;
				}
				
			}
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
