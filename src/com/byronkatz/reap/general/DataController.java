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

  public DataController(Context context, SharedPreferences sp, Resources resources) {

    DataController.resources = resources;

    inputMap = new EnumMap<ValueEnum, Double>(ValueEnum.class);

    databaseAdapter = new DatabaseAdapter(context);
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

  /**
   * This method gets called whenever the system is about to recalculate.
   * It should not re-init if the number of years is the same.
   * @param years number of years that will be calculated.  Sets the proper size of arry.
   */
  @SuppressWarnings("unchecked")
  public void initNumericCache(int years) {

    if (arrayMultiDivisionNumericCache != null) {
      if ((arrayMultiDivisionNumericCache.length == (GraphActivity.DIVISIONS_OF_VALUE_SLIDER + 1)) &&
          arrayMultiDivisionNumericCache[0].length == (years + 1)) {
        //bail if the cache is non-null and already initialized to the right size.
        return;
      }

    }

    arrayMultiDivisionNumericCache = new Map[GraphActivity.DIVISIONS_OF_VALUE_SLIDER + 1][years + 1];
    
    for (int i = 0; i < GraphActivity.DIVISIONS_OF_VALUE_SLIDER + 1; i++ ) {
      for (int j = 0; j < (years + 1); j++) {
        arrayMultiDivisionNumericCache[i][j] = new EnumMap<ValueEnum, Double>(ValueEnum.class);
      }
    }
  }

  /**
   * This method is used when the user leaves the app for other things.  The app should
   * not continue to hold onto memory it does not need.
   */
  public void nullifyNumericCache() {
    arrayMultiDivisionNumericCache = null;
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


  /**
   * This method is used to change the input values, set by the user.  None of these values are
   * calculated by the system
   * @param key the ValueEnum which is used to determine the name of the value
   * @param value the numeric value associated with the particular input
   */
  public void setValueAsDouble(ValueEnum key, Double value) {

    //only add the value and set data changed if that number isn't already there
    if (inputMap.get(key) == null || ! inputMap.get(key).equals(value)) {
      setDataChanged(true);
      inputMap.put(key, value);
    }



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

    cv = placeCalcValueInContentValues(cv);

    //Put year into database
    cv = placeYearInDatabase(cv);
    
    //insert into database and return the last rowindex
    Integer rowIndex = databaseAdapter.insertEntry(cv);
    return rowIndex;
  }

  private ContentValues placeYearInDatabase(ContentValues cv) {
    if (getCurrentYearSelected() == null) {
      cv.put(DatabaseAdapter.YEAR_VALUE, 0);
    } else {
      cv.put(DatabaseAdapter.YEAR_VALUE, getCurrentYearSelected());
    }

    return cv;
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

    cv = placeCalcValueInContentValues(cv);

    //Put year into database
    cv = placeYearInDatabase(cv);

    databaseAdapter.updateEntry((long)currentRowIndex, cv);
  }

  /**
   * gets the calculated values for the currently selected year and stores in database
   * @param cv
   * @return
   */
  private ContentValues placeCalcValueInContentValues(ContentValues cv) {
    //do the following ONLY if the values have been calculated.  If not, set as zero

    //take the current year's values for ATCF, ATER, NPV, MIRR, CRPV and CRCV, pop those in
    String[] calcEnumsForDatabase = { 
        ValueEnum.ATCF.name(),
        ValueEnum.ATER.name(),
        ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN.name(),
        ValueEnum.CAP_RATE_ON_PROJECTED_VALUE.name(),
        ValueEnum.CAP_RATE_ON_PURCHASE_VALUE.name(),
        ValueEnum.NPV.name()
    };
    //first let's set the values to zero, in case we bail later.  
    //If we don't bail, they get overwritten
    for (String s : calcEnumsForDatabase) {
      cv.put(s, 0.0d);
    }

    Integer year = getCurrentYearSelected();

    //first chance to bail - is year null?
    if (year == null) {return cv;}

    //second chance to bail - is currentDivisionForReading between 0 and max?
    if (!(currentDivisionForReading >= 0 && currentDivisionForReading < GraphActivity.DIVISIONS_OF_VALUE_SLIDER)) {
      return cv;
    }

    //third chance to bail - is dataChanged true?
    if (dataChanged) {
      return cv;
    }

    Map<ValueEnum, Double> calcMap = arrayMultiDivisionNumericCache[currentDivisionForReading][year];

    //This will loop through the entries in the map we have for this year and division.
    //if it does not find a mapping, it merely keeps going, and leaves it 0 in that slot.
    for (Entry<ValueEnum, Double> m: calcMap.entrySet()) {
      if (Arrays.asList(calcEnumsForDatabase).contains(m.getKey().name())) {
        String key = m.getKey().name();
        Double value = m.getValue();

        //we don't want to put in a null value.
        if (value == null) {
          cv.put(key, 0d);
        } else {
          cv.put(key, value);
        }
      }
    }

    return cv;
  }

  public Cursor getAllDatabaseValues(String sorterText) {
    Cursor cursor = databaseAdapter.getAllEntries(sorterText);
    
    return cursor;
  }

  /**
   * Sets the stored row index for use for data operations
   * @param currentRowIndex the index number for the row in the database
   */
  public void setCurrentDatabaseRow(int currentRowIndex) {
    this.currentRowIndex = currentRowIndex;
  }

  /**
   * Returns the index number for the row in the database, under the column _id
   * @return
   */
  public int getCurrentDatabaseRow() {
    return currentRowIndex;
  }


  /**
   * This takes values from a ContentValues and inserts them into the active cache.
   * 
   * Used to load values from the database into the active cache
   * @param cv the ContentValues (basically a hashmap) which is a row from the database
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

