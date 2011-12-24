package com.byronkatz;

//** Listing 7-1: Skeleton code for a standard database adapter implementation
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {
  private static final String DATABASE_NAME                    = "investmentValues";

  private static final String DATABASE_TABLE = "mainTable";
  private static final int DATABASE_VERSION = 1;

  // The index (key) column name for use in where clauses.
  public static final String KEY_ID="_id";


  private static final String DATABASE_CREATE = "create table " + 
      DATABASE_TABLE + " ("     + 
      KEY_ID + " integer primary key autoincrement" + ", " +
      ValueEnum.TOTAL_PURCHASE_VALUE               + " REAL"    +     ", " +
      ValueEnum.YEARLY_INTEREST_RATE               + " REAL"    +     ", " +
      ValueEnum.BUILDING_VALUE                     + " REAL"    +     ", " +
      ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS      + " INTEGER" +     ", " +
      ValueEnum.INFLATION_RATE                     + " REAL"    +     ", " +
      ValueEnum.PRIMARY_MORTGAGE_INSURANCE_RATE    + " REAL"    +     ", " +
      ValueEnum.DOWN_PAYMENT                       + " REAL"    +     ", " +
      ValueEnum.STREET_ADDRESS                     + " TEXT"    +     ", " +
      ValueEnum.CITY                               + " TEXT"    +     ", " +
      ValueEnum.STATE_INITIALS                     + " TEXT"    +     ", " +
      ValueEnum.ESTIMATED_RENT_PAYMENTS            + " REAL"    +     ", " +
      ValueEnum.REAL_ESTATE_APPRECIATION_RATE      + " REAL"    +     ", " +
      ValueEnum.YEARLY_ALTERNATE_INVESTMENT_RETURN + " REAL"    +     ", " +
      ValueEnum.YEARLY_HOME_INSURANCE              + " REAL"    +     ", " +
      ValueEnum.PROPERTY_TAX_RATE                  + " REAL"    +     ", " +
      ValueEnum.LOCAL_MUNICIPAL_FEES               + " REAL"    +     ", " +
      ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE       + " REAL"    +     ", " +
      ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES    + " REAL"    +     ", " +
      ValueEnum.MARGINAL_TAX_RATE                  + " REAL"    +     ", " +
      ValueEnum.SELLING_BROKER_RATE                + " REAL"    +     ", " +
      ValueEnum.GENERAL_SALE_EXPENSES              + " REAL"    +     ", " +
      ValueEnum.REQUIRED_RATE_OF_RETURN            + " REAL"    +     ", " +
      ValueEnum.FIX_UP_COSTS                       + " REAL"    +     ","  +
      ValueEnum.CLOSING_COSTS                      + " REAL"    +
      		" );";

  private SQLiteDatabase db;
  private myDbHelper dbHelper;

  public DatabaseAdapter(Context context) {
    dbHelper = new myDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    db = dbHelper.getWritableDatabase();
  }

  public DatabaseAdapter open() throws SQLException {
    db = dbHelper.getWritableDatabase();
    return this;
  }

  public void close() {
    db.close();
  }

  public int insertEntry(ContentValues contentValues) {
    contentValues.remove(KEY_ID);
    int index = (int) db.insertOrThrow(DATABASE_TABLE, null, contentValues);

    return index;
  }

  public boolean removeEntry(long rowIndex) {
    return db.delete(DATABASE_TABLE, KEY_ID + "=" + rowIndex, null) > 0;
  }

  public Cursor getAllEntries () {
    
    return db.query(DATABASE_TABLE, null, null, null, null, null, null);
  }

  public HashMap<String, String> getEntry(long rowIndex) {
    HashMap<String, String> fieldValues = null;
    // TODO: Return a cursor to a row from the database and
    // use the values to populate an instance of MyObject
    return fieldValues;
  }

  public boolean updateEntry(long rowIndex, HashMap<String, String> fieldValues) {
    // TODO: Create a new ContentValues based on the new object
    // and use it to update a row in the database.
    return true;
  }

  private static class myDbHelper extends SQLiteOpenHelper {

    public myDbHelper(Context context, String name, 
        CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    // Called when no database exists in disk and the helper class needs
    // to create a new one. 
    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(DATABASE_CREATE);
    }

    // Called when there is a database version mismatch meaning that the version
    // of the database on disk needs to be upgraded to the current version.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // Log the version upgrade.
      Log.w("TaskDBAdapter", "Upgrading from version " + 
          oldVersion + " to " +
          newVersion + ", which will destroy all old data");

      // Upgrade the existing database to conform to the new version. Multiple 
      // previous versions can be handled by comparing oldVersion and newVersion
      // values.

      // The simplest case is to drop the old table and create a new one.
      db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
      // Create a new one.
      onCreate(db);
    }
  }
}