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
import ru.illarionovroman.yandexmobilizationhomework.ui.activity.MainActivity;


/**
 * One of the three main fragments. This one contains InternalHistoryFragment and
 * InternalFavoritesFragment via ViewPager.
 */
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

    private InternalPagerAdapter mInternalAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPagerAndTabs();
        setOnDeleteClickListener();
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

    private void initPagerAndTabs() {
        // It is important to use CHILD FragmentManager
        mInternalAdapter = new InternalPagerAdapter(getActivity(),
                getChildFragmentManager());
        mInternalPager.setAdapter(mInternalAdapter);
        mInternalTabLayout.setupWithViewPager(mInternalPager);
    }

    /**
     * Delete button click action depends on the currently selected page
     */
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

    /**
     * This method will be called from {@link MainActivity.OnMainPageChangeListener#onPageSelected(int)}
     * It will cause update of InternalFavoritesFragment visible state.
     *
     * @param isHistoryFragmentVisible is this HistoryFragment visible to user
     */
    public void updateFavoritesVisibleState(boolean isHistoryFragmentVisible) {
        InternalFavoritesFragment fragment = (InternalFavoritesFragment) mInternalAdapter
                .getRegisteredFragment(POSITION_FAVORITES);

        // The logic is as follows:
        // If HistoryFragment is invisible, InternalHistoryFragment will be definitely invisible too.
        // If HistoryFragment is visible, we need to check which page is currently selected in ViewPager.
        if (!isHistoryFragmentVisible) {
            fragment.setIsVisible(false);
        } else {
            boolean isInternalFavoritesVisible = mInternalPager.getCurrentItem() == POSITION_FAVORITES;
            fragment.setIsVisible(isInternalFavoritesVisible);
        }
    }
}
