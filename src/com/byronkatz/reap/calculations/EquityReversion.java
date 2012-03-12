package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.ValueEnum;

public class EquityReversion {

  private Double sellingBrokerRate;
  private EstateValue estateValue; 
  private Double sellingExpenses;
  private RentalUnitOwnership rentalUnitOwnership;
  public static final Double TAX_ON_CAPITAL_GAINS = 0.15d;
  private Mortgage mortgage;
  
  private DataController dataController;
  
  public EquityReversion(DataController dataController, 
      EstateValue estateValue, RentalUnitOwnership rentalUnitOwnership, Mortgage mortgage) {
    
    this.mortgage = mortgage;
    this.dataController = dataController;
    sellingExpenses = dataController.getValueAsDouble(ValueEnum.GENERAL_SALE_EXPENSES);
    sellingBrokerRate = dataController.getValueAsDouble(ValueEnum.SELLING_BROKER_RATE);
    this.estateValue = estateValue;
    this.rentalUnitOwnership = rentalUnitOwnership;
  }
  
  private Double calculateBrokerCutOfSale(final int year) {

    final Double brokerCut = estateValue.getEstateValue(year) * sellingBrokerRate;
    dataController.setValueAsDouble(ValueEnum.BROKER_CUT_OF_SALE, brokerCut, year);

    return brokerCut;
  }
  
  private Double getFVSellingExpenses(final int year) {

    Double fvSellingExpenses = 0.0d;
    fvSellingExpenses = sellingExpenses * rentalUnitOwnership.getFVIr(year - 1);
    dataController.setValueAsDouble(ValueEnum.SELLING_EXPENSES, fvSellingExpenses, year);

    return fvSellingExpenses;
  }
  
  private Double calculateTaxesDueAtSale(final int year) {
    final Double taxesDueAtSale = ((estateValue.getEstateValue(year) - calculateBrokerCutOfSale(year))
        - estateValue.getOriginalEstateValue() + (rentalUnitOwnership.getYearlyDepreciation() * year))
        * TAX_ON_CAPITAL_GAINS;
    dataController.setValueAsDouble(ValueEnum.TAXES_DUE_AT_SALE, taxesDueAtSale, year);

    return taxesDueAtSale;
  }
  
  private Double calculateValueOfAter(int year) {
    
    final Double principalOutstandingAtSale = mortgage.getPrincipalOutstandingAtPoint(year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR);
    Double ater = 0.0d;
    
    ater = estateValue.getEstateValue(year) - calculateBrokerCutOfSale(year) - 
        getFVSellingExpenses(year) - principalOutstandingAtSale - calculateTaxesDueAtSale(year);
    dataController.setValueAsDouble(ValueEnum.ATER, ater, year);
    
    return ater;
  }
  
  public Double calculatePresentValueAter(int year) {
    

    final Double ater = calculateValueOfAter(year);

    rentalUnitOwnership.getModifiedInternalRateOfReturn().calculateMirr(year, null, ater);
    
    final Double adjustedAter = (ater / Math.pow(1 + rentalUnitOwnership.getMonthlyRequiredRateOfReturn(), year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR));
    dataController.setValueAsDouble(ValueEnum.ATER_PV, adjustedAter, year);

    return adjustedAter;
  }

}
