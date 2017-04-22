package ru.illarionovroman.yandexmobilizationhomework.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.ui.activity.MainActivity;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.HistoryFragment;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.SettingsFragment;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation.TranslationFragment;


/**
 * Pager adapter which returns fragments for three main screens: Translation, History and Settings
 */
public class MainPagerAdapter extends SmartFragmentPagerAdapter {
    private int[] mImageResIds;

    public MainPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        // Get icon resource ids from xml integer array
        mImageResIds = context.getResources().getIntArray(R.array.main_pager_icons);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == MainActivity.FragmentPosition.TRANSLATION) {
            return new TranslationFragment();
        } else if (position == MainActivity.FragmentPosition.HISTORY) {
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
