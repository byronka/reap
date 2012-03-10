package com.byronkatz.reap.activity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.byronkatz.R;
import com.byronkatz.reap.calculations.RentalUnitOwnership;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.OnItemSelectedListenerWrapper;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;
import com.byronkatz.reap.general.ValueEnum.ValueType;

public class GraphActivity extends Activity {

  ValueEnum currentSliderKey;
  EditText currentValueEditText;
  SeekBar valueSlider;
  SeekBar timeSlider;
  TextView yearDisplayAtSeekBar;
  DataTable dataTable;
  public static final String PREFS_NAME = "MyPrefsFile";
  private Boolean isGraphVisible;
  public static final String IS_GRAPH_VISIBLE = "IS_GRAPH_VISIBLE";
  public static final String CURRENT_SLIDER_KEY = "CURRENT_SLIDER_KEY";
  private TabHost tabs;

  Boolean cleanShutdownBackgroundTask;

  ArrayAdapter<ValueEnum> spinnerArrayAdapter;

  private final DataController dataController = RealEstateMarketAnalysisApplication
      .getInstance().getDataController();
  Map<ValueEnum, TableRow> valueToDataTableItemCorrespondence;

  Double minValueNumeric;
  Double maxValueNumeric;
  Double deltaValueNumeric;
  Double currentValueNumeric;
  Double originalCurrentValueNumeric;

  SharedPreferences sp;

  public static final int DIVISIONS_OF_VALUE_SLIDER = 40;
  //  public static final int CONFIGURE_DATA_TABLE_ACTIVITY_REQUEST_CODE = 1;

  ValueEnum[] dataTableItems = ValueEnum.values();
  Double percentageSlid;
  public static AsyncTask<Void, Integer, Void> calculateInBackgroundTask;

  @Override
  public boolean onCreateOptionsMenu (Menu menu){
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.graph_page_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected (MenuItem item) {
    super.onOptionsItemSelected(item);

    if (tabs.getVisibility() == View.GONE) {
      isGraphVisible = false;
    } else {
      isGraphVisible = true;
    }

    Utility.switchForMenuItem(item, GraphActivity.this);
    return false;
  }

  //  @Override
  //  public void onActivityResult(int requestCode, int resultCode, Intent data) {
  //    super.onActivityResult(requestCode, resultCode, data);
  //
  //    if (requestCode == CONFIGURE_DATA_TABLE_ACTIVITY_REQUEST_CODE) {
  //
  //      if (data != null) {
  //        isGraphVisible = data.getExtras().getBoolean(IS_GRAPH_VISIBLE, true);
  //      }
  //
  //      executeGraphVisibility(isGraphVisible);
  //    }
  //  }

  private void executeGraphVisibility(Boolean isGraphVisible) {
    if (isGraphVisible) {
      tabs.setVisibility(View.VISIBLE);
    } else {
      tabs.setVisibility(View.GONE);
    }
  }

  @Override
  public void onResume() {

    //    Log.d(getClass().getName(), "Entering onResume");
    setCleanShutdown(true);
    sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    dataController.setViewableDataTableRows(
        dataTable.restoreViewableDataTableRows(sp));
    dataTable.makeSelectedRowsVisible( valueToDataTableItemCorrespondence);

    isGraphVisible = sp.getBoolean(IS_GRAPH_VISIBLE, false);
    executeGraphVisibility(isGraphVisible);
    //set the current current_slider_key, which is shown in the spinner at the top.  If
    //nothing set, then set Building Value as the default (it's the first one)
    String temp = sp.getString(CURRENT_SLIDER_KEY, ValueEnum.BUILDING_VALUE.name());
    currentSliderKey = ValueEnum.valueOf(temp);
    setSpinnerSelection(currentSliderKey);

    if (DataController.isDataChanged()) {

      currentValueNumeric = dataController.getValueAsDouble(currentSliderKey);
      //      Log.d("Tag 005", "currentValueNumeric is " + currentValueNumeric);

      //following is for the reset button
      originalCurrentValueNumeric = currentValueNumeric;
      dataTable.colorTheDataTables();
      recalcGraphPage();

    }
    super.onResume();
    //    Log.d(getClass().getName(), "exiting onResume");

  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {

    //    Log.d(getClass().getName(), "Entering onCreate");
    super.onCreate(savedState);
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    setContentView(R.layout.graph);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.my_title);

    //data table is the table of calculated and input values shown under the graph
    dataTable = new DataTable(this);


    Integer extraYears = dataController.getValueAsDouble(ValueEnum.EXTRA_YEARS).intValue();
    Integer currentYearMaximum = Utility.getNumOfCompoundingPeriods() + extraYears ;
    setupValueSpinner();

    setupTimeSlider(currentYearMaximum);
    setupValueSlider(currentYearMaximum);
    setupGraphs(currentYearMaximum);
    setupCurrentValueFields();
    valueToDataTableItemCorrespondence = dataTable.createDataTableItems(GraphActivity.this);

    setupGraphTabs();
    //    Log.d(getClass().getName(), "exiting onCreate");

  }

  /**
   * to control shutting down the background thread more precisely
   * This will correctly determine how to handle the task of shutting down
   * the background thread.  You are guaranteed that it will be cleanly
   * finished when this method returns.
   */
  private void shutDownBackGroundThreadIfRunning() {
    
    //first we want to avoid null pointer exception...if null, no need to cancel it
    if (calculateInBackgroundTask == null) {
      return;
    }
  
    //secondly, we want to make sure it's actually running before we try cancelling
    if (calculateInBackgroundTask.getStatus() == AsyncTask.Status.RUNNING) {
      calculateInBackgroundTask.cancel(false);
    }
    
    //finally, if it's not null, and not running, it must be cancelled.  However,
    //we want to make sure it ended cleanly, that's why isCancelled() won't work.
    //cleanshutdown will be set at both normal and cancelled endings for the thread
    if (! isCleanShutdown()) {
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    }
//        calculateInBackgroundTask.getStatus() != AsyncTask.Status.RUNNING) {
//  calculateInBackgroundTask.cancel(false);

}

@Override
public void onPause() {

  //    Log.d("GraphActivity onPause", "Entering onPause");

  //full stop - no restart on background thread
  shutDownBackGroundThreadIfRunning();
  SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
  dataTable.saveGraphPageData(sharedPreferences, isGraphVisible, currentSliderKey);

  //Following saves the data to persistence between onPause / onResume
  //    dataController.saveFieldValues();
  super.onPause();
  //    Log.d("GraphActivity onPause", "Exiting onPause");

}


@Override
public void onDestroy() {
  super.onDestroy();
  dataController.nullifyNumericCache();
  System.runFinalizersOnExit(true);
}

//  @Override
//  public void onRestoreInstanceState(Bundle outState) {
//
//    super.onRestoreInstanceState(outState);
//    dataController.setViewableDataTableRows(
//        dataTable.restoreViewableDataTableRows(outState));
//
//  }
//
//  @Override
//  public void onSaveInstanceState(Bundle outState) {
//
//    dataTable.saveViewableDataTableRows(outState);
//    super.onSaveInstanceState(outState);
//  }

private void setupGraphTabs() {
  tabs = (TabHost) findViewById(android.R.id.tabhost);        
  tabs.setup();

  TabHost.TabSpec spec = tabs.newTabSpec("NPV");
  spec.setContent(R.id.tab1);
  spec.setIndicator(getText(R.string.netPresentValueTabText));
  tabs.addTab(spec);

  spec = tabs.newTabSpec("ATCF");
  spec.setContent(R.id.tab2);
  spec.setIndicator(getText(R.string.atcfTabText));
  tabs.addTab(spec);

  spec = tabs.newTabSpec("ATER");
  spec.setContent(R.id.tab3);
  spec.setIndicator(getText(R.string.aterTabText));
  tabs.addTab(spec);

  spec = tabs.newTabSpec("MIRR");
  spec.setContent(R.id.tab4);
  spec.setIndicator(getText(R.string.modifiedInternalRateOfReturnTabText));
  tabs.addTab(spec);

  spec = tabs.newTabSpec("CRPV");
  spec.setContent(R.id.tab5);
  spec.setIndicator(getText(R.string.capRateOnPurchaseValueTabText));
  tabs.addTab(spec);

  spec = tabs.newTabSpec("CRCV");
  spec.setContent(R.id.tab6);
  spec.setIndicator(getText(R.string.capRateOnProjectedValueTabText));
  tabs.addTab(spec);

  //    if (isGraphVisible) {
  //      tabs.setVisibility(View.VISIBLE);
  //    } else {
  //      tabs.setVisibility(View.GONE);
  //    }
}

private void updateYearDisplayAtSeekBar(Integer year) {
  yearDisplayAtSeekBar.setText("Year:\n" + String.valueOf(year));

}

private void setDataChangedToggle(boolean toggle) {
  DataController.setDataChanged(toggle);
}



private void setupGraphs(Integer currentYearMaximum) {

  GraphActivityFunctions.invalidateGraphs(GraphActivity.this);
  GraphActivityFunctions.highlightCurrentYearOnGraph(currentYearMaximum, GraphActivity.this);

}

private void recalcGraphPage() {
  //    Log.d(getClass().getName(), "entering recalcGraphPage");
  EditText maxValueEditText = (EditText) findViewById (R.id.maxValueEditText);
  EditText minValueEditText = (EditText) findViewById (R.id.minValueEditText);
  //    Log.d("recalcGraphPage", "currentValueNumeric is " + currentValueNumeric);
  //    Thread.dumpStack();

  minValueNumeric = GraphActivityFunctions.calculateMinFromCurrent(currentValueNumeric);
  maxValueNumeric = GraphActivityFunctions.calculateMaxFromCurrent(currentValueNumeric);
  deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(minValueNumeric, maxValueNumeric);

  GraphActivityFunctions.displayValue(currentValueEditText, currentValueNumeric, currentSliderKey);
  GraphActivityFunctions.displayValue(minValueEditText, minValueNumeric, currentSliderKey);
  GraphActivityFunctions.displayValue(maxValueEditText, maxValueNumeric, currentSliderKey);

  executeCalculationBackgroundTask();
  //    Log.d(getClass().getName(), "exiting recalcGraphPage");

}

private void executeCalculationBackgroundTask() {

  if (calculateInBackgroundTask == null) {
    calculateInBackgroundTask = new CalculateInBackgroundTask().execute();
    //separate these so it is not possible to try running a method (that being, checking
    //whether it is running or not) on a null pointer, however, if it's *not* null, we can check

    //if not running, or has been cancelled, start it
  } else if (calculateInBackgroundTask.getStatus() != AsyncTask.Status.RUNNING ||
      calculateInBackgroundTask.isCancelled() ) {
    calculateInBackgroundTask = new CalculateInBackgroundTask().execute();

    //if it is indeed running, do a clean restart
  } else if (calculateInBackgroundTask.getStatus() == AsyncTask.Status.RUNNING) {
    shutDownBackGroundThreadIfRunning();
    calculateInBackgroundTask = new CalculateInBackgroundTask().execute();

  }
}

private void sendFocusToJail() {

  findViewById(R.id.focusJail).requestFocus();

}

private void setupCurrentValueFields() {
  //    Log.d(getClass().getName(), "entering setupCurrentValueFields");

  Button resetButton;

  resetButton          = (Button)   findViewById(R.id.resetButton);
  currentValueEditText = (EditText) findViewById(R.id.currentValueEditText);
  final EditText minValueEditText     = (EditText) findViewById(R.id.minValueEditText);
  final EditText maxValueEditText     = (EditText) findViewById(R.id.maxValueEditText);

  resetButton.setOnClickListener(new OnClickListener() {

    @Override
    public void onClick(View v) {

      currentValueNumeric = originalCurrentValueNumeric;
      //        Log.d("Tag 007", "currentValueNumeric is " + currentValueNumeric);

      recalcGraphPage();

    }
  });


  currentValueEditText.setOnEditorActionListener(new OnEditorActionListener() {

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

      sendFocusToJail();
      return false;
    }
  });

  currentValueEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

      if (hasFocus) {
        Utility.setSelectionOnView(v, currentSliderKey);

      } else if (! hasFocus) {

        Double tempValueNumeric = GraphActivityFunctions.parseEditText(currentValueEditText, currentSliderKey);
        if (tempValueNumeric.equals(currentValueNumeric)) {

          Toast toast = Toast.makeText(GraphActivity.this, "You entered the same value as already existed for Current", Toast.LENGTH_LONG);
          toast.show();
        } else {
          shutDownBackGroundThreadIfRunning();
          currentValueNumeric = tempValueNumeric;

          recalcGraphPage();

        }
        GraphActivityFunctions.displayValue(currentValueEditText, currentValueNumeric, currentSliderKey);

      }
    }
  });

  minValueEditText.setOnEditorActionListener(new OnEditorActionListener() {

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

      //put focus on the invisible View - see graph.xml
      sendFocusToJail();

      return false;
    }
  });

  minValueEditText.setOnFocusChangeListener(new OnFocusChangeListener() {


    @Override
    public void onFocusChange(View v, boolean hasFocus) {

      if (hasFocus) {
        Utility.setSelectionOnView(v, currentSliderKey);
      } else if (! hasFocus) {

        Double tempMinValue = GraphActivityFunctions.parseEditText(minValueEditText, currentSliderKey);
        if (tempMinValue.equals(minValueNumeric)) {
          Toast toast = Toast.makeText(GraphActivity.this, "You entered the same value as already existed for Minimum", Toast.LENGTH_LONG);
          toast.show();
        } else if (tempMinValue < currentValueNumeric) {

          minValueNumeric = tempMinValue;
          deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(minValueNumeric, maxValueNumeric);
          GraphActivityFunctions.displayValue(minValueEditText, minValueNumeric, currentSliderKey);
          executeCalculationBackgroundTask();
        }  else {
          Toast toast = Toast.makeText(GraphActivity.this, "new Minimum must be less than Current value: " + currentValueEditText.getText().toString(), Toast.LENGTH_LONG);
          toast.show();
        }

        GraphActivityFunctions.displayValue(minValueEditText, minValueNumeric, currentSliderKey);

      }
    }
  });

  maxValueEditText.setOnEditorActionListener(new OnEditorActionListener() {

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

      sendFocusToJail();
      return false;
    }
  });

  maxValueEditText.setOnFocusChangeListener(new OnFocusChangeListener() {


    @Override
    public void onFocusChange(View v, boolean hasFocus) {

      if (hasFocus) {
        Utility.setSelectionOnView(v, currentSliderKey);
      } else if (! hasFocus) {

        Double tempMaxValue = GraphActivityFunctions.parseEditText(maxValueEditText, currentSliderKey);

        if (tempMaxValue.equals(maxValueNumeric)) {
          Toast toast = Toast.makeText(GraphActivity.this, "You entered the same value as already existed for Maximum", Toast.LENGTH_LONG);
          toast.show();
        } else if (tempMaxValue > currentValueNumeric) {
          //            Log.d("Tag 001", "currentValueNumeric is " + currentValueNumeric);
          maxValueNumeric = tempMaxValue;

          deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(minValueNumeric, maxValueNumeric);
          Log.d("", "deltaValueNumeric is " + deltaValueNumeric);
          GraphActivityFunctions.displayValue(maxValueEditText, maxValueNumeric, currentSliderKey);
          executeCalculationBackgroundTask();
        } else {
          Toast toast = Toast.makeText(GraphActivity.this, "new Maximum must be greater than Current value: " + currentValueEditText.getText().toString(), Toast.LENGTH_LONG);
          toast.show();
        }


        GraphActivityFunctions.displayValue(maxValueEditText, maxValueNumeric, currentSliderKey);
      }
    }
  });

  //    Log.d(getClass().getName(), "exiting setupCurrentValueFields");

}

private Double changeCurrentValueBasedOnProgress (int progress) {
  Log.d("in changeCurrentValuesBasedOnProgress", "deltaValueNumeric is " + deltaValueNumeric);

  return (minValueNumeric + ((progress / (double) DIVISIONS_OF_VALUE_SLIDER) * deltaValueNumeric));

}


private void setupValueSlider(Integer currentYearMaximum) {

  //    Log.d(getClass().getName(), "entering setupValueSlider");

  valueSlider = (SeekBar) findViewById(R.id.valueSlider);
  valueSlider.setMax(DIVISIONS_OF_VALUE_SLIDER);
  valueSlider.setProgress(valueSlider.getMax() / 2);

  yearDisplayAtSeekBar = (TextView) findViewById(R.id.yearLabel);
  updateYearDisplayAtSeekBar(currentYearMaximum);


  valueSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
      //empty - do nothing
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
      //empty - do nothing
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
        boolean fromUser) {

      //set the current division by the progress
      DataController.setCurrentDivisionForReading(progress);

      //set the value in the current value field:
      currentValueNumeric = changeCurrentValueBasedOnProgress(progress);
              Log.d("Tag 002", "currentValueNumeric is " + currentValueNumeric);

      dataController.setValueAsDouble(currentSliderKey, currentValueNumeric);
      GraphActivityFunctions.displayValue(currentValueEditText, currentValueNumeric, currentSliderKey);

      GraphActivityFunctions.invalidateGraphs(GraphActivity.this);

      Integer currentYearSelected = getCurrentYearSelected();
      dataTable.setDataTableItems( currentYearSelected, valueToDataTableItemCorrespondence);

    }
  });
  //    Log.d(getClass().getName(), "exiting setupValueSlider");

}

private void setupTimeSlider(Integer currentYearMaximum) {

  //    Log.d(getClass().getName(), "entering setupTimeSlider");

  timeSlider = (SeekBar) findViewById(R.id.timeSlider);
  timeSlider.setMax(currentYearMaximum - 1);
  timeSlider.setProgress(timeSlider.getMax());

  timeSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
      //do nothing here
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
      //do nothing here
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

      Integer currentYearSelected = getCurrentYearSelected();
      dataController.setCurrentYearSelected(currentYearSelected);

      updateYearDisplayAtSeekBar(currentYearSelected);

      dataTable.setDataTableItems(currentYearSelected, valueToDataTableItemCorrespondence);
      GraphActivityFunctions.highlightCurrentYearOnGraph(currentYearSelected, GraphActivity.this);
      GraphActivityFunctions.invalidateGraphs(GraphActivity.this);
    }
  });
  //    Log.d(getClass().getName(), "exiting setupTimeSlider");

}

private void setupValueSpinner() {
  //    Log.d(getClass().getName(), "entering setupValueSpinner");

  final Spinner valueSpinner = (Spinner) findViewById(R.id.valueSpinner);
  List<ValueEnum> selectionValues = new ArrayList<ValueEnum>();

  ValueEnum.ValueType vt;
  for (ValueEnum v : ValueEnum.values()) {

    vt = v.getType();

    if (!(vt == ValueType.STRING) && (v.isSavedToDatabase())) {
      selectionValues.add(v);
    }

    selectionValues.remove(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS);
    selectionValues.remove(ValueEnum.EXTRA_YEARS);
  }

  //sort the list
  //this will sort by how it is listed in the ValueEnum class.
  Collections.sort(selectionValues);

  spinnerArrayAdapter = new ArrayAdapter<ValueEnum>(this,
      android.R.layout.simple_spinner_dropdown_item, selectionValues);
  valueSpinner.setAdapter(spinnerArrayAdapter);

  //Is the line below necessary?  IT gets called in onResume
  //    setSpinnerSelection(currentSliderKey);

  valueSpinner.setOnItemSelectedListener(
      new OnItemSelectedListenerWrapper(new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
            long arg3) {
          //first we need to stop the background thread if running

          //sending focus to jail will make the current value save
          sendFocusToJail();
          currentSliderKey = spinnerArrayAdapter.getItem(pos);

          shutDownBackGroundThreadIfRunning();


          //the problem at this point is that if the background thread is operating,
          //the value this pulls could be anything on the range between the minimum
          //and maximum.  Therefore, to protect against that, I will set logic so
          //that it only tries to get currentValueNumeric this way if the
          //sliderkey is changing.  If the sliderKey is the same as before, don't
          //pull from the cache.

          //            Log.d("OnItemSelected", "oldcurrentSliderKey: " + oldCurrentSliderKey);
          //                Log.d("OnItemSelected", "newcurrentSliderKey: " + currentSliderKey);

          //            if (! oldCurrentSliderKey.equals(currentSliderKey)) {
          //                Log.d("OnItemSelected", "old and new currentSliderKey are different");
          //                Log.d("OnItemSelected", "currentValueNumeric was " + currentValueNumeric);
          currentValueNumeric = dataController.getValueAsDouble(currentSliderKey);
          //                Log.d("OnItemSelected", "currentValueNumeric is now " + currentValueNumeric);

          //            }
          //                Log.d("Tag 003", "currentValueNumeric is " + currentValueNumeric);
          //                Thread.dumpStack();

          //following is for the reset button.  It stores the new current value.
          originalCurrentValueNumeric = currentValueNumeric;

          recalcGraphPage();

}

@Override
public void onNothingSelected(AdapterView<?> arg0) {
  // do nothing with this. This method is necessary to satisfy interface.

}
}));

  //    Log.d(getClass().getName(), "exiting setupValueSpinner");

}

/**
 * This method sets the spinner on the graph page to the correct value
 * per the enumerated item supplied to it.
 * @param currentSliderKey
 */
private void setSpinnerSelection(ValueEnum currentSliderKey) {
  Spinner valueSpinner = (Spinner) findViewById(R.id.valueSpinner);
  //This will set the current selection from what was in the sharedPreferences save file
  ValueEnum oldCurrentSliderKey = spinnerArrayAdapter.getItem(valueSpinner.getSelectedItemPosition());

  //only if the new selection is different from what is already there.
  //This avoids firing off the background process multiple times, which messes up data
  if (oldCurrentSliderKey != currentSliderKey) {
    valueSpinner.setSelection(spinnerArrayAdapter.getPosition(currentSliderKey));
  }
}

private Integer getCurrentYearSelected() {
  Integer currentYearSelected = ((SeekBar) findViewById(R.id.timeSlider)).getProgress() + 1;

  return currentYearSelected;

}

private void lockSliderBars() {
  valueSlider.setEnabled(false);
  timeSlider.setEnabled(false);
}

private void unlockSliderBars() {
  valueSlider.setEnabled(true);
  timeSlider.setEnabled(true);
}


/**
 * sets the isCleanShutdown parameter
 * @param isCleanShutdown tells us whether the CalculateInBackgroundTask was able to finish all its cleanup tasks
 * after ending or being cancelled
 */
public void setCleanShutdown (Boolean cleanShutdownBackgroundTask) {
  this.cleanShutdownBackgroundTask = cleanShutdownBackgroundTask;
}

public Boolean isCleanShutdown() {
  return cleanShutdownBackgroundTask;
}

private class CalculateInBackgroundTask extends AsyncTask<Void, Integer, Void> {


  Double currentValueStorage = 0.0d;
  ValueEnum currentSliderKeyStorage;
  Double newCurrentValue = 0.0d;
  Integer yearsToCalculate = 0;

  
  @Override
  protected void onProgressUpdate(Integer... progress) {
    String updateValue = String.valueOf((Math.round(((float) (progress[0] )/ DIVISIONS_OF_VALUE_SLIDER)*100))) + "%";
    ((TextView) findViewById(R.id.customtitlebar)).setText("Calculating... " + updateValue);

  }

  @Override
  protected void onPostExecute(Void result) {

    //restore the original current value to the array
    dataController.setValueAsDouble(currentSliderKeyStorage, currentValueStorage);
    //      Log.d("onPostExecute", "currentSliderKeyStorage: " + currentSliderKeyStorage + " currentValueStorage: " + currentValueStorage);

    ((TextView) findViewById(R.id.customtitlebar)).setBackgroundColor(0);
    ((TextView) findViewById(R.id.customtitlebar)).setText(R.string.entryScreenActivityTitleText);

    GraphActivityFunctions.invalidateGraphs(GraphActivity.this);
    GraphActivityFunctions.highlightCurrentYearOnGraph(getCurrentYearSelected(), GraphActivity.this);

    //necessary in case the user switches between loan types (15 vs. 30 year)
    Integer extraYears = dataController.getValueAsDouble(ValueEnum.EXTRA_YEARS).intValue();
    Integer currentYearMaximum = Utility.getNumOfCompoundingPeriods() + extraYears;

    Integer currentYearSelected = GraphActivityFunctions.updateTimeSliderAfterChange (timeSlider, currentYearMaximum);
    updateYearDisplayAtSeekBar(currentYearSelected);
    dataController.setCurrentYearSelected(currentYearSelected);

    dataTable.setDataTableItems(currentYearSelected, valueToDataTableItemCorrespondence);


    DataController.setCurrentDivisionForReading(valueSlider.getMax() / 2);

    //we only set this when we are truly all done with calculating.
    //until it gets set to false, the system will keep trying to run this thread
    setDataChangedToggle(false);

    //unlock the valueSlider and timeSlider after processing
    unlockSliderBars();
    valueSlider.setProgress(0);
    valueSlider.setProgress(valueSlider.getMax() / 2);
    setCleanShutdown(true);
  }

  @Override
  protected void onCancelled() {
    //      Log.d("background thread onCancelled", "Entering onCancelled");

    //clean up - put the stored current value back in place
    dataController.setValueAsDouble(currentSliderKeyStorage, currentValueStorage);
    //      Log.d("background thread onCancelled", "currentSliderKeyStorage: " + currentSliderKeyStorage +
    //          "and currentValueStorage: " + currentValueStorage + " were restored");
    ((TextView) findViewById(R.id.customtitlebar)).setBackgroundColor(0);
    ((TextView) findViewById(R.id.customtitlebar)).setText(R.string.entryScreenActivityTitleText);
    //Following saves the data to persistence between onPause / onResume
    //It is here because this way we are guaranteed that the data is clean before persisting
    dataController.saveFieldValues();
    setCleanShutdown(true);
    
  }

  @Override
  protected void onPreExecute() {
  
    setCleanShutdown(false);
    currentValueStorage = currentValueNumeric;
    //      Log.d("onPreExecute", "currentValueNumeric is " + currentValueNumeric);

    currentSliderKeyStorage = currentSliderKey;
    //      Log.d("onPreExecute", "currentSliderKeyStorage is " + currentSliderKeyStorage);

    ((TextView) findViewById(R.id.customtitlebar)).setBackgroundColor(R.color.orange01);
    Integer yearlyPeriods = dataController.getValueAsDouble(
        ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS).intValue() / 12;

    Integer extraYears = dataController.getValueAsDouble(ValueEnum.EXTRA_YEARS).intValue();
    yearsToCalculate = yearlyPeriods + extraYears;
    dataController.initNumericCache(yearsToCalculate);

    //lock the valueSlider and timeSlider while processing
    lockSliderBars();
  }

  @Override
  protected Void doInBackground(Void... arg0) {


    for (int division = 0; division <= DIVISIONS_OF_VALUE_SLIDER; division++) {
      calculateEachDivision(division);
      if (isCancelled()) {
        break;
      }
      publishProgress(division);
    }

    return null;
  }

  private void calculateEachDivision(int division) {

    //get all the values for the current number and number of divisions
    //take those numbers and crunch them in the main equation, once for each division
    //then store them in a map of division numbers to crunched values
    DataController.setCurrentDivisionForWriting(division);
    newCurrentValue = changeCurrentValueBasedOnProgress (division);
    dataController.setValueAsDouble(currentSliderKeyStorage, newCurrentValue);
    new RentalUnitOwnership(dataController).crunchCalculation(yearsToCalculate);
  }
}
}
