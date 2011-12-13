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
  private EditText closingCosts;
  private Button backButton;
  ArrayAdapter<CharSequence> adapter;
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.loan);
    
    //Hook up the components from the GUI to some variables here
    yearlyInterestRate = (EditText)findViewById(R.id.yearlyInterestRateEditText);
    downPayment        = (EditText)findViewById(R.id.downPaymentEditText);
    loanTerm           = (Spinner) findViewById(R.id.numOfCompoundingPeriodsSpinner);
    totalPurchasePrice = (EditText)findViewById(R.id.totalPurchasePriceEditText);
    closingCosts       = (EditText)findViewById(R.id.closingCostsEditText);
    backButton         = (Button)findViewById(R.id.backButton);
    
    adapter = ArrayAdapter.createFromResource(
        this, R.array.numOfCompoundingPeriodsArray, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    loanTerm.setAdapter(adapter);

    assignValuesToFields();
    
    //Set up the listeners for the inputs
    yearlyInterestRate.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.YEARLY_INTEREST_RATE;
        String value = yearlyInterestRate.getText().toString();
        dataController.setValue(key, value);
        return false;
      }
    });
    
    downPayment.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View arg0, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.DOWN_PAYMENT;
        String value = downPayment.getText().toString();
        dataController.setValue(key, value);
        return false;
      }});
    
    totalPurchasePrice.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View arg0, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.TOTAL_PURCHASE_VALUE;
        String value = totalPurchasePrice.getText().toString();
        dataController.setValue(key, value);
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
        dataController.setValue(key, value);
        
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
    
    closingCosts.setOnKeyListener(new OnKeyListener() {
      
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.CLOSING_COSTS;
        String value = closingCosts.getText().toString();
        dataController.setValue(key, value);
        return false;
      }
    });
  }

  private void assignValuesToFields() {

    //Have to do the following in order to pick item in array by number - see setSelection()
    int THIRTY_YEARS  = adapter.getPosition("Fixed-rate mortgage - 30 years");
    int FIFTEEN_YEARS = adapter.getPosition("Fixed-rate mortgage - 15 years");

    String numOfCompoundingPeriods = 
        dataController.getValue(DatabaseAdapter.NUMBER_OF_COMPOUNDING_PERIODS);
    
    if (numOfCompoundingPeriods.equals("360")) {
      loanTerm.setSelection(THIRTY_YEARS);
    } else if (numOfCompoundingPeriods.equals("180")) {
      loanTerm.setSelection(FIFTEEN_YEARS);
    }
    
    String yir = dataController.getValue(DatabaseAdapter.YEARLY_INTEREST_RATE);
    yearlyInterestRate.setText(yir);
    
    String dP = dataController.getValue(DatabaseAdapter.DOWN_PAYMENT);
    downPayment.setText(dP);
    
    String tPP = dataController.getValue(DatabaseAdapter.TOTAL_PURCHASE_VALUE);
    totalPurchasePrice.setText(tPP);
    
    String cC = dataController.getValue(DatabaseAdapter.CLOSING_COSTS);
    closingCosts.setText(cC);
  }
  
}
