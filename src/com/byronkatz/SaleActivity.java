package com.byronkatz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class SaleActivity extends Activity {
  
  private EditText generalSaleExpenses;
  private EditText sellingBrokerRate;
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.sale);
    
  //Hook up the components from the GUI to some variables here
    generalSaleExpenses   = (EditText)findViewById(R.id.generalSaleExpensesEditText);
    sellingBrokerRate     = (EditText)findViewById(R.id.sellingBrokerRateEditText);
    
    assignValuesToFields();

    
  }
  
  @Override
  protected void onPause() {
    ValueEnum key = ValueEnum.GENERAL_SALE_EXPENSES;
    Float value = CalculatedVariables.parseCurrency(generalSaleExpenses.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.SELLING_BROKER_RATE;
    value = CalculatedVariables.parsePercentage(sellingBrokerRate.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    super.onPause();
  }
  
  private void assignValuesToFields() {
    
    Float gse = dataController.getValueAsFloat(ValueEnum.GENERAL_SALE_EXPENSES);
    generalSaleExpenses.setText(CalculatedVariables.displayCurrency(gse));
    
    Float sbr = dataController.getValueAsFloat(ValueEnum.SELLING_BROKER_RATE);
    sellingBrokerRate.setText(CalculatedVariables.displayPercentage(sbr));
    
  }
  
}
