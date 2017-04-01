package ru.illarionovroman.yandexmobilizationhomework.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.adapters.LanguageSelectionAdapter;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragments.TranslationFragment;


public class LanguageSelectionActivity extends AppCompatActivity
        implements LanguageSelectionAdapter.OnListItemClickListener {

    public static final String EXTRA_RESULT = "ru.illarionovroman.yandexmobilizationhomework.ui.activities.LanguageSelectionActivity.EXTRA_RESULT";

    @BindView(R.id.rvLanguageSelection)
    RecyclerView mRvLanguageSelection;

    private LanguageSelectionAdapter mAdapter;

    private String mCurrentLanguageCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);
        ButterKnife.bind(this);
        initializeActivity();
    }

    private void initializeActivity() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        loadIntentData(actionBar);

        mAdapter = new LanguageSelectionAdapter(this, mCurrentLanguageCode, this);
        initializeRecyclerView(mAdapter);
    }

    private void loadIntentData(ActionBar actionBar) {
        Intent incomingIntent = getIntent();
        if (incomingIntent != null) {
            mCurrentLanguageCode = incomingIntent
                    .getStringExtra(TranslationFragment.EXTRA_CURRENT_LANGUAGE);
            int requestCode = incomingIntent.getIntExtra(TranslationFragment.EXTRA_REQUEST_CODE,
                    TranslationFragment.REQUEST_CODE_LANGUAGE_FROM);
            if (requestCode == TranslationFragment.REQUEST_CODE_LANGUAGE_FROM) {
                actionBar.setTitle(R.string.language_selection_title_from);
            } else if (requestCode == TranslationFragment.REQUEST_CODE_LANGUAGE_TO) {
                actionBar.setTitle(R.string.language_selection_title_to);
            }
        }
    }

    private void initializeRecyclerView(LanguageSelectionAdapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvLanguageSelection.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                layoutManager.getOrientation());
        mRvLanguageSelection.addItemDecoration(dividerItemDecoration);
        mRvLanguageSelection.setAdapter(adapter);
    }

    @Override
    public void onListItemClick(String langCode) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_RESULT, langCode);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
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
}
