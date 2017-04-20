package ru.illarionovroman.yandexmobilizationhomework;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
import ru.illarionovroman.yandexmobilizationhomework.db.Contract;
import ru.illarionovroman.yandexmobilizationhomework.db.DBHelper;
import ru.illarionovroman.yandexmobilizationhomework.db.DBManager;
import ru.illarionovroman.yandexmobilizationhomework.model.HistoryItem;
import ru.illarionovroman.yandexmobilizationhomework.network.RestApi;
import ru.illarionovroman.yandexmobilizationhomework.network.response.TranslationResponse;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation.TranslationLoader;
import ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation.TranslationParams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TranslationLoaderTest {

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

        // Clear history before each test
        clearHistoryTable();
    }

    /**
     * Test which checks loading from database
     */
    @Test
    public void testLoadItemFromDatabase() {
        // First, we need test item to be in database
        HistoryItem item = new HistoryItem(
                TestUtils.TEST_VALUE_WORD,
                TestUtils.TEST_VALUE_TRANSLATION,
                TestUtils.TEST_VALUE_LANG_FROM,
                TestUtils.TEST_VALUE_LANG_TO
        );
        DBManager.addHistoryItem(mContext, item);

        // Now when we ask TranslationLoader to get translation from DB or network, it must
        // return result from database.
        TestObserver<HistoryItem> testObserver = new TestObserver<>();
        Single<HistoryItem> loadSingle =
                TranslationLoader.loadHistoryItem(mContext, mRestApi, mParams);
        loadSingle.subscribe(testObserver);

        // Wait and check is everything went as expected
        testObserver.awaitDone(1, TimeUnit.SECONDS);
        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);

        // Check that network was not invoked
        verify(mRestApi, never()).getTranslation(anyString(), anyString(), eq(TRANSLATION_PARAM_FORMAT));

        // We can confirm that this item came straight from DB by checking its Id and Date fields.
        // For items from network ids will be undefined, date will be null.
        HistoryItem loadedItem = testObserver.values().get(0);
        assertNotEquals(loadedItem.getId(), HistoryItem.UNSPECIFIED_ID);
        assertEquals(loadedItem.getWord(), TestUtils.TEST_VALUE_WORD);
        assertEquals(loadedItem.getTranslation(), TestUtils.TEST_VALUE_TRANSLATION);
        assertEquals(loadedItem.getLanguageCodeFrom(), TestUtils.TEST_VALUE_LANG_FROM);
        assertEquals(loadedItem.getLanguageCodeTo(), TestUtils.TEST_VALUE_LANG_TO);
        assertEquals(loadedItem.getIsFavorite(), TestUtils.TEST_VALUE_IS_FAVORITE.equals("1"));
        assertNotNull(loadedItem.getDate());
    }

    /**
     * This test will check scenario when we have empty database, so it must knock to network
     * for result.
     */
    @Test
    public void testLoadItemFromNetwork() {
        // Perform item load with mocked api
        TestObserver<HistoryItem> testObserver = new TestObserver<>();
        Single<HistoryItem> loadSingle =
                TranslationLoader.loadHistoryItem(mContext, mRestApi, mParams);
        loadSingle.subscribe(testObserver);

        // Wait and check observer work
        testObserver.awaitDone(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        testObserver.assertComplete();
        testObserver.assertNoErrors();
        testObserver.assertValueCount(1);

        // Check that network was called once
        verify(mRestApi, times(1)).getTranslation(anyString(), anyString(), eq(TRANSLATION_PARAM_FORMAT));

        // Check correctness of loaded item
        HistoryItem loadedItem = testObserver.values().get(0);
        assertEquals(loadedItem.getId(), HistoryItem.UNSPECIFIED_ID);
        assertEquals(loadedItem.getWord(), TestUtils.TEST_VALUE_WORD);
        assertEquals(loadedItem.getTranslation(), TestUtils.TEST_VALUE_TRANSLATION);
        assertEquals(loadedItem.getLanguageCodeFrom(), TestUtils.TEST_VALUE_LANG_FROM);
        assertEquals(loadedItem.getLanguageCodeTo(), TestUtils.TEST_VALUE_LANG_TO);
        assertEquals(loadedItem.getIsFavorite(), TestUtils.TEST_VALUE_IS_FAVORITE.equals("1"));
        assertNull(loadedItem.getDate());
    }

    /**
     * Simulates successful translation response from server
     */
    @NonNull
    private TranslationResponse createSuccessfulTranslationResponse() {
        TranslationResponse response = new TranslationResponse();
        response.setCode(RESPONSE_CODE_SUCCESSFUL);
        response.setLang(TranslationLoader.buildTranslationLangParam(mContext,
                TestUtils.TEST_VALUE_LANG_FROM, TestUtils.TEST_VALUE_LANG_TO));
        List<String> translations = new ArrayList<>(1);
        translations.add(TestUtils.TEST_VALUE_TRANSLATION);
        response.setTranslations(translations);
        return response;
    }

    private void clearHistoryTable() {
        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(Contract.HistoryEntry.TABLE_NAME, null, null);
    }
}