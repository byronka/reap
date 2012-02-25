package com.byronkatz.reap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class TaxesActivity extends Activity {
  
  private EditText marginalTaxRate;
  private EditText buildingValue;
  private EditText propertyTax;
  private EditText localMunicipalFees;
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  
  @Override
  public boolean onCreateOptionsMenu (Menu menu){
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.edit_data_page_menu, menu);
    return true;
  }
  
  /**
   * This gets called every time the menu is called
   */
  @Override
  public boolean onPrepareOptionsMenu (Menu menu) {
    super.onPrepareOptionsMenu(menu);
    saveValuesToCache();

    return true;
  }

  @Override
  public boolean onOptionsItemSelected (MenuItem item) {
    super.onOptionsItemSelected(item);

    Utility.switchForMenuItem(item, this, false);
    return false;
  }
  
  
  @Override
  public void onResume() {
    super.onResume();
    assignValuesToFields();

  }
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.taxes);
    
    marginalTaxRate    = (EditText)findViewById(R.id.marginalTaxRateEditText);
    buildingValue      = (EditText)findViewById(R.id.buildingValueEditText);
    propertyTax    = (EditText)findViewById(R.id.propertyTaxEditText);
    localMunicipalFees = (EditText)findViewById(R.id.localMunicipalFeesEditText);
        
    marginalTaxRate.setOnFocusChangeListener(
        new OnFocusChangeListenerWrapper(ValueEnum.MARGINAL_TAX_RATE));

    TextView marginalTaxRateTitle = 
        (TextView)findViewById(R.id.marginalTaxRateTitle);
    marginalTaxRateTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.marginalTaxRateHelpText, 
            R.string.marginalTaxRateTitleText, TaxesActivity.this);
      }
    });
    
    buildingValue.setOnFocusChangeListener(
        new OnFocusChangeListenerWrapper(ValueEnum.BUILDING_VALUE));

    
    TextView buildingValueTitle = 
        (TextView)findViewById(R.id.buildingValueTitle);
    buildingValueTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.buildingValueHelpText, 
            R.string.buildingValueTitleText, TaxesActivity.this);
      }
    });
    
    propertyTax.setOnFocusChangeListener(
        new OnFocusChangeListenerWrapper(ValueEnum.PROPERTY_TAX));
    
    TextView propertyTaxTitle = 
        (TextView)findViewById(R.id.propertyTaxTitle);
    propertyTaxTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.propertyTaxHelpText, 
            R.string.propertyTaxTitleText, TaxesActivity.this);
      }
    });
    
    localMunicipalFees.setOnFocusChangeListener(
        new OnFocusChangeListenerWrapper(ValueEnum.LOCAL_MUNICIPAL_FEES));
    
    TextView localMunicipalFeesTitle = 
        (TextView)findViewById(R.id.localMunicipalFeesTitle);
    localMunicipalFeesTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.localMunicipalFeesHelpText, 
            R.string.localMunicipalFeesTitleText, TaxesActivity.this);
      }
    });
    
  }
  
  public void callCalculator(View v) {
    Utility.callCalc(this);
  }
  
  private void saveValuesToCache() {
    ValueEnum key = ValueEnum.MARGINAL_TAX_RATE;
    Double value = Utility.parsePercentage(marginalTaxRate.getText().toString());
    dataController.setValueAsDouble(key, value);
    
    key = ValueEnum.BUILDING_VALUE;
    value = Utility.parseCurrency(buildingValue.getText().toString());
    dataController.setValueAsDouble(key, value);
    
    key = ValueEnum.PROPERTY_TAX;
    value = Utility.parseCurrency(propertyTax.getText().toString());
    dataController.setValueAsDouble(key, value);
    
    key = ValueEnum.LOCAL_MUNICIPAL_FEES;
    value = Utility.parseCurrency(localMunicipalFees.getText().toString());
    dataController.setValueAsDouble(key, value);
  }
  
  @Override
  public void onPause() {
    super.onPause();
    saveValuesToCache();

  }
  
  private void assignValuesToFields() {
    
    Double mtr = dataController.getValueAsDouble(ValueEnum.MARGINAL_TAX_RATE);
    marginalTaxRate.setText(Utility.displayPercentage(mtr));
    
    Double bv = dataController.getValueAsDouble(ValueEnum.BUILDING_VALUE);
    buildingValue.setText(Utility.displayCurrency(bv));
    
    Double ptr = dataController.getValueAsDouble(ValueEnum.PROPERTY_TAX);
    propertyTax.setText(Utility.displayCurrency(ptr));
    
    Double lmf = dataController.getValueAsDouble(ValueEnum.LOCAL_MUNICIPAL_FEES);
    localMunicipalFees.setText(Utility.displayCurrency(lmf));
  }
}
