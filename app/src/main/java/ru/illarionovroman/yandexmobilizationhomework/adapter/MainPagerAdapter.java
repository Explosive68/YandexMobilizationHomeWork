package ru.illarionovroman.yandexmobilizationhomework.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.HistoryFragment;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.SettingsFragment;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.TranslationFragment;


public class MainPagerAdapter extends FragmentPagerAdapter {
    private int[] mImageResIds;

    public MainPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mImageResIds = context.getResources().getIntArray(R.array.main_pager_icons);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return TranslationFragment.newInstance();
        } else if (position == 1) {
            return HistoryFragment.newInstance();
        } else {
            return SettingsFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return mImageResIds.length;
    }
}