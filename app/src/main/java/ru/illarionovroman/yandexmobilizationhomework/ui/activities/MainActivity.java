package ru.illarionovroman.yandexmobilizationhomework.ui.activities;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

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

    private static final String FRAGMENT_TAG_TRANSLATION = "FRAGMENT_TAG_TRANSLATION";
    private static final String FRAGMENT_TAG_HISTORY = "FRAGMENT_TAG_HISTORY";
    private static final String FRAGMENT_TAG_SETTINGS = "FRAGMENT_TAG_SETTINGS";

    @IntDef({FragmentPosition.TRANSLATION, FragmentPosition.HISTORY, FragmentPosition.SETTINGS})
    @Retention(RetentionPolicy.SOURCE)
    private @interface FragmentPosition {
        int TRANSLATION = 0;
        int HISTORY = 1;
        int SETTINGS = 2;
    }

    @BindView(R.id.flFragmentContainer)
    FrameLayout mFlFragmentContainer;
    @BindView(R.id.bnvMain)
    BottomNavigationView mBnvMain;

    private @FragmentPosition int mPreviousFragmentPosition = FragmentPosition.TRANSLATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.flFragmentContainer, TranslationFragment.newInstance(), FRAGMENT_TAG_TRANSLATION)
                .commit();

        mBnvMain.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_translate:
                    replaceFragment(FragmentPosition.TRANSLATION);
                    return true;
                case R.id.action_favorites:
                    replaceFragment(FragmentPosition.HISTORY);
                    return true;
                case R.id.action_settings:
                    replaceFragment(FragmentPosition.SETTINGS);
                    return true;
                default:
                    return false;
            }
        });
    }

    private void replaceFragment(@FragmentPosition int position) {
        Fragment newFragment = getFragmentByPosition(position);
        String fragmentTag = getFragmentTagByPosition(position);
        AnimationDirection animDirection = getAnimationDirection(position, mPreviousFragmentPosition);

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(animDirection.getFrom(), animDirection.getTo())
                .replace(R.id.flFragmentContainer, newFragment, fragmentTag)
                .commit();

        mPreviousFragmentPosition = position;
    }

    private Fragment getFragmentByPosition(@FragmentPosition int position) {
        switch (position) {
            case FragmentPosition.TRANSLATION:
                return TranslationFragment.newInstance();
            case FragmentPosition.HISTORY:
                return HistoryFragment.newInstance();
            case FragmentPosition.SETTINGS:
                return SettingsFragment.newInstance();
            default:
                throw new IllegalArgumentException("Unacceptable fragment position: " + position);
        }
    }

    private String getFragmentTagByPosition(@FragmentPosition int position) {
        switch (position) {
            case FragmentPosition.TRANSLATION:
                return FRAGMENT_TAG_TRANSLATION;
            case FragmentPosition.HISTORY:
                return FRAGMENT_TAG_HISTORY;
            case FragmentPosition.SETTINGS:
                return FRAGMENT_TAG_SETTINGS;
            default:
                throw new IllegalArgumentException("Unacceptable fragment position: " + position);
        }
    }

    private AnimationDirection getAnimationDirection(@FragmentPosition int newPosition,
                                                     @FragmentPosition int oldPosition) {
        if (oldPosition < newPosition) {
            return AnimationDirection.FROM_RIGHT_TO_LEFT;
        } else {
            return AnimationDirection.FROM_LEFT_TO_RIGHT;
        }
    }

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BOTTOM_NAVIGATION_POSITION, mBnvMain.getSelectedItemId());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mBnvMain.setSelectedItemId(
                savedInstanceState.getInt(BOTTOM_NAVIGATION_POSITION, R.id.action_translate));
    }
}
