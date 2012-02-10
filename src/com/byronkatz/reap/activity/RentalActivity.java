package com.byronkatz.reap.activity;

import android.app.Activity;
import android.content.Intent;
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

    estimatedRentPayments.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.ESTIMATED_RENT_PAYMENTS));

    ImageButton estimatedRentPaymentsHelpButton = 
        (ImageButton)findViewById(R.id.estimatedRentPaymentsHelpButton);
    estimatedRentPaymentsHelpButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.estimatedRentPaymentsDescriptionText, 
            R.string.estimatedRentPaymentsTitleText, RentalActivity.this);
      }
    });

    yearlyHomeInsurance.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.YEARLY_HOME_INSURANCE));

    ImageButton yearlyHomeInsuranceHelpButton = 
        (ImageButton)findViewById(R.id.yearlyHomeInsuranceHelpButton);
    yearlyHomeInsuranceHelpButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.yearlyHomeInsuranceDescriptionText, 
            R.string.yearlyHomeInsuranceTitleText, RentalActivity.this);
      }
    });

    vacancyAndCreditLoss.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE));

    ImageButton vacancyAndCreditLossHelpButton = 
        (ImageButton)findViewById(R.id.vacancyAndCreditLossHelpButton);
    vacancyAndCreditLossHelpButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.vacancyAndCreditLossDescriptionText, 
            R.string.vacancyAndCreditLossTitleText, RentalActivity.this);
      }
    });

    fixupCosts.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.FIX_UP_COSTS));

    ImageButton fixupCostsHelpButton =
        (ImageButton)findViewById(R.id.fixupCostsHelpButton);
    fixupCostsHelpButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.fixupCostsDescriptionText, R.string.fixupCostsTitleText, RentalActivity.this);
      }
    });

    initialYearlyGeneralExpenses.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES));

    ImageButton initialYearlyGeneralExpensesHelpButton =
        (ImageButton)findViewById(R.id.initialYearlyGeneralExpensesHelpButton);
    initialYearlyGeneralExpensesHelpButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.initialYearlyGeneralExpensesDescriptionText, 
            R.string.initialYearlyGeneralExpensesTitleText, RentalActivity.this);
      }
    });

    requiredRateOfReturn.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.REQUIRED_RATE_OF_RETURN));

    ImageButton requiredRateOfReturnHelpButton = 
        (ImageButton)findViewById(R.id.requiredRateOfReturnHelpButton);
    requiredRateOfReturnHelpButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.requiredRateOfReturnDescriptionText, R.string.requiredRateOfReturnTitleText, RentalActivity.this);
      }
    });

  }

  public void callCalculator(View v) {
    Intent i = new Intent();
    i.setClassName("com.android.calculator2",
    "com.android.calculator2.Calculator");
    startActivity(i);
  }
  
  @Override
  public void onPause() {
    super.onPause();

    ValueEnum key = ValueEnum.ESTIMATED_RENT_PAYMENTS;
    Float value = Utility.parseCurrency(estimatedRentPayments.getText().toString());
    dataController.setValueAsFloat(key, value);

    key = ValueEnum.YEARLY_HOME_INSURANCE;
    value = Utility.parseCurrency(yearlyHomeInsurance.getText().toString());
    dataController.setValueAsFloat(key, value);

    key = ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE;
    value = Utility.parsePercentage(vacancyAndCreditLoss.getText().toString());
    dataController.setValueAsFloat(key, value);

    key = ValueEnum.FIX_UP_COSTS;
    value = Utility.parseCurrency(fixupCosts.getText().toString());
    dataController.setValueAsFloat(key, value);

    key = ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES;
    value = Utility.parseCurrency(initialYearlyGeneralExpenses.getText().toString());
    dataController.setValueAsFloat(key, value);

    key = ValueEnum.REQUIRED_RATE_OF_RETURN;
    value = Utility.parsePercentage(requiredRateOfReturn.getText().toString());
    dataController.setValueAsFloat(key, value);

  }

  private void assignValuesToFields() {

    Float erp = dataController.getValueAsFloat(ValueEnum.ESTIMATED_RENT_PAYMENTS);
    estimatedRentPayments.setText(Utility.displayCurrency(erp));

    Float yhi = dataController.getValueAsFloat(ValueEnum.YEARLY_HOME_INSURANCE);
    yearlyHomeInsurance.setText(Utility.displayCurrency(yhi));

    Float vacl = dataController.getValueAsFloat(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE);
    vacancyAndCreditLoss.setText(Utility.displayPercentage(vacl));

    Float fc = dataController.getValueAsFloat(ValueEnum.FIX_UP_COSTS);
    fixupCosts.setText(Utility.displayCurrency(fc));

    Float iyge = dataController.getValueAsFloat(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES);
    initialYearlyGeneralExpenses.setText(Utility.displayCurrency(iyge));

    Float rrr = dataController.getValueAsFloat(ValueEnum.REQUIRED_RATE_OF_RETURN);
    requiredRateOfReturn.setText(Utility.displayPercentage(rrr));
  }
}
