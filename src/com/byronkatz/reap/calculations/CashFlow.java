//package com.byronkatz.reap.calculations;
//
//import com.byronkatz.reap.general.DataManager;
//import com.byronkatz.reap.general.ValueEnum;
//
//public class CashFlow implements ValueSettable {
//
//  private CalcValueGettable afterTaxCashFlow;
//  private CalcValueGettable afterTaxCashFlowNPV;
//  private CalcValueGettable afterTaxCashFlowAccumulator;
//  private CalcValueGettable beforeTaxCashFlow;
//  private CalcValueGettable yearlyOutlay;
//  private CalcValueGettable capitalizationRateOnPurchaseValue;
//  private CalcValueGettable capitalizationRateOnProjectedValue;
//  private CalcValueGettable firstDayCosts;
//  private CalcValueGettable npv;
//
//  private Double totalYearsToCalculate;
//  private Double requiredRateOfReturn;
//  private Double atcfNpvAccumulator;
//  private Double atcfAccumulator;
//
//  //mirr stuff starts
//  private Double yearlyLoanInterestRate;
//  private Double mirr;
//  Double futureValuePositiveCashFlowsAccumulator;
//  Double presentValueNegativeCashFlowsAccumulator;
//  private CalcValueGettable mirrObject;
//  private Double atcfTempValue;
//  private Double aterTempValue;
//
//  //mirr stuff ends
//
//  private DataManager dataManager;
//
//
//  public CashFlow() {
//
//    totalYearsToCalculate = 0d;
//    requiredRateOfReturn = 0d;
//    atcfNpvAccumulator = 0d;
//    atcfAccumulator = 0d;
//    atcfTempValue = 0d;
//    aterTempValue = 0d;
//
//    //mirr stuff starts
//    yearlyLoanInterestRate = 0d;
//    mirr = 0.0d;
//    futureValuePositiveCashFlowsAccumulator = 0.0d;
//    presentValueNegativeCashFlowsAccumulator = 0.0d;
//
//    mirrObject = new MIRR();
//
//    //mirr stuff ends
//
//    afterTaxCashFlow = new AfterTaxCashFlow();
//    afterTaxCashFlowNPV = new AtcfNpv();
//    afterTaxCashFlowAccumulator = new AfterTaxCashFlowAccumulator();
//    beforeTaxCashFlow = new BeforeTaxCashFlow();
//    yearlyOutlay = new YearlyOutlay();
//    capitalizationRateOnPurchaseValue = new CapitalizationRateOnPurchaseValue();
//    capitalizationRateOnProjectedValue = new CapitalizationRateOnProjectedValue();
//    firstDayCosts = new FirstDayCosts();
//    npv = new NPV();
//    dataManager = null;
//
//  }
//
//  @Override
//  public void setValues(DataManager dataManager) {
//    this.dataManager = dataManager;
//
//    totalYearsToCalculate = dataManager.getInputValue(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS) +
//        dataManager.getInputValue(ValueEnum.EXTRA_YEARS);
//    requiredRateOfReturn = dataManager.getInputValue(ValueEnum.REQUIRED_RATE_OF_RETURN);
//    yearlyLoanInterestRate = dataManager.getInputValue(ValueEnum.YEARLY_INTEREST_RATE);
//    assignDataManager(dataManager);
//
//  }
//
//  /**
//   * assigns pointers to the calculations to the hashmap
//   * @param dataManager
//   */
//  private void assignDataManager(DataManager dataManager) {
//    dataManager.addCalcValuePointers(
//        ValueEnum.ATCF, afterTaxCashFlow);
//    dataManager.addCalcValuePointers(
//        ValueEnum.YEARLY_BEFORE_TAX_CASH_FLOW, beforeTaxCashFlow);    
//    dataManager.addCalcValuePointers(
//        ValueEnum.YEARLY_OUTLAY, yearlyOutlay);
//    dataManager.addCalcValuePointers(
//        ValueEnum.CAP_RATE_ON_PROJECTED_VALUE, capitalizationRateOnProjectedValue);
//    dataManager.addCalcValuePointers(
//        ValueEnum.CAP_RATE_ON_PURCHASE_VALUE, capitalizationRateOnPurchaseValue);
//    dataManager.addCalcValuePointers(
//        ValueEnum.FIRST_DAY_COSTS, firstDayCosts);
//    dataManager.addCalcValuePointers(
//        ValueEnum.NPV, npv);
//    dataManager.addCalcValuePointers(
//        ValueEnum.ATCF_ACCUMULATOR, afterTaxCashFlowAccumulator);
//    dataManager.addCalcValuePointers(
//        ValueEnum.ATCF_NPV, afterTaxCashFlowNPV);
//    dataManager.addCalcValuePointers(
//        ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN, mirrObject);
//  }
//
//
//  private class FirstDayCosts implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//
//      return 
//          dataManager.getInputValue(ValueEnum.DOWN_PAYMENT) +
//          dataManager.getInputValue(ValueEnum.CLOSING_COSTS) +
//          dataManager.getInputValue(ValueEnum.FIX_UP_COSTS);
//    }
//  }  
//
//  private class NPV implements CalcValueGettable {
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0) {return 0d;}
//      return 
//          ((- firstDayCosts.getValue(compoundingPeriod)) + 
//              afterTaxCashFlowNPV.getValue(compoundingPeriod) +
//              dataManager.getCalcValue(ValueEnum.ATER_PV, compoundingPeriod));
//
//    } 
//  }
//
//  private class MIRR implements CalcValueGettable {
//
//    @Override
//    public Double getValue (Integer compoundingPeriod) {
//      if (compoundingPeriod < 0) {return 0d;}
//
//      presentValueNegativeCashFlowsAccumulator = 0d;
//      futureValuePositiveCashFlowsAccumulator = 0d;
//      atcfTempValue = 0d;
//      aterTempValue = 0d;
//
//      //cash flows for mirr:
//
//      //step 1, first day costs:
//      presentValueNegativeCashFlowsAccumulator +=
//          -firstDayCosts.getValue(compoundingPeriod);
//
//      //step 2: after tax cash flows - different action depending on sign
//      for (int year = 0; year < (compoundingPeriod / MONTHS_IN_YEAR); year++) {
//
//
//        atcfTempValue = afterTaxCashFlow.getValue(year*MONTHS_IN_YEAR);
//        if (atcfTempValue < 0d) {
//          presentValueNegativeCashFlowsAccumulator +=
//              atcfTempValue /  Math.pow(1 + yearlyLoanInterestRate, year);
//        } else {
//          futureValuePositiveCashFlowsAccumulator +=
//              atcfTempValue * 
//              Math.pow(1 + requiredRateOfReturn, totalYearsToCalculate - year);
//        }
//
//      }
//
//      //step 3: ater
//      aterTempValue = dataManager.getCalcValue(ValueEnum.ATER, compoundingPeriod);
//      if (aterTempValue < 0.0f) {
//        presentValueNegativeCashFlowsAccumulator += 
//            aterTempValue / Math.pow(1 + yearlyLoanInterestRate, compoundingPeriod/MONTHS_IN_YEAR);
//      } else if (aterTempValue > 0.0f) {
//        futureValuePositiveCashFlowsAccumulator += aterTempValue;
//      }
//
//
//      //step 4: division
//      //we don't want divide by zero errors.
//      if (! (presentValueNegativeCashFlowsAccumulator == 0f)) {
//        mirr = Math.pow(
//            futureValuePositiveCashFlowsAccumulator / 
//            - presentValueNegativeCashFlowsAccumulator, 
//            (1.0f/compoundingPeriod)) - 1;
//      } else {
//        mirr = 0d;
//      }
//
//        return mirr;
//      }
//    }
//
//    private class AtcfNpv implements CalcValueGettable {
//
//      @Override
//      public Double getValue (Integer compoundingPeriod) {
//        if (compoundingPeriod < 0) {return 0d;}
//        atcfNpvAccumulator = 0d;
//        for (int i = 0; i < (compoundingPeriod / MONTHS_IN_YEAR); i++) {
//          atcfNpvAccumulator += getAtcfNpv(i);
//        }
//        return atcfNpvAccumulator;
//      }
//
//      private Double getAtcfNpv(Integer compoundingPeriod) {
//        return (afterTaxCashFlow.getValue(compoundingPeriod / MONTHS_IN_YEAR) / 
//            Math.pow((1 + requiredRateOfReturn), compoundingPeriod / MONTHS_IN_YEAR));
//      }
//
//    }
//
//    private class AfterTaxCashFlowAccumulator implements CalcValueGettable {
//
//      @Override
//      public Double getValue(Integer compoundingPeriod) {
//        if (compoundingPeriod < 0) {return 0d;}
//        atcfAccumulator = 0d;
//        for (int i = 0; i < (compoundingPeriod / MONTHS_IN_YEAR); i++) {
//          atcfAccumulator += afterTaxCashFlow.getValue(i);
//        }
//        return atcfAccumulator;
//      }
//
//    }
//
//    private class AfterTaxCashFlow implements CalcValueGettable {
//
//      @Override
//      public Double getValue(Integer compoundingPeriod) {
//        if (compoundingPeriod < 0) {return 0d;}
//
//        return beforeTaxCashFlow.getValue(compoundingPeriod) - 
//            dataManager.getCalcValue(ValueEnum.YEARLY_TAX_ON_INCOME, compoundingPeriod);
//      }
//
//    }
//
//    private class BeforeTaxCashFlow implements CalcValueGettable {
//
//      @Override
//      public Double getValue(Integer compoundingPeriod) {
//        if (compoundingPeriod < 0) {return 0d;}
//
//        return dataManager.getCalcValue(ValueEnum.YEARLY_NET_OPERATING_INCOME, compoundingPeriod) -
//            MONTHS_IN_YEAR * dataManager.getCalcValue(ValueEnum.MONTHLY_MORTGAGE_PAYMENT, compoundingPeriod);
//      }
//    }
//
//    private class CapitalizationRateOnPurchaseValue implements CalcValueGettable {
//
//      @Override
//      public Double getValue(Integer compoundingPeriod) {
//        if (compoundingPeriod < 0) {return 0d;}
//
//        return dataManager.getCalcValue(ValueEnum.YEARLY_NET_OPERATING_INCOME, compoundingPeriod) /
//            dataManager.getInputValue(ValueEnum.TOTAL_PURCHASE_VALUE);
//      }
//    }
//
//    private class CapitalizationRateOnProjectedValue implements CalcValueGettable {
//
//      @Override
//      public Double getValue(Integer compoundingPeriod) {
//        if (compoundingPeriod < 0) {return 0d;}
//
//        return dataManager.getCalcValue(ValueEnum.YEARLY_NET_OPERATING_INCOME, compoundingPeriod) /
//            dataManager.getCalcValue(ValueEnum.PROJECTED_HOME_VALUE, compoundingPeriod);
//      }
//    }
//
//    private class YearlyOutlay implements CalcValueGettable {
//
//      @Override
//      public Double getValue(Integer compoundingPeriod) {
//        if (compoundingPeriod < 0) {return 0d;}
//        else {
//          return dataManager.getCalcValue(ValueEnum.YEARLY_OPERATING_EXPENSES, compoundingPeriod) +
//              MONTHS_IN_YEAR * dataManager.getCalcValue(ValueEnum.MONTHLY_MORTGAGE_PAYMENT, compoundingPeriod);
//        }
//      }
//    }
//
//  }
//
