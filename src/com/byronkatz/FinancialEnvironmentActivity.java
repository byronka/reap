package com.byronkatz;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;

public class FinancialEnvironmentActivity extends Activity {

  private EditText inflationRate;
  private EditText realEstateAppreciationRate;
  private Button backButton;
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
    backButton                  = (Button)findViewById(R.id.backButton);
    
    assignValuesToFields();
    
    inflationRate.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        ValueEnum key = ValueEnum.INFLATION_RATE;
        Float value = Float.valueOf(inflationRate.getText().toString());
        
        dataController.setValueAsFloat(key, value);
        return false;
      }
    });
    
    realEstateAppreciationRate.setOnKeyListener(new OnKeyListener() {
      
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        ValueEnum key = ValueEnum.REAL_ESTATE_APPRECIATION_RATE;
        Float value = Float.valueOf(realEstateAppreciationRate.getText().toString());
        
        dataController.setValueAsFloat(key, value);
        return false;
      }
    });
    
    backButton.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
       finish();
      }
    });
  }
  
  private void assignValuesToFields() {

    String ir = dataController.getValueAsString(ValueEnum.INFLATION_RATE);
    inflationRate.setText(ir);
    
    String rear = dataController.getValueAsString(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);
    realEstateAppreciationRate.setText(rear);
    
  }
}

