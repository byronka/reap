package com.byronkatz.reap.general;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Log;

import com.byronkatz.reap.activity.GraphActivity;
import com.byronkatz.reap.calculations.GeneralCalculations;

public class DataController {

  //variable below is to hold the pointer to which set (division) of data we want.
  private Integer currentYearSelected;
  private Integer currentRowIndex;
  private static Integer currentDivisionForWriting = 0;
  private static Integer currentDivisionForReading = 0;
  private static DatabaseAdapter databaseAdapter;
  private static Map<Integer, Map<ValueEnum, Double>> numericValues;
  private static Map<ValueEnum, Double> numericMap;
  //below data structure holds a whole set of calculated values 
  //for each division of the progress slider
  private static Map<Integer, Map<Integer, Map<ValueEnum, Double>>> multiDivisionNumericValues;
  private static Map<ValueEnum, Double>[][] arrayMultiDivisionNumericCache;
  private static Map<ValueEnum, String> textValues;
  private static Set<ValueEnum> viewableDataTableRows;
  private static Boolean dataChanged;

  //DEFAULT_YEAR is for which year to store values that don't change per year.
  public static final Integer DEFAULT_YEAR = 0;
  public static final Integer DEFAULT_DIVISION = 0;
  public static final Integer CURRENT_MAX_NUM_OF_YEARS = 99 + 30;
  public static Resources resources;

  public static final Double EPSILON = 0.00001d;

  @SuppressWarnings("unchecked")
  public DataController(Context context, SharedPreferences sp, Resources resources) {

    DataController.resources = resources;
    //WORK AREA

    arrayMultiDivisionNumericCache = new Map[GraphActivity.DIVISIONS_OF_VALUE_SLIDER + 1][CURRENT_MAX_NUM_OF_YEARS];

    for (int i = 0; i < GraphActivity.DIVISIONS_OF_VALUE_SLIDER + 1; i++ ) {
      for (int j = 0; j < CURRENT_MAX_NUM_OF_YEARS; j++) {
        arrayMultiDivisionNumericCache[i][j] = new EnumMap<ValueEnum, Double>(ValueEnum.class);
      }
    }
    
//    Arrays.fill(arrayMultiDivisionNumericCache, new EnumMap<ValueEnum, Double>(ValueEnum.class));
    //WORK AREA
    
    databaseAdapter = new DatabaseAdapter(context);
    numericValues = new HashMap<Integer, Map<ValueEnum, Double>>();
    multiDivisionNumericValues = new HashMap<Integer, Map<Integer, Map<ValueEnum, Double>>>();
    textValues = new EnumMap<ValueEnum, String>(ValueEnum.class);
    setViewableDataTableRows(new HashSet<ValueEnum>());
    //datachanged is to tell graphactivity when it's time to recalculate
    dataChanged = true;
    loadFieldValues(sp);
    //set to -1 when the system start to flag that it is not set
    currentRowIndex = -1;
  }
  
  public static Resources getAppResources() {
    return resources;
  }

  public void loadFieldValues(SharedPreferences sp) {

    //either a string or not a string
    for (ValueEnum inputEnum : ValueEnum.values()) {
      if (inputEnum == ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS) {
        setValueAsDouble(inputEnum, Double.valueOf(sp.getString(inputEnum.name(), "360")));
      } else if (inputEnum.isSavedToDatabase() && (inputEnum.getType() != ValueEnum.ValueType.STRING)) {
        setValueAsDouble(inputEnum, Double.valueOf(sp.getString(inputEnum.name(), "0")));
      } else if (inputEnum.isSavedToDatabase() && (inputEnum.getType() == ValueEnum.ValueType.STRING)) {
        setValueAsString(inputEnum, sp.getString(inputEnum.name(), ""));
      }
    }
  }
  
  public void saveFieldValues(SharedPreferences sp) {
    
    SharedPreferences.Editor editor = sp.edit();
    editor.clear();
    //either a string or not a string
    for (ValueEnum inputEnum : ValueEnum.values()) {
      if (inputEnum.isSavedToDatabase() && (inputEnum.getType() != ValueEnum.ValueType.STRING)) {
        editor.putString(inputEnum.name(), String.valueOf(getValueAsDouble(inputEnum)));    
      } else if (inputEnum.isSavedToDatabase() && (inputEnum.getType() == ValueEnum.ValueType.STRING)) {
        editor.putString(inputEnum.name(), getValueAsString(inputEnum));
      }
    }
    
    editor.commit();
  }


  
//  public void setValueAsDouble(ValueEnum key, Double value) {
//
//    //get the current division
//    numericValues = multiDivisionNumericValues.get(DEFAULT_DIVISION);
//
//    //prime candidate for refactoring below.
//    /*
//     * check to see if this division's Map has been initialized.
//     * If not, create a new Map and sub-map for it.
//     */
//    if (numericValues == null) {
//      //if numericValues is null, then create it and its numeric map
//      numericMap = new EnumMap<ValueEnum, Double>(ValueEnum.class);
//      numericMapPut(numericMap, key, value);
//
//
//      numericValues = new HashMap<Integer, Map<ValueEnum, Double>>();
//      numericValues.put(DEFAULT_YEAR, numericMap);
//
//      multiDivisionNumericValues.put(DEFAULT_DIVISION, numericValues);
//    } else {
//      numericMap = numericValues.get(DEFAULT_YEAR);
//    }
//
//    if (numericMap == null) {
//      numericMap = new EnumMap<ValueEnum, Double>(ValueEnum.class);
//      numericMapPut(numericMap, key, value);
//
//      numericValues.put(DEFAULT_YEAR, numericMap);
//    } else {
//      numericMapPut(numericMap, key, value);
//    }
//  }

//  private void numericMapPut(Map<ValueEnum, Double> numericMap, 
//      ValueEnum key, Double value) {
//    //if the same value as what is already there, don't put the value
//    Double existingValue = numericMap.get(key);
//    
//    if ((existingValue == null) || (Math.abs(existingValue - value) > EPSILON)) {
//      dataChanged = true;
//      numericMap.put(key, value);
//    }
//  }
  
//  public void setValueAsDouble(ValueEnum key, Double value, Integer year) {
//
//    //TODO: add code to check that the year parameter is kosher
//
//    //get the current division
//    numericValues = multiDivisionNumericValues.get(currentDivisionForWriting);
//
//    //prime candidate for refactoring below.
//    /*
//     * check to see if this division's Map has been initialized.
//     * If not, create a new Map and sub-map for it.
//     */
//    if (numericValues == null) {
//      //if numericValues is null, then create it and its numeric map
//      numericMap = new EnumMap<ValueEnum, Double>(ValueEnum.class);
//      numericMap.put(key, value);
//
//      numericValues = new HashMap<Integer, Map<ValueEnum, Double>>();
//      numericValues.put(year, numericMap);
//
//      multiDivisionNumericValues.put(currentDivisionForWriting, numericValues);
//    } else {
//      numericMap = numericValues.get(year);
//    }
//
//    if (numericMap == null) {
//      numericMap = new EnumMap<ValueEnum, Double>(ValueEnum.class);
//      numericMap.put(key, value);
//      numericValues.put(year, numericMap);
//    } else {
//      numericMap.put(key, value);
//    }
//
//  }
  
  //WORK AREA
  
  public void setValueAsDouble(ValueEnum key, Double value) {
    //get the default division and year
    arrayMultiDivisionNumericCache[DEFAULT_DIVISION][DEFAULT_YEAR].put(key, value);

  }

  
  public void setValueAsDouble(ValueEnum key, Double value, Integer year) {
 
      arrayMultiDivisionNumericCache[currentDivisionForWriting][year].put(key, value);

  }

  public Double getValueAsDouble(ValueEnum key) {

    //get the default division and year
    Double returnValue = arrayMultiDivisionNumericCache[DEFAULT_DIVISION][DEFAULT_YEAR].get(key);
    if (returnValue == null){
      returnValue = 0d;
    }
    return returnValue;
  }
  
  public Double getValueAsDouble(ValueEnum key, Integer year) {
    
      //unpack the numericValues for this division
    Double returnValue = arrayMultiDivisionNumericCache[currentDivisionForReading][year].get(key);
    if (returnValue == null){
      returnValue = 0d;
    }
    return returnValue;
  }

  
  //WORK AREA ENDS

  public void setValueAsString(ValueEnum key, String value) {

    textValues.put(key, value);
  }

  public String getValueAsString(ValueEnum key) {

    //    Map<ValueEnum, String> m = textValues.get(DEFAULT_YEAR);
    String returnValue = textValues.get(key);
    return returnValue;
  }

//  public Double getValueAsDouble(ValueEnum key) {
//
//    //get the default division
//    numericValues = multiDivisionNumericValues.get(DEFAULT_DIVISION);
//
//    if (numericValues == null) {
//      return 0d;
//    }
//    Map<ValueEnum, Double> m = numericValues.get(DEFAULT_YEAR);
//    Double returnValue = m.get(key);
//    return returnValue;
//  }



//  public Double getValueAsDouble(ValueEnum key, Integer year) {
//
//    //unpack the numericValues for this division
//    numericValues = multiDivisionNumericValues.get(currentDivisionForReading);
//
//    if (numericValues == null) {
//      return 0d;
//    }
//    
//    //unpack the map for this year
//    Map<ValueEnum, Double> m = numericValues.get(year);
//    Double returnValue = m.get(key);
//    return returnValue;
//  }

  public Double[] getPlotPoints(ValueEnum graphKeyValue) {

    Double[] dataPoints;
    Double yValue;

    int yearsOfCompounding = getValueAsDouble(
        ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS).intValue() / 
        GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    
    int extraYears = getValueAsDouble(
        ValueEnum.EXTRA_YEARS).intValue();

    dataPoints = new Double[yearsOfCompounding + extraYears];
    for (int year = 0; year < (yearsOfCompounding + extraYears); year++) {
      yValue = getValueAsDouble(graphKeyValue, year + 1);
      dataPoints[year] = yValue;
//      Log.d(getClass().getName(), "year: " + year + " yValue: " + yValue + " grapKeyValue: " + graphKeyValue.name());
      
    }

    return dataPoints;
  }

  public int saveValues() {
    ContentValues cv = new ContentValues();

    numericValues = multiDivisionNumericValues.get(DEFAULT_DIVISION);
    numericMap = numericValues.get(DEFAULT_YEAR);

    for (Entry<ValueEnum, Double> m: numericMap.entrySet()) {
      if (m.getKey().isSavedToDatabase()) {
        String key = m.getKey().name();
        Double value = m.getValue();
        cv.put(key, value);
      }
    }

    for (Entry<ValueEnum, String> m: textValues.entrySet()) {
      if (m.getKey().isSavedToDatabase()) {
        String key = m.getKey().name();
        String value = m.getValue();
        cv.put(key, value);
      }
    }

    //insert into database and return the last rowindex
    return databaseAdapter.insertEntry(cv);
  }

  public Cursor getAllDatabaseValues() {
    Cursor cursor = databaseAdapter.getAllEntries();
    return cursor;
  }

  public void setCurrentDatabaseRow(int currentRowIndex) {
    this.currentRowIndex = currentRowIndex;
  }

  public int getCurrentDatabaseRow() {
    return currentRowIndex;
  }

  public void updateRow() {

    ContentValues cv = new ContentValues();

    numericValues = multiDivisionNumericValues.get(DEFAULT_DIVISION);
    numericMap = numericValues.get(DEFAULT_YEAR);

    for (Entry<ValueEnum, Double> m: numericMap.entrySet()) {
      if (m.getKey().isSavedToDatabase()) {
        String key = m.getKey().name();
        Double value = m.getValue();
        cv.put(key, value);
      }
    }

    for (Entry<ValueEnum, String> m: textValues.entrySet()) {
      if (m.getKey().isSavedToDatabase()) {
        String key = m.getKey().name();
        String value = m.getValue();
        cv.put(key, value);
      }
    }

    databaseAdapter.updateEntry((long)currentRowIndex, cv);
  }



  public void setCurrentData(ContentValues cv) {

    //either a string or not a string
    for (ValueEnum inputEnum : ValueEnum.values()) {
      if (inputEnum.isSavedToDatabase() && (inputEnum.getType() != ValueEnum.ValueType.STRING)) {
        setValueAsDouble(inputEnum, cv.getAsDouble(inputEnum.name()));
      } else if (inputEnum.isSavedToDatabase() && (inputEnum.getType() == ValueEnum.ValueType.STRING)) {
        setValueAsString(inputEnum, cv.getAsString(inputEnum.name()));
      }
    }
  }

  public boolean removeDatabaseEntry(int rowIndex) {
    boolean returnValue = databaseAdapter.removeEntry(rowIndex);
    return returnValue;
  }

  public Set<ValueEnum> getViewableDataTableRows() {
    return viewableDataTableRows;
  }

  public final void setViewableDataTableRows(Set<ValueEnum> viewableDataTableRows) {
    DataController.viewableDataTableRows = viewableDataTableRows;
  }

  public static void setCurrentDivisionForWriting (Integer currentDivisionForWriting) {
    DataController.currentDivisionForWriting = currentDivisionForWriting;
  }

  public static Integer getCurrentDivisionForReading() {
    return currentDivisionForReading;
  }

  //is this method ever used?
  public static void setCurrentDivisionForReading(
      Integer currentDivisionForReading) {
    DataController.currentDivisionForReading = currentDivisionForReading;
  }

  public static Boolean isDataChanged() {
    return dataChanged;
  }

  public static void setDataChanged(Boolean dataChanged) {
    DataController.dataChanged = dataChanged;
  }

  public Integer getCurrentYearSelected() {
    return currentYearSelected;
  }

  public void setCurrentYearSelected(Integer currentYearSelected) {
    this.currentYearSelected = currentYearSelected;
  }




}

