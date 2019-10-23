package com.empover.htconstable;

import static com.empover.htconstable.CommonUtilities.SENDER_ID;

import static com.empover.htconstable.CommonUtilities.displayMessage;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.empover.htconstable.activities.LoginActivity;
import com.empover.htconstable.activities.MainActivity;
import com.empover.htconstable.utility.Utility;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
    	Utility.setRegId(getApplicationContext(), registrationId);
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Your device registred with GCM");
        Log.d("USERNAME", LoginActivity.username);
                
        CommonUtilities.mLoginActivity.new LoginAsync(LoginActivity.username, LoginActivity.password, LoginActivity.regId, LoginActivity.device_id, LoginActivity.isSave).execute();
        
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("htp-complain");
        String message1 = intent.getExtras().getString("htp-complain-accept");
        if(message!=null && message.length()>0){
        	generateNotification(context, message);
        }
        if(message1!=null && message1.length()>0) {
        	generateNotification(context, message1);
        }
//        displayMessage(context, message);
        // notifies user
//        generateNotification(context, message);
//        generateNotification(context, message1);
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
    	Intent notificationIntent =null;
        int icon = R.drawable.app_logo;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
        
        if(Utility.getPassword(context)!=null){
			if(Utility.getUsername(context)!=null){
				notificationIntent = new Intent(context, MainActivity.class);
				// set intent so it does not start a new activity
		        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
		                Intent.FLAG_ACTIVITY_SINGLE_TOP);
			}
		}else{
			notificationIntent = new Intent(context, LoginActivity.class);
		}
        
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
//        notification.setLatestEventInfo(context, title, message, intent);
        
        /*Builder builder = new Notification.Builder(context);
        
        builder.setContentTitle(title);
        builder.setContentText("New Notification");
        builder.setSmallIcon(R.drawable.app_logo);
        builder.setStyle(new NotificationCompat());
        
        Notification notification = builder.build();*/
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        
        builder.setContentTitle(title)
        .setContentText(message)
        .setSmallIcon(R.drawable.app_logo)
        .setContentIntent(intent)
        .setStyle(new NotificationCompat.BigTextStyle(builder)
            .bigText(message));
        
        Notification notification = builder.build();
        
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);      

    }

}
