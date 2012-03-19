package com.byronkatz.reap.general;

import com.byronkatz.reap.calculations.CalcValueGettable;


public interface DataManager {

  /**
   * This method returns the calculated value related to the Enum and for the year specified
   * @param valueEnum
   * @param year
   * @return the Double value for that year and value
   */
  public Double getCalcValue(ValueEnum valueEnum, Integer compoundingPeriod);
  
  /**
   * This function pulls a user-input value from the data structure.  These values are
   * entered at a previous time on other pages, and as they traverse the pages, each value
   * is filled in.  That means, if a value does not exist, this method should return a 0 for safety.
   * @param valueEnum
   * @return
   */
  public Double getInputValue(ValueEnum valueEnum);
  
  /**
   * This function is used throughout the user interface to set the user-input values which
   * will be calculated later.
   * 
   * @param value
   * @param valueEnum
   */
  public void putInputValue(Double value, ValueEnum valueEnum);
  
  /**
   * This method is used to map the getValue() method for each calculated value
   * to a valueEnum key.
   * @param valueEnum the ValueEnum enumerator which specifies which value type we want
   * @param calcValue a pointer to the object CalcValueGettable which allows us direct
   *  access to the object method
   */
  public void addCalcValuePointers(ValueEnum valueEnum, CalcValueGettable calcValue);
}
