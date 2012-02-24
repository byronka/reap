package com.byronkatz.reap.general;

import java.util.Arrays;
import java.util.EnumMap;
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
//  private static Map<Integer, Map<ValueEnum, Double>> numericValues;
  private static Map<ValueEnum, Double> inputMap;
  //below data structure holds a whole set of calculated values 
  //for each division of the progress slider
  private static Map<ValueEnum, Double>[][] arrayMultiDivisionNumericCache;
  private static Map<ValueEnum, String> textValues;
  private static Set<ValueEnum> viewableDataTableRows;
  private static Boolean dataChanged;

  //DEFAULT_YEAR is for which year to store values that don't change per year.
  public static final Integer DEFAULT_YEAR = 0;
  public static final Integer DEFAULT_DIVISION = 0;
  public static final Integer CURRENT_MAX_NUM_OF_YEARS = 99 + 30 + 1;
  public static Resources resources;

  public static final Double EPSILON = 0.00001d;

  @SuppressWarnings("unchecked")
  public DataController(Context context, SharedPreferences sp, Resources resources) {

    DataController.resources = resources;
    //WORK AREA

    inputMap = new EnumMap<ValueEnum, Double>(ValueEnum.class);
    arrayMultiDivisionNumericCache = new Map[GraphActivity.DIVISIONS_OF_VALUE_SLIDER + 1][CURRENT_MAX_NUM_OF_YEARS];

    for (int i = 0; i < GraphActivity.DIVISIONS_OF_VALUE_SLIDER + 1; i++ ) {
      for (int j = 0; j < CURRENT_MAX_NUM_OF_YEARS; j++) {
        arrayMultiDivisionNumericCache[i][j] = new EnumMap<ValueEnum, Double>(ValueEnum.class);
      }
    }
    
//    Arrays.fill(arrayMultiDivisionNumericCache, new EnumMap<ValueEnum, Double>(ValueEnum.class));
    //WORK AREA
    
    databaseAdapter = new DatabaseAdapter(context);
//    numericValues = new HashMap<Integer, Map<ValueEnum, Double>>();
//    multiDivisionNumericValues = new HashMap<Integer, Map<Integer, Map<ValueEnum, Double>>>();
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


  public void setValueAsDouble(ValueEnum key, Double value) {
    setDataChanged(true);
    inputMap.put(key, value);
  }

  public void setValueAsDouble(ValueEnum key, Double value, Integer year) {
 
      arrayMultiDivisionNumericCache[currentDivisionForWriting][year].put(key, value);
  }
  
  public Double getValueAsDouble(ValueEnum key) {

    //get the default division and year
    Double returnValue = inputMap.get(key);
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


  public void setValueAsString(ValueEnum key, String value) {

    textValues.put(key, value);
  }

  public String getValueAsString(ValueEnum key) {

    //    Map<ValueEnum, String> m = textValues.get(DEFAULT_YEAR);
    String returnValue = textValues.get(key);
    return returnValue;
  }

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
      
    }

    return dataPoints;
  }

  public int saveValues() {
    ContentValues cv = new ContentValues();

    //take numeric values from user input
    for (Entry<ValueEnum, Double> m: inputMap.entrySet()) {
      if (m.getKey().isSavedToDatabase()) {
        String key = m.getKey().name();
        Double value = m.getValue();
        cv.put(key, value);
      }
    }
    
    //take string values from user input
    for (Entry<ValueEnum, String> m: textValues.entrySet()) {
      if (m.getKey().isSavedToDatabase()) {
        String key = m.getKey().name();
        String value = m.getValue();
        cv.put(key, value);
      }
    }

    //take the current year's values for ATCF, ATER, NPV, MIRR, CRPV and CRCV, pop those in
    String[] calcEnumsForDatabase = { ValueEnum.ATCF.name(),
                                    ValueEnum.ATER.name(),
                                    ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN.name(),
                                    ValueEnum.CAP_RATE_ON_PROJECTED_VALUE.name(),
                                    ValueEnum.CAP_RATE_ON_PURCHASE_VALUE.name(),
                                    ValueEnum.NPV.name()
    };
    Integer year = getCurrentYearSelected();
    if (year == null) {
      year = 0;
    }
    Log.d("DataController", "year: " + year  + " currentDivisionForReading: " + currentDivisionForReading);
    Map<ValueEnum, Double> calcMap = arrayMultiDivisionNumericCache[currentDivisionForReading][year];

    for (Entry<ValueEnum, Double> m: calcMap.entrySet()) {
      if (Arrays.asList(calcEnumsForDatabase).contains(m.getKey().name())) {
        String key = m.getKey().name();
        Double value = m.getValue();
        cv.put(key, value);
      }
    }
    
    //Put year into database
    cv.put(DatabaseAdapter.YEAR_VALUE, getCurrentYearSelected());
    
    //insert into database and return the last rowindex
    Integer rowIndex = databaseAdapter.insertEntry(cv);
    return rowIndex;
  }
  
  public void updateRow() {

    ContentValues cv = new ContentValues();

    //take numeric values from user input
    for (Entry<ValueEnum, Double> m: inputMap.entrySet()) {
      if (m.getKey().isSavedToDatabase()) {
        String key = m.getKey().name();
        Double value = m.getValue();
        cv.put(key, value);
      }
    }

    //take string values from user input
    for (Entry<ValueEnum, String> m: textValues.entrySet()) {
      if (m.getKey().isSavedToDatabase()) {
        String key = m.getKey().name();
        String value = m.getValue();
        cv.put(key, value);
      }
    }
    
    //take the current year's values for ATCF, ATER, NPV, MIRR, CRPV and CRCV, pop those in
    String[] calcEnumsForDatabase = { ValueEnum.ATCF.name(),
                                    ValueEnum.ATER.name(),
                                    ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN.name(),
                                    ValueEnum.CAP_RATE_ON_PROJECTED_VALUE.name(),
                                    ValueEnum.CAP_RATE_ON_PURCHASE_VALUE.name(),
                                    ValueEnum.NPV.name()
    };
    Integer year = getCurrentYearSelected();
    Map<ValueEnum, Double> calcMap = arrayMultiDivisionNumericCache[currentDivisionForReading][year];

    for (Entry<ValueEnum, Double> m: calcMap.entrySet()) {
      if (Arrays.asList(calcEnumsForDatabase).contains(m.getKey().name())) {
        String key = m.getKey().name();
        Double value = m.getValue();
        cv.put(key, value);
      }
    }

    //Put year into database
    cv.put(DatabaseAdapter.YEAR_VALUE, getCurrentYearSelected());
    
    databaseAdapter.updateEntry((long)currentRowIndex, cv);
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


/**
 * This takes values from a ContentValues and inserts them into the active cache
 * @param cv
 */
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

