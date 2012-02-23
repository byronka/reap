package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.ValueEnum;

public class EstateValue {


  private Double originalEstateValue;
  private Double realEstateAppreciationRate;
  private Double futureEstateValue;
  private Boolean cached;
  private int cachedYear;

  private DataController dc;

  public EstateValue(DataController dc) {
    this.dc = dc;
    originalEstateValue = dc.getValueAsDouble(ValueEnum.TOTAL_PURCHASE_VALUE);
    realEstateAppreciationRate = dc.getValueAsDouble(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);

  }

  private void saveValue(int year) {
    dc.setValueAsDouble(ValueEnum.PROJECTED_HOME_VALUE, futureEstateValue, year);
  }

  public Double getOriginalEstateValue() {
    return originalEstateValue;
  }
  
  /**
   * returns current value for year 1 and future value for future years
   * @param year current year.  1 is the year it was purchased
   * @return the current or future value for the estate value
   */
  public Double getEstateValue (final int year) {

      Double compoundingPeriodDesired = (double) (year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR);
      Double rate = realEstateAppreciationRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
      futureEstateValue = GeneralCalculations.futureValue(compoundingPeriodDesired, rate, originalEstateValue);
      saveValue(year);

    return futureEstateValue;
  }
}
