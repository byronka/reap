package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class MortgagePayment {

  private Float monthlyInterestRate;
  private Integer numOfCompoundingPeriods;
  private Float loanAmount;
  private Float monthlyMortgagePayment;
  private Float yearlyMortgagePayment;
  private static final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();


  public MortgagePayment(Integer numOfCompoundingPeriods, Float loanAmount, 
      Float monthlyInterestRate) {

    this.numOfCompoundingPeriods = numOfCompoundingPeriods;
    this.loanAmount = loanAmount;
    this.monthlyInterestRate = monthlyInterestRate;
    calculateMortgagePayment();
    saveValues();

  }

  private void saveValues() {

    dataController.setValueAsFloat(ValueEnum.MONTHLY_MORTGAGE_PAYMENT, monthlyMortgagePayment);
    dataController.setValueAsFloat(ValueEnum.YEARLY_MORTGAGE_PAYMENT, yearlyMortgagePayment);

  }

  public Float getYearlyMortgagePayment() {
    return yearlyMortgagePayment;
  }

  public Float getMonthlyMortgagePayment() {
    return monthlyMortgagePayment;
  }

  private void calculateMortgagePayment() {
    //to avoid divide by zero error
    if (monthlyInterestRate == 0.0f || numOfCompoundingPeriods == 0) {
      monthlyMortgagePayment = 0.0f;
    } else {
      monthlyMortgagePayment = (float) ((loanAmount * monthlyInterestRate) / 
          (1 - (Math.pow(1 + monthlyInterestRate, -numOfCompoundingPeriods))));
    }
    yearlyMortgagePayment = GeneralCalculations.NUM_OF_MONTHS_IN_YEAR * monthlyMortgagePayment;

  }

}
