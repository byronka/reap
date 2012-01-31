package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class InterestRate {

  private Float monthlyInterestRate;
  private Float yearlyInterestRate;
  
  public InterestRate() {
    yearlyInterestRate = (RealEstateMarketAnalysisApplication.getInstance().
        getDataController().getValueAsFloat(ValueEnum.YEARLY_INTEREST_RATE));
    monthlyInterestRate = yearlyInterestRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
  }

  public Float getMonthlyInterestRate() {
    return monthlyInterestRate;
  }
  
  public Float getFutureValueMonthlyInterestRate(int compoundingPeriodDesired) {
    return (float) Math.pow(1 + monthlyInterestRate, compoundingPeriodDesired);
  }

}
