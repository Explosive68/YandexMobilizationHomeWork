package ru.illarionovroman.yandexmobilizationhomework.ui.fragments;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.adapters.HistoryCursorAdapter;
import ru.illarionovroman.yandexmobilizationhomework.db.Contract;
import ru.illarionovroman.yandexmobilizationhomework.utils.Utils;


public class InternalFavoritesFragment extends Fragment {

    @BindView(R.id.rvInternalFavorite)
    RecyclerView mRvInternalFavorite;

    private HistoryCursorAdapter mAdapter;

    private ContentObserver mDbObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            Cursor favoritesCursor = Utils.DB.getFavoriteHistoryItemsCursor(getContext());
            if (mAdapter != null) {
                mAdapter.swapCursor(favoritesCursor);
            }
        }
    };

    public InternalFavoritesFragment() {
    }

    public static InternalFavoritesFragment newInstance() {
        InternalFavoritesFragment fragment = new InternalFavoritesFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_internal_favorites, container, false);
        ButterKnife.bind(this, view);

        mRvInternalFavorite.setLayoutManager(new LinearLayoutManager(getContext()));

        Cursor favoritesCursor = Utils.DB.getFavoriteHistoryItemsCursor(getContext());
        mAdapter = new HistoryCursorAdapter(getContext(), favoritesCursor);
        mRvInternalFavorite.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().getContentResolver().registerContentObserver(
                Contract.HistoryEntry.CONTENT_URI_FAVORITES, false, mDbObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDbObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(mDbObserver);
        }
    }
}
