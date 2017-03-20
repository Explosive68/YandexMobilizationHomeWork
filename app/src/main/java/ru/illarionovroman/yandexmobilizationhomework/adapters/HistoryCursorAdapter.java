package ru.illarionovroman.yandexmobilizationhomework.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.illarionovroman.yandexmobilizationhomework.R;

/**
 * Created by WakeUp on 20.03.2017.
 */

public class HistoryCursorAdapter extends RecyclerView.Adapter<HistoryCursorAdapter.HistoryViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public HistoryCursorAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.history_list_item, parent,false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        // TODO: Set data for item
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null;
        }

        Cursor temp = mCursor;
        this.mCursor = c;

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        // TODO: Declare history list item fields

        public HistoryViewHolder(View itemView) {
            super(itemView);

            // TODO: Implement view onClick behaviour (open Translation fragment with passed in _id)
        }
    }

}
