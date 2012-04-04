package com.byronkatz.reap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.byronkatz.reap.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.OnFocusChangeListenerWrapper;
import com.byronkatz.reap.general.RealEstateAnalysisProcessorApplication;
import com.byronkatz.reap.general.TitleTextOnClickListenerWrapper;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

public class SaleActivity extends Activity {
  
  private EditText generalSaleExpenses;
  private EditText sellingBrokerRate;
  private final DataController dataController = 
      RealEstateAnalysisProcessorApplication.getInstance().getDataController();
  
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

    Utility.switchForMenuItem(item, this);
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
    setContentView(R.layout.sale);
    
  //Hook up the components from the GUI to some variables here
    generalSaleExpenses   = (EditText)findViewById(R.id.generalSaleExpensesEditText);
    sellingBrokerRate     = (EditText)findViewById(R.id.sellingBrokerRateEditText);
    

    generalSaleExpenses.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.GENERAL_SALE_EXPENSES));
    
    ((TextView)findViewById(R.id.generalSaleExpensesTitle)).setOnClickListener(
        new TitleTextOnClickListenerWrapper(ValueEnum.GENERAL_SALE_EXPENSES));

  
    sellingBrokerRate.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.SELLING_BROKER_RATE));
    
    ((TextView)findViewById(R.id.sellingBrokerRateTitle)).setOnClickListener(
        new TitleTextOnClickListenerWrapper(ValueEnum.SELLING_BROKER_RATE));
    
  }
  
  public void callCalculator(View v) {
    Utility.callCalc(this);
  }
  
  private void saveValuesToCache() {
    ValueEnum key = ValueEnum.GENERAL_SALE_EXPENSES;
    Double value = Utility.parseCurrency(generalSaleExpenses.getText().toString());
    dataController.putInputValue(value, key);
    
    key = ValueEnum.SELLING_BROKER_RATE;
    value = Utility.parsePercentage(sellingBrokerRate.getText().toString());
    dataController.putInputValue(value, key);
  }
  
  @Override
  public void onPause() {
    saveValuesToCache();
    dataController.saveFieldValues();
    super.onPause();
  }
  
  private void assignValuesToFields() {
    
    Double gse = dataController.getInputValue(ValueEnum.GENERAL_SALE_EXPENSES);
    generalSaleExpenses.setText(Utility.displayCurrency(gse));
    
    Double sbr = dataController.getInputValue(ValueEnum.SELLING_BROKER_RATE);
    sellingBrokerRate.setText(Utility.displayPercentage(sbr));
    
  }
  
}
