package com.byronkatz;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.byronkatz.ValueEnum.ValueType;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.EditText;

public class DataController {

  private DatabaseAdapter databaseAdapter;
  private Map<Integer, Map<ValueEnum, Float>> numericValues;
  private Map<Integer, Map<ValueEnum, String>> textValues;
  private static final Integer DEFAULT_YEAR = 1;

  public DataController(Context context) {

    databaseAdapter = new DatabaseAdapter(context);
    numericValues = new HashMap<Integer, Map<ValueEnum, Float>>();
    textValues = new HashMap<Integer, Map<ValueEnum, String>>();

    loadFieldValues();
  }

  private void loadFieldValues() {

    Map<ValueEnum, Float> numericFieldValues = new HashMap<ValueEnum, Float> ();    
    Map<ValueEnum, String> textFieldValues = new HashMap<ValueEnum, String> ();

    numericFieldValues.put(ValueEnum.TOTAL_PURCHASE_VALUE,0f);
    numericFieldValues.put(ValueEnum.YEARLY_INTEREST_RATE, 0f);
    numericFieldValues.put(ValueEnum.BUILDING_VALUE, 0f);
    numericFieldValues.put(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS, 360f);
    numericFieldValues.put(ValueEnum.INFLATION_RATE, 0f);
    numericFieldValues.put(ValueEnum.PRIMARY_MORTGAGE_INSURANCE_RATE, 0f);
    numericFieldValues.put(ValueEnum.DOWN_PAYMENT, 0f);
    textFieldValues.put(ValueEnum.STREET_ADDRESS, "1234 Anywhere Street");
    textFieldValues.put(ValueEnum.CITY, "Memphis");
    textFieldValues.put(ValueEnum.STATE_INITIALS, "Tennessee");
    numericFieldValues.put(ValueEnum.ESTIMATED_RENT_PAYMENTS, 0f);
    numericFieldValues.put(ValueEnum.REAL_ESTATE_APPRECIATION_RATE, 0f);
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

    numericValues.put(DEFAULT_YEAR, numericFieldValues);
    textValues.put(DEFAULT_YEAR, textFieldValues);
  }

  public void setValueAsFloat(ValueEnum key, Float value) {

    //get the Map for this year
    Map<ValueEnum, Float> numericMap = numericValues.get(DEFAULT_YEAR);

    /*
     * this year has always been initialized by default, 
     * so just go ahead and add the value.
     */
    numericMap.put(key, value);
  }

  public void setValueAsFloat(ValueEnum key, Float value, Integer year) {

    //TODO: add code to check that the year parameter is kosher

    Map<ValueEnum, Float> numericMap = numericValues.get(year);
    
    /*
     * check to see if this year's Map has been initialized.
     * If not, create a new Map for it.
     */
    if (numericMap == null) {
      numericMap = new HashMap<ValueEnum, Float>();
      numericMap.put(key, value);
      numericValues.put(year, numericMap);
    } else {
      numericMap.put(key, value);      
    }
  }

  public void setValueAsString(ValueEnum key, String value) {

    //get the Map for this year
    Map<ValueEnum, String> textMap = textValues.get(DEFAULT_YEAR);

    /*
     * this year has always been initialized by default, 
     * so just go ahead and add the value.
     */
    textMap.put(key, value);
  }

  public void setValueAsString(ValueEnum key, String value, Integer year) {

    //TODO: add code to check that the year parameter is kosher

    Map<ValueEnum, String> textMap = textValues.get(year);
    
    /*
     * check to see if this year's Map has been initialized.
     * If not, create a new Map for it.
     */
    if (textMap == null) {
      textMap = new HashMap<ValueEnum, String>();
      textMap.put(key, value);
      textValues.put(year, textMap);
    } else {
      textMap.put(key, value);
    }
  }

  public String getValueAsString(ValueEnum key) {

    Map<ValueEnum, String> m = textValues.get(DEFAULT_YEAR);
    String returnValue = m.get(key);
    return returnValue;
  }

  public Float getValueAsFloat(ValueEnum key) {
    Map<ValueEnum, Float> m = numericValues.get(DEFAULT_YEAR);
    Float returnValue = m.get(key);
    return returnValue;
  }

  public Float getValueAsFloat(ValueEnum key, Integer year) {
    Map<ValueEnum, Float> m = numericValues.get(year);
    Float returnValue = m.get(key);
    return returnValue;
  }

  public void saveValues() {
    ContentValues cv = new ContentValues();
    // get year 1
    //TODO: This next 5 lines may be a problem, since it includes calc
    //values which don't go in the database.  Check.
    Map<ValueEnum, Float> numericMap = numericValues.get(DEFAULT_YEAR);
    for (Entry<ValueEnum, Float> m: numericMap.entrySet()) {
      String key = m.getKey().name();
      Float value = m.getValue();
      cv.put(key, value);
    }

    Map<ValueEnum, String> textMap = textValues.get(DEFAULT_YEAR);
    for (Entry<ValueEnum, String> m: textMap.entrySet()) {
      String key = m.getKey().name();
      String value = m.getValue();
      cv.put(key, value);
    }
    databaseAdapter.insertEntry(cv);
  }

  public Cursor getAllDatabaseValues() {
    Cursor cursor = databaseAdapter.getAllEntries();
    return cursor;
  }

  public static void setSelectionOnView(View v, ValueType valueType) {
    EditText editText = (EditText) v;
    //we'll use textInEditText to measure the string for the selection
    String textInEditText = editText.getText().toString();
    int textLength = textInEditText.length();
    
    switch (valueType) {
    case CURRENCY:
      editText.setSelection(1, textLength);
      break;
    case PERCENTAGE:
      editText.setSelection(0, textLength - 1);
      break;
    case STRING:
      editText.setSelection(0, textLength);
      break;
      default:
        System.err.println("shouldn't get here in setSelectionOnView");
    }
  }
  
  public void setCurrentData(ContentValues cv) {

    Map<ValueEnum, Float> numericFieldValues = new HashMap<ValueEnum, Float> ();    
    Map<ValueEnum, String> textFieldValues = new HashMap<ValueEnum, String> ();

    numericFieldValues.put(
        ValueEnum.TOTAL_PURCHASE_VALUE,
        cv.getAsFloat(ValueEnum.TOTAL_PURCHASE_VALUE.name()));
    numericFieldValues.put(
        ValueEnum.YEARLY_INTEREST_RATE, 
        cv.getAsFloat(ValueEnum.YEARLY_INTEREST_RATE.name()));
    numericFieldValues.put(
        ValueEnum.BUILDING_VALUE, 
        cv.getAsFloat(ValueEnum.BUILDING_VALUE.name()));
    numericFieldValues.put(
        ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS, 360f);
    numericFieldValues.put(
        ValueEnum.INFLATION_RATE, 
        cv.getAsFloat(ValueEnum.INFLATION_RATE.name()));
    numericFieldValues.put(
        ValueEnum.PRIMARY_MORTGAGE_INSURANCE_RATE, 
        cv.getAsFloat(ValueEnum.PRIMARY_MORTGAGE_INSURANCE_RATE.name()));
    numericFieldValues.put(
        ValueEnum.DOWN_PAYMENT, 
        cv.getAsFloat(ValueEnum.DOWN_PAYMENT.name()));
    textFieldValues.put(
        ValueEnum.STREET_ADDRESS, 
        cv.getAsString(ValueEnum.STREET_ADDRESS.name()));
    textFieldValues.put(
        ValueEnum.CITY, 
        cv.getAsString(ValueEnum.CITY.name()));
    textFieldValues.put(
        ValueEnum.STATE_INITIALS, 
        cv.getAsString(ValueEnum.STATE_INITIALS.name()));
    numericFieldValues.put(
        ValueEnum.ESTIMATED_RENT_PAYMENTS, 
        cv.getAsFloat(ValueEnum.ESTIMATED_RENT_PAYMENTS.name()));
    numericFieldValues.put(
        ValueEnum.REAL_ESTATE_APPRECIATION_RATE, 
        cv.getAsFloat(ValueEnum.REAL_ESTATE_APPRECIATION_RATE.name()));
    numericFieldValues.put(
        ValueEnum.YEARLY_HOME_INSURANCE, 
        cv.getAsFloat(ValueEnum.YEARLY_HOME_INSURANCE.name()));
    numericFieldValues.put(
        ValueEnum.PROPERTY_TAX_RATE, 
        cv.getAsFloat(ValueEnum.PROPERTY_TAX_RATE.name()));
    numericFieldValues.put(
        ValueEnum.LOCAL_MUNICIPAL_FEES, 
        cv.getAsFloat(ValueEnum.LOCAL_MUNICIPAL_FEES.name()));
    numericFieldValues.put(
        ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE, 
        cv.getAsFloat(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE.name()));
    numericFieldValues.put(
        ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES, 
        cv.getAsFloat(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES.name()));
    numericFieldValues.put(
        ValueEnum.MARGINAL_TAX_RATE, 
        cv.getAsFloat(ValueEnum.MARGINAL_TAX_RATE.name()));
    numericFieldValues.put(
        ValueEnum.SELLING_BROKER_RATE, 
        cv.getAsFloat(ValueEnum.SELLING_BROKER_RATE.name()));
    numericFieldValues.put(
        ValueEnum.GENERAL_SALE_EXPENSES, 
        cv.getAsFloat(ValueEnum.GENERAL_SALE_EXPENSES.name()));
    numericFieldValues.put(
        ValueEnum.REQUIRED_RATE_OF_RETURN, 
        cv.getAsFloat(ValueEnum.REQUIRED_RATE_OF_RETURN.name()));
    numericFieldValues.put(
        ValueEnum.FIX_UP_COSTS, 
        cv.getAsFloat(ValueEnum.FIX_UP_COSTS.name()));
    numericFieldValues.put(
        ValueEnum.CLOSING_COSTS, 
        cv.getAsFloat(ValueEnum.CLOSING_COSTS.name()));

    numericValues.put(DEFAULT_YEAR, numericFieldValues);
    textValues.put(DEFAULT_YEAR, textFieldValues);
  }

}

