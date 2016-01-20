package com.jcedar.visinaas.gcm;

/**
 * Created by Afolayan on 12/10/2015.
 */

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jcedar.visinaas.R;
import com.jcedar.visinaas.helper.AccountUtils;
import com.jcedar.visinaas.ui.MainActivity;

public class GcmIntentServices extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "GcmIntentService";
    private static final String PHONE_NUMBER = "phoneNumber";
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    String phoneNumber;

    public GcmIntentServices() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i+1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.d(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

                phoneNumber = extras.getString("phone");
                AccountUtils.setPhoneNumber(this, phoneNumber);
                Log.e(TAG, phoneNumber + " phoneNumber");

                // Post notification of received message.
                sendNotification(extras.getString("Notice"));


                Log.d(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class)
                        .putExtra(PHONE_NUMBER, phoneNumber),
                0);

        Intent sendSMS = new Intent(Intent.ACTION_VIEW).putExtra("address", AccountUtils.getPhoneNumber(this)).setType("vnd.android-dir/mms-sms");
         PendingIntent smsIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), sendSMS, 0);

        Log.d(TAG, "phone number "+phoneNumber + " and "+AccountUtils.getPhoneNumber(this));
        Intent call = new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel: " + AccountUtils.getPhoneNumber(this)));
        PendingIntent callIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), call, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("VisiBook")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .addAction(R.mipmap.ic_sms, "Send SMS", smsIntent )
                        .addAction(R.mipmap.ic_call, "Call", callIntent )
                        .setPriority(Notification.PRIORITY_HIGH)
                ;

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}