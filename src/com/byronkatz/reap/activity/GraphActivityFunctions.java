package com.byronkatz.reap.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
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

//  static void saveViewableDataTableRows(Bundle b) {
//
//    Set<ValueEnum> vdtr = dataController.getViewableDataTableRows(); 
//    String[] stringArray = new String[vdtr.size()];
//
//    int i = 0;
//    for (ValueEnum ve : vdtr) {
//      stringArray[i] = ve.name();
//      i++;
//    }
//
//    b.putStringArray("viewableDataTableRows", stringArray);
//
//  }
//  
//  static void saveViewableDataTableRows (SharedPreferences sp) {
//    
//    SharedPreferences.Editor editor = sp.edit();
//    
//    Set<ValueEnum> vdtr = dataController.getViewableDataTableRows(); 
//    
//    for (ValueEnum ve : vdtr) {
//      editor.putBoolean(ve.name(), true);
//    }
//    editor.commit();
//  }
//
//  static Set<ValueEnum> restoreViewableDataTableRows(SharedPreferences sp) {
//    
//    Set<ValueEnum> vdtr = new HashSet<ValueEnum>();
//    Map<String, ?> entries = sp.getAll();
//    
//    for (ValueEnum ve : ValueEnum.values()) {
//      if (entries.containsKey(ve.name())) {
//        vdtr.add(ve);
//      }
//    }
//
//    return vdtr;
//  }
//  
//  static Set<ValueEnum> restoreViewableDataTableRows(Bundle b) {
//
//    Set<ValueEnum> tempSet = new HashSet<ValueEnum>();
//
//    String[] stringArray = b.getStringArray("viewableDataTableRows");
//
//    for (String s : stringArray) {
//      tempSet.add(ValueEnum.valueOf(s));
//    }
//
//    return tempSet;
//  }

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
