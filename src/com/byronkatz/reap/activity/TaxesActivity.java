package com.byronkatz.reap.activity;

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.OnFocusChangeListenerWrapper;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class TaxesActivity extends Activity {
  
  private EditText marginalTaxRate;
  private EditText buildingValue;
  private EditText propertyTax;
  private EditText localMunicipalFees;
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.taxes);
    
    marginalTaxRate    = (EditText)findViewById(R.id.marginalTaxRateEditText);
    buildingValue      = (EditText)findViewById(R.id.buildingValueEditText);
    propertyTax    = (EditText)findViewById(R.id.propertyTaxEditText);
    localMunicipalFees = (EditText)findViewById(R.id.localMunicipalFeesEditText);
    
    assignValuesToFields();
    
    marginalTaxRate.setOnFocusChangeListener(
        new OnFocusChangeListenerWrapper(ValueEnum.MARGINAL_TAX_RATE));

    ImageButton marginalTaxRateHelpButton = 
        (ImageButton)findViewById(R.id.marginalTaxRateHelpButton);
    marginalTaxRateHelpButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.marginalTaxRateDescriptionText, 
            R.string.marginalTaxRateTitleText, TaxesActivity.this);
      }
    });
    
    buildingValue.setOnFocusChangeListener(
        new OnFocusChangeListenerWrapper(ValueEnum.BUILDING_VALUE));

    
    ImageButton buildingValueHelpButton = 
        (ImageButton)findViewById(R.id.buildingValueHelpButton);
    buildingValueHelpButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.buildingValueDescriptionText, 
            R.string.buildingValueTitleText, TaxesActivity.this);
      }
    });
    
    propertyTax.setOnFocusChangeListener(
        new OnFocusChangeListenerWrapper(ValueEnum.PROPERTY_TAX));
    
    ImageButton propertyTaxHelpButton = 
        (ImageButton)findViewById(R.id.propertyTaxHelpButton);
    propertyTaxHelpButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.propertyTaxDescriptionText, 
            R.string.propertyTaxTitleText, TaxesActivity.this);
      }
    });
    
    localMunicipalFees.setOnFocusChangeListener(
        new OnFocusChangeListenerWrapper(ValueEnum.LOCAL_MUNICIPAL_FEES));
    
    ImageButton localMunicipalFeesHelpButton = 
        (ImageButton)findViewById(R.id.localMunicipalFeesHelpButton);
    localMunicipalFeesHelpButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.localMunicipalFeesDescriptionText, 
            R.string.localMunicipalFeesTitleText, TaxesActivity.this);
      }
    });
    
  }
  
  public void callCalculator(View v) {
    Utility.callCalc(this);
  }
  
  @Override
  public void onPause() {
    super.onPause();
    
    ValueEnum key = ValueEnum.MARGINAL_TAX_RATE;
    Float value = Utility.parsePercentage(marginalTaxRate.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.BUILDING_VALUE;
    value = Utility.parseCurrency(buildingValue.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.PROPERTY_TAX;
    value = Utility.parseCurrency(propertyTax.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.LOCAL_MUNICIPAL_FEES;
    value = Utility.parseCurrency(localMunicipalFees.getText().toString());
    dataController.setValueAsFloat(key, value);
  }
  
  private void assignValuesToFields() {
    
    Float mtr = dataController.getValueAsFloat(ValueEnum.MARGINAL_TAX_RATE);
    marginalTaxRate.setText(Utility.displayPercentage(mtr));
    
    Float bv = dataController.getValueAsFloat(ValueEnum.BUILDING_VALUE);
    buildingValue.setText(Utility.displayCurrency(bv));
    
    Float ptr = dataController.getValueAsFloat(ValueEnum.PROPERTY_TAX);
    propertyTax.setText(Utility.displayCurrency(ptr));
    
    Float lmf = dataController.getValueAsFloat(ValueEnum.LOCAL_MUNICIPAL_FEES);
    localMunicipalFees.setText(Utility.displayCurrency(lmf));
  }
}
