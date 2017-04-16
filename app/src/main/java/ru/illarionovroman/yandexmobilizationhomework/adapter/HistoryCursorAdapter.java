package ru.illarionovroman.yandexmobilizationhomework.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.db.DBManager;
import ru.illarionovroman.yandexmobilizationhomework.model.HistoryItem;


public class HistoryCursorAdapter extends RecyclerView.Adapter<HistoryCursorAdapter.HistoryViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private OnListItemClickListener mOnClickListener;

    public HistoryCursorAdapter(Context context, Cursor cursor,
                                OnListItemClickListener listener) {
        mContext = context;
        mCursor = cursor;
        mOnClickListener = listener;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.history_list_item, parent,false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        final HistoryItem item = new HistoryItem(mCursor);
        bindHolderItem(holder, item);
    }

    private void bindHolderItem(HistoryViewHolder holder, HistoryItem item) {
        holder.itemView.setTag(item.getId());
        holder.tvOriginalWord.setText(item.getWord());
        holder.tvTranslation.setText(item.getTranslation());
        String translationDirection = String.format(
                mContext.getString(R.string.language_from_to),
                item.getLanguageCodeFrom(),
                item.getLanguageCodeTo());
        holder.tvTranslationDirection.setText(translationDirection);

        holder.ivFavorite.setActivated(item.getIsFavorite());
        holder.ivFavorite.setOnClickListener(view -> {
            boolean activated = view.isActivated();
            if (activated) {
                view.setActivated(false);
                item.setIsFavorite(false);
            } else {
                view.setActivated(true);
                item.setIsFavorite(true);
            }
            DBManager.updateHistoryItemWithId(mContext, item);
        });

        holder.itemView.setOnClickListener(holderItemView -> {
            // Send item id to InternalFragment -> MainActivity -> TranslationFragment
            mOnClickListener.onListItemClicked(item.getId());
        });
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public void swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return;
        }
        this.mCursor = c;
        this.notifyDataSetChanged();
    }

    public void swapCursorWithoutNotify(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return;
        }
        this.mCursor = c;
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivFavorite)
        ImageView ivFavorite;
        @BindView(R.id.tvOriginalWord)
        TextView tvOriginalWord;
        @BindView(R.id.tvTranslation)
        TextView tvTranslation;
        @BindView(R.id.tvTranslationDirection)
        TextView tvTranslationDirection;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnListItemClickListener {
        void onListItemClicked(long itemId);
    }
}
