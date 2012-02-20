package com.byronkatz.reap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class AppInfoActivity extends Activity {

  private final DataController dataController = RealEstateMarketAnalysisApplication
      .getInstance().getDataController();
  public final static String INCORRECT = "\nINCORRECT!";
  public final static String CORRECT = "\nchecks out\n\n";
  public final static Double EPSILON = 0.00001d;


  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.math_check);
  }

  @Override
  public void onResume() {

    super.onResume();
    int year = getIntent().getExtras().getInt("year", 1);

    showCalculations(year);
  }

  private void showCalculations(int year) {

    TextView mathCheckTextView = (TextView) findViewById(R.id.mathCheckTextView);
    CharSequence checkMathText = runCheckCalcMath(year); 
    mathCheckTextView.setText(checkMathText);
  }

  private String runCheckCalcMath(int year) {

    StringBuffer checkCalcsString = new StringBuffer();

    checkCalcsString = getRentalIncome(checkCalcsString, year);
    checkCalcsString = getBeforeTaxCashFlow(checkCalcsString, year);


    return checkCalcsString.toString();
  }

  
  private StringBuffer getRentalIncome(StringBuffer s, int year) {

    Double fvMonthlyRent = dataController.getValueAsDouble(ValueEnum.MONTHLY_RENT_FV, year);
    Double rentalIncome = dataController.getValueAsDouble(ValueEnum.YEARLY_INCOME, year);
    Double vacancyRate = dataController.getValueAsDouble(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE);

    s.append("RENTAL INCOME");
    s.append(String.format("\nMonthly Rent (MR): %.2f", fvMonthlyRent));
    s.append(String.format("\nVacancy rate (VR): %.4f", vacancyRate));
    s.append(String.format("\nRental Income (RI): %.2f", rentalIncome));
    s.append("\nCheck:\n  MR * (1-VR) * 12 months = RI");
    s.append(String.format("\nCheck:\n  %.2f * (1 - %.4f) * 12 = %.2f", fvMonthlyRent, vacancyRate, rentalIncome));
    Double actualRI = fvMonthlyRent * (1 - vacancyRate) * 12;

    if (Math.abs(actualRI - rentalIncome) < EPSILON) {
      s.append(CORRECT);
    } else {
      s.append(INCORRECT);
      s.append("\nActual answer is:");
      s.append(String.format("\n  %.2f * (1 - %.2f) * 12 = %.2f\n\n", fvMonthlyRent, vacancyRate, actualRI));
    }

    return s;
  }

  private StringBuffer getBeforeTaxCashFlow(StringBuffer s, int year) {
    // cashflowIn - cashflowOut

    final Double yearlyPrivateMortgageInsurance = dataController.getValueAsDouble(
        ValueEnum.YEARLY_PRIVATE_MORTGAGE_INSURANCE, year);
    final Double yearlyMortgagePayment = dataController.getValueAsDouble(
        ValueEnum.YEARLY_MORTGAGE_PAYMENT, year);
    final Double fVPropertyTax = dataController.getValueAsDouble(
        ValueEnum.YEARLY_PROPERTY_TAX, year);
    final Double fVMunicipalFees = dataController.getValueAsDouble(
        ValueEnum.YEARLY_MUNICIPAL_FEES, year);
    final Double fVYearlyGeneralExpenses = dataController.getValueAsDouble(
        ValueEnum.YEARLY_GENERAL_EXPENSES, year);
    final Double fVYearlyHomeInsurance = dataController.getValueAsDouble(
        ValueEnum.YEARLY_HOME_INSURANCE, year);
    final Double fVNetYearlyIncome = dataController.getValueAsDouble(
        ValueEnum.YEARLY_INCOME, year);
    final Double yearlyOutlay = dataController.getValueAsDouble(
        ValueEnum.YEARLY_OUTLAY, year);

    final Double actualYearlyOutlay = fVPropertyTax + fVMunicipalFees + 
        yearlyMortgagePayment + fVYearlyGeneralExpenses + 
        fVYearlyHomeInsurance + yearlyPrivateMortgageInsurance;
    final Double actualYearlyBeforeTaxCashFlow = fVNetYearlyIncome - actualYearlyOutlay;
    final Double yearlyBeforeTaxCashFlow = dataController.getValueAsDouble(
        ValueEnum.YEARLY_BEFORE_TAX_CASH_FLOW, year);

    s.append("YEARLY OUTLAY (yO)");
    s.append(String.format("\nYearly Private Mortgage Insurance (yPMI): %.2f +", yearlyPrivateMortgageInsurance));
    s.append(String.format("\nYearly Mortgage Payment (yMP): %.2f +", yearlyMortgagePayment));
    s.append(String.format("\nYearly Property Tax (yPT): %.2f +", fVPropertyTax));
    s.append(String.format("\nYearly Municipal Fees (yMF): %.2f +", fVMunicipalFees));
    s.append(String.format("\nYearly General Expenses (yGE): %.2f +", fVYearlyGeneralExpenses));
    s.append(String.format("\nYearly Home Insurance (yHI): %.2f", fVYearlyHomeInsurance));
    s.append("\n-----------------------------");
    s.append(String.format("\nYearly Outlay (yO): %.2f", yearlyOutlay));
    
    if (Math.abs(actualYearlyOutlay - yearlyOutlay) < EPSILON) {
      s.append(CORRECT);
    } else {
      s.append(INCORRECT);
      s.append(String.format("\nActual answer is: %.2f\n\n", actualYearlyOutlay));
    }

    s.append("YEARLY BEFORE TAX CASH FLOW");
    s.append(String.format("\nYearly Net Income (yNI): %.2f", fVNetYearlyIncome));
    s.append(String.format("\nYearly Before Tax Cash Flow (yBTCF): %.2f", yearlyBeforeTaxCashFlow));
    s.append("\nCheck:\n  yNI - yO = yBTCF");
    s.append(String.format("\n%.2f - %.2f = %.2f", fVNetYearlyIncome, yearlyOutlay, yearlyBeforeTaxCashFlow));
    
    if (Math.abs(actualYearlyBeforeTaxCashFlow - yearlyBeforeTaxCashFlow) < EPSILON) {
      s.append(CORRECT);
    } else {
      s.append(INCORRECT);
      s.append(String.format("\nActual answer is: %.2f\n\n", actualYearlyBeforeTaxCashFlow));
    }


    return s;
  }
  

}
