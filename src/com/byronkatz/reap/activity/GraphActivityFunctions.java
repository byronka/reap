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

  private static final Float INCREASE_PERCENTAGE = 1.5F;
  private static final Float DECREASE_PERCENTAGE = 0.5F;

  private static DataController dataController = RealEstateMarketAnalysisApplication
      .getInstance().getDataController();

  static void switchForMenuItem(MenuItem item, GraphActivity graphActivity, int currentYear) {
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
      saveValueDialog(graphActivity);

      break;

    case R.id.databaseMenuItem:
      intent = new Intent(graphActivity, SavedDataBrowserActivity.class);
      graphActivity.startActivity(intent); 
      break;

    case R.id.checkMathMenuItem:
      intent = new Intent(graphActivity, MathCheckActivity.class);
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
