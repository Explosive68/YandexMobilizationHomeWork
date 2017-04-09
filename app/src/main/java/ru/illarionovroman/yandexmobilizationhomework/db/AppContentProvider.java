package ru.illarionovroman.yandexmobilizationhomework.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Application content provider. Closed for externals for now.
 *
 * I was trying to build useful provider, which can be easily used without knowing DB scheme.
 * So, here we have several URI options to pass in. But, how to glue it up with DB structure?
 *
 * I mean, there are History and Favorites items, which should have separate URIs to work with,
 * but it is just categories of the same table, and whenever one of them changes, we must notify
 * URI of another one, in both directions.
 *
 * The first thought was about organizing structure like this:
 * History - "authority/history"
 * Favorites - "authority/history/favorites"
 * But if we want to give provider's users to search for certain word (as a parameter of URI),
 * there would be a problem with this approach, since last part of Favorites URI will be recognized
 * as word to search in history ("authority/history/*").
 *
 * And how did you implement the behaviour, when you delete items from the History or Favorites
 * screen, these items remain on another screen?!
 *
 * Two separate tables? Kinda hard to maintain their tough connection.
 * Or, maybe, it's still one table, but there are two columns "IS_HISTORY" and "IS_FAVORITE".
 *
 * So I've ended up with following scheme (for now):
 * History - "authority/history"
 * Favorites - "authority/favorites"
 * And I manually notify the second one, when one of them changes.
 */
public class AppContentProvider extends ContentProvider {

    private static final int HISTORY = 100;
    private static final int HISTORY_WITH_ID = 101;
    private static final int HISTORY_SEARCH = 102;

    private static final int FAVORITES = 200;
    private static final int FAVORITES_SEARCH = 201;

    private static final String SQL_BOOLEAN_FALSE = "0";
    private static final String SQL_BOOLEAN_TRUE = "1";

    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH_HISTORY, HISTORY);
        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH_HISTORY + "/#", HISTORY_WITH_ID);
        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH_HISTORY + "/*", HISTORY_SEARCH);
        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH_FAVORITES + "/*", FAVORITES_SEARCH);

        return uriMatcher;
    }

    private DBHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        int matchCode = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (matchCode) {
            case HISTORY:
                retCursor = db.query(Contract.HistoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // Curly braces to resolve variable naming conflict
            case FAVORITES: {
                String mSelection = Contract.HistoryEntry.IS_FAVORITE + "=?";
                String[] mSelectionArgs = new String[]{SQL_BOOLEAN_TRUE};

                retCursor = db.query(Contract.HistoryEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case HISTORY_WITH_ID: {
                String idToSearch = uri.getLastPathSegment();

                String mSelection = Contract.HistoryEntry._ID + "=?";
                String[] mSelectionArgs = new String[]{idToSearch};

                retCursor = db.query(Contract.HistoryEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case HISTORY_SEARCH: {
                String textToSearch = uri.getLastPathSegment();

                String mSelection = Contract.HistoryEntry.WORD + "=? OR " +
                        Contract.HistoryEntry.TRANSLATION + "=?";
                String[] mSelectionArgs = new String[]{textToSearch, textToSearch};

                retCursor = db.query(Contract.HistoryEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case FAVORITES_SEARCH: {
                String textToSearch = uri.getLastPathSegment();

                String mSelection = Contract.HistoryEntry.IS_FAVORITE + "=?" +
                        " AND (" +
                        Contract.HistoryEntry.WORD + "=? OR " +
                        Contract.HistoryEntry.TRANSLATION + "=?" +
                        ")";
                String[] mSelectionArgs = new String[]{SQL_BOOLEAN_TRUE, textToSearch, textToSearch};

                retCursor = db.query(Contract.HistoryEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int matchCode = sUriMatcher.match(uri);
        Uri idUri = uri;

        switch (matchCode) {
            case HISTORY: {
                long id = db.insert(Contract.HistoryEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    idUri = ContentUris.withAppendedId(
                            Contract.HistoryEntry.CONTENT_URI_HISTORY, id);
                    getContext().getContentResolver().notifyChange(idUri, null);

                    // History changed, notify Favorites!
                    Uri favIdUri = Contract.HistoryEntry.CONTENT_URI_FAVORITES.buildUpon()
                            .appendPath(String.valueOf(id))
                            .build();
                    getContext().getContentResolver().notifyChange(favIdUri, null);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return idUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int deletedCount = 0;
        int matchCode = sUriMatcher.match(uri);
        switch (matchCode) {
            case HISTORY:
                deletedCount = db.delete(Contract.HistoryEntry.TABLE_NAME,
                        selection, selectionArgs);
                if (deletedCount != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    getContext().getContentResolver().notifyChange(
                            Contract.HistoryEntry.CONTENT_URI_FAVORITES, null);
                }
                break;
            case FAVORITES: {
                String mSelection = Contract.HistoryEntry.IS_FAVORITE + "=?";
                String[] mSelectionArgs = new String[]{SQL_BOOLEAN_TRUE};
                deletedCount = db.delete(Contract.HistoryEntry.TABLE_NAME,
                        mSelection, mSelectionArgs);
                if (deletedCount != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    // Favorites changed, History must know about it!
                    getContext().getContentResolver().notifyChange(
                            Contract.HistoryEntry.CONTENT_URI_HISTORY, null);
                }
                break;
            }
            case HISTORY_WITH_ID: {
                String idToDelete = uri.getLastPathSegment();

                String whereClause = Contract.HistoryEntry._ID + "=?";
                String[] whereArgs = new String[]{idToDelete};
                deletedCount = db.delete(Contract.HistoryEntry.TABLE_NAME, whereClause, whereArgs);
                if (deletedCount != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);

                    Uri favIdUri = Contract.HistoryEntry.CONTENT_URI_FAVORITES.buildUpon()
                            .appendPath(idToDelete)
                            .build();
                    getContext().getContentResolver().notifyChange(favIdUri, null);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return deletedCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int updatedCount = 0;
        int matchCode = sUriMatcher.match(uri);
        switch (matchCode) {
            case HISTORY: {
                updatedCount = db.update(Contract.HistoryEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                if (updatedCount != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    getContext().getContentResolver()
                            .notifyChange(Contract.HistoryEntry.CONTENT_URI_FAVORITES, null);
                }
                break;
            }
            case HISTORY_WITH_ID: {
                String idToUpdate = uri.getLastPathSegment();
                String mSelection = Contract.HistoryEntry._ID + "=?";
                String[] mSelectionArgs = new String[]{idToUpdate};

                updatedCount = db.update(Contract.HistoryEntry.TABLE_NAME, values,
                        mSelection, mSelectionArgs);
                if (updatedCount != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);

                    Uri favIdUri = Contract.HistoryEntry.CONTENT_URI_FAVORITES.buildUpon()
                            .appendPath(idToUpdate)
                            .build();
                    getContext().getContentResolver().notifyChange(favIdUri, null);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return updatedCount;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
