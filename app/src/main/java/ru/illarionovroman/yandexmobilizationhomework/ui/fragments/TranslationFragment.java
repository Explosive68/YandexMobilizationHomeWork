package ru.illarionovroman.yandexmobilizationhomework.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.illarionovroman.yandexmobilizationhomework.R;


public class TranslationFragment extends Fragment {

    @BindView(R.id.tvTranslation)
    TextView tvTranslation;
    @BindView(R.id.btnTest)
    Button btnTest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_translation, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO: Init ui
        btnTest.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Click!", Toast.LENGTH_SHORT).show();
        });
    }

}
