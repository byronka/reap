package com.byronkatz.reap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.OnFocusChangeListenerWrapper;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

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

    generalSaleExpenses.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.GENERAL_SALE_EXPENSES));
    
    ImageButton generalSaleExpensesHelpButton = 
        (ImageButton)findViewById(R.id.generalSaleExpensesHelpButton);
    generalSaleExpensesHelpButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.generalSaleExpensesDescriptionText, 
            R.string.generalSaleExpensesTitleText, SaleActivity.this);
      }
    });

  
    sellingBrokerRate.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.SELLING_BROKER_RATE));
    
    ImageButton sellingBrokerRateHelpButton = 
        (ImageButton)findViewById(R.id.sellingBrokerRateHelpButton);
    sellingBrokerRateHelpButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.sellingBrokerRateDescriptionText, 
            R.string.sellingBrokerRateTitleText, SaleActivity.this);
      }
    });
  }
  
  @Override
  protected void onPause() {
    ValueEnum key = ValueEnum.GENERAL_SALE_EXPENSES;
    Float value = Utility.parseCurrency(generalSaleExpenses.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.SELLING_BROKER_RATE;
    value = Utility.parsePercentage(sellingBrokerRate.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    super.onPause();
  }
  
  private void assignValuesToFields() {
    
    Float gse = dataController.getValueAsFloat(ValueEnum.GENERAL_SALE_EXPENSES);
    generalSaleExpenses.setText(Utility.displayCurrency(gse));
    
    Float sbr = dataController.getValueAsFloat(ValueEnum.SELLING_BROKER_RATE);
    sellingBrokerRate.setText(Utility.displayPercentage(sbr));
    
  }
  
}
