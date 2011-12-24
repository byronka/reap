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
  private Button pmiButton;
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
    pmiButton          = (Button)findViewById(R.id.calcPMIDownPaymentButton);
    backButton         = (Button)findViewById(R.id.backButton);
    
    adapter = ArrayAdapter.createFromResource(
        this, R.array.numOfCompoundingPeriodsArray, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    loanTerm.setAdapter(adapter);

    assignValuesToFields();
    
    pmiButton.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        Double totalPurchasevalue = 
            Double.valueOf(dataController.getValue(DatabaseAdapter.TOTAL_PURCHASE_VALUE));
        Double pmiDownPayment = totalPurchasevalue * 0.20;
        String pmiDownPaymentText = String.valueOf(pmiDownPayment);
        downPayment.setText(pmiDownPaymentText);
        
      }
    });
    
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
        
        int THIRTY_YEARS  = adapter.getPosition("Fixed-rate mortgage - 30 years");
        int FIFTEEN_YEARS = adapter.getPosition("Fixed-rate mortgage - 15 years");
        String value = null;
        
        String key = DatabaseAdapter.NUMBER_OF_COMPOUNDING_PERIODS;
        if (pos == THIRTY_YEARS) {
          value = "360";
        } else if (pos == FIFTEEN_YEARS) {
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
  
  @Override
  protected void onPause() {
    // TODO Auto-generated method stub
    super.onPause();
  }
  
  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
  }
}
