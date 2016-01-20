package com.jcedar.visinaas.gcm;

/**
 * Created by Afolayan on 12/10/2015.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jcedar.visinaas.helper.AccountUtils;
import com.jcedar.visinaas.helper.AppSettings;
import com.jcedar.visinaas.ui.MainActivity;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class RegisterApp extends AsyncTask<Void, Void, String> {

    private static final String TAG = "GCMRelated";
    Context ctx;
    GoogleCloudMessaging gcm;
    String SENDER_ID = "204576531803";
    String regid = null;
    private int appVersion;
    public RegisterApp(Context ctx, GoogleCloudMessaging gcm, int appVersion){
        this.ctx = ctx;
        this.gcm = gcm;
        this.appVersion = appVersion;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(Void... arg0) {
        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(ctx);
            }
            regid = gcm.register(SENDER_ID);
            Log.d(TAG, regid +" regId");
            msg = "Device registered, registration ID=" + regid;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            sendRegistrationIdToBackend();

            // Persist the regID - no need to register again.
            storeRegistrationId(ctx, regid);
        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
        }
        return msg;
    }

    private void storeRegistrationId(Context ctx, String regid) {
        final SharedPreferences prefs = ctx.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        Log.d(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AccountUtils.PROPERTY_REG_ID, regid);
        editor.putInt(AccountUtils.PROPERTY_APP_VERSION, appVersion);
        editor.commit();

    }


    private void sendRegistrationIdToBackend() {
        URI url = null;
        try {
            String email = AccountUtils.getUserEmail(ctx);

            url = new URI(AppSettings.SERVER_URL +"register.php?regId=" + regid+"&email="+ (email!= null ? email: "") );
            //url = new URI("http://www.mobile.jcedar.com.ng/gcm_server/register.php?regId=" + regid+"&email="+ (email!= null ? email: "") );
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        request.setURI(url);
        try {
            httpclient.execute(request);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Toast.makeText(ctx, "Registration Completed. Now you can see the notifications", Toast.LENGTH_SHORT).show();
        Log.d(TAG, result);
    }
}
