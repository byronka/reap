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

  ProgressDialog progressDialog;

  ArrayAdapter<ValueEnum> spinnerArrayAdapter;
  Integer currentYearMaximum;
  Integer currentYearSelected;
  Button resetButton;
  Float minValueNumeric;
  Float maxValueNumeric;
  Float deltaValueNumeric;
  Float currentValueNumeric;
  boolean isConfigurationDisplayMode;

  TableLayout dataTableLayout;

  public static final int DIVISIONS_OF_VALUE_SLIDER = 15;
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
    
    if (DataController.isDataChanged()) {

    currentValueNumeric = dataController.getValueAsFloat(currentSliderKey);
    minValueNumeric = GraphActivityFunctions.calculateMinFromCurrent(currentValueNumeric);
    maxValueNumeric = GraphActivityFunctions.calculateMaxFromCurrent(currentValueNumeric);
    deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(minValueNumeric, maxValueNumeric);
    GraphActivityFunctions.displayValue(currentValueEditText, currentValueNumeric, currentSliderKey);
    GraphActivityFunctions.displayValue(minValueEditText, minValueNumeric, currentSliderKey);
    GraphActivityFunctions.displayValue(maxValueEditText, maxValueNumeric, currentSliderKey);
    
    valueSlider.setProgress(valueSlider.getMax() / 2);
    DataController.setCurrentDivisionForReading(valueSlider.getMax() / 2);

    //necessary in case the user switches between loan types (15 vs. 30 year)
    currentYearMaximum = Utility.getNumOfCompoundingPeriods();
    timeSlider.setMax(currentYearMaximum);
    timeSlider.setProgress(currentYearMaximum);
    yearDisplayAtSeekBar.setText("Year:\n" + String.valueOf(timeSlider.getProgress()));

    calculateInBackgroundTask = new CalculateInBackgroundTask().execute();
    }
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.graph);

    currentYearMaximum = Utility.getNumOfCompoundingPeriods();
    setupValueSpinner();
    setupTimeSlider();
    setupValueSlider();
    setupGraphs();
    setupCurrentValueFields();
    valueToDataTableItemCorrespondence = GraphActivityFunctions.createDataTableItems(GraphActivity.this);
    setupProgressGraphDialog();
    currentSliderKey = spinnerArrayAdapter.getItem(0);
    DataController.setDataChanged(true);
  }



  private void setupGraphs() {

    GraphActivityFunctions.invalidateGraphs(GraphActivity.this);
    currentYearSelected = currentYearMaximum;
    GraphActivityFunctions.highlightCurrentYearOnGraph(currentYearSelected, GraphActivity.this);

  }


  private void setupCurrentValueFields() {

    resetButton      = (Button)   findViewById(R.id.resetButton);
    currentValueEditText = (EditText) findViewById(R.id.currentValueEditText);
    minValueEditText = (EditText) findViewById(R.id.minValueEditText);
    maxValueEditText = (EditText) findViewById(R.id.maxValueEditText);

    resetButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        valueSlider.setProgress(valueSlider.getMax() / 2);

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
          GraphActivityFunctions.displayValue(currentValueEditText, currentValueNumeric, currentSliderKey);
          dataController.setValueAsFloat(currentSliderKey, currentValueNumeric);

          minValueNumeric = GraphActivityFunctions.calculateMinFromCurrent(currentValueNumeric);
          GraphActivityFunctions.displayValue(minValueEditText, minValueNumeric, currentSliderKey);

          maxValueNumeric = GraphActivityFunctions.calculateMaxFromCurrent(currentValueNumeric);
          GraphActivityFunctions.displayValue(maxValueEditText, maxValueNumeric, currentSliderKey);

          valueSlider.setProgress(valueSlider.getMax() / 2);
          calculateInBackgroundTask = new CalculateInBackgroundTask().execute();
          deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(minValueNumeric, maxValueNumeric);
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

      Float tempMinValue = 0.0f;
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
          Utility.setSelectionOnView(v, currentSliderKey.getType());
        } else if (! hasFocus) {
          
          tempMinValue = GraphActivityFunctions.parseEditText(minValueEditText, currentSliderKey);
          GraphActivityFunctions.displayValue(minValueEditText, tempMinValue, currentSliderKey);

          currentValueNumeric = GraphActivityFunctions.calculateCurrentFromMin(tempMinValue);
          GraphActivityFunctions.displayValue(currentValueEditText, currentValueNumeric, currentSliderKey);
          dataController.setValueAsFloat(currentSliderKey, currentValueNumeric);

          maxValueNumeric = GraphActivityFunctions.calculateMaxFromCurrent(currentValueNumeric);
          GraphActivityFunctions.displayValue(maxValueEditText, maxValueNumeric, currentSliderKey);
          
          deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(minValueNumeric, maxValueNumeric);
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

      Float tempMaxValue = 0.0f;
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
          Utility.setSelectionOnView(v, currentSliderKey.getType());
        } else if (! hasFocus) {
          
          tempMaxValue = GraphActivityFunctions.parseEditText(minValueEditText, currentSliderKey);
          GraphActivityFunctions.displayValue(minValueEditText, tempMaxValue, currentSliderKey);

          currentValueNumeric = GraphActivityFunctions.calculateCurrentFromMax(tempMaxValue);
          GraphActivityFunctions.displayValue(currentValueEditText, currentValueNumeric, currentSliderKey);
          dataController.setValueAsFloat(currentSliderKey, currentValueNumeric);
          
          minValueNumeric = GraphActivityFunctions.calculateMinFromCurrent(currentValueNumeric);
          GraphActivityFunctions.displayValue(minValueEditText, minValueNumeric, currentSliderKey);
          
          deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(minValueNumeric, maxValueNumeric);
        }
      }
    });

  }

  private void setupProgressGraphDialog() {
    progressDialog = new ProgressDialog(GraphActivity.this);
    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    progressDialog.setMax(DIVISIONS_OF_VALUE_SLIDER);
    progressDialog.setMessage("Loading...");
    progressDialog.setCancelable(false);
    progressDialog.show();
  }


  private void setupValueSlider() {


    valueSlider = (SeekBar) findViewById(R.id.valueSlider);
    valueSlider.setMax(DIVISIONS_OF_VALUE_SLIDER);
    valueSlider.setProgress(valueSlider.getMax() / 2);

    yearDisplayAtSeekBar = (TextView) findViewById(R.id.yearLabel);
    yearDisplayAtSeekBar.setText("Year:\n" + String.valueOf(currentYearMaximum));


    valueSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

      Float percentageSlid;

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress,
          boolean fromUser) {

        //set the current division by the progress
        DataController.setCurrentDivisionForReading(progress);

        //set the value in the current value field:
        percentageSlid = (progress / (float) DIVISIONS_OF_VALUE_SLIDER);
        currentValueNumeric = minValueNumeric + (percentageSlid * deltaValueNumeric);

        switch (currentSliderKey.getType()) {
        case CURRENCY:
          currentValueEditText.setText(Utility.displayCurrency(currentValueNumeric));
          break;
        case PERCENTAGE:
          currentValueEditText.setText(Utility.displayPercentage(currentValueNumeric));
          break;
        case INTEGER:
          currentValueEditText.setText(String.valueOf(currentValueNumeric));
          break;
        default:
          System.err.println("Should not get here in valueSlider.setOnSeekBarChangeListener");
          break;
        }

        //set the values for use by the graphs and data tables
        GraphActivityFunctions.invalidateGraphs(GraphActivity.this);
        setDataTableItems(dataTableItems, currentYearSelected);

      }
    });

  }

  private void setupTimeSlider() {


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
          yearDisplayAtSeekBar.setText("Year:\n" + String.valueOf(progress));

          currentYearSelected = progress;
          setDataTableItems(dataTableItems, currentYearSelected);
          GraphActivityFunctions.highlightCurrentYearOnGraph(currentYearSelected, GraphActivity.this);
          GraphActivityFunctions.invalidateGraphs(GraphActivity.this);
        }
      }
    });
  }

  private void setupValueSpinner() {

    Spinner valueSpinner = (Spinner) findViewById(R.id.valueSpinner);

    //  ArrayList<ValueEnum> spinnerValuesArray = new ArrayList<ValueEnum>(Arrays.asList(ValueEnum.values()));
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
        minValueNumeric = GraphActivityFunctions.calculateMinFromCurrent(currentValueNumeric);
        maxValueNumeric = GraphActivityFunctions.calculateMaxFromCurrent(currentValueNumeric);
        calculateInBackgroundTask = new CalculateInBackgroundTask().execute();

      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
        // do nothing with this. This method is necessary to satisfy interface.

      }
    }));

  }

//  private void setMinAndMaxFromCurrent(Float currentValueNumeric) {
//    minValueNumeric = currentValueNumeric / 2;
//    maxValueNumeric = currentValueNumeric + (currentValueNumeric - minValueNumeric);
//    deltaValueNumeric = maxValueNumeric - minValueNumeric;
//    ValueType test = currentSliderKey.getType(); 
//
//    switch (test) {
//    case CURRENCY:
//
//      maxValueEditText.setText(Utility.displayCurrency(maxValueNumeric));
//      minValueEditText.setText(Utility.displayCurrency(minValueNumeric));
//      currentValueEditText.setText(Utility.displayCurrency(currentValueNumeric));
//      break;
//    case PERCENTAGE:
//
//      maxValueEditText.setText(Utility.displayPercentage(maxValueNumeric));
//      minValueEditText.setText(Utility.displayPercentage(minValueNumeric));
//      currentValueEditText.setText(Utility.displayPercentage(currentValueNumeric));
//      break;
//    default:
//      System.err.println("Should not get here in maxValueEditText.setOnFocusChangeListener");
//      break;
//    }
//
//  }

  

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

        if (ve.isVaryingByYear()) {
          tempDataTablePropertyValue.setText(Utility.displayCurrency(dataController.getValueAsFloat(ve, year)));
        } else if (! ve.isVaryingByYear()) {
          tempDataTablePropertyValue.setText(Utility.displayCurrency(dataController.getValueAsFloat(ve)));
        }
        break;


      case PERCENTAGE:


        if (ve.isVaryingByYear()) {
          tempDataTablePropertyValue.setText(Utility.displayPercentage(dataController.getValueAsFloat(ve, year)));
        } else if (! ve.isVaryingByYear()) {
          tempDataTablePropertyValue.setText(Utility.displayPercentage(dataController.getValueAsFloat(ve)));
        }
        break;


      case STRING:

        tempDataTablePropertyValue.setText(dataController.getValueAsString(ve));
        break;

      case INTEGER:

        if (ve.isVaryingByYear()) {
          tempDataTablePropertyValue.setText(String.valueOf(dataController.getValueAsFloat(ve, year).intValue()));
        } else if (! ve.isVaryingByYear()) {
          tempDataTablePropertyValue.setText(String.valueOf(dataController.getValueAsFloat(ve).intValue()));
        }
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



  private class CalculateInBackgroundTask extends AsyncTask<Void, Integer, Void> {
    Float newCurrentValue = 0.0f;


    @Override
    protected void onProgressUpdate(Integer... progress) {
      progressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(Void result) {
      progressDialog.dismiss();
      GraphActivityFunctions.invalidateGraphs(GraphActivity.this);
      setDataTableItems(dataTableItems, currentYearSelected);
      DataController.setDataChanged(false);
    }

    @Override
    protected void onPreExecute() {
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
