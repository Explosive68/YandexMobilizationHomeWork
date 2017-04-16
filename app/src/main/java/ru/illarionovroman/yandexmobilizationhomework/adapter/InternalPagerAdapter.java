package ru.illarionovroman.yandexmobilizationhomework.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.InternalFavoritesFragment;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.InternalHistoryFragment;


public class InternalPagerAdapter extends FragmentPagerAdapter {

    private String[] mTabTitles;

    public InternalPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mTabTitles = context.getResources().getStringArray(R.array.internal_tabs);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return InternalHistoryFragment.newInstance();
        } else {
            return InternalFavoritesFragment.newInstance();
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
