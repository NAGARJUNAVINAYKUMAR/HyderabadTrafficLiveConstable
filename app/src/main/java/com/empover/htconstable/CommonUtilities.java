package com.empover.htconstable;

import com.empover.htconstable.activities.LoginActivity;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
	
	private static final String URL = "http://117.239.149.90:8080/HTP/";//live
		
	// give your server registration url here
    public static final String SERVER_URL = URL+"rest/htpService/complainReceiverLogin";
    
    //new complaints service
    public static final String NEWCOMPLAINTS = URL+"rest/htpService/getNewComplainsNotification";
    
    //complaint accept url
    public static final String ACCEPT_COMPLAINT = URL+"rest/htpService/saveComplainReceiverRespond";
    
  //complaint accept url
    public static final String RESOLVE_COMPLAINT = URL+"rest/htpService/saveComplainReceiverRespond";
    
  //resolve type category url
    public static final String RESOLVE_CATEGORY = URL+"rest/htpService/loadComplaintResolveCategory";
    
    public static final String SENDER_ID = "611068094036";

    public static LoginActivity mLoginActivity;

    static final String TAG = "Android GCM";
    
    private static final String DISPLAY_MESSAGE_ACTION ="com.empover.htconstable.DISPLAY_MESSAGE";
    
    private static final String EXTRA_MESSAGE = "message";

    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
