package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class MortgagePayment {
  
  private LoanAmount aO;
  private InterestRate interestRate;
  private NumberOfCompoundingPeriods numOfCompoundingPeriods;
  private Float monthlyMortgagePayment;
  private Float yearlyMortgagePayment;

  private static final DataController dc = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();

  public MortgagePayment(InterestRate interestRate, LoanAmount aO,
      NumberOfCompoundingPeriods numOfCompoundingPeriods) {

    this.interestRate = interestRate;
    this.aO = aO;
    this.numOfCompoundingPeriods = numOfCompoundingPeriods;
    calculateMortgagePayment();
    saveValues();

  }

  private void saveValues() {

    dc.setValueAsFloat(ValueEnum.MONTHLY_MORTGAGE_PAYMENT, monthlyMortgagePayment);
    dc.setValueAsFloat(ValueEnum.YEARLY_MORTGAGE_PAYMENT, yearlyMortgagePayment);

  }

  public Float getYearlyMortgagePayment() {
    return yearlyMortgagePayment;
  }

  public Float getMonthlyMortgagePayment() {
    return monthlyMortgagePayment;
  }

  private void calculateMortgagePayment() {
    
    //to avoid divide by zero error
    if (interestRate.getMonthlyInterestRate() == 0.0f) {
      monthlyMortgagePayment = 0.0f;
    } else {
      monthlyMortgagePayment = (float) ((aO.getLoanAmount() * interestRate.getMonthlyInterestRate()) / 
          (1 - (Math.pow(1 + interestRate.getMonthlyInterestRate(), -numOfCompoundingPeriods.getNumberOfCompoundingPeriods()))));
    }
    yearlyMortgagePayment = GeneralCalculations.NUM_OF_MONTHS_IN_YEAR * monthlyMortgagePayment;

  }

}
