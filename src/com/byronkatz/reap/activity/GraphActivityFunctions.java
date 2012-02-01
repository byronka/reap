package com.byronkatz.reap.activity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.byronkatz.R;
import com.byronkatz.reap.customview.AnalysisGraph;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

public class GraphActivityFunctions {

  private static final Float INCREASE_PERCENTAGE = 1.5F;
  private static final Float DECREASE_PERCENTAGE = 0.5F;

  private static final DataController dataController = RealEstateMarketAnalysisApplication
      .getInstance().getDataController();

  static void switchForMenuItem(MenuItem item, GraphActivity graphActivity) {
    Intent intent = null;
    //which item is selected?
    switch (item.getItemId()) {

    case R.id.configureGraphPageMenuItem:

      intent = new Intent(graphActivity, ConfigureDataTablesActivity.class);
      graphActivity.startActivityForResult(intent, GraphActivity.CONFIGURE_DATA_TABLE_ACTIVITY_REQUEST_CODE);
      break;
    case R.id.editValuesMenuItem:
      intent = new Intent(graphActivity, DataPagesActivity.class);
      graphActivity.startActivity(intent); 
      break;

    case R.id.saveCurrentValuesMenuItem:
      dataController.saveValues();
      Toast toast = Toast.makeText(graphActivity, "Data saved", Toast.LENGTH_SHORT);
      toast.show();
      break;

    case R.id.databaseMenuItem:
      intent = new Intent(graphActivity, SavedDataBrowserActivity.class);
      graphActivity.startActivity(intent); 
      break;
    default:
      //select nothing / do nothing
    }
  }

  static ProgressDialog setupProgressGraphDialog(GraphActivity graphActivity) {
    ProgressDialog progressDialog = new ProgressDialog(graphActivity);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    progressDialog.setMax(GraphActivity.DIVISIONS_OF_VALUE_SLIDER);
    progressDialog.setMessage("Calculating...");
    progressDialog.setCancelable(false);

    return progressDialog;
  }

  static void saveViewableDataTableRows(Bundle b) {

    Set<ValueEnum> vdtr = dataController.getViewableDataTableRows(); 
    String[] stringArray = new String[vdtr.size()];

    int i = 0;
    for (ValueEnum ve : vdtr) {
      stringArray[i] = ve.name();
      i++;
    }

    b.putStringArray("viewableDataTableRows", stringArray);

  }
  
  static void saveViewableDataTableRows (SharedPreferences sp) {
    
    SharedPreferences.Editor editor = sp.edit();
    
    Set<ValueEnum> vdtr = dataController.getViewableDataTableRows(); 
    
    for (ValueEnum ve : vdtr) {
      editor.putBoolean(ve.name(), true);
    }
    editor.commit();
  }

  static Set<ValueEnum> restoreViewableDataTableRows(SharedPreferences sp) {
    
    Set<ValueEnum> vdtr = new HashSet<ValueEnum>();
    Map<String, ?> entries = sp.getAll();
    
    for (ValueEnum ve : ValueEnum.values()) {
      if (entries.containsKey(ve.name())) {
        vdtr.add(ve);
      }
    }

    return vdtr;
  }
  
  static Set<ValueEnum> restoreViewableDataTableRows(Bundle b) {

    Set<ValueEnum> tempSet = new HashSet<ValueEnum>();

    String[] stringArray = b.getStringArray("viewableDataTableRows");

    for (String s : stringArray) {
      tempSet.add(ValueEnum.valueOf(s));
    }

    return tempSet;
  }

  static void invalidateGraphs(Activity activity) {

    AnalysisGraph graph;
    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.atcfFrameLayout);
    graph.invalidate();

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.npvFrameLayout);
    graph.invalidate();
  }

  static void highlightCurrentYearOnGraph(Integer currentYearHighlight, Activity activity) {

    AnalysisGraph graph;

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.atcfFrameLayout);
    graph.setCurrentYearHighlighted(currentYearHighlight);

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.npvFrameLayout);
    graph.setCurrentYearHighlighted(currentYearHighlight);

  }

  static Map<ValueEnum, TableRow> createDataTableItems(GraphActivity graphActivity) {



    TextView dataTablePropertyName;
    Map<ValueEnum, TableRow> valueToDataTableItemCorrespondence = new HashMap<ValueEnum, TableRow> ();

    LayoutInflater inflater = (LayoutInflater) graphActivity.getSystemService
        (Context.LAYOUT_INFLATER_SERVICE);
    ValueEnum[] dataTableValues = ValueEnum.values();

    //get variable
    Set<ValueEnum> viewableDataTableRows = dataController.getViewableDataTableRows();
    TableLayout dataTableLayout = (TableLayout) graphActivity.findViewById(R.id.dataTableLayout);      
    //This is where we create the TableLayout
    //main loop to create the data table rows

    for (ValueEnum ve : dataTableValues) {

      //set up the correspondence between the table index and the valueEnums
      TableRow newTableRow = (TableRow) inflater.inflate(R.layout.data_table_tablerow, null);
      
      //make it invisible to start
      newTableRow.setVisibility(View.GONE);
      
      valueToDataTableItemCorrespondence.put(ve, newTableRow);

      dataTablePropertyName = (TextView) newTableRow.getChildAt(GraphActivity.PROPERTY_LABEL_INDEX);

      //the property name is always a string
      dataTablePropertyName.setText(ve.toString());

      dataTableLayout.addView(newTableRow);
    } //end of main for loop to set dataTableItems

    setColorDataTableRows(dataTableLayout, viewableDataTableRows);
    return valueToDataTableItemCorrespondence;

  }


  static void setColorDataTableRows(TableLayout dataTableLayout, Set<ValueEnum> viewableDataTableRows) {

    boolean alternateColor = true;
    TableRow tempRow = null;

    for (int i = 0; i < dataTableLayout.getChildCount(); i++) {

      tempRow = (TableRow) dataTableLayout.getChildAt(i);

      //Get the ValueEnum associated with the label string
      TextView tempTextView = (TextView) tempRow.getChildAt(GraphActivity.PROPERTY_LABEL_INDEX);
      String tempString = tempTextView.getText().toString();

      for (ValueEnum ve : viewableDataTableRows) {
        if (ve.toString() == tempString) {
          if (alternateColor) {
            tempRow.setBackgroundResource(R.color.data_table_row_color_alternate_a);
            tempRow.setVisibility(View.VISIBLE);
            alternateColor = ! alternateColor;
          } else {
            tempRow.setBackgroundResource(R.color.data_table_row_color_alternate_b);
            tempRow.setVisibility(View.VISIBLE);
            alternateColor = ! alternateColor;
          }
        }
      }



    }
  }

  static void setDataTableValueByInteger(TextView t, ValueEnum ve, Integer year) {
    if (ve.isVaryingByYear()) {
      t.setText(String.valueOf(dataController.getValueAsFloat(ve, year).intValue()));
    } else if (! ve.isVaryingByYear()) {
      t.setText(String.valueOf(dataController.getValueAsFloat(ve).intValue()));
    }
  }

  static void setDataTableValueByCurrency(TextView t, ValueEnum ve, Integer year) {
    if (ve.isVaryingByYear()) {
      t.setText(Utility.displayCurrency(dataController.getValueAsFloat(ve, year)));
    } else if (! ve.isVaryingByYear()) {
      t.setText(Utility.displayCurrency(dataController.getValueAsFloat(ve)));
    }
  }

  static void setDataTableValueByPercentage(TextView t, ValueEnum ve, Integer year) {
    if (ve.isVaryingByYear()) {
      t.setText(Utility.displayPercentage(dataController.getValueAsFloat(ve, year)));
    } else if (! ve.isVaryingByYear()) {
      t.setText(Utility.displayPercentage(dataController.getValueAsFloat(ve)));
    }
  }

  static Float calculateMaxFromCurrent(Float currentValueNumeric) {

    return currentValueNumeric * INCREASE_PERCENTAGE;

  }

  static Float calculateMinFromCurrent(Float currentValueNumeric) {

    return currentValueNumeric * DECREASE_PERCENTAGE;

  }

  static Float calculateCurrentFromMax(Float maxValueNumeric) {

    return maxValueNumeric / INCREASE_PERCENTAGE; 
  }

  static Float calculateCurrentFromMin(Float minValueNumeric) {
    return minValueNumeric / DECREASE_PERCENTAGE;
  }

  static Float calculateMinMaxDelta(Float minValueNumeric, Float maxValueNumeric) {
    return maxValueNumeric - minValueNumeric;
  }


  static Float parseEditText(EditText editText, ValueEnum currentSliderKey) {
    Float returnValue = 0.0f;

    switch (currentSliderKey.getType()) {
    case CURRENCY:
      returnValue = Utility.parseCurrency(editText.getText().toString());
      break;
    case PERCENTAGE:
      returnValue = Utility.parsePercentage(editText.getText().toString());
      break;
    case INTEGER:
    default:
      break;
    }

    return returnValue;
  }

  static void displayValue(EditText editText, Float valueNumeric, 
      ValueEnum currentSliderKey) {

    switch (currentSliderKey.getType()) {
    case CURRENCY:

      editText.setText(Utility.displayCurrency(valueNumeric));
      break;
    case PERCENTAGE:

      editText.setText(Utility.displayPercentage(valueNumeric));

      break;
    case INTEGER:

      editText.setText(String.valueOf(valueNumeric));
      break;
    default:
      //do nothing
      break;  
    }
  }

  static void updateTimeSliderAfterChange(SeekBar timeSlider, Integer currentYearMaximum) {

    timeSlider.setMax(currentYearMaximum - 1);
    timeSlider.setProgress(currentYearMaximum - 1);
  }

}
