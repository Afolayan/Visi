package com.jcedar.visinaas.ui;

import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jcedar.visinaas.R;
import com.jcedar.visinaas.gcm.RegisterApp;
import com.jcedar.visinaas.helper.AccountUtils;
import com.jcedar.visinaas.helper.AppSettings;
import com.jcedar.visinaas.helper.ServiceHandler;
import com.jcedar.visinaas.helper.UIUtils;
import com.jcedar.visinaas.io.jsonhandlers.StudentChapterHandler;
import com.jcedar.visinaas.io.jsonhandlers.StudentHandler;
import com.jcedar.visinaas.io.model.Student;
import com.jcedar.visinaas.provider.DataContract;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;


public class FbActivity extends FragmentActivity {

    private static final String TAG = FbActivity.class.getSimpleName();
    private LoginButton loginBtn;
    private TextView username;
    private UiLifecycleHelper uiHelper;
    private ImageView imageView;
    private boolean isEmailChecked = false;
    private Button checkEmail;
    GoogleCloudMessaging gcm;
    String regid;
    ServiceHandler serviceHandler;
    boolean hasGoneToFb = false;
    Context context = FbActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( ! AccountUtils.isFirstRun(this)) {
            startActivity( new Intent( this, DashboardActivity.class));
            finish();
        }
        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb);
        serviceHandler = new ServiceHandler();

        try {
            //For key hashes
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
                e.printStackTrace();
        }


        loginBtn = (LoginButton) findViewById(R.id.fb_login_button);
        loginBtn.setVisibility(View.INVISIBLE);
        checkEmail = (Button) findViewById( R.id.checkEmailButton);

        checkEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.email);
                String email = et.getText().toString().trim();
                if (email.toLowerCase().contains("@") && UIUtils.isOnline(FbActivity.this)) {
                    //check db
                    Toast.makeText(FbActivity.this, "Verifying email, please wait", Toast.LENGTH_SHORT).show();
                    new CheckUserEmail(email).execute();
                } else {
                    et.setError("Enter a valid email address");
                }
            }
        });

        if( UIUtils.isOnline( this )) {

            // do this if email is checked and found
            loginBtn.setReadPermissions(Arrays.asList("email"));
            loginBtn.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {

                @Override
                public void onUserInfoFetched(GraphUser graphUser) {
                    if (graphUser != null) {


                        AccountUtils.setUserId(FbActivity.this, graphUser.getId());
                        AccountUtils.setUserName(FbActivity.this, graphUser.getName());


                        String photoUrl = "https://graph.facebook.com/" + graphUser.getId() + "/picture?type=large";
                        hasGoneToFb = true;
                        try {
                            LoadProfileImage ll = new LoadProfileImage();
                            Bitmap bb = ll.execute(photoUrl).get();
                            UIUtils.setProfilePic(FbActivity.this, bb);
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        GetAllData allData = new GetAllData();
                        allData.execute();
                        startActivity(new Intent(FbActivity.this, DashboardActivity.class));
                        AccountUtils.setFirstRun(false, FbActivity.this);
                        FbActivity.this.finish();
                    }


                }
            });

        }

    }

    public class GetAllData extends AsyncTask<Void, Void, Void> {

        public GetAllData(){

        }

        @Override
        protected Void doInBackground(Void... params) {
            String result="";
            StudentHandler ss = new StudentHandler(context);
            StudentChapterHandler ss1 = new StudentChapterHandler(context);
            try {
                String response =  ServiceHandler.makeServiceCall
                        (AppSettings.SERVER_URL +"get_all_students.php", ServiceHandler.GET);
                if(response == null){
                    return null ;
                }
                Log.e(TAG, response + " response");

                ArrayList<ContentProviderOperation> operations = ss.parse(response);
                if (operations.size() > 0) {
                    ContentResolver resolver = context.getContentResolver();
                    resolver.applyBatch(DataContract.CONTENT_AUTHORITY, operations);
                }
            }catch (IOException | OperationApplicationException | RemoteException e) {
                e.printStackTrace();
            }

            try {
                String chapter="";
                if ( AccountUtils.getUserChapter(context) != null){
                    chapter = AccountUtils.getUserChapter(context);
                }
                Log.e(TAG, "starting student chapter data response");
                String response =  ServiceHandler.makeServiceCall
                        (AppSettings.SERVER_URL +"getUsersChapter.php?chapter="+chapter, ServiceHandler.GET);
                if(response == null){
                    return null;
                }
                Log.e(TAG, response + " response");

                ArrayList<ContentProviderOperation> operations =ss1.parse(response);
                if (operations.size() > 0) {
                    ContentResolver resolver = context.getContentResolver();
                    resolver.applyBatch(DataContract.CONTENT_AUTHORITY, operations);

                }


            }catch (IOException | OperationApplicationException | RemoteException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback(){

        @Override
        public void call(Session session, SessionState sessionState, Exception e) {
            if( sessionState.isOpened()){
                Log.d("FbActivity", "Facebook session opened.");
                Request.newMeRequest(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser graphUser, Response response) {
                        Log.e(TAG, "Name: " + graphUser.getName() + "  " + graphUser.getBirthday());
                    }
                });
            } else if ( sessionState.isClosed()){
                Log.d("FbActivity", "Facebook session closed.");

            }
        }
    };

    public class LoadProfileImage extends AsyncTask<String, String, Bitmap> {
        // ImageView downloadedImage;
        Bitmap photoBitmap;

        public LoadProfileImage() {
            //this.downloadedImage = image;
        }
        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap icon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage() + "Hello world");
                e.printStackTrace();
            }
            return icon;
        }

        protected void onPostExecute(Bitmap result) {
            //downloadedImage.setImageBitmap(result);
            photoBitmap = result;
        }

    }

    public class CheckUserEmail extends AsyncTask<Void, Void, String>{
        ProgressDialog dialog = new ProgressDialog(FbActivity.this);


        String emailS ;
        public CheckUserEmail(String email){
            emailS = email;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Verifying email address....");
            dialog.setTitle("Please Wait");
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
        }


        @Override
        protected String doInBackground(Void... params) {
           String result="";
            try {
                String url = AppSettings.SERVER_URL +"check_email.php?email="+emailS;
                result =  serviceHandler.makeServiceCall(url, ServiceHandler.GET);
                Log.e(TAG, result +" json");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if( dialog.isShowing())
                dialog.dismiss();
            if ( s.equals("404")){
                UIUtils.showAlert("Oops!!!", emailS+ " is not found in the database", FbActivity.this);
            } else if ( s.equals("101")){
                UIUtils.showAlert("Oops!!!", "Enter a valid email address", FbActivity.this);
            } else {
                parseUserJson(s);
                isEmailChecked = true;
                loginBtn.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();

        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


    public void parseUserJson ( String json ){
        if(json == null){
            return;
        }
        Log.d(TAG, TextUtils.isEmpty(json) ? "Empty Student Json" : json);
            Student[] student = Student.fromJson(json);

        try{
            AccountUtils.setUserGender(this, student[0].getGender());
            AccountUtils.setUserChapter(this, student[0].getChapter());
            AccountUtils.setUserEmail(this, student[0].getEmail());
            AccountUtils.setUserCourse(this, student[0].getCourse());
            AccountUtils.setUserPhoneNumber(this, student[0].getPhoneNumber());
            AccountUtils.setUserDOB(this, student[0].getDateOfBirth());

            Log.e(TAG, student[0].getChapter() +" chapter");
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            Log.d(TAG, "inside finally block!!!");
            if (UIUtils.checkPlayServices(this)) {
                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                regid = AccountUtils.getRegistrationId(getApplicationContext());

                if (regid.isEmpty()) {
                    new RegisterApp(getApplicationContext(), gcm, UIUtils.getAppVersion(getApplicationContext())).execute();
                }else{
                    Toast.makeText(getApplicationContext(), "Device already Registered", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "No valid Google Play Services APK found.");
            }
        }


    }
}
