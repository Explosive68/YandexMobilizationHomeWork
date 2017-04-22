package ru.illarionovroman.yandexmobilizationhomework.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.ui.activity.AboutActivity;


/**
 * One of the three main fragments. This one contains all available settings.
 * Right now there is the one item only, "About".
 * If I would have implement lots of settings here, I'd use {@link android.preference.PreferenceFragment}
 */
public class SettingsFragment extends BaseFragment {

    @BindView(R.id.llAbout)
    LinearLayout mLlAbout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLlAbout.setOnClickListener(itemView -> {
            Intent aboutIntent = new Intent(getContext(), AboutActivity.class);
            startActivity(aboutIntent);
        });
    }
}
