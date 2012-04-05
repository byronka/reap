package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataManager;

/**
 * This class manages the creation and storage of calculated objects
 * @author byron
 *
 */
public interface ValueSettable {
  
  /**
   * Uses the DataManager object to extract values for use with the object.  Also, it is assumed
   * that the ValueSettable object will have a private method which assigns its CalcValueGettable
   * objects (itself or its children) to the DataManager.
   * @param dataManager Datamanager object for user input and calculated value output
   */
  public void setValues (DataManager dataManager);
    
}