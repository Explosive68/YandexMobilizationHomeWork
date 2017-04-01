package ru.illarionovroman.yandexmobilizationhomework.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.HttpException;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.models.HistoryItem;
import ru.illarionovroman.yandexmobilizationhomework.network.ApiManager;
import ru.illarionovroman.yandexmobilizationhomework.network.responses.ErrorResponse;
import ru.illarionovroman.yandexmobilizationhomework.network.responses.ResponseErrorCodes;
import ru.illarionovroman.yandexmobilizationhomework.network.responses.TranslationResponse;
import ru.illarionovroman.yandexmobilizationhomework.ui.activities.LanguageSelectionActivity;
import ru.illarionovroman.yandexmobilizationhomework.utils.Utils;
import timber.log.Timber;


public class TranslationFragment extends Fragment {

    public static final int REQUEST_CODE_LANGUAGE_FROM = 1;
    public static final int REQUEST_CODE_LANGUAGE_TO = 2;
    public static final String EXTRA_CURRENT_LANGUAGE = "ru.illarionovroman.yandexmobilizationhomework.ui.fragments.TranslationFragment.EXTRA_CURRENT_LANGUAGE";
    public static final String EXTRA_REQUEST_CODE = "ru.illarionovroman.yandexmobilizationhomework.ui.fragments.TranslationFragment.EXTRA_REQUEST_CODE";

    @BindView(R.id.etWordInput)
    EditText mEtWordInput;
    @BindView(R.id.tvTranslation)
    TextView mTvTranslation;
    @BindView(R.id.llTranslationButtons)
    LinearLayout mLlTranslationButtons;
    @BindView(R.id.tvTranslationError)
    TextView mTvTranslationError;
    @BindView(R.id.pbLoading)
    ProgressBar mPbLoading;

    @BindView(R.id.ivTranslationFullscreen)
    ImageView mIvTranslationFullscreen;

    private TextView mTvLanguageFrom;
    private TextView mTvLanguageTo;
    private ImageView mIvSwapLanguages;

    private CompositeDisposable mDisposables;

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
        ButterKnife.bind(this, view);

        mTvLanguageFrom = ButterKnife.findById(getActivity(), R.id.tvLanguageFrom);
        mTvLanguageTo = ButterKnife.findById(getActivity(), R.id.tvLanguageTo);
        mIvSwapLanguages = ButterKnife.findById(getActivity(), R.id.ivSwapLanguages);
        setLanguageSelectionClickListeners();

        mDisposables = new CompositeDisposable();
        mDisposables.add(createDisposableInputWatcher());
        return view;
    }

    private void setLanguageSelectionClickListeners() {
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

            // Insert translation to input
            String translation = mTvTranslation.getText().toString();
            if (!TextUtils.isEmpty(translation)) {
                mEtWordInput.setText(translation);
            }
        });
    }

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
            prepareAndProcessTranslationRequest();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @NonNull
    private Disposable createDisposableInputWatcher() {
        return RxTextView.textChanges(mEtWordInput)
                .debounce(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(textToTranslate -> {
                    prepareAndProcessTranslationRequest();
                });
    }

    @OnClick(R.id.btnRetry)
    public void retry() {
        prepareAndProcessTranslationRequest();
    }

    private void prepareAndProcessTranslationRequest() {
        String inputText = mEtWordInput.getText().toString().trim();
        if (inputText.length() == 0) {
            return;
        }

        showLoading();

        String langFromTo = buildTranslationLangParam();
        Observable<TranslationResponse> translationResponseObservable = ApiManager
                .getApiInterfaceInstance().getTranslation(inputText, langFromTo, null);

        mDisposables.add(translationResponseObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleTranslateResponse, this::handleTranslateError)
        );
    }

    private String buildTranslationLangParam() {
        String codeLangFrom = getCurrentCodeLanguageFrom();
        String codeLangTo = getCurrentCodeLanguageTo();
        return getString(R.string.translate_query_param_language_from_to,
                codeLangFrom, codeLangTo);
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

    private void handleTranslateResponse(TranslationResponse response) {
        hideLoading();

        StringBuilder translationBuilder = new StringBuilder();
        for (int i = 0; i < response.getTranslations().size(); i++) {
            translationBuilder.append(response.getTranslations().get(i));
        }
        mTvTranslation.setText(translationBuilder.toString());

        HistoryItem item = new HistoryItem(
                mEtWordInput.getText().toString(),
                translationBuilder.toString(),
                getCurrentCodeLanguageFrom(),
                getCurrentCodeLanguageTo()
        );
        long id = Utils.DB.addHistoryItem(getContext(), item);

        Toast.makeText(getContext(), "id = " + id, Toast.LENGTH_SHORT).show();
    }

    private void handleTranslateError(Throwable error) {
        hideLoading();

        if (error instanceof HttpException) {
            try {
                Converter<ResponseBody, ErrorResponse> errorConverter = ApiManager.getRetrofitInstance()
                        .responseBodyConverter(ErrorResponse.class, new Annotation[0]);
                ErrorResponse errorResponse = errorConverter
                        .convert(((HttpException) error).response().errorBody());
                handleError(errorResponse.getCode(), errorResponse.getErrorMessage());
            } catch (IOException ex) {
                showError("Unknown error: " + error.getMessage());
                Timber.e(error, "Couldn't convert error response!");
            }
        } else {
            showError("Unknown error: " + error.getMessage());
        }

        error.printStackTrace();
    }

    private void handleError(@ResponseErrorCodes int errorCode, @Nullable String errorMessage) {
        hideLoading();

        // TODO: always show localized error text
        if (!TextUtils.isEmpty(errorMessage)) {
            showError(errorMessage);
            return;
        }

        switch (errorCode) {
            case ResponseErrorCodes.API_KEY_BLOCKED:
                showError("API_KEY_BLOCKED");
                break;
            case ResponseErrorCodes.API_KEY_INVALID:
                showError("API_KEY_INVALID");
                break;
            case ResponseErrorCodes.DAY_LIMIT_EXCEED:
                showError("DAY_LIMIT_EXCEED");
                break;
            case ResponseErrorCodes.TEXT_SIZE_EXCEED:
                showError("TEXT_SIZE_EXCEED");
                break;
            case ResponseErrorCodes.TEXT_UNTRANSLATABLE:
                showError("TEXT_UNTRANSLATABLE");
                break;
            case ResponseErrorCodes.TRANSLATION_DIRECTION_UNSUPPORTED:
                showError("TRANSLATION_DIRECTION_UNSUPPORTED");
                break;
            default:
                showError("Unknown error occurred while loading translation data");
                break;
        }
    }

    private void showLoading() {
        mPbLoading.setVisibility(View.VISIBLE);
        mTvTranslation.setVisibility(View.GONE);
        mLlTranslationButtons.setVisibility(View.GONE);
        mTvTranslationError.setVisibility(View.GONE);
    }

    private void hideLoading() {
        mPbLoading.setVisibility(View.GONE);
        mTvTranslation.setVisibility(View.VISIBLE);
        mLlTranslationButtons.setVisibility(View.VISIBLE);
        mTvTranslationError.setVisibility(View.GONE);
    }

    private void showError(String errorText) {
        mPbLoading.setVisibility(View.GONE);
        mTvTranslation.setVisibility(View.GONE);
        mLlTranslationButtons.setVisibility(View.GONE);

        mTvTranslationError.setText(errorText);
        mTvTranslationError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposables != null) {
            mDisposables.clear();
        }
    }
}
