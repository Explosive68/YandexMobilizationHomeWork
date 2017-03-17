package ru.illarionovroman.yandexmobilizationhomework.ui.activities;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.adapters.MainPagerAdapter;


public class MainActivity extends AppCompatActivity {

    private static final String MAIN_PAGER_POSITION = "MAIN_PAGER_POSITION";

    @BindView(R.id.vpMain)
    ViewPager mPager;
    @BindView(R.id.tlMain)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        MainPagerAdapter mainAdapter = new MainPagerAdapter(this, getSupportFragmentManager());
        mPager.setAdapter(mainAdapter);

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MAIN_PAGER_POSITION, mTabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPager.setCurrentItem(savedInstanceState.getInt(MAIN_PAGER_POSITION, 0));
    }
}
