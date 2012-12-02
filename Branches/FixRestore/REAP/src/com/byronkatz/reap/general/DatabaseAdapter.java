package com.byronkatz.reap.general;

//** Listing 7-1: Skeleton code for a standard database adapter implementation
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {
  private static final String DATABASE_NAME            = "investmentValues";

  private static final String LOCATIONS_DATABASE_TABLE = "mainTable";
  private static final int DATABASE_VERSION = 2;

  // The index (key) column name for use in where clauses.
  public static final String KEY_ID="_id";
  public static final String MODIFIED_AT = "modified_at";
  public static final String YEAR_VALUE = "YEAR_VALUE";



  private static final String LOCATIONS_DATABASE_CREATE = "create table if not exists " + 
      LOCATIONS_DATABASE_TABLE + " ("     + 
      KEY_ID + " integer primary key autoincrement" + ", " +
      MODIFIED_AT                                         + " DEFAULT (datetime(\'now\',\'localtime\'))" +   ", " +
      ValueEnum.TOTAL_PURCHASE_VALUE.name()               + " REAL"    +     ", " +
      ValueEnum.YEARLY_INTEREST_RATE.name()               + " REAL"    +     ", " +
      ValueEnum.BUILDING_VALUE.name()                     + " REAL"    +     ", " +
      ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS.name()      + " INTEGER" +     ", " +
      ValueEnum.EXTRA_YEARS.name()                        + " INTEGER" +     ", " +
      ValueEnum.INFLATION_RATE.name()                     + " REAL"    +     ", " +
      ValueEnum.DOWN_PAYMENT.name()                       + " REAL"    +     ", " +
      ValueEnum.STREET_ADDRESS.name()                     + " TEXT"    +     ", " +
      ValueEnum.CITY.name()                               + " TEXT"    +     ", " +
      ValueEnum.STATE_INITIALS.name()                     + " TEXT"    +     ", " +
      ValueEnum.COMMENTS.name()                           + " TEXT"    +     ", " +
      ValueEnum.ESTIMATED_RENT_PAYMENTS.name()            + " REAL"    +     ", " +
      ValueEnum.REAL_ESTATE_APPRECIATION_RATE.name()      + " REAL"    +     ", " +
      ValueEnum.INITIAL_HOME_INSURANCE.name()             + " REAL"    +     ", " +
      ValueEnum.PROPERTY_TAX.name()                       + " REAL"    +     ", " +
      ValueEnum.LOCAL_MUNICIPAL_FEES.name()               + " REAL"    +     ", " +
      ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE.name()       + " REAL"    +     ", " +
      ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES.name()    + " REAL"    +     ", " +
      ValueEnum.MARGINAL_TAX_RATE.name()                  + " REAL"    +     ", " +
      ValueEnum.MONTHS_UNTIL_RENT_STARTS.name()           + " REAL"    +     ", " +
      ValueEnum.SELLING_BROKER_RATE.name()                + " REAL"    +     ", " +
      ValueEnum.GENERAL_SALE_EXPENSES.name()              + " REAL"    +     ", " +
      ValueEnum.REQUIRED_RATE_OF_RETURN.name()            + " REAL"    +     ", " +
      ValueEnum.FIX_UP_COSTS.name()                       + " REAL"    +     ","  +
      ValueEnum.INITIAL_VALUATION.name()                  + " REAL"    +     ","  +
      ValueEnum.PRIVATE_MORTGAGE_INSURANCE.name()         + " REAL"    +     ","  +
      ValueEnum.CLOSING_COSTS.name()                      + " REAL"    +     ","  +
      ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN.name()   + " REAL"    +     ","  +
      ValueEnum.ATCF.name()                               + " REAL"    +     ","  +
      ValueEnum.NPV.name()                                + " REAL"    +     ","  +
      ValueEnum.ATER.name()                               + " REAL"    +     ","  +
      ValueEnum.CAP_RATE_ON_PROJECTED_VALUE.name()        + " REAL"    +     ","  +
      ValueEnum.CAP_RATE_ON_PURCHASE_VALUE.name()         + " REAL"    +     ","  +
      YEAR_VALUE                                          + " REAL"    +
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

  public Cursor getAllEntries (String sorterText) {
    
    return db.query(LOCATIONS_DATABASE_TABLE, null, null, null, null, null, sorterText);
  }

  public HashMap<String, String> getEntry(long rowIndex) {
    HashMap<String, String> fieldValues = null;
    return fieldValues;
  }

  public boolean updateEntry(long rowIndex, ContentValues contentValue) {

    db.update(LOCATIONS_DATABASE_TABLE, contentValue, 
        KEY_ID + "=" + rowIndex, null);
    return true;
  }

  private static class myDbHelper extends SQLiteOpenHelper {

    public myDbHelper(Context context, String name, 
        CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    private void createNewTable(SQLiteDatabase db) {
      
      db.execSQL(LOCATIONS_DATABASE_CREATE);
    }
    
    private void createTrigger(SQLiteDatabase db) {
      //following adds a trigger so the modified time gets changed whenever an update occurs

      db.execSQL(
          "CREATE TRIGGER update_modified_timestamp AFTER UPDATE ON mainTable " +
          "BEGIN " +
          " update mainTable SET modified_at = datetime('now', 'localtime') WHERE rowid = new.rowid; " +
          "END;"
          );
    }
    
    // Called when no database exists in disk and the helper class needs
    // to create a new one. 
    @Override
    public void onCreate(SQLiteDatabase db) {
      createNewTable(db);
      createTrigger(db);
    }
    
    private void upgradeProcedure(SQLiteDatabase db) {
      //all this is done to account for situations where I am actually
      //dropping certain columns in the table before copying values from one to the other.
      db.beginTransaction();
      try {
        //note that creating a table has a IF NOT EXISTS clause, so this will not run
        //if the table is already there.
        createNewTable(db);
        
        //get the columns in the existing (old) table
        List<String> columns = getColumns(db, LOCATIONS_DATABASE_TABLE);
        
        //rename the table to temp_table
        db.execSQL("ALTER table " + LOCATIONS_DATABASE_TABLE +
            " RENAME TO temp_" + LOCATIONS_DATABASE_TABLE);
        
        //create a new table (note that the first one a few lines before was probably not run)
        createNewTable(db);
        
        //if this upgrade removes columns, this is where they get removed
        columns.retainAll(getColumns(db, LOCATIONS_DATABASE_TABLE));
        String cols = join(columns, ","); 
        
        
        db.execSQL(String.format( "INSERT INTO %s (%s) SELECT %s from temp_%s", 
            LOCATIONS_DATABASE_TABLE, cols, cols, LOCATIONS_DATABASE_TABLE)); 
        db.execSQL("DROP table temp_" + LOCATIONS_DATABASE_TABLE);
        db.setTransactionSuccessful();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        db.endTransaction();
      }
    }
    
    /**
     * simply returns the columns of the table in the database 
     * @param db the database which has a table with columns
     * @param tableName the name of the table in the database
     * @return a list of strings representing the columns of the table
     */
    private List<String> getColumns(SQLiteDatabase db, String tableName) {
      List<String> archive = null;
      Cursor c = null;
      try {
          c = db.rawQuery("select * from " + tableName + " limit 1", null);
          if (c != null) {
              archive = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
          }
      } catch (Exception e) {
          Log.v(tableName, e.getMessage(), e);
          e.printStackTrace();
      } finally {
          if (c != null)
              c.close();
      }
      return archive;
  }

    /**
     * returns a delimited, joined, list of strings
     * 
     * For example, if we provide apple, banana, cat in a list, this will return
     * "apple,banana,cat" if we provide a comma as a delimiter
     * @param list the list of strings to join together
     * @param delim the character we wish as a delimited
     * @return
     */
  private String join(List<String> list, String delim) {
      StringBuilder buf = new StringBuilder();
      int num = list.size();
      for (int i = 0; i < num; i++) {
          if (i != 0)
              buf.append(delim);
          buf.append((String) list.get(i));
      }
      return buf.toString();
  }


    // Called when there is a database version mismatch meaning that the version
    // of the database on disk needs to be upgraded to the current version.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // Log the version upgrade.
      Log.w("TaskDBAdapter", "Upgrading from version " + 
          oldVersion + " to " +
          newVersion + ", and will carry old data to the new database");

      // Upgrade the existing database to conform to the new version. Multiple 
      // previous versions can be handled by comparing oldVersion and newVersion
      // values.

      upgradeProcedure(db);
    }
  }
}