package ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import ru.illarionovroman.yandexmobilizationhomework.MobilizationApp;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.db.Contract;
import ru.illarionovroman.yandexmobilizationhomework.db.DBManager;
import ru.illarionovroman.yandexmobilizationhomework.model.HistoryItem;
import ru.illarionovroman.yandexmobilizationhomework.network.RestApi;
import ru.illarionovroman.yandexmobilizationhomework.network.response.ErrorResponse;
import ru.illarionovroman.yandexmobilizationhomework.network.response.ResponseErrorCodes;
import ru.illarionovroman.yandexmobilizationhomework.ui.activity.FullscreenActivity;
import ru.illarionovroman.yandexmobilizationhomework.ui.activity.LanguageSelectionActivity;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.BaseFragment;
import ru.illarionovroman.yandexmobilizationhomework.util.Languages;
import ru.illarionovroman.yandexmobilizationhomework.util.Prefs;
import ru.illarionovroman.yandexmobilizationhomework.util.Utils;
import timber.log.Timber;


public class TranslationFragment extends BaseFragment {

    public static final int REQUEST_CODE_LANGUAGE_FROM = 1;
    public static final int REQUEST_CODE_LANGUAGE_TO = 2;

    public static final String EXTRA_CURRENT_LANGUAGE = "ru.illarionovroman.yandexmobilizationhomework.ui.fragments.TranslationFragment.EXTRA_CURRENT_LANGUAGE";
    public static final String EXTRA_REQUEST_CODE = "ru.illarionovroman.yandexmobilizationhomework.ui.fragments.TranslationFragment.EXTRA_REQUEST_CODE";

    public static final String ARG_CURRENT_ITEM = "ARG_CURRENT_ITEM";

    private static final float ALPHA_BUTTONS_DISABLED = 0.3f;

    //region Views binding
    //----------------------------------------------------------------------------------------------
    /** User input views */
    @BindView(R.id.etWordInput)
    EditText mEtWordInput;
    @BindView(R.id.ivWordMic)
    ImageView mIvWorkMic;
    @BindView(R.id.ivWordSpeaker)
    ImageView mIvWordSpeaker;

    /** Translation views */
    @BindView(R.id.tvTranslation)
    TextView mTvTranslation;
    @BindView(R.id.llTranslationButtons)
    LinearLayout mLlTranslationButtons;
    @BindView(R.id.ivTranslationSpeaker)
    ImageView mIvTranslationSpeaker;
    @BindView(R.id.ivTranslationFavorite)
    ImageView mIvTranslationFavorite;
    @BindView(R.id.ivTranslationShare)
    ImageView mIvTranslationShare;
    @BindView(R.id.ivTranslationFullscreen)
    ImageView mIvTranslationFullscreen;

    /** Error views */
    @BindView(R.id.llTranslationError)
    LinearLayout mLlTranslationError;
    @BindView(R.id.tvTranslationErrorTitle)
    TextView mTvTranslationErrorTitle;
    @BindView(R.id.tvTranslationErrorText)
    TextView mTvTranslationErrorText;

    /** Progress bar */
    @BindView(R.id.pbLoading)
    ProgressBar mPbLoading;

    /** ActionBar views */
    @BindView(R.id.tvLanguageFrom)
    TextView mTvLanguageFrom;
    @BindView(R.id.tvLanguageTo)
    TextView mTvLanguageTo;
    @BindView(R.id.ivSwapLanguages)
    ImageView mIvSwapLanguages;
    //----------------------------------------------------------------------------------------------
    //endregion

    @Inject
    RestApi mRestApi;
    @Inject
    Gson mGson;
    @Inject
    Languages mLanguages;

    private CompositeDisposable mDisposables = new CompositeDisposable();
    private Disposable mLoaderDisposable;

    /** Always use {@link #setCurrentItem(HistoryItem)} to change this field */
    private HistoryItem mCurrentItem;

    /** Current HistoryItem updater */
    private HistoryItemContentObserver mDbObserver = new HistoryItemContentObserver(new Handler());

    public TranslationFragment() {
    }

    public static TranslationFragment newInstance() {
        TranslationFragment fragment = new TranslationFragment();
        return fragment;
    }

    //region Lifecycle methods
    //----------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inject network dependencies and languages
        MobilizationApp.get(getContext()).getAppComponent().inject(this);
        restoreInstanceState(savedInstanceState);
        initializeFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentItem != null) {
            outState.putParcelable(ARG_CURRENT_ITEM, mCurrentItem);
            Prefs.putLastUsedItemId(getContext(), mCurrentItem.getId());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDisposables != null) {
            mDisposables.clear();
        }
        if (mDbObserver != null) {
            mDbObserver.unregisterIdObserver();
        }
    }
    //----------------------------------------------------------------------------------------------
    //endregion

    //region Initialization methods
    //----------------------------------------------------------------------------------------------
    private void restoreInstanceState(@Nullable Bundle savedInstanceState) {
        HistoryItem item = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(ARG_CURRENT_ITEM)) {
            item = savedInstanceState.getParcelable(ARG_CURRENT_ITEM);
        } else {
            long itemId = Prefs.getLastUsedItemId(getContext());
            if (itemId != -1) {
                // Preload in UI thread to show ready data
                item = DBManager.getHistoryItemById(getContext(), itemId);
            }
        }
        handleUpdatedHistoryItem(item, true);
    }

    private void initializeFragment() {
        initializeActionBar();
        initializeInputWatcher();
    }

    private void initializeInputWatcher() {
        Observable<String> inputWatcher = TranslationHelper.createInputWatcher(mEtWordInput);
        Disposable inputDisposable = inputWatcher
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(inputText -> loadItemFromDatabaseOrNetwork(
                        new TranslationParams(inputText, getCurrentCodeLanguageFrom(), getCurrentCodeLanguageTo()),
                        false)
                );
        mDisposables.add(inputDisposable);
    }

    private void initializeActionBar() {
        setActionBarClickListeners();
    }

    private void setActionBarClickListeners() {
        mTvLanguageFrom.setOnClickListener(tvLangFromTextView -> {
            Intent intent = new Intent(getContext(), LanguageSelectionActivity.class);
            intent.putExtra(EXTRA_CURRENT_LANGUAGE, getCurrentCodeLanguageFrom());
            intent.putExtra(EXTRA_REQUEST_CODE, REQUEST_CODE_LANGUAGE_FROM);
            startActivityForResult(intent, REQUEST_CODE_LANGUAGE_FROM);
        });

        mTvLanguageTo.setOnClickListener(tvLangToTextView -> {
            Intent intent = new Intent(getContext(), LanguageSelectionActivity.class);
            intent.putExtra(EXTRA_CURRENT_LANGUAGE, getCurrentCodeLanguageTo());
            intent.putExtra(EXTRA_REQUEST_CODE, REQUEST_CODE_LANGUAGE_TO);
            startActivityForResult(intent, REQUEST_CODE_LANGUAGE_TO);
        });

        mIvSwapLanguages.setOnClickListener(swapLangsImageView -> {
            // Swap actionBar items(languages)
            String buf = mTvLanguageFrom.getText().toString();
            mTvLanguageFrom.setText(mTvLanguageTo.getText().toString());
            mTvLanguageTo.setText(buf);

            // Place translation into input and translate it
            String translation = mTvTranslation.getText().toString();
            mEtWordInput.setText(translation);
            if (!TextUtils.isEmpty(translation)) {
                loadItemFromDatabaseOrNetwork(
                        new TranslationParams(translation, getCurrentCodeLanguageFrom(), getCurrentCodeLanguageTo()),
                        true
                );
            }
        });
    }
    //----------------------------------------------------------------------------------------------
    //endregion

    //region Data loaders
    //----------------------------------------------------------------------------------------------
    /**
     * Perform HistoryItem loading in background. It will try to get data from DB,
     * but if there is no such data there - then try to get it from network.
     * In the end - show successfully retrieved data or localized error with reason.
     */
    private void loadItemFromDatabaseOrNetwork(final TranslationParams params, boolean updateInput) {
        showLoading();

        Single<HistoryItem> historyItemSingle = TranslationHelper.loadHistoryItem(getContext(), mRestApi, params);

        // Dispose previous loading if exists
        if (mLoaderDisposable != null) {
            mLoaderDisposable.dispose();
        }
        mLoaderDisposable = historyItemSingle.subscribe(
                (item) -> handleUpdatedHistoryItem(item, updateInput),
                this::handleTranslationError
        );
        mDisposables.add(mLoaderDisposable);
    }

    /**
     * Load item with given id from DB in background, then pass it to UI
     * @param itemId Id of item to load from DB
     */
    private void loadAndSetItemFavoriteState(long itemId) {
        Single.just(itemId)
                .map(id -> {
                    HistoryItem item = DBManager.getHistoryItemById(getContext(), id);
                    return item;
                })
                .subscribeOn(Schedulers.trampoline())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((item) -> handleUpdatedHistoryItem(item, false));
    }

    public void showSelectedItem(HistoryItem item) {
        handleUpdatedHistoryItem(item, true);
        // Update item date in background
        new Thread(() -> {
            item.setDate(Utils.getCurrentFormattedDateUtc());
            DBManager.updateHistoryItemWithId(getContext(), item);
        }).start();
    }
    //----------------------------------------------------------------------------------------------
    //endregion

    //region Result data/error handlers
    //----------------------------------------------------------------------------------------------
    /**
     * Update current item, toolbar items and show successful screen state
     * @param item {@link HistoryItem} to show
     */
    private void handleUpdatedHistoryItem(@Nullable HistoryItem item, boolean updateInput) {
        if (item != null) {
            setCurrentItem(item);
            fillAndShowTranslationViews(item, updateInput);
        } else {
            Timber.d("Item is null");
        }
    }

    /**
     * Replace current item with new one and update observer
     * @param item New {@link HistoryItem} to replace the old one
     */
    private void setCurrentItem(HistoryItem item) {
        if (item != null) {
            mCurrentItem = item;
            mDbObserver.replaceItemObserver(item.getId());
        }
    }

    private void fillAndShowTranslationViews(HistoryItem item, boolean updateInput) {
        fillTranslationViews(item, updateInput);
        showTranslationViews();
    }

    /**
     * Get data from item and put it in corresponding views
     * @param item {@link HistoryItem} to get data from
     */
    private void fillTranslationViews(HistoryItem item, boolean updateInput) {
        // Set toolbar languages
        mTvLanguageFrom.setText(mLanguages.getLangNameByCode(item.getLanguageCodeFrom()));
        mTvLanguageTo.setText(mLanguages.getLangNameByCode(item.getLanguageCodeTo()));

        if (updateInput) {
            // Set user input field. If it was focused in the moment of update - move cursor to the end
            mEtWordInput.setText(item.getWord());
            if (mEtWordInput.hasFocus()) {
                mEtWordInput.setSelection(mEtWordInput.getText().length());
            }
        }

        // Set translation text and favorite state button
        mTvTranslation.setText(item.getTranslation());
        mIvTranslationFavorite.setActivated(item.getIsFavorite());
    }

    /**
     * Method for processing all kinds of errors that may appear during data loading.
     * It recognizes type of error and shows corresponding localized text to UI
     * @param error Throwable to analyze
     */
    private void handleTranslationError(Throwable error) {
        showTranslationViews();

        if (error instanceof HttpException) {
            try {
                // Get inner Json error
                ResponseBody errorBody = ((HttpException) error).response().errorBody();
                ErrorResponse errorResponse = mGson.fromJson(errorBody.string(), ErrorResponse.class);
                // Use code to show localized error
                localizeAndShowErrorByCode(errorResponse.getCode(), errorResponse.getMessage());
            } catch (IOException ex) {
                showError(getString(R.string.error_title_unknown),
                        getString(R.string.error_text_unknown));
                Timber.e(error, "Couldn't convert error response! Message: " + error.getMessage());
            }
        } else if (error instanceof UnknownHostException) {
            showError(getString(R.string.error_title_connection),
                    getString(R.string.error_text_connection));
            Timber.d(error, "Unknown host error. Message: " + error.getMessage());
        } else {
            showError(getString(R.string.error_title_simple),
                    getString(R.string.error_text_unknown));
            Timber.e(error, "Not HTTP translation error! Message: " + error.getMessage());
        }

        Timber.e(error);
    }

    /**
     * Method for showing a localized error message by its code
     */
    private void localizeAndShowErrorByCode(@ResponseErrorCodes int errorCode,
                                            @Nullable String errorMessage) {
        switch (errorCode) {
            case ResponseErrorCodes.API_KEY_BLOCKED:
                showError(getString(R.string.error_title_authorization),
                        getString(R.string.error_text_api_key_blocked));
                break;
            case ResponseErrorCodes.API_KEY_INVALID:
                showError(getString(R.string.error_title_authorization),
                        getString(R.string.error_text_api_key_invalid));
                break;
            case ResponseErrorCodes.DAY_LIMIT_EXCEED:
                showError(getString(R.string.error_title_translation),
                        getString(R.string.error_text_day_limit_exceed));
                break;
            case ResponseErrorCodes.TEXT_SIZE_EXCEED:
                showError(getString(R.string.error_title_translation),
                        getString(R.string.error_text_text_size_exceed));
                break;
            case ResponseErrorCodes.TEXT_UNTRANSLATABLE:
                showError(getString(R.string.error_title_translation),
                        getString(R.string.error_text_untranslatable));
                break;
            case ResponseErrorCodes.TRANSLATION_DIRECTION_UNSUPPORTED:
                showError(getString(R.string.error_title_translation),
                        getString(R.string.error_text_direction_unsupported));
                break;
            default:
                showError(getString(R.string.error_title_unknown),
                        getString(R.string.error_text_unknown));
                break;
        }
        Timber.d("localizeAndShowErrorByCode: errorCode=" + errorCode + ", msg=" + errorMessage);
    }
    //----------------------------------------------------------------------------------------------
    //endregion

    //region Button clicks
    //----------------------------------------------------------------------------------------------
    @OnClick(R.id.ivWordMic)
    void captureTextFromVoice() {
        Toast.makeText(getContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.ivWordSpeaker)
    void sayWordInputAloud() {
        Toast.makeText(getContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.ivWordClean)
    void cleanWordInput() {
        mEtWordInput.setText("");
    }

    @OnClick(R.id.ivTranslationSpeaker)
    void sayTranslationAloud() {
        Toast.makeText(getContext(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.ivTranslationFavorite)
    void toggleTranslationFavoriteState() {
        if (TextUtils.isEmpty(mTvTranslation.getText().toString())) {
            return;
        }
        if (mCurrentItem != null) {
            boolean activated = mIvTranslationFavorite.isActivated();
            if (activated) {
                mCurrentItem.setIsFavorite(false);
                mIvTranslationFavorite.setActivated(false);
            } else {
                mCurrentItem.setIsFavorite(true);
                mIvTranslationFavorite.setActivated(true);
            }
            int updatedCount = DBManager.updateHistoryItemWithId(getContext(), mCurrentItem);
            if (updatedCount == 0) {
                DBManager.addHistoryItem(getContext(), mCurrentItem);
            }
        }
    }

    @OnClick(R.id.ivTranslationShare)
    void shareTranslation() {
        String translation = mTvTranslation.getText().toString();
        if (TextUtils.isEmpty(translation)) {
            return;
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, translation);

        Intent chooser = Intent.createChooser(shareIntent,
                getString(R.string.translation_share_intent_title));

        if (shareIntent.resolveActivity(getContext().getPackageManager()) != null) {
            getContext().startActivity(chooser);
        }
    }

    @OnClick(R.id.ivTranslationFullscreen)
    void gotoFullScreenActivity() {
        if (TextUtils.isEmpty(mTvTranslation.getText().toString())) {
            return;
        }
        Intent intent = new Intent(getContext(), FullscreenActivity.class);
        intent.putExtra(FullscreenActivity.EXTRA_FULLSCREEN_TEXT, mTvTranslation.getText());
        startActivity(intent);
    }

    @OnClick(R.id.btnRetry)
    void retry() {
        loadItemFromDatabaseOrNetwork(getCurrentTranslationParams(), false);
    }
    //----------------------------------------------------------------------------------------------
    //endregion

    /**
     * Method for processing language selection
     * @param data Intent, which contains code of selected language
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String resultLangCode = data.getStringExtra(LanguageSelectionActivity.EXTRA_RESULT);
            String selectedLangName = mLanguages.getLangNameByCode(resultLangCode);
            if (requestCode == REQUEST_CODE_LANGUAGE_FROM) {
                mTvLanguageFrom.setText(selectedLangName);
            } else if (requestCode == REQUEST_CODE_LANGUAGE_TO) {
                mTvLanguageTo.setText(selectedLangName);
            }
            loadItemFromDatabaseOrNetwork(getCurrentTranslationParams(), true);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Helper method to get the language code FROM which to translate
     */
    private String getCurrentCodeLanguageFrom() {
        if (mTvLanguageFrom != null) {
            String nameLangFrom = mTvLanguageFrom.getText().toString();
            return mLanguages.getLangCodeByName(nameLangFrom);
        } else {
            return "";
        }
    }

    /**
     * Helper method to get the language code TO which to translate
     */
    private String getCurrentCodeLanguageTo() {
        if (mTvLanguageTo != null) {
            String nameLangTo = mTvLanguageTo.getText().toString();
            return mLanguages.getLangCodeByName(nameLangTo);
        } else {
            return "";
        }
    }

    private TranslationParams getCurrentTranslationParams() {
        return new TranslationParams(mEtWordInput.getText().toString(),
                getCurrentCodeLanguageFrom(),
                getCurrentCodeLanguageTo());
    }

    //region View state change methods
    //----------------------------------------------------------------------------------------------
    private void showLoading() {
        mPbLoading.setVisibility(View.VISIBLE);
        mTvTranslation.setVisibility(View.GONE);
        setTranslationButtonsEnabledState(false);
        mLlTranslationError.setVisibility(View.GONE);
    }

    private void showTranslationViews() {
        mPbLoading.setVisibility(View.GONE);
        mTvTranslation.setVisibility(View.VISIBLE);
        mLlTranslationButtons.setVisibility(View.VISIBLE);
        setTranslationButtonsEnabledState(true);
        mLlTranslationError.setVisibility(View.GONE);
    }

    private void setTranslationButtonsEnabledState(boolean enabled) {
        float alpha = enabled ? 1f : ALPHA_BUTTONS_DISABLED;

        mIvTranslationSpeaker.setEnabled(enabled);
        mIvTranslationSpeaker.setAlpha(alpha);
        mIvTranslationFavorite.setEnabled(enabled);
        mIvTranslationFavorite.setAlpha(alpha);
        mIvTranslationShare.setEnabled(enabled);
        mIvTranslationShare.setAlpha(alpha);
        mIvTranslationFullscreen.setEnabled(enabled);
        mIvTranslationFullscreen.setAlpha(alpha);
    }

    private void showError(String errorTitle, String errorText) {
        mPbLoading.setVisibility(View.GONE);
        mTvTranslation.setVisibility(View.GONE);
        mLlTranslationButtons.setVisibility(View.GONE);

        mTvTranslationErrorTitle.setText(errorTitle);
        mTvTranslationErrorText.setText(errorText);
        mLlTranslationError.setVisibility(View.VISIBLE);
    }
    //----------------------------------------------------------------------------------------------
    //endregion

    /**
     * ContentObserver to watch for specific HistoryItem changes and display it
     */
    private class HistoryItemContentObserver extends ContentObserver {
        HistoryItemContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            long id = -1;
            try {
                id = ContentUris.parseId(uri);
            } catch (NumberFormatException ignore) {
            }
            if (id != -1 && mCurrentItem != null && id == mCurrentItem.getId()) {
                loadAndSetItemFavoriteState(id);
            }
        }

        /**
         * Watch for changes of item with specific id
         * @param itemId Id of item to watch for
         */
        private void registerIdObserver(long itemId) {
            Uri idUri = Contract.HistoryEntry.CONTENT_URI_HISTORY.buildUpon()
                    .appendPath(String.valueOf(itemId))
                    .build();
            getActivity().getContentResolver().registerContentObserver(idUri, false, this);
        }

        private void unregisterIdObserver() {
            getActivity().getContentResolver().unregisterContentObserver(this);
        }

        /**
         * Update Id of the item to observe
         * @param itemId Id of new item to observe
         */
        private void replaceItemObserver(long itemId) {
            unregisterIdObserver();
            registerIdObserver(itemId);
        }
    }
}
