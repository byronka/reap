package com.byronkatz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DataController {

  private ContentValues contentValues;
  private DatabaseAdapter databaseAdapter;

  
  public DataController(Context context) {
    contentValues = new ContentValues();
    loadFieldValues();
    databaseAdapter = new DatabaseAdapter(context);
  }
  
  public void loadFieldValues() {

    contentValues.put(DatabaseAdapter.TOTAL_PURCHASE_VALUE,"0");
    contentValues.put(DatabaseAdapter.YEARLY_INTEREST_RATE, "0");
    contentValues.put(DatabaseAdapter.MONTHLY_INTEREST_RATE, "0");
    contentValues.put(DatabaseAdapter.BUILDING_VALUE, "0");
    contentValues.put(DatabaseAdapter.NUMBER_OF_COMPOUNDING_PERIODS, "360");
    contentValues.put(DatabaseAdapter.INFLATION_RATE, "0");
    contentValues.put(DatabaseAdapter.PRIMARY_MORTGAGE_INSURANCE_RATE, "0");
    contentValues.put(DatabaseAdapter.DOWN_PAYMENT, "0");
    contentValues.put(DatabaseAdapter.STREET_ADDRESS, "");
    contentValues.put(DatabaseAdapter.CITY, "");
    contentValues.put(DatabaseAdapter.STATE_INITIALS, "");
    contentValues.put(DatabaseAdapter.ESTIMATED_RENT_PAYMENTS, "0");
    contentValues.put(DatabaseAdapter.REAL_ESTATE_APPRECIATION_RATE, "0");
    contentValues.put(DatabaseAdapter.YEARLY_ALTERNATE_INVESTMENT_RETURN, "0");
    contentValues.put(DatabaseAdapter.YEARLY_HOME_INSURANCE, "0");
    contentValues.put(DatabaseAdapter.PROPERTY_TAX_RATE, "0");
    contentValues.put(DatabaseAdapter.LOCAL_MUNICIPAL_FEES, "0");
    contentValues.put(DatabaseAdapter.VACANCY_AND_CREDIT_LOSS_RATE, "0");
    contentValues.put(DatabaseAdapter.INITIAL_YEARLY_GENERAL_EXPENSES, "0");
    contentValues.put(DatabaseAdapter.MARGINAL_TAX_RATE, "0");
    contentValues.put(DatabaseAdapter.SELLING_BROKER_RATE, "0");
    contentValues.put(DatabaseAdapter.GENERAL_SALE_EXPENSES, "0");
    contentValues.put(DatabaseAdapter.REQUIRED_RATE_OF_RETURN, "0");
    contentValues.put(DatabaseAdapter.FIX_UP_COSTS, "0");
    contentValues.put(DatabaseAdapter.CLOSING_COSTS, "0");
  }
  
  public void setContentValues(ContentValues contentValues) {
    this.contentValues = contentValues;
  }
  
  public void setValue(String key, String value) {
    contentValues.put(key, value);
  }
  
  public String getValue(String key) {
    String value = contentValues.getAsString(key);
    return value;
  }

  public void saveValues() {
    databaseAdapter.insertEntry(contentValues);
  }

  public Cursor getAllDatabaseValues() {
    Cursor cursor = databaseAdapter.getAllEntries();
    return cursor;
  }
  
}

