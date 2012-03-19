//package com.byronkatz.reap.calculations;
//
//import com.byronkatz.reap.general.DataManager;
//import com.byronkatz.reap.general.ValueEnum;
//
//public class Tax implements ValueSettable {
//
//  private Double buildingValue;
//  private Double marginalTaxRate;
//  private Double totalPurchaseValue;
//  private final Double DEPRECIATION_CONSTANT = 27.5d;
//  private final Double TAX_ON_CAPITAL_GAINS = 0.15d;
//
//  private DataManager dataManager;
//
//  private CalcValueGettable yearlyDepreciation;
//  private CalcValueGettable taxesDueAtSale;
//  private CalcValueGettable yearlyTaxOnIncome;
//
//  public Tax() {
//    buildingValue = 0d;
//    marginalTaxRate = 0d;
//    totalPurchaseValue = 0d;
//    yearlyDepreciation = new YearlyDepreciation();
//    taxesDueAtSale = new TaxesDueAtSale();
//    yearlyTaxOnIncome = new YearlyTaxOnIncome();
//    dataManager = null;
//  }
//
//  /**
//   * assigns pointers to the calculations to the hashmap
//   * @param dataManager
//   */
//  private void assignDataManager(DataManager dataManager) {
//    dataManager.addCalcValuePointers(
//        ValueEnum.YEARLY_DEPRECIATION, yearlyDepreciation);
//    dataManager.addCalcValuePointers(
//        ValueEnum.TAXES_DUE_AT_SALE, taxesDueAtSale);
//    dataManager.addCalcValuePointers(
//        ValueEnum.YEARLY_TAX_ON_INCOME, yearlyTaxOnIncome);
//
//  }
//
//  @Override
//  public void setValues(DataManager dataManager) {
//
//    this.dataManager = dataManager;
//    totalPurchaseValue = dataManager.getInputValue(ValueEnum.TOTAL_PURCHASE_VALUE);
//    marginalTaxRate = dataManager.getInputValue(ValueEnum.MARGINAL_TAX_RATE);
//    buildingValue = dataManager.getInputValue(
//        ValueEnum.BUILDING_VALUE);
//
//    assignDataManager(dataManager);
//  }
//
//  private class YearlyDepreciation implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0) {return 0d;}
//      return buildingValue / DEPRECIATION_CONSTANT;
//    }
//
//  }
//
//  private class YearlyTaxOnIncome implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0) {return 0d;}
//
//      return 
//          dataManager.getCalcValue(ValueEnum.TAXABLE_INCOME, compoundingPeriod) * 
//          marginalTaxRate;
//    }
//
//  }
//
//  private class TaxesDueAtSale implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0) {return 0d;}
//      if (taxesDueFunction(compoundingPeriod) < 0) {return 0d;}
//
//      else {
//        return taxesDueFunction(compoundingPeriod);
//      }
//    }
//    
//    private Double taxesDueFunction(Integer compoundingPeriod) {
//      return (dataManager.getCalcValue(ValueEnum.PROJECTED_HOME_VALUE, compoundingPeriod) - 
//          dataManager.getCalcValue(ValueEnum.BROKER_CUT_OF_SALE, compoundingPeriod) -
//          totalPurchaseValue + 
//          (dataManager.getCalcValue(ValueEnum.YEARLY_DEPRECIATION, compoundingPeriod) * 
//              ((compoundingPeriod/MONTHS_IN_YEAR)+1))) *
//          TAX_ON_CAPITAL_GAINS;
//    }
//
//  }
//}
