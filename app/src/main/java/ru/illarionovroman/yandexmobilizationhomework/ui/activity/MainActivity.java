package ru.illarionovroman.yandexmobilizationhomework.ui.activity;

import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.adapter.MainPagerAdapter;
import ru.illarionovroman.yandexmobilizationhomework.model.HistoryItem;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.HistoryFragment;
import ru.illarionovroman.yandexmobilizationhomework.view.NonSwipeableViewPager;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation.TranslationFragment;


/**
 * MainActivity, which contains ViewPager with disabled swipes.
 * I've tried BottomNavigationView, but couldn't find analogue of setOffscreenPageLimit for it,
 * and it was extremely laggy at screens navigation, since it was creating new fragment from scratch
 * every time you switch between screens. Also, it was kinda hard to maintain screen rotations with BNV.
 * So I've replaced it with ViewPager + TabLayout and it works fine.
 */
public class MainActivity extends AppCompatActivity {

    private static final String KEY_MAIN_PAGER_POSITION = "KEY_MAIN_PAGER_POSITION";

    /**
     * Android's enum of possible fragment positions
     */
    @IntDef({FragmentPosition.TRANSLATION, FragmentPosition.HISTORY, FragmentPosition.SETTINGS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FragmentPosition {
        int TRANSLATION = 0;
        int HISTORY = 1;
        int SETTINGS = 2;
    }

    @BindView(R.id.vpMain)
    NonSwipeableViewPager mPager;
    @BindView(R.id.tlMain)
    TabLayout mTabLayout;

    private MainPagerAdapter mAdapter;

    //region Lifecycle methods
    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initializeActivity();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPager.setCurrentItem(savedInstanceState.getInt(KEY_MAIN_PAGER_POSITION));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_MAIN_PAGER_POSITION, mPager.getCurrentItem());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPager != null) {
            mPager.clearOnPageChangeListeners();
        }
        if (mTabLayout != null) {
            mTabLayout.clearOnTabSelectedListeners();
        }
    }
    //----------------------------------------------------------------------------------------------
    //endregion

    //region Initialization methods
    //----------------------------------------------------------------------------------------------
    private void initializeActivity() {
        initializePagerAndTabs();
    }

    private void initializePagerAndTabs() {
        mAdapter = new MainPagerAdapter(this, getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(new OnMainPageChangeListener());
        mTabLayout.setupWithViewPager(mPager);
        // Keep all screens up all the time to provide smooth animations
        mPager.setOffscreenPageLimit(mTabLayout.getTabCount() - 1);
        setBottomTabLayoutIcons(mTabLayout, R.array.main_pager_icons);
    }

    /**
     * Full manual setup of bottom navigation icons.
     * This is the dark side of using TabLayout with icons. With BNV you can just connect it with
     * menu xml resource file.
     * @param tabLayout Target TabLayout
     * @param drawableIdsArrayId Resource id of xml array containing drawable ids
     */
    private void setBottomTabLayoutIcons(TabLayout tabLayout, int drawableIdsArrayId) {
        // Get drawable ids from resources
        TypedArray imageResIds = getResources().obtainTypedArray(drawableIdsArrayId);
        if (tabLayout.getTabCount() != imageResIds.length()) {
            throw new IllegalStateException("Tabs count must be equal to icons amount");
        }
        initializeTabLayoutIcons(tabLayout, imageResIds);
        // Release obtained array
        imageResIds.recycle();

        tabLayout.addOnTabSelectedListener(new OnMainTabSelectedListener());
    }

    /**
     * Set icons with colors for all tabs, considering tab selection state
     */
    private void initializeTabLayoutIcons(TabLayout tabLayout, TypedArray imageResIds) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                // Get icon drawable
                int imageId = imageResIds.getResourceId(i, -1);
                if (imageId == -1) {
                    throw new IllegalStateException("Icon from xml array was not found");
                }
                Drawable drawable = ContextCompat.getDrawable(this, imageId);

                // Set color of icon
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
    }
    //----------------------------------------------------------------------------------------------
    //endregion

    /**
     * This method is going to be called from one of internal fragments,
     * then we delegate the selected item to TranslationFragment, telling it to show this item
     */
    public void onListItemClicked(HistoryItem item) {
        mPager.setCurrentItem(FragmentPosition.TRANSLATION, true);
        TranslationFragment fragment = (TranslationFragment) mAdapter
                .getRegisteredFragment(FragmentPosition.TRANSLATION);
        fragment.showSelectedItem(item);
    }

    /**
     * Tab selection listener. Changes tab icon color on selection/deselection
     */
    private class OnMainTabSelectedListener implements TabLayout.OnTabSelectedListener {
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
        public void onTabReselected(TabLayout.Tab tab) {}
    }

    /**
     * Page change listener. When page selection occurs, listener tells about it to HistoryFragment
     */
    private class OnMainPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            // Decide whether HistoryFragment is visible to user after page selection and ask this
            // fragment to update visibility of its internal fragment
            HistoryFragment fragment = (HistoryFragment) mAdapter
                    .getRegisteredFragment(FragmentPosition.HISTORY);
            if (fragment != null) {
                fragment.updateFavoritesVisibleState(position == FragmentPosition.HISTORY);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    }
}
