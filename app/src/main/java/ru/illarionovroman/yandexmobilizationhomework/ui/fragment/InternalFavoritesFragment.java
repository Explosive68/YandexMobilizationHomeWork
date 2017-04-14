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
import android.widget.TextView;

import butterknife.BindView;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.adapter.HistoryCursorAdapter;
import ru.illarionovroman.yandexmobilizationhomework.db.Contract;
import ru.illarionovroman.yandexmobilizationhomework.db.DBManager;
import ru.illarionovroman.yandexmobilizationhomework.ui.activity.MainActivity;


public class InternalFavoritesFragment extends BaseFragment
        implements HistoryCursorAdapter.OnListItemClickListener{

    @BindView(R.id.rvInternalFavorite)
    RecyclerView mRvInternalFavorite;
    @BindView(R.id.tvFavoritesEmpty)
    TextView mTvFavoritesEmpty;

    private HistoryCursorAdapter mAdapter;

    /**
     * Is fragment visible right now. We need it to decide whether to show data change immediately
     */
    private boolean mIsVisible = false;

    private ContentObserver mFavoritesObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            updateAdapterCursor(mAdapter, uri, mIsVisible);
        }
    };

    /**
     * Update favorites list, performing db load in background
     *
     * @param adapter       {@link HistoryCursorAdapter} to change
     * @param uri           {@link Uri}, which gives us information whether that was single item
     *                      or general data change
     * @param isVisible Is this fragment visible to user right now
     */
    private void updateAdapterCursor(HistoryCursorAdapter adapter, Uri uri, boolean isVisible) {
        Single<Cursor> cursorSingle = Single.create(emitter -> {
            Cursor cursor = DBManager.getFavoriteHistoryItemsCursor(getContext());
            emitter.onSuccess(cursor);
        });

        cursorSingle
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cursor -> {
                    // Analyze URI: is it specific item change?
                    long id = -1;
                    try {
                        id = ContentUris.parseId(uri);
                    } catch (NumberFormatException ignore) {
                    }

                    if (id == -1) {
                        // If it is generic Favorites list change - update data
                        // and display it right now
                        adapter.swapCursor(cursor);
                        setListEmptyState(cursor.getCount());
                    } else {
                        // If it is a specific item change
                        if (isVisible) {
                            // If list is visible right now - update adapter's data,
                            // but do not delete item from list
                            adapter.swapCursorWithoutNotify(cursor);
                        } else {
                            // If list is invisible - update data and display it
                            adapter.swapCursor(cursor);
                            setListEmptyState(cursor.getCount());
                        }
                    }
                });
    }

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
        mAdapter = new HistoryCursorAdapter(getContext(), favoritesCursor, this);
        initializeRecyclerView(mAdapter);
        setListEmptyState(mAdapter.getItemCount());

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

    private void setListEmptyState(int itemsCount) {
        if (itemsCount > 0) {
            mRvInternalFavorite.setVisibility(View.VISIBLE);
            mTvFavoritesEmpty.setVisibility(View.GONE);
        } else {
            mRvInternalFavorite.setVisibility(View.GONE);
            mTvFavoritesEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisible = isVisibleToUser;
        // When fragment hides - show previously changed data
        if (mAdapter != null && !mIsVisible) {
            mAdapter.notifyDataSetChanged();
            setListEmptyState(mAdapter.getItemCount());
        }
    }

    @Override
    public void onListItemClicked(long itemId) {
        ((MainActivity) getActivity()).onListItemClicked(itemId);
    }
}
