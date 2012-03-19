//package com.byronkatz.reap.calculations;
//
//import com.byronkatz.reap.general.DataManager;
//import com.byronkatz.reap.general.ValueEnum;
//
//public class OperatingExpenses implements ValueSettable {
//
//  private Double propertyTax;
//  private Double insurance;
//  private Double generalExpenses;
//  private Double inflationRate;
//  private Double realEstateAppreciationRate;
//  private Double municipalExpenses;
//
//  private CalcValueGettable propertyTaxFValue;
//  private CalcValueGettable insuranceFValue;
//  private CalcValueGettable generalExpensesFValue;
//  private CalcValueGettable operatingExpensesFValue;
//  private CalcValueGettable municipalExpensesFValue;
//
//  public OperatingExpenses() {
//    propertyTax = 0d;
//    insurance = 0d;      
//    generalExpenses = 0d;
//    inflationRate = 0d;
//    realEstateAppreciationRate = 0d;
//    municipalExpenses = 0d;
//    
//    propertyTaxFValue = new PropertyTaxFValue();
//    insuranceFValue = new InsuranceFValue();
//    generalExpensesFValue = new GeneralExpensesFValue();
//    operatingExpensesFValue = new OperatingExpensesFValue();
//    municipalExpensesFValue = new MunicipalExpensesFValue();
//  }
//  
//  /**
//   * assigns pointers to the calculations to the hashmap
//   * @param dataManager
//   */
//  private void assignDataManager(DataManager dataManager) {
//    dataManager.addCalcValuePointers(
//        ValueEnum.YEARLY_PROPERTY_TAX, propertyTaxFValue);
//    dataManager.addCalcValuePointers(
//        ValueEnum.YEARLY_HOME_INSURANCE, insuranceFValue);
//    dataManager.addCalcValuePointers(
//        ValueEnum.YEARLY_GENERAL_EXPENSES, generalExpensesFValue);
//    dataManager.addCalcValuePointers(
//        ValueEnum.YEARLY_OPERATING_EXPENSES, operatingExpensesFValue);
//    dataManager.addCalcValuePointers(
//        ValueEnum.YEARLY_MUNICIPAL_FEES, municipalExpensesFValue);
//  }
//
//  @Override
//  public void setValues(DataManager dataManager) {
//
//    assignDataManager(dataManager);
//    
//    propertyTax = dataManager.getInputValue(ValueEnum.PROPERTY_TAX);
//    insurance = dataManager.getInputValue(ValueEnum.INITIAL_HOME_INSURANCE);
//    generalExpenses = dataManager.getInputValue(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES);
//    inflationRate = dataManager.getInputValue(ValueEnum.INFLATION_RATE);
//    realEstateAppreciationRate = dataManager.getInputValue(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);
//    municipalExpenses = dataManager.getInputValue(ValueEnum.LOCAL_MUNICIPAL_FEES);
//  }
//
//  /**
//   * compounding yearly rather than monthly.
//   * @author byron
//   *
//   */
//  private class MunicipalExpensesFValue implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0) {return 0d;}
//      return (municipalExpenses * Math.pow(1 + inflationRate, compoundingPeriod / MONTHS_IN_YEAR));
//    }
//    
//  }
//  
//  
//  /**
//   * Increments the property tax at the rate of REAR once a year.
//   * We assert that this value should be non-null and greater than 0.  Less than zero
//   * does not make sense.
//   * @author byron
//   *
//   */
//  private class PropertyTaxFValue implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0) {return 0d;}
//      return (propertyTax * Math.pow(1 + realEstateAppreciationRate, compoundingPeriod / MONTHS_IN_YEAR));
//    }
//
//  }
//
//  /**
//   * Increments the insurance value at the rate of inflation once a year.
//   * We assert that this value should be non-null and greater than 0.  Less than zero
//   * does not make sense.
//   * @author byron
//   *
//   */
//  private class InsuranceFValue implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0) {return 0d;}
//      return (insurance * Math.pow(1 + inflationRate, compoundingPeriod / MONTHS_IN_YEAR));
//    }
//
//  }
//  
//  /**
//   * Increments the general expenses value at the rate of inflation once a year.
//   * We assert that this value should be non-null and greater than 0.  Less than zero
//   * does not make sense.
//   * @author byron
//   *
//   */
//  private class GeneralExpensesFValue implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0) {return 0d;}
//      return (generalExpenses * Math.pow(1 + inflationRate, compoundingPeriod / MONTHS_IN_YEAR));
//    }
//
//  }
//  
//  /**
//   * Increments the general expenses value at the rate of inflation once a year.
//   * We assert that this value should be non-null and greater than 0.  Less than zero
//   * does not make sense.
//   * @author byron
//   * 
//   *
//   */
//  private class OperatingExpensesFValue implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0) {return 0d;}
//      return ((generalExpenses + insurance + municipalExpenses) * 
//          Math.pow(1 + inflationRate, compoundingPeriod / 12)) +
//          propertyTax * Math.pow(1+realEstateAppreciationRate, compoundingPeriod / MONTHS_IN_YEAR);
//    }
//
//  }
//
//
//
//}
