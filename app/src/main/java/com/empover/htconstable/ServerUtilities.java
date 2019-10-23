package com.empover.htconstable;

import static com.empover.htconstable.CommonUtilities.SERVER_URL;
import static com.empover.htconstable.CommonUtilities.TAG;
import static com.empover.htconstable.CommonUtilities.displayMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

import android.content.Context;
import android.util.Log;

import com.empover.htconstable.model.LoginJSONModel;
import com.empover.htconstable.utility.Utility;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;


public final class ServerUtilities {
	private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    
    private static LoginJSONModel jsonModel = new LoginJSONModel();

    /**
     * Register this account/device pair within the server.
     *
     */
    public static String register(final Context context, String name, String email, final String regId, String DeviceId) {
        Log.i(TAG, "registering device (regId = " + regId + ")");
        String serverUrl = SERVER_URL;
        jsonModel.setDeviceId(DeviceId);
        jsonModel.setLoginId(name);
        jsonModel.setPassword(email);
        jsonModel.setRegistrationId(regId);
        String response=null;
        
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                
                Gson gson = new Gson();
                String json = gson.toJson(jsonModel);
                response = post(serverUrl, json);
                if(response.equalsIgnoreCase("success"))
                	GCMRegistrar.setRegisteredOnServer(context, true);
                return response;
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return null;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
//        CommonUtilities.displayMessage(context, message);
        
        return null;
    }

    /**
     * Unregister this account/device pair within the server.
     */
    static void unregister(final Context context, final String regId) {
        Log.i(TAG, "unregistering device (regId = " + regId + ")");
        String serverUrl = SERVER_URL + "/unregister";
        
        String json = "registrationId : "+regId;
        try {
            post(serverUrl, json);
            GCMRegistrar.setRegisteredOnServer(context, false);
            String message = context.getString(R.string.server_unregistered);
            CommonUtilities.displayMessage(context, message);
        } catch (IOException e) {
            // At this point the device is unregistered from GCM, but still
            // registered in the server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.
            String message = context.getString(R.string.server_unregister_error,
                    e.getMessage());
            CommonUtilities.displayMessage(context, message);
        }
    }

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    private static String post(String endpoint, String json)
            throws IOException {
    	String responseString=null;
    	HttpURLConnection conn = null;
        HttpClient httpclient=null;
        try {
        	
        	HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 3000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket = 3000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            httpclient = new DefaultHttpClient(httpParameters);
//            http://192.168.3.151:8080/HTP/rest/htpService/complainReceiverLogin
            HttpPost httppost = new HttpPost(endpoint);
            Log.d("json request", json);
            StringEntity entity = new StringEntity(json);
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            
            httppost.setEntity(entity);

            HttpResponse response = httpclient.execute(httppost);
            responseString = Utility.convertStreamToString(response.getEntity().getContent());
            Log.d("Http Response", responseString);
        	
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        
        return responseString;
      }
    
}
