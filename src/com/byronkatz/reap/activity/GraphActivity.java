package com.byronkatz.reap.activity;


import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.byronkatz.R;

import com.byronkatz.reap.general.CalculatedVariables;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.OnItemSelectedListenerWrapper;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

public class GraphActivity extends Activity {

  ValueEnum currentSliderKey;
  EditText currentValueEditText;
  SeekBar valueSlider;
  SeekBar timeSlider;
  TextView yearDisplayAtSeekBar;
  EditText minValueEditText;
  EditText maxValueEditText;
  static final DataController dataController = RealEstateMarketAnalysisApplication
      .getInstance().getDataController();
  Map<ValueEnum, TableRow> valueToDataTableItemCorrespondence;


  ArrayAdapter<ValueEnum> spinnerArrayAdapter;
  Integer currentYearSelected;
  Float minValueNumeric;
  Float maxValueNumeric;
  Float deltaValueNumeric;
  Float currentValueNumeric;
  Float originalCurrentValueNumeric;

  public static final int DIVISIONS_OF_VALUE_SLIDER = 40;
  public static final int PROPERTY_LABEL_INDEX = 0;
  public static final int PROPERTY_VALUE_INDEX = 1;
  public static final int TOGGLE_BUTTON_INDEX = 2;
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

    GraphActivityFunctions.switchForMenuItem(item, GraphActivity.this);

    return false;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == CONFIGURE_DATA_TABLE_ACTIVITY_REQUEST_CODE) {
      makeSelectedRowsVisible(dataController.getViewableDataTableRows());
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    colorTheDataTables();
    
    if (DataController.isDataChanged()) {

      currentValueNumeric = dataController.getValueAsFloat(currentSliderKey);
      //following is for the reset button
      originalCurrentValueNumeric = currentValueNumeric;
      
      //necessary in case the user switches between loan types (15 vs. 30 year)
      Integer currentYearMaximum = Utility.getNumOfCompoundingPeriods();
      GraphActivityFunctions.updateTimeSliderAfterChange (timeSlider, currentYearMaximum);
      currentYearSelected = currentYearMaximum;
      //necessary to do the following or else the Year will not update right after the change
      updateYearDisplayAtSeekBar(currentYearMaximum);
      
      setCurrentValueHeaderDataValues();
    }
  }

  private void updateYearDisplayAtSeekBar(Integer year) {
    yearDisplayAtSeekBar.setText("Year:\n" + String.valueOf(year));

  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.graph);

    Integer currentYearMaximum = Utility.getNumOfCompoundingPeriods();
    setupValueSpinner();
    setupTimeSlider(currentYearMaximum);
    setupValueSlider(currentYearMaximum);
    setupGraphs(currentYearMaximum);
    setupCurrentValueFields();
    valueToDataTableItemCorrespondence = GraphActivityFunctions.createDataTableItems(GraphActivity.this);
    currentSliderKey = spinnerArrayAdapter.getItem(0);
    DataController.setDataChanged(true);
  }



  private void setupGraphs(Integer currentYearMaximum) {

    GraphActivityFunctions.invalidateGraphs(GraphActivity.this);
    currentYearSelected = currentYearMaximum;
    GraphActivityFunctions.highlightCurrentYearOnGraph(currentYearSelected, GraphActivity.this);

  }

  private void setCurrentValueHeaderDataValues() {

    minValueNumeric = GraphActivityFunctions.calculateMinFromCurrent(currentValueNumeric);
    maxValueNumeric = GraphActivityFunctions.calculateMaxFromCurrent(currentValueNumeric);
    deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(minValueNumeric, maxValueNumeric);
    GraphActivityFunctions.displayValue(currentValueEditText, currentValueNumeric, currentSliderKey);
    GraphActivityFunctions.displayValue(minValueEditText, minValueNumeric, currentSliderKey);
    GraphActivityFunctions.displayValue(maxValueEditText, maxValueNumeric, currentSliderKey);

    valueSlider.setProgress(valueSlider.getMax() / 2);
    //the following line is a test!
    DataController.setCurrentDivisionForReading(valueSlider.getMax() / 2);

    dataController.setValueAsFloat(currentSliderKey, currentValueNumeric);

    calculateInBackgroundTask = new CalculateInBackgroundTask().execute();

  }

  private void setupCurrentValueFields() {

    Button resetButton;

    resetButton          = (Button)   findViewById(R.id.resetButton);
    currentValueEditText = (EditText) findViewById(R.id.currentValueEditText);
    minValueEditText     = (EditText) findViewById(R.id.minValueEditText);
    maxValueEditText     = (EditText) findViewById(R.id.maxValueEditText);

    resetButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        currentValueNumeric = originalCurrentValueNumeric;

        setCurrentValueHeaderDataValues();

      }
    });



    currentValueEditText.setOnEditorActionListener(new OnEditorActionListener() {

      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        findViewById(R.id.focusJail).requestFocus();

        return false;
      }
    });

    currentValueEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

      @Override
      public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
          Utility.setSelectionOnView(v, currentSliderKey.getType());
        } else if (! hasFocus) {

          currentValueNumeric = GraphActivityFunctions.parseEditText(currentValueEditText, currentSliderKey);

          setCurrentValueHeaderDataValues();

          GraphActivityFunctions.displayValue(currentValueEditText, currentValueNumeric, currentSliderKey);

        }
      }
    });

    minValueEditText.setOnEditorActionListener(new OnEditorActionListener() {

      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        //put focus on the invisible View - see graph.xml
        findViewById(R.id.focusJail).requestFocus();

        return false;
      }
    });

    minValueEditText.setOnFocusChangeListener(new OnFocusChangeListener() {


      @Override
      public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
          Utility.setSelectionOnView(v, currentSliderKey.getType());
        } else if (! hasFocus) {

          Float tempMinValue = GraphActivityFunctions.parseEditText(minValueEditText, currentSliderKey);
          if (tempMinValue < currentValueNumeric) {
            minValueNumeric = tempMinValue;

            deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(minValueNumeric, maxValueNumeric);

            GraphActivityFunctions.displayValue(minValueEditText, minValueNumeric, currentSliderKey);

            calculateInBackgroundTask = new CalculateInBackgroundTask().execute();
          }

          GraphActivityFunctions.displayValue(minValueEditText, minValueNumeric, currentSliderKey);

        }
      }
    });

    maxValueEditText.setOnEditorActionListener(new OnEditorActionListener() {

      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        findViewById(R.id.focusJail).requestFocus();

        return false;
      }
    });

    maxValueEditText.setOnFocusChangeListener(new OnFocusChangeListener() {


      @Override
      public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
          Utility.setSelectionOnView(v, currentSliderKey.getType());
        } else if (! hasFocus) {

          Float tempMaxValue = GraphActivityFunctions.parseEditText(maxValueEditText, currentSliderKey);
          if (tempMaxValue > currentValueNumeric) {
            maxValueNumeric = tempMaxValue;

            deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(minValueNumeric, maxValueNumeric);

            GraphActivityFunctions.displayValue(maxValueEditText, maxValueNumeric, currentSliderKey);

            calculateInBackgroundTask = new CalculateInBackgroundTask().execute();
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
    yearDisplayAtSeekBar.setText("Year:\n" + String.valueOf(currentYearMaximum));


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

        //set the values for use by the graphs and data tables
        GraphActivityFunctions.invalidateGraphs(GraphActivity.this);
        setDataTableItems(dataTableItems, currentYearSelected);

      }
    });

  }

  private void setupTimeSlider(Integer currentYearMaximum) {


    timeSlider = (SeekBar) findViewById(R.id.timeSlider);
    timeSlider.setMax(currentYearMaximum);
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
        if ( progress > 0 ) {
          currentYearSelected = progress;

          updateYearDisplayAtSeekBar(progress);

          setDataTableItems(dataTableItems, progress);
          GraphActivityFunctions.highlightCurrentYearOnGraph(progress, GraphActivity.this);
          GraphActivityFunctions.invalidateGraphs(GraphActivity.this);
        }
      }
    });
  }

  private void setupValueSpinner() {

    Spinner valueSpinner = (Spinner) findViewById(R.id.valueSpinner);

    //  ArrayList<ValueEnum> spinnerValuesArray = new ArrayList<ValueEnum>(Arrays.asList(ValueEnum.values()));
    //TODO: set up for loop to select Enums that don't vary by year and aren't strings

    ValueEnum[] selectionValues = {
        ValueEnum.DOWN_PAYMENT,
        ValueEnum.ESTIMATED_RENT_PAYMENTS,
        ValueEnum.FIX_UP_COSTS,
        ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES,
        ValueEnum.REAL_ESTATE_APPRECIATION_RATE,
        ValueEnum.REQUIRED_RATE_OF_RETURN,
        ValueEnum.TOTAL_PURCHASE_VALUE,
        ValueEnum.YEARLY_INTEREST_RATE
    };

    spinnerArrayAdapter = new ArrayAdapter<ValueEnum>(this,
        android.R.layout.simple_spinner_dropdown_item, selectionValues);
    valueSpinner.setAdapter(spinnerArrayAdapter);


    valueSpinner.setOnItemSelectedListener(
        new OnItemSelectedListenerWrapper(new OnItemSelectedListener() {

          @Override
          public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
              long arg3) {
            currentSliderKey = spinnerArrayAdapter.getItem(pos);
            currentValueNumeric = dataController.getValueAsFloat(currentSliderKey);

            //following is for the reset button
            originalCurrentValueNumeric = currentValueNumeric;

            setCurrentValueHeaderDataValues();

          }

          @Override
          public void onNothingSelected(AdapterView<?> arg0) {
            // do nothing with this. This method is necessary to satisfy interface.

          }
        }));

  }


  private void setDataTableItems(ValueEnum[] items, Integer year) {

    ValueEnum ve;
    TableRow tempTableRow;
    TextView tempDataTablePropertyValue;

    for (Entry<ValueEnum, TableRow> entry : valueToDataTableItemCorrespondence.entrySet()) {

      ve = entry.getKey();
      tempTableRow = entry.getValue();
      tempDataTablePropertyValue = (TextView) tempTableRow.getChildAt(PROPERTY_VALUE_INDEX);


      switch (ve.getType()) {
      case CURRENCY:

        GraphActivityFunctions.setDataTableValueByCurrency(tempDataTablePropertyValue, ve, year);

        break;

      case PERCENTAGE:

        GraphActivityFunctions.setDataTableValueByPercentage(tempDataTablePropertyValue, ve, year);

        break;


      case STRING:

        tempDataTablePropertyValue.setText(dataController.getValueAsString(ve));
        break;

      case INTEGER:
        GraphActivityFunctions.setDataTableValueByInteger(tempDataTablePropertyValue, ve, year);

        break;        
      default:
        break;
      }
    }

  }
  

  
  public void makeSelectedRowsVisible(Set<ValueEnum> values) {
    TableRow tempTableRow;

    for (Entry<ValueEnum, TableRow> entry : valueToDataTableItemCorrespondence.entrySet()) {
      tempTableRow = entry.getValue();
      if (values.contains(entry.getKey())) {
        tempTableRow.setVisibility(View.VISIBLE);
      } else if (! values.contains(entry.getKey())) {
        tempTableRow.setVisibility(View.GONE);
      }
    }
  }

  private void colorTheDataTables() {
    TableLayout dataTableLayout = (TableLayout) findViewById(R.id.dataTableLayout);
    Set<ValueEnum> viewableDataTableRows = dataController.getViewableDataTableRows();
    
    GraphActivityFunctions.setColorDataTableRows(dataTableLayout, viewableDataTableRows, valueToDataTableItemCorrespondence);
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
      setDataTableItems(dataTableItems, currentYearSelected);

      DataController.setDataChanged(false);
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
      CalculatedVariables.crunchCalculation();
    }
  }
}
