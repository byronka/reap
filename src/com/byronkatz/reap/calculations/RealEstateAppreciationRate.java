package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class RealEstateAppreciationRate {

  private Float yearlyRealEstateAppreciationRate;
  private Float monthlyRealEstateAppreciationRate;
  
  public RealEstateAppreciationRate() {
    yearlyRealEstateAppreciationRate = RealEstateMarketAnalysisApplication.getInstance().
    getDataController().getValueAsFloat(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);
    monthlyRealEstateAppreciationRate = yearlyRealEstateAppreciationRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
  }

  public Float getYearlyRealEstateAppreciationRate() {
    return yearlyRealEstateAppreciationRate;
  }

  public Float getMonthlyRealEstateAppreciationRate() {
    return monthlyRealEstateAppreciationRate;
  }

  public Float getFutureValueYearlyRear(int year) {
    return (float) Math.pow(1 + yearlyRealEstateAppreciationRate, year);
  }
  
  
}
