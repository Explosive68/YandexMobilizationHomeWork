package ru.illarionovroman.yandexmobilizationhomework;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.illarionovroman.yandexmobilizationhomework.model.HistoryItem;
import ru.illarionovroman.yandexmobilizationhomework.util.Prefs;

import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PreferencesTest {

    private Context mContext = InstrumentationRegistry.getTargetContext();

    private HistoryItem mHistoryItem;

    @Before
    public void initTestData() {
        mHistoryItem = new HistoryItem(
                TestUtils.TEST_VALUE_WORD,
                TestUtils.TEST_VALUE_TRANSLATION,
                TestUtils.TEST_VALUE_LANG_FROM,
                TestUtils.TEST_VALUE_LANG_TO);
        mHistoryItem.setId(TestUtils.TEST_VALUE_ID);
        mHistoryItem.setIsFavorite(TestUtils.TEST_VALUE_IS_FAVORITE.equals("1"));
        mHistoryItem.setDate(TestUtils.TEST_VALUE_DATE);
    }

    @Test
    public void preferences_WriteAndReadTest() throws Exception {

        // Use Prefs helper exactly as it's used in program
        Prefs.putLastUsedItemId(mContext, mHistoryItem.getId());
        long firstReadId = Prefs.getLastUsedItemId(mContext);
        // Check first write and read values match
        assertTrue("First write and read Ids are not equals", TestUtils.TEST_VALUE_ID == firstReadId);

        // Write another random id into preferences
        long secondWriteId = 10001;
        Prefs.putLastUsedItemId(mContext, secondWriteId);
        long secondReadId = Prefs.getLastUsedItemId(mContext);
        // Check first id was actually overwritten
        assertTrue("Old id was not overwritten", secondReadId != firstReadId);
        // Check second write and read values match
        assertTrue("Second write and read Ids are not equals", secondReadId == secondWriteId);
    }
}