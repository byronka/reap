package com.byronkatz.reap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.OnFocusChangeListenerWrapper;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

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
   
    inflationRate.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.INFLATION_RATE));
    
    TextView inflationRateTitle = 
        (TextView)findViewById(R.id.inflationRateTitle);
    inflationRateTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.inflationRateDescriptionText, 
            R.string.inflationRateTitleText,
            FinancialEnvironmentActivity.this);
      }
    });
    
    realEstateAppreciationRate.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.REAL_ESTATE_APPRECIATION_RATE));
    
    TextView realEstateAppreciationRateTitle = 
        (TextView)findViewById(R.id.realEstateAppreciationRateTitle);
    realEstateAppreciationRateTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.realEstateAppreciationRateDescriptionText, 
            R.string.realEstateAppreciationRateTitleText,
            FinancialEnvironmentActivity.this);
      }
    });
    
  }
  
  public void callCalculator(View v) {
    Utility.callCalc(this);
  }
  
  
  private void assignValuesToFields() {

    Float ir = dataController.getValueAsFloat(ValueEnum.INFLATION_RATE);
    inflationRate.setText(Utility.displayPercentage(ir));
    
    Float rear = dataController.getValueAsFloat(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);
    realEstateAppreciationRate.setText(Utility.displayPercentage(rear));
    
  }
  
  @Override
  public void onPause() {
    super.onPause();
    
    ValueEnum key = ValueEnum.INFLATION_RATE;
    Float value = Utility.parsePercentage(inflationRate.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.REAL_ESTATE_APPRECIATION_RATE;
    value = Utility.parsePercentage(realEstateAppreciationRate.getText().toString());   
    dataController.setValueAsFloat(key, value);

  }
}

