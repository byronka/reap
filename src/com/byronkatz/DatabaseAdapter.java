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
  public static final String KEY_ID="id";

  // The name and column index of each column in your database.
  public static final String KEY_NAME                          = "name"; 
  public static final int    NAME_COLUMN                       = 1;
  public static final String TOTAL_PURCHASE_VALUE              = "total_purchase_value";
  public static final String YEARLY_INTEREST_RATE              = "yearly_interest_rate";
  public static final String MONTHLY_INTEREST_RATE             = "monthly_interest_rate";
  public static final String BUILDING_VALUE                    = "building_value";
  public static final String NUMBER_OF_COMPOUNDING_PERIODS     = "number_of_compounding_periods";
  public static final String INFLATION_RATE                    = "inflation_rate";
  public static final String PRIMARY_MORTGAGE_INSURANCE_RATE   = "primary_mortgage_insurance_rate";
  public static final String DOWN_PAYMENT                      = "down_payment";
  public static final String STREET_ADDRESS                    = "street_address";
  public static final String CITY                              = "city";
  public static final String STATE_INITIALS                    = "state_initials";
  public static final String ESTIMATED_RENT_PAYMENTS           = "estimated_rent_payments";
  public static final String REAL_ESTATE_APPRECIATION_RATE     = "real_estate_appreciation_rate";
  public static final String YEARLY_ALTERNATE_INVESTMENT_RETURN= "yearly_alternate_investment_return";
  public static final String YEARLY_HOME_INSURANCE             = "yearly_home_insurance";
  public static final String PROPERTY_TAX_RATE                 = "property_tax_rate";
  public static final String LOCAL_MUNICIPAL_FEES              = "local_municipal_fees";
  public static final String VACANCY_AND_CREDIT_LOSS_RATE      = "vacancy_and_credit_loss_rate";
  public static final String INITIAL_YEARLY_GENERAL_EXPENSES   = "initial_yearly_general_expenses";
  public static final String MARGINAL_TAX_RATE                 = "marginal_tax_rate";
  public static final String SELLING_BROKER_RATE               = "selling_broker_rate";
  public static final String GENERAL_SALE_EXPENSES             = "general_sale_expenses";
  public static final String REQUIRED_RATE_OF_RETURN           = "required_rate_of_return";
  public static final String FIX_UP_COSTS                      = "fix_up_costs";
  public static final String CLOSING_COSTS                     = "closing_costs";

  private static final String DATABASE_CREATE = "create table " + 
      DATABASE_TABLE + " ("     + 
      KEY_ID + " integer primary key autoincrement" + ", " +
//      KEY_NAME                           + " text not null" + ", " +
      TOTAL_PURCHASE_VALUE               + "REAL"    +     ", " +
      YEARLY_INTEREST_RATE               + "REAL"    +     ", " +
      MONTHLY_INTEREST_RATE              + "REAL"    +     ", " +
      BUILDING_VALUE                     + "REAL"    +     ", " +
      NUMBER_OF_COMPOUNDING_PERIODS      + "INTEGER" +     ", " +
      INFLATION_RATE                     + "REAL"    +     ", " +
      PRIMARY_MORTGAGE_INSURANCE_RATE    + "REAL"    +     ", " +
      DOWN_PAYMENT                       + "REAL"    +     ", " +
      STREET_ADDRESS                     + "TEXT"    +     ", " +
      CITY                               + "TEXT"    +     ", " +
      STATE_INITIALS                     + "TEXT"    +     ", " +
      ESTIMATED_RENT_PAYMENTS            + "REAL"    +     ", " +
      REAL_ESTATE_APPRECIATION_RATE      + "REAL"    +     ", " +
      YEARLY_ALTERNATE_INVESTMENT_RETURN + "REAL"    +     ", " +
      YEARLY_HOME_INSURANCE              + "REAL"    +     ", " +
      PROPERTY_TAX_RATE                  + "REAL"    +     ", " +
      LOCAL_MUNICIPAL_FEES               + "REAL"    +     ", " +
      VACANCY_AND_CREDIT_LOSS_RATE       + "REAL"    +     ", " +
      INITIAL_YEARLY_GENERAL_EXPENSES    + "REAL"    +     ", " +
      MARGINAL_TAX_RATE                  + "REAL"    +     ", " +
      SELLING_BROKER_RATE                + "REAL"    +     ", " +
      GENERAL_SALE_EXPENSES              + "REAL"    +     ", " +
      REQUIRED_RATE_OF_RETURN            + "REAL"    +     ", " +
      FIX_UP_COSTS                       + "REAL"    +     ","  +
      CLOSING_COSTS                      + "REAL"    +
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

  public int insertEntry(HashMap<String, String> fieldValues) {
    // Create a new ContentValues to represent my row
    // and insert it into the database.
    ContentValues contentValues = new ContentValues();
    
    for (HashMap.Entry<String, String> entry : fieldValues.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      contentValues.put(key, value);
    }
    
    int index = (int) db.insert(DATABASE_TABLE, null, contentValues);

    return index;
  }

  public boolean removeEntry(long rowIndex) {
    return db.delete(DATABASE_TABLE, KEY_ID + "=" + rowIndex, null) > 0;
  }

  public Cursor getAllEntries () {
    return db.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_NAME,TOTAL_PURCHASE_VALUE, 
YEARLY_INTEREST_RATE, MONTHLY_INTEREST_RATE, BUILDING_VALUE, NUMBER_OF_COMPOUNDING_PERIODS,
INFLATION_RATE, PRIMARY_MORTGAGE_INSURANCE_RATE, DOWN_PAYMENT, STREET_ADDRESS, CITY, STATE_INITIALS,
ESTIMATED_RENT_PAYMENTS, REAL_ESTATE_APPRECIATION_RATE, YEARLY_ALTERNATE_INVESTMENT_RETURN,
YEARLY_HOME_INSURANCE, PROPERTY_TAX_RATE, LOCAL_MUNICIPAL_FEES, VACANCY_AND_CREDIT_LOSS_RATE,
INITIAL_YEARLY_GENERAL_EXPENSES, MARGINAL_TAX_RATE, SELLING_BROKER_RATE, GENERAL_SALE_EXPENSES,
REQUIRED_RATE_OF_RETURN, FIX_UP_COSTS}, 
        null, null, null, null, null);
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