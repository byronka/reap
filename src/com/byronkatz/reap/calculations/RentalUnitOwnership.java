package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class RentalUnitOwnership {
  
  private Float propertyTax;
  private Float fixupCosts;
  private Float yearlyRealEstateAppreciationRate;
  private Float yearlyInflationRate;
  private Float monthlyInflationRate;
  private Float monthlyRealEstateAppreciationRate;
  
  private static final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  
  public RentalUnitOwnership() {
    fixupCosts = dataController.getValueAsFloat(ValueEnum.FIX_UP_COSTS);
    propertyTax = dataController.getValueAsFloat(ValueEnum.PROPERTY_TAX);
    yearlyRealEstateAppreciationRate = dataController.getValueAsFloat(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);
    monthlyRealEstateAppreciationRate = yearlyRealEstateAppreciationRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    yearlyInflationRate = dataController.getValueAsFloat(ValueEnum.INFLATION_RATE);
    monthlyInflationRate = yearlyInflationRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;

  }
  
  public Float getYearlyRealEstateAppreciationRate() {
    return yearlyRealEstateAppreciationRate;
  }
  
  public Float getMonthlyRealEstateAppreciationRate() {
    return monthlyRealEstateAppreciationRate;
  }
  
  public Float getPropertyTax() {
    return propertyTax;
  }
  
  public Float getFixupCosts() {
    return fixupCosts;
  }
  
  public Float getFVPropertyTax(int year) {
    
    Integer compoundingPeriodDesired = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    final Float yearlyPropertyTax = getPropertyTax() * getFVRear(compoundingPeriodDesired); 
    dataController.setValueAsFloat(ValueEnum.YEARLY_PROPERTY_TAX, yearlyPropertyTax, year);  
  
    return yearlyPropertyTax;
  }
  
  public Float getFVRear(int compoundingPeriodDesired) {
    return (float) Math.pow(1 + monthlyRealEstateAppreciationRate, (float) compoundingPeriodDesired);
  }
  
  public Float getFVMir(int compoundingPeriodDesired) {
    return (float) Math.pow(1 + monthlyInflationRate, (float) compoundingPeriodDesired);
  }

}
