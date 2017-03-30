package ru.illarionovroman.yandexmobilizationhomework.ui.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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


public class InternalFavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FAVORITES_LOADER_ID = 2;

    @BindView(R.id.rvInternalFavorite)
    RecyclerView mRvInternalFavorite;

    private HistoryCursorAdapter mAdapter;

    private Boolean mIsVisible;

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

        getActivity().getSupportLoaderManager().initLoader(FAVORITES_LOADER_ID, null, this);

        return view;
    }

    /**
     * We don't need to instantly delete manually unfavorited items, so we update data when
     * Favorites fragment has been hidden
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Save it to prevent instant item deletion in onLoadFinished()
        mIsVisible = isVisibleToUser;

        if (!isVisibleToUser) {
            Context context = getContext();
            if (context != null && mAdapter != null) {
                Cursor favoritesCursor = Utils.DB.getFavoriteHistoryItemsCursor(context);
                mAdapter.swapCursor(favoritesCursor);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(
                getContext(),
                Contract.HistoryEntry.CONTENT_URI_FAVORITES,
                null,
                null,
                null,
                Contract.HistoryEntry.DATE);
        return cursorLoader;
    }

    /**
     * Do not delete items while we are looking at it
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mIsVisible != null && !mIsVisible) {
            if (cursor.moveToFirst()) {
                mAdapter.swapCursor(cursor);
            } else {
                mAdapter.swapCursor(null);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
