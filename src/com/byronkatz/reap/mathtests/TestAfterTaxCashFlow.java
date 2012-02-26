package com.byronkatz.reap.mathtests;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class TestAfterTaxCashFlow implements ItemTestInterface {


  public TestAfterTaxCashFlow() {
    
  }

  @Override
  public String getValue() {

    StringBuffer s = new StringBuffer();
    final DataController dataController = RealEstateMarketAnalysisApplication
        .getInstance().getDataController();
    Integer year = dataController.getCurrentYearSelected();
    
    final Double yearlyBeforeTaxCashFlow = dataController.getValueAsDouble(
        ValueEnum.YEARLY_BEFORE_TAX_CASH_FLOW, year);
    final Double yearlyIncomeTax = dataController.getValueAsDouble(ValueEnum.YEARLY_TAX_ON_INCOME, year);
    final Double yearlyAfterTaxCashFlow = dataController.getValueAsDouble(ValueEnum.ATCF, year);
    
    s.append("YEARLY AFTER TAX CASH FLOW (yATCF)");
    s.append(String.format("\nYearly Before Tax Cash Flow (yBTCF): %.2f -", yearlyBeforeTaxCashFlow));
    s.append(String.format("\nYearly Income Tax (yIT): %.2f", yearlyIncomeTax));
    s.append("\n-----------------------------");
    s.append(String.format("\nYearly After Tax Cash Flow (yATCF): %.2f", yearlyAfterTaxCashFlow));
    
    Double actualATCF = yearlyBeforeTaxCashFlow - yearlyIncomeTax;
    
    if (Math.abs(actualATCF - yearlyAfterTaxCashFlow) < EPSILON) {
      s.append(CORRECT);
    } else {
      s.append(INCORRECT);
      s.append(String.format("\nActual answer is: %.2f\n\n", actualATCF));
    }

    return s.toString();
  }


}
