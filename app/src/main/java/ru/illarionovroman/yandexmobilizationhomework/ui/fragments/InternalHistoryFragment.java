package ru.illarionovroman.yandexmobilizationhomework.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.adapters.HistoryCursorAdapter;
import ru.illarionovroman.yandexmobilizationhomework.utils.Utils;


public class InternalHistoryFragment extends Fragment {

    @BindView(R.id.rvInternalHistory)
    RecyclerView mRvInternalHistory;

    private HistoryCursorAdapter mAdapter;

    public InternalHistoryFragment() {
    }

    public static InternalHistoryFragment newInstance() {
        InternalHistoryFragment fragment = new InternalHistoryFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_internal_history, container, false);
        ButterKnife.bind(this, view);

        Cursor historyCursor = Utils.DB.getAllHistoryItemsCursor(getContext());
        mAdapter = new HistoryCursorAdapter(getContext(), historyCursor);
        mRvInternalHistory.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
