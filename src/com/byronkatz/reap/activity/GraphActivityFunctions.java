package com.byronkatz.reap.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

  private static final Double INCREASE_PERCENTAGE = 1.5d;
  private static final Double DECREASE_PERCENTAGE = 0.5d;

  private static DataController dataController = RealEstateMarketAnalysisApplication
      .getInstance().getDataController();

  static void switchForMenuItem(MenuItem item, GraphActivity graphActivity,
      int currentYear, boolean isGraphVisible) {
    Intent intent = null;
    //which item is selected?
    switch (item.getItemId()) {

    case R.id.configureGraphPageMenuItem:

      intent = new Intent(graphActivity, ConfigureDataTablesActivity.class);
      intent.putExtra("IS_GRAPH_VISIBLE", isGraphVisible);
      graphActivity.startActivityForResult(intent, GraphActivity.CONFIGURE_DATA_TABLE_ACTIVITY_REQUEST_CODE);
      break;
    case R.id.editValuesMenuItem:
      intent = new Intent(graphActivity, DataPagesActivity.class);
      graphActivity.startActivity(intent); 
      break;

    case R.id.saveCurrentValuesMenuItem:
      saveValueDialog(graphActivity);

      break;

    case R.id.databaseMenuItem:
      intent = new Intent(graphActivity, SavedDataBrowserActivity.class);
      graphActivity.startActivity(intent); 
      break;

    case R.id.checkMathMenuItem:
      intent = new Intent(graphActivity, AppInfoActivity.class);
      intent.putExtra("year", currentYear );
      graphActivity.startActivity(intent) ;
      break;

    default:
      //select nothing / do nothing
    }
  }

  public static void saveValueDialog(final GraphActivity graphActivity) {

    AlertDialog.Builder builder = new AlertDialog.Builder(graphActivity);

    builder.setPositiveButton("Add new entry", new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {

        int newRowIndex = dataController.saveValues();
        dataController.setCurrentDatabaseRow(newRowIndex);
        Toast toast = Toast.makeText(graphActivity, "Data saved as new entry", Toast.LENGTH_SHORT);
        toast.show();

      }
    } );

    //following is so the "update" button only appears if there is a row to update
    Integer currentDataRow = dataController.getCurrentDatabaseRow();
    if ( currentDataRow != -1) {
      String message = "Current data row is " + currentDataRow;
      builder.setMessage(message);
      builder.setNegativeButton("Update current entry", new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {


          dataController.updateRow();
          Toast toast = Toast.makeText(graphActivity, "Data saved into current entry", Toast.LENGTH_SHORT);
          toast.show();

        }
      });
    }
    AlertDialog saveNewOrUpdate = builder.create();
    saveNewOrUpdate.show();

  }

  static ProgressDialog setupProgressGraphDialog(GraphActivity graphActivity) {
    ProgressDialog progressDialog = new ProgressDialog(graphActivity);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    progressDialog.setMax(GraphActivity.DIVISIONS_OF_VALUE_SLIDER);
    progressDialog.setMessage("Calculating...");
    progressDialog.setCancelable(false);

    return progressDialog;
  }

  static void invalidateGraphs(Activity activity) {

    AnalysisGraph graph;
    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.atcfFrameLayout);
    graph.invalidate();

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.npvFrameLayout);
    graph.invalidate();

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.aterFrameLayout);
    graph.invalidate();

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.mirrFrameLayout);
    graph.invalidate();

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.crpvFrameLayout);
    graph.invalidate();

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.crcvFrameLayout);
    graph.invalidate();
  }

  static void highlightCurrentYearOnGraph(Integer currentYearHighlight, Activity activity) {

    AnalysisGraph graph;

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.atcfFrameLayout);
    graph.setCurrentYearHighlighted(currentYearHighlight);

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.npvFrameLayout);
    graph.setCurrentYearHighlighted(currentYearHighlight);

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.aterFrameLayout);
    graph.setCurrentYearHighlighted(currentYearHighlight);

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.mirrFrameLayout);
    graph.setCurrentYearHighlighted(currentYearHighlight);

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.crpvFrameLayout);
    graph.setCurrentYearHighlighted(currentYearHighlight);

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.crcvFrameLayout);
    graph.setCurrentYearHighlighted(currentYearHighlight);
  }


  static Double calculateMaxFromCurrent(Double currentValueNumeric) {

    return currentValueNumeric * INCREASE_PERCENTAGE;

  }

  static Double calculateMinFromCurrent(Double currentValueNumeric) {

    return currentValueNumeric * DECREASE_PERCENTAGE;

  }

  static Double calculateCurrentFromMax(Double maxValueNumeric) {

    return maxValueNumeric / INCREASE_PERCENTAGE; 
  }

  static Double calculateCurrentFromMin(Double minValueNumeric) {
    return minValueNumeric / DECREASE_PERCENTAGE;
  }

  static Double calculateMinMaxDelta(Double minValueNumeric, Double maxValueNumeric) {
    return maxValueNumeric - minValueNumeric;
  }


  static Double parseEditText(EditText editText, ValueEnum currentSliderKey) {
    Double returnValue = 0.0d;

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

  static void displayValue(EditText editText, Double valueNumeric, 
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

  static Integer updateTimeSliderAfterChange(SeekBar timeSlider, Integer currentYearMaximum) {

    //set the new max value on the progress bar
    timeSlider.setMax(currentYearMaximum - 1);
    Integer oldCurrentValue = timeSlider.getProgress() + 1;
    Integer newValue = 0;

    //here we decide - do we need to change the currentYearSelected?  only if the new max
    //is less than the old currentYearSelected
    if (currentYearMaximum < oldCurrentValue) {
      newValue = currentYearMaximum;
    } else {
      newValue = oldCurrentValue;
    }

    //necessary to setprogress twice here.  Bug in Android code.
    timeSlider.setProgress(0);
    timeSlider.setProgress(newValue - 1);
    
    return newValue;
  }

}
