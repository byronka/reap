package com.byronkatz;


import java.util.ArrayList;
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


public class GraphActivity extends Activity {

  private String currentSliderKey;
  private ATERGraph aterGraph;
  private NPVGraph npvGraph;
  private ATCFGraph atcfGraph;
  private Spinner valueSpinner;
  private EditText currentValueEditText;
  private SeekBar valueSlider;
  private EditText minValueEditText;
  private EditText maxValueEditText;
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  private ContentValues contentValues;
  private ArrayAdapter<String> spinnerArrayAdapter;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.graph);

    valueSpinner = (Spinner) findViewById(R.id.valueSpinner);
    contentValues = dataController.getContentValues();
    ArrayList<String> spinnerValuesArray = new ArrayList<String>();
    Set<Entry<String, Object>> contentValuesSet = contentValues.valueSet();
    for (Entry<String, Object> entry : contentValuesSet) {
      spinnerValuesArray.add((String)entry.getKey());
    }
    spinnerArrayAdapter = new ArrayAdapter<String>(this, 
        android.R.layout.simple_spinner_dropdown_item, spinnerValuesArray);
    valueSpinner.setAdapter(spinnerArrayAdapter);
    currentValueEditText =   (EditText) findViewById(R.id.currentValueEditText);
    valueSlider          =   (SeekBar) findViewById(R.id.valueSlider);
    minValueEditText     =   (EditText) findViewById(R.id.minValueEditText);
    maxValueEditText     =   (EditText) findViewById(R.id.maxValueEditText);
    aterGraph            =   (com.byronkatz.ATERGraph) findViewById(R.id.aterFrameLayout);
    atcfGraph            =   (com.byronkatz.ATCFGraph) findViewById(R.id.atcfFrameLayout);
    npvGraph            =   (com.byronkatz.NPVGraph) findViewById(R.id.npvFrameLayout);
    
    
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
        // do nothing with this.  This method is necessary to satisfy interface.

      }
    });
        
    valueSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
      
      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        String minValueString = minValueEditText.getText().toString();
        String maxValueString = maxValueEditText.getText().toString();
        Double minValueNumeric = Double.valueOf(minValueString);
        Double maxValueNumeric = Double.valueOf(maxValueString);
        Double deltaValueNumeric = maxValueNumeric - minValueNumeric;
        Integer progressSlid = valueSlider.getProgress();
        Double percentageSlid = progressSlid / 100.0;
        Double newCurrentValue = minValueNumeric + (percentageSlid * deltaValueNumeric);
        String newCurrentValueString = String.valueOf(newCurrentValue);
        currentValueEditText.setText(newCurrentValueString);
        dataController.setValue(currentSliderKey, newCurrentValueString);
        aterGraph.invalidate();
        atcfGraph.invalidate();
        npvGraph.invalidate();
      }
      
      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

        
      }
      
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

      }
    });
  }

}
