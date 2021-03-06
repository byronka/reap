package com.byronkatz.reap.general;

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

  private static final String LOCATIONS_DATABASE_TABLE = "mainTable";
  private static final int DATABASE_VERSION = 1;

  // The index (key) column name for use in where clauses.
  public static final String KEY_ID="_id";



  private static final String LOCATIONS_DATABASE_CREATE = "create table " + 
      LOCATIONS_DATABASE_TABLE + " ("     + 
      KEY_ID + " integer primary key autoincrement" + ", " +
      ValueEnum.TOTAL_PURCHASE_VALUE.name()               + " REAL"    +     ", " +
      ValueEnum.YEARLY_INTEREST_RATE.name()               + " REAL"    +     ", " +
      ValueEnum.BUILDING_VALUE.name()                     + " REAL"    +     ", " +
      ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS.name()      + " INTEGER" +     ", " +
      ValueEnum.INFLATION_RATE.name()                     + " REAL"    +     ", " +
      ValueEnum.DOWN_PAYMENT.name()                       + " REAL"    +     ", " +
      ValueEnum.STREET_ADDRESS.name()                     + " TEXT"    +     ", " +
      ValueEnum.CITY.name()                               + " TEXT"    +     ", " +
      ValueEnum.STATE_INITIALS.name()                     + " TEXT"    +     ", " +
      ValueEnum.ESTIMATED_RENT_PAYMENTS.name()            + " REAL"    +     ", " +
      ValueEnum.REAL_ESTATE_APPRECIATION_RATE.name()      + " REAL"    +     ", " +
      ValueEnum.YEARLY_HOME_INSURANCE.name()              + " REAL"    +     ", " +
      ValueEnum.PROPERTY_TAX.name()                       + " REAL"    +     ", " +
      ValueEnum.LOCAL_MUNICIPAL_FEES.name()               + " REAL"    +     ", " +
      ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE.name()       + " REAL"    +     ", " +
      ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES.name()    + " REAL"    +     ", " +
      ValueEnum.MARGINAL_TAX_RATE.name()                  + " REAL"    +     ", " +
      ValueEnum.SELLING_BROKER_RATE.name()                + " REAL"    +     ", " +
      ValueEnum.GENERAL_SALE_EXPENSES.name()              + " REAL"    +     ", " +
      ValueEnum.REQUIRED_RATE_OF_RETURN.name()            + " REAL"    +     ", " +
      ValueEnum.FIX_UP_COSTS.name()                       + " REAL"    +     ","  +
      ValueEnum.PRIVATE_MORTGAGE_INSURANCE.name()         + " REAL"    +     ","  +
      ValueEnum.CLOSING_COSTS.name()                      + " REAL"    +
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
    int index = (int) db.insertOrThrow(LOCATIONS_DATABASE_TABLE, null, contentValues);

    return index;
  }

  public boolean removeEntry(long rowIndex) {
    return db.delete(LOCATIONS_DATABASE_TABLE, KEY_ID + "=" + rowIndex, null) > 0;
  }

  public Cursor getAllEntries () {
    
    return db.query(LOCATIONS_DATABASE_TABLE, null, null, null, null, null, null);
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
      db.execSQL(LOCATIONS_DATABASE_CREATE);

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
      db.execSQL("DROP TABLE IF EXISTS " + LOCATIONS_DATABASE_TABLE);
      // Create a new one.
      onCreate(db);
    }
  }
}