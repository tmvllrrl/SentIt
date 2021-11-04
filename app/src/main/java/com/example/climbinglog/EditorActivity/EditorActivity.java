package com.example.climbinglog.EditorActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.climbinglog.R;
import com.google.android.material.tabs.TabLayout;

public class EditorActivity extends AppCompatActivity {

    //Constants that EditorFragment uses.
    public static final String UPDATE_ROUTE_ID = "updateRoute";
    public static final int SELECT_DATE_REQUEST_CODE = 22;
    public static final int DEFAULT_TASK_ID = -1;

    private static final int NUM_PAGES = 3;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private String[] tabTitles = {"Editor", "View Picture", "View Video"};
    private TabLayout tabLayout;

    private Toolbar toolbar;

    private int mRouteID = DEFAULT_TASK_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_editor);
        setTitle(getString(R.string.addroute_activity_title));

        toolbar = findViewById(R.id.my_toolbar_editor);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pager = (ViewPager) findViewById(R.id.viewpager_editor);
        pagerAdapter = new EditorActivity.RouteEditorPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs_editor);
        tabLayout.setupWithViewPager(pager);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(UPDATE_ROUTE_ID)) {
            mRouteID = intent.getIntExtra(UPDATE_ROUTE_ID, DEFAULT_TASK_ID);
        }
    }

    /**
     * PagerAdapter class used with the ViewPager to return the correct fragments at the correct positions
     * in the ViewPager
     * */
    private class RouteEditorPagerAdapter extends FragmentStatePagerAdapter {

        public RouteEditorPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Bundle bundleEditor = new Bundle();
                    bundleEditor.putInt("route_id", mRouteID);
                    EditorFragment editorFragment = new EditorFragment();
                    editorFragment.setArguments(bundleEditor);
                    return editorFragment;
                case 1:
                    Bundle bundleViewPicture = new Bundle();
                    bundleViewPicture.putInt("route_id", mRouteID);
                    ViewPictureFragment viewPictureFragment = new ViewPictureFragment();
                    viewPictureFragment.setArguments(bundleViewPicture);
                    return viewPictureFragment;
                case 2:
                    Bundle bundleViewVideo = new Bundle();
                    bundleViewVideo.putInt("route_id", mRouteID);
                    ViewVideoFragment viewVideoFragment = new ViewVideoFragment();
                    viewVideoFragment.setArguments(bundleViewVideo);
                    return viewVideoFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
