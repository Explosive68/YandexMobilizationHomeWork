package ru.illarionovroman.yandexmobilizationhomework.ui.activity;

import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.adapter.MainPagerAdapter;
import ru.illarionovroman.yandexmobilizationhomework.view.NonSwipeableViewPager;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation.TranslationFragment;


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

    private MainPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initializeActivity();
    }

    private void initializeActivity() {
        initializePagerAndTabs();
    }

    private void initializePagerAndTabs() {
        mAdapter = new MainPagerAdapter(this, getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        // Keep all screens up all the time to provide smooth animations
        mPager.setOffscreenPageLimit(2);
        mTabLayout.setupWithViewPager(mPager);
        setBottomTabLayoutIcons(mTabLayout, R.array.main_pager_icons);
    }

    /**
     * Full setup of bottom navigation icons
     * @param tabLayout Target TabLayout
     * @param drawableIdsArrayId Resource id of xml array containing drawable ids
     */
    private void setBottomTabLayoutIcons(TabLayout tabLayout, int drawableIdsArrayId) {
        TypedArray imageResIds = getResources().obtainTypedArray(drawableIdsArrayId);
        if (tabLayout.getTabCount() != imageResIds.length()) {
            throw new IllegalStateException("Tabs count must be equal to icons amount");
        }

        // Set icons with colors for all tabs, considering tab selection state
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                int imageId = imageResIds.getResourceId(i, -1);
                if (imageId == -1) {
                    throw new IllegalStateException("Icon from xml array was not found");
                }
                Drawable drawable = ContextCompat.getDrawable(this, imageId);

                int color;
                if (tab.isSelected()) {
                    color = R.color.colorBottomIconSelected;
                } else {
                    color = R.color.colorBottomIcon;
                }
                drawable.setColorFilter(ContextCompat.getColor(this, color),
                        PorterDuff.Mode.SRC_ATOP);

                tab.setIcon(drawable);
            }
        }
        imageResIds.recycle();

        // Change tab icon color on selection/deselection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int selectedTabIconColor = ContextCompat.getColor(MainActivity.this, R.color.colorBottomIconSelected);
                Drawable icon = tab.getIcon();
                if (icon != null) {
                    icon.setColorFilter(selectedTabIconColor, PorterDuff.Mode.SRC_ATOP);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.colorBottomIcon);
                Drawable icon = tab.getIcon();
                if (icon != null) {
                    icon.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_ATOP);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTabLayout != null) {
            mTabLayout.clearOnTabSelectedListeners();
        }
    }

    /**
     * This method is gonna be called from list adapter, then we delegate it to translation fragment,
     * telling it to show this item
     */
    public void onListItemClicked(long itemId) {
        mPager.setCurrentItem(FragmentPosition.TRANSLATION, true);
        TranslationFragment fragment = (TranslationFragment) mAdapter
                .getRegisteredFragment(FragmentPosition.TRANSLATION);
        fragment.loadAndShowItemFromDB(itemId);
    }
}
