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

  
  private static final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  
  public EquityReversion(EstateValue estateValue, RentalUnitOwnership rentalUnitOwnership) {
    
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
  
  public Float calculateTaxesDueAtSale(final Float accumulatingDepreciation, final int year) {
    final Float taxesDueAtSale = (estateValue.getEstateValue(year) - estateValue.getEstateValue(0) + accumulatingDepreciation)
        * TAX_ON_CAPITAL_GAINS;
    dataController.setValueAsFloat(ValueEnum.TAXES_DUE_AT_SALE, taxesDueAtSale, year);

    return taxesDueAtSale;
  }

}
