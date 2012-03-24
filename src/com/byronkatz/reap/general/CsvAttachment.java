package com.byronkatz.reap.general;


import java.io.File;
import java.io.FilePermission;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Parcel;

import com.byronkatz.reap.general.ValueEnum.ValueType;


public class CsvAttachment {

  private final DataController dataController = 
      RealEstateAnalysisProcessorApplication.getInstance().getDataController();
  public static final String OUTPUT_WORKBOOK = "REAP_workbook.csv";
  private FileWriter outputWriter;
  private Context context;
  private File outputFile;
  private String csvOutputArray;
  private Cursor cursor;

  public CsvAttachment( Context context, Cursor cursor) {
    this.context = context;
    this.cursor = cursor;
    csvOutputArray = "";

  }


  public void createAttachment() {
    openFileOutputStream(context);
    addDataToWorkbook();
    writeAndCloseBook();
  }

  public File getFile() {
    return outputFile;
  }
  

  private void writeAndCloseBook() {

    try {
      outputWriter.write(csvOutputArray);
      outputWriter.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void openFileOutputStream(Context context) {
    try {
      context.openFileOutput(OUTPUT_WORKBOOK, Context.MODE_WORLD_READABLE);
      File file = new File(context.getFilesDir(), OUTPUT_WORKBOOK);
      outputWriter = new FileWriter(file);

    } catch (IOException e) {
      e.printStackTrace();
    }  
  }



  private void addDataToWorkbook() {
    
    
    ContentValues emailContentValues = new ContentValues();
    DatabaseUtils.cursorRowToContentValues(cursor, emailContentValues);
    
    if (emailContentValues.getAsString(ValueEnum.STREET_ADDRESS.name()).length() > 0) {
      csvOutputArray += "Address:\n";
      csvOutputArray += emailContentValues.getAsString(ValueEnum.STREET_ADDRESS.name()) + "\n";
      csvOutputArray += emailContentValues.getAsString(ValueEnum.CITY.name()) + "\n";
      csvOutputArray += emailContentValues.getAsString(ValueEnum.STATE_INITIALS.name()) + "\n";
      csvOutputArray += "\n\n";
    }
    
    if (emailContentValues.getAsString(ValueEnum.COMMENTS.name()).length() > 0) {

    csvOutputArray += "Comments:\n";
    csvOutputArray += "\n\n";
    csvOutputArray += emailContentValues.getAsString(ValueEnum.COMMENTS.name()) + "\n\n";
    }
    
    String yearValue = emailContentValues.getAsString(DatabaseAdapter.YEAR_VALUE);
    csvOutputArray += "\nCalculated Values For Year: " + yearValue + "\n\n";
    
    for (Map.Entry<String,Object> m : emailContentValues.valueSet()) {

      ValueEnum viewEnum = null;
      //if we can extract a valueEnum, in order to format the value, do so.
      try {
        viewEnum = ValueEnum.valueOf(m.getKey());
      } catch (IllegalArgumentException e) {
        //do nothing - just move on.  This is where we hit things not in valueEnum, like year
      }

      //if we successfully got a ValueEnum, we can use it to format the string
      if ((viewEnum != null) && (viewEnum.getType() != ValueType.STRING) && (! viewEnum.isVaryingByYear())) {
        
        csvOutputArray += context.getString(viewEnum.getTitleText());
        csvOutputArray += ",";
        csvOutputArray += "\"" + 
            Utility.parseAndDisplayValue(String.valueOf(m.getValue()), viewEnum) + "\"\n" ;
        
      }
    }
    
    csvOutputArray += "\n\n";
    
    int totalYears = (emailContentValues.getAsInteger(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS.name()) +
        emailContentValues.getAsInteger(ValueEnum.EXTRA_YEARS.name())) / 12;
    
    csvOutputArray += 
        "Year"                                                + "," + 
        ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN.toString() + "," +
        ValueEnum.NPV                                         + "," +
        ValueEnum.ATCF                                        + ",\n";
    
    
    
    for (int i = 0; i < totalYears ;i++) {  
      csvOutputArray += "\"";
      csvOutputArray += i;
      csvOutputArray += "\"";
      csvOutputArray += ",";
      
      csvOutputArray += "\"";
      csvOutputArray += Utility.displayValue(dataController.getCalcValue(
          ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN, i), ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN);
      csvOutputArray += "\"";
      csvOutputArray += ",";

      csvOutputArray += "\"";
      csvOutputArray += Utility.displayValue(dataController.getCalcValue(
          ValueEnum.NPV, i), ValueEnum.NPV);
      csvOutputArray += "\"";
      csvOutputArray += ",";

      csvOutputArray += "\"";
      csvOutputArray += Utility.displayValue(dataController.getCalcValue(
          ValueEnum.ATCF, i), ValueEnum.ATCF);
      csvOutputArray += "\"";
      csvOutputArray += ",";

      
      
      csvOutputArray += "\n";

    }
    
  }

}

