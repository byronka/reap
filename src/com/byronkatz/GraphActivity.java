package com.byronkatz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
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
  private Integer currentYearMaximum;
  private Integer currentYearSelected;

  // calculated variables
  private HashMap<Integer, HashMap<String, Float>> calculatedValuesHashMap;

  // grid variables at bottom
  private TextView yearGridTextView;
  private TextView atcfGridTextView;
  private TextView aterGridTextView;
  private TextView npvGridTextView;
  
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
    currentYearMaximum = Integer.
        valueOf(dataController.getValue(NOCP)) / CalculatedVariables.NUM_OF_MONTHS_IN_YEAR;
    currentYearSelected = currentYearMaximum;
  }
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.graph);

    calculatedValuesHashMap = dataController.getCalculatedValuesHashMap();

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
    
    yearGridTextView = (TextView) findViewById(R.id.yearGridTextView);
    atcfGridTextView = (TextView) findViewById(R.id.atcfGridTextView);
    aterGridTextView = (TextView) findViewById(R.id.aterGridTextView);
    npvGridTextView = (TextView) findViewById(R.id.npvGridTextView);

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
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        
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
        //take values from min and max edit fields
        String minValueString = minValueEditText.getText().toString();
        String maxValueString = maxValueEditText.getText().toString();
        Double minValueNumeric = Double.valueOf(minValueString);
        Double maxValueNumeric = Double.valueOf(maxValueString);

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
    
    HashMap<String, Float> dataValues = calculatedValuesHashMap.get(currentYearSelected);
    String currentYearSelectedString = String.valueOf(currentYearSelected);

    Float currentYearAter = dataValues.get(AnalysisGraph.GraphType.ATER.getGraphName());
    String currentYearAterString = CalculatedVariables.displayCurrency(currentYearAter);

    Float currentYearAtcf = dataValues.get(AnalysisGraph.GraphType.ATCF.getGraphName());
    String currentYearAtcfString = CalculatedVariables.displayCurrency(currentYearAtcf);

    Float currentYearNpv = dataValues.get(AnalysisGraph.GraphType.NPV.getGraphName());
    String currentYearNpvString = CalculatedVariables.displayCurrency(currentYearNpv);

    yearGridTextView.setText(currentYearSelectedString);
    atcfGridTextView.setText(currentYearAtcfString);
    aterGridTextView.setText(currentYearAterString);
    npvGridTextView.setText(currentYearNpvString);
  }

}
