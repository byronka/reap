package com.byronkatz;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;



public class LoanActivity extends Activity {
  
  private EditText yearlyInterestRate;
  private EditText downPayment;
  private Spinner loanTerm;
  private EditText totalPurchasePrice;
  private Button backButton;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.loan);
    
    //Hook up the components from the GUI to some variables here
    yearlyInterestRate = (EditText)findViewById(R.id.yearlyInterestRateEditText);
    downPayment =        (EditText)findViewById(R.id.downPaymentEditText);
    loanTerm =           (Spinner) findViewById(R.id.numOfCompoundingPeriodsSpinner);
    totalPurchasePrice = (EditText)findViewById(R.id.totalPurchasePriceEditText);
    backButton         = (Button)findViewById(R.id.backButton);
    
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
        this, R.array.numOfCompoundingPeriodsArray, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    loanTerm.setAdapter(adapter);
    
    //Set up the listeners for the inputs
    yearlyInterestRate.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.YEARLY_INTEREST_RATE;
        String value = yearlyInterestRate.getText().toString();
        RealEstateMarketAnalysisApplication.getInstance().getDataController().setValue(key, value);
        return false;
      }
    });
    
    downPayment.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View arg0, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.DOWN_PAYMENT;
        String value = downPayment.getText().toString();
        RealEstateMarketAnalysisApplication.getInstance().getDataController().setValue(key, value);
        return false;
      }});
    
    totalPurchasePrice.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View arg0, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.TOTAL_PURCHASE_VALUE;
        String value = totalPurchasePrice.getText().toString();
        RealEstateMarketAnalysisApplication.getInstance().getDataController().setValue(key, value);
        return false;
      }});
    
    loanTerm.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
          long arg3) {
        String value = null;
        String key = DatabaseAdapter.NUMBER_OF_COMPOUNDING_PERIODS;
        if (loanTerm.getItemAtPosition(pos).toString() == "Fixed-rate mortgage - 30 years") {
          value = "360";
        } else if (loanTerm.getItemAtPosition(pos).toString() == "Fixed-rate mortgage - 15 years") {
          value = "180";
        }
        RealEstateMarketAnalysisApplication.getInstance().getDataController().setValue(key, value);
        
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing.
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
