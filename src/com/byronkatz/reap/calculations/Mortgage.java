package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class Mortgage {

  private Float downPayment;
  private Float loanAmount;
  private MortgagePayment mortgagePayment;
  private Float yearlyInterestRate;
  private Integer numberOfCompoundingPeriods;
  
  private static final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  
  public Mortgage(EstateValue estateValue) {

    numberOfCompoundingPeriods = dataController.getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS).intValue();
    downPayment = dataController.getValueAsFloat(ValueEnum.DOWN_PAYMENT);
    loanAmount = estateValue.getEstateValue(0) - downPayment;

    yearlyInterestRate = dataController.getValueAsFloat(ValueEnum.YEARLY_INTEREST_RATE) / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;

    mortgagePayment = new MortgagePayment(
        numberOfCompoundingPeriods, loanAmount, yearlyInterestRate);
  }

  public MortgagePayment getMortgagePayment() {
    return mortgagePayment;
  }

  public Float getDownPayment() {
    return downPayment;
  }

  public Float getLoanAmount() {
    return loanAmount;
  }

  public Float getYearlyInterestRate() {
    return yearlyInterestRate;
  }

  public Integer getNumberOfCompoundingPeriods() {
    return numberOfCompoundingPeriods;
  }

}
