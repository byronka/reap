package com.byronkatz.reap.general;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.byronkatz.R;
import com.byronkatz.reap.activity.AppInfoActivity;
import com.byronkatz.reap.activity.ConfigureDataTablesActivity;
import com.byronkatz.reap.activity.DataPagesActivity;
import com.byronkatz.reap.activity.GraphActivity;
import com.byronkatz.reap.activity.SavedDataBrowserActivity;
import com.byronkatz.reap.general.ValueEnum.ValueType;

public class Utility {

  private static Dialog helpDialog = null;
  private static Window window = null;
  private static TextView helpTextView = null;
  private static String result = null;
  private static DataController dataController = RealEstateAnalysisProcessorApplication
      .getInstance().getDataController();
  private static NumberFormat currencyNumberFormat = getCurrencyNumberFormat();

  
  private static NumberFormat getCurrencyNumberFormat() {
    NumberFormat currencyNumberFormat =NumberFormat.getCurrencyInstance(Locale.US);
    currencyNumberFormat.setMaximumFractionDigits(0);
    return currencyNumberFormat;
  }

  private static NumberFormat percentNumberFormat = NumberFormat.getPercentInstance(Locale.US);

  public static void showHelpDialog(int helpText, int helpTitle, Context context) {
    helpDialog = new Dialog(context);
    window = helpDialog.getWindow();
    window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, 
        WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
    helpDialog.setContentView(R.layout.help_dialog_view);
    helpTextView = (TextView)helpDialog.findViewById(R.id.help_text);
    helpTextView.setText(helpText);
    helpDialog.setTitle(helpTitle);
    helpDialog.setCanceledOnTouchOutside(true);
    helpDialog.show();
  }

  /**
   * simple utility method to remove certain values from the data table so they don't ever show
   * up.  For example, street address, city, state, and comments never have any reason to show up.
   * @param dataTableValues the List of values that we are removing values from
   * @return the List of values, with certain values removed
   */
  public static List<ValueEnum> removeCertainItemsFromDataTable(List<ValueEnum> dataTableValues) {
    
    //remove the following values, unneeded in the table
    dataTableValues.remove(ValueEnum.COMMENTS);
    dataTableValues.remove(ValueEnum.CITY);
    dataTableValues.remove(ValueEnum.STATE_INITIALS);
    dataTableValues.remove(ValueEnum.STREET_ADDRESS);
    
    return dataTableValues;
  }
  
  /**
   * Simple utility method to sort the dataTableValues in some arbitrary way.
   * @param dataTableValues the dataTableValues List to sort
   * @return the sorted List
   */
  public static List<ValueEnum> sortDataTableValues(final Activity activity, List<ValueEnum> dataTableValues) {
    
    Comparator<ValueEnum> comparator = new Comparator<ValueEnum>() {

      /**
       * We want to compare the Title Text, since we are alphabetizing based on that.
       * @param object1 the first ValueEnum to compare
       * @param object2 the second ValueEnum to compare
       * @return the compare int, based on the strings
       */
      @Override
      public int compare(ValueEnum object1, ValueEnum object2) {
        String object1String = activity.getString(object1.getTitleText());
        String object2String = activity.getString(object2.getTitleText());
        
        return object1String.compareTo(object2String);
      }
    };
    
    Collections.sort(dataTableValues, comparator);
    return dataTableValues;
  }
  
  public static void switchForMenuItem(MenuItem item, Activity activity) {


    Intent intent = null;

    //which item is selected?
    switch (item.getItemId()) {

    case R.id.configureGraphPageMenuItem:

      intent = new Intent(activity, ConfigureDataTablesActivity.class);
      activity.startActivity(intent);
      break;
    case R.id.editValuesMenuItem:
      intent = new Intent(activity, DataPagesActivity.class);
      activity.startActivity(intent); 
      break;

    case R.id.saveCurrentValuesMenuItem:
      saveValueDialog(activity);

      break;

    case R.id.databaseMenuItem:
      intent = new Intent(activity, SavedDataBrowserActivity.class);
      activity.startActivity(intent); 
      break;

    case R.id.infoMenuItem:
      intent = new Intent(activity, AppInfoActivity.class);
      activity.startActivity(intent);
      break;

    case R.id.viewGraphMenuItem:
      intent = new Intent(activity, GraphActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
      activity.startActivity(intent);
      activity.finish();
      break;

    default:
      //select nothing / do nothing
    }
  }

 
  public static void saveValueDialog(final Activity activity) {

    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    final String baseMessage = activity.getString(R.string.saveDataDialogBaseMessageText);
    final String addNewEntryText = activity.getString(R.string.addNewEntryText);
    final String dataSavedAsNewEntry = activity.getString(R.string.dataSavedAsNewEntryText);
    final String currentEntryIdIs = activity.getString(R.string.currentEntryIdIsText);
    final String updateCurrentEntry = activity.getString(R.string.updateCurrentEntryText);
    final String dataSavedIntoCurrentEntryText = activity.getString(R.string.dataSavedIntoCurrentEntryText);


    builder.setMessage(baseMessage);
    builder.setPositiveButton(addNewEntryText, new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {

        int newRowIndex = dataController.saveValues();
        dataController.setCurrentDatabaseRow(newRowIndex);
        Toast toast = Toast.makeText(activity, dataSavedAsNewEntry + " " + newRowIndex, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
        toast.show();

      }
    } );

    //following is so the "update" button only appears if there is a row to update
    final Integer currentDataRow = dataController.getCurrentDatabaseRow();
    if ( currentDataRow > 0) {
      String currentEntryIdmessage = currentEntryIdIs + " " + currentDataRow;
      String message = baseMessage + currentEntryIdmessage;
      builder.setMessage(message);
      builder.setNegativeButton(updateCurrentEntry, new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {


          dataController.updateRow();
          Toast toast = Toast.makeText(activity, dataSavedIntoCurrentEntryText + " " + currentDataRow , Toast.LENGTH_SHORT);
          toast.show();

        }
      });
    }
    AlertDialog saveNewOrUpdate = builder.create();
    saveNewOrUpdate.show();

  }

  public static void showHelpDialog(String helpText, int helpTitle, Context context) {
    helpDialog = new Dialog(context);
    window = helpDialog.getWindow();
    window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, 
        WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
    helpDialog.setContentView(R.layout.help_dialog_view);
    helpTextView = (TextView)helpDialog.findViewById(R.id.help_text);
    helpTextView.setText(helpText);
    helpDialog.setTitle(helpTitle);
    helpDialog.setCanceledOnTouchOutside(true);
    helpDialog.show();
  }



  public static void callCalc(Activity a) {

    Intent i = new Intent();
    i.setClassName("com.android.calculator2",
        "com.android.calculator2.Calculator");

    String calcActNotFound = a.getString(R.string.calculatorActivityNotFoundText);
    String calcGenException = a.getString(R.string.calcActivityNotFoundGenExceptionText);

    try {
      a.startActivity(i);
    } catch (ActivityNotFoundException e) {
      showAlertDialog(a, calcActNotFound);
    } catch (Exception e) {
      showAlertDialog(a, calcGenException);
    }
  }

  public static void showAlertDialog(Activity activity, String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(activity);

    builder.setMessage(message);
    String buttonText = activity.getString(android.R.string.ok);
    builder.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {

        dialog.dismiss();
      }
    } );
  }

  public static void showAlertDialog(Context context, String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);

    builder.setMessage(message);
    String buttonText = context.getString(android.R.string.ok);
    builder.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {

        dialog.dismiss();
      }
    } );
  }

  public static String toHexString(byte[] bytes) {
    char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    char[] hexChars = new char[bytes.length * 2];
    int v;
    for ( int j = 0; j < bytes.length; j++ ) {
      v = bytes[j] & 0xFF;
      hexChars[j*2] = hexArray[v/16];
      hexChars[j*2 + 1] = hexArray[v%16];
    }
    return new String(hexChars);
  }

  public static void showToast(Activity activity, String message) {
    Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
    toast.show();
  }

  public static void setSelectionOnView(View v, ValueEnum ve) {
    EditText editText = (EditText) v;
    //we'll use textInEditText to measure the string for the selection
    String textInEditText = editText.getText().toString();
    int textLength = textInEditText.length();
    ValueType valueType = ve.getType();

    switch (valueType) {
    case CURRENCY:
      editText.setSelection(1, textLength);
      break;
    case PERCENTAGE:
      editText.setSelection(0, textLength - 1);
      break;
    case INTEGER:
      editText.setSelection(0, textLength);
      break;
    case STRING:
      editText.setSelection(0, textLength);
      break;
    default:
//      Log.e("setSelectionOnView in Utility class", "shouldn't get here");
    }
  }

  public static String displayCurrency(Double value) {
//    currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
//    return currencyFormatter.format(value);
    //TODO
//    currencyNumberFormat.setMaximumFractionDigits(0);
    return currencyNumberFormat.format(value);
  }

  public static String displayShortCurrency(Double value) {
//    currencyNumberFormat.setMaximumFractionDigits(0);
    return currencyNumberFormat.format(value);
  }

  /**
   * This method converts a raw number into a nicely formatted string
   * @param value the original number
   * @param ve the type of number, enumerated by ValueEnum
   * @return
   */
  public static String displayShortValue(Double value, ValueEnum ve) {
    String type = ve.getType().name();
    String outputValue = "nothing";

    if (type == "CURRENCY") {
      outputValue = displayShortCurrency(value);
      //following is identical to displayValue - change if needed.
    } else if (type == "PERCENTAGE") {
      outputValue = displayShortPercentage(value);
    } else if (type == "INTEGER") {
      outputValue = String.valueOf(value.intValue());
    }

    return outputValue;
  }

  public static String displayShortPercentage(Double value) {

    //Seems that currencyNumberFormat has a beef with Double values.  Downgrade to Float so it doesn't choke

    //This might be something we want to externalize IF we get that far
    percentNumberFormat.setMaximumFractionDigits(2);
    result = percentNumberFormat.format(value.floatValue());
    return result;
  }

  /**
   * Based on the valueEnum, determines the kind of number (currency, integer, percentage)
   * and returns a string value which is correct.
   * @param value the Double value to be converted to a string
   * @param ve the ValueEnum which corresponds to the value
   * @return a formatted string appropriate to the value
   */
  public static String displayValue(Double value, ValueEnum ve) {
    String type = ve.getType().name();
    String outputValue = "nothing";

    if (type == "CURRENCY") {
      outputValue = displayCurrency(value);
    } else if (type == "PERCENTAGE") {
      outputValue = displayPercentage(value);
    } else if (type == "INTEGER") {
      outputValue = String.valueOf(value.intValue());
    }

    return outputValue;
  }

  /**
   * Tries to convert the string into a number.  If it cannot, it returns 0
   * @param value the String in currency format.
   * @return a Double equatable to value, or else 0
   */
  public static Double parseCurrency(String value) {
    Double returnValue = 0.0d;
    if (value.contains("$")) {
      try {
        returnValue = currencyNumberFormat.parse(value).doubleValue();
      } catch (ParseException e) {
        e.printStackTrace();
      }
    } else {
      try {
        returnValue = Double.valueOf(value);
      } catch (NumberFormatException e) {
        returnValue = 0.0d;
      }
    }
    return returnValue;
  }

  /**
   * This method takes a string value, parses it into a proper value, and converts that to a 
   * formatted string, with no pennies if currency.
   * @param value the original string value (currently takes currency, percentage, string, integer)
   * @param ve the ValueEnum associated with this value
   * @return
   */
  public static String parseAndDisplayShortValue(String value, ValueEnum ve) {

    String returnValue = value;

    //we don't want null values
    if (value == null) {
      returnValue = "0";
    }

    //if it is a string we just want to pass it through
    if (ve.getType() != ValueType.STRING) {
      returnValue = displayShortValue(parseValue(returnValue, ve), ve);
    }

    return returnValue;
  }
  
  /**
   * This method takes a string value, parses it into a proper value, and converts that to a 
   * formatted string, with pennies if currency.
   * @param value the original string value (currently takes currency, percentage, string, integer)
   * @param ve the ValueEnum associated with this value
   * @return
   */
  public static String parseAndDisplayValue(String value, ValueEnum ve) {

    String returnValue = value;

    //we don't want null values
    if (value == null) {
      returnValue = "0";
    }

    //if it is a string we just want to pass it through
    if (ve.getType() != ValueType.STRING) {
      returnValue = displayValue(parseValue(returnValue, ve), ve);
    }

    return returnValue;
  }

  /**
   * Currently only set up to handle percentages and currency
   * @param value the string value which is a currency, percentage, or integer
   * @param ve the ValueEnum associated with the value
   * @return
   */
  public static Double parseValue(String value, ValueEnum ve) {

    Double returnValue = 0.0d;

    if (ve.getType() == ValueType.CURRENCY) {
      returnValue = parseCurrency(value);
    } else if (ve.getType() == ValueType.PERCENTAGE){
      returnValue = parsePercentage(value);
    } else if (ve.getType() == ValueType.INTEGER){
      returnValue = Double.valueOf(value);
    }

    return returnValue;
  }

  public static void parseThenDisplayValue(View v, ValueEnum ve) {
    if (ve.getType() == ValueType.CURRENCY) {
      parseThenDisplayCurrency(v);
    } else if (ve.getType() == ValueType.PERCENTAGE) {
      parseThenDisplayPercentage(v);
    } else if (ve.getType() == ValueType.INTEGER) {
      parseThenDisplayInteger(v);
    }
  }

  private static void parseThenDisplayCurrency(View v) {

    ((EditText) v).setText (Utility.displayCurrency(
        Utility.parseCurrency(((EditText) v).getText().toString())));

  }

  private static void parseThenDisplayInteger(View v) {

    ((EditText) v).setText (String.valueOf(
        Integer.valueOf(((EditText) v).getText().toString())));

  }


  private static void parseThenDisplayPercentage(View v) {

    ((EditText) v).setText (Utility.displayPercentage(
        Utility.parsePercentage(((EditText) v).getText().toString())));
  }

  public static String displayPercentage(Double value) {

    //Seems that currencyNumberFormat has a beef with Double values.  Downgrade to Float so it doesn't choke

    percentNumberFormat.setMaximumFractionDigits(4);
    result = percentNumberFormat.format(value.floatValue());
    return result;
  }

  public static Double parsePercentage (String value) {
    Double returnValue = 0.0d;
    percentNumberFormat.setMaximumFractionDigits(4);
    if (value.contains("%")) {
      try {
        Number n = percentNumberFormat.parse(value);
        returnValue = n.doubleValue();
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }else {
      try {
        returnValue = Double.valueOf(value);
      } catch (NumberFormatException e) {
        returnValue = 0.0d;
      }
    }
    return returnValue;
  }

}
