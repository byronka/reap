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
        Float totalPurchasevalue = 
            Float.valueOf(dataController.getValueAsFloat(ValueEnum.TOTAL_PURCHASE_VALUE));
        Float pmiDownPayment = totalPurchasevalue * 0.20f;
        String pmiDownPaymentText = String.valueOf(pmiDownPayment);
        downPayment.setText(pmiDownPaymentText);
        
      }
    });
    
    
    
    loanTerm.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
          long arg3) {
        
        int THIRTY_YEARS  = adapter.getPosition("Fixed-rate mortgage - 30 years");
        int FIFTEEN_YEARS = adapter.getPosition("Fixed-rate mortgage - 15 years");
        Float value = null;
        
        ValueEnum key = ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS;
        if (pos == THIRTY_YEARS) {
          value = 360.0f;
        } else if (pos == FIFTEEN_YEARS) {
          value = 360.0f;
        }
        dataController.setValueAsFloat(key, value);
        
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

  private void assignValuesToFields() {

    //Have to do the following in order to pick item in array by number - see setSelection()
    int THIRTY_YEARS  = adapter.getPosition("Fixed-rate mortgage - 30 years");
    int FIFTEEN_YEARS = adapter.getPosition("Fixed-rate mortgage - 15 years");

    Float numOfCompoundingPeriods = 
        dataController.getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS);
    
    if (numOfCompoundingPeriods.intValue() == 360) {
      loanTerm.setSelection(THIRTY_YEARS);
    } else if (numOfCompoundingPeriods.intValue() == 180) {
      loanTerm.setSelection(FIFTEEN_YEARS);
    }
    
    Float yir = dataController.getValueAsFloat(ValueEnum.YEARLY_INTEREST_RATE);
    yearlyInterestRate.setText(CalculatedVariables.displayPercentage(yir));
    
    Float dP = dataController.getValueAsFloat(ValueEnum.DOWN_PAYMENT);
    downPayment.setText(CalculatedVariables.displayCurrency(dP));
    
    Float tPP = dataController.getValueAsFloat(ValueEnum.TOTAL_PURCHASE_VALUE);
    totalPurchasePrice.setText(CalculatedVariables.displayCurrency(tPP));
    
    Float cC = dataController.getValueAsFloat(ValueEnum.CLOSING_COSTS);
    closingCosts.setText(CalculatedVariables.displayCurrency(cC));
  }
  
  @Override
  protected void onPause() {
    super.onPause();
    
    ValueEnum key = ValueEnum.TOTAL_PURCHASE_VALUE;
    Float value = Float.valueOf(totalPurchasePrice.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.DOWN_PAYMENT;
    value = Float.valueOf(downPayment.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.YEARLY_INTEREST_RATE;
    value = Float.valueOf(yearlyInterestRate.getText().toString());
    dataController.setValueAsFloat(key, value);

    key = ValueEnum.CLOSING_COSTS;
    value = Float.valueOf(closingCosts.getText().toString());
    dataController.setValueAsFloat(key, value);
  }
  
  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
  }
}
