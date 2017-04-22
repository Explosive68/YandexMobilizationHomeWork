package ru.illarionovroman.yandexmobilizationhomework;

import android.content.ComponentName;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.illarionovroman.yandexmobilizationhomework.db.AppContentProvider;
import ru.illarionovroman.yandexmobilizationhomework.db.Contract;
import ru.illarionovroman.yandexmobilizationhomework.db.DBHelper;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

/**
 * Some crazy stuff with ContentProvider testing, that I've seen at Udacity lessons.
 */
@RunWith(AndroidJUnit4.class)
public class AppContentProviderTest {

    private final Context mContext = InstrumentationRegistry.getTargetContext();

    /**
     * Clear History table before each test
     */
    @Before
    public void setUp() {
        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(Contract.HistoryEntry.TABLE_NAME, null, null);
    }

    @Test
    public void testProviderRegistry() {

        String packageName = mContext.getPackageName();
        String contentProviderClassName = AppContentProvider.class.getName();

        // A ComponentName is an identifier for a specific application component, such as an
        // Activity, ContentProvider, BroadcastReceiver, or a Service.
        ComponentName componentName = new ComponentName(packageName, contentProviderClassName);

        // PackageManager gives us information about registered ContentProvider
        PackageManager pm = mContext.getPackageManager();

        try {
            // Get ProviderInfo to fetch registered authority from it
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            String actualAuthority = providerInfo.authority;
            String expectedAuthority = packageName;

            // Compare actual registered authority with package name
            String incorrectAuthority =
                    "Error: AppContentProvider registered with authority: " + actualAuthority +
                            " instead of expected authority: " + expectedAuthority;
            assertEquals(incorrectAuthority,
                    expectedAuthority,
                    actualAuthority);
        } catch (PackageManager.NameNotFoundException e) {
            String providerNotRegistered =
                    "Error: AppContentProvider not registered at " + packageName;
            // If it fails - make sure you did register ContentProvider in manifest file
            fail(providerNotRegistered);
        }
    }

    private static final Uri HISTORY = Contract.HistoryEntry.CONTENT_URI_HISTORY;
    private static final Uri HISTORY_WITH_ID = HISTORY.buildUpon().appendPath("1").build();
    private static final Uri HISTORY_SEARCH = HISTORY.buildUpon().appendPath("word").build();
    private static final Uri FAVORITES = Contract.HistoryEntry.CONTENT_URI_FAVORITES;
    private static final Uri FAVORITES_SEARCH = FAVORITES.buildUpon().appendPath("favWord").build();

    /**
     * Test correctness of UriMatcher implementation
     */
    @Test
    public void testUriMatcher() {

        UriMatcher testMatcher = AppContentProvider.buildUriMatcher();

        String historyUriMatchError = "History Uri wrong match.";
        int actualHistoryMatchCode = testMatcher.match(HISTORY);
        int expectedHistoryMatchCode = AppContentProvider.HISTORY;
        assertEquals(historyUriMatchError,
                expectedHistoryMatchCode, actualHistoryMatchCode);

        String historyWithIdUriMatchError = "HistoryWithId Uri wrong match.";
        int actualHistoryWithIdMatchCode = testMatcher.match(HISTORY_WITH_ID);
        int expectedHistoryWithIdMatchCode = AppContentProvider.HISTORY_WITH_ID;
        assertEquals(historyWithIdUriMatchError,
                expectedHistoryWithIdMatchCode, actualHistoryWithIdMatchCode);

        String historySearchUriMatchError = "HistorySearch Uri wrong match.";
        int actualHistorySearchMatchCode = testMatcher.match(HISTORY_SEARCH);
        int expectedHistorySearchMatchCode = AppContentProvider.HISTORY_SEARCH;
        assertEquals(historySearchUriMatchError,
                expectedHistorySearchMatchCode, actualHistorySearchMatchCode);

        String favoritesUriMatchError = "Favorites Uri wrong match.";
        int actualFavoritesMatchCode = testMatcher.match(FAVORITES);
        int expectedFavoritesMatchCode = AppContentProvider.FAVORITES;
        assertEquals(favoritesUriMatchError,
                expectedFavoritesMatchCode, actualFavoritesMatchCode);

        String favoritesSearchUriMatchError = "FavoritesSearch Uri wrong match.";
        int actualFavoritesSearchMatchCode = testMatcher.match(FAVORITES_SEARCH);
        int expectedFavoritesSearchMatchCode = AppContentProvider.FAVORITES_SEARCH;
        assertEquals(favoritesSearchUriMatchError,
                expectedFavoritesSearchMatchCode, actualFavoritesSearchMatchCode);
    }
}