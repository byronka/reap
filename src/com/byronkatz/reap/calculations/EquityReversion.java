//package com.byronkatz.reap.calculations;
//
//import com.byronkatz.reap.general.DataManager;
//import com.byronkatz.reap.general.ValueEnum;
//
//public class EquityReversion implements ValueSettable {
//
//  private Double brokerRate;
//  private Double initialSellingExpenses;
//  private Double requiredRateOfReturn;
//  private Double inflationRate;
//
//  
//  private DataManager dataManager;
//
//  private CalcValueGettable sellingExpensesFValue;
//  private CalcValueGettable brokerCutOfSale;
//  private CalcValueGettable afterTaxEquityReversion;
//  private CalcValueGettable aterPv;
//
//  public EquityReversion() {
//    
//    brokerRate = 0d;
//    initialSellingExpenses = 0d;
//    requiredRateOfReturn = 0d;
//    
//    sellingExpensesFValue = new SellingExpensesFValue();
//    brokerCutOfSale = new BrokerCutOfSale();
//    afterTaxEquityReversion = new AfterTaxEquityReversion();
//    aterPv = new AterPV();
//    dataManager = null;
//  }
//
//  /**
//   * assigns pointers to the calculations to the hashmap
//   * @param dataManager
//   */
//  private void assignDataManager(DataManager dataManager) {
//    dataManager.addCalcValuePointers(
//        ValueEnum.BROKER_CUT_OF_SALE, brokerCutOfSale);
//    dataManager.addCalcValuePointers(
//        ValueEnum.ATER, afterTaxEquityReversion);
//    dataManager.addCalcValuePointers(
//        ValueEnum.SELLING_EXPENSES, sellingExpensesFValue);    
//    dataManager.addCalcValuePointers(
//            ValueEnum.ATER_PV, aterPv);
//
//  }
//
//  @Override
//  public void setValues(DataManager dataManager) {
//
//    this.dataManager = dataManager;
//    
//    inflationRate = dataManager.getInputValue(ValueEnum.INFLATION_RATE);
//    brokerRate = dataManager.getInputValue(
//        ValueEnum.SELLING_BROKER_RATE);
//    initialSellingExpenses = dataManager.getInputValue(
//        ValueEnum.GENERAL_SALE_EXPENSES);
//    requiredRateOfReturn = dataManager.getInputValue(ValueEnum.REQUIRED_RATE_OF_RETURN);
//
//    assignDataManager(dataManager);
//  }
//
//  private class BrokerCutOfSale implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0) {return 0d;}
//
//      return (brokerRate * 
//          dataManager.getCalcValue(ValueEnum.PROJECTED_HOME_VALUE, compoundingPeriod));
//    }
//
//  }
//
//  private class AfterTaxEquityReversion implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0) {return 0d;}
//
//      return 
//          dataManager.getCalcValue(ValueEnum.PROJECTED_HOME_VALUE, compoundingPeriod) -
//          dataManager.getCalcValue(ValueEnum.BROKER_CUT_OF_SALE, compoundingPeriod) -
//          dataManager.getCalcValue(ValueEnum.SELLING_EXPENSES, compoundingPeriod) -
//          dataManager.getCalcValue(ValueEnum.CURRENT_AMOUNT_OUTSTANDING, compoundingPeriod) -
//          dataManager.getCalcValue(ValueEnum.TAXES_DUE_AT_SALE, compoundingPeriod);
//    }
//
//  }
//  
//  private class AterPV implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      return (afterTaxEquityReversion.getValue(compoundingPeriod) / 
//          Math.pow((1 + requiredRateOfReturn/MONTHS_IN_YEAR), compoundingPeriod));
//    }
//    
//  }
//
//  private class SellingExpensesFValue implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0) {return 0d;}
//
//      return initialSellingExpenses * Math.pow(1 + inflationRate , compoundingPeriod / MONTHS_IN_YEAR);
//    }
//
//  }
//}
