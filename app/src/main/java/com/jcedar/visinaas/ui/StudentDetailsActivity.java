package com.jcedar.visinaas.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jcedar.visinaas.R;
import com.jcedar.visinaas.helper.UIUtils;

import java.util.ArrayList;

public class StudentDetailsActivity extends BaseActivity
        implements DashboardFragment.Listener, StudentDetailsFragment.DetailsListener{

    ViewPagerAdapter detailsAdapter;
    StudentDetailsFragment mFragment = null;
    private static final String TAG = StudentDetailsActivity.class.getSimpleName();
    public ArrayList<Long> mStudents;
    private ViewPager mPager;
    Uri mSelectedStudent;
    String phoneNumber, emailAddress, name;

    public static String ARG_STUDENT_LIST = "ARG_STUDENT_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        if (findViewById(R.id.payItemDetailsPane) != null) {
            mFragment =
                    (StudentDetailsFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.studentDetailFrag);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Intent intent = getIntent();
        if(intent != null){
            mSelectedStudent = intent.getData();
            if( mSelectedStudent == null ) {
                return;
            }
            Bundle payItemBundle = intent.getBundleExtra(ARG_STUDENT_LIST);


            if(payItemBundle != null){
                mStudents = convertToArray(payItemBundle, ARG_STUDENT_LIST);
                Log.d(TAG, "Student id bundle is not null: "
                        + UIUtils.bundle2string(payItemBundle) + "    "+mStudents);
            }else {
                Log.d(TAG, "Student id Bundle is null");
            }
        }

        detailsAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                mStudents.size());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(detailsAdapter);

        int selectedIndex = mStudents.indexOf(
                Long.parseLong(mSelectedStudent.getLastPathSegment()));
        mPager.setCurrentItem(selectedIndex);


        final Toolbar toolbar = getActionBarToolbar();
        if(toolbar == null) return;
        toolbar.setNavigationIcon(R.drawable.ic_up);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle("Student Details");
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




    }



    public void  fabClicked (View v){
        int id = v.getId();
        switch (id){

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onSchoolSelected(long courseId, Bundle data) {
        Log.d(TAG, courseId +" courseId");
        mPager.setCurrentItem(mStudents.indexOf(courseId), true);
    }

    @Override
    public void onFragmentAttached(ListFragment fragment) {

    }

    @Override
    public void onFragmentDetached(ListFragment fragment) {

    }

    @Override
    public void getUserData(String phoneNumber, String emailAddress, String name) {
        setPhoneNumber(phoneNumber);
        setEmailAddress(emailAddress);
        setName(name);
    }

    @Override
    public void onFragmentAttached(Fragment fragment) {

    }

    @Override
    public void onFragmentDetached(Fragment fragment) {

    }


    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final int mSize;

        public ViewPagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
            Log.d( TAG, "Calling "+ViewPagerAdapter.class.getSimpleName());
        }

        @Override
        public Fragment getItem(int position) {
            return StudentDetailsFragment.newInstance(position, StudentDetailsActivity.this);
        }

        @Override
        public int getCount() {
            return mSize;
        }
    }


    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
