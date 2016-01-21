package com.jcedar.visinaas.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AppEventsLogger;
import com.jcedar.visinaas.R;
import com.jcedar.visinaas.helper.AccountUtils;
import com.jcedar.visinaas.provider.DataContract;
import com.jcedar.visinaas.ui.view.SlidingTabLayout;

import java.util.HashSet;
import java.util.Set;

public class DashboardActivity extends BaseActivity
        implements   StudentListFragment.Listener, AllStudentListFragment.Listener{

    private Toolbar toolbar;
    private SlidingTabLayout tabs;
    private  Context context = DashboardActivity.this;
    private Set<Fragment> mHomeFragments = new HashSet<>();
    private static final String TAG = DashboardActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("NAAS Western Nigeria");
        setSupportActionBar(toolbar);
        toolbar.setCollapsible(true);
        DesignPagerAdapter adapter = new DesignPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        tabs.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        Resources res = getResources();
        tabs.setSelectedIndicatorColors(res.getColor(R.color.tab_selected_strip));
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.theme_primary_light);
            }
        });

        tabs.setViewPager(viewPager);

        if (tabs != null) {
            tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset,
                                           int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE);
                }
            });
        }
    }

    @Override
    public void onSchoolSelected(long studentId) {
        Intent detailIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = DataContract.StudentsChapter.buildStudentUri(studentId);
        detailIntent.setData(uri);
        startActivity(detailIntent);
    }

    @Override
    public void onFragmentAttached(Fragment fragment) {
        mHomeFragments.add(fragment);
    }

    @Override
    public void onFragmentDetached(Fragment fragment) {
        mHomeFragments.remove(fragment);
    }

    @Override
    public void onAllSelected(long studentId) {
        Intent detailIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = DataContract.Students.buildStudentUri(studentId);
        detailIntent.setData(uri);
        startActivity(detailIntent);
    }


    class DesignPagerAdapter extends FragmentStatePagerAdapter {

        public DesignPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if ( position == 0)
                return StudentListFragment.newInstance(position);
            else
                return AllStudentListFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if ( position == 0)
                return AccountUtils.getUserChapter(context);

            else return "All NAAS";
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
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
    protected int getSelfNavDrawerItem() {
        return NavigationDrawerFragment.MenuConstants.NAVDRAWER_ITEM_DASHBOARD;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    protected Toolbar getActionBarToolbar() {
        return super.getActionBarToolbar();
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {

        for (Fragment fragment : mHomeFragments) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                if (!fragment.getUserVisibleHint()) {
                    continue;
                }
            }

            return ViewCompat.canScrollVertically(fragment.getView(), -1);
        }

        return false;

    }
}
