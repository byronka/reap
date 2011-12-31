package com.byronkatz;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.EditText;

import com.byronkatz.ValueEnum.ValueType;

/**
 * 
 * @author byron
 *
 */
public class DataController {

  //variable below is to hold the pointer to which set (division) of data we want.
  private static Integer currentDivision;
  private static DatabaseAdapter databaseAdapter;
  private static Map<Integer, Map<ValueEnum, Float>> numericValues;
  private static Map<ValueEnum, Float> numericMap;
  //below data structure holds a whole set of calculated values 
  //for each division of the progress slider
  private static Map<Integer, Map<Integer, Map<ValueEnum, Float>>> multiDivisionNumericValues;
  private static Map<Integer, Map<ValueEnum, String>> textValues;
  private static Set<ValueEnum> viewableDataTableRows;

  //DEFAULT_YEAR is for which year to store values that don't change per year.
  public static final Integer DEFAULT_YEAR = 1;
  public static final Integer DEFAULT_DIVISION = 0;

  public DataController(Context context) {

    databaseAdapter = new DatabaseAdapter(context);
    numericValues = new HashMap<Integer, Map<ValueEnum, Float>>();
    multiDivisionNumericValues = new HashMap<Integer, Map<Integer, Map<ValueEnum, Float>>>();
    textValues = new HashMap<Integer, Map<ValueEnum, String>>();
    setViewableDataTableRows(new HashSet<ValueEnum>());
    loadFieldValues();
  }

  private void loadFieldValues() {

    numericMap = new HashMap<ValueEnum, Float> ();    
    Map<ValueEnum, String> textFieldValues = new HashMap<ValueEnum, String> ();

    numericMap.put(ValueEnum.TOTAL_PURCHASE_VALUE,0f);
    numericMap.put(ValueEnum.YEARLY_INTEREST_RATE, 0f);
    numericMap.put(ValueEnum.BUILDING_VALUE, 0f);
    numericMap.put(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS, 360f);
    numericMap.put(ValueEnum.INFLATION_RATE, 0f);
    numericMap.put(ValueEnum.DOWN_PAYMENT, 0f);
    textFieldValues.put(ValueEnum.STREET_ADDRESS, "1234 Anywhere Street");
    textFieldValues.put(ValueEnum.CITY, "Memphis");
    textFieldValues.put(ValueEnum.STATE_INITIALS, "Tennessee");
    numericMap.put(ValueEnum.ESTIMATED_RENT_PAYMENTS, 0f);
    numericMap.put(ValueEnum.REAL_ESTATE_APPRECIATION_RATE, 0f);
    numericMap.put(ValueEnum.YEARLY_HOME_INSURANCE, 0f);
    numericMap.put(ValueEnum.PROPERTY_TAX_RATE, 0f);
    numericMap.put(ValueEnum.LOCAL_MUNICIPAL_FEES, 0f);
    numericMap.put(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE, 0f);
    numericMap.put(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES, 0f);
    numericMap.put(ValueEnum.MARGINAL_TAX_RATE, 0f);
    numericMap.put(ValueEnum.SELLING_BROKER_RATE, 0f);
    numericMap.put(ValueEnum.GENERAL_SALE_EXPENSES, 0f);
    numericMap.put(ValueEnum.REQUIRED_RATE_OF_RETURN, 0f);
    numericMap.put(ValueEnum.FIX_UP_COSTS, 0f);
    numericMap.put(ValueEnum.CLOSING_COSTS, 0f);

    numericValues.put(DEFAULT_YEAR, numericMap);
    multiDivisionNumericValues.put(DEFAULT_DIVISION, numericValues);

    textValues.put(DEFAULT_YEAR, textFieldValues);
  }

  public void setValueAsFloat(ValueEnum key, Float value) {

    //get the default division
    numericValues = multiDivisionNumericValues.get(DEFAULT_DIVISION);
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

    //get the current division
    numericValues = multiDivisionNumericValues.get(currentDivision);

    //prime candidate for refactoring below.
    /*
     * check to see if this division's Map has been initialized.
     * If not, create a new Map and sub-map for it.
     */
    if (numericValues == null) {
      //if numericValues is null, then create it and its numeric map
      numericMap = new HashMap<ValueEnum, Float>();
      numericMap.put(key, value);

      numericValues = new HashMap<Integer, Map<ValueEnum, Float>>();
      numericValues.put(year, numericMap);

      multiDivisionNumericValues.put(currentDivision, numericValues);
    } else {
      numericMap = numericValues.get(year);
    }

    if (numericMap == null) {
      numericMap = new HashMap<ValueEnum, Float>();
      numericMap.put(key, value);
      numericValues.put(year, numericMap);
    } else {
      numericMap.put(key, value);
    }

  }

  public void setCurrentDivision(Integer currentDivision) {
    DataController.currentDivision = currentDivision;
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

  //Not sure what use this method is.  probably safe to remove.
  //  public void setValueAsString(ValueEnum key, String value, Integer year) {
  //
  //    //TODO: add code to check that the year parameter is kosher
  //
  //    Map<ValueEnum, String> textMap = textValues.get(year);
  //
  //    /*
  //     * check to see if this year's Map has been initialized.
  //     * If not, create a new Map for it.
  //     */
  //    if (textMap == null) {
  //      textMap = new HashMap<ValueEnum, String>();
  //      textMap.put(key, value);
  //      textValues.put(year, textMap);
  //    } else {
  //      textMap.put(key, value);
  //    }
  //  }

  public String getValueAsString(ValueEnum key) {

    Map<ValueEnum, String> m = textValues.get(DEFAULT_YEAR);
    String returnValue = m.get(key);
    return returnValue;
  }

  public Float getValueAsFloat(ValueEnum key) {

    //get the default division
    numericValues = multiDivisionNumericValues.get(DEFAULT_DIVISION);

    Map<ValueEnum, Float> m = numericValues.get(DEFAULT_YEAR);
    Float returnValue = m.get(key);
    return returnValue;
  }

  //  public Float getValueAsFloat(ValueEnum key, Integer year) {
  //    Map<ValueEnum, Float> m = numericValues.get(year);
  //    Float returnValue = m.get(key);
  //    return returnValue;
  //  }

  public Float getValueAsFloat(ValueEnum key, Integer year) {

    //unpack the numericValues for this division
    numericValues = multiDivisionNumericValues.get(currentDivision);

    //unpack the map for this year
    Map<ValueEnum, Float> m = numericValues.get(year);
    Float returnValue = m.get(key);
    return returnValue;
  }

  public Map<Integer, Float> getPlotPoints(ValueEnum graphKeyValue) {
    Map<Integer, Float> dataPoints = new HashMap<Integer, Float>();
    Float yValue;

    int yearsOfCompounding = getValueAsFloat(
        ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS).intValue() / CalculatedVariables.NUM_OF_MONTHS_IN_YEAR;

    for (int year = 1; year <= yearsOfCompounding; year++) {
      yValue = getValueAsFloat(graphKeyValue, year);
      dataPoints.put(year, yValue);
    }

    return dataPoints;
  }

  public void saveValues() {
    ContentValues cv = new ContentValues();
    // get year 1

    Map<ValueEnum, Float> numericMap = numericValues.get(DEFAULT_YEAR);
    for (Entry<ValueEnum, Float> m: numericMap.entrySet()) {
      if (m.getKey().isSavedToDatabase()) {
        String key = m.getKey().name();
        Float value = m.getValue();
        cv.put(key, value);
      }
    }

    Map<ValueEnum, String> textMap = textValues.get(DEFAULT_YEAR);
    for (Entry<ValueEnum, String> m: textMap.entrySet()) {
      if (m.getKey().isSavedToDatabase()) {
        String key = m.getKey().name();
        String value = m.getValue();
        cv.put(key, value);
      }
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
    case INTEGER:
      editText.setSelection(0, textLength);
      break;
    case STRING:
      editText.setSelection(0, textLength);
      break;
    default:
      System.err.println("shouldn't get here in setSelectionOnView");
    }
  }

  public void setCurrentData(ContentValues cv) {
//    Map<ValueEnum, String> textMap = textValues.get(DEFAULT_YEAR);
//    Map<ValueEnum, Float> numericMap = numericValues.get(DEFAULT_YEAR);

    for (ValueEnum inputEnum : ValueEnum.values()) {
      if (inputEnum.isSavedToDatabase() && (inputEnum.getType() != ValueEnum.ValueType.STRING)) {
        setValueAsFloat(inputEnum,
            cv.getAsFloat(inputEnum.name()));
      } else if (inputEnum.isSavedToDatabase() && (inputEnum.getType() == ValueEnum.ValueType.STRING)) {
        setValueAsString(inputEnum,
            cv.getAsString(inputEnum.name()));
      }
    }
// 
//    textMap.put(
//        ValueEnum.STREET_ADDRESS, 
//        cv.getAsString(ValueEnum.STREET_ADDRESS.name()));
//    textMap.put(
//        ValueEnum.CITY, 
//        cv.getAsString(ValueEnum.CITY.name()));
//    textMap.put(
//        ValueEnum.STATE_INITIALS, 
//        cv.getAsString(ValueEnum.STATE_INITIALS.name()));
//    
//    numericMap.put(
//        ValueEnum.TOTAL_PURCHASE_VALUE,
//        cv.getAsFloat(ValueEnum.TOTAL_PURCHASE_VALUE.name()));
//    numericMap.put(
//        ValueEnum.YEARLY_INTEREST_RATE, 
//        cv.getAsFloat(ValueEnum.YEARLY_INTEREST_RATE.name()));
//    numericMap.put(
//        ValueEnum.BUILDING_VALUE, 
//        cv.getAsFloat(ValueEnum.BUILDING_VALUE.name()));
//    numericMap.put(
//        ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS, 360f);
//    numericMap.put(
//        ValueEnum.INFLATION_RATE, 
//        cv.getAsFloat(ValueEnum.INFLATION_RATE.name()));
//    numericMap.put(
//        ValueEnum.DOWN_PAYMENT, 
//        cv.getAsFloat(ValueEnum.DOWN_PAYMENT.name()));

//    numericMap.put(
//        ValueEnum.ESTIMATED_RENT_PAYMENTS, 
//        cv.getAsFloat(ValueEnum.ESTIMATED_RENT_PAYMENTS.name()));
//    numericMap.put(
//        ValueEnum.REAL_ESTATE_APPRECIATION_RATE, 
//        cv.getAsFloat(ValueEnum.REAL_ESTATE_APPRECIATION_RATE.name()));
//    numericMap.put(
//        ValueEnum.YEARLY_HOME_INSURANCE, 
//        cv.getAsFloat(ValueEnum.YEARLY_HOME_INSURANCE.name()));
//    numericMap.put(
//        ValueEnum.PROPERTY_TAX_RATE, 
//        cv.getAsFloat(ValueEnum.PROPERTY_TAX_RATE.name()));
//    numericMap.put(
//        ValueEnum.LOCAL_MUNICIPAL_FEES, 
//        cv.getAsFloat(ValueEnum.LOCAL_MUNICIPAL_FEES.name()));
//    numericMap.put(
//        ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE, 
//        cv.getAsFloat(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE.name()));
//    numericMap.put(
//        ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES, 
//        cv.getAsFloat(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES.name()));
//    numericMap.put(
//        ValueEnum.MARGINAL_TAX_RATE, 
//        cv.getAsFloat(ValueEnum.MARGINAL_TAX_RATE.name()));
//    numericMap.put(
//        ValueEnum.SELLING_BROKER_RATE, 
//        cv.getAsFloat(ValueEnum.SELLING_BROKER_RATE.name()));
//    numericMap.put(
//        ValueEnum.GENERAL_SALE_EXPENSES, 
//        cv.getAsFloat(ValueEnum.GENERAL_SALE_EXPENSES.name()));
//    numericMap.put(
//        ValueEnum.REQUIRED_RATE_OF_RETURN, 
//        cv.getAsFloat(ValueEnum.REQUIRED_RATE_OF_RETURN.name()));
//    numericMap.put(
//        ValueEnum.FIX_UP_COSTS, 
//        cv.getAsFloat(ValueEnum.FIX_UP_COSTS.name()));
//    numericMap.put(
//        ValueEnum.CLOSING_COSTS, 
//        cv.getAsFloat(ValueEnum.CLOSING_COSTS.name()));

//    numericValues.put(DEFAULT_YEAR, numericMap);
//    textValues.put(DEFAULT_YEAR, textMap);
  }

  public boolean removeDatabaseEntry(int rowIndex) {
    boolean returnValue = databaseAdapter.removeEntry(rowIndex);
    return returnValue;
  }

  public Set<ValueEnum> getViewableDataTableRows() {
    return viewableDataTableRows;
  }

  public void setViewableDataTableRows(Set<ValueEnum> viewableDataTableRows) {
    DataController.viewableDataTableRows = viewableDataTableRows;
  }




}

