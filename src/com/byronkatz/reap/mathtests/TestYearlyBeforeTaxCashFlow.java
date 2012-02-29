package com.byronkatz.reap.mathtests;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class TestYearlyBeforeTaxCashFlow implements ItemTestInterface {
  
  public TestYearlyBeforeTaxCashFlow() {
    
  }

  private String getYearlyBeforeTaxCashFlow() {
    // cashflowIn - cashflowOut

    StringBuffer s = new StringBuffer();
    final DataController dataController = RealEstateMarketAnalysisApplication
        .getInstance().getDataController();
    Integer year = dataController.getCurrentYearSelected();
    
    final Double yearlyPrivateMortgageInsurance = dataController.getValueAsDouble(
        ValueEnum.YEARLY_PRIVATE_MORTGAGE_INSURANCE, year);
    final Double yearlyMortgagePayment = dataController.getValueAsDouble(
        ValueEnum.YEARLY_MORTGAGE_PAYMENT, year);
    final Double fVPropertyTax = dataController.getValueAsDouble(
        ValueEnum.YEARLY_PROPERTY_TAX, year);
    final Double fVMunicipalFees = dataController.getValueAsDouble(
        ValueEnum.YEARLY_MUNICIPAL_FEES, year);
    final Double fVYearlyGeneralExpenses = dataController.getValueAsDouble(
        ValueEnum.YEARLY_GENERAL_EXPENSES, year);
    final Double fVYearlyHomeInsurance = dataController.getValueAsDouble(
        ValueEnum.YEARLY_HOME_INSURANCE, year);
    final Double fVNetYearlyIncome = dataController.getValueAsDouble(
        ValueEnum.YEARLY_INCOME, year);
    final Double yearlyOutlay = dataController.getValueAsDouble(
        ValueEnum.YEARLY_OUTLAY, year);

    final Double actualYearlyOutlay = fVPropertyTax + fVMunicipalFees + 
        yearlyMortgagePayment + fVYearlyGeneralExpenses + 
        fVYearlyHomeInsurance + yearlyPrivateMortgageInsurance;
    final Double actualYearlyBeforeTaxCashFlow = fVNetYearlyIncome - actualYearlyOutlay;
    final Double yearlyBeforeTaxCashFlow = dataController.getValueAsDouble(
        ValueEnum.YEARLY_BEFORE_TAX_CASH_FLOW, year);

    s.append("YEARLY BEFORE TAX CASH FLOW");
    s.append(String.format("\nYearly Net Income (yNI): %.2f", fVNetYearlyIncome));
    s.append(String.format("\nYearly Before Tax Cash Flow (yBTCF): %.2f", yearlyBeforeTaxCashFlow));
    s.append("\nCheck:\n  yNI - yO = yBTCF");
    s.append(String.format("\n%.2f - %.2f = %.2f", fVNetYearlyIncome, yearlyOutlay, yearlyBeforeTaxCashFlow));
    
    if (Math.abs(actualYearlyBeforeTaxCashFlow - yearlyBeforeTaxCashFlow) < EPSILON) {
      s.append(CORRECT);
    } else {
      s.append(INCORRECT);
      s.append(String.format("\nActual answer is: %.2f\n\n", actualYearlyBeforeTaxCashFlow));
    }


    return s.toString();
  }

  @Override
  public String getValue() {
    return getYearlyBeforeTaxCashFlow();
  }


}
