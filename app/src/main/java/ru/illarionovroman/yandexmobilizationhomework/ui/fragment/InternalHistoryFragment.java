package ru.illarionovroman.yandexmobilizationhomework.ui.fragment;

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
import ru.illarionovroman.yandexmobilizationhomework.adapter.HistoryCursorAdapter;
import ru.illarionovroman.yandexmobilizationhomework.db.Contract;
import ru.illarionovroman.yandexmobilizationhomework.util.Utils;


public class InternalHistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int HISTORY_LOADER_ID = 1;

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

        mRvInternalHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        Cursor historyCursor = Utils.DB.getAllHistoryItemsCursor(getContext());
        mAdapter = new HistoryCursorAdapter(getContext(), historyCursor);
        mRvInternalHistory.setAdapter(mAdapter);

        getActivity().getSupportLoaderManager().initLoader(HISTORY_LOADER_ID, null, this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(
                getContext(),
                Contract.HistoryEntry.CONTENT_URI_HISTORY,
                null,
                null,
                null,
                Contract.HistoryEntry.DATE);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            mAdapter.swapCursor(cursor);
        } else {
            mAdapter.swapCursor(null);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
