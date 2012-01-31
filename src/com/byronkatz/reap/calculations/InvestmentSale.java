package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class InvestmentSale {

  private EstateValue estateValue;
  private Float sellingExpenses;
  private Float brokerCut;
  private GeneralCalculations gc;
  private Float yearlyDepreciation;
  private AmortizationSchedule as;
  
  public static final Float TAX_ON_CAPITAL_GAINS = 0.15f;
  public static final Float RESIDENTIAL_DEPRECIATION_YEARS = 27.5f;
  private static final int YEAR_FOR_ORIGINAL_INVESTMENT_VALUE = 0;
  
  private static final DataController dc = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  
  public InvestmentSale(EstateValue estateValue, GeneralCalculations gc,
      AmortizationSchedule as) {
    this.estateValue = estateValue;
    sellingExpenses = dc.getValueAsFloat(ValueEnum.SELLING_EXPENSES);
    this.gc = gc;
    this.as = as;
    brokerCut = dc.getValueAsFloat(ValueEnum.BROKER_CUT_OF_SALE);
    yearlyDepreciation = estateValue.getEstateValue(YEAR_FOR_ORIGINAL_INVESTMENT_VALUE) / RESIDENTIAL_DEPRECIATION_YEARS;
  }
  
  private Float calculateFutureValueSellingExpenses(int year) {
    return sellingExpenses * gc.getInflationFvAdjuster(year);
  }
  
  private Float calculateCurrentDepreciation(int year) {
    Float yearlyAccumulatedDepreciation = 0.0f;
    yearlyAccumulatedDepreciation = yearlyDepreciation * year;
    dc.setValueAsFloat(ValueEnum.YEARLY_ACCUMULATED_DEPRECIATION, yearlyAccumulatedDepreciation, year);
    return yearlyAccumulatedDepreciation;
  }
  
  private Float calculateTaxesDueAtSale(int year) {
    
    Float taxesDueAtSale = 0.0f;
    taxesDueAtSale = (estateValue.getEstateValue(year) - 
        estateValue.getEstateValue(YEAR_FOR_ORIGINAL_INVESTMENT_VALUE) + 
        calculateCurrentDepreciation(year)) * TAX_ON_CAPITAL_GAINS;
    dc.setValueAsFloat(ValueEnum.TAXES_DUE_AT_SALE, taxesDueAtSale, year);
    return taxesDueAtSale;
  }
  
  public Float calculateValueOfAter(int year) {
    
    Float ater = 0.0f;
    
    ater = estateValue.getEstateValue(year) - brokerCut - 
        calculateFutureValueSellingExpenses(year) - as.getPrincipalOutstandingAtPoint(year) - calculateTaxesDueAtSale(year);
    dc.setValueAsFloat(ValueEnum.ATER, ater, year);
    
    return ater;
  }
  
}
