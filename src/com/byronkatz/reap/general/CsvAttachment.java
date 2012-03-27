package com.byronkatz.reap.general;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;

import com.byronkatz.reap.calculations.Calculations;
import com.byronkatz.reap.general.ValueEnum.ValueType;


public class CsvAttachment {

  public static final String OUTPUT_WORKBOOK = "REAP_workbook.csv";
  private FileWriter outputWriter;
  private Activity activity;
  private File outputFile;
  private StringBuilder csvOutputArray;
  private Cursor cursor;
  private DataManager dataManager;

  public CsvAttachment( Activity activity, Cursor cursor) {
    this.activity = activity;
    this.cursor = cursor;
    csvOutputArray = new StringBuilder();
    dataManager = new DataManagerImpl();

  }


  public void createAttachment() {
    openFileOutputStream(activity);
    addDataToWorkbook();
    writeAndCloseBook();
  }

  public File getFile() {
    return outputFile;
  }


  private void writeAndCloseBook() {

    try {
      outputWriter.write(csvOutputArray.toString());
      outputWriter.close();
      csvOutputArray = null;

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void openFileOutputStream(Context activity) {
    try {
      activity.openFileOutput(OUTPUT_WORKBOOK, Context.MODE_WORLD_READABLE);
      File file = new File(activity.getFilesDir(), OUTPUT_WORKBOOK);
      outputWriter = new FileWriter(file);

    } catch (IOException e) {
      e.printStackTrace();
    }  
  }

  private String getStringFromCursor(ValueEnum valueEnum) {
    return cursor.getString(cursor.getColumnIndex(valueEnum.name()));
  }

  private double getDoubleFromCursor(ValueEnum valueEnum) {
    return cursor.getDouble(cursor.getColumnIndex(valueEnum.name()));
  }

  private int getIntegerFromCursor(ValueEnum valueEnum) {
    return cursor.getInt(cursor.getColumnIndex(valueEnum.name()));
  }

  private void addDataToWorkbook() {

    //get address info for this entry if it exists
    if (getStringFromCursor(ValueEnum.STREET_ADDRESS).length() > 0) {
      csvOutputArray.append( "Address:\n");
      csvOutputArray.append(getStringFromCursor(ValueEnum.STREET_ADDRESS) + "\n");
      csvOutputArray.append(getStringFromCursor(ValueEnum.CITY) + "\n");
      csvOutputArray.append(getStringFromCursor(ValueEnum.STATE_INITIALS) + "\n");
      csvOutputArray.append("\n\n");
    }

    //get comments info for this entry if it exists
    if (getStringFromCursor(ValueEnum.COMMENTS).length() > 0) {

      csvOutputArray.append("Comments:\n");
      csvOutputArray.append("\n\n");
      csvOutputArray.append(getStringFromCursor(ValueEnum.COMMENTS) + "\n\n");
    }

    //get input values from this entry.  Create a new DataManager object to 
    //insert into the Calculations object in order to get the correct calculated
    //values for this entry.
    csvOutputArray.append("\"USER INPUT VALUES\",\n\n");

    for (ValueEnum ve : ValueEnum.values()) {

      if (ve.isSavedToDatabase() && ! ve.isVaryingByYear()) {
        if (ve.getType() == ValueType.CURRENCY ||
            ve.getType() == ValueType.PERCENTAGE) {
          
          dataManager.putInputValue(getDoubleFromCursor(ve), ve);

          //append these values to the string to create the CSV
          csvOutputArray.append(activity.getString(ve.getTitleText()));
          csvOutputArray.append(",");
          csvOutputArray.append("\"" + 
              Utility.displayValue(getDoubleFromCursor(ve), ve) + "\"\n" );
          
        } else if (ve.getType() == ValueType.INTEGER) {
          
          dataManager.putInputValue(getIntegerFromCursor(ve), ve);

          //append these values to the string to create the CSV
          csvOutputArray.append(activity.getString(ve.getTitleText()));
          csvOutputArray.append(",");
          csvOutputArray.append("\"" + 
              getIntegerFromCursor(ve) + "\"\n" );

        }
      }
    }

    //now we have all the input values in our DataManager, so let's create a new Calculation object,
    //which will do all our calculations, and we can access it through our dataManager
    Calculations calculations = new Calculations();
    calculations.setValues(dataManager);

    csvOutputArray.append("\n\n");

    int nocp = getIntegerFromCursor(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS);
    int extraYears = getIntegerFromCursor(ValueEnum.EXTRA_YEARS);
    int totalYears = (nocp /12) + extraYears;

    
    
    List<ValueEnum> valueEnumArrayList = new ArrayList<ValueEnum>(Arrays.asList(ValueEnum.values()));

    valueEnumArrayList = Utility.removeCertainItemsFromDataTable(valueEnumArrayList);
    valueEnumArrayList = Utility.sortDataTableValues(activity, valueEnumArrayList);

    
    csvOutputArray.append("\"CALCULATED VALUES\",\n\n");

    //Set the titles over the different calculated values

    csvOutputArray.append("Year,");
    for (ValueEnum valueEnum : valueEnumArrayList) {
      if (valueEnum.getType() != ValueType.STRING && !valueEnum.isSavedToDatabase()) {
        csvOutputArray.append(activity.getString(valueEnum.getTitleText()));
        csvOutputArray.append(",");
      }
    }
    csvOutputArray.append("\n");

    //loop through the values for each year and type
    for (int i = 0; i < totalYears ; i++) {  
      //have to add 1 to the year display
      csvOutputArray = addCompoundingPeriod(csvOutputArray, i+1);

      for (ValueEnum valueEnum : valueEnumArrayList) {
        if (valueEnum.getType() != ValueType.STRING && !valueEnum.isSavedToDatabase()) {
          csvOutputArray = addValue(csvOutputArray, valueEnum, i*12);
        }
      }
      csvOutputArray.append("\n");
    }

    csvOutputArray.append("\n\n");
    csvOutputArray.append("\"AMORTIZATION TABLE\",\n\n");

    //Add the amortization table
    csvOutputArray.append("Month,");
    csvOutputArray.append(activity.getString(ValueEnum.MONTHLY_AMOUNT_OWED.getTitleText()));
    csvOutputArray.append(",");
    csvOutputArray.append(activity.getString(ValueEnum.MONTHLY_MORTGAGE_PAYMENT.getTitleText()));
    csvOutputArray.append(",");
    csvOutputArray.append(activity.getString(ValueEnum.INTEREST_PAYMENT.getTitleText()));
    csvOutputArray.append(",");
    csvOutputArray.append(activity.getString(ValueEnum.MONTHLY_INTEREST_ACCUMULATED.getTitleText()));
    csvOutputArray.append(",");
    csvOutputArray.append(activity.getString(ValueEnum.PRINCIPAL_PAYMENT.getTitleText()));
    csvOutputArray.append(",\n");


    //do the amortization loop
    for (int i = 0; i < nocp; i++) {
      //month 1 to month 360 - have to add 1 for month display only
      addCompoundingPeriod(csvOutputArray, i+1);
      csvOutputArray = addValue(csvOutputArray, ValueEnum.MONTHLY_AMOUNT_OWED, i);
      csvOutputArray = addValue(csvOutputArray, ValueEnum.MONTHLY_MORTGAGE_PAYMENT, i);
      csvOutputArray = addValue(csvOutputArray, ValueEnum.INTEREST_PAYMENT, i);
      csvOutputArray = addValue(csvOutputArray, ValueEnum.MONTHLY_INTEREST_ACCUMULATED, i);
      csvOutputArray = addValue(csvOutputArray, ValueEnum.PRINCIPAL_PAYMENT, i);

      csvOutputArray.append("\n");
    }
    
    csvOutputArray.append("\n\n");
    
    //add the yearly atcf equations
    csvOutputArray.append("\"AFTER TAX CASH FLOWS\",\n\n");

    //year at top
    csvOutputArray.append("\"Year:\",");
    for (int i = 0; i < totalYears; i++) {
      csvOutputArray.append(i+1 + ",");
    }
    csvOutputArray.append("\n");

    //gross income
    csvOutputArray.append("\"Potential Gross Income:\",");
    for (int i = 0; i < totalYears; i++) {
      csvOutputArray = addValue(
          csvOutputArray, ValueEnum.GROSS_YEARLY_INCOME, i*12);
    }
    csvOutputArray.append("\n");

    csvOutputArray.append("\"minus Vacancy and Credit Losses:\",");
    for (int i = 0; i < totalYears; i++) {
      csvOutputArray.append("\"");
      csvOutputArray.append(

          dataManager.getInputValue(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE) *
          dataManager.getCalcValue(ValueEnum.GROSS_YEARLY_INCOME, i*12)
          );
      csvOutputArray.append("\"");
      csvOutputArray.append(",");
    }
    csvOutputArray.append("\n");

    csvOutputArray.append("\"equals Effective Gross Income:\",");
    for (int i = 0; i < totalYears; i++) {
      csvOutputArray = addValue(
          csvOutputArray, ValueEnum.YEARLY_INCOME, i*12);
    }
    csvOutputArray.append("\n");

    csvOutputArray.append("\"minus Operating Expenses:\",");
    for (int i = 0; i < totalYears; i++) {
      csvOutputArray = addValue(
          csvOutputArray, ValueEnum.YEARLY_OPERATING_EXPENSES, i*12);
    }
    csvOutputArray.append("\n");

    csvOutputArray.append("\"equals Net Operating Income:\",");
    for (int i = 0; i < totalYears; i++) {
      csvOutputArray = addValue(
          csvOutputArray, ValueEnum.YEARLY_NET_OPERATING_INCOME, i*12);
    }
    csvOutputArray.append("\n");

    csvOutputArray.append("\"minus Annual Debt Service:\",");
    for (int i = 0; i < totalYears; i++) {
      csvOutputArray = addValue(
          csvOutputArray, ValueEnum.YEARLY_MORTGAGE_PAYMENT, i*12);
    }
    csvOutputArray.append("\n");

    csvOutputArray.append("\"equals Before Tax Cash Flow:\",");
    for (int i = 0; i < totalYears; i++) {
      csvOutputArray = addValue(
          csvOutputArray, ValueEnum.YEARLY_BEFORE_TAX_CASH_FLOW, i*12);
    }
    csvOutputArray.append("\n");

    csvOutputArray.append("\"minus Taxes:\",");
    for (int i = 0; i < totalYears; i++) {
      csvOutputArray = addValue(
          csvOutputArray, ValueEnum.YEARLY_TAX_ON_INCOME, i*12);
    }
    csvOutputArray.append("\n");

    csvOutputArray.append("\"equals After Tax Cash Flow:\",");
    for (int i = 0; i < totalYears; i++) {
      csvOutputArray = addValue(
          csvOutputArray, ValueEnum.ATCF, i*12);
    }
    csvOutputArray.append("\n");

    csvOutputArray.append("\n\nDetermining Taxes");
    csvOutputArray.append  ("\n-----------------\n\n");
    
    csvOutputArray.append("\"Net Operating Income:\",");
    for (int i = 0; i < totalYears; i++) {
      csvOutputArray = addValue(
          csvOutputArray, ValueEnum.YEARLY_NET_OPERATING_INCOME, i*12);
    }
    csvOutputArray.append("\n");

    csvOutputArray.append("\"minus Interest Expense:\",");
    for (int i = 0; i < totalYears; i++) {
      csvOutputArray = addValue(
          csvOutputArray, ValueEnum.YEARLY_INTEREST_PAID, i*12);
    }
    csvOutputArray.append("\n");

    csvOutputArray.append("\"minus Depreciation deduction:\",");
    for (int i = 0; i < totalYears; i++) {
      csvOutputArray = addValue(
          csvOutputArray, ValueEnum.YEARLY_DEPRECIATION, i*12);
    }
    csvOutputArray.append("\n");

    csvOutputArray.append("\"equals Taxable Income:\",");
    for (int i = 0; i < totalYears; i++) {
      csvOutputArray = addValue(
          csvOutputArray, ValueEnum.TAXABLE_INCOME, i*12);
    }
    csvOutputArray.append("\n");

    csvOutputArray.append("\"times Marginal Tax Rate:\",");
    for (int i = 0; i < totalYears; i++) {
      csvOutputArray.append("\"");
      csvOutputArray.append(
          dataManager.getInputValue(ValueEnum.MARGINAL_TAX_RATE));
      csvOutputArray.append("\"");
      csvOutputArray.append(",");

    }
    csvOutputArray.append("\n");

    csvOutputArray.append("\"equals Taxes:\",");
    for (int i = 0; i < totalYears; i++) {
      csvOutputArray = addValue(
          csvOutputArray, ValueEnum.YEARLY_TAX_ON_INCOME, i*12);
    }
    
    

  }

  private StringBuilder addCompoundingPeriod(StringBuilder s, int cP) {
    s.append("\"");
    s.append(cP);
    s.append("\"");
    s.append(",");

    return s;
  }

  private StringBuilder addValue(StringBuilder s, ValueEnum valueEnum, int year) {
    s.append("\"");
    s.append(dataManager.getCalcValue(valueEnum, year));
    s.append("\"");
    s.append(",");

    return s;
  }

}

