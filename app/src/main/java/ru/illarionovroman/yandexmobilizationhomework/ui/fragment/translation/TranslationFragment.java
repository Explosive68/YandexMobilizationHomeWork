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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.HttpException;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.db.Contract;
import ru.illarionovroman.yandexmobilizationhomework.db.DBManager;
import ru.illarionovroman.yandexmobilizationhomework.model.HistoryItem;
import ru.illarionovroman.yandexmobilizationhomework.network.ApiManager;
import ru.illarionovroman.yandexmobilizationhomework.network.response.ErrorResponse;
import ru.illarionovroman.yandexmobilizationhomework.network.response.ResponseErrorCodes;
import ru.illarionovroman.yandexmobilizationhomework.ui.activity.FullscreenActivity;
import ru.illarionovroman.yandexmobilizationhomework.ui.activity.LanguageSelectionActivity;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.BaseFragment;
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


    @BindView(R.id.etWordInput)
    EditText mEtWordInput;
    @BindView(R.id.tvTranslation)
    TextView mTvTranslation;
    @BindView(R.id.llTranslationButtons)
    LinearLayout mLlTranslationButtons;
    @BindView(R.id.llTranslationError)
    LinearLayout mLlTranslationError;
    @BindView(R.id.tvTranslationErrorTitle)
    TextView mTvTranslationErrorTitle;
    @BindView(R.id.tvTranslationErrorText)
    TextView mTvTranslationErrorText;
    @BindView(R.id.pbLoading)
    ProgressBar mPbLoading;

    @BindView(R.id.ivTranslationSpeaker)
    ImageView mIvTranslationSpeaker;
    @BindView(R.id.ivTranslationFavorite)
    ImageView mIvTranslationFavorite;
    @BindView(R.id.ivTranslationShare)
    ImageView mIvTranslationShare;
    @BindView(R.id.ivTranslationFullscreen)
    ImageView mIvTranslationFullscreen;

    @BindView(R.id.tvLanguageFrom)
    TextView mTvLanguageFrom;
    @BindView(R.id.tvLanguageTo)
    TextView mTvLanguageTo;
    @BindView(R.id.ivSwapLanguages)
    ImageView mIvSwapLanguages;

    private CompositeDisposable mDisposables;

    /**
     * Always use {@link #setCurrentItem(HistoryItem)} to change this field
     */
    private HistoryItem mCurrentItem;

    /**
     * If some changes of currently displayed item have been registered - update it
     */
    private ContentObserver mDbObserver = new ContentObserver(new Handler()) {
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
                loadAndShowItemFromDB(id);
            }
        }
    };

    public TranslationFragment() {
    }

    public static TranslationFragment newInstance() {
        TranslationFragment fragment = new TranslationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        restoreInstanceState(savedInstanceState);
        initializeFragment();
    }

    private void restoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ARG_CURRENT_ITEM)) {
                HistoryItem item = savedInstanceState.getParcelable(ARG_CURRENT_ITEM);
                setCurrentItem(item);
                fillAndShowTranslationViews(item);
            }
        } else {
            long itemId = Prefs.getLastUsedItemId(getContext());
            if (itemId != -1) {
                // Preload in UI thread to show ready data
                HistoryItem item = DBManager.getHistoryItemById(getContext(), itemId);
                handleTranslationSuccess(item);
            }
        }
    }

    private void initializeFragment() {
        initializeActionBar();
        initializeInputWatcher();
    }

    private void initializeInputWatcher() {
        mDisposables = new CompositeDisposable();
        Observable<String> inputWatcher = TranslationHelper.createInputWatcher(
                mEtWordInput, mCurrentItem);
        Disposable inputDisposable = inputWatcher.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(inputText -> loadItemFromDatabaseOrNetwork(inputText,
                        getCurrentCodeLanguageFrom(), getCurrentCodeLanguageTo()));
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
            // Swap actionBar items
            String buf = mTvLanguageFrom.getText().toString();
            mTvLanguageFrom.setText(mTvLanguageTo.getText().toString());
            mTvLanguageTo.setText(buf);

            // Translate word from translation
            String translation = mTvTranslation.getText().toString();
            if (!TextUtils.isEmpty(translation)) {
                loadItemFromDatabaseOrNetwork(translation,
                        getCurrentCodeLanguageFrom(), getCurrentCodeLanguageTo());
            }
        });
    }

    /**
     * We use reactivex.Single here,
     * since there could be either just one resulting item or failure
     */
    private void loadItemFromDatabaseOrNetwork(String wordToTranslate, String langCodeFrom,
                                               String langCodeTo) {
        showLoading();

        Single<HistoryItem> historyItemSingle = TranslationHelper.loadHistoryItem(getContext(),
                wordToTranslate, langCodeFrom, langCodeTo);

        mDisposables.add(historyItemSingle
                .subscribe(this::handleTranslationSuccess, this::handleTranslationError));
    }

    /**
     * Load item with given id from DB in background, then pass it to UI
     * @param itemId Id of item to load from DB
     */
    public void loadAndShowItemFromDB(long itemId) {
        Single.just(itemId)
                .map(id -> {
                    HistoryItem item = DBManager.getHistoryItemById(getContext(), id);
                    return item;
                })
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleTranslationSuccess);
    }

    /**
     * Update current item, toolbar items and show successful screen state
     * @param item {@link HistoryItem} to show
     */
    private void handleTranslationSuccess(HistoryItem item) {
        if (item != null) {
            setCurrentItem(item);
            fillAndShowTranslationViews(item);
        } else {
            Timber.wtf("Item can't be null in successful case");
        }
    }

    /**
     * Replace current item with new one and update observer
     * @param item New {@link HistoryItem} to replace the old one
     */
    private void setCurrentItem(HistoryItem item) {
        if (item != null) {
            mCurrentItem = item;
            refreshItemObserver(item.getId());
        }
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
                // Get real error code
                Converter<ResponseBody, ErrorResponse> errorConverter = ApiManager
                        .getRetrofitInstance()
                        .responseBodyConverter(ErrorResponse.class, new Annotation[0]);
                ResponseBody errorBody = ((HttpException) error).response().errorBody();
                ErrorResponse errorResponse = errorConverter.convert(errorBody);
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

        error.printStackTrace();
    }

    /**
     * Watch for changes of item with specific id
     * @param itemId Id of item to watch for
     */
    private void registerIdObserver(long itemId) {
        Uri idUri = Contract.HistoryEntry.CONTENT_URI_HISTORY.buildUpon()
                .appendPath(String.valueOf(itemId))
                .build();
        getActivity().getContentResolver().registerContentObserver(idUri, false,
                mDbObserver);
    }

    private void unregisterIdObserver() {
        if (mDbObserver != null) {
            getActivity().getContentResolver().unregisterContentObserver(mDbObserver);
        }
    }

    /**
     * Update Id of the item to observe
     * @param itemId
     */
    private void refreshItemObserver(long itemId) {
        unregisterIdObserver();
        registerIdObserver(itemId);
    }

    private void fillAndShowTranslationViews(HistoryItem item) {
        fillTranslationViews(item);
        showTranslationViews();
    }

    private void fillTranslationViews(HistoryItem item) {
        mTvLanguageFrom.setText(Utils.getLangNameByCode(getContext(), item.getLanguageCodeFrom()));
        mTvLanguageTo.setText(Utils.getLangNameByCode(getContext(), item.getLanguageCodeTo()));

        mEtWordInput.setText(item.getWord());
        mTvTranslation.setText(item.getTranslation());
        mIvTranslationFavorite.setActivated(item.getIsFavorite());
    }

    @OnClick(R.id.btnRetry)
    void retry() {
        loadItemFromDatabaseOrNetwork(mEtWordInput.getText().toString(),
                getCurrentCodeLanguageFrom(), getCurrentCodeLanguageTo());
    }

    @OnClick(R.id.ivWordClean)
    void cleanWordInput() {
        mEtWordInput.setText("");
    }

    @OnClick(R.id.ivTranslationFavorite)
    void toggleTranslationFavorite() {
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
    void showFullScreen() {
        startActivity(new Intent(getContext(), FullscreenActivity.class));
    }

    /**
     * Method for showing a localized error message by its code
     */
    private void localizeAndShowErrorByCode(@ResponseErrorCodes int errorCode,
                                            @Nullable String errorMessage) {
        showTranslationViews();

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

    /**
     * Method for processing language selection
     * @param data Intent, which contains code of selected language
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String resultLangCode = data.getStringExtra(LanguageSelectionActivity.EXTRA_RESULT);
            String selectedLangName = Utils.getLangNameByCode(getContext(), resultLangCode);
            if (requestCode == REQUEST_CODE_LANGUAGE_FROM) {
                mTvLanguageFrom.setText(selectedLangName);
            } else if (requestCode == REQUEST_CODE_LANGUAGE_TO) {
                mTvLanguageTo.setText(selectedLangName);
            }
            loadItemFromDatabaseOrNetwork(mEtWordInput.getText().toString(),
                    getCurrentCodeLanguageFrom(), getCurrentCodeLanguageTo());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String getCurrentCodeLanguageFrom() {
        if (mTvLanguageFrom != null) {
            String nameLangFrom = mTvLanguageFrom.getText().toString();
            return Utils.getLangCodeByName(getContext(), nameLangFrom);
        } else {
            return "";
        }
    }

    private String getCurrentCodeLanguageTo() {
        if (mTvLanguageTo != null) {
            String nameLangTo = mTvLanguageTo.getText().toString();
            return Utils.getLangCodeByName(getContext(), nameLangTo);
        } else {
            return "";
        }
    }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDisposables != null) {
            mDisposables.clear();
        }
        unregisterIdObserver();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentItem != null) {
            outState.putParcelable(ARG_CURRENT_ITEM, mCurrentItem);
            Prefs.putLastUsedItemId(getContext(), mCurrentItem.getId());
        }
    }
}
