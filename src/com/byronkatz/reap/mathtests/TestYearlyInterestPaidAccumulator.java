package com.byronkatz.reap.mathtests;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class TestYearlyInterestPaidAccumulator implements ItemTestInterface {

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
    
    Double monthlyInterestRate = dataController.getValueAsDouble(ValueEnum.YEARLY_INTEREST_RATE).doubleValue() / 12;
    s.append(String.format("\nmonthlyInterestRate = %.4f" , monthlyInterestRate));
    
    Double totalPurchaseValue = dataController.getValueAsDouble(ValueEnum.TOTAL_PURCHASE_VALUE);
    s.append(String.format("\ntotalPurchaseValue = %.2f", totalPurchaseValue));
    
    Double downPayment = dataController.getValueAsDouble(ValueEnum.DOWN_PAYMENT);
    s.append(String.format("\ndownPayment = %.2f", downPayment));
    
    Double amountOwed = (double) (totalPurchaseValue - downPayment);
    s.append(String.format("\namountOwed = %.2f", amountOwed));
    
    Double monthlyInterestPaid = 0d;
    Double monthlyPrincipalPaid = 0d;
    Double monthlyInterestPaidAccumulator = 0d;
    Double monthlyInterestPaidAccumulatorPrevYear = 0d;
    
    Double totalCompoundingPeriodMonths = dataController.getValueAsDouble(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS);
    s.append(String.format("\ntotalLoanCompoundingPeriods = %d", totalCompoundingPeriodMonths.intValue()));
    
    final Double mortgagePayment = amountOwed * (monthlyInterestRate / (1 - (1 / Math.pow(1 + monthlyInterestRate, totalCompoundingPeriodMonths))));
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
    
    Double interestPaidThisYear = monthlyInterestPaidAccumulator - monthlyInterestPaidAccumulatorPrevYear;
    s.append(String.format("\ninterestPaidThisYear = %.2f", interestPaidThisYear));
    
    Double yearlyInterestPaidAccumulatorCached = dataController.getValueAsDouble(ValueEnum.ACCUM_INTEREST, year);
    s.append(String.format("\nyearlyInterestPaidAccumulator in cache = %.2f", yearlyInterestPaidAccumulatorCached));
    
    if (Math.abs(yearlyInterestPaidAccumulatorCached - monthlyInterestPaidAccumulator) < EPSILON) {
      s.append(CORRECT);
    } else {
      s.append(INCORRECT);
      s.append(String.format("\nActual answer is: %.2f\n\n", monthlyInterestPaidAccumulator));
    }
    
    return s.toString();
  }


}
