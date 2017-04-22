package ru.illarionovroman.yandexmobilizationhomework.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.illarionovroman.yandexmobilizationhomework.R;


/**
 * Activity with About content.
 */
public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.ivLogo)
    ImageView mIvLogo;

    private Toast mToast;
    private int mClickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initializeActivity();
    }

    private void initializeActivity() {
        initializeActionBar();
        setLogoClickListener();
    }

    private void initializeActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAbout);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.about_title);
        }
    }

    private void setLogoClickListener() {
        int secretCount = 10;
        mIvLogo.setOnClickListener(view -> {
            mClickCount++;
            if (2 < mClickCount && mClickCount < secretCount) {
                showToast(String.valueOf(secretCount - mClickCount) + " more to go...");
            } else if (mClickCount >= secretCount) {
                showToast(getString(R.string.easter_egg));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Cancel currently displayed toast before showing the new one
     */
    private void showToast(String textToShow) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, textToShow, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
