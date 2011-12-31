package com.byronkatz;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.widget.Toast;

import com.byronkatz.ValueEnum.ValueType;

public class GraphActivity extends Activity {

  ValueEnum currentSliderKey;
  AnalysisGraph npvGraph;
  //  AnalysisGraph aterGraph;
  AnalysisGraph atcfGraph;
  EditText currentValueEditText;
  SeekBar valueSlider;
  SeekBar timeSlider;
  TextView yearDisplayAtSeekBar;
  EditText minValueEditText;
  EditText maxValueEditText;
  static final DataController dataController = RealEstateMarketAnalysisApplication
      .getInstance().getDataController();
  Map<ValueEnum, TableRow> valueToDataTableItemCorrespondence;
  Set<ValueEnum> viewableDataTableRows;

  Spinner valueSpinner;
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

  static final int DIVISIONS_OF_VALUE_SLIDER = 100;
  static final int PROPERTY_LABEL_INDEX = 0;
  static final int PROPERTY_VALUE_INDEX = 1;
  static final int TOGGLE_BUTTON_INDEX = 2;
  static final int CONFIGURE_DATA_TABLE_ACTIVITY_REQUEST_CODE = 1;

  ValueEnum[] dataTableItems = ValueEnum.values();
  Float percentageSlid;
  Float newCurrentValue;

  @Override
  public boolean onCreateOptionsMenu (Menu menu){
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.data_pages_menu, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    MenuItem menuItem = menu.findItem(R.id.configureGraphPageMenuItem);

    if (isConfigurationDisplayMode) {
      menuItem.setTitle("Show data display");

    } else if (! isConfigurationDisplayMode) {
      menuItem.setTitle("Configure graph page");

    }

    return true;
  }

  @Override
  public boolean onOptionsItemSelected (MenuItem item) {
    super.onOptionsItemSelected(item);
    Intent intent = null;
    //which item is selected?
    switch (item.getItemId()) {

    case R.id.configureGraphPageMenuItem:

      intent = new Intent(GraphActivity.this, ConfigureDataTablesActivity.class);
      startActivityForResult(intent, CONFIGURE_DATA_TABLE_ACTIVITY_REQUEST_CODE);
      break;
    case R.id.editValuesMenuItem:
      intent = new Intent(GraphActivity.this, DataPagesActivity.class);
      startActivity(intent); 
      break;

    case R.id.saveCurrentValuesMenuItem:
      dataController.saveValues();
      Toast toast = Toast.makeText(GraphActivity.this, "Data saved", Toast.LENGTH_SHORT);
      toast.show();
      break;

    case R.id.databaseMenuItem:
      intent = new Intent(GraphActivity.this, SavedDataBrowserActivity.class);
      startActivity(intent); 
      break;
    default:
      //select nothing / do nothing
      return true;
    }

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
    //necessary in case the user switches between loan types (15 vs. 30 year)
    getNumOfCompoundingPeriods();

    currentSliderKey = spinnerArrayAdapter.getItem(0);
    currentValueNumeric = dataController.getValueAsFloat(currentSliderKey);
    setMinAndMaxFromCurrent();
    valueSlider.setProgress(valueSlider.getMax() / 2);
    timeSlider.setProgress(timeSlider.getMax());

    //get all the values for the current number and number of divisions
    //take those numbers and crunch them in the main equation, once for each division
    //then store them in a map of division numbers to crunched values
    for (int i = 0; i <= DIVISIONS_OF_VALUE_SLIDER; i++) {
      dataController.setCurrentDivision(i);
      percentageSlid = (i / (float) DIVISIONS_OF_VALUE_SLIDER);
      newCurrentValue = minValueNumeric + (percentageSlid * deltaValueNumeric);
      dataController.setValueAsFloat(currentSliderKey, newCurrentValue);
      CalculatedVariables.crunchCalculation();
    }
    invalidateGraphs();

    setDataTableItems(dataTableItems, currentYearSelected);
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.graph);

    viewableDataTableRows = new HashSet<ValueEnum>();
    dataController.setViewableDataTableRows(viewableDataTableRows);
    dataTableLayout = (TableLayout) findViewById(R.id.dataTableLayout);        
    valueToDataTableItemCorrespondence = new HashMap<ValueEnum, TableRow> ();


    getNumOfCompoundingPeriods();
    setupValueSpinner();
    setupTimeSlider();
    setupValueSlider();
    setupGraphs();
    setupCurrentValueFields();
    createDataTableItems();

  }

  private void invalidateGraphs() {
    atcfGraph.invalidate();
    npvGraph.invalidate();
  }

  private void highlightCurrentYearOnGraph(Integer currentYearHighlight) {
    atcfGraph.setCurrentYearHighlighted(currentYearHighlight);
    npvGraph.setCurrentYearHighlighted(currentYearHighlight);
  }

  private void setupGraphs() {

    atcfGraph = (com.byronkatz.AnalysisGraph) findViewById(R.id.atcfFrameLayout);
    npvGraph = (com.byronkatz.AnalysisGraph) findViewById(R.id.npvFrameLayout);

    invalidateGraphs();
    highlightCurrentYearOnGraph(currentYearSelected);

  }

  private void getNumOfCompoundingPeriods() {
    Float tempFloatValue = dataController.
        getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS) / CalculatedVariables.NUM_OF_MONTHS_IN_YEAR;
    currentYearMaximum = tempFloatValue.intValue();
    currentYearSelected = currentYearMaximum.intValue();
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
        ValueType test = currentSliderKey.getType(); 
        String value = null;

        if (hasFocus) {
          DataController.setSelectionOnView(v, test);
        } else if (! hasFocus) {
          switch (test) {
          case CURRENCY:
            value = currentValueEditText.getText().toString();
            currentValueNumeric = Utility.parseCurrency(value);
            currentValueEditText.setText(Utility.displayCurrency(currentValueNumeric));
            break;
          case PERCENTAGE:
            value = currentValueEditText.getText().toString();
            currentValueNumeric = Utility.parsePercentage(value);
            currentValueEditText.setText(Utility.displayPercentage(currentValueNumeric));
            break;
          case INTEGER:
          default:
            //do nothing
            break;
          }
          setMinAndMaxFromCurrent();
          valueSlider.setProgress(valueSlider.getMax() / 2);
          //get all the values for the current number and number of divisions
          //take those numbers and crunch them in the main equation, once for each division
          //then store them in a map of division numbers to crunched values
          for (int i = 0; i <= DIVISIONS_OF_VALUE_SLIDER; i++) {
            dataController.setCurrentDivision(i);
            percentageSlid = (i / (float) DIVISIONS_OF_VALUE_SLIDER);
            newCurrentValue = minValueNumeric + (percentageSlid * deltaValueNumeric);
            dataController.setValueAsFloat(currentSliderKey, newCurrentValue);
            CalculatedVariables.crunchCalculation();
          }
          invalidateGraphs();

          setDataTableItems(dataTableItems, currentYearSelected);
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
        ValueType test = currentSliderKey.getType(); 
        String value = null;

        if (hasFocus) {
          switch (test) {
          case CURRENCY:
            DataController.setSelectionOnView(v, ValueType.CURRENCY);
            break;
          case PERCENTAGE:
            DataController.setSelectionOnView(v, ValueType.PERCENTAGE);
            break;
          default:
            System.err.println("Should not get here in currentValueEditText.setOnFocusChangeListener");
            break;
          }

        } else {
          switch (test) {
          case CURRENCY:
            value = minValueEditText.getText().toString();
            minValueNumeric = Utility.parseCurrency(value);
            minValueEditText.setText(Utility.displayCurrency(minValueNumeric));
            break;
          case PERCENTAGE:
            value = minValueEditText.getText().toString();
            minValueNumeric = Utility.parsePercentage(value);
            minValueEditText.setText(Utility.displayPercentage(minValueNumeric));
            break;
          default:
            System.err.println("Should not get here in minValueEditText.setOnFocusChangeListener");
            break;
          }
        }
        deltaValueNumeric = maxValueNumeric - minValueNumeric;
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
        ValueType test = currentSliderKey.getType(); 
        String value = null;

        if (hasFocus) {
          DataController.setSelectionOnView(v, test);
        } else if (! hasFocus) {
          switch (test) {
          case CURRENCY:
            value = maxValueEditText.getText().toString();
            maxValueNumeric = Utility.parseCurrency(value);
            maxValueEditText.setText(Utility.displayCurrency(maxValueNumeric));
            break;
          case PERCENTAGE:
            value = maxValueEditText.getText().toString();
            maxValueNumeric = Utility.parsePercentage(value);
            maxValueEditText.setText(Utility.displayPercentage(maxValueNumeric));
            break;
          default:
            System.err.println("Should not get here in maxValueEditText.setOnFocusChangeListener");
            break;
          }
        }
        deltaValueNumeric = maxValueNumeric - minValueNumeric;
      }
    });

  }

  private void setupValueSlider() {


    valueSlider = (SeekBar) findViewById(R.id.valueSlider);
    valueSlider.setMax(DIVISIONS_OF_VALUE_SLIDER);
    valueSlider.setProgress(valueSlider.getMax() / 2);

    yearDisplayAtSeekBar = (TextView) findViewById(R.id.yearLabel);
    yearDisplayAtSeekBar.setText("Year:\n" + String.valueOf(currentYearMaximum));


    valueSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

      Float newCurrentValue;
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

        //WORK AREA

        //set the current division by the progress
        dataController.setCurrentDivision(progress);

        //set the value in the current value field:
        percentageSlid = (progress / (float) DIVISIONS_OF_VALUE_SLIDER);
        newCurrentValue = minValueNumeric + (percentageSlid * deltaValueNumeric);

        switch (currentSliderKey.getType()) {
        case CURRENCY:
          currentValueEditText.setText(Utility.displayCurrency(newCurrentValue));
          break;
        case PERCENTAGE:
          currentValueEditText.setText(Utility.displayPercentage(newCurrentValue));
          break;
        case INTEGER:
          currentValueEditText.setText(String.valueOf(newCurrentValue));
          break;
        default:
          System.err.println("Should not get here in valueSlider.setOnSeekBarChangeListener");
          break;
        }

        //set the values for use by the graphs and data tables
        invalidateGraphs();
        setDataTableItems(dataTableItems, currentYearSelected);





        //WORK AREA ENDS

        //OLD STUFF
        //        percentageSlid = (progress / (float) DIVISIONS_OF_VALUE_SLIDER);
        //        newCurrentValue = minValueNumeric + (percentageSlid * deltaValueNumeric);
        //
        //        ValueType test = currentSliderKey.getType(); 
        //
        //        switch (test) {
        //        case CURRENCY:
        //          currentValueEditText.setText(Utility.displayCurrency(newCurrentValue));
        //          break;
        //        case PERCENTAGE:
        //          currentValueEditText.setText(Utility.displayPercentage(newCurrentValue));
        //          break;
        //        default:
        //          System.err.println("Should not get here in valueSlider.setOnSeekBarChangeListener");
        //          break;
        //        }
        //        dataController.setValueAsFloat(currentSliderKey, newCurrentValue);
        //
        //        CalculatedVariables.crunchCalculation();
        //
        //        invalidateGraphs();
        //        setDataTableItems(dataTableItems, currentYearSelected);
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
          highlightCurrentYearOnGraph(currentYearSelected);
          invalidateGraphs();
        }
      }
    });
  }

  private void setupValueSpinner() {
    valueSpinner = (Spinner) findViewById(R.id.valueSpinner);

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


    valueSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
          long arg3) {
        currentSliderKey = spinnerArrayAdapter.getItem(pos);
        currentValueNumeric = dataController.getValueAsFloat(currentSliderKey);

        setMinAndMaxFromCurrent();


        //get all the values for the current number and number of divisions
        //take those numbers and crunch them in the main equation, once for each division
        //then store them in a map of division numbers to crunched values
        for (int i = 0; i <= DIVISIONS_OF_VALUE_SLIDER; i++) {
          dataController.setCurrentDivision(i);
          percentageSlid = (i / (float) DIVISIONS_OF_VALUE_SLIDER);
          newCurrentValue = minValueNumeric + (percentageSlid * deltaValueNumeric);
          dataController.setValueAsFloat(currentSliderKey, newCurrentValue);
          CalculatedVariables.crunchCalculation();
        }

      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
        // do nothing with this. This method is necessary to satisfy interface.

      }
    });

  }

  private void setMinAndMaxFromCurrent() {
    minValueNumeric = currentValueNumeric / 2;
    maxValueNumeric = currentValueNumeric + (currentValueNumeric - minValueNumeric);
    deltaValueNumeric = maxValueNumeric - minValueNumeric;
    ValueType test = currentSliderKey.getType(); 

    switch (test) {
    case CURRENCY:

      maxValueEditText.setText(Utility.displayCurrency(maxValueNumeric));
      minValueEditText.setText(Utility.displayCurrency(minValueNumeric));
      currentValueEditText.setText(Utility.displayCurrency(currentValueNumeric));
      break;
    case PERCENTAGE:

      maxValueEditText.setText(Utility.displayPercentage(maxValueNumeric));
      minValueEditText.setText(Utility.displayPercentage(minValueNumeric));
      currentValueEditText.setText(Utility.displayPercentage(currentValueNumeric));
      break;
    default:
      System.err.println("Should not get here in maxValueEditText.setOnFocusChangeListener");
      break;
    }

  }

  private void createDataTableItems() {



    TextView dataTablePropertyName;

    LayoutInflater inflater = (LayoutInflater)GraphActivity.this.getSystemService
        (Context.LAYOUT_INFLATER_SERVICE);
    ValueEnum[] dataTableValues = ValueEnum.values();


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

      dataTablePropertyName = (TextView) newTableRow.getChildAt(PROPERTY_LABEL_INDEX);

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


  //  public void showWelcomeScreen() {
  //    Dialog dialog = new Dialog(GraphActivity.this);
  //    Window window = dialog.getWindow();
  //    window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, 
  //        WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
  //    window.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, 
  //        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
  //    dialog.setContentView(R.layout.welcome_dialog_view);
  //    
  //    TextView textView = (TextView)dialog.findViewById(R.id.welcome_text);
  //    textView.setText("HELLO");
  //    dialog.setTitle("WELCOME");
  //    dialog.show();
  //  }
}
