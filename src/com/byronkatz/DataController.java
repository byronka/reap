package com.byronkatz;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;



public class DataController {

 
  private ContentValues contentValues;
  private HashMap<String, String> fieldValues;
  private DatabaseAdapter databaseAdapter;

  
  public DataController(Context context) {
    loadFieldValues();
    openOrCreateDatabase();
    
  }

  public void loadFieldValues() {
    fieldValues = new HashMap<String, String>();

    fieldValues.put(DatabaseAdapter.TOTAL_PURCHASE_VALUE,"450000.00");
    fieldValues.put(DatabaseAdapter.YEARLY_INTEREST_RATE, "0.05");
    fieldValues.put(DatabaseAdapter.MONTHLY_INTEREST_RATE, "0.00416667");
    fieldValues.put(DatabaseAdapter.BUILDING_VALUE, "150000.0");
    fieldValues.put(DatabaseAdapter.NUMBER_OF_COMPOUNDING_PERIODS, "360");
    fieldValues.put(DatabaseAdapter.INFLATION_RATE, "0.03");
    fieldValues.put(DatabaseAdapter.PRIMARY_MORTGAGE_INSURANCE_RATE, "0.20");
    fieldValues.put(DatabaseAdapter.DOWN_PAYMENT, "100000");
    fieldValues.put(DatabaseAdapter.STREET_ADDRESS, "1234 Anywhere Street");
    fieldValues.put(DatabaseAdapter.CITY, "Bethesda");
    fieldValues.put(DatabaseAdapter.STATE_INITIALS, "MD");
    fieldValues.put(DatabaseAdapter.ESTIMATED_RENT_PAYMENTS, "2015.0");
    fieldValues.put(DatabaseAdapter.REAL_ESTATE_APPRECIATION_RATE, "0.04");
    fieldValues.put(DatabaseAdapter.YEARLY_ALTERNATE_INVESTMENT_RETURN, "0.05");
    fieldValues.put(DatabaseAdapter.YEARLY_HOME_INSURANCE, "1000.0");
    fieldValues.put(DatabaseAdapter.PROPERTY_TAX_RATE, "0.0109");
    fieldValues.put(DatabaseAdapter.LOCAL_MUNICIPAL_FEES, "443.17");
    fieldValues.put(DatabaseAdapter.VACANCY_AND_CREDIT_LOSS_RATE, "0.03");
    fieldValues.put(DatabaseAdapter.INITIAL_YEARLY_GENERAL_EXPENSES, "2988.57");
    fieldValues.put(DatabaseAdapter.MARGINAL_TAX_RATE, "0.28");
    fieldValues.put(DatabaseAdapter.SELLING_BROKER_RATE, "0.06");
    fieldValues.put(DatabaseAdapter.GENERAL_SALE_EXPENSES, "2000");
    fieldValues.put(DatabaseAdapter.REQUIRED_RATE_OF_RETURN, "0.05");
    fieldValues.put(DatabaseAdapter.FIX_UP_COSTS, "6000.00");
  }
  
  private int openOrCreateDatabase() {
    int index = databaseAdapter.insertEntry(fieldValues);
    
    return index;
  }
 
}

