package com.byronkatz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DataController {

  private ContentValues contentValues;
  private DatabaseAdapter databaseAdapter;
  private List<Map<String, Float>> numericValues;
  private List<Map<String, String>> textValues;
  
  public DataController(Context context) {
    contentValues = new ContentValues();
    loadFieldValues();
    databaseAdapter = new DatabaseAdapter(context);
    numericValues = new ArrayList<Map<String, Float>>();
    textValues = new ArrayList<Map<String, String>>();
  }
  
  private void loadFieldValues() {
    
    Map<String, Float> numericFieldValues = new HashMap<String, Float> ();    
    Map<String, String> textFieldValues = new HashMap<String, String> ();
    
    numericFieldValues.put(DatabaseAdapter.TOTAL_PURCHASE_VALUE,0f);
    numericFieldValues.put(DatabaseAdapter.YEARLY_INTEREST_RATE, 0f);
    numericFieldValues.put(DatabaseAdapter.MONTHLY_INTEREST_RATE, 0f);
    numericFieldValues.put(DatabaseAdapter.BUILDING_VALUE, 0f);
    numericFieldValues.put(DatabaseAdapter.NUMBER_OF_COMPOUNDING_PERIODS, 360f);
    numericFieldValues.put(DatabaseAdapter.INFLATION_RATE, 0f);
    numericFieldValues.put(DatabaseAdapter.PRIMARY_MORTGAGE_INSURANCE_RATE, 0f);
    numericFieldValues.put(DatabaseAdapter.DOWN_PAYMENT, 0f);
    textFieldValues.put(DatabaseAdapter.STREET_ADDRESS, "");
    textFieldValues.put(DatabaseAdapter.CITY, "");
    textFieldValues.put(DatabaseAdapter.STATE_INITIALS, "");
    numericFieldValues.put(DatabaseAdapter.ESTIMATED_RENT_PAYMENTS, 0f);
    numericFieldValues.put(DatabaseAdapter.REAL_ESTATE_APPRECIATION_RATE, 0f);
    numericFieldValues.put(DatabaseAdapter.YEARLY_ALTERNATE_INVESTMENT_RETURN, 0f);
    numericFieldValues.put(DatabaseAdapter.YEARLY_HOME_INSURANCE, 0f);
    numericFieldValues.put(DatabaseAdapter.PROPERTY_TAX_RATE, 0f);
    numericFieldValues.put(DatabaseAdapter.LOCAL_MUNICIPAL_FEES, 0f);
    numericFieldValues.put(DatabaseAdapter.VACANCY_AND_CREDIT_LOSS_RATE, 0f);
    numericFieldValues.put(DatabaseAdapter.INITIAL_YEARLY_GENERAL_EXPENSES, 0f);
    numericFieldValues.put(DatabaseAdapter.MARGINAL_TAX_RATE, 0f);
    numericFieldValues.put(DatabaseAdapter.SELLING_BROKER_RATE, 0f);
    numericFieldValues.put(DatabaseAdapter.GENERAL_SALE_EXPENSES, 0f);
    numericFieldValues.put(DatabaseAdapter.REQUIRED_RATE_OF_RETURN, 0f);
    numericFieldValues.put(DatabaseAdapter.FIX_UP_COSTS, 0f);
    numericFieldValues.put(DatabaseAdapter.CLOSING_COSTS, 0f);
    
    numericValues.add(numericFieldValues);
    textValues.add(textFieldValues);
  }

  public void setValueAsFloat(String key, Float value) {
    
    Integer year = 1;
    Map<String, Float> numericMap = new HashMap<String, Float> ();
    numericMap.put(key, value);
    
    numericValues.add(year, numericMap);
  }
  
  public void setValueAsFloat(String key, Float value, Integer year) {
    
    //TODO: add code to check that the year parameter is kosher

    Map<String, Float> numericMap = new HashMap<String, Float> ();
    numericMap.put(key, value);
    
    numericValues.add(year, numericMap);
  }
  
  public void setValueAsString(String key, String value) {
    
    Integer year = 1;
    Map<String, String> textMap = new HashMap<String, String> ();
    textMap.put(key, value);
    textValues.add(year, textMap);
  }

  public void setValueAsString(String key, String value, Integer year) {
    
    //TODO: add code to check that the year parameter is kosher

    Map<String, String> textMap = new HashMap<String, String> ();
    textMap.put(key, value);
    textValues.add(year, textMap);
  }
  
  public String getValueAsString(String key) {
    
    Integer year = 1;
    Map<String, String> textMap = textValues.get(year);
    
    return textMap.get(key);
  }

  public String getValueAsString(String key, Integer year) {
    
    //TODO: add code to check that the year parameter is kosher
    
    Map<String, String> textMap = textValues.get(year);
    return textMap.get(key);
  }
  
  public Float getValueAsFloat(String key) {
    
    Integer year = 1;
    Map<String, Float> numericMap = numericValues.get(year);
    
    return numericMap.get(key);
  }

  public Float getValueAsFloat(String key, Integer year) {
    
    Map<String, Float> numericMap = numericValues.get(year);
    
    return numericMap.get(key);
  }
  
  public void saveValues() {
    databaseAdapter.insertEntry(contentValues);
  }

  public Cursor getAllDatabaseValues() {
    Cursor cursor = databaseAdapter.getAllEntries();
    return cursor;
  }

}

