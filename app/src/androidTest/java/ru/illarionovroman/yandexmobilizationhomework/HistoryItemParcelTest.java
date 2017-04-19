package ru.illarionovroman.yandexmobilizationhomework;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.illarionovroman.yandexmobilizationhomework.model.HistoryItem;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class HistoryItemParcelTest {

    private HistoryItem mHistoryItem;

    @Before
    public void createHistoryItem() {
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
    public void historyItem_ParcelableWriteRead() throws Exception {

        // Write data to parcel
        Parcel parcel = Parcel.obtain();
        mHistoryItem.writeToParcel(parcel, mHistoryItem.describeContents());

        // Reset parcel for reading
        parcel.setDataPosition(0);

        // Read data from parcel and compare to written
        HistoryItem createdFromParcel = HistoryItem.CREATOR.createFromParcel(parcel);
        assertThat(createdFromParcel.getWord(), is(TestUtils.TEST_VALUE_WORD));
        assertThat(createdFromParcel.getTranslation(), is(TestUtils.TEST_VALUE_TRANSLATION));
        assertThat(createdFromParcel.getLanguageCodeFrom(), is(TestUtils.TEST_VALUE_LANG_FROM));
        assertThat(createdFromParcel.getLanguageCodeTo(), is(TestUtils.TEST_VALUE_LANG_TO));
        assertThat(createdFromParcel.getIsFavorite(), is(TestUtils.TEST_VALUE_IS_FAVORITE.equals("1")));
        assertThat(createdFromParcel.getDate(), is(TestUtils.TEST_VALUE_DATE));
    }
}