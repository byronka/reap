package com.byronkatz;

import com.byronkatz.ValueEnum.ValueType;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
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
    
    adapter = ArrayAdapter.createFromResource(
        this, R.array.numOfCompoundingPeriodsArray, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    loanTerm.setAdapter(adapter);

    assignValuesToFields();
    
    pmiButton.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        Float totalPurchasevalue = 
            CalculatedVariables.parseCurrency(totalPurchasePrice.getText().toString());
        Float pmiDownPayment = totalPurchasevalue * 0.20f;
        String pmiDownPaymentText = CalculatedVariables.displayCurrency(pmiDownPayment);
        downPayment.setText(pmiDownPaymentText);
        
      }
    });
    
    yearlyInterestRate.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          DataController.setSelectionOnView(v, ValueType.PERCENTAGE);
        }     
      }
    });
    
    downPayment.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          DataController.setSelectionOnView(v, ValueType.CURRENCY);
        }     
      }
    });
    
    totalPurchasePrice.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          DataController.setSelectionOnView(v, ValueType.CURRENCY);
        }     
      }
    });
    
    closingCosts.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          DataController.setSelectionOnView(v, ValueType.CURRENCY);
        }     
      }
    });
    
    loanTerm.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
          long arg3) {
        
        int THIRTY_YEARS  = adapter.getPosition("Fixed-rate mortgage - 30 years");
        int FIFTEEN_YEARS = adapter.getPosition("Fixed-rate mortgage - 15 years");
        Float value = null;
        
        if (pos == THIRTY_YEARS) {
          value = 360.0f;
        } else if (pos == FIFTEEN_YEARS) {
          value = 180.0f;
        }
        ValueEnum key = ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS;
        dataController.setValueAsFloat(key, value);
        
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing.
      }
    });
    
  }

//  private void setSelectionOnView(View v, ValueType valueType) {
//    EditText editText = (EditText) v;
//    //we'll use textInEditText to measure the string for the selection
//    String textInEditText = editText.getText().toString();
//    int textLength = textInEditText.length();
//    
//    switch (valueType) {
//    case CURRENCY:
//      editText.setSelection(1, textLength);
//      break;
//    case PERCENTAGE:
//      editText.setSelection(0, textLength - 1);
//      break;
//    case STRING:
//      editText.setSelection(0, textLength);
//      break;
//      default:
//        System.err.println("shouldn't get here in setSelectionOnView");
//    }
//  }
  
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
    Float value = CalculatedVariables.parseCurrency(totalPurchasePrice.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.DOWN_PAYMENT;
    value = CalculatedVariables.parseCurrency(downPayment.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.YEARLY_INTEREST_RATE;
    value = CalculatedVariables.parsePercentage(yearlyInterestRate.getText().toString());
    dataController.setValueAsFloat(key, value);

    key = ValueEnum.CLOSING_COSTS;
    value = CalculatedVariables.parseCurrency(closingCosts.getText().toString());
    dataController.setValueAsFloat(key, value);
  }
  
  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
  }
}
