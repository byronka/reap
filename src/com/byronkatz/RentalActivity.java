package com.byronkatz;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;

public class RentalActivity extends Activity {

  private EditText estimatedRentPayments;
  private EditText yearlyHomeInsurance;
  private EditText vacancyAndCreditLoss;
  private EditText fixupCosts;
  private EditText initialYearlyGeneralExpenses;
  private EditText requiredRateOfReturn;
  private Button backButton;
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
    backButton                    = (Button)  findViewById(R.id.backButton);

    assignValuesToFields();
    
    //Set up the listeners for the inputs
    estimatedRentPayments.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        ValueEnum key = ValueEnum.ESTIMATED_RENT_PAYMENTS;
        Float value = Float.valueOf(estimatedRentPayments.getText().toString());
        dataController.setValueAsFloat(key, value);
        return false;
      }
    });

    yearlyHomeInsurance.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        ValueEnum key = ValueEnum.YEARLY_HOME_INSURANCE;
        Float value = Float.valueOf(yearlyHomeInsurance.getText().toString());
        dataController.setValueAsFloat(key, value);
        return false;
      }
    });

    vacancyAndCreditLoss.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        ValueEnum key = ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE;
        Float value = Float.valueOf(vacancyAndCreditLoss.getText().toString());
        dataController.setValueAsFloat(key, value);
        return false;
      }
    });

    fixupCosts.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        ValueEnum key = ValueEnum.FIX_UP_COSTS;
        Float value = Float.valueOf(fixupCosts.getText().toString());
        dataController.setValueAsFloat(key, value);
        return false;
      }
    });

    initialYearlyGeneralExpenses.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        ValueEnum key = ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES;
        Float value = Float.valueOf(initialYearlyGeneralExpenses.getText().toString());
        dataController.setValueAsFloat(key, value);
        return false;
      }
    });

    requiredRateOfReturn.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        ValueEnum key = ValueEnum.REQUIRED_RATE_OF_RETURN;
        Float value = Float.valueOf(requiredRateOfReturn.getText().toString());
        dataController.setValueAsFloat(key, value);
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
