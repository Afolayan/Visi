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

import java.util.ArrayList;

public class AllStudentDetailsActivity extends BaseActivity
            implements HomeFragment.Listener{

    public static final String ARG_ALL_LIST = "ARG_ALL_LIST";
    private static final String TAG = AllStudentDetailsActivity.class.getSimpleName();
    public ArrayList<Long> mStudents;
    HomeFragment mHomeFragment = null;
    private ViewPager mPager;
    private AllStudentPagerAdapter mPagerAdapter;
    Uri mSelectedStudent;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_student_details);

        if (findViewById(R.id.installmentDetailsPane) != null) {
            mHomeFragment =
                    (HomeFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.studentFrag);

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
            Bundle installmentBundle = intent.getBundleExtra(ARG_ALL_LIST);


            if(installmentBundle != null){
                mStudents = convertToArray(installmentBundle, ARG_ALL_LIST);
                Log.d(TAG, "Installment bundle is not null: ");
                       // + UIUtils.bundle2string(installmentBundle));
            }else {
                Log.d(TAG, "Installment Bundle is null");
            }
        }

        mPagerAdapter = new AllStudentPagerAdapter(getSupportFragmentManager(),
                mStudents.size());
        mPager = (ViewPager) findViewById(R.id.pager2);
        mPager.setAdapter(mPagerAdapter);

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
                toolbar.setTitle("Deatils");
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_student_details, menu);
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
    public void onAllSelected(long studentId, Bundle data) {
        mPager.setCurrentItem(mStudents.indexOf(studentId), true);
    }

    @Override
    public void onFragmentAttached(ListFragment fragment) {

    }

    @Override
    public void onFragmentDetached(ListFragment fragment) {

    }

    private class AllStudentPagerAdapter extends FragmentStatePagerAdapter {


        private final int mSize;

        public AllStudentPagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
            Log.d(TAG, "Calling AllStudentPagerAdapter");
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "Calling Fragment");
            return AllStudentsDetailsFragment.newInstance(position, AllStudentDetailsActivity.this);
        }

        @Override
        public int getCount() {
            return mSize;
        }
    }
}
