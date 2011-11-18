package com.byronkatz;

import java.util.ArrayList;


/** This class controls the access to data provided by the user, not calculated.
 * 
 * @author byron
 *
 */
public class DataController{

  ArrayList<DataItem> fieldValues;

  public ArrayList<DataItem> getFieldValues() {
    return fieldValues;
  }

  /**
   * constructor
   */
  public DataController() {
    loadFieldValues();
  }

  public void loadFieldValues() {
    fieldValues = new ArrayList<DataItem>();
    //   HashMap<String, DataItem> fieldValues = new HashMap<String, DataItem>();

    fieldValues.add(new DataItem("total purchase value","450000.00", DataItem.REGULAR));
    fieldValues.add(new DataItem("yearly interest rate", "0.05", DataItem.REGULAR));
    fieldValues.add(new DataItem("monthly interest rate", "0.00416667", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("building value", "150000.0", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("number of compounding periods on loan", "360", DataItem.REGULAR));
    fieldValues.add(new DataItem("inflation rate", "0.03", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("primary mortgage insurance rate", "0.20", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("down payment", "100000", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("street address", "1234 Anywhere Street", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("city", "Bethesda", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("state initials", "MD", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("estimated rent payments", "2015.0", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("real estate appreciation rate", "0.04", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("yearly alternate investment return", "0.05", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("yearly home insurance", "1000.0", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("property tax rate", "0.0109", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("local municipal fees", "443.17", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("vacancy and credit loss rate", "0.03", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("yearly general expenses", "1460.00", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("marginal tax rate", "0.28", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("selling broker rate", "0.06", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("general sale expenses", "2000", DataItem.CHECK_BOX));
    fieldValues.add(new DataItem("required rate of return", "0.05", DataItem.CHECK_BOX));

  }    
}

