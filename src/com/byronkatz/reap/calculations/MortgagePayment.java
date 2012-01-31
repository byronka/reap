package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class MortgagePayment {

  private Float totalPurchaseValue;
  private Float interestRate;
  private Float numOfCompoundingPeriods;
  private Float downPayment;
  private Float principal;
  private Float monthlyMortgagePayment;
  private Float yearlyMortgagePayment;

  private static final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();

  public MortgagePayment() {

    totalPurchaseValue = dataController.getValueAsFloat(ValueEnum.TOTAL_PURCHASE_VALUE);
    interestRate = dataController.getValueAsFloat(ValueEnum.YEARLY_INTEREST_RATE) / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    numOfCompoundingPeriods = dataController.getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS);
    downPayment = dataController.getValueAsFloat(ValueEnum.DOWN_PAYMENT);
    principal = totalPurchaseValue - downPayment;
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
    if (interestRate == 0.0f) {
      monthlyMortgagePayment = 0.0f;
    } else {
      monthlyMortgagePayment = (float) ((principal * interestRate) / 
          (1 - (Math.pow(1 + interestRate, -numOfCompoundingPeriods))));
    }
    yearlyMortgagePayment = GeneralCalculations.NUM_OF_MONTHS_IN_YEAR * monthlyMortgagePayment;

  }

}
