package ru.illarionovroman.yandexmobilizationhomework.ui.fragments;

import android.os.Bundle;
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
import ru.illarionovroman.yandexmobilizationhomework.network.ApiManager;
import ru.illarionovroman.yandexmobilizationhomework.network.responses.ErrorResponse;
import ru.illarionovroman.yandexmobilizationhomework.network.responses.ResponseErrorCodes;
import ru.illarionovroman.yandexmobilizationhomework.network.responses.TranslationResponse;
import timber.log.Timber;


public class TranslationFragment extends Fragment {

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

    private CompositeDisposable mDisposables;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation, container, false);
        ButterKnife.bind(this, view);

        mDisposables = new CompositeDisposable();

        Disposable inputWatcher = RxTextView.textChanges(mEtWordInput)
                .debounce(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    prepareAndProcessTranslationRequest();
                });
        mDisposables.add(inputWatcher);

        return view;
    }

    @OnClick(R.id.ivTranslationFullscreen)
    public void submit() {
        prepareAndProcessTranslationRequest();
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

        Observable<TranslationResponse> translationResponseObservable =
                ApiManager.getApiInterfaceInstance().getTranslation(inputText, "en-ru", null);

        mDisposables.add(translationResponseObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleRxResponse, this::handleRxError)
        );
    }

    private void handleRxResponse(TranslationResponse response) {
        hideLoading();

        StringBuilder translationBuilder = new StringBuilder();
        for (int i = 0; i < response.getTranslations().size(); i++) {
            translationBuilder.append(i+1);
            translationBuilder.append(") ");
            translationBuilder.append(response.getTranslations().get(i));
            translationBuilder.append("\n");
        }

        mTvTranslation.setText(translationBuilder.toString());
    }

    private void handleRxError(Throwable error) {
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
