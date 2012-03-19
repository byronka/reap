//package com.byronkatz.reap.calculations;
//
//import com.byronkatz.reap.general.DataManager;
//import com.byronkatz.reap.general.ValueEnum;
//
//public class Income implements ValueSettable {
//
//  private Double initialRent;
//  private Double yearlyRealEstateAppreciationRate;
//  private Double vacancyAndCreditLossRate;
//  
//  private DataManager dataManager;
//  
//  private CalcValueGettable monthlyRentFValue;
//  private CalcValueGettable yearlyGrossIncomeValue;
//  private CalcValueGettable yearlyNetIncomeValue;
//  private CalcValueGettable netOperatingIncome;
//  private CalcValueGettable taxableIncome;
//  
//  public Income() {
//    
//    monthlyRentFValue = new MonthlyRentFValue();
//    yearlyGrossIncomeValue = new YearlyGrossIncomeValue();
//    yearlyNetIncomeValue = new YearlyNetIncomeValue();
//    netOperatingIncome = new NetOperatingIncome();
//    taxableIncome = new TaxableIncome();
//    dataManager = null;
//    initialRent = 0d;
//    yearlyRealEstateAppreciationRate = 0d;
//    vacancyAndCreditLossRate = 0d;
//  }
//  
//  @Override
//  public void setValues(DataManager dataManager) {
//    
//    this.dataManager = dataManager;
//    initialRent = dataManager.getInputValue(
//        ValueEnum.ESTIMATED_RENT_PAYMENTS);
//    yearlyRealEstateAppreciationRate = dataManager.getInputValue(
//        ValueEnum.REAL_ESTATE_APPRECIATION_RATE);
//    vacancyAndCreditLossRate = dataManager.getInputValue(
//        ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE);
//    assignDataManager(dataManager);
//  }
//  
//  /**
//   * assigns pointers to the calculations to the hashmap
//   * @param dataManager
//   */
//  private void assignDataManager(DataManager dataManager) {
//    dataManager.addCalcValuePointers(
//        ValueEnum.MONTHLY_RENT_FV, monthlyRentFValue);
//    dataManager.addCalcValuePointers(
//        ValueEnum.GROSS_YEARLY_INCOME, yearlyGrossIncomeValue);
//    dataManager.addCalcValuePointers(
//        ValueEnum.YEARLY_INCOME, yearlyNetIncomeValue);
//    dataManager.addCalcValuePointers(
//        ValueEnum.YEARLY_NET_OPERATING_INCOME, netOperatingIncome);
//    dataManager.addCalcValuePointers(
//        ValueEnum.TAXABLE_INCOME, taxableIncome);
//  }
//  
//  /**
//   * Future value of the monthly rent.  Gets incremented by REAR once each 12 months.
//   * @author byron
//   *
//   */
//  private class MonthlyRentFValue implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {  
//      if (compoundingPeriod < 0 ) {return 0d;}
//      return (initialRent * Math.pow(1 + yearlyRealEstateAppreciationRate, compoundingPeriod / MONTHS_IN_YEAR));
//    }
//    
//  }
//
//  /**
//   * The yearly gross income.  The monthly rent times 12.
//   * @author byron
//   *
//   */
//  private class YearlyGrossIncomeValue implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0 ) {return 0d;}
//      return 12 * (initialRent * Math.pow(1 + yearlyRealEstateAppreciationRate, compoundingPeriod / MONTHS_IN_YEAR));
//    }
//  
//  }
//  
//  /**
//   * returns the yearly net income, which is the yearly gross income times (1 minus vacancy)
//   * @author byron
//   *
//   */
//  private class YearlyNetIncomeValue implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0 ) {return 0d;}
//      return MONTHS_IN_YEAR * (1-vacancyAndCreditLossRate) * 
//          (initialRent * Math.pow(1 + yearlyRealEstateAppreciationRate, compoundingPeriod / MONTHS_IN_YEAR));
//    }
//    
//  }
//  
//  private class NetOperatingIncome implements CalcValueGettable {
//    
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0) {return 0d;}
//      return (yearlyNetIncomeValue.getValue(compoundingPeriod) - 
//          dataManager.getCalcValue(ValueEnum.YEARLY_OPERATING_EXPENSES, compoundingPeriod));
//    }
//    
//  }
//  
//  private class TaxableIncome implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0) {return 0d;}
//      if (calcValue(compoundingPeriod) < 0) {
//        return 0d;
//      } else {
//        return calcValue(compoundingPeriod);
//      }
//    }
//    
//    private Double calcValue(Integer compoundingPeriod) {
//      return 
//          netOperatingIncome.getValue(compoundingPeriod) - 
//          dataManager.getCalcValue(ValueEnum.YEARLY_INTEREST_PAID, compoundingPeriod) -
//          dataManager.getCalcValue(ValueEnum.YEARLY_DEPRECIATION, compoundingPeriod);
//    }
//  }
//
//
//
//}
