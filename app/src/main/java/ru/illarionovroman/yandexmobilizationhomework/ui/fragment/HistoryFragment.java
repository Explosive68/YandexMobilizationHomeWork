package ru.illarionovroman.yandexmobilizationhomework.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.adapter.InternalPagerAdapter;
import ru.illarionovroman.yandexmobilizationhomework.db.DBManager;


public class HistoryFragment extends BaseFragment {

    private static final String BUNDLE_SELECTED_PAGE = "BUNDLE_SELECTED_PAGE";

    private static final int POSITION_HISTORY = 0;
    private static final int POSITION_FAVORITES = 1;

    @BindView(R.id.tlInternal)
    TabLayout mInternalTabLayout;
    @BindView(R.id.vpInternal)
    ViewPager mInternalPager;

    @BindView(R.id.ivDelete)
    ImageView mIvDelete;

    public HistoryFragment() {
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPagerAndTabs();
        setOnDeleteClickListener();
    }

    private void initPagerAndTabs() {
        // It is important to use CHILD FragmentManager
        InternalPagerAdapter internalAdapter = new InternalPagerAdapter(getActivity(),
                getChildFragmentManager());
        mInternalPager.setAdapter(internalAdapter);
        mInternalTabLayout.setupWithViewPager(mInternalPager);
    }

    private void setOnDeleteClickListener() {
        mIvDelete.setOnClickListener(imageView -> {
            int currentPosition = mInternalPager.getCurrentItem();
            if (currentPosition == POSITION_HISTORY) {
                showDeleteHistoryDialog();
            } else {
                showDeleteFavoritesDialog();
            }
        });
    }

    public void showDeleteHistoryDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.history_title)
                .setMessage(R.string.dialog_delete_history_message)
                .setPositiveButton(android.R.string.yes, ((dialog1, which) -> {
                    DBManager.deleteAllHistory(getContext());
                }))
                .setNegativeButton(android.R.string.cancel, ((dialog1, which) -> {
                    // Do nothing
                }))
                .create();
        dialog.show();
    }

    protected void showDeleteFavoritesDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.favorites_title)
                .setMessage(R.string.dialog_delete_favorites_message)
                .setPositiveButton(android.R.string.yes, ((dialog1, which) -> {
                    DBManager.deleteAllFavorites(getContext());
                }))
                .setNegativeButton(android.R.string.cancel, ((dialog1, which) -> {
                    // Do nothing
                }))
                .create();
        dialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mInternalPager != null) {
            outState.putInt(BUNDLE_SELECTED_PAGE, mInternalPager.getCurrentItem());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mInternalPager != null) {
            mInternalPager.clearOnPageChangeListeners();
        }
    }
}
