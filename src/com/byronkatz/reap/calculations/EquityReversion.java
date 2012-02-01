package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class EquityReversion {

  private Float sellingBrokerRate;
  private EstateValue estateValue; 
  private Float sellingExpenses;
  public static final Float TAX_ON_CAPITAL_GAINS = 0.15f;

  
  private static final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  
  public EquityReversion(EstateValue estateValue) {
    
    sellingExpenses = dataController.getValueAsFloat(ValueEnum.GENERAL_SALE_EXPENSES);
    sellingBrokerRate = dataController.getValueAsFloat(ValueEnum.SELLING_BROKER_RATE);
    this.estateValue = estateValue;
  }
  
  public Float calculateBrokerCutOfSale(final int year) {

    final Float brokerCut = estateValue.getEstateValue(year) * sellingBrokerRate;
    dataController.setValueAsFloat(ValueEnum.BROKER_CUT_OF_SALE, brokerCut, year);

    return brokerCut;
  }
  
  public Float calculateSellingExpensesFutureValue(final int year, final Float rate) {

    final Float sellingExpensesFutureValue = 
        GeneralCalculations.futureValue((float) year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR, rate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR, sellingExpenses);
    dataController.setValueAsFloat(ValueEnum.SELLING_EXPENSES, sellingExpensesFutureValue, year);

    return sellingExpensesFutureValue;
  }
  
  public Float calculateTaxesDueAtSale(final Float accumulatingDepreciation, final int year) {
    final Float taxesDueAtSale = (estateValue.getEstateValue(year) - estateValue.getEstateValue(0) + accumulatingDepreciation)
        * TAX_ON_CAPITAL_GAINS;
    dataController.setValueAsFloat(ValueEnum.TAXES_DUE_AT_SALE, taxesDueAtSale, year);

    return taxesDueAtSale;
  }

}
