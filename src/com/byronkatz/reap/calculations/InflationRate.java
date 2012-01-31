package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class InflationRate {

  private Float yearlyInflationRate;
  private Float monthlyInflationRate;
  
  public InflationRate() {
    yearlyInflationRate = RealEstateMarketAnalysisApplication.getInstance().
    getDataController().getValueAsFloat(ValueEnum.INFLATION_RATE);
    monthlyInflationRate = yearlyInflationRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
  }

  public Float getYearlyInflationRate() {
    return yearlyInflationRate;
  }

  public Float getMonthlyInflationRate() {
    return monthlyInflationRate;
  }

  public Float getFutureValueYearlyIR(int year) {
    return (float) Math.pow(1 + yearlyInflationRate, year);
  }
}
