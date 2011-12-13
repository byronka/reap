package com.byronkatz;

import android.app.Activity;
import android.os.Bundle;
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
        String key = DatabaseAdapter.ESTIMATED_RENT_PAYMENTS;
        String value = estimatedRentPayments.getText().toString();
        dataController.setValue(key, value);
        return false;
      }
    });

    yearlyHomeInsurance.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.YEARLY_HOME_INSURANCE;
        String value = yearlyHomeInsurance.getText().toString();
        dataController.setValue(key, value);
        return false;
      }
    });

    vacancyAndCreditLoss.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.VACANCY_AND_CREDIT_LOSS_RATE;
        String value = vacancyAndCreditLoss.getText().toString();
        dataController.setValue(key, value);
        return false;
      }
    });

    fixupCosts.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.FIX_UP_COSTS;
        String value = fixupCosts.getText().toString();
        dataController.setValue(key, value);
        return false;
      }
    });

    initialYearlyGeneralExpenses.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.INITIAL_YEARLY_GENERAL_EXPENSES;
        String value = initialYearlyGeneralExpenses.getText().toString();
        dataController.setValue(key, value);
        return false;
      }
    });

    requiredRateOfReturn.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.REQUIRED_RATE_OF_RETURN;
        String value = requiredRateOfReturn.getText().toString();
        dataController.setValue(key, value);
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
    
    String erp = dataController.getValue(DatabaseAdapter.ESTIMATED_RENT_PAYMENTS);
    estimatedRentPayments.setText(erp);
    
    String yhi = dataController.getValue(DatabaseAdapter.YEARLY_HOME_INSURANCE);
    yearlyHomeInsurance.setText(yhi);
    
    String vacl = dataController.getValue(DatabaseAdapter.VACANCY_AND_CREDIT_LOSS_RATE);
    vacancyAndCreditLoss.setText(vacl);
    
    String fc = dataController.getValue(DatabaseAdapter.FIX_UP_COSTS);
    fixupCosts.setText(fc);
    
    String iyge = dataController.getValue(DatabaseAdapter.INITIAL_YEARLY_GENERAL_EXPENSES);
    initialYearlyGeneralExpenses.setText(iyge);
    
    String rrr = dataController.getValue(DatabaseAdapter.REQUIRED_RATE_OF_RETURN);
    requiredRateOfReturn.setText(rrr);
  }
}
