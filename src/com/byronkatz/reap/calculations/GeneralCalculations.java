package com.byronkatz.reap.calculations;

public class GeneralCalculations {

  public final static int NUM_OF_MONTHS_IN_YEAR = 12;

  
  public static Double futureValue (final Double numberOfCompoundingPeriods, 
      final Double rate, final Double originalValue) {

    return(originalValue * Math.pow((1 + rate), numberOfCompoundingPeriods));
  }
  
  public static Double presentValue(final int numberOfCompoundingPeriods, 
      final Double rate, final Double futureValue) {
    
    return (futureValue / Math.pow(1 + rate, numberOfCompoundingPeriods));
    
  }
}
