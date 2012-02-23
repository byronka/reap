package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.ValueEnum;

public class EquityReversion {

  private Double sellingBrokerRate;
  private EstateValue estateValue; 
  private Double sellingExpenses;
  private RentalUnitOwnership rentalUnitOwnership;
  public static final Double TAX_ON_CAPITAL_GAINS = 0.15d;

  
  private DataController dataController;
  
  public EquityReversion(DataController dataController, 
      EstateValue estateValue, RentalUnitOwnership rentalUnitOwnership) {
    
    this.dataController = dataController;
    sellingExpenses = dataController.getValueAsDouble(ValueEnum.GENERAL_SALE_EXPENSES);
    sellingBrokerRate = dataController.getValueAsDouble(ValueEnum.SELLING_BROKER_RATE);
    this.estateValue = estateValue;
    this.rentalUnitOwnership = rentalUnitOwnership;
  }
  
  public Double calculateBrokerCutOfSale(final int year) {

    final Double brokerCut = estateValue.getEstateValue(year) * sellingBrokerRate;
    dataController.setValueAsDouble(ValueEnum.BROKER_CUT_OF_SALE, brokerCut, year);

    return brokerCut;
  }
  
  public Double getFVSellingExpenses(final int year) {

    Integer compoundingPeriodDesired = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    Double fvSellingExpenses = 0.0d;
    fvSellingExpenses = sellingExpenses * rentalUnitOwnership.getFVMir(compoundingPeriodDesired);
    dataController.setValueAsDouble(ValueEnum.SELLING_EXPENSES, fvSellingExpenses, year);

    return fvSellingExpenses;
  }
  
  public Double calculateTaxesDueAtSale(final int year) {
    final Double taxesDueAtSale = ((estateValue.getEstateValue(year) - calculateBrokerCutOfSale(year))
        - estateValue.getOriginalEstateValue() + (rentalUnitOwnership.getYearlyDepreciation() * year))
        * TAX_ON_CAPITAL_GAINS;
    dataController.setValueAsDouble(ValueEnum.TAXES_DUE_AT_SALE, taxesDueAtSale, year);

    return taxesDueAtSale;
  }
  
  public Double calculateValueOfAter(int year) {
    
    final Double principalOutstandingAtSale = rentalUnitOwnership.getMortgage().getPrincipalOutstandingAtPoint(year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR);
    Double ater = 0.0d;
    
    ater = estateValue.getEstateValue(year) - calculateBrokerCutOfSale(year) - 
        getFVSellingExpenses(year) - principalOutstandingAtSale - calculateTaxesDueAtSale(year);
    dataController.setValueAsDouble(ValueEnum.ATER, ater, year);
    
    return ater;
  }
  
  public Double calculateAter(int year) {
    

    final Double ater = calculateValueOfAter(year);

    rentalUnitOwnership.getModifiedInternalRateOfReturn().calculateMirr(year, null, ater);
    
    final Double adjustedAter = (ater / Math.pow(1 + rentalUnitOwnership.getMonthlyRequiredRateOfReturn(), year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR));
    dataController.setValueAsDouble(ValueEnum.ATER_PV, adjustedAter, year);

    return adjustedAter;
  }

}
