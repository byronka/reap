package com.byronkatz.reap.calculations;

public class LoanAmount {

  private TotalPurchaseValue totalPurchaseValue;
  private Float loanAmount;
  private DownPayment downPayment;
  
  
  public LoanAmount(TotalPurchaseValue totalPurchaseValue, DownPayment downPayment) {
    this.totalPurchaseValue = totalPurchaseValue;
    this.downPayment = downPayment;
    loanAmount = totalPurchaseValue.getTotalPurchaseValue() - downPayment.getDownPayment();
  }

  public Float getLoanAmount() {
    return loanAmount;
  }

}
