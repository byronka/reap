package com.byronkatz.reap.mathtests;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class TestYearlyInterestPaid implements ItemTestInterface {

  @Override
  public String getValue() {
    //yearly Interest paid on loan, using standard amortizing loan type
    
    StringBuffer s = new StringBuffer();
    final DataController dataController = RealEstateMarketAnalysisApplication
        .getInstance().getDataController();
    
    s.append("Yearly Interest Paid (yIP)");
    
    Integer year = dataController.getCurrentYearSelected();
    s.append(String.format("\nyear = %d" , year));
    
    Integer numOfCompoundingMonths = year * 12;
    s.append(String.format("\nnumOfCompoundingMonths = %d" , numOfCompoundingMonths));
    
    Float monthlyInterestRate = dataController.getValueAsFloat(ValueEnum.YEARLY_INTEREST_RATE) / 12;
    s.append(String.format("\nmonthlyInterestRate = %.4f" , monthlyInterestRate));
    
    Float totalPurchaseValue = dataController.getValueAsFloat(ValueEnum.TOTAL_PURCHASE_VALUE);
    s.append(String.format("\ntotalPurchaseValue = %.2f", totalPurchaseValue));
    
    Float downPayment = dataController.getValueAsFloat(ValueEnum.DOWN_PAYMENT);
    s.append(String.format("\ndownPayment = %.2f", downPayment));
    
    Float amountOwed = totalPurchaseValue - downPayment;
    s.append(String.format("\namountOwed = %.2f", amountOwed));
    
    Float monthlyInterestPaid = 0f;
    Float monthlyPrincipalPaid = 0f;
    Float monthlyInterestPaidAccumulator = 0f;
    Float monthlyInterestPaidAccumulatorPrevYear = 0f;
    
    Float totalCompoundingPeriodMonths = dataController.getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS);
    s.append(String.format("\ntotalLoanCompoundingPeriods = %d", totalCompoundingPeriodMonths.intValue()));
    
    final Float mortgagePayment = (float) (amountOwed * (monthlyInterestRate / (1 - (1 / Math.pow(1 + monthlyInterestRate, totalCompoundingPeriodMonths)))));
    s.append(String.format("\nmortgagePayment = %.2f", mortgagePayment));
    
    for (int i = 0; i < numOfCompoundingMonths; i++) {
      monthlyInterestPaid = amountOwed * monthlyInterestRate;
      monthlyInterestPaidAccumulator += monthlyInterestPaid;
      monthlyPrincipalPaid = mortgagePayment - monthlyInterestPaid;
      amountOwed -= monthlyPrincipalPaid;
      
      //13 because we are starting with 0
      if (i == (numOfCompoundingMonths - 13)) {
        monthlyInterestPaidAccumulatorPrevYear = monthlyInterestPaidAccumulator;
      }
      
    }
    
    s.append(String.format("\nmonthlyInterestPaidAccumulator = %.2f", monthlyInterestPaidAccumulator));
    s.append(String.format("\nmonthlyInterestPaidAccumulatorPrevYear = %.2f", monthlyInterestPaidAccumulatorPrevYear));
    
    Float interestPaidThisYear = monthlyInterestPaidAccumulator - monthlyInterestPaidAccumulatorPrevYear;
    s.append(String.format("\ninterestPaidThisYear = %.2f", interestPaidThisYear));
    
    Float yearlyInterestPaidCached = dataController.getValueAsFloat(ValueEnum.YEARLY_INTEREST_PAID, year);
    s.append(String.format("\nyearlyInterestPaid in cache = %.2f", yearlyInterestPaidCached));
    
    if (Math.abs(yearlyInterestPaidCached - interestPaidThisYear) < EPSILON) {
      s.append(CORRECT);
    } else {
      s.append(INCORRECT);
      s.append(String.format("\nActual answer is: %.2f\n\n", interestPaidThisYear));
    }
    
    return s.toString();
  }

}
