package ru.illarionovroman.yandexmobilizationhomework.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.illarionovroman.yandexmobilizationhomework.R;
import ru.illarionovroman.yandexmobilizationhomework.network.ApiModule;
import ru.illarionovroman.yandexmobilizationhomework.network.responses.ResponseCodes;
import ru.illarionovroman.yandexmobilizationhomework.network.responses.TranslationResponse;


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

    private CompositeDisposable mCompositeDisposable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translation, container, false);
        ButterKnife.bind(this, view);

        mIvTranslationFullscreen.setOnClickListener(v -> {
            String inputText = mEtWordInput.getText().toString().trim();
            if (inputText.length() == 0) {
                return;
            }

            showLoading();

            Observable<TranslationResponse> translationResponseObservable =
                    ApiModule.getApiInterface().getTranslation(
                            inputText,
                            "en-ru",
                            null);

            mCompositeDisposable = new CompositeDisposable();
            mCompositeDisposable.add(translationResponseObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleRxResponse, this::handleRxError, this::handleRxComplete)
            );
        });

        return view;
    }

    private void handleRxResponse(TranslationResponse response) {
        hideLoading();

        @ResponseCodes int responseCode = response.getCode();
        if (responseCode != ResponseCodes.SUCCESS) {
            handleError(responseCode);
            return;
        }

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
        Toast.makeText(getContext(), "onError!", Toast.LENGTH_SHORT).show();
        error.printStackTrace();
    }

    private void handleRxComplete() {
        hideLoading();
        Toast.makeText(getContext(), "onComplete!", Toast.LENGTH_SHORT).show();
    }

    private void handleError(@ResponseCodes int errorCode) {
        hideLoading();

        switch (errorCode) {
            case ResponseCodes.API_KEY_BLOCKED:
                showError("API_KEY_BLOCKED");
                break;
            case ResponseCodes.API_KEY_INVALID:
                showError("API_KEY_INVALID");
                break;
            case ResponseCodes.DAY_LIMIT_EXCEED:
                showError("DAY_LIMIT_EXCEED");
                break;
            case ResponseCodes.TEXT_SIZE_EXCEED:
                showError("TEXT_SIZE_EXCEED");
                break;
            case ResponseCodes.TEXT_UNTRANSLATABLE:
                showError("TEXT_UNTRANSLATABLE");
                break;
            case ResponseCodes.TRANSLATION_DIRECTION_UNSUPPORTED:
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
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }
}
