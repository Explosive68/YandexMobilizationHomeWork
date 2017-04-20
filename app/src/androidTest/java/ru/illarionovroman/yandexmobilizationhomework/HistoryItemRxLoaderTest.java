package ru.illarionovroman.yandexmobilizationhomework;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import ru.illarionovroman.yandexmobilizationhomework.model.HistoryItem;
import ru.illarionovroman.yandexmobilizationhomework.network.RestApi;
import ru.illarionovroman.yandexmobilizationhomework.network.response.TranslationResponse;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation.TranslationLoader;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation.TranslationParams;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation.TranslationLoader.buildTranslationLangParam;

@RunWith(MockitoJUnitRunner.class)
public class HistoryItemRxLoaderTest {

    private static final String TRANSLATION_PARAM_FORMAT = null;
    private static final int RESPONSE_CODE_SUCCESSFUL = 200;
    private static final int RESPONSE_TIMEOUT_SECONDS = 10;

    private Context mContext = InstrumentationRegistry.getTargetContext();

    private TranslationParams mParams;

    @Mock
    RestApi mRestApi;

    @Before
    public void prepareTranslation() {
        // Build test translation parameters
        mParams = new TranslationParams(TestUtils.TEST_VALUE_WORD,
                TestUtils.TEST_VALUE_LANG_FROM, TestUtils.TEST_VALUE_LANG_TO);

        // Simulate successful translation response from server
        TranslationResponse response = createSuccessfulTranslationResponse();
        Single<TranslationResponse> responseSingle = Single.just(response);
        when(mRestApi.getTranslation(anyString(), anyString(), eq(TRANSLATION_PARAM_FORMAT)))
                .thenReturn(responseSingle);
    }

    @Test
    public void translation_RxLoaderTestNetwork() {
        // Perform item load with mocked api
        TestObserver<HistoryItem> testObserver = new TestObserver<>();
        Single<HistoryItem> loadedSingle =
                TranslationLoader.loadHistoryItem(mContext, mRestApi, mParams);
        loadedSingle.subscribe(testObserver);

        // Wait and check observer work
        testObserver.awaitDone(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);

        // Check correctness of built item
        HistoryItem loadedItem = testObserver.values().get(0);
        assertTrue(loadedItem.getId() == HistoryItem.UNSPECIFIED_ID);
        assertTrue(loadedItem.getWord().equals(TestUtils.TEST_VALUE_WORD));
        assertTrue(loadedItem.getTranslation().equals(TestUtils.TEST_VALUE_TRANSLATION));
        assertTrue(loadedItem.getLanguageCodeFrom().equals(TestUtils.TEST_VALUE_LANG_FROM));
        assertTrue(loadedItem.getLanguageCodeTo().equals(TestUtils.TEST_VALUE_LANG_TO));
        assertTrue(loadedItem.getIsFavorite() == TestUtils.TEST_VALUE_IS_FAVORITE.equals("1"));
        assertTrue(loadedItem.getDate() == null);
    }

    /**
     * Simulates successful translation response from server
     */
    @NonNull
    private TranslationResponse createSuccessfulTranslationResponse() {
        TranslationResponse response = new TranslationResponse();
        response.setCode(RESPONSE_CODE_SUCCESSFUL);
        response.setLang(buildTranslationLangParam(mContext,
                TestUtils.TEST_VALUE_LANG_FROM, TestUtils.TEST_VALUE_LANG_TO));
        List<String> translations = new ArrayList<>(1);
        translations.add(TestUtils.TEST_VALUE_TRANSLATION);
        response.setTranslations(translations);
        return response;
    }
}