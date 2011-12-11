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
    
    marginalTaxRate.setOnKeyListener(new OnKeyListener() {
      
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.MARGINAL_TAX_RATE;
        String value = marginalTaxRate.getText().toString();
        RealEstateMarketAnalysisApplication.getInstance().getDataController().setValue(key, value);
        return false;
      }
    });
    
    buildingValue.setOnKeyListener(new OnKeyListener() {
      
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.BUILDING_VALUE;
        String value = buildingValue.getText().toString();
        RealEstateMarketAnalysisApplication.getInstance().getDataController().setValue(key, value);
        return false;
      }
    });
    
    propertyTaxRate.setOnKeyListener(new OnKeyListener() {
      
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.PROPERTY_TAX_RATE;
        String value = propertyTaxRate.getText().toString();
        RealEstateMarketAnalysisApplication.getInstance().getDataController().setValue(key, value);
        return false;
      }
    });
    
    localMunicipalFees.setOnKeyListener(new OnKeyListener() {
      
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.LOCAL_MUNICIPAL_FEES;
        String value = localMunicipalFees.getText().toString();
        RealEstateMarketAnalysisApplication.getInstance().getDataController().setValue(key, value);
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
}
