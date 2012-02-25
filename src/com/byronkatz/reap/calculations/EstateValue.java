package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.ValueEnum;

public class EstateValue {


  private Double originalEstateValue;
  private Double realEstateAppreciationRate;
  private Double futureEstateValue;
  private Boolean cached;
  private int cachedYear;
  private Double renovatedValue;
  private Double fixupCosts;


  private DataController dc;

  public EstateValue(DataController dc) {
    this.dc = dc;
    fixupCosts = dc.getValueAsDouble(ValueEnum.FIX_UP_COSTS);

    originalEstateValue = dc.getValueAsDouble(ValueEnum.TOTAL_PURCHASE_VALUE);
    renovatedValue = fixupCosts + originalEstateValue;
        
    realEstateAppreciationRate = dc.getValueAsDouble(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);

  }

  
  private void saveValue(int year) {
    dc.setValueAsDouble(ValueEnum.PROJECTED_HOME_VALUE, futureEstateValue, year);
  }

  public Double getOriginalEstateValue() {
    return originalEstateValue;
  }
  
  public Double getFixupCosts() {
    return fixupCosts;
  }
  
  /**
   * returns current value for year 1 and future value for future years
   * @param year current year.  1 is the year it was purchased
   * @return the current or future value for the estate value
   */
  public Double getEstateValue (final int year) {

      Double compoundingPeriodDesired = (double) (year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR);
      Double rate = realEstateAppreciationRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
      futureEstateValue = GeneralCalculations.futureValue(compoundingPeriodDesired, rate, renovatedValue);
      saveValue(year);

    return futureEstateValue;
  }
}
