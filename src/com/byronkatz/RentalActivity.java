package com.byronkatz;

import com.byronkatz.ValueEnum.ValueType;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

public class RentalActivity extends Activity {

  private EditText estimatedRentPayments;
  private EditText yearlyHomeInsurance;
  private EditText vacancyAndCreditLoss;
  private EditText fixupCosts;
  private EditText initialYearlyGeneralExpenses;
  private EditText requiredRateOfReturn;
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();


  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.rental);

    //Hook up the components from the GUI to some variables here
    estimatedRentPayments         = (EditText)findViewById(R.id.estimatedRentPaymentsEditText);
    yearlyHomeInsurance           = (EditText)findViewById(R.id.yearlyHomeInsuranceEditText);
    vacancyAndCreditLoss          = (EditText)findViewById(R.id.vacancyAndCreditLossEditText);
    fixupCosts                    = (EditText)findViewById(R.id.fixupCostsEditText);
    initialYearlyGeneralExpenses  = (EditText)findViewById(R.id.initialYearlyGeneralExpensesEditText);
    requiredRateOfReturn          = (EditText)findViewById(R.id.requiredRateOfReturnEditText);

    assignValuesToFields();

    estimatedRentPayments.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          DataController.setSelectionOnView(v, ValueType.CURRENCY);
        }     
      }
    });
    
    yearlyHomeInsurance.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          DataController.setSelectionOnView(v, ValueType.CURRENCY);
        }     
      }
    });
    
    vacancyAndCreditLoss.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          DataController.setSelectionOnView(v, ValueType.PERCENTAGE);
        }     
      }
    });
    
    fixupCosts.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          DataController.setSelectionOnView(v, ValueType.CURRENCY);
        }     
      }
    });
    
    initialYearlyGeneralExpenses.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          DataController.setSelectionOnView(v, ValueType.CURRENCY);
        }     
      }
    });
    
    requiredRateOfReturn.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          DataController.setSelectionOnView(v, ValueType.PERCENTAGE);
        }     
      }
    });
  }

  @Override
  public void onPause() {
    super.onPause();
    
    ValueEnum key = ValueEnum.ESTIMATED_RENT_PAYMENTS;
    Float value = CalculatedVariables.parseCurrency(estimatedRentPayments.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.YEARLY_HOME_INSURANCE;
    value = CalculatedVariables.parseCurrency(yearlyHomeInsurance.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE;
    value = CalculatedVariables.parsePercentage(vacancyAndCreditLoss.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.FIX_UP_COSTS;
    value = CalculatedVariables.parseCurrency(fixupCosts.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES;
    value = CalculatedVariables.parseCurrency(initialYearlyGeneralExpenses.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.REQUIRED_RATE_OF_RETURN;
    value = CalculatedVariables.parsePercentage(requiredRateOfReturn.getText().toString());
    dataController.setValueAsFloat(key, value);

  }
  
  private void assignValuesToFields() {
    
    Float erp = dataController.getValueAsFloat(ValueEnum.ESTIMATED_RENT_PAYMENTS);
    estimatedRentPayments.setText(CalculatedVariables.displayCurrency(erp));
    
    Float yhi = dataController.getValueAsFloat(ValueEnum.YEARLY_HOME_INSURANCE);
    yearlyHomeInsurance.setText(CalculatedVariables.displayCurrency(yhi));
    
    Float vacl = dataController.getValueAsFloat(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE);
    vacancyAndCreditLoss.setText(CalculatedVariables.displayPercentage(vacl));
    
    Float fc = dataController.getValueAsFloat(ValueEnum.FIX_UP_COSTS);
    fixupCosts.setText(CalculatedVariables.displayCurrency(fc));
    
    Float iyge = dataController.getValueAsFloat(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES);
    initialYearlyGeneralExpenses.setText(CalculatedVariables.displayCurrency(iyge));
    
    Float rrr = dataController.getValueAsFloat(ValueEnum.REQUIRED_RATE_OF_RETURN);
    requiredRateOfReturn.setText(CalculatedVariables.displayPercentage(rrr));
  }
}
