package ru.illarionovroman.yandexmobilizationhomework.ui.activities;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragments.HistoryFragment;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragments.SettingsFragment;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragments.TranslationFragment;


public class MainActivity extends AppCompatActivity {

    private static final String BOTTOM_NAVIGATION_POSITION = "BOTTOM_NAVIGATION_POSITION";

    /**
     * Android's enum of possible fragment positions
     */
    @IntDef({FragmentPosition.TRANSLATION, FragmentPosition.HISTORY, FragmentPosition.SETTINGS})
    @Retention(RetentionPolicy.SOURCE)
    private @interface FragmentPosition {
        int TRANSLATION = 0;
        int HISTORY = 1;
        int SETTINGS = 2;
    }

    @BindView(R.id.bnvMain)
    BottomNavigationView mBnvMain;

    private @FragmentPosition int mCurrentFragmentPosition = FragmentPosition.TRANSLATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initializeActivity(savedInstanceState);
    }

    private void initializeActivity(Bundle savedInstanceState) {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
        }

        if (savedInstanceState != null) {
            @FragmentPosition int restoredPosition = savedInstanceState.getInt(BOTTOM_NAVIGATION_POSITION,
                    FragmentPosition.TRANSLATION);
            replaceFragmentAndUpdateActionBar(restoredPosition);
        } else {
            replaceFragmentAndUpdateActionBar(FragmentPosition.TRANSLATION);
        }

        mBnvMain.setOnNavigationItemSelectedListener(this::navigateToItem);
        mBnvMain.setOnNavigationItemReselectedListener(item -> {
            // Do nothing
        });
    }

    /**
     * Method for navigation - performs replacing of fragments and ActionBar's layout
     *
     * @param menuItem which fragment to show
     * @return true - if menu has been processed successfully,<br>
     * false - otherwise
     */
    private boolean navigateToItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_translate:
                replaceFragmentAndUpdateActionBar(FragmentPosition.TRANSLATION);
                return true;
            case R.id.action_history:
                replaceFragmentAndUpdateActionBar(FragmentPosition.HISTORY);
                return true;
            case R.id.action_settings:
                replaceFragmentAndUpdateActionBar(FragmentPosition.SETTINGS);
                return true;
            default:
                return false;
        }
    }

    private void replaceFragmentAndUpdateActionBar(@FragmentPosition int newPosition) {
        replaceFragment(newPosition);
        updateActionBar(newPosition);
        mCurrentFragmentPosition = newPosition;
    }

    /**
     * Replaces fragments with calculated animation direction
     *
     * @param newPosition position of fragment to show
     */
    private void replaceFragment(@FragmentPosition int newPosition) {
        Fragment newFragment;
        switch (newPosition) {
            case FragmentPosition.TRANSLATION:
                newFragment = TranslationFragment.newInstance();
                break;
            case FragmentPosition.HISTORY:
                newFragment = HistoryFragment.newInstance();
                break;
            case FragmentPosition.SETTINGS:
                newFragment = SettingsFragment.newInstance();
                break;
            default:
                throw new IllegalArgumentException("Unacceptable fragment position: " + newPosition);
        }

        AnimationDirection animDirection = getAnimationDirection(newPosition, mCurrentFragmentPosition);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(animDirection.getFrom(), animDirection.getTo())
                .replace(R.id.flFragmentContainer, newFragment, newFragment.getClass().toString())
                .commit();
    }

    private void updateActionBar(@FragmentPosition int newPosition) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            switch (newPosition) {
                case FragmentPosition.TRANSLATION:
                    actionBar.setCustomView(R.layout.actionbar_translation);
                    break;
                case FragmentPosition.HISTORY:
                    actionBar.setCustomView(R.layout.actionbar_history);
                    break;
                case FragmentPosition.SETTINGS:
                    actionBar.setCustomView(R.layout.actionbar_settings);
                    break;
                default:
                    throw new IllegalArgumentException("Unacceptable fragment position: " + newPosition);
            }
        }
    }

    /**
     * Calculates animation direction for replacing fragments
     *
     * @return {@link AnimationDirection}
     */
    private AnimationDirection getAnimationDirection(@FragmentPosition int newPosition,
                                                     @FragmentPosition int currentPosition) {
        if (currentPosition < newPosition) {
            return AnimationDirection.FROM_RIGHT_TO_LEFT;
        } else {
            return AnimationDirection.FROM_LEFT_TO_RIGHT;
        }
    }

    /**
     * Enum to store possible animation directions with their resources for 'enter' and 'exit'
     */
    private enum AnimationDirection {
        FROM_LEFT_TO_RIGHT(R.anim.enter_from_left, R.anim.exit_to_right),
        FROM_RIGHT_TO_LEFT(R.anim.enter_from_right, R.anim.exit_to_left);

        private int mFrom;
        private int mTo;

        AnimationDirection(int from, int to) {
            mFrom = from;
            mTo = to;
        }

        public int getFrom() {
            return mFrom;
        }

        public int getTo() {
            return mTo;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mBnvMain.setSelectedItemId(
                savedInstanceState.getInt(BOTTOM_NAVIGATION_POSITION, FragmentPosition.TRANSLATION));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BOTTOM_NAVIGATION_POSITION, mCurrentFragmentPosition);
    }
}
