package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class EstateValue {


  private Float originalEstateValue;
  private Float realEstateAppreciationRate;
  private Float futureEstateValue;
  private Boolean cached;
  private int cachedYear;

  private static final DataController dc = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();

  public EstateValue() {
    originalEstateValue = dc.getValueAsFloat(ValueEnum.TOTAL_PURCHASE_VALUE);
    realEstateAppreciationRate = dc.getValueAsFloat(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);
    cached = false;
    cachedYear = -1;
  }

  private void saveValue(int year) {
    dc.setValueAsFloat(ValueEnum.PROJECTED_HOME_VALUE, futureEstateValue, year);
  }
  
  public Float getEstateValue (final int year) {
    //if all the values are the same and the same year, use the cached data
    if ( !(   (cached == true) && (cachedYear == year)   ) ) {
      
      cachedYear = year;
      cached = true;
      int compoundingPeriodDesired = (year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR);
      Float rate = realEstateAppreciationRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
      futureEstateValue = GeneralCalculations.futureValue(compoundingPeriodDesired, rate, originalEstateValue);
      saveValue(year);
   
    }

    return futureEstateValue;
  }
}
