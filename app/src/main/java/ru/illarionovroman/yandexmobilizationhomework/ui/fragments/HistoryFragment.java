package ru.illarionovroman.yandexmobilizationhomework.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.adapters.InternalPagerAdapter;


public class HistoryFragment extends Fragment {

    private static final String BUNDLE_SELECTED_PAGE = "BUNDLE_SELECTED_PAGE";

    @Nullable
    @BindView(R.id.tlInternal)
    TabLayout mInternalTabLayout;
    @BindView(R.id.vpInternal)
    ViewPager mInternalPager;

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
        ButterKnife.bind(this, view);

        InternalPagerAdapter internalAdapter = new InternalPagerAdapter(getActivity(),
                getChildFragmentManager());
        mInternalPager.setAdapter(internalAdapter);

        // FIXME: There is no listener for ivDelete button before PageChange event
        mInternalPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ImageView ivDelete = (ImageView) getActivity().findViewById(R.id.ivDelete);
                if (ivDelete != null) {
                    if (position == 0) {
                        ivDelete.setOnClickListener(view -> deleteHistory());
                    } else {
                        ivDelete.setOnClickListener(view -> deleteFavorites());
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mInternalTabLayout = ButterKnife.findById(getActivity(), R.id.tlInternal);
        if (mInternalTabLayout != null) {
            mInternalTabLayout.setupWithViewPager(mInternalPager);
        }

        // FIXME: Not working :(
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_SELECTED_PAGE)) {
            mInternalPager.setCurrentItem(savedInstanceState.getInt(BUNDLE_SELECTED_PAGE, 0));
        }

        return view;
    }

    public void deleteHistory() {
        Toast.makeText(getActivity(), "Delete history not implemented yet",
                Toast.LENGTH_SHORT).show();
    }

    protected void deleteFavorites() {
        Toast.makeText(getActivity(), "Delete favorites not implemented yet",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mInternalTabLayout != null) {
            outState.putInt(BUNDLE_SELECTED_PAGE, mInternalTabLayout.getSelectedTabPosition());
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
