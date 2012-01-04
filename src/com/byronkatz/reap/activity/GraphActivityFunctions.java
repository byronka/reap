package com.byronkatz.reap.activity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
    progressDialog.setMessage("Loading...");
    progressDialog.setCancelable(false);
    
    return progressDialog;
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

    //initialize variable
    Set<ValueEnum> viewableDataTableRows = new HashSet<ValueEnum>();
    TableLayout dataTableLayout = (TableLayout) graphActivity.findViewById(R.id.dataTableLayout);      
    //This is where we create the TableLayout
    //set alternate colors by row
    boolean alternateColor = true;
    //main loop to create the data table rows

    for (ValueEnum ve : dataTableValues) {

      //set up the correspondence between the table index and the valueEnums
      TableRow newTableRow = (TableRow) inflater.inflate(R.layout.data_table_tablerow, null);
      valueToDataTableItemCorrespondence.put(ve, newTableRow);


      //make every row viewable by default
      viewableDataTableRows.add(ve);

      if (alternateColor) {
        newTableRow.setBackgroundResource(R.color.data_table_row_color_alternate_a);
        alternateColor = ! alternateColor;
      } else {
        newTableRow.setBackgroundResource(R.color.data_table_row_color_alternate_b);
        alternateColor = ! alternateColor;
      }

      dataTablePropertyName = (TextView) newTableRow.getChildAt(GraphActivity.PROPERTY_LABEL_INDEX);

      //the property name is always a string
      dataTablePropertyName.setText(ve.toString());

      /* set value based on what type of number it is, or string if 
       * applicable if it is saved to database, that 
       * means we only need the first year, or 
       * "getValueAsFloat(key)" rather than "getValueAsFloat(key, year)"
       */

      //set the map to find these later


      dataTableLayout.addView(newTableRow);
    } //end of main for loop to set dataTableItems

    dataController.setViewableDataTableRows(viewableDataTableRows);
    return valueToDataTableItemCorrespondence;

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


    timeSlider.setMax(currentYearMaximum);
    timeSlider.setProgress(currentYearMaximum);
//    yearDisplayAtSeekBar.setText("Year:\n" + String.valueOf(timeSlider.getProgress()));
  }

}
