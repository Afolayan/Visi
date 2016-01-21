package com.jcedar.visinaas.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.jcedar.visinaas.helper.AppHelper;
import com.jcedar.visinaas.helper.UIUtils;
import com.jcedar.visinaas.io.model.Student;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/*import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;*/

public class FbActivity1 extends FragmentActivity {

    private static final String TAG = FbActivity1.class.getSimpleName();
    private LoginButton loginBtn;
    private TextView username;
    private UiLifecycleHelper uiHelper;
    private ImageView imageView;
    private boolean isEmailChecked = false;
    private Button checkEmail;
    GoogleCloudMessaging gcm;
    String regid;
    String emailS ;

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
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }
        loginBtn = (LoginButton) findViewById(R.id.fb_login_button);
        loginBtn.setVisibility(View.INVISIBLE);
        checkEmail = (Button) findViewById( R.id.checkEmailButton);

            // do this if email is checked and found
            loginBtn.setReadPermissions(Arrays.asList("email"));
            loginBtn.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {

                @Override
                public void onUserInfoFetched(GraphUser graphUser) {
                    if (graphUser != null) {


                        AccountUtils.setUserId(FbActivity1.this, graphUser.getId());
                        AccountUtils.setUserName(FbActivity1.this, graphUser.getName());


                        String photoUrl = "https://graph.facebook.com/" + graphUser.getId() + "/picture?type=large";
                        try {
                            Bitmap bb = new AccountUtils.LoadProfileImage().execute(photoUrl).get();
                            //imageView.setImageBitmap(bb);
                            UIUtils.setProfilePic(FbActivity1.this, bb);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        AppHelper.pullAndSaveStudentChapterData(FbActivity1.this);
                        AppHelper.pullAndSaveAllStudentData(FbActivity1.this);

                        startActivity(new Intent(FbActivity1.this, DashboardActivity.class));
                        AccountUtils.setFirstRun(false, FbActivity1.this);
                        FbActivity1.this.finish();
                        //imageView.setVisibility(View.VISIBLE);
                    } else {
                        //username.setText("You are not logged in");

                    }
                }
            });

            // get email and check db is true

            checkEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText et = (EditText) findViewById(R.id.email);
                    String email = et.getText().toString().trim();
                    if( email.toLowerCase().contains("@")){
                       /* emailS = email;
                        RequestHandler requestHandler = new RequestHandler();
                        requestHandler.checkUserEmail(email, onSuccess, onError);
*/
                    } else {
                        et.setError("Enter a valid email address");
                    }
                }
            });

    }
    /*private Listener<String> onSuccess = new Listener<String>() {
        @Override
        public void onResponse(String s) {
            if( !s.equalsIgnoreCase("")) {
                Log.e(TAG, "response is "+s);
                parseUserJson(s);

            }
            else
                UIUtils.showAlert("Oops!!!",
                        emailS+ " is not found in the database\nContact your fellowship's PRO",
                        FbActivity1.this);

        }
    };

    private ErrorListener onError = new ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            UIUtils.showAlert("Oops!!!", "Something happened", FbActivity1.this);
        }
    };*/


    private Session.StatusCallback statusCallback = new Session.StatusCallback(){

        @Override
        public void call(Session session, SessionState sessionState, Exception e) {
            if( sessionState.isOpened()){
                Log.d("FbActivity1", "Facebook session opened.");
                Request.newMeRequest(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser graphUser, Response response) {
                        Log.e(TAG, "Name: " + graphUser.getName() + "  " + graphUser.getBirthday());
                    }
                });
            } else if ( sessionState.isClosed()){
                Log.d("FbActivity1", "Facebook session closed.");

            }
        }
    };



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

            Log.d(TAG, student[0].getChapter() +" chapter");
        } catch (Exception e){
            e.printStackTrace();
        } finally {
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
