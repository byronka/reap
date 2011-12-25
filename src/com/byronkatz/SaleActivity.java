package com.byronkatz;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;

public class SaleActivity extends Activity {
  
  private EditText generalSaleExpenses;
  private EditText sellingBrokerRate;
  private Button backButton;
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
    backButton            = (Button)  findViewById(R.id.backButton);
    
    assignValuesToFields();
    
   
    
    backButton.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        finish();
        
      }
    });
    
  }
  
  @Override
  protected void onPause() {
    ValueEnum key = ValueEnum.GENERAL_SALE_EXPENSES;
    Float value = Float.valueOf(generalSaleExpenses.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.SELLING_BROKER_RATE;
    value = Float.valueOf(sellingBrokerRate.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    super.onPause();
  }
  
  private void assignValuesToFields() {
    
    Float gse = dataController.getValueAsFloat(ValueEnum.GENERAL_SALE_EXPENSES);
    generalSaleExpenses.setText(String.valueOf(gse));
    
    Float sbr = dataController.getValueAsFloat(ValueEnum.SELLING_BROKER_RATE);
    sellingBrokerRate.setText(String.valueOf(sbr));
    
  }
  
}
