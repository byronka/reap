package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class EquityReversion {

  private Float sellingBrokerRate;
  private EstateValue estateValue; 
  private Float sellingExpenses;
  private RentalUnitOwnership rentalUnitOwnership;
  public static final Float TAX_ON_CAPITAL_GAINS = 0.15f;

  
  private DataController dataController;
  
  public EquityReversion(DataController dataController, 
      EstateValue estateValue, RentalUnitOwnership rentalUnitOwnership) {
    
    this.dataController = dataController;
    sellingExpenses = dataController.getValueAsFloat(ValueEnum.GENERAL_SALE_EXPENSES);
    sellingBrokerRate = dataController.getValueAsFloat(ValueEnum.SELLING_BROKER_RATE);
    this.estateValue = estateValue;
    this.rentalUnitOwnership = rentalUnitOwnership;
  }
  
  public Float calculateBrokerCutOfSale(final int year) {

    final Float brokerCut = estateValue.getEstateValue(year) * sellingBrokerRate;
    dataController.setValueAsFloat(ValueEnum.BROKER_CUT_OF_SALE, brokerCut, year);

    return brokerCut;
  }
  
  public Float getFVSellingExpenses(final int year) {

    Integer compoundingPeriodDesired = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    Float fvSellingExpenses = 0.0f;
    fvSellingExpenses = sellingExpenses * rentalUnitOwnership.getFVMir(compoundingPeriodDesired);
    dataController.setValueAsFloat(ValueEnum.SELLING_EXPENSES, fvSellingExpenses, year);

    return fvSellingExpenses;
  }
  
  public Float calculateTaxesDueAtSale(final int year) {
    final Float taxesDueAtSale = ((estateValue.getEstateValue(year) - calculateBrokerCutOfSale(year))
        - estateValue.getEstateValue(0) + (rentalUnitOwnership.getYearlyDepreciation() * year))
        * TAX_ON_CAPITAL_GAINS;
    dataController.setValueAsFloat(ValueEnum.TAXES_DUE_AT_SALE, taxesDueAtSale, year);

    return taxesDueAtSale;
  }
  
  public Float calculateValueOfAter(int year) {
    
    final Float principalOutstandingAtSale = rentalUnitOwnership.getMortgage().getPrincipalOutstandingAtPoint(year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR);
    Float ater = 0.0f;
    
    ater = estateValue.getEstateValue(year) - calculateBrokerCutOfSale(year) - 
        getFVSellingExpenses(year) - principalOutstandingAtSale - calculateTaxesDueAtSale(year);
    dataController.setValueAsFloat(ValueEnum.ATER, ater, year);
    
    return ater;
  }
  
  public Float calculateAter(int year) {
    

    final Float ater = calculateValueOfAter(year);

    rentalUnitOwnership.getModifiedInternalRateOfReturn().calculateMirr(year, null, ater);
    
    final Float adjustedAter = (float) (ater / Math.pow(1 + rentalUnitOwnership.getMonthlyRequiredRateOfReturn(), year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR));
    dataController.setValueAsFloat(ValueEnum.ATER_PV, adjustedAter, year);

    return adjustedAter;
  }

}
