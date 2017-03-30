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
        Uri returnUri;

        switch (matchCode) {
            case HISTORY: {
                long id = db.insert(Contract.HistoryEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(
                            Contract.HistoryEntry.CONTENT_URI_HISTORY, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
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
                break;
            case FAVORITES: {
                String mSelection = Contract.HistoryEntry.IS_FAVORITE + "=?";
                String[] mSelectionArgs = new String[]{SQL_BOOLEAN_TRUE};
                deletedCount = db.delete(Contract.HistoryEntry.TABLE_NAME,
                        mSelection, mSelectionArgs);
                break;
            }
            case HISTORY_WITH_ID: {
                String idToDelete = uri.getLastPathSegment();

                String whereClause = Contract.HistoryEntry._ID + "=?";
                String[] whereArgs = new String[]{idToDelete};
                deletedCount = db.delete(Contract.HistoryEntry.TABLE_NAME, whereClause, whereArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (deletedCount != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
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
            case HISTORY:
                updatedCount = db.update(Contract.HistoryEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (updatedCount != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            // TODO: Is this the good way?
            // Manually notify Favorites observers about History changes
            getContext().getContentResolver()
                    .notifyChange(Contract.HistoryEntry.CONTENT_URI_FAVORITES, null);
        }

        return updatedCount;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
