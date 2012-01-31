package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class PrivateMortgageInsurance {

  private Float monthlyPrivateMortgageInsurance;
  private Float loanToValueRatio;
  private static final Float STANDARD_LTV_RATIO = 0.80f;
  private Loan loan;
  
  public PrivateMortgageInsurance(Loan loan) {
    monthlyPrivateMortgageInsurance = (RealEstateMarketAnalysisApplication.getInstance().
    getDataController().getValueAsFloat(ValueEnum.PRIVATE_MORTGAGE_INSURANCE));
    loanToValueRatio = STANDARD_LTV_RATIO;

  }

  public Float getMonthlyPrivateMortgageInsurance() {
    return monthlyPrivateMortgageInsurance;
  }
  
  private Integer getCompoundingPeriodWhereAmountOwedPassesLTV(Float principalOutstanding) {
    Float mp = loan.getMp().getMonthlyMortgagePayment();
    Float loanAmount = loan.getLoanAmount().getLoanAmount();
    Float totalPurchaseValue = loan.getTotalPurchaseValue().getTotalPurchaseValue();
    Float mir = loan.getInterestRate().getMonthlyInterestRate();
    Integer periodWherePmiEnds = 0;
    Float pointAtWhichPmiNoLongerApplies = loanToValueRatio * totalPurchaseValue;
    
    //TODO Note: this does NOT currently work.  When fixed, remove this comment. BRK 1/29/2012
    periodWherePmiEnds = (int) ((principalOutstanding - mp / mir + mp) / (Math.log10(1 + mir) * totalPurchaseValue - mp / mir ));
    
    return periodWherePmiEnds;
  }

}
