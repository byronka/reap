package com.byronkatz.reap.calculations;

public class Loan {

  private InterestRate interestRate;
  private NumberOfCompoundingPeriods numOfCompoundingPeriods;
  private LoanAmount loanAmount;
  private Float principalPayment;
  private Float interestPayment;
  private MortgagePayment mp;
  private TotalPurchaseValue totalPurchaseValue;
  private DownPayment downPayment;
  private PrivateMortgageInsurance pmi;
  
  public Loan() {
    downPayment = new DownPayment();
    totalPurchaseValue = new TotalPurchaseValue();
    loanAmount = new LoanAmount(totalPurchaseValue, downPayment);
    numOfCompoundingPeriods = new NumberOfCompoundingPeriods();
    interestRate = new InterestRate();
    mp = new MortgagePayment(interestRate, loanAmount, numOfCompoundingPeriods);
    pmi = new PrivateMortgageInsurance(this);
    
  }
  
  public Float getPrincipalOutstandingAtPoint (int compoundingPeriodDesired) {

    Float fvir = interestRate.getFutureValueMonthlyInterestRate(compoundingPeriodDesired);
    Float mPayment = mp.getMonthlyMortgagePayment();
    Float mir = interestRate.getMonthlyInterestRate();
    Float aO = loanAmount.getLoanAmount();

    Float a = interestRate.getMonthlyInterestRate()+1;

    Float principalOutstandingAtPoint = (float) ((fvir * aO) -
        ( mPayment *  (((a - fvir )/ -mir) + 1)));

    return principalOutstandingAtPoint;
  }


  public InterestRate getInterestRate() {
    return interestRate;
  }

  public LoanAmount getLoanAmount() {
    return loanAmount;
  }

  public MortgagePayment getMp() {
    return mp;
  }

  public TotalPurchaseValue getTotalPurchaseValue() {
    return totalPurchaseValue;
  }
}
