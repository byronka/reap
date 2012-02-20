package com.byronkatz.reap.calculations;

import android.util.Log;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.ValueEnum;

public class Mortgage {

  private Float closingCosts;
  private Float downPayment;
  private Float loanAmount;
  private MortgagePayment mortgagePayment;
  private Double monthlyMortgagePayment;
  private Double yearlyInterestRate;
  private Double monthlyInterestRate;
  private Integer numberOfCompoundingPeriods;
  public static final Float PMI_PERCENTAGE = 0.20f;
  private Float monthlyPrivateMortgageInsurance;
  private Float totalPurchaseValue;
  private Double[] interestPaymentArray;
  private Double[] interestPaymentAccumulatorArray;
  private Double[] principalPaymentArray;
  private Double[] amountOwedArray;
  
  private DataController dataController;
  
  public Mortgage(DataController dataController, Float totalPurchaseValue) {

    this.dataController = dataController;
    closingCosts = dataController.getValueAsFloat(ValueEnum.CLOSING_COSTS);
    numberOfCompoundingPeriods = dataController.getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS).intValue();
    
    interestPaymentArray = new Double[numberOfCompoundingPeriods + 1];
    interestPaymentAccumulatorArray = new Double[numberOfCompoundingPeriods + 1];
    principalPaymentArray = new Double[numberOfCompoundingPeriods + 1];
    amountOwedArray = new Double[numberOfCompoundingPeriods + 1];
        
    downPayment = dataController.getValueAsFloat(ValueEnum.DOWN_PAYMENT);
    this.totalPurchaseValue = totalPurchaseValue;
    loanAmount = totalPurchaseValue - downPayment;

    yearlyInterestRate =  dataController.getValueAsFloat(ValueEnum.YEARLY_INTEREST_RATE).doubleValue();
    monthlyInterestRate = yearlyInterestRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;

    mortgagePayment = new MortgagePayment( dataController,
        numberOfCompoundingPeriods, loanAmount, yearlyInterestRate);
    monthlyMortgagePayment = getMonthlyMortgagePayment(1);
    monthlyPrivateMortgageInsurance = dataController.getValueAsFloat(ValueEnum.PRIVATE_MORTGAGE_INSURANCE);

    calculateMortgageArray();
  }
  
  private void calculateMortgageArray() {
    amountOwedArray[0] = loanAmount.doubleValue();
    principalPaymentArray[0] = 0d;
    interestPaymentArray[0] = 0d;
    interestPaymentAccumulatorArray[0] = 0.0d;
    Double tempInterestPaymentAccumulator = 0d;
    
    for (int i = 1; i <= numberOfCompoundingPeriods; i++) {
      
      interestPaymentArray[i] = amountOwedArray[i - 1] * monthlyInterestRate;
      tempInterestPaymentAccumulator += interestPaymentArray[i];
      interestPaymentAccumulatorArray[i] = tempInterestPaymentAccumulator;
      principalPaymentArray[i] = monthlyMortgagePayment - interestPaymentArray[i];
      amountOwedArray[i] = amountOwedArray[i - 1] - principalPaymentArray[i];


    }
  }
  
  public Mortgage() {
     }

  public Float getDownPayment() {
    return downPayment;
  }
  
  public Float getClosingCosts() {
    return closingCosts;
  }
  
  public Double getYearlyInterestRate() {
    return yearlyInterestRate;
  }

  public Double getMonthlyInterestRate() {
    return monthlyInterestRate;
  }
  
  public Integer getNumberOfCompoundingPeriods() {
    return numberOfCompoundingPeriods;
  }
  
  public Integer getYearlyNumberOfCompoundingPeriods() {
    return numberOfCompoundingPeriods / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
  }
  
  /**
   * returns the amount of money put towards PMI for the year
   * 
   * Determines the number of months for the year during which the amount
   * outstanding will be more than 80% of the total purchase price.  It then
   * multiplies that number of months by the monthly PMI cost, set by the user.
   * 
   * This equation currently uses an implementation which uses a loop - for
   * each month in the year requested, it determines the number of months
   * that the amount outstanding will be more than 80%.  This is an
   * expensive loop, and a functional algorithm would be better suited.
   * @param year the year for which we wish to obtain the PMI
   * @return the amount of money towards PMI for the year
   */
  public Float getYearlyPmi(int year) {
    //TODO - come up with a function for this rather than a loop.

    Float pmiThisYear = 0.0f;
    int begOfYear = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    int endOfYear = year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;

    for (int i = begOfYear; i < endOfYear; i++) {

      //if principal outstanding is greater than Loan To Value (LTV) ratio (usually 80%), apply pmi.
      if (getPrincipalOutstandingAtPoint(i) > ((1 - PMI_PERCENTAGE) * totalPurchaseValue)) {
        pmiThisYear += monthlyPrivateMortgageInsurance;
      }
    }

    dataController.setValueAsFloat(ValueEnum.YEARLY_PRIVATE_MORTGAGE_INSURANCE, pmiThisYear, year);
    return pmiThisYear;
  }
  
  /**
   * Determines the interest paid on the loan by the end of the year, year 1 being the first year
   * @param year is the year of the mortgage.  Year 0 represents the day the loan is received
   * @return the accumulated interest to that point in time
   */
  public Float getAccumulatedInterestPaymentsAtPoint (int year) {

    Float accumInterestPaymentAtPoint = interestPaymentAccumulatorArray[year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR].floatValue();

    dataController.setValueAsFloat(ValueEnum.ACCUM_INTEREST, accumInterestPaymentAtPoint, year);

    return accumInterestPaymentAtPoint;
  }
  
  public Float getPrincipalOutstandingAtPoint (int compoundingPeriodDesired) {

    return amountOwedArray[compoundingPeriodDesired].floatValue();
  }
  
  public Float calculateYearlyPrincipalPaid(int year, int monthCPModifier, int prevYearMonthCPModifier) {
    //next year's yearlyAmountOutstanding minus this year's
    final Float pastYearAmountOutstanding = getPrincipalOutstandingAtPoint(prevYearMonthCPModifier);

    final Float currentYearAmountOutstanding = getPrincipalOutstandingAtPoint(monthCPModifier);
    dataController.setValueAsFloat(ValueEnum.CURRENT_AMOUNT_OUTSTANDING,
        currentYearAmountOutstanding, year);

    final Float yearlyPrincipalPaid = pastYearAmountOutstanding - currentYearAmountOutstanding;
    dataController.setValueAsFloat(ValueEnum.YEARLY_PRINCIPAL_PAID, yearlyPrincipalPaid, year);

    return yearlyPrincipalPaid;
  }

  public Float getLoanAmount() {
    return loanAmount;
  }

  public void setLoanAmount(Float loanAmount) {
    this.loanAmount = loanAmount;
  }

  public Float getMonthlyPrivateMortgageInsurance() {
    return monthlyPrivateMortgageInsurance;
  }

  public void setMonthlyPrivateMortgageInsurance(Float monthlyPrivateMortgageInsurance) {
    this.monthlyPrivateMortgageInsurance = monthlyPrivateMortgageInsurance;
  }

  public DataController getDataController() {
    return dataController;
  }

  public void setDataController(DataController dataController) {
    this.dataController = dataController;
  }

  public void setClosingCosts(Float closingCosts) {
    this.closingCosts = closingCosts;
  }

  public void setDownPayment(Float downPayment) {
    this.downPayment = downPayment;
  }

  public void setYearlyInterestRate(Double yearlyInterestRate) {
    this.yearlyInterestRate = yearlyInterestRate;
  }

  public void setMonthlyInterestRate(Double monthlyInterestRate) {
    this.monthlyInterestRate = monthlyInterestRate;
  }

  public void setNumberOfCompoundingPeriods(Integer numberOfCompoundingPeriods) {
    this.numberOfCompoundingPeriods = numberOfCompoundingPeriods;
  }

  public Float getTotalPurchaseValue() {
    return totalPurchaseValue;
  }

  public void setTotalPurchaseValue(Float totalPurchaseValue) {
    this.totalPurchaseValue = totalPurchaseValue;
  }

  public Double getMonthlyMortgagePayment(int year) {
    return mortgagePayment.getMonthlyMortgagePayment(year);
  }

  public void setMonthlyMortgagePayment(Double monthlyMortgagePayment) {
    this.monthlyMortgagePayment = monthlyMortgagePayment;
  }



}
