package ru.illarionovroman.yandexmobilizationhomework.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.adapter.HistoryCursorAdapter;
import ru.illarionovroman.yandexmobilizationhomework.db.DBManager;
import ru.illarionovroman.yandexmobilizationhomework.model.HistoryItem;
import ru.illarionovroman.yandexmobilizationhomework.ui.activity.MainActivity;


/**
 * One of the internal fragments of HistoryFragment. This one shows History list.
 * It uses CursorLoader to keep data fresh.
 */
public class InternalHistoryFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        HistoryCursorAdapter.OnListItemClickListener {

    private static final int HISTORY_LOADER_ID = 1;

    @BindView(R.id.rvInternalHistory)
    RecyclerView mRvInternalHistory;
    @BindView(R.id.tvHistoryEmpty)
    TextView mTvHistoryEmpty;

    private HistoryCursorAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_internal_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvInternalHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        Cursor historyCursor = DBManager.getAllHistoryCursor(getContext());
        mAdapter = new HistoryCursorAdapter(getContext(), historyCursor, this);
        initializeRecyclerView(mAdapter);

        getActivity().getSupportLoaderManager().initLoader(HISTORY_LOADER_ID, null, this);
    }

    private void initializeRecyclerView(HistoryCursorAdapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvInternalHistory.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                layoutManager.getOrientation());
        mRvInternalHistory.addItemDecoration(dividerItemDecoration);
        mRvInternalHistory.setAdapter(adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return DBManager.getHistoryCursorLoader(getContext());
    }

    /**
     * Always show fresh data and toggle empty view state
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            mAdapter.swapCursor(cursor);
        } else {
            mAdapter.swapCursor(null);
        }
        toggleFragmentEmptyState(cursor.getCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private void toggleFragmentEmptyState(int itemsCount) {
        if (itemsCount > 0) {
            mRvInternalHistory.setVisibility(View.VISIBLE);
            mTvHistoryEmpty.setVisibility(View.GONE);
        } else {
            mRvInternalHistory.setVisibility(View.GONE);
            mTvHistoryEmpty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Delegate listItemClick event from HistoryCursorAdapter to MainActivity
     */
    @Override
    public void onListItemClicked(HistoryItem item) {
        ((MainActivity) getActivity()).onListItemClicked(item);
    }
}
