package com.byronkatz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.ContentValues;
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

  private String currentSliderKey;
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
  private ContentValues contentValues;
  private ArrayAdapter<String> spinnerArrayAdapter;
  private Float currentYearMaximum;
  private Float currentYearSelected;

  // calculated variables
  private List<Map<String, Float>> calculatedValuesList;

  private Double minValueNumeric;
  private Double maxValueNumeric;
  
  private static final String NOCP = DatabaseAdapter.NUMBER_OF_COMPOUNDING_PERIODS;

  String[] contentValuesNotToUse = {
      DatabaseAdapter.STREET_ADDRESS,
      DatabaseAdapter.CITY,
      DatabaseAdapter.STATE_INITIALS,
      DatabaseAdapter.NUMBER_OF_COMPOUNDING_PERIODS,
      DatabaseAdapter.KEY_ID
  };

  @Override
  public void onResume() {
    super.onResume();
    //necessary in case the user switches between loan types (15 vs. 30 year)
    currentYearMaximum = dataController.getValueAsFloat(
        DatabaseAdapter.NUMBER_OF_COMPOUNDING_PERIODS);
    currentYearSelected = currentYearMaximum;
  }
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.graph);

    calculatedValuesList = dataController.getCalculatedValuesList();

    currentYearMaximum = Integer.
        valueOf(dataController.getValue(NOCP)) / CalculatedVariables.NUM_OF_MONTHS_IN_YEAR;
    currentYearSelected = currentYearMaximum;

    
    valueSpinner = (Spinner) findViewById(R.id.valueSpinner);

    contentValues = dataController.getContentValues();
    ArrayList<String> spinnerValuesArray = new ArrayList<String>();

    Set<Entry<String, Object>> contentValuesSet = contentValues.valueSet();
    for (Entry<String, Object> entry : contentValuesSet) {
      spinnerValuesArray.add((String) entry.getKey());
    }

    for (String s : contentValuesNotToUse) {
      spinnerValuesArray.remove(s);
    }
    
    Collections.sort(spinnerValuesArray);

    spinnerArrayAdapter = new ArrayAdapter<String>(this,
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
        minValueNumeric = Double.valueOf(minValueEditText.getText().toString());
        return false;
      }
    });
    
    maxValueEditText.setOnKeyListener(new OnKeyListener() {
      
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        maxValueNumeric = Double.valueOf(maxValueEditText.getText().toString());
        return false;
      }
    });
    
    valueSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
          long arg3) {
        currentSliderKey = (String) valueSpinner.getItemAtPosition(pos);
        String currentValueString = dataController.getValue(currentSliderKey);
        currentValueEditText.setText(currentValueString);
        Double currentValueNumeric = Double.valueOf(currentValueString);
        Double halfCurrentValue = currentValueNumeric / 2;
        Double twiceCurrentValue = currentValueNumeric * 2;
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

        Double deltaValueNumeric = maxValueNumeric - minValueNumeric;
        Double percentageSlid = progress / 100.0;
        Double newCurrentValue = minValueNumeric
            + (percentageSlid * deltaValueNumeric);
        String newCurrentValueString = CalculatedVariables.displayCurrency(
            newCurrentValue);
        currentValueEditText.setText(newCurrentValueString);
        
        String currentValueStorageString = String.valueOf(newCurrentValue);
        dataController.setValue(currentSliderKey, currentValueStorageString);

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
    
    HashMap<String, Float> dataValues = calculatedValuesList.get(currentYearSelected);
    String currentYearSelectedString = String.valueOf(currentYearSelected);

    Float currentYearAter = dataValues.get(AnalysisGraph.GraphType.ATER.getGraphName());
    String currentYearAterString = CalculatedVariables.displayCurrency(currentYearAter);

    Float currentYearAtcf = dataValues.get(AnalysisGraph.GraphType.ATCF.getGraphName());
    String currentYearAtcfString = CalculatedVariables.displayCurrency(currentYearAtcf);

    Float currentYearNpv = dataValues.get(AnalysisGraph.GraphType.NPV.getGraphName());
    String currentYearNpvString = CalculatedVariables.displayCurrency(currentYearNpv);

    TextView yearGridTextView = (TextView) findViewById(R.id.yearGridTextView);
    TextView atcfGridTextView = (TextView) findViewById(R.id.atcfGridTextView);
    TextView aterGridTextView = (TextView) findViewById(R.id.aterGridTextView);
    TextView npvGridTextView = (TextView) findViewById(R.id.npvGridTextView);

    yearGridTextView.setText(currentYearSelectedString);
    atcfGridTextView.setText(currentYearAtcfString);
    aterGridTextView.setText(currentYearAterString);
    npvGridTextView.setText(currentYearNpvString);
    
    TextView tpvGridTextView = (TextView) findViewById(R.id.tpvGridTextView);
    tpvGridTextView.setText(dataController.getValue(DatabaseAdapter.TOTAL_PURCHASE_VALUE));  
    TextView yirGridTextView = (TextView) findViewById(R.id.yirGridTextView);
    yirGridTextView.setText(dataController.getValue(DatabaseAdapter.YEARLY_INTEREST_RATE));
    TextView bvGridTextView = (TextView) findViewById(R.id.bvGridTextView);
    bvGridTextView.setText(dataController.getValue(DatabaseAdapter.BUILDING_VALUE));
    TextView nocpGridTextView = (TextView) findViewById(R.id.nocpGridTextView);
    nocpGridTextView.setText(dataController.getValue(DatabaseAdapter.NUMBER_OF_COMPOUNDING_PERIODS));
    TextView irGridTextView = (TextView) findViewById(R.id.irGridTextView);
    irGridTextView.setText(dataController.getValue(DatabaseAdapter.INFLATION_RATE));
    TextView pmirGridTextView = (TextView) findViewById(R.id.pmirGridTextView);
    pmirGridTextView.setText(dataController.getValue(DatabaseAdapter.PRIMARY_MORTGAGE_INSURANCE_RATE));
    TextView dpGridTextView = (TextView) findViewById(R.id.dpGridTextView);
    dpGridTextView.setText(dataController.getValue(DatabaseAdapter.DOWN_PAYMENT));
    TextView saGridTextView = (TextView) findViewById(R.id.saGridTextView);
    saGridTextView.setText(dataController.getValue(DatabaseAdapter.STREET_ADDRESS));
    TextView cityGridTextView = (TextView) findViewById(R.id.cityGridTextView);
    cityGridTextView.setText(dataController.getValue(DatabaseAdapter.CITY));
    TextView siGridTextView = (TextView) findViewById(R.id.siGridTextView);
    siGridTextView.setText(dataController.getValue(DatabaseAdapter.STATE_INITIALS));
    TextView erpGridTextView = (TextView) findViewById(R.id.erpGridTextView);
    erpGridTextView.setText(dataController.getValue(DatabaseAdapter.ESTIMATED_RENT_PAYMENTS));
    TextView rearGridTextView = (TextView) findViewById(R.id.rearGridTextView);
    rearGridTextView.setText(dataController.getValue(DatabaseAdapter.REAL_ESTATE_APPRECIATION_RATE));
    TextView yhiGridTextView = (TextView) findViewById(R.id.yhiGridTextView);
    yhiGridTextView.setText(dataController.getValue(DatabaseAdapter.YEARLY_HOME_INSURANCE));
    TextView ptrGridTextView = (TextView) findViewById(R.id.ptrGridTextView);
    ptrGridTextView.setText(dataController.getValue(DatabaseAdapter.PROPERTY_TAX_RATE));
    TextView lmfGridTextView = (TextView) findViewById(R.id.lmfGridTextView);
    lmfGridTextView.setText(dataController.getValue(DatabaseAdapter.LOCAL_MUNICIPAL_FEES));
    TextView vaclrGridTextView = (TextView) findViewById(R.id.vaclrGridTextView);
    vaclrGridTextView.setText(dataController.getValue(DatabaseAdapter.VACANCY_AND_CREDIT_LOSS_RATE));
    TextView iygeGridTextView = (TextView) findViewById(R.id.iygeGridTextView);
    iygeGridTextView.setText(dataController.getValue(DatabaseAdapter.INITIAL_YEARLY_GENERAL_EXPENSES));
    TextView mtrGridTextView = (TextView) findViewById(R.id.mtrGridTextView);
    mtrGridTextView.setText(dataController.getValue(DatabaseAdapter.MARGINAL_TAX_RATE));
    TextView sbrGridTextView = (TextView) findViewById(R.id.sbrGridTextView);
    sbrGridTextView.setText(dataController.getValue(DatabaseAdapter.SELLING_BROKER_RATE));
    TextView gseGridTextView = (TextView) findViewById(R.id.gseGridTextView);
    gseGridTextView.setText(dataController.getValue(DatabaseAdapter.GENERAL_SALE_EXPENSES));
    TextView rrrGridTextView = (TextView) findViewById(R.id.rrrGridTextView);
    rrrGridTextView.setText(dataController.getValue(DatabaseAdapter.REQUIRED_RATE_OF_RETURN));
    TextView fucGridTextView = (TextView) findViewById(R.id.fucGridTextView);
    fucGridTextView.setText(dataController.getValue(DatabaseAdapter.FIX_UP_COSTS));
    TextView ccGridTextView = (TextView) findViewById(R.id.ccGridTextView);
    ccGridTextView.setText(dataController.getValue(DatabaseAdapter.CLOSING_COSTS));
  }

}
