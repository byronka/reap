package com.byronkatz;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import android.widget.LinearLayout;
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
  Spinner valueSpinner;
  ArrayAdapter<ValueEnum> spinnerArrayAdapter;
  Integer currentYearMaximum;
  Integer currentYearSelected;
  Button resetButton;
  Float minValueNumeric;
  Float maxValueNumeric;
  Float deltaValueNumeric;
  Float currentValueNumeric;
  Map<ValueEnum, Integer> valueToDataTableItemCorrespondence;

  TableLayout dataTableLayout;
  LinearLayout dataTableRow;
  TextView dataTablePropertyName;
  TextView dataTablePropertyValue;
  static final int DIVISIONS_OF_VALUE_SLIDER = 100;
  ValueEnum[] dataTableItems = ValueEnum.values();


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
    Intent intent = null;
    //which item is selected?
    switch (item.getItemId()) {

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
  public void onResume() {
    super.onResume();
    //necessary in case the user switches between loan types (15 vs. 30 year)
    getNumOfCompoundingPeriods();

    currentSliderKey = spinnerArrayAdapter.getItem(0);
    currentValueNumeric = dataController.getValueAsFloat(currentSliderKey);
    setMinAndMaxFromCurrent();
    valueSlider.setProgress(valueSlider.getMax() / 2);
    timeSlider.setProgress(timeSlider.getMax());

    CalculatedVariables.crunchCalculation();
    createDataPointsOnGraphs();
    invalidateGraphs();

    setDataTableItems(dataTableItems, currentYearSelected);
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.graph);

    valueToDataTableItemCorrespondence = new HashMap<ValueEnum, Integer>();
    dataTableLayout = (TableLayout) findViewById(R.id.dataTableLayout);        


    getNumOfCompoundingPeriods();
    setupValueSpinner();
    setupTimeSlider();
    setupValueSlider();
    setupGraphs();
    setupCurrentValueFields();
    createDataTableItems();


    //    showWelcomeScreen();

  }

  private void createDataPointsOnGraphs() {
    //    aterGraph.createDataPoints();
    atcfGraph.createDataPoints();
    npvGraph.createDataPoints();
  }

  private void invalidateGraphs() {
    //    aterGraph.invalidate();
    atcfGraph.invalidate();
    npvGraph.invalidate();
  }

  private void highlightCurrentYearOnGraph(Integer currentYearHighlight) {
    //    aterGraph.setCurrentYearHighlighted(currentYearHighlight);
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
            value = currentValueEditText.getText().toString();
            currentValueNumeric = CalculatedVariables.parseCurrency(value);
            currentValueEditText.setText(CalculatedVariables.displayCurrency(currentValueNumeric));
            break;
          case PERCENTAGE:
            value = currentValueEditText.getText().toString();
            currentValueNumeric = CalculatedVariables.parsePercentage(value);
            currentValueEditText.setText(CalculatedVariables.displayPercentage(currentValueNumeric));
            break;
          default:
            System.err.println("Should not get here in currentValueEditText.setOnFocusChangeListener");
            break;
          }
          setMinAndMaxFromCurrent();
          valueSlider.setProgress(valueSlider.getMax() / 2);
          CalculatedVariables.crunchCalculation();

          createDataPointsOnGraphs();

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
            minValueNumeric = CalculatedVariables.parseCurrency(value);
            minValueEditText.setText(CalculatedVariables.displayCurrency(minValueNumeric));
            break;
          case PERCENTAGE:
            value = minValueEditText.getText().toString();
            minValueNumeric = CalculatedVariables.parsePercentage(value);
            minValueEditText.setText(CalculatedVariables.displayPercentage(minValueNumeric));
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
            value = maxValueEditText.getText().toString();
            maxValueNumeric = CalculatedVariables.parseCurrency(value);
            maxValueEditText.setText(CalculatedVariables.displayCurrency(maxValueNumeric));
            break;
          case PERCENTAGE:
            value = maxValueEditText.getText().toString();
            maxValueNumeric = CalculatedVariables.parsePercentage(value);
            maxValueEditText.setText(CalculatedVariables.displayPercentage(maxValueNumeric));
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

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress,
          boolean fromUser) {


        Float percentageSlid = (progress / (float) DIVISIONS_OF_VALUE_SLIDER);
        Float newCurrentValue = minValueNumeric + (percentageSlid * deltaValueNumeric);

        ValueType test = currentSliderKey.getType(); 

        switch (test) {
        case CURRENCY:
          currentValueEditText.setText(CalculatedVariables.displayCurrency(newCurrentValue));
          break;
        case PERCENTAGE:
          currentValueEditText.setText(CalculatedVariables.displayPercentage(newCurrentValue));
          break;
        default:
          System.err.println("Should not get here in valueSlider.setOnSeekBarChangeListener");
          break;
        }
        dataController.setValueAsFloat(currentSliderKey, newCurrentValue);

        CalculatedVariables.crunchCalculation();

        createDataPointsOnGraphs();

        invalidateGraphs();

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

      maxValueEditText.setText(CalculatedVariables.displayCurrency(maxValueNumeric));
      minValueEditText.setText(CalculatedVariables.displayCurrency(minValueNumeric));
      currentValueEditText.setText(CalculatedVariables.displayCurrency(currentValueNumeric));
      break;
    case PERCENTAGE:

      maxValueEditText.setText(CalculatedVariables.displayPercentage(maxValueNumeric));
      minValueEditText.setText(CalculatedVariables.displayPercentage(minValueNumeric));
      currentValueEditText.setText(CalculatedVariables.displayPercentage(currentValueNumeric));
      break;
    default:
      System.err.println("Should not get here in maxValueEditText.setOnFocusChangeListener");
      break;
    }

  }

  private void createDataTableItems() {

    LayoutInflater inflater = (LayoutInflater)GraphActivity.this.getSystemService
        (Context.LAYOUT_INFLATER_SERVICE);

    //This is where we create the TableLayout
    ValueEnum[] dataTableValues = ValueEnum.values();
    //set alternate colors by row
    boolean alternateColor = true;
    //main loop to create the data table rows

    int index = 0;
    for (ValueEnum ve : dataTableValues) {

      //set up the correspondence between the table index and the valueEnums
      valueToDataTableItemCorrespondence.put(ve, index);
      
      TableRow newTableRow = (TableRow) inflater.inflate(R.layout.data_table_tablerow, null);
      dataTablePropertyName = (TextView) newTableRow.getChildAt(0);
      dataTablePropertyValue = (TextView) newTableRow.getChildAt(1);

      if (alternateColor) {
        newTableRow.setBackgroundResource(R.color.data_table_row_color_alternate_a);
        alternateColor = ! alternateColor;
      } else {
        newTableRow.setBackgroundResource(R.color.data_table_row_color_alternate_b);
        alternateColor = ! alternateColor;
      }

      //the property name is always a string
      dataTablePropertyName.setText(ve.toString());

      /* set value based on what type of number it is, or string if 
       * applicable if it is saved to database, that 
       * means we only need the first year, or 
       * "getValueAsFloat(key)" rather than "getValueAsFloat(key, year)"
       */
 
      //set the map to find these later
      dataTableLayout.addView(newTableRow, index);
      index++;
    } //end of main for loop to set dataTableItems
  }

  private void setDataTableItems(ValueEnum[] items, Integer year) {
    ValueEnum ve;
    int index;
    TableRow tempTableRow;
    TextView tempDataTablePropertyValue;
    for (Entry<ValueEnum, Integer> entry : valueToDataTableItemCorrespondence.entrySet()) {

    ve = entry.getKey();
    index = entry.getValue();
    tempTableRow = (TableRow) dataTableLayout.getChildAt(index);
    tempDataTablePropertyValue = (TextView) tempTableRow.getChildAt(1);
    
    
    switch (ve.getType()) {
    case CURRENCY:

      if (ve.isSavedToDatabase()) {
        tempDataTablePropertyValue.setText(CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ve)));
      } else if (! ve.isSavedToDatabase()) {
        tempDataTablePropertyValue.setText(CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ve, year)));
      }
      break;


    case PERCENTAGE:


      if (ve.isSavedToDatabase()) {
        tempDataTablePropertyValue.setText(CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ve)));
      } else if (! ve.isSavedToDatabase()) {
        tempDataTablePropertyValue.setText(CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ve, year)));
      }
      break;


    case STRING:

      tempDataTablePropertyValue.setText(dataController.getValueAsString(ve));
      break;
    default:
      break;
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
