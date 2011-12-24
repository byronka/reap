package com.byronkatz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class GraphActivity extends Activity {

  private Enum<ValueEnum> currentSliderKey;
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
  
  private static final Enum<ValueEnum> NOCP = ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS;

  ValueEnum[] valuesNotToUse = {
      ValueEnum.STREET_ADDRESS,
      ValueEnum.CITY,
      ValueEnum.STATE_INITIALS,
      ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS,
  };

  @Override
  public void onResume() {
    super.onResume();
    //necessary in case the user switches between loan types (15 vs. 30 year)
    Float tempFloatValue = dataController.
        getValueAsFloat(NOCP) / CalculatedVariables.NUM_OF_MONTHS_IN_YEAR;
    currentYearMaximum = tempFloatValue.intValue();
    currentYearSelected = currentYearMaximum.intValue();
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

    
    valueSpinner = (Spinner) findViewById(R.id.valueSpinner);

//    contentValues = dataController.getContentValues();
    ArrayList<ValueEnum> spinnerValuesArray = new ArrayList<ValueEnum>(Arrays.asList(ValueEnum.values()));

    for (ValueEnum e : valuesNotToUse) {
      spinnerValuesArray.remove(e);
    }
    
    Collections.sort(spinnerValuesArray);

    spinnerArrayAdapter = new ArrayAdapter<ValueEnum>(this,
        android.R.layout.simple_spinner_dropdown_item, spinnerValuesArray);
    valueSpinner.setAdapter(spinnerArrayAdapter);

    currentValueEditText = (EditText) findViewById(R.id.currentValueEditText);

    valueSlider = (SeekBar) findViewById(R.id.valueSlider);
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
    

    minValueEditText.setOnKeyListener(new OnKeyListener() {
      
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        minValueNumeric = Float.valueOf(minValueEditText.getText().toString());
        return false;
      }
    });
    
    maxValueEditText.setOnKeyListener(new OnKeyListener() {
      
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        maxValueNumeric = Float.valueOf(maxValueEditText.getText().toString());
        return false;
      }
    });
    
    valueSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
          long arg3) {
        currentSliderKey = spinnerArrayAdapter.getItem(pos);
        String currentValueString = dataController.getValueAsString(currentSliderKey);
        currentValueEditText.setText(currentValueString);
        Float currentValueNumeric = Float.valueOf(currentValueString);
        Float halfCurrentValue = currentValueNumeric / 2;
        Float twiceCurrentValue = currentValueNumeric * 2;
        minValueEditText.setText(String.valueOf(halfCurrentValue));
        maxValueEditText.setText(String.valueOf(twiceCurrentValue));
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
        assignValuesToDataTable();
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

        Float deltaValueNumeric = maxValueNumeric - minValueNumeric;
        Float percentageSlid = progress / 100.0f;
        Float newCurrentValue = minValueNumeric
            + (percentageSlid * deltaValueNumeric);
        String newCurrentValueString = CalculatedVariables.displayCurrency(
            newCurrentValue);
        currentValueEditText.setText(newCurrentValueString);
        
        dataController.setValueAsFloat(currentSliderKey, newCurrentValue);

        AnalysisGraph.calculatedVariables.crunchCalculation();

        aterGraph.createDataPoints();
        atcfGraph.createDataPoints();
        npvGraph.createDataPoints();

        aterGraph.invalidate();
        atcfGraph.invalidate();
        npvGraph.invalidate();
        assignValuesToDataTable();
      }
    });

  }


  private void assignValuesToDataTable() {
    
    //get connections to xml elements
    TextView yearGridTextView = (TextView) findViewById(R.id.yearGridTextView);
    TextView atcfGridTextView = (TextView) findViewById(R.id.atcfGridTextView);
    TextView aterGridTextView = (TextView) findViewById(R.id.aterGridTextView);
    TextView npvGridTextView = (TextView) findViewById(R.id.npvGridTextView);
    
    String currentYearSelectedString = String.valueOf(currentYearSelected);

    Float currentYearAter = dataController.getValueAsFloat(ValueEnum.ATER);
    String currentYearAterString = CalculatedVariables.displayCurrency(currentYearAter);

    Float currentYearAtcf = dataController.getValueAsFloat(ValueEnum.ATCF);
    String currentYearAtcfString = CalculatedVariables.displayCurrency(currentYearAtcf);

    Float currentYearNpv = dataController.getValueAsFloat(ValueEnum.NPV);
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
    tempStringValue = String.format("{0,number,#.##%}", dataController.getValueAsFloat(ValueEnum.YEARLY_INTEREST_RATE));
    tempTextView.setText(tempStringValue);
    
    tempTextView = (TextView) findViewById(R.id.bvGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.BUILDING_VALUE));
    tempTextView.setText(tempStringValue);
    
    tempTextView = (TextView) findViewById(R.id.nocpGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS));
    tempTextView.setText(tempStringValue);
   
    tempTextView = (TextView) findViewById(R.id.irGridTextView);
    tempStringValue = String.format("{0,number,#.##%}",dataController.getValueAsFloat(ValueEnum.INFLATION_RATE));
    tempTextView.setText(tempStringValue);
   
    tempTextView = (TextView) findViewById(R.id.pmirGridTextView);
    tempStringValue = String.format("{0,number,#.##%}",dataController.getValueAsFloat(ValueEnum.PRIMARY_MORTGAGE_INSURANCE_RATE));
    tempTextView.setText(tempStringValue);
    
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
    tempStringValue = String.format("{0,number,#.##%}",dataController.getValueAsFloat(ValueEnum.REAL_ESTATE_APPRECIATION_RATE));
    tempTextView.setText(tempStringValue);
    
    tempTextView = (TextView) findViewById(R.id.yhiGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.YEARLY_HOME_INSURANCE));
    tempTextView.setText(tempStringValue);
    
    tempTextView = (TextView) findViewById(R.id.ptrGridTextView);
    tempStringValue = String.format("{0,number,#.##%}",dataController.getValueAsFloat(ValueEnum.PROPERTY_TAX_RATE));
    tempTextView.setText(tempStringValue);
    
    tempTextView = (TextView) findViewById(R.id.lmfGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.LOCAL_MUNICIPAL_FEES));
    tempTextView.setText(tempStringValue);
    
    tempTextView = (TextView) findViewById(R.id.vaclrGridTextView);
    tempStringValue = String.format("{0,number,#.##%}",dataController.getValueAsFloat(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE));
    tempTextView.setText(tempStringValue);
    
    tempTextView = (TextView) findViewById(R.id.iygeGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES));
    tempTextView.setText(tempStringValue);
    
    tempTextView = (TextView) findViewById(R.id.mtrGridTextView);
    tempStringValue = String.format("{0,number,#.##%}",dataController.getValueAsFloat(ValueEnum.MARGINAL_TAX_RATE));
    tempTextView.setText(tempStringValue);
    
    tempTextView = (TextView) findViewById(R.id.sbrGridTextView);
    tempStringValue = String.format("{0,number,#.##%}",dataController.getValueAsFloat(ValueEnum.SELLING_BROKER_RATE));
    tempTextView.setText(tempStringValue);
    
    tempTextView = (TextView) findViewById(R.id.gseGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.GENERAL_SALE_EXPENSES));
    tempTextView.setText(tempStringValue);
    
    tempTextView = (TextView) findViewById(R.id.rrrGridTextView);
    tempStringValue = String.format("{0,number,#.##%}",dataController.getValueAsFloat(ValueEnum.REQUIRED_RATE_OF_RETURN));
    tempTextView.setText(tempStringValue);
    
    tempTextView = (TextView) findViewById(R.id.fucGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.FIX_UP_COSTS));
    tempTextView.setText(tempStringValue);
    
    tempTextView = (TextView) findViewById(R.id.ccGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.CLOSING_COSTS));
    tempTextView.setText(tempStringValue);
  }

}
