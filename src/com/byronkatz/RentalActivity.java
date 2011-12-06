package com.byronkatz;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.Spinner;

public class RentalActivity extends Activity {

  private EditText estimatedRentPayments;
  private EditText yearlyHomeInsurance;
  private EditText vacancyAndCreditLoss;
  private EditText fixupCosts;
  private EditText ininialYearlyGeneralExpenses;
  private EditText requiredRateOfReturn;


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
    ininialYearlyGeneralExpenses  = (EditText)findViewById(R.id.initialYearlyGeneralExpensesEditText);
    requiredRateOfReturn          = (EditText)findViewById(R.id.requiredRateOfReturnEditText);

    //Set up the listeners for the inputs
    estimatedRentPayments.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.ESTIMATED_RENT_PAYMENTS;
        String value = estimatedRentPayments.getText().toString();
        RealEstateMarketAnalysisApplication.getInstance().getDataController().setValue(key, value);
        return false;
      }
    });

    yearlyHomeInsurance.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.YEARLY_HOME_INSURANCE;
        String value = yearlyHomeInsurance.getText().toString();
        RealEstateMarketAnalysisApplication.getInstance().getDataController().setValue(key, value);
        return false;
      }
    });

    vacancyAndCreditLoss.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.VACANCY_AND_CREDIT_LOSS_RATE;
        String value = vacancyAndCreditLoss.getText().toString();
        RealEstateMarketAnalysisApplication.getInstance().getDataController().setValue(key, value);
        return false;
      }
    });

    fixupCosts.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.FIX_UP_COSTS;
        String value = fixupCosts.getText().toString();
        RealEstateMarketAnalysisApplication.getInstance().getDataController().setValue(key, value);
        return false;
      }
    });

    ininialYearlyGeneralExpenses.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.INITIAL_YEARLY_GENERAL_EXPENSES;
        String value = ininialYearlyGeneralExpenses.getText().toString();
        RealEstateMarketAnalysisApplication.getInstance().getDataController().setValue(key, value);
        return false;
      }
    });

    requiredRateOfReturn.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.REQUIRED_RATE_OF_RETURN;
        String value = requiredRateOfReturn.getText().toString();
        RealEstateMarketAnalysisApplication.getInstance().getDataController().setValue(key, value);
        return false;
      }
    });
  }

}
