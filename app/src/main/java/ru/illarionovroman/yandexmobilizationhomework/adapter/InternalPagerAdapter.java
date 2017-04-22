package ru.illarionovroman.yandexmobilizationhomework.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.InternalFavoritesFragment;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.InternalHistoryFragment;

/**
 * Pager adapter which returns InternalHistory and InternalFavorites screens
 */
public class InternalPagerAdapter extends SmartFragmentPagerAdapter {
    private String[] mTabTitles;

    public InternalPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        // Get tab titles and count from xml string array
        mTabTitles = context.getResources().getStringArray(R.array.internal_tabs);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new InternalHistoryFragment();
        } else {
            return new InternalFavoritesFragment();
        }
    }

    @Override
    public int getCount() {
        return mTabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }
}
