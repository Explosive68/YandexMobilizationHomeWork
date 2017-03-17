package ru.illarionovroman.yandexmobilizationhomework.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragments.InternalFavoritesFragment;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragments.InternalHistoryFragment;


public class InternalPagerAdapter extends FragmentPagerAdapter {

    private String[] mTabTitles;

    private Context mContext;

    public InternalPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mTabTitles = mContext.getResources().getStringArray(R.array.internal_tabs);
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
