package com.byronkatz.reap.calculations;

import android.util.Log;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.ValueEnum;

public class Mortgage {

  private Double closingCosts;
  private Double downPayment;
  private Double loanAmount;
  private MortgagePayment mortgagePayment;
  private Double monthlyMortgagePayment;
  private Double yearlyInterestRate;
  private Double monthlyInterestRate;
  private Integer numberOfCompoundingPeriods;
  public static final Double PMI_PERCENTAGE = 0.20d;
  private Double monthlyPrivateMortgageInsurance;
  private Double totalPurchaseValue;
  private Double[] interestPaymentArray;
  private Double[] interestPaymentAccumulatorArray;
  private Double[] principalPaymentArray;
  private Double[] amountOwedArray;
  
  private DataController dataController;
  
  public Mortgage(DataController dataController, Double totalPurchaseValue) {

    this.dataController = dataController;
    closingCosts = dataController.getValueAsDouble(ValueEnum.CLOSING_COSTS);
    numberOfCompoundingPeriods = dataController.getValueAsDouble(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS).intValue();
    
    interestPaymentArray = new Double[numberOfCompoundingPeriods + 1];
    interestPaymentAccumulatorArray = new Double[numberOfCompoundingPeriods + 1];
    principalPaymentArray = new Double[numberOfCompoundingPeriods + 1];
    amountOwedArray = new Double[numberOfCompoundingPeriods + 1];
        
    downPayment = dataController.getValueAsDouble(ValueEnum.DOWN_PAYMENT);
    this.totalPurchaseValue = totalPurchaseValue;
    loanAmount = totalPurchaseValue - downPayment;

    yearlyInterestRate =  dataController.getValueAsDouble(ValueEnum.YEARLY_INTEREST_RATE).doubleValue();
    monthlyInterestRate = yearlyInterestRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;

    mortgagePayment = new MortgagePayment( dataController,
        numberOfCompoundingPeriods, loanAmount, yearlyInterestRate);
    monthlyMortgagePayment = getMonthlyMortgagePayment(1);
    monthlyPrivateMortgageInsurance = dataController.getValueAsDouble(ValueEnum.PRIVATE_MORTGAGE_INSURANCE);

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

  public Double getDownPayment() {
    return downPayment;
  }
  
  public Double getClosingCosts() {
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
  public Double getYearlyPmi(int year) {
    //TODO - come up with a function for this rather than a loop.

    Double pmiThisYear = 0.0d;
    int begOfYear = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    int endOfYear = year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;

    for (int i = begOfYear; i < endOfYear; i++) {

      //if principal outstanding is greater than Loan To Value (LTV) ratio (usually 80%), apply pmi.
      if (getPrincipalOutstandingAtPoint(i) > ((1 - PMI_PERCENTAGE) * totalPurchaseValue)) {
        pmiThisYear += monthlyPrivateMortgageInsurance;
      }
    }

    dataController.setValueAsDouble(ValueEnum.YEARLY_PRIVATE_MORTGAGE_INSURANCE, pmiThisYear, year);
    return pmiThisYear;
  }
  
  /**
   * Determines the interest paid on the loan by the end of the year, year 1 being the first year
   * @param year is the year of the mortgage.  Year 0 represents the day the loan is received
   * @return the accumulated interest to that point in time
   */
  public Double getAccumulatedInterestPaymentsAtPoint (int year) {

    Double accumInterestPaymentAtPoint = interestPaymentAccumulatorArray[year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR];

    dataController.setValueAsDouble(ValueEnum.ACCUM_INTEREST, accumInterestPaymentAtPoint, year);

    return accumInterestPaymentAtPoint;
  }
  
  public Double getPrincipalOutstandingAtPoint (int compoundingPeriodDesired) {

    return amountOwedArray[compoundingPeriodDesired];
  }
  
  public Double calculateYearlyPrincipalPaid(int year, int monthCPModifier, int prevYearMonthCPModifier) {
    //next year's yearlyAmountOutstanding minus this year's
    final Double pastYearAmountOutstanding = getPrincipalOutstandingAtPoint(prevYearMonthCPModifier);

    final Double currentYearAmountOutstanding = getPrincipalOutstandingAtPoint(monthCPModifier);
    dataController.setValueAsDouble(ValueEnum.CURRENT_AMOUNT_OUTSTANDING,
        currentYearAmountOutstanding, year);

    final Double yearlyPrincipalPaid = pastYearAmountOutstanding - currentYearAmountOutstanding;
    dataController.setValueAsDouble(ValueEnum.YEARLY_PRINCIPAL_PAID, yearlyPrincipalPaid, year);

    return yearlyPrincipalPaid;
  }

  public Double getLoanAmount() {
    return loanAmount;
  }

  public void setLoanAmount(Double loanAmount) {
    this.loanAmount = loanAmount;
  }

  public Double getMonthlyPrivateMortgageInsurance() {
    return monthlyPrivateMortgageInsurance;
  }

  public void setMonthlyPrivateMortgageInsurance(Double monthlyPrivateMortgageInsurance) {
    this.monthlyPrivateMortgageInsurance = monthlyPrivateMortgageInsurance;
  }

  public DataController getDataController() {
    return dataController;
  }

  public void setDataController(DataController dataController) {
    this.dataController = dataController;
  }

  public void setClosingCosts(Double closingCosts) {
    this.closingCosts = closingCosts;
  }

  public void setDownPayment(Double downPayment) {
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

  public Double getTotalPurchaseValue() {
    return totalPurchaseValue;
  }

  public void setTotalPurchaseValue(Double totalPurchaseValue) {
    this.totalPurchaseValue = totalPurchaseValue;
  }

  public Double getMonthlyMortgagePayment(int year) {
    return mortgagePayment.getMonthlyMortgagePayment(year);
  }

  public void setMonthlyMortgagePayment(Double monthlyMortgagePayment) {
    this.monthlyMortgagePayment = monthlyMortgagePayment;
  }



}
