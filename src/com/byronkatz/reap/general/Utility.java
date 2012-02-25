package com.byronkatz.reap.general;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
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
import com.byronkatz.reap.calculations.GeneralCalculations;
import com.byronkatz.reap.general.ValueEnum.ValueType;

public class Utility {

  //  private static Double returnValue = 0.0f;
  private static NumberFormat percentFormat = null;
  private static NumberFormat currencyFormat = null;
  private static NumberFormat currencyFormatter = null;
  private static Dialog helpDialog = null;
  private static Window window = null;
  private static TextView helpTextView = null;
  private static String result = null;
  private static DataController dataController = RealEstateMarketAnalysisApplication
      .getInstance().getDataController();

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
  
  public static void switchForMenuItem(MenuItem item, Activity activity, boolean isGraphVisible) {
   
    
    Intent intent = null;
    
    //which item is selected?
    switch (item.getItemId()) {

    case R.id.configureGraphPageMenuItem:

      intent = new Intent(activity, ConfigureDataTablesActivity.class);
      intent.putExtra(GraphActivity.IS_GRAPH_VISIBLE, isGraphVisible);
      activity.startActivityForResult(intent, GraphActivity.CONFIGURE_DATA_TABLE_ACTIVITY_REQUEST_CODE);
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

    builder.setPositiveButton("Add new entry", new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {

        int newRowIndex = dataController.saveValues();
        dataController.setCurrentDatabaseRow(newRowIndex);
        Toast toast = Toast.makeText(activity, "Data saved as new entry id: " + newRowIndex, Toast.LENGTH_SHORT);
        toast.show();

      }
    } );

    //following is so the "update" button only appears if there is a row to update
    Integer currentDataRow = dataController.getCurrentDatabaseRow();
    if ( currentDataRow != -1) {
      String message = "Current entry id is " + currentDataRow;
      builder.setMessage(message);
      builder.setNegativeButton("Update current entry", new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {


          dataController.updateRow();
          Toast toast = Toast.makeText(activity, "Data saved into current entry", Toast.LENGTH_SHORT);
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

  public static Integer getNumOfCompoundingPeriods() {

    Integer currentYearMaximum = 0;

    Double tempDoubleValue = dataController.
        getValueAsDouble(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS) / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    currentYearMaximum = tempDoubleValue.intValue();
    return currentYearMaximum;
  }

  public static void callCalc(Activity a) {

    Intent i = new Intent();
    i.setClassName("com.android.calculator2",
        "com.android.calculator2.Calculator");

    try {
      a.startActivity(i);
    } catch (ActivityNotFoundException e) {
      showAlertDialog(a, "Activity was not found");
    } catch (Exception e) {
      showAlertDialog(a, "General exception");
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
      System.err.println("shouldn't get here in setSelectionOnView");
    }
  }

  public static String displayCurrency(Double value) {
    currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    return currencyFormatter.format(value);
  }

  public static String displayShortCurrency(Double value) {
    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
    nf.setMaximumFractionDigits(0);
    return nf.format(value);
  }
  
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
    
    //Seems that numberFormat has a beef with Double values.  Downgrade to Float so it doesn't choke
    
    percentFormat = NumberFormat.getPercentInstance(Locale.US);
    percentFormat.setMaximumFractionDigits(0);
    result = percentFormat.format(value.floatValue());
//    Log.d("Utilityclass", "result: " + result + " value: " + value);
    return result;
  }
  
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

  public static Double parseCurrency(String value) {
    Double returnValue = 0.0d;
    currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    if (value.contains("$")) {
      try {
        returnValue = currencyFormat.parse(value).doubleValue();
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

  public static void parseThenDisplayValue(View v, ValueEnum ve) {
    if (ve.getType() == ValueType.CURRENCY) {
      parseThenDisplayCurrency(v);
    } else if (ve.getType() == ValueType.PERCENTAGE) {
      parseThenDisplayPercentage(v);
    }
  }

  private static void parseThenDisplayCurrency(View v) {

    ((EditText) v).setText (Utility.displayCurrency(
        Utility.parseCurrency(((EditText) v).getText().toString())));

  }



  private static void parseThenDisplayPercentage(View v) {

    ((EditText) v).setText (Utility.displayPercentage(
        Utility.parsePercentage(((EditText) v).getText().toString())));
  }

  public static String displayPercentage(Double value) {
    
    //Seems that numberFormat has a beef with Double values.  Downgrade to Float so it doesn't choke
    
    percentFormat = NumberFormat.getPercentInstance(Locale.US);
    percentFormat.setMaximumFractionDigits(4);
    result = percentFormat.format(value.floatValue());
//    Log.d("Utilityclass", "result: " + result + " value: " + value);
    return result;
  }

  public static Double parsePercentage (String value) {
    Double returnValue = 0.0d;
    percentFormat = NumberFormat.getPercentInstance(Locale.US);
    percentFormat.setMaximumFractionDigits(4);
    if (value.contains("%")) {
      try {
        Number n = percentFormat.parse(value);
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
