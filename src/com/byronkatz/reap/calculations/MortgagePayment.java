package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.ValueEnum;

public class MortgagePayment {

  private Float monthlyInterestRate;
  private Integer numOfCompoundingPeriods;
  private Float loanAmount;
  private Float monthlyMortgagePayment;
  private Float yearlyMortgagePayment;
  private DataController dataController;
  private Integer numOfCompoundingPeriodYears;


  public MortgagePayment(DataController dataController, 
      Integer numOfCompoundingPeriods, Float loanAmount, 
      Float monthlyInterestRate) {
    this.dataController = dataController;
    this.numOfCompoundingPeriods = numOfCompoundingPeriods;
    numOfCompoundingPeriodYears = numOfCompoundingPeriods / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    this.loanAmount = loanAmount;
    this.monthlyInterestRate = monthlyInterestRate;
    calculateMortgagePayment();

  }

  private void saveValues(int year, Float monthlyMortgagePayment, Float yearlyMortgagePayment) {

    dataController.setValueAsFloat(ValueEnum.MONTHLY_MORTGAGE_PAYMENT, monthlyMortgagePayment, year);
    dataController.setValueAsFloat(ValueEnum.YEARLY_MORTGAGE_PAYMENT, yearlyMortgagePayment, year);

  }

  public Float getMonthlyMortgagePayment(int year) {

    if (year > numOfCompoundingPeriodYears) {
      saveValues(year, 0f, 0f);
      return 0f;
    } else {
      saveValues(year, monthlyMortgagePayment, yearlyMortgagePayment);
      return monthlyMortgagePayment;
    }

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
