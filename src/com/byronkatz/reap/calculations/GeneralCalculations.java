package com.byronkatz.reap.calculations;

public class GeneralCalculations {

  public final static int NUM_OF_MONTHS_IN_YEAR = 12;

  
  public static Float futureValue (final Float numberOfCompoundingPeriods, 
      final Float rate, final Float originalValue) {

    return (float) (originalValue * Math.pow((1 + rate), numberOfCompoundingPeriods));
  }
  
  public static Float presentValue(final int numberOfCompoundingPeriods, 
      final Float rate, final Float futureValue) {
    
    return (float) (futureValue / Math.pow(1 + rate, numberOfCompoundingPeriods));
    
  }
}
