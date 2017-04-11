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
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation.TranslationFragment;


public class MainPagerAdapter extends FragmentPagerAdapter {
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

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

    // Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    // Unregister when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    // Returns the fragment for the position (if instantiated)
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
