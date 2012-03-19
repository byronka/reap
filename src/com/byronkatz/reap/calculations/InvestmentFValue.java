//package com.byronkatz.reap.calculations;
//
//import com.byronkatz.reap.general.DataManager;
//import com.byronkatz.reap.general.ValueEnum;
//
//public class InvestmentFValue implements ValueSettable, CalcValueGettable {
//
//  private Double totalPurchaseValue;
//  private Double realEstateAppreciationRate;
//  private Double fixupCosts;
//
//  public InvestmentFValue() {
//    totalPurchaseValue = 0d;
//    realEstateAppreciationRate = 0d;
//    fixupCosts = 0d;
//  }
//
//  /**
//   * assigns pointers to the calculations to the hashmap
//   * @param dataManager
//   */
//  private void assignDataManager(DataManager dataManager) {
//    dataManager.addCalcValuePointers(
//        ValueEnum.PROJECTED_HOME_VALUE, this);
//  }
//
//  @Override
//  public void setValues(DataManager dataManager) {
//
//    assignDataManager(dataManager);
//    realEstateAppreciationRate = dataManager.getInputValue(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);
//    totalPurchaseValue = dataManager.getInputValue(ValueEnum.TOTAL_PURCHASE_VALUE);
//    fixupCosts = dataManager.getInputValue(ValueEnum.FIX_UP_COSTS);
//
//  }
//
//  @Override
//  public Double getValue(Integer compoundingPeriod) {
//    if (compoundingPeriod < 0) {return 0d;}
//    if (compoundingPeriod == 0) {return totalPurchaseValue;}
//    else {
//    return totalPurchaseValue * Math.pow(1 + realEstateAppreciationRate / MONTHS_IN_YEAR, compoundingPeriod);
//    }
//  }
//
//
//
//
//}
