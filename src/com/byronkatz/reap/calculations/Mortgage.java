package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class Mortgage {

  private Float closingCosts;
  private Float downPayment;
  private Float loanAmount;
  private MortgagePayment mortgagePayment;
  private Float yearlyInterestRate;
  private Float monthlyInterestRate;
  private Integer numberOfCompoundingPeriods;
  public static final Float PMI_PERCENTAGE = 0.20f;
  private Float monthlyPrivateMortgageInsurance;
  private EstateValue estateValue;
  
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  
  public Mortgage(EstateValue estateValue) {

    closingCosts = dataController.getValueAsFloat(ValueEnum.CLOSING_COSTS);
    numberOfCompoundingPeriods = dataController.getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS).intValue();
    downPayment = dataController.getValueAsFloat(ValueEnum.DOWN_PAYMENT);
    loanAmount = estateValue.getEstateValue(0) - downPayment;
    this.estateValue = estateValue;

    yearlyInterestRate =  dataController.getValueAsFloat(ValueEnum.YEARLY_INTEREST_RATE);
    monthlyInterestRate = yearlyInterestRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;

    mortgagePayment = new MortgagePayment(
        numberOfCompoundingPeriods, loanAmount, monthlyInterestRate);
    monthlyPrivateMortgageInsurance = dataController.getValueAsFloat(ValueEnum.PRIVATE_MORTGAGE_INSURANCE);

  }

  public MortgagePayment getMortgagePayment() {
    return mortgagePayment;
  }

  public Float getDownPayment() {
    return downPayment;
  }
  
  public Float getClosingCosts() {
    return closingCosts;
  }
  
  public Float getYearlyInterestRate() {
    return yearlyInterestRate;
  }

  public Float getMonthlyInterestRate() {
    return monthlyInterestRate;
  }
  
  public Integer getNumberOfCompoundingPeriods() {
    return numberOfCompoundingPeriods;
  }
  
  public Integer getYearlyNumberOfCompoundingPeriods() {
    return numberOfCompoundingPeriods / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
  }
  
  public Float getYearlyPmi(int year) {
    //TODO - come up with a function for this rather than a loop.

    Float pmiThisYear = 0.0f;
    int begOfYear = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    int endOfYear = year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;

    for (int i = begOfYear; i < endOfYear; i++) {

      //if principal outstanding is greater than Loan To Value (LTV) ratio (usually 80%), apply pmi.
      if (getPrincipalOutstandingAtPoint(i) > ((1 - PMI_PERCENTAGE) * estateValue.getEstateValue(0))) {
        pmiThisYear += monthlyPrivateMortgageInsurance;
      }
    }

    dataController.setValueAsFloat(ValueEnum.YEARLY_PRIVATE_MORTGAGE_INSURANCE, pmiThisYear, year);
    return pmiThisYear;
  }
  
  public Float getAccumulatedInterestPaymentsAtPoint (int year) {
    Float monthlyInterestRate = yearlyInterestRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;

    Float f = 0.0f;
    Integer compoundingPeriodDesired = year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    
    if (loanAmount < 0.0) {
      loanAmount = 0.0f;
    }
    if (monthlyInterestRate < 0.0) {
      monthlyInterestRate = 0.0f;
    }
    if (compoundingPeriodDesired < 0) {
      compoundingPeriodDesired = 0;
    }
    if (compoundingPeriodDesired > numberOfCompoundingPeriods) {
      compoundingPeriodDesired = numberOfCompoundingPeriods;
    }
    Float c = monthlyInterestRate+1;
    Float d = (float) Math.pow(c, compoundingPeriodDesired);
    
    Float e = 0f;
    
    if (c != 0f) {
      e = (1-d)/(1-c);

    }
    if (monthlyInterestRate == 0) {
      f = 0.0f;
    } else {
    f = mortgagePayment.getMonthlyMortgagePayment()/monthlyInterestRate;
    }
    Integer g = compoundingPeriodDesired + 1;

    Float accumInterestPaymentAtPoint = monthlyInterestRate*(loanAmount*(e+d)+(f)*(c*g-(e + d))-(mortgagePayment.getMonthlyMortgagePayment()*g));

    dataController.setValueAsFloat(ValueEnum.ACCUM_INTEREST, accumInterestPaymentAtPoint, year);
    return accumInterestPaymentAtPoint;
  }
  
  public Float getPrincipalOutstandingAtPoint (int compoundingPeriodDesired) {

    Float principalOutstandingAtPoint = 0.0f;
    Float a = monthlyInterestRate + 1;

    if (monthlyInterestRate != 0.0f) {
      principalOutstandingAtPoint = (float) ((Math.pow(a,compoundingPeriodDesired) * loanAmount) -
        ( mortgagePayment.getMonthlyMortgagePayment() *  (((a - Math.pow(a,compoundingPeriodDesired) )/ -monthlyInterestRate) + 1)));
    }
    
    return principalOutstandingAtPoint;
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


}
