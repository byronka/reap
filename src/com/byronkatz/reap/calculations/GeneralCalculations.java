package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class GeneralCalculations {

  public final static int NUM_OF_MONTHS_IN_YEAR = 12;

  private Float inflationRate;
  private Float realEstateAppreciationRate;
  
  public GeneralCalculations(Float inflationRate, Float realEstateAppreciationRate) {
    this.inflationRate = inflationRate;
    this.realEstateAppreciationRate = realEstateAppreciationRate;
  }
  
  public static Float futureValue (final int numberOfCompoundingPeriods, 
      final Float rate, final Float originalValue) {

    return (float) (originalValue * Math.pow((1 + rate), numberOfCompoundingPeriods));
  }
  
  public static Float presentValue(final int numberOfCompoundingPeriods, 
      final Float rate, final Float futureValue) {
    
    return (float) (futureValue / Math.pow(1 + rate, numberOfCompoundingPeriods));
    
  }
  
  public Float getInflationFvAdjuster(int year) {
    return (float) Math.pow(1 + inflationRate , year);
  }
  
  public Float getRearFVAdjuster(int year) {
    return (float) Math.pow(1 + realEstateAppreciationRate, year);
  }
}
