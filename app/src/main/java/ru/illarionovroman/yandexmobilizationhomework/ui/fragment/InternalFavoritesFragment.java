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
import ru.illarionovroman.yandexmobilizationhomework.model.HistoryItem;
import ru.illarionovroman.yandexmobilizationhomework.ui.activity.MainActivity;


/**
 * One of the internal fragments of HistoryFragment. This one shows Favorites list.
 *
 * The hardest part in this fragment is different behaviour of visible data update depends on
 * visibility to user. The goal is to do not delete item immediately when we are toggling its
 * isFavorite state.
 *
 * So, this is reachable through combining {@link #setUserVisibleHint(boolean)}
 * and {@link #setIsVisible(boolean)}
 * which is called from {@link HistoryFragment#updateFavoritesVisibleState(boolean)}.
 *
 * The reason of so complex approach lies in ViewPager's setOffScreenPageLimit(2). It retains
 * all main fragments independent from their actual visibility. So system counts that all of them
 * are visible all the time (and setUserVisibleHint is not invoked), even if user can't see two of
 * them in particular moment.
 */
public class InternalFavoritesFragment extends BaseFragment
        implements HistoryCursorAdapter.OnListItemClickListener{

    @BindView(R.id.rvInternalFavorite)
    RecyclerView mRvInternalFavorite;
    @BindView(R.id.tvFavoritesEmpty)
    TextView mTvFavoritesEmpty;

    private HistoryCursorAdapter mAdapter;

    /**
     * Is fragment visible right now. We need it to decide, whether to update Favorites list
     * immediately upon separate item change, or do it later.
     */
    private boolean mIsVisible = false;

    /**
     * Favorites list updater. I'm using a simple ContentObserver here because of the need to
     * analyze incoming Uri.
     */
    private ContentObserver mFavoritesObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            updateAdapterCursor(mAdapter, uri, mIsVisible);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_internal_favorites, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Cursor favoritesCursor = DBManager.getAllFavoritesCursor(getContext());
        mAdapter = new HistoryCursorAdapter(getContext(), favoritesCursor, this);
        initializeRecyclerView(mAdapter);
        toggleFragmentEmptyState(mAdapter.getItemCount());

        getContext().getContentResolver().registerContentObserver(
                Contract.HistoryEntry.CONTENT_URI_FAVORITES, true, mFavoritesObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mFavoritesObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(mFavoritesObserver);
        }
    }

    private void initializeRecyclerView(HistoryCursorAdapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvInternalFavorite.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        mRvInternalFavorite.addItemDecoration(dividerItemDecoration);
        mRvInternalFavorite.setAdapter(adapter);
    }

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
            Cursor cursor = DBManager.getAllFavoritesCursor(getContext());
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
                        toggleFragmentEmptyState(cursor.getCount());
                    } else {
                        // If it is a specific item change
                        if (isVisible) {
                            // If list is visible right now - update adapter's data,
                            // but do not delete item from list
                            adapter.swapCursorWithoutNotify(cursor);
                        } else {
                            // If list is invisible - update data and display it
                            adapter.swapCursor(cursor);
                            toggleFragmentEmptyState(cursor.getCount());
                        }
                    }
                });
    }

    private void toggleFragmentEmptyState(int itemsCount) {
        if (itemsCount > 0) {
            mRvInternalFavorite.setVisibility(View.VISIBLE);
            mTvFavoritesEmpty.setVisibility(View.GONE);
        } else {
            mRvInternalFavorite.setVisibility(View.GONE);
            mTvFavoritesEmpty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Watch for fragment visibility changes
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisible = isVisibleToUser;
        // When fragment hides - show previously changed data
        if (mAdapter != null && !mIsVisible) {
            mAdapter.notifyDataSetChanged();
            toggleFragmentEmptyState(mAdapter.getItemCount());
        }
    }

    /**
     * Delegate listItemClick event from HistoryCursorAdapter to MainActivity
     */
    @Override
    public void onListItemClicked(HistoryItem item) {
        ((MainActivity) getActivity()).onListItemClicked(item);
    }

    /**
     * This method will be used to set fragment's visibility from the parental fragment
     * depending on parent's visibility and currently selected page
     * @param isVisible Visibility state to set
     */
    public void setIsVisible(boolean isVisible) {
        mIsVisible = isVisible;
    }
}
