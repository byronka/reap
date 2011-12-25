package com.byronkatz;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;

public class TaxesActivity extends Activity {
  
  private EditText marginalTaxRate;
  private EditText buildingValue;
  private EditText propertyTaxRate;
  private EditText localMunicipalFees;
  private Button backButton;
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.taxes);
    
    marginalTaxRate    = (EditText)findViewById(R.id.marginalTaxRateEditText);
    buildingValue      = (EditText)findViewById(R.id.buildingValueEditText);
    propertyTaxRate    = (EditText)findViewById(R.id.propertyTaxRateEditText);
    localMunicipalFees = (EditText)findViewById(R.id.localMunicipalFeesEditText);
    backButton         = (Button)findViewById(R.id.backButton);
    
    assignValuesToFields();

    backButton.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
       finish();
      }
    });
  }
  
  @Override
  public void onPause() {
    super.onPause();
    
    ValueEnum key = ValueEnum.MARGINAL_TAX_RATE;
    Float value = Float.valueOf(marginalTaxRate.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.BUILDING_VALUE;
    value = Float.valueOf(buildingValue.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.PROPERTY_TAX_RATE;
    value = Float.valueOf(propertyTaxRate.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.LOCAL_MUNICIPAL_FEES;
    value = Float.valueOf(localMunicipalFees.getText().toString());
    dataController.setValueAsFloat(key, value);
  }
  private void assignValuesToFields() {
    
    Float mtr = dataController.getValueAsFloat(ValueEnum.MARGINAL_TAX_RATE);
    marginalTaxRate.setText(CalculatedVariables.displayPercentage(mtr));
    
    Float bv = dataController.getValueAsFloat(ValueEnum.BUILDING_VALUE);
    buildingValue.setText(CalculatedVariables.displayCurrency(bv));
    
    Float ptr = dataController.getValueAsFloat(ValueEnum.PROPERTY_TAX_RATE);
    propertyTaxRate.setText(CalculatedVariables.displayPercentage(ptr));
    
    Float lmf = dataController.getValueAsFloat(ValueEnum.LOCAL_MUNICIPAL_FEES);
    localMunicipalFees.setText(CalculatedVariables.displayCurrency(lmf));
  }
}
