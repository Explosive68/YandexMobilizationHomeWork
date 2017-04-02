package ru.illarionovroman.yandexmobilizationhomework.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.util.Utils;


public class LanguageSelectionAdapter extends RecyclerView.Adapter<LanguageSelectionAdapter.LanguageViewHolder> {

    private Context mContext;
    private Object[] mLangEntriesArray;
    private String mCurrentLangCode;
    private OnListItemClickListener mOnClickListener;

    public LanguageSelectionAdapter(Context context, String currentLangCode,
                                    OnListItemClickListener listener) {
        mContext = context;
        LinkedHashMap<String, String> languages = Utils.getLanguagesMap(mContext);
        mLangEntriesArray = languages.entrySet().toArray();
        mCurrentLangCode = currentLangCode;
        mOnClickListener = listener;
    }

    @Override
    public LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.language_selection_list_item,
                parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LanguageViewHolder holder, int position) {
        // We know the data type for sure
        @SuppressWarnings("unchecked")
        LinkedHashMap.Entry<String, String> langEntry =
                ((LinkedHashMap.Entry<String, String>) mLangEntriesArray[position]);

        String itemLanguageName = langEntry.getValue();
        holder.tvLanguageName.setText(itemLanguageName);

        String itemLanguageCode = langEntry.getKey();
        if (!TextUtils.isEmpty(mCurrentLangCode)) {
            if (itemLanguageCode.equals(mCurrentLangCode)) {
                holder.ivCheck.setVisibility(View.VISIBLE);
            } else {
                holder.ivCheck.setVisibility(View.INVISIBLE);
            }
        }

        // Return language code to pass it further as activity result
        holder.itemView.setOnClickListener(view -> {
            mOnClickListener.onListItemClick(itemLanguageCode);
        });
    }

    @Override
    public int getItemCount() {
        return mLangEntriesArray.length;
    }

    class LanguageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvLanguageName)
        TextView tvLanguageName;
        @BindView(R.id.ivCheck)
        ImageView ivCheck;

        public LanguageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnListItemClickListener {
        void onListItemClick(String langCode);
    }
}
