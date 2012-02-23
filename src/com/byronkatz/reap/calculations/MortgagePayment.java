package com.byronkatz.reap.calculations;

import android.util.Log;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.ValueEnum;

public class MortgagePayment {

  private Double monthlyMortgagePayment;
  private Double yearlyMortgagePayment;
  private DataController dataController;
  private Integer numOfCompoundingPeriodYears;


  public MortgagePayment(DataController dataController, 
      Integer numOfCompoundingPeriods, Double loanAmount, 
      Double yearlyInterestRate) {
    this.dataController = dataController;
    numOfCompoundingPeriodYears = numOfCompoundingPeriods / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    monthlyMortgagePayment = calculateMortgagePayment((double)yearlyInterestRate, numOfCompoundingPeriods, loanAmount);
    yearlyMortgagePayment = GeneralCalculations.NUM_OF_MONTHS_IN_YEAR * monthlyMortgagePayment;

  }
  
  /**
   * no-args test constructor
   */
  public MortgagePayment() {}

  private void saveValues(int year, Double monthlyMortgagePayment, Double yearlyMortgagePayment) {

    dataController.setValueAsDouble(ValueEnum.MONTHLY_MORTGAGE_PAYMENT, monthlyMortgagePayment, year);
    dataController.setValueAsDouble(ValueEnum.YEARLY_MORTGAGE_PAYMENT, yearlyMortgagePayment, year);

  }

  public Double getMonthlyMortgagePayment(int year) {

    if (year > numOfCompoundingPeriodYears) {
      saveValues(year, 0d, 0d);
      return 0d;
    } else {
      saveValues(year, monthlyMortgagePayment, yearlyMortgagePayment);
      return monthlyMortgagePayment;
    }

  }

  private Double calculateMortgagePayment(Double yearlyInterestRate, 
      Integer numOfCompoundingPeriods, Double loanAmount) {
    
    Double mP = 0.0d;
    
    //to avoid divide by zero error
    if (yearlyInterestRate == 0.0f || numOfCompoundingPeriods == 0) {
      mP = 0.0d;
    } else {
      mP = ((loanAmount * (yearlyInterestRate / 12)) / 
          (1.0f - (1 / (Math.pow(1 + (yearlyInterestRate / 12), numOfCompoundingPeriods)))));
    }
    
    if (mP < 0d) {
      mP = 0.0d;
    }
    
    return mP;
  }


}
