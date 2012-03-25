package com.byronkatz.reap.general;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Log;

import com.byronkatz.reap.calculations.CalcValueGettable;
import com.byronkatz.reap.calculations.Calculations;

public class DataController implements DataManager {

  //variable below is to hold the pointer to which set (division) of data we want.
  private Integer currentYearSelected;
  private Integer currentRowIndex;
  private DatabaseAdapter databaseAdapter;
  private Map<ValueEnum, Double> inputMap;
  private Map<ValueEnum, String> textValues;
  private Set<ValueEnum> viewableDataTableRows;
  public Resources resources;

  
  private Map<ValueEnum, CalcValueGettable> calcValuePointers;
  private Calculations calculations;
  public final Double EPSILON = 0.00001d;

  public DataController(Context context, SharedPreferences sp) {

    inputMap = new EnumMap<ValueEnum, Double>(ValueEnum.class);
    calcValuePointers = new EnumMap<ValueEnum, CalcValueGettable>(ValueEnum.class);
    calculations = new Calculations();

    databaseAdapter = new DatabaseAdapter(context);
    textValues = new EnumMap<ValueEnum, String>(ValueEnum.class);
    setViewableDataTableRows(new HashSet<ValueEnum>());
    loadFieldValues(sp);
    //set to -1 when the system start to flag that it is not set
    currentRowIndex = -1;
    
  }

  public void calculationsSetValues() {
    calculations.setValues(this);
  }
  
    /**
     * this method is meant as a callback for the actual calculation objects
     * to add themselves to the map of functions.  
     */
  public void addCalcValuePointers(ValueEnum valueEnum, CalcValueGettable valueStorage) {
    calcValuePointers.put(valueEnum, valueStorage);
  }

  /**
   * using this method to get a calculated value actually causes the calculation
   * method in question to run at that time.
   */
  @Override
  public double getCalcValue(ValueEnum valueEnum, int compoundingPeriod) {
//    Thread.dumpStack();
//    Log.d("call " + getCalcCalls++, "calculating " + valueEnum.name() + " " + compoundingPeriod);
    if (calcValuePointers.get(valueEnum) != null) {
      return calcValuePointers.get(valueEnum).getValue(compoundingPeriod);
    } else {
      return 0d;
    }
  }

  /**
   * This method is used to insert user values into the user value map
   *
   */
  @Override
  public void putInputValue(double value, ValueEnum valueEnum) {
    inputMap.put(valueEnum, value);
  }

  @Override
  public double getInputValue(ValueEnum valueEnum) {

    if (inputMap.get(valueEnum) == null) {
      return 0d;
    } else {
      return inputMap.get(valueEnum);
    }
  }

  /**
   * gets a sharedPreferences from RealEstateAnalysisProcessorApplication and loads those values
   * @param sp
   */
  public void loadFieldValues(SharedPreferences sp) {

    //either a string or not a string
    //this method tries to pull values from the Shared Preferences object.  If it finds nothing
    //there, it sets defaults, per the if-then below.
    for (ValueEnum inputEnum : ValueEnum.values()) {
      if (inputEnum == ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS) {
        putInputValue(Double.valueOf(sp.getString(inputEnum.name(), "360")), inputEnum);
      } else if (inputEnum.isSavedToDatabase() && (inputEnum.getType() != ValueEnum.ValueType.STRING)) {
        putInputValue(Double.valueOf(sp.getString(inputEnum.name(), "0")), inputEnum );
      } else if (inputEnum.isSavedToDatabase() && (inputEnum.getType() == ValueEnum.ValueType.STRING)) {
        setValueAsString(inputEnum, sp.getString(inputEnum.name(), ""));
      }
    }
  }
  
  /**
   * clears out the cache of saved values from the Shared preferences
   * so we can be sure they are not corrupting our current data
   */
  public void deleteSavedUserValues() {
    SharedPreferences sp = RealEstateAnalysisProcessorApplication.getInstance().
        getSharedPreferences(RealEstateAnalysisProcessorApplication.BASE_VALUES, ContextWrapper.MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();
    editor.clear();
    editor.commit();
    loadFieldValues(sp);


  }

  /**
   * calls a SharedPreferences from RealEstateAnalysisProcessorApplication and saves
   * the current input values there.
   */
  public void saveFieldValues() {
    
    SharedPreferences sp = RealEstateAnalysisProcessorApplication.getInstance().
        getSharedPreferences(RealEstateAnalysisProcessorApplication.BASE_VALUES, ContextWrapper.MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();
    editor.clear();
    //either a string or not a string
    for (ValueEnum inputEnum : ValueEnum.values()) {
      if (inputEnum.isSavedToDatabase() && (inputEnum.getType() != ValueEnum.ValueType.STRING)) {
        editor.putString(inputEnum.name(), String.valueOf(getInputValue(inputEnum)));    
      } else if (inputEnum.isSavedToDatabase() && (inputEnum.getType() == ValueEnum.ValueType.STRING)) {
        editor.putString(inputEnum.name(), getValueAsString(inputEnum));

      }
    }

    editor.commit();
  }

  public void setValueAsString(ValueEnum key, String value) {

    textValues.put(key, value);
  }

  public String getValueAsString(ValueEnum key) {

    String returnValue = textValues.get(key);
    return returnValue;
  }

  /**
   * Save the values in the User-input-values map into the database.  Also
   * adds a few other things - some calculated values, and the year saved.
   * @return
   */
  public int saveValues() {
    
   ContentValues cv = prepareValuesForSaving();
   
    //insert into database and return the last rowindex
    Integer rowIndex = databaseAdapter.insertEntry(cv);
    return rowIndex;
  }

  /**
   * Take the year that is currently selected in the graph activity and set that
   * into the set of values to be saved to a database.
   * @param cv the set of values that will be saved to the database
   * @return the modified set of values that will be saved to the database.
   */
  private ContentValues placeYearInDatabase(ContentValues cv) {
    if (getCurrentYearSelected() == null) {
      cv.put(DatabaseAdapter.YEAR_VALUE, 0);
    } else {
      //save year + 1, since it is an "external" version of year.  Internally,
      //system works 0 to n-1, externally, 1 to n.
      cv.put(DatabaseAdapter.YEAR_VALUE, getCurrentYearSelected()+1);
    }

    return cv;
  }

  /**
   * Mostly the same as saveValues(), but updates an existing row
   * rather than creating a new row.
   */
  public void updateRow() {

    ContentValues cv = prepareValuesForSaving();

    databaseAdapter.updateEntry((long)currentRowIndex, cv);
  }

  /**
   * This method handles the portion of work where the data is accumulated
   * for insertion to the set of values which will eventually be inserted 
   * intod the database.
   * @return a newly created ContentValues which can be saved to a database row.
   */
  private ContentValues prepareValuesForSaving() {
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

    //Put year into valueset
    cv = placeYearInDatabase(cv);
    
    return cv;
  }
  
  /**
   * gets the calculated values for the currently selected year and stores in database
   * @param cv
   * @return
   */
  private ContentValues placeCalcValueInContentValues(ContentValues cv) {
    //do the following ONLY if the values have been calculated.  If not, set as zero

    //take the current year's values for ATCF, ATER, NPV, MIRR, CRPV and CRCV, pop those in
    ValueEnum[] calcEnumsForDatabase = { 
        ValueEnum.ATCF,
        ValueEnum.ATER,
        ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN,
        ValueEnum.CAP_RATE_ON_PROJECTED_VALUE,
        ValueEnum.CAP_RATE_ON_PURCHASE_VALUE,
        ValueEnum.NPV
    };



    Integer year = getCurrentYearSelected();

    //first chance to bail - is year null?
    if (year == null) {      
      Log.d("DataController", "year is null");
      return cv;
    }
    
    for (ValueEnum v : calcEnumsForDatabase) {
      cv.put(v.name(), getCalcValue(v, year*12));
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
        putInputValue(cv.getAsDouble(inputEnum.name()), inputEnum);
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
    this.viewableDataTableRows = viewableDataTableRows;
  }

  public Integer getCurrentYearSelected() {
    return currentYearSelected;
  }

  public void setCurrentYearSelected(Integer currentYearSelected) {
    this.currentYearSelected = currentYearSelected;
  }




}

