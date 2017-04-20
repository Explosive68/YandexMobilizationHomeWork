package ru.illarionovroman.yandexmobilizationhomework;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import ru.illarionovroman.yandexmobilizationhomework.db.Contract;
import ru.illarionovroman.yandexmobilizationhomework.db.DBHelper;
import ru.illarionovroman.yandexmobilizationhomework.model.HistoryItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Instrumented tests to check DB work
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private final Context mContext = InstrumentationRegistry.getTargetContext();
    private final Class mDbHelperClass = DBHelper.class;

    /**
     * To start each test clean, we delete the database to do so.
     */
    @Before
    public void setUp() {
        deleteTheDatabase();
    }

    /**
     * This method tests that our database contains all of the tables that we think it should
     * contain.
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void testCreateDatabase() throws Exception {

        // Use reflection to try to run the correct constructor whenever implemented
        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class)
                .newInstance(mContext);

        // Use DBHelper to get access to a writable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Verify database is open
        String databaseIsNotOpen = "The database should be open and isn't";
        assertEquals(databaseIsNotOpen,
                true,
                database.isOpen());

        // This Cursor will contain the names of each table in the database
        Cursor tableNameCursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" +
                        Contract.HistoryEntry.TABLE_NAME + "'",
                null);

        // If tableNameCursor.moveToFirst returns false from this query, it means the database
        // wasn't created properly. In actuality, it means that database contains no tables.
        String errorInCreatingDatabase =
                "Error: The database has not been created correctly";
        assertTrue(errorInCreatingDatabase,
                tableNameCursor.moveToFirst());

        // If this fails, it means that database doesn't contain the expected table
        assertEquals("Error: The database was created without the expected tables.",
                Contract.HistoryEntry.TABLE_NAME,
                tableNameCursor.getString(0));

        // Always close a cursor when you are done with it
        tableNameCursor.close();
    }

    /**
     * This method tests inserting a single record into an empty table from a brand new database.
     * The purpose is to test that the database is working as expected
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void testInsertSingleRecord() throws Exception {

        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class)
                .newInstance(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Create test ContentValues to insert
        ContentValues testValues = TestUtils.createTestContentValues();

        // Insert ContentValues into database and get first row ID back
        long firstRowId = database.insert(
                Contract.HistoryEntry.TABLE_NAME,
                null,
                testValues);

        // If the insert fails, database.insert returns -1
        assertNotEquals("Unable to insert into the database", -1, firstRowId);

        // Query the database and receive a Cursor
        Cursor cursor = database.query(
                Contract.HistoryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        // Cursor.moveToFirst will return false if there are no records returned from your query
        String emptyQueryError = "Error: No Records returned from history query";
        assertTrue(emptyQueryError,
                cursor.moveToFirst());

        // Close cursor and database
        cursor.close();
        dbHelper.close();
    }

    /**
     * This method tests equality of the queried values with inserted ones
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void testQuerySingleRecord() throws Exception {

        // Insert test row
        testInsertSingleRecord();

        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class)
                .newInstance(mContext);
        // Get readable database to query from
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query(
                Contract.HistoryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Check cursor in not empty
        String emptyQueryError = "Error: No Records returned from history query";
        assertTrue(emptyQueryError,
                cursor.moveToFirst());

        HistoryItem item = new HistoryItem(cursor);
        // Check every queried values to be equal to inserted
        assertEquals("Error: incorrect Word value",
                TestUtils.TEST_VALUE_WORD,
                item.getWord());
        assertEquals("Error: incorrect Translation value",
                TestUtils.TEST_VALUE_TRANSLATION,
                item.getTranslation());
        assertEquals("Error: incorrect LangFrom value",
                TestUtils.TEST_VALUE_LANG_FROM,
                item.getLanguageCodeFrom());
        assertEquals("Error: incorrect LangTo value",
                TestUtils.TEST_VALUE_LANG_TO,
                item.getLanguageCodeTo());
        assertEquals("Error: incorrect Date value",
                TestUtils.TEST_VALUE_DATE,
                item.getDate());
        assertEquals("Error: incorrect IsFavorite value",
                TestUtils.TEST_VALUE_IS_FAVORITE,
                item.getIsFavorite() ? "1" : "0");

        cursor.close();
        dbHelper.close();
    }

    /**
     * This method tests changes of item upon update
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void testUpdateSingleRecord() throws Exception {

        // Insert test row
        testInsertSingleRecord();

        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class)
                .newInstance(mContext);
        // Get readable database to query from
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query(
                Contract.HistoryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Check cursor in not empty
        String emptyQueryError = "Error: No Records returned from history query";
        assertTrue(emptyQueryError,
                cursor.moveToFirst());

        // Change some item data to check it later
        HistoryItem item = new HistoryItem(cursor);
        cursor.close();
        String changedDate = "2017-04-15 23:59:59";
        item.setDate(changedDate);
        ContentValues cv = item.toContentValues();

        // Update particular row
        String where = Contract.HistoryEntry._ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(item.getId())};
        int updatedCount = database.update(
                Contract.HistoryEntry.TABLE_NAME,
                cv,
                where,
                whereArgs
        );

        // Check whether update procedure has been successful
        assertTrue("Error: No records were updated",
                updatedCount > 0);

        Cursor updCursor = database.query(
                Contract.HistoryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Check cursor with updated values in not empty
        String emptyUpdatedQueryError = "Error: No Records returned from updated history query";
        assertTrue(emptyUpdatedQueryError,
                updCursor.moveToFirst());

        // Check whether updated date is changed
        HistoryItem updItem = new HistoryItem(updCursor);
        updCursor.close();
        assertEquals("Error: incorrect updated Date value",
                changedDate,
                updItem.getDate());

        dbHelper.close();
    }

    /**
     * This method tests deletion of item from table
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void testDeleteSingleRecord() throws Exception {

        // Insert test row
        testInsertSingleRecord();

        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class)
                .newInstance(mContext);
        // Get readable database to query from
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query(
                Contract.HistoryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // Check cursor in not empty
        String emptyQueryError = "Error: No Records returned from history query";
        assertTrue(emptyQueryError,
                cursor.moveToFirst());

        // Get item id to delete
        long itemId = cursor.getLong(cursor.getColumnIndex(Contract.HistoryEntry._ID));
        cursor.close();

        // Delete item
        String where = Contract.HistoryEntry._ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(itemId)};
        int deletedCount = database.delete(
                Contract.HistoryEntry.TABLE_NAME,
                where,
                whereArgs
        );

        // Check whether update procedure has been successful
        assertTrue("Error: No records were deleted",
                deletedCount > 0);

        // Try to get item with deleted id
        Cursor delCursor = database.query(
                Contract.HistoryEntry.TABLE_NAME,
                null,
                where,
                whereArgs,
                null,
                null,
                null
        );

        // Check cursor is empty
        String notEmptyQueryAfterDeletionError = "Error: Row still exists after deletion";
        assertFalse(notEmptyQueryAfterDeletionError,
                delCursor.moveToFirst());
        delCursor.close();

        dbHelper.close();
    }

    /**
     * Tests that database onUpgrade works as intended, by inserting row then calling onUpgrade
     * and verifies that the database has been successfully dropped and recreated by checking
     * that the database is there but empty
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void testUpgradeDatabase() throws Exception {

        SQLiteOpenHelper dbHelper =
                (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class).newInstance(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Insert row before we upgrade to check that we dropped the database correctly
        ContentValues testValues = TestUtils.createTestContentValues();
        database.insert(
                Contract.HistoryEntry.TABLE_NAME,
                null,
                testValues);

        // Perform upgrade, which supposed to drop table
        dbHelper.onUpgrade(database, 0, 1);
        database = dbHelper.getReadableDatabase();

        // This Cursor will contain the names of each table in our database
        Cursor tableNameCursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" +
                        Contract.HistoryEntry.TABLE_NAME + "'",
                null);

        // There must be only one such table
        assertTrue(tableNameCursor.getCount() == 1);
        tableNameCursor.close();

        // Query the new database and receive a Cursor
        Cursor cursor = database.query(
                Contract.HistoryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        // Check there is no items in brand new database
        assertFalse("Database doesn't seem to have been dropped successfully when upgrading",
                cursor.moveToFirst());
        cursor.close();

        database.close();
    }

    /**
     * Tests to ensure that inserts into the database results in automatically
     * incrementing row IDs.
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void testAutoincrement() throws Exception {

        // Insert test row
        testInsertSingleRecord();

        SQLiteOpenHelper dbHelper =
                (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class).newInstance(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtils.createTestContentValues();

        // Insert ContentValues into database and get first row ID back
        long firstRowId = database.insert(
                Contract.HistoryEntry.TABLE_NAME,
                null,
                testValues);

        // Change some data since there is unique constraint in the table
        testValues.put(Contract.HistoryEntry.WORD, "Another example");
        testValues.put(Contract.HistoryEntry.TRANSLATION, "Другой пример");

        // Insert changed ContentValues into database and get another row ID back
        long secondRowId = database.insert(
                Contract.HistoryEntry.TABLE_NAME,
                null,
                testValues);

        assertEquals("ID Autoincrement test failed!",
                firstRowId + 1, secondRowId);
    }

    /**
     * Deletes the entire database.
     */
    void deleteTheDatabase(){

        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(Contract.HistoryEntry.TABLE_NAME, null, null);

        try {
            // Use reflection to get the database name from the db helper class
            Field f = mDbHelperClass.getDeclaredField("DATABASE_NAME");
            f.setAccessible(true);
            mContext.deleteDatabase((String)f.get(null));
        }catch (NoSuchFieldException ex){
            fail("Make sure you have a member called DATABASE_NAME in the DBHelper");
        }catch (Exception ex){
            fail(ex.getMessage());
        }
    }
}
