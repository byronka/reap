package com.byronkatz.reap.general;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import com.byronkatz.R;
import com.byronkatz.reap.general.ValueEnum.ValueType;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class Utility {

  private static Float returnValue = 0.0f;
  private static NumberFormat percentFormat = null;
  private static NumberFormat currencyFormat = null;
  private static NumberFormat currencyFormatter = null;
  private static Dialog helpDialog = null;
  private static Window window = null;
  private static TextView helpTextView = null;
  private static String result = null;
  private static final DataController dataController = RealEstateMarketAnalysisApplication
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
    helpDialog.show();
  }

public static Integer getNumOfCompoundingPeriods() {
  
  Integer currentYearMaximum = 0;
  
  Float tempFloatValue = dataController.
      getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS) / CalculatedVariables.NUM_OF_MONTHS_IN_YEAR;
  currentYearMaximum = tempFloatValue.intValue();
  return currentYearMaximum;
}


public static void setSelectionOnView(View v, ValueType valueType) {
  EditText editText = (EditText) v;
  //we'll use textInEditText to measure the string for the selection
  String textInEditText = editText.getText().toString();
  int textLength = textInEditText.length();

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

public static String displayCurrency(Float value) {
  currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
  return currencyFormatter.format(value);
}

public static Float parseCurrency(String value) {
  returnValue = 0.0f;
  currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
  if (value.contains("$")) {
    try {
      returnValue = currencyFormat.parse(value).floatValue();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  } else {
    try {
      returnValue = Float.valueOf(value);
    } catch (NumberFormatException e) {
      returnValue = 0.0f;
    }
  }
  return returnValue;
}

public static String displayPercentage(Float value) {
  percentFormat = NumberFormat.getPercentInstance(Locale.US);
  percentFormat.setMaximumFractionDigits(4);
  result = percentFormat.format(value);
  return result;
}

public static Float parsePercentage (String value) {
  returnValue = 0.0f;
  percentFormat = NumberFormat.getPercentInstance(Locale.US);
  percentFormat.setMaximumFractionDigits(4);
  if (value.contains("%")) {
    try {
      Number n = percentFormat.parse(value);
      returnValue = n.floatValue();
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }else {
    try {
      returnValue = Float.valueOf(value);
    } catch (NumberFormatException e) {
      returnValue = 0.0f;
    }
  }
  return returnValue;
}

}
