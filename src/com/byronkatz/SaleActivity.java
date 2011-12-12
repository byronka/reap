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
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.sale);
    
  //Hook up the components from the GUI to some variables here
    generalSaleExpenses   = (EditText)findViewById(R.id.generalSaleExpensesEditText);
    sellingBrokerRate     = (EditText)findViewById(R.id.sellingBrokerRateEditText);
    backButton            = (Button)  findViewById(R.id.backButton);
    
    //Set up the listeners for the inputs
    generalSaleExpenses.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.GENERAL_SALE_EXPENSES;
        String value = generalSaleExpenses.getText().toString();
        RealEstateMarketAnalysisApplication.getInstance().getDataController().setValue(key, value);
        return false;
      }
    });
    
    sellingBrokerRate.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.SELLING_BROKER_RATE;
        String value = sellingBrokerRate.getText().toString();
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
