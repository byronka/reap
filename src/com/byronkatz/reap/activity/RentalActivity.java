package com.byronkatz.reap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.OnFocusChangeListenerWrapper;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

public class RentalActivity extends Activity {

  private EditText estimatedRentPayments;
  private EditText initialHomeInsurance;
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
    initialHomeInsurance           = (EditText)findViewById(R.id.initialHomeInsuranceEditText);
    vacancyAndCreditLoss          = (EditText)findViewById(R.id.vacancyAndCreditLossEditText);
    fixupCosts                    = (EditText)findViewById(R.id.fixupCostsEditText);
    initialYearlyGeneralExpenses  = (EditText)findViewById(R.id.initialYearlyGeneralExpensesEditText);
    requiredRateOfReturn          = (EditText)findViewById(R.id.requiredRateOfReturnEditText);

    assignValuesToFields();

    estimatedRentPayments.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.ESTIMATED_RENT_PAYMENTS));

    TextView estimatedRentPaymentsTitle = 
        (TextView)findViewById(R.id.estimatedRentPaymentsTitle);
    estimatedRentPaymentsTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.estimatedRentPaymentsDescriptionText, 
            R.string.estimatedRentPaymentsTitleText, RentalActivity.this);
      }
    });

    initialHomeInsurance.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.INITIAL_HOME_INSURANCE));

    TextView initialHomeInsuranceTitle = 
        (TextView)findViewById(R.id.initialHomeInsuranceTitle);
    initialHomeInsuranceTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.initialHomeInsuranceHelpText, 
            R.string.initialHomeInsuranceTitleText, RentalActivity.this);
      }
    });

    vacancyAndCreditLoss.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE));

    TextView vacancyAndCreditLossTitle = 
        (TextView)findViewById(R.id.vacancyAndCreditLossTitle);
    vacancyAndCreditLossTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.vacancyAndCreditLossDescriptionText, 
            R.string.vacancyAndCreditLossTitleText, RentalActivity.this);
      }
    });

    fixupCosts.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.FIX_UP_COSTS));

    TextView fixupCostsTitle =
        (TextView)findViewById(R.id.fixupCostsTitle);
    fixupCostsTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.fixupCostsDescriptionText, R.string.fixupCostsTitleText, RentalActivity.this);
      }
    });

    initialYearlyGeneralExpenses.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES));

    TextView initialYearlyGeneralExpensesTitle =
        (TextView)findViewById(R.id.initialYearlyGeneralExpensesTitle);
    initialYearlyGeneralExpensesTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.initialYearlyGeneralExpensesDescriptionText, 
            R.string.initialYearlyGeneralExpensesTitleText, RentalActivity.this);
      }
    });

    requiredRateOfReturn.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.REQUIRED_RATE_OF_RETURN));

    TextView requiredRateOfReturnTitle = 
        (TextView)findViewById(R.id.requiredRateOfReturnTitle);
    requiredRateOfReturnTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.requiredRateOfReturnDescriptionText, R.string.requiredRateOfReturnTitleText, RentalActivity.this);
      }
    });

  }

  public void callCalculator(View v) {
    Utility.callCalc(this);
  }
  
  @Override
  public void onPause() {
    super.onPause();

    ValueEnum key = ValueEnum.ESTIMATED_RENT_PAYMENTS;
    Float value = Utility.parseCurrency(estimatedRentPayments.getText().toString());
    dataController.setValueAsFloat(key, value);

    key = ValueEnum.INITIAL_HOME_INSURANCE;
    value = Utility.parseCurrency(initialHomeInsurance.getText().toString());
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

    Float yhi = dataController.getValueAsFloat(ValueEnum.INITIAL_HOME_INSURANCE);
    initialHomeInsurance.setText(Utility.displayCurrency(yhi));

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
