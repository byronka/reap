package com.byronkatz.reap.activity;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
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
  private TabHost tabs;


  private final DataController dataController = RealEstateMarketAnalysisApplication
      .getInstance().getDataController();
  Map<ValueEnum, TableRow> valueToDataTableItemCorrespondence;

  Float minValueNumeric;
  Float maxValueNumeric;
  Float deltaValueNumeric;
  Float currentValueNumeric;
  Float originalCurrentValueNumeric;

  SharedPreferences sp;

  public static final int DIVISIONS_OF_VALUE_SLIDER = 40;
  public static final int CONFIGURE_DATA_TABLE_ACTIVITY_REQUEST_CODE = 1;

  ValueEnum[] dataTableItems = ValueEnum.values();
  Float percentageSlid;
  public static AsyncTask<Void, Integer, Void> calculateInBackgroundTask;

  @Override
  public boolean onCreateOptionsMenu (Menu menu){
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.data_pages_menu, menu);
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

    GraphActivityFunctions.switchForMenuItem(item, GraphActivity.this, 
        getCurrentYearSelected(), isGraphVisible);
    return false;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == CONFIGURE_DATA_TABLE_ACTIVITY_REQUEST_CODE) {
      dataTable.makeSelectedRowsVisible(dataController.getViewableDataTableRows(), valueToDataTableItemCorrespondence);
     
      if (data != null) {
      isGraphVisible = data.getExtras().getBoolean(IS_GRAPH_VISIBLE, true);
      }

      if (isGraphVisible) {
        tabs.setVisibility(View.VISIBLE);
      } else {
        tabs.setVisibility(View.GONE);
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    if (DataController.isDataChanged()) {

      currentValueNumeric = dataController.getValueAsFloat(currentSliderKey);
      //following is for the reset button
      originalCurrentValueNumeric = currentValueNumeric;

      //necessary in case the user switches between loan types (15 vs. 30 year)
      Integer currentYearMaximum = Utility.getNumOfCompoundingPeriods();
      GraphActivityFunctions.updateTimeSliderAfterChange (timeSlider, currentYearMaximum);
      //necessary to do the following or else the Year will not update right after the change
      updateYearDisplayAtSeekBar(currentYearMaximum);
      dataTable.colorTheDataTables();
      recalcGraphPage();
    }
  }



  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    dataTable = new DataTable(this);

    if (savedState != null) {
      dataController.setViewableDataTableRows(
          dataTable.restoreViewableDataTableRows(savedState));
    } else {

      sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
      dataController.setViewableDataTableRows(
          dataTable.restoreViewableDataTableRows(sp));
      isGraphVisible = sp.getBoolean(IS_GRAPH_VISIBLE, false);

    }

    setContentView(R.layout.graph);

    Integer currentYearMaximum = Utility.getNumOfCompoundingPeriods();
    setupValueSpinner();
    setupTimeSlider(currentYearMaximum);
    setupValueSlider(currentYearMaximum);
    setupGraphs(currentYearMaximum);
    setupCurrentValueFields();
    valueToDataTableItemCorrespondence = dataTable.createDataTableItems(GraphActivity.this);

    setupGraphTabs();

  }

  @Override
  public void onPause() {
    super.onPause();

    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);

    dataTable.saveGraphPageData(sharedPreferences, isGraphVisible);
  }

  @Override
  public void onRestoreInstanceState(Bundle outState) {

    super.onRestoreInstanceState(outState);
    dataController.setViewableDataTableRows(
        dataTable.restoreViewableDataTableRows(outState));

  }

  @Override
  public void onSaveInstanceState(Bundle outState) {

    dataTable.saveViewableDataTableRows(outState);
    super.onSaveInstanceState(outState);
  }

  private void setupGraphTabs() {
    tabs = (TabHost) findViewById(android.R.id.tabhost);        
    tabs.setup();

    TabHost.TabSpec spec = tabs.newTabSpec("NPV");
    spec.setContent(R.id.tab1);
    spec.setIndicator("NPV");
    tabs.addTab(spec);

    spec = tabs.newTabSpec("ATCF");
    spec.setContent(R.id.tab2);
    spec.setIndicator("ATCF");
    tabs.addTab(spec);

    spec = tabs.newTabSpec("ATER");
    spec.setContent(R.id.tab3);
    spec.setIndicator("ATER");
    tabs.addTab(spec);

    spec = tabs.newTabSpec("MIRR");
    spec.setContent(R.id.tab4);
    spec.setIndicator("MIRR");
    tabs.addTab(spec);

    if (isGraphVisible) {
      tabs.setVisibility(View.VISIBLE);
    } else {
      tabs.setVisibility(View.GONE);
    }
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
    EditText maxValueEditText = (EditText) findViewById (R.id.maxValueEditText);
    EditText minValueEditText = (EditText) findViewById (R.id.minValueEditText);


    minValueNumeric = GraphActivityFunctions.calculateMinFromCurrent(currentValueNumeric);
    maxValueNumeric = GraphActivityFunctions.calculateMaxFromCurrent(currentValueNumeric);
    deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(minValueNumeric, maxValueNumeric);
    GraphActivityFunctions.displayValue(currentValueEditText, currentValueNumeric, currentSliderKey);
    GraphActivityFunctions.displayValue(minValueEditText, minValueNumeric, currentSliderKey);
    GraphActivityFunctions.displayValue(maxValueEditText, maxValueNumeric, currentSliderKey);

    valueSlider.setProgress(valueSlider.getMax() / 2);
    DataController.setCurrentDivisionForReading(valueSlider.getMax() / 2);

    dataController.setValueAsFloat(currentSliderKey, currentValueNumeric);

    calculateInBackgroundTask = new CalculateInBackgroundTask().execute();

  }

  private void sendFocusToJail() {

    findViewById(R.id.focusJail).requestFocus();

  }

  private void setupCurrentValueFields() {

    Button resetButton;

    resetButton          = (Button)   findViewById(R.id.resetButton);
    currentValueEditText = (EditText) findViewById(R.id.currentValueEditText);
    final EditText minValueEditText     = (EditText) findViewById(R.id.minValueEditText);
    final EditText maxValueEditText     = (EditText) findViewById(R.id.maxValueEditText);

    resetButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        currentValueNumeric = originalCurrentValueNumeric;
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

          currentValueNumeric = GraphActivityFunctions.parseEditText(currentValueEditText, currentSliderKey);
          recalcGraphPage();
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

          Float tempMinValue = GraphActivityFunctions.parseEditText(minValueEditText, currentSliderKey);
          if (tempMinValue < currentValueNumeric) {
            minValueNumeric = tempMinValue;
            deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(minValueNumeric, maxValueNumeric);
            GraphActivityFunctions.displayValue(minValueEditText, minValueNumeric, currentSliderKey);
            calculateInBackgroundTask = new CalculateInBackgroundTask().execute();
          } else {
            Toast toast = Toast.makeText(GraphActivity.this, "new min value must be less than current value", Toast.LENGTH_SHORT);
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

          Float tempMaxValue = GraphActivityFunctions.parseEditText(maxValueEditText, currentSliderKey);
          if (tempMaxValue > currentValueNumeric) {
            maxValueNumeric = tempMaxValue;

            deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(minValueNumeric, maxValueNumeric);
            GraphActivityFunctions.displayValue(maxValueEditText, maxValueNumeric, currentSliderKey);
            calculateInBackgroundTask = new CalculateInBackgroundTask().execute();
          } else {
            Toast toast = Toast.makeText(GraphActivity.this, "new max value must be greater than current value", Toast.LENGTH_SHORT);
            toast.show();
          }


          GraphActivityFunctions.displayValue(maxValueEditText, maxValueNumeric, currentSliderKey);
        }
      }
    });

  }




  private void setupValueSlider(Integer currentYearMaximum) {


    valueSlider = (SeekBar) findViewById(R.id.valueSlider);
    valueSlider.setMax(DIVISIONS_OF_VALUE_SLIDER);
    valueSlider.setProgress(valueSlider.getMax() / 2);

    yearDisplayAtSeekBar = (TextView) findViewById(R.id.yearLabel);
    updateYearDisplayAtSeekBar(currentYearMaximum);


    valueSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

      Float percentageSlid;

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
        percentageSlid = (progress / (float) DIVISIONS_OF_VALUE_SLIDER);
        currentValueNumeric = minValueNumeric + (percentageSlid * deltaValueNumeric);
        dataController.setValueAsFloat(currentSliderKey, currentValueNumeric);
        GraphActivityFunctions.displayValue(currentValueEditText, currentValueNumeric, currentSliderKey);

        GraphActivityFunctions.invalidateGraphs(GraphActivity.this);

        Integer currentYearSelected = getCurrentYearSelected();
        dataTable.setDataTableItems(dataTableItems, currentYearSelected, valueToDataTableItemCorrespondence);

      }
    });

  }

  private void setupTimeSlider(Integer currentYearMaximum) {


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

        Integer currentYearSelected = progress + 1;
        updateYearDisplayAtSeekBar(currentYearSelected);

        dataTable.setDataTableItems(dataTableItems, currentYearSelected, valueToDataTableItemCorrespondence);
        GraphActivityFunctions.highlightCurrentYearOnGraph(currentYearSelected, GraphActivity.this);
        GraphActivityFunctions.invalidateGraphs(GraphActivity.this);
      }
    });
  }

  private void setupValueSpinner() {

    Spinner valueSpinner = (Spinner) findViewById(R.id.valueSpinner);


    List<ValueEnum> selectionValues = new ArrayList<ValueEnum>();

    ValueEnum.ValueType vt;
    for (ValueEnum v : ValueEnum.values()) {

      vt = v.getType();

      if (!(vt == ValueType.STRING) && (v.isSavedToDatabase())) {
        selectionValues.add(v);
      }

      selectionValues.remove(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS);
    }

    //sort the list
    //this will sort by how it is listed in the ValueEnum class.
    Collections.sort(selectionValues);

    final ArrayAdapter<ValueEnum> spinnerArrayAdapter;
    spinnerArrayAdapter = new ArrayAdapter<ValueEnum>(this,
        android.R.layout.simple_spinner_dropdown_item, selectionValues);
    valueSpinner.setAdapter(spinnerArrayAdapter);

    currentSliderKey = spinnerArrayAdapter.getItem(0);

    valueSpinner.setOnItemSelectedListener(
        new OnItemSelectedListenerWrapper(new OnItemSelectedListener() {

          @Override
          public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
              long arg3) {
            currentSliderKey = spinnerArrayAdapter.getItem(pos);
            currentValueNumeric = dataController.getValueAsFloat(currentSliderKey);

            //following is for the reset button
            originalCurrentValueNumeric = currentValueNumeric;

            recalcGraphPage();

          }

          @Override
          public void onNothingSelected(AdapterView<?> arg0) {
            // do nothing with this. This method is necessary to satisfy interface.

          }
        }));

  }

  private Integer getCurrentYearSelected() {
    Integer currentYearSelected = ((SeekBar) findViewById(R.id.timeSlider)).getProgress() + 1;
    dataController.setCurrentYearSelected(currentYearSelected);
    
    return currentYearSelected;

  }

  private class CalculateInBackgroundTask extends AsyncTask<Void, Integer, Void> {
    Float newCurrentValue = 0.0f;

    ProgressDialog progressDialog;


    @Override
    protected void onProgressUpdate(Integer... progress) {
      progressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(Void result) {

      //restore the original current value to the array
      dataController.setValueAsFloat(currentSliderKey, currentValueNumeric);

      progressDialog.dismiss();
      GraphActivityFunctions.invalidateGraphs(GraphActivity.this);

      Integer currentYearSelected = getCurrentYearSelected();
      dataTable.setDataTableItems(dataTableItems, currentYearSelected, valueToDataTableItemCorrespondence);

      setDataChangedToggle(false);
    }

    @Override
    protected void onPreExecute() {

      progressDialog = GraphActivityFunctions.setupProgressGraphDialog(GraphActivity.this);
      progressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... arg0) {

      for (int division = 0; division <= DIVISIONS_OF_VALUE_SLIDER; division++) {
        calculateEachDivision(division);
        publishProgress(division);
      }
      return null;
    }

    private void calculateEachDivision(int division) {

      //get all the values for the current number and number of divisions
      //take those numbers and crunch them in the main equation, once for each division
      //then store them in a map of division numbers to crunched values
      DataController.setCurrentDivisionForWriting(division);
      percentageSlid = (division / (float) DIVISIONS_OF_VALUE_SLIDER);
      newCurrentValue = minValueNumeric + (percentageSlid * deltaValueNumeric);
      dataController.setValueAsFloat(currentSliderKey, newCurrentValue);
      new RentalUnitOwnership(dataController).crunchCalculation();
    }
  }
}
