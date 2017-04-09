package ru.illarionovroman.yandexmobilizationhomework.ui.fragment;

import android.content.ContentUris;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.adapter.HistoryCursorAdapter;
import ru.illarionovroman.yandexmobilizationhomework.db.Contract;
import ru.illarionovroman.yandexmobilizationhomework.db.DBManager;


public class InternalFavoritesFragment extends BaseFragment {

    @BindView(R.id.rvInternalFavorite)
    RecyclerView mRvInternalFavorite;

    private HistoryCursorAdapter mAdapter;

    private boolean mIsVisible = false;

    private ContentObserver mFavoritesObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            // Update favorites list, performing db load in background
            Single.just(1)
                    .map(integer -> DBManager.getFavoriteHistoryItemsCursor(getContext()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(cursor -> {
                        // Check whether at least one element exists
                        if (cursor.moveToFirst()) {
                            if (!mIsVisible) {
                                // If screen is not visible right now - update immediately
                                mAdapter.swapCursor(cursor);
                            } else {
                                // Otherwise, analyze URI: is it specific item change?
                                long id = -1;
                                try {
                                    id = ContentUris.parseId(uri);
                                } catch (NumberFormatException ignore) {
                                }

                                if (id != -1) {
                                    // If yes - update adapter's data,
                                    // but do not delete item from list for now
                                    mAdapter.swapCursorWithoutNotify(cursor);
                                } else {
                                    // If it is generic favorite list change - update data
                                    // and display it right now
                                    mAdapter.swapCursor(cursor);
                                }
                            }
                        } else {
                            // No elements? Seems like someone pressed
                            // "Delete all history/favorites" button, show changes instantly
                            mAdapter.swapCursor(null);
                        }
                    });
        }
    };

    public InternalFavoritesFragment() {
    }

    public static InternalFavoritesFragment newInstance() {
        InternalFavoritesFragment fragment = new InternalFavoritesFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_internal_favorites, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Cursor favoritesCursor = DBManager.getFavoriteHistoryItemsCursor(getContext());
        mAdapter = new HistoryCursorAdapter(getContext(), favoritesCursor);
        initializeRecyclerView(mAdapter);

        getContext().getContentResolver().registerContentObserver(
                Contract.HistoryEntry.CONTENT_URI_FAVORITES, true, mFavoritesObserver);
    }

    private void initializeRecyclerView(HistoryCursorAdapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvInternalFavorite.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        mRvInternalFavorite.addItemDecoration(dividerItemDecoration);
        mRvInternalFavorite.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mFavoritesObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(mFavoritesObserver);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisible = isVisibleToUser;
        // Whenever visibility changes - always notify adapter, to be sure it's showing actual data
        if (mAdapter != null && !mIsVisible) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
