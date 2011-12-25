package com.byronkatz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DataController {

  private DatabaseAdapter databaseAdapter;
  private List<Map<Enum<ValueEnum>, Float>> numericValues;
  private List<Map<Enum<ValueEnum>, String>> textValues;
  
  public DataController(Context context) {
    loadFieldValues();
    databaseAdapter = new DatabaseAdapter(context);
    numericValues = new ArrayList<Map<Enum<ValueEnum>, Float>>();
    textValues = new ArrayList<Map<Enum<ValueEnum>, String>>();
  }
  
  private void loadFieldValues() {
    
    Map<Enum<ValueEnum>, Float> numericFieldValues = new HashMap<Enum<ValueEnum>, Float> ();    
    Map<Enum<ValueEnum>, String> textFieldValues = new HashMap<Enum<ValueEnum>, String> ();
    
    numericFieldValues.put(ValueEnum.TOTAL_PURCHASE_VALUE,0f);
    numericFieldValues.put(ValueEnum.YEARLY_INTEREST_RATE, 0f);
    numericFieldValues.put(ValueEnum.MONTHLY_INTEREST_RATE, 0f);
    numericFieldValues.put(ValueEnum.BUILDING_VALUE, 0f);
    numericFieldValues.put(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS, 360f);
    numericFieldValues.put(ValueEnum.INFLATION_RATE, 0f);
    numericFieldValues.put(ValueEnum.PRIMARY_MORTGAGE_INSURANCE_RATE, 0f);
    numericFieldValues.put(ValueEnum.DOWN_PAYMENT, 0f);
    textFieldValues.put(ValueEnum.STREET_ADDRESS, "");
    textFieldValues.put(ValueEnum.CITY, "");
    textFieldValues.put(ValueEnum.STATE_INITIALS, "");
    numericFieldValues.put(ValueEnum.ESTIMATED_RENT_PAYMENTS, 0f);
    numericFieldValues.put(ValueEnum.REAL_ESTATE_APPRECIATION_RATE, 0f);
    numericFieldValues.put(ValueEnum.YEARLY_ALTERNATE_INVESTMENT_RETURN, 0f);
    numericFieldValues.put(ValueEnum.YEARLY_HOME_INSURANCE, 0f);
    numericFieldValues.put(ValueEnum.PROPERTY_TAX_RATE, 0f);
    numericFieldValues.put(ValueEnum.LOCAL_MUNICIPAL_FEES, 0f);
    numericFieldValues.put(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE, 0f);
    numericFieldValues.put(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES, 0f);
    numericFieldValues.put(ValueEnum.MARGINAL_TAX_RATE, 0f);
    numericFieldValues.put(ValueEnum.SELLING_BROKER_RATE, 0f);
    numericFieldValues.put(ValueEnum.GENERAL_SALE_EXPENSES, 0f);
    numericFieldValues.put(ValueEnum.REQUIRED_RATE_OF_RETURN, 0f);
    numericFieldValues.put(ValueEnum.FIX_UP_COSTS, 0f);
    numericFieldValues.put(ValueEnum.CLOSING_COSTS, 0f);
    
    numericValues.add(numericFieldValues);
    textValues.add(textFieldValues);
  }

  public void setValueAsFloat(Enum<ValueEnum> key, Float value) {
    
    Integer year = 1;
    Map<Enum<ValueEnum>, Float> numericMap = new HashMap<Enum<ValueEnum>, Float> ();
    numericMap.put(key, value);
    
    numericValues.add(year, numericMap);
  }
  
  public void setValueAsFloat(Enum<ValueEnum> key, Float value, Integer year) {
    
    //TODO: add code to check that the year parameter is kosher

    Map<Enum<ValueEnum>, Float> numericMap = new HashMap<Enum<ValueEnum>, Float> ();
    numericMap.put(key, value);
    
    numericValues.add(year, numericMap);
  }
  
  public void setValueAsString(Enum<ValueEnum> key, String value) {
    
    Integer year = 1;
    Map<Enum<ValueEnum>, String> textMap = new HashMap<Enum<ValueEnum>, String> ();
    textMap.put(key, value);
    textValues.add(year, textMap);
  }

  public void setValueAsString(Enum<ValueEnum> key, String value, Integer year) {
    
    //TODO: add code to check that the year parameter is kosher

    Map<Enum<ValueEnum>, String> textMap = new HashMap<Enum<ValueEnum>, String> ();
    textMap.put(key, value);
    textValues.add(year, textMap);
  }
  
  public String getValueAsString(Enum<ValueEnum> key) {
    
    Integer year = 1;
    Map<Enum<ValueEnum>, String> textMap = textValues.get(year);
    
    return textMap.get(key);
  }

  public String getValueAsString(Enum<ValueEnum> key, Integer year) {
    
    //TODO: add code to check that the year parameter is kosher
    
    Map<Enum<ValueEnum>, String> textMap = textValues.get(year);
    return textMap.get(key);
  }
  
  public Float getValueAsFloat(Enum<ValueEnum> key) {
    
    Integer year = 1;
    Map<Enum<ValueEnum>, Float> numericMap = numericValues.get(year);
    
    return numericMap.get(key);
  }

  public Float getValueAsFloat(Enum<ValueEnum> key, Integer year) {
    
    Map<Enum<ValueEnum>, Float> numericMap = numericValues.get(year);
    
    return numericMap.get(key);
  }
  
  public void saveValues() {
    ContentValues cv = new ContentValues();
    // get year 1
    //TODO: This next 5 lines may be a problem, since it includes calc
    //values which don't go in the database.  Check.
    for (Entry<Enum<ValueEnum>, Float> m: numericValues.get(1).entrySet()) {
      String key = m.getKey().toString();
      Float value = m.getValue();
      cv.put(key, value);
    }
    
    for (Entry<Enum<ValueEnum>, String> m: textValues.get(1).entrySet()) {
      String key = m.getKey().toString();
      String value = m.getValue();
      cv.put(key, value);
    }
    databaseAdapter.insertEntry(cv);
  }

  public Cursor getAllDatabaseValues() {
    Cursor cursor = databaseAdapter.getAllEntries();
    return cursor;
  }
  
  public void setCurrentData(ContentValues cv) {
    
    Map<Enum<ValueEnum>, Float> numericFieldValues = new HashMap<Enum<ValueEnum>, Float> ();    
    Map<Enum<ValueEnum>, String> textFieldValues = new HashMap<Enum<ValueEnum>, String> ();
    
    numericFieldValues.put(
        ValueEnum.TOTAL_PURCHASE_VALUE,cv.getAsFloat(
            ValueEnum.TOTAL_PURCHASE_VALUE.toString()));
    numericFieldValues.put(
        ValueEnum.YEARLY_INTEREST_RATE, 0f);
    numericFieldValues.put(
        ValueEnum.MONTHLY_INTEREST_RATE, 0f);
    numericFieldValues.put(
        ValueEnum.BUILDING_VALUE, 0f);
    numericFieldValues.put(
        ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS, 360f);
    numericFieldValues.put(
        ValueEnum.INFLATION_RATE, 0f);
    numericFieldValues.put(
        ValueEnum.PRIMARY_MORTGAGE_INSURANCE_RATE, 0f);
    numericFieldValues.put(
        ValueEnum.DOWN_PAYMENT, 0f);
    textFieldValues.put(
        ValueEnum.STREET_ADDRESS, "");
    textFieldValues.put(
        ValueEnum.CITY, "");
    textFieldValues.put(
        ValueEnum.STATE_INITIALS, "");
    numericFieldValues.put(
        ValueEnum.ESTIMATED_RENT_PAYMENTS, 0f);
    numericFieldValues.put(
        ValueEnum.REAL_ESTATE_APPRECIATION_RATE, 0f);
    numericFieldValues.put(
        ValueEnum.YEARLY_ALTERNATE_INVESTMENT_RETURN, 0f);
    numericFieldValues.put(
        ValueEnum.YEARLY_HOME_INSURANCE, 0f);
    numericFieldValues.put(
        ValueEnum.PROPERTY_TAX_RATE, 0f);
    numericFieldValues.put(
        ValueEnum.LOCAL_MUNICIPAL_FEES, 0f);
    numericFieldValues.put(
        ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE, 0f);
    numericFieldValues.put(
        ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES, 0f);
    numericFieldValues.put(
        ValueEnum.MARGINAL_TAX_RATE, 0f);
    numericFieldValues.put(
        ValueEnum.SELLING_BROKER_RATE, 0f);
    numericFieldValues.put(
        ValueEnum.GENERAL_SALE_EXPENSES, 0f);
    numericFieldValues.put(
        ValueEnum.REQUIRED_RATE_OF_RETURN, 0f);
    numericFieldValues.put(
        ValueEnum.FIX_UP_COSTS, 0f);
    numericFieldValues.put(
        ValueEnum.CLOSING_COSTS, 0f);
    
    numericValues.add(numericFieldValues);
    textValues.add(textFieldValues);
  }

}

