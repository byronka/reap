package com.byronkatz;

import java.util.ArrayList;
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
  private EditText minValueEditText;
  private EditText maxValueEditText;
  private final DataController dataController = RealEstateMarketAnalysisApplication
      .getInstance().getDataController();
  private ContentValues contentValues;
  private ArrayAdapter<String> spinnerArrayAdapter;
  private Integer currentYearSelected;

  // calculated variables
  private HashMap<Integer, HashMap<String, Float>> calculatedValuesHashMap;

  // grid variables at bottom
  private TextView yearGridTextView;
  private TextView atcfGridTextView;
  private TextView aterGridTextView;
  private TextView npvGridTextView;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.graph);

    calculatedValuesHashMap = dataController.getCalculatedValuesHashMap();

    currentYearSelected = 29;

    valueSpinner = (Spinner) findViewById(R.id.valueSpinner);

    contentValues = dataController.getContentValues();
    ArrayList<String> spinnerValuesArray = new ArrayList<String>();
    Set<Entry<String, Object>> contentValuesSet = contentValues.valueSet();
    for (Entry<String, Object> entry : contentValuesSet) {
      spinnerValuesArray.add((String) entry.getKey());
    }
    spinnerArrayAdapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_spinner_dropdown_item, spinnerValuesArray);
    valueSpinner.setAdapter(spinnerArrayAdapter);
    currentValueEditText = (EditText) findViewById(R.id.currentValueEditText);
    valueSlider = (SeekBar) findViewById(R.id.valueSlider);
    minValueEditText = (EditText) findViewById(R.id.minValueEditText);
    maxValueEditText = (EditText) findViewById(R.id.maxValueEditText);
    aterGraph = (com.byronkatz.AnalysisGraph) findViewById(R.id.aterFrameLayout);
    atcfGraph = (com.byronkatz.AnalysisGraph) findViewById(R.id.atcfFrameLayout);
    npvGraph = (com.byronkatz.AnalysisGraph) findViewById(R.id.npvFrameLayout);

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
        String minValueString = minValueEditText.getText().toString();
        String maxValueString = maxValueEditText.getText().toString();
        Double minValueNumeric = Double.valueOf(minValueString);
        Double maxValueNumeric = Double.valueOf(maxValueString);
        
        Double deltaValueNumeric = maxValueNumeric - minValueNumeric;
        Double percentageSlid = progress / 100.0;
        Double newCurrentValue = minValueNumeric
            + (percentageSlid * deltaValueNumeric);
        String newCurrentValueString = CalculatedVariables.displayCurrency(newCurrentValue);
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
