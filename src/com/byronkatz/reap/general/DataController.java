package com.byronkatz.reap.general;

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

import com.byronkatz.reap.calculations.GeneralCalculations;

public class DataController {

  //variable below is to hold the pointer to which set (division) of data we want.
  private Integer currentYearSelected;
  private Integer currentRowIndex;
  private static Integer currentDivisionForWriting = 0;
  private static Integer currentDivisionForReading = 0;
  private static DatabaseAdapter databaseAdapter;
  private static Map<Integer, Map<ValueEnum, Float>> numericValues;
  private static Map<ValueEnum, Float> numericMap;
  //below data structure holds a whole set of calculated values 
  //for each division of the progress slider
  private static Map<Integer, Map<Integer, Map<ValueEnum, Float>>> multiDivisionNumericValues;
  private static Map<ValueEnum, String> textValues;
  private static Set<ValueEnum> viewableDataTableRows;
  private static Boolean dataChanged;

  //DEFAULT_YEAR is for which year to store values that don't change per year.
  public static final Integer DEFAULT_YEAR = 1;
  public static final Integer DEFAULT_DIVISION = 0;
  public static Resources resources;

  public static final Float EPSILON = 0.00001f;

  public DataController(Context context, SharedPreferences sp, Resources resources) {

    DataController.resources = resources;
    databaseAdapter = new DatabaseAdapter(context);
    numericValues = new HashMap<Integer, Map<ValueEnum, Float>>();
    multiDivisionNumericValues = new HashMap<Integer, Map<Integer, Map<ValueEnum, Float>>>();
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
        setValueAsFloat(inputEnum, sp.getFloat(inputEnum.name(), 360f));
      } else if (inputEnum.isSavedToDatabase() && (inputEnum.getType() != ValueEnum.ValueType.STRING)) {
        setValueAsFloat(inputEnum, sp.getFloat(inputEnum.name(), 0f));
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
        editor.putFloat(inputEnum.name(), getValueAsFloat(inputEnum));    
      } else if (inputEnum.isSavedToDatabase() && (inputEnum.getType() == ValueEnum.ValueType.STRING)) {
        editor.putString(inputEnum.name(), getValueAsString(inputEnum));
      }
    }
    
    editor.commit();
  }

  public void setValueAsFloat(ValueEnum key, Float value) {

    //get the current division
    numericValues = multiDivisionNumericValues.get(DEFAULT_DIVISION);

    //prime candidate for refactoring below.
    /*
     * check to see if this division's Map has been initialized.
     * If not, create a new Map and sub-map for it.
     */
    if (numericValues == null) {
      //if numericValues is null, then create it and its numeric map
      numericMap = new EnumMap<ValueEnum, Float>(ValueEnum.class);
      numericMapPut(numericMap, key, value);


      numericValues = new HashMap<Integer, Map<ValueEnum, Float>>();
      numericValues.put(DEFAULT_YEAR, numericMap);

      multiDivisionNumericValues.put(DEFAULT_DIVISION, numericValues);
    } else {
      numericMap = numericValues.get(DEFAULT_YEAR);
    }

    if (numericMap == null) {
      numericMap = new EnumMap<ValueEnum, Float>(ValueEnum.class);
      numericMapPut(numericMap, key, value);

      numericValues.put(DEFAULT_YEAR, numericMap);
    } else {
      numericMapPut(numericMap, key, value);
    }
  }

  private void numericMapPut(Map<ValueEnum, Float> numericMap, 
      ValueEnum key, Float value) {
    //if the same value as what is already there, don't put the value
    Float existingValue = numericMap.get(key);
    
    if ((existingValue == null) || (Math.abs(existingValue - value) > EPSILON)) {
      dataChanged = true;
      numericMap.put(key, value);
    }
  }

  public void setValueAsFloat(ValueEnum key, Float value, Integer year) {

    //TODO: add code to check that the year parameter is kosher

    //get the current division
    numericValues = multiDivisionNumericValues.get(currentDivisionForWriting);

    //prime candidate for refactoring below.
    /*
     * check to see if this division's Map has been initialized.
     * If not, create a new Map and sub-map for it.
     */
    if (numericValues == null) {
      //if numericValues is null, then create it and its numeric map
      numericMap = new EnumMap<ValueEnum, Float>(ValueEnum.class);
      numericMap.put(key, value);

      numericValues = new HashMap<Integer, Map<ValueEnum, Float>>();
      numericValues.put(year, numericMap);

      multiDivisionNumericValues.put(currentDivisionForWriting, numericValues);
    } else {
      numericMap = numericValues.get(year);
    }

    if (numericMap == null) {
      numericMap = new EnumMap<ValueEnum, Float>(ValueEnum.class);
      numericMap.put(key, value);
      numericValues.put(year, numericMap);
    } else {
      numericMap.put(key, value);
    }

  }


  public void setValueAsString(ValueEnum key, String value) {

    textValues.put(key, value);
  }

  public String getValueAsString(ValueEnum key) {

    //    Map<ValueEnum, String> m = textValues.get(DEFAULT_YEAR);
    String returnValue = textValues.get(key);
    return returnValue;
  }

  public Float getValueAsFloat(ValueEnum key) {

    //get the default division
    numericValues = multiDivisionNumericValues.get(DEFAULT_DIVISION);

    Map<ValueEnum, Float> m = numericValues.get(DEFAULT_YEAR);
    Float returnValue = m.get(key);
    return returnValue;
  }


  public Float getValueAsFloat(ValueEnum key, Integer year) {

//    Log.d(getClass().getName(), "ValueEnum: " + key.toString() + " Year: " + year.toString());

    //unpack the numericValues for this division
    numericValues = multiDivisionNumericValues.get(currentDivisionForReading);

    //unpack the map for this year
    Map<ValueEnum, Float> m = numericValues.get(year);
    Float returnValue = m.get(key);
    return returnValue;
  }

  public Float[] getPlotPoints(ValueEnum graphKeyValue) {

    Float[] dataPoints;
    Float yValue;

    int yearsOfCompounding = getValueAsFloat(
        ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS).intValue() / 
        GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    
    int extraYears = getValueAsFloat(
        ValueEnum.EXTRA_YEARS).intValue();

    dataPoints = new Float[yearsOfCompounding + extraYears];
    for (int year = 0; year < (yearsOfCompounding + extraYears); year++) {
      yValue = getValueAsFloat(graphKeyValue, year + 1);
      dataPoints[year] = yValue;
//      Log.d(getClass().getName(), "year: " + year + " yValue: " + yValue + " grapKeyValue: " + graphKeyValue.name());
      
    }

    return dataPoints;
  }

  public int saveValues() {
    ContentValues cv = new ContentValues();

    numericValues = multiDivisionNumericValues.get(DEFAULT_DIVISION);
    numericMap = numericValues.get(DEFAULT_YEAR);

    for (Entry<ValueEnum, Float> m: numericMap.entrySet()) {
      if (m.getKey().isSavedToDatabase()) {
        String key = m.getKey().name();
        Float value = m.getValue();
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

    for (Entry<ValueEnum, Float> m: numericMap.entrySet()) {
      if (m.getKey().isSavedToDatabase()) {
        String key = m.getKey().name();
        Float value = m.getValue();
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
        setValueAsFloat(inputEnum, cv.getAsFloat(inputEnum.name()));
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

