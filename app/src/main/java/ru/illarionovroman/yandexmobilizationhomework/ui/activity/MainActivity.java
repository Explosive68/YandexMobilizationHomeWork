package ru.illarionovroman.yandexmobilizationhomework.ui.activity;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.adapter.MainPagerAdapter;
import ru.illarionovroman.yandexmobilizationhomework.adapter.NonSwipeableViewPager;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.HistoryFragment;


public class MainActivity extends AppCompatActivity {

    private static final String MAIN_PAGER_POSITION = "MAIN_PAGER_POSITION";

    /**
     * Android's enum of possible fragment positions
     */
    @IntDef({FragmentPosition.TRANSLATION, FragmentPosition.HISTORY, FragmentPosition.SETTINGS})
    @Retention(RetentionPolicy.SOURCE)
    private @interface FragmentPosition {
        int TRANSLATION = 0;
        int HISTORY = 1;
        int SETTINGS = 2;
    }

    @BindView(R.id.vpMain)
    NonSwipeableViewPager mPager;
    @BindView(R.id.tlMain)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initializeActivity(savedInstanceState);
    }

    private void initializeActivity(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            if (savedInstanceState != null) {
                @FragmentPosition int position = savedInstanceState.getInt(MAIN_PAGER_POSITION,
                        FragmentPosition.TRANSLATION);
                //updateActionBar(position);
            }
        }
        initializePagerAndTabs();
    }

    private void initializePagerAndTabs() {
        MainPagerAdapter mainAdapter = new MainPagerAdapter(this, getSupportFragmentManager());
        mPager.setAdapter(mainAdapter);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //updateActionBar(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // Keep all screens up all the time
        mPager.setOffscreenPageLimit(2);

        mTabLayout.setupWithViewPager(mPager);
        setTabLayoutIcons(mTabLayout, R.array.main_pager_icons);
    }

    private void setTabLayoutIcons(TabLayout tabLayout, int drawableIdsArrayId) {
        TypedArray imageResIds = getResources().obtainTypedArray(drawableIdsArrayId);
        if (tabLayout.getTabCount() != imageResIds.length()) {
            throw new IllegalStateException("Tabs count must be equal to icons amount");
        }
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(imageResIds.getResourceId(i, -1));
        }
        imageResIds.recycle();
    }

    private void updateActionBar(@FragmentPosition int newPosition) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            switch (newPosition) {
                case FragmentPosition.TRANSLATION:
                    actionBar.setCustomView(R.layout.actionbar_translation);
                    break;
                case FragmentPosition.HISTORY:
                    try {
                        actionBar.setCustomView(R.layout.actionbar_history);
                        MainPagerAdapter adapter = ((MainPagerAdapter) mPager.getAdapter());
                        HistoryFragment fragment = (HistoryFragment) adapter.getRegisteredFragment(newPosition);
                        fragment.onFragmentSelected();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;
                case FragmentPosition.SETTINGS:
                    actionBar.setCustomView(R.layout.actionbar_settings);
                    break;
                default:
                    throw new IllegalArgumentException("Unacceptable fragment position: " + newPosition);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPager.setCurrentItem(savedInstanceState.getInt(MAIN_PAGER_POSITION));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MAIN_PAGER_POSITION, mPager.getCurrentItem());
    }
}
