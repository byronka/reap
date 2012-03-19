//package com.byronkatz.reap.calculations;
//
//import com.byronkatz.reap.general.DataManager;
//import com.byronkatz.reap.general.ValueEnum;
//
//
//public class AmortizationTable implements ValueSettable {
//
//  private Integer nocp;
//  private Double loanAmount;
//  private Double totalPurchaseValue;
//  private Double downPayment;
//  private Double loanInterestRateMonthly;
//  private Double interestAccumulator;
//  private Double interestPayment;
//  private Double principalPayment;
//  private Double amountOwed;
//  private Double mpValue;
//  private Double pmiMonthly;
//  private Double pmiEndsValue;
//  private Double pmiAccumulator;
//  public static final Double PMI_BOUNDARY_PERCENTAGE = 0.80D;
//  
//  private DataManager dataManager;
//
//  private CalcValueGettable interestPaymentValue;
//  private CalcValueGettable interestPaidAccumulatedValue;
//  private CalcValueGettable amountOwedValue;
//  private CalcValueGettable loanAmountValue;
//  private CalcValueGettable yearlyInterestPaid;
//  private CalcValueGettable yearlyPrincipalPaid;
//  private CalcValueGettable mortgagePayment;
//  private CalcValueGettable yearlyMortgagePayment;
//  private CalcValueGettable privateMortgageInsurance;
//  private CalcValueGettable privateMortgageInsuranceAccum;
//
//  public AmortizationTable() {
//    interestPaymentValue = new InterestPaymentValue();
//    interestPaidAccumulatedValue = new InterestPaidAccumulatedValue();
//    
//    amountOwedValue = new AmountOwedValue();
//    loanAmountValue = new LoanAmountValue();
//    yearlyPrincipalPaid = new YearlyPrincipalPaid();
//    yearlyInterestPaid = new YearlyInterestPaid();
//    mortgagePayment = new MortgagePayment();
//    yearlyMortgagePayment = new YearlyMortgagePayment();
//    privateMortgageInsurance = new PrivateMortgageInsurance();
//    privateMortgageInsuranceAccum = new PrivateMortgageInsuranceAccum();
//    
//    dataManager = null;
//
//    interestAccumulator = 0d;
//    interestPayment = 0d;
//    interestAccumulator = 0d;
//    principalPayment = 0d;
//    amountOwed = 0d;
//    mpValue = 0d;
//    pmiMonthly = 0d;
//    pmiEndsValue = 0d;
//    pmiAccumulator = 0d;
//
//    //following are things which need to be input later
//    nocp = 0;
//    loanInterestRateMonthly = 0d;
//    totalPurchaseValue = 0d;
//
//  }
//
//
//  @Override
//  public void setValues( DataManager dataManager) {
//    this.dataManager = dataManager;
//    assignDataManager(dataManager);
//    loadCurrentUserInputValues();
//
//  }
//  
//  private void loadCurrentUserInputValues() {
//
//    nocp = dataManager.getInputValue(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS).intValue();
//    totalPurchaseValue = dataManager.getInputValue(ValueEnum.TOTAL_PURCHASE_VALUE);
//    downPayment = dataManager.getInputValue(ValueEnum.DOWN_PAYMENT);
//    loanInterestRateMonthly = dataManager.getInputValue(ValueEnum.YEARLY_INTEREST_RATE) / 12;
//    pmiMonthly = dataManager.getInputValue(ValueEnum.PRIVATE_MORTGAGE_INSURANCE);
//    
//    if (totalPurchaseValue > downPayment) {
//      loanAmount = totalPurchaseValue - downPayment;
//    } else {
//      loanAmount = 0d;
//    }
//    
//    pmiEndsValue = PMI_BOUNDARY_PERCENTAGE * totalPurchaseValue;
//  
//
//  }
//
//
//  private void createAmortizationTable(int nocp) {
//
//    interestPayment = 0d;
//    amountOwed = loanAmount;
//    interestAccumulator = 0d;
//    principalPayment = 0d;
//    pmiAccumulator = 0d;
//    //pick an arbitrary month for the mortgage payment - in this case, 1.
//    mpValue = dataManager.getCalcValue(ValueEnum.MONTHLY_MORTGAGE_PAYMENT, 1);
//
//
//    //each amount calculated is for the *beginning* of that month.
//    for (int i = 0; i < nocp; i++) {
//      
//      if (amountOwed >= pmiEndsValue) {pmiAccumulator += pmiMonthly;}
//      
//      interestPayment = amountOwed * loanInterestRateMonthly;
//      interestAccumulator += interestPayment;
//      principalPayment = mpValue - interestPayment;
//      amountOwed -= principalPayment;
//    }
//  }
//
//  private class PrivateMortgageInsuranceAccum implements CalcValueGettable {
//
////    private int numberOfCalls;
//
//    public PrivateMortgageInsuranceAccum() {
////      numberOfCalls = 0;
//    }
//
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
////      System.out.println(getClass().getName() + " calls: " + numberOfCalls++);
//
//      if (compoundingPeriod < 0 || compoundingPeriod > nocp) {return 0d;}
//      createAmortizationTable(compoundingPeriod);
//      return pmiAccumulator;
//    }
//    
//  }
//  
//  private class PrivateMortgageInsurance implements CalcValueGettable {
//
////    private int numberOfCalls;
//
//    public PrivateMortgageInsurance() {
////      numberOfCalls = 0;
//    }
//    
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
////      System.out.println(getClass().getName() + " calls: " + numberOfCalls++);
//
//      if (compoundingPeriod < 0 || compoundingPeriod >= nocp) {return 0d;}
//      //notice we divide by 12 and multiply by 12.  We are using the fact that an integer
//      //truncates the fraction to our advantage.  We want the first month for each year.
//      //for example, ( 15 / 12 ) * 12 = 12.
//      return getAccumPMI(MONTHS_IN_YEAR * ((compoundingPeriod/MONTHS_IN_YEAR)+1)) -
//          getAccumPMI((compoundingPeriod / MONTHS_IN_YEAR) * MONTHS_IN_YEAR);
//    }
//    
//    private Double getAccumPMI(Integer compoundingPeriod) {
//      
//      createAmortizationTable(compoundingPeriod);
//      return pmiAccumulator;
//    }
//    
//  }
//  private class AmountOwedValue implements CalcValueGettable {
//
////    private int numberOfCalls;
//
//    public AmountOwedValue() {
////      numberOfCalls = 0;
//    }
//    
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//
////      System.out.println(getClass().getName() + " calls: " + numberOfCalls++);
//
//      if (compoundingPeriod < 0 || compoundingPeriod >= nocp) {return 0d;}
//      if (compoundingPeriod == 0) { return loanAmount; }
//      createAmortizationTable(compoundingPeriod);
//      return amountOwed;
//    }
//  }
//  
//  private class LoanAmountValue implements CalcValueGettable {
//    
////    private int numberOfCalls;
//
//    public LoanAmountValue() {
////      numberOfCalls = 0;
//    }
//    
//    @Override
//    public Double getValue (Integer compoundingPeriod) {
////      System.out.println(getClass().getName() + " calls: " + numberOfCalls++);
//
//      if (compoundingPeriod < 0) {return 0d;}
//      return loanAmount;
//    }
//  }
//
//  private class MortgagePayment implements CalcValueGettable {
//
////    private int numberOfCalls;
//
//    public MortgagePayment() {
////      numberOfCalls = 0;
//    }
//    
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
////      System.out.println(getClass().getName() + " calls: " + numberOfCalls++);
//
//      if (compoundingPeriod < 0 || compoundingPeriod >= nocp) {return 0d;}
//      return loanAmount * (loanInterestRateMonthly 
//          / (1 - (1 / Math.pow((1 + (loanInterestRateMonthly)),
//              nocp.doubleValue()))));
//    } 
//  }
//  
//  private class YearlyMortgagePayment implements CalcValueGettable {
//    
////    private int numberOfCalls;
//
//    public YearlyMortgagePayment() {
////      numberOfCalls = 0;
//    }
//    
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//
//      if (compoundingPeriod < 0 || compoundingPeriod >= nocp) {return 0d;}
//      return mortgagePayment.getValue(compoundingPeriod) * MONTHS_IN_YEAR;
//    }
//  }
//
//
//  private class InterestPaidAccumulatedValue implements CalcValueGettable {
//
////    private int numberOfCalls;
//
//    public InterestPaidAccumulatedValue() {
////      numberOfCalls = 0;
//    }
//    
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0 ) {return 0d;}
//      loadCurrentUserInputValues();
////      System.out.println(getClass().getName() + " calls: " + numberOfCalls++);
//
//      //if we go past the end of the table, then interest accumulator remains the last number
//      if (compoundingPeriod >= nocp) {
//        createAmortizationTable(nocp);
//        return interestAccumulator;
//      } else {
//        createAmortizationTable(compoundingPeriod);
//        return interestAccumulator;
//      }
//
//    }
//  }
//
//
//  private class InterestPaymentValue implements CalcValueGettable {
//
////    private int numberOfCalls;
//
//    public InterestPaymentValue() {
////      numberOfCalls = 0;
//    }
//    
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0 || compoundingPeriod >= nocp ) {return 0d;}
////      System.out.println(getClass().getName() + " calls: " + numberOfCalls++);
//
//      createAmortizationTable(compoundingPeriod);
//      return interestPayment;    
//    }
//
//  }
//
//  
//  private class YearlyPrincipalPaid implements CalcValueGettable {
//    
////    private int numberOfCalls;
//
//    public YearlyPrincipalPaid() {
////      numberOfCalls = 0;
//    }
//    
//    @Override
//    public Double getValue (Integer compoundingPeriod) {
//      if (compoundingPeriod < 0 || compoundingPeriod > nocp ) {return 0d;}
////      System.out.println(getClass().getName() + " calls: " + numberOfCalls++);
//
//      //we divide by 12 then multiply by 12 to get the month at the beginning of the year.
//      //This works because in integer division, it truncates the remainder value.
//      // so for example, 6 / 12 = 0, and 0 * 12 = 0.  25 / 12 = 2, and 2 * 12 = 24
//      return
//          amountOwedValue.getValue(MONTHS_IN_YEAR * (compoundingPeriod / MONTHS_IN_YEAR)) -
//          amountOwedValue.getValue(MONTHS_IN_YEAR * (compoundingPeriod / MONTHS_IN_YEAR)+MONTHS_IN_YEAR);
//    }
//  }
//  
//  private class YearlyInterestPaid implements CalcValueGettable {
//
//
////    private int numberOfCalls;
//
//    public YearlyInterestPaid() {
////      numberOfCalls = 0;
//    }
//    
//    @Override
//    public Double getValue(Integer compoundingPeriod) {
//      if (compoundingPeriod < 0 || compoundingPeriod > nocp ) {return 0d;}
////      System.out.println(getClass().getName() + " calls: " + numberOfCalls++);
//
//      //we divide by 12 then multiply by 12 to get the month at the beginning of the year.
//      //This works because in integer division, it truncates the remainder value.
//      // so for example, 6 / 12 = 0, and 0 * 12 = 0.  25 / 12 = 2, and 2 * 12 = 24
//      return interestPaidAccumulatedValue.getValue(MONTHS_IN_YEAR * (compoundingPeriod / MONTHS_IN_YEAR)+MONTHS_IN_YEAR) - 
//          interestPaidAccumulatedValue.getValue(MONTHS_IN_YEAR * (compoundingPeriod / MONTHS_IN_YEAR));
//    }
//
//  }
//
//
//  private void assignDataManager(DataManager dataManager) {
//    dataManager.addCalcValuePointers(
//        ValueEnum.CURRENT_AMOUNT_OUTSTANDING, amountOwedValue);
//    dataManager.addCalcValuePointers(
//        ValueEnum.ACCUM_INTEREST, interestPaidAccumulatedValue);
//    dataManager.addCalcValuePointers(
//        ValueEnum.INTEREST_PAYMENT, interestPaymentValue);
//    dataManager.addCalcValuePointers(
//        ValueEnum.YEARLY_INTEREST_PAID, yearlyInterestPaid);
//    dataManager.addCalcValuePointers(
//        ValueEnum.YEARLY_PRINCIPAL_PAID, yearlyPrincipalPaid);
//    dataManager.addCalcValuePointers(
//        ValueEnum.MONTHLY_MORTGAGE_PAYMENT, mortgagePayment);
//    dataManager.addCalcValuePointers(
//        ValueEnum.YEARLY_MORTGAGE_PAYMENT, yearlyMortgagePayment);
//    dataManager.addCalcValuePointers(
//        ValueEnum.YEARLY_PRIVATE_MORTGAGE_INSURANCE_ACCUM, privateMortgageInsuranceAccum);
//    dataManager.addCalcValuePointers(
//        ValueEnum.YEARLY_PRIVATE_MORTGAGE_INSURANCE, privateMortgageInsurance);
//    dataManager.addCalcValuePointers(
//        ValueEnum.LOAN_AMOUNT, loanAmountValue);
//  }
//
//
//}