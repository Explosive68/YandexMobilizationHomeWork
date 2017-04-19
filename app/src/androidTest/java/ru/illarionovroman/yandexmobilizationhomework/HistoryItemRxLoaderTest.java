package ru.illarionovroman.yandexmobilizationhomework;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import retrofit2.http.Query;
import ru.illarionovroman.yandexmobilizationhomework.model.HistoryItem;
import ru.illarionovroman.yandexmobilizationhomework.network.RestApi;
import ru.illarionovroman.yandexmobilizationhomework.network.response.DetectLanguageResponse;
import ru.illarionovroman.yandexmobilizationhomework.network.response.SupportedLanguagesResponse;
import ru.illarionovroman.yandexmobilizationhomework.network.response.TranslationResponse;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation.TranslationLoader;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation.TranslationParams;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation.TranslationLoader.loadHistoryItem;

@RunWith(AndroidJUnit4.class)
public class HistoryItemRxLoaderTest {

    private Context mContext = InstrumentationRegistry.getTargetContext();

    private TranslationParams mParams;

    //@Mock
    RestApi mRestApi;

    @Before
    public void prepare() {

        mParams = new TranslationParams(TestUtils.TEST_VALUE_WORD,
                TestUtils.TEST_VALUE_LANG_FROM, TestUtils.TEST_VALUE_LANG_TO);

        // Simulate successful translation response from server
        TranslationResponse response = new TranslationResponse();
        response.setCode(200);
        response.setLang(TranslationLoader.buildTranslationLangParam(mContext,
                TestUtils.TEST_VALUE_LANG_FROM, TestUtils.TEST_VALUE_LANG_TO));
        List<String> translations = new ArrayList<>(1);
        translations.add(TestUtils.TEST_VALUE_TRANSLATION);
        response.setTranslations(translations);
        Single<TranslationResponse> responseSingle = Single.just(response);

        mRestApi = new RestApi() {
            @Override
            public Observable<SupportedLanguagesResponse> getSupportedLanguages(@Query("ui") String languageCode) {
                return null;
            }

            @Override
            public Observable<DetectLanguageResponse> detectLanguage(@Query("text") String languageCode, @Query("hint") String hintList) {
                return null;
            }

            @Override
            public Single<TranslationResponse> getTranslation(@Query("text") String text, @Query("lang") String langFromTo, @Query("format") String format) {
                return responseSingle;
            }
        };

        //FIXME: Mock is throwing NPE, why?
        /*when(mRestApi.getTranslation(anyString(), anyString(), anyString()))
                .thenReturn(responseSingle);*/
    }

    @Test
    public void translation_RxLoaderTest() {
        TestObserver<HistoryItem> testObserver = new TestObserver<>();

        Single<HistoryItem> loadedItem =
                TranslationLoader.loadHistoryItem(mContext, mRestApi, mParams);
        loadedItem.subscribe(testObserver);

        testObserver.awaitDone(10, TimeUnit.SECONDS);
        testObserver.assertComplete();
        testObserver.assertNoErrors();

        HistoryItem newItem = testObserver.values().get(0);
        assertTrue(newItem.getWord().equals(TestUtils.TEST_VALUE_WORD));
        assertTrue(newItem.getTranslation().equals(TestUtils.TEST_VALUE_TRANSLATION));
        assertTrue(newItem.getLanguageCodeFrom().equals(TestUtils.TEST_VALUE_LANG_FROM));
        assertTrue(newItem.getLanguageCodeTo().equals(TestUtils.TEST_VALUE_LANG_TO));
    }
}
