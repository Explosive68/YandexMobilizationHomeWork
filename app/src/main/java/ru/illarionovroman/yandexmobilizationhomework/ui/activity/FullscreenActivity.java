package ru.illarionovroman.yandexmobilizationhomework.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import ru.illarionovroman.yandexmobilizationhomework.R;

/**
 * Fullscreen activity with single big text and one button to exit
 */
public class FullscreenActivity extends AppCompatActivity {

    public static final String EXTRA_FULLSCREEN_TEXT = "ru.illarionovroman.yandexmobilizationhomework.ui.activity.FullscreenActivity.EXTRA_FULLSCREEN_TEXT";

    private TextView mTvContentView;
    private ImageView mIvCloseFullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_fullscreen);

        mTvContentView = (TextView) findViewById(R.id.tvFullscreenText);
        mIvCloseFullscreen = (ImageView) findViewById(R.id.ivCloseFullscreen);

        loadTextFromIntent(mTvContentView);
        mIvCloseFullscreen.setOnClickListener(view -> {
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupFullscreenMode();
    }

    private void setupFullscreenMode() {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 19) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            mTvContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    private void loadTextFromIntent(@NonNull TextView textView) {
        String fullscreenText = "";
        Intent incomingIntent = getIntent();
        if (incomingIntent != null) {
            fullscreenText = incomingIntent.getStringExtra(EXTRA_FULLSCREEN_TEXT);
        }
        textView.setText(fullscreenText);
    }
}
