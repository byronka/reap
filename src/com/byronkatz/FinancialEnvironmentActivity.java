package com.byronkatz;

import com.byronkatz.ValueEnum.ValueType;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

public class FinancialEnvironmentActivity extends Activity {

  private EditText inflationRate;
  private EditText realEstateAppreciationRate;
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.financial_environment);

    //Hook up the components from the GUI to some variables here
    inflationRate               = (EditText)findViewById(R.id.inflationRateEditText);
    realEstateAppreciationRate  = (EditText)findViewById(R.id.realEstateAppreciationRateEditText);
    
    assignValuesToFields();
   
    inflationRate.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          DataController.setSelectionOnView(v, ValueType.PERCENTAGE);
        }     
      }
    });
    
    realEstateAppreciationRate.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          DataController.setSelectionOnView(v, ValueType.PERCENTAGE);
        }     
      }
    });
  }
  

  
  
  private void assignValuesToFields() {

    Float ir = dataController.getValueAsFloat(ValueEnum.INFLATION_RATE);
    inflationRate.setText(CalculatedVariables.displayPercentage(ir));
    
    Float rear = dataController.getValueAsFloat(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);
    realEstateAppreciationRate.setText(CalculatedVariables.displayPercentage(rear));
    
  }
  
  @Override
  public void onPause() {
    super.onPause();
    
    ValueEnum key = ValueEnum.INFLATION_RATE;
    Float value = CalculatedVariables.parsePercentage(inflationRate.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.REAL_ESTATE_APPRECIATION_RATE;
    value = CalculatedVariables.parsePercentage(realEstateAppreciationRate.getText().toString());   
    dataController.setValueAsFloat(key, value);

  }
}

