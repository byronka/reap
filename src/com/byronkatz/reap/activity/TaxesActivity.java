package com.byronkatz.reap.activity;

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;
import com.byronkatz.reap.general.ValueEnum.ValueType;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class TaxesActivity extends Activity {
  
  private EditText marginalTaxRate;
  private EditText buildingValue;
  private EditText propertyTaxRate;
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
    propertyTaxRate    = (EditText)findViewById(R.id.propertyTaxRateEditText);
    localMunicipalFees = (EditText)findViewById(R.id.localMunicipalFeesEditText);
    
    assignValuesToFields();

    marginalTaxRate.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          Utility.setSelectionOnView(v, ValueType.PERCENTAGE);
        } else if (!hasFocus) {
          Utility.parseThenDisplayPercentage(v);
        } 
      }
    });
    
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
    
    buildingValue.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          Utility.setSelectionOnView(v, ValueType.CURRENCY);
        } else if (!hasFocus) {
          Utility.parseThenDisplayCurrency(v);
        }
      }
    });
    
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
    
    propertyTaxRate.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          Utility.setSelectionOnView(v, ValueType.PERCENTAGE);
        } else if (!hasFocus) {
          Utility.parseThenDisplayPercentage(v);
        }
      }
    });
    
    ImageButton propertyTaxRateHelpButton = 
        (ImageButton)findViewById(R.id.propertyTaxRateHelpButton);
    propertyTaxRateHelpButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.propertyTaxRateDescriptionText, 
            R.string.propertyTaxRateTitleText, TaxesActivity.this);
      }
    });
    
    localMunicipalFees.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          Utility.setSelectionOnView(v, ValueType.CURRENCY);
        } else if (!hasFocus) {
          Utility.parseThenDisplayCurrency(v);
        }   
      }
    });
    
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
  

  
  @Override
  public void onPause() {
    super.onPause();
    
    ValueEnum key = ValueEnum.MARGINAL_TAX_RATE;
    Float value = Utility.parsePercentage(marginalTaxRate.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.BUILDING_VALUE;
    value = Utility.parseCurrency(buildingValue.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.PROPERTY_TAX_RATE;
    value = Utility.parsePercentage(propertyTaxRate.getText().toString());
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
    
    Float ptr = dataController.getValueAsFloat(ValueEnum.PROPERTY_TAX_RATE);
    propertyTaxRate.setText(Utility.displayPercentage(ptr));
    
    Float lmf = dataController.getValueAsFloat(ValueEnum.LOCAL_MUNICIPAL_FEES);
    localMunicipalFees.setText(Utility.displayCurrency(lmf));
  }
}
