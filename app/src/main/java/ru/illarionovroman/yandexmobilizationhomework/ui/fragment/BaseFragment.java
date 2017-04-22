package ru.illarionovroman.yandexmobilizationhomework.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.squareup.leakcanary.RefWatcher;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.illarionovroman.yandexmobilizationhomework.MobilizationApp;


/**
 * Base fragment for all fragments in app
 */
public abstract class BaseFragment extends Fragment {

    protected Unbinder unbinder;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        // Watch for fragment leaks
        RefWatcher refWatcher = MobilizationApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
