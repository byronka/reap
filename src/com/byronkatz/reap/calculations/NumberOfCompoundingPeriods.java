package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class NumberOfCompoundingPeriods {

  private Float numberOfCompoundingPeriods;
  private Float numberOfYears;
  
  public NumberOfCompoundingPeriods() {
    numberOfCompoundingPeriods = RealEstateMarketAnalysisApplication.getInstance().
        getDataController().getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS);
    numberOfYears = numberOfCompoundingPeriods / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
  }

  public Float getNumberOfCompoundingPeriods() {
    return numberOfCompoundingPeriods;
  }

  public Float getNumberOfYears() {
    return numberOfYears;
  }

}
