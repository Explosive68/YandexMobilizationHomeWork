package ru.illarionovroman.yandexmobilizationhomework.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.adapters.InternalPagerAdapter;


public class HistoryFragment extends Fragment {

    @BindView(R.id.tlInternal)
    TabLayout mInternalTabLayout;
    @BindView(R.id.vpInternal)
    ViewPager mInternalPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InternalPagerAdapter internalAdapter = new InternalPagerAdapter(getActivity(), getActivity().getSupportFragmentManager());
        mInternalPager.setAdapter(internalAdapter);

        mInternalTabLayout.setupWithViewPager(mInternalPager);
    }
}
