package com.byronkatz;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.byronkatz.ValueEnum.ValueType;

public class GraphActivity extends Activity {

  private ValueEnum currentSliderKey;
  private AnalysisGraph npvGraph;
  private AnalysisGraph aterGraph;
  private AnalysisGraph atcfGraph;
  private Spinner valueSpinner;
  private EditText currentValueEditText;
  private SeekBar valueSlider;
  private SeekBar timeSlider;
  private EditText minValueEditText;
  private EditText maxValueEditText;
  private final DataController dataController = RealEstateMarketAnalysisApplication
      .getInstance().getDataController();
  private ArrayAdapter<ValueEnum> spinnerArrayAdapter;
  private Integer currentYearMaximum;
  private Integer currentYearSelected;

  //  // calculated variables
  //  private List<Map<String, Float>> calculatedValuesList;

  private Float minValueNumeric;
  private Float maxValueNumeric;
  private Float deltaValueNumeric;
  private Float currentValueNumeric;

  private static final ValueEnum NOCP = ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS;
  private static final int DIVISIONS_OF_VALUE_SLIDER = 20;

  @Override
  public void onResume() {
    super.onResume();
    //necessary in case the user switches between loan types (15 vs. 30 year)
    Float tempFloatValue = dataController.
        getValueAsFloat(NOCP) / CalculatedVariables.NUM_OF_MONTHS_IN_YEAR;
    currentYearMaximum = tempFloatValue.intValue();
    currentYearSelected = currentYearMaximum.intValue();
    assignValuesToDataTable(currentYearSelected);
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.graph);

    Float tempFloatValue = dataController.
        getValueAsFloat(NOCP) / CalculatedVariables.NUM_OF_MONTHS_IN_YEAR;
    currentYearMaximum = tempFloatValue.intValue();
    currentYearSelected = currentYearMaximum.intValue();
    assignValuesToDataTable(currentYearSelected);


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

    currentValueEditText = (EditText) findViewById(R.id.currentValueEditText);

    valueSlider = (SeekBar) findViewById(R.id.valueSlider);
    valueSlider.setMax(DIVISIONS_OF_VALUE_SLIDER);
    valueSlider.setProgress(valueSlider.getMax() / 2);
    
    
    timeSlider = (SeekBar) findViewById(R.id.timeSlider);
    timeSlider.setMax(currentYearMaximum);
    timeSlider.setProgress(timeSlider.getMax());

    minValueEditText = (EditText) findViewById(R.id.minValueEditText);
    maxValueEditText = (EditText) findViewById(R.id.maxValueEditText);

    aterGraph = (com.byronkatz.AnalysisGraph) findViewById(R.id.aterFrameLayout);
    atcfGraph = (com.byronkatz.AnalysisGraph) findViewById(R.id.atcfFrameLayout);
    npvGraph = (com.byronkatz.AnalysisGraph) findViewById(R.id.npvFrameLayout);

    aterGraph.invalidate();
    atcfGraph.invalidate();
    npvGraph.invalidate();

    aterGraph.setCurrentYearHighlighted(currentYearSelected);
    atcfGraph.setCurrentYearHighlighted(currentYearSelected);
    npvGraph.setCurrentYearHighlighted(currentYearSelected);

    //currentValueEditText.setOnKeyListener(new OnKeyListener() {
    //
    //  @Override
    //  public boolean onKey(View v, int keyCode, KeyEvent event) {
    //    currentValueNumeric = Float.valueOf(currentValueEditText.getText().toString());
    //    return false;
    //  }
    //});

    currentValueEditText.setOnEditorActionListener(new OnEditorActionListener() {

      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        ValueType test = currentSliderKey.getType(); 
        String value = null;

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

        dataController.setValueAsFloat(currentSliderKey, currentValueNumeric);
        setMinAndMaxFromCurrent();
        valueSlider.setProgress(valueSlider.getMax() / 2);

        return false;
      }
    });

    currentValueEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        ValueType test = currentSliderKey.getType(); 
        String value = null;

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
      }
    });

    //    minValueEditText.setOnKeyListener(new OnKeyListener() {
    //
    //      @Override
    //      public boolean onKey(View v, int keyCode, KeyEvent event) {
    //        minValueNumeric = Float.valueOf(minValueEditText.getText().toString());
    //        return false;
    //      }
    //    });

    minValueEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        ValueType test = currentSliderKey.getType(); 
        String value = null;

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
        
        deltaValueNumeric = maxValueNumeric - minValueNumeric;
      }
    });

    //    maxValueEditText.setOnKeyListener(new OnKeyListener() {
    //
    //      @Override
    //      public boolean onKey(View v, int keyCode, KeyEvent event) {
    //        maxValueNumeric = Float.valueOf(maxValueEditText.getText().toString());
    //        return false;
    //      }
    //    });

    maxValueEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        ValueType test = currentSliderKey.getType(); 
        String value = null;

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
        
        deltaValueNumeric = maxValueNumeric - minValueNumeric;
      }
    });

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
          assignValuesToDataTable(currentYearSelected);
          aterGraph.setCurrentYearHighlighted(currentYearSelected);
          atcfGraph.setCurrentYearHighlighted(currentYearSelected);
          npvGraph.setCurrentYearHighlighted(currentYearSelected);
          aterGraph.invalidate();
          atcfGraph.invalidate();
          npvGraph.invalidate();
        }
      }
    });

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

        AnalysisGraph.calculatedVariables.crunchCalculation();

        aterGraph.createDataPoints();
        atcfGraph.createDataPoints();
        npvGraph.createDataPoints();

        aterGraph.invalidate();
        atcfGraph.invalidate();
        npvGraph.invalidate();
        assignValuesToDataTable(currentYearSelected);
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

  private void assignValuesToDataTable(Integer year) {

    //get connections to gui elements
    TextView yearGridTextView = (TextView) findViewById(R.id.yearGridTextView);
    TextView atcfGridTextView = (TextView) findViewById(R.id.atcfGridTextView);
    TextView aterGridTextView = (TextView) findViewById(R.id.aterGridTextView);
    TextView npvGridTextView = (TextView) findViewById(R.id.npvGridTextView);

    String currentYearSelectedString = String.valueOf(currentYearSelected);

    Float currentYearAter = dataController.getValueAsFloat(ValueEnum.ATER, year);
    String currentYearAterString = CalculatedVariables.displayCurrency(currentYearAter);

    Float currentYearAtcf = dataController.getValueAsFloat(ValueEnum.ATCF, year);
    String currentYearAtcfString = CalculatedVariables.displayCurrency(currentYearAtcf);

    Float currentYearNpv = dataController.getValueAsFloat(ValueEnum.NPV, year);
    String currentYearNpvString = CalculatedVariables.displayCurrency(currentYearNpv);

    //set text values
    yearGridTextView.setText(currentYearSelectedString);
    atcfGridTextView.setText(currentYearAtcfString);
    aterGridTextView.setText(currentYearAterString);
    npvGridTextView.setText(currentYearNpvString);

    String tempStringValue = "";
    TextView tempTextView = null;

    tempTextView = (TextView) findViewById(R.id.tpvGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.TOTAL_PURCHASE_VALUE));
    tempTextView.setText(tempStringValue);  

    tempTextView = (TextView) findViewById(R.id.yirGridTextView);
    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.YEARLY_INTEREST_RATE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.bvGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.BUILDING_VALUE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.nocpGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.irGridTextView);
    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.INFLATION_RATE));
    tempTextView.setText(tempStringValue);

    //    tempTextView = (TextView) findViewById(R.id.pmirGridTextView);
    //    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.PRIMARY_MORTGAGE_INSURANCE_RATE));
    //    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.dpGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.DOWN_PAYMENT));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.saGridTextView);
    tempStringValue = dataController.getValueAsString(ValueEnum.STREET_ADDRESS);
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.cityGridTextView);
    tempStringValue = dataController.getValueAsString(ValueEnum.CITY);
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.siGridTextView);
    tempStringValue = dataController.getValueAsString(ValueEnum.STATE_INITIALS);
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.erpGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.ESTIMATED_RENT_PAYMENTS));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.rearGridTextView);
    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.REAL_ESTATE_APPRECIATION_RATE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.yhiGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.YEARLY_HOME_INSURANCE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.ptrGridTextView);
    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.PROPERTY_TAX_RATE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.lmfGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.LOCAL_MUNICIPAL_FEES));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.vaclrGridTextView);
    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.iygeGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.mtrGridTextView);
    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.MARGINAL_TAX_RATE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.sbrGridTextView);
    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.SELLING_BROKER_RATE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.gseGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.GENERAL_SALE_EXPENSES));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.rrrGridTextView);
    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.REQUIRED_RATE_OF_RETURN));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.fucGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.FIX_UP_COSTS));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.ccGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.CLOSING_COSTS));
    tempTextView.setText(tempStringValue);
  }

}
