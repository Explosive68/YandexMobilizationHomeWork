package ru.illarionovroman.yandexmobilizationhomework.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragments.HistoryFragment;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragments.SettingsFragment;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragments.TranslationFragment;


public class MainPagerAdapter extends FragmentPagerAdapter {

    private int[] mImageResIds;

    private Context mContext;
    
    public MainPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mImageResIds = mContext.getResources().getIntArray(R.array.main_pager_icons);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new TranslationFragment();
        } else if (position == 1) {
            return new HistoryFragment();
        } else {
            return new SettingsFragment();
        }
    }

    @Override
    public int getCount() {
        return mImageResIds.length;
    }
}
