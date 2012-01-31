package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class AmortizationSchedule {

  private EstateValue estateValue;
  private Float monthlyInterestRate; 
  private MortgagePayment mp;
  
  private static final DataController dc = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  
  public AmortizationSchedule(EstateValue estateValue, MortgagePayment mp) {
    this.estateValue = estateValue;
    this.mp = mp;
    monthlyInterestRate = dc.getValueAsFloat(ValueEnum.YEARLY_INTEREST_RATE) / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
  }
  
  public Float getPrincipalOutstandingAtPoint (int compoundingPeriodDesired) {

    Float a = monthlyInterestRate+1;

    Float princpalOutstandingAtPoint = (float) ((Math.pow(a,compoundingPeriodDesired) * estateValue.getEstateValue(0)) -
        ( mp.getMonthlyMortgagePayment() *  (((a - Math.pow(a,compoundingPeriodDesired) )/ -monthlyInterestRate) + 1)));

    return princpalOutstandingAtPoint;
  }
  
  public Float calculateYearlyPrincipalPaid(int year, int monthCPModifier, int prevYearMonthCPModifier) {
    //next year's yearlyAmountOutstanding minus this year's
    final Float pastYearAmountOutstanding = getPrincipalOutstandingAtPoint(prevYearMonthCPModifier);

    final Float currentYearAmountOutstanding = getPrincipalOutstandingAtPoint(monthCPModifier);
    dc.setValueAsFloat(ValueEnum.CURRENT_AMOUNT_OUTSTANDING,
        currentYearAmountOutstanding, year);

    final Float yearlyPrincipalPaid = pastYearAmountOutstanding - currentYearAmountOutstanding;
    dc.setValueAsFloat(ValueEnum.YEARLY_PRINCIPAL_PAID, yearlyPrincipalPaid, year);

    return yearlyPrincipalPaid;
  }
  
  public Float getAccumulatedInterestPaymentsAtPoint (int compoundingPeriodDesired) {

    Float f = 0.0f;

    Float c = monthlyInterestRate+1;
    Float d = (float) Math.pow(c, compoundingPeriodDesired);
    Float e = (1-d)/(1-c);
    if (monthlyInterestRate == 0) {
      f = 0.0f;
    } else {
    f = mp.getMonthlyMortgagePayment()/monthlyInterestRate;
    }
    Integer g = compoundingPeriodDesired + 1;

    Float accumInterestPaymentAtPoint = monthlyInterestRate*(estateValue.getEstateValue(0)*(e+d)+(f)*(c*g-(e + d))-(mp.getMonthlyMortgagePayment()*g));

    return accumInterestPaymentAtPoint;
  }
  
}
