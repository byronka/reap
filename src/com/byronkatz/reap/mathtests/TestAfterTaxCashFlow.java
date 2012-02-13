package com.byronkatz.reap.mathtests;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class TestAfterTaxCashFlow implements ItemTestInterface {


  public TestAfterTaxCashFlow() {
    
  }

  private String getATCF() {
    // cashflowIn - cashflowOut

    StringBuffer s = new StringBuffer();
    final DataController dataController = RealEstateMarketAnalysisApplication
        .getInstance().getDataController();
    Integer year = dataController.getCurrentYearSelected();
    
    final Float yearlyPrivateMortgageInsurance = dataController.getValueAsFloat(
        ValueEnum.YEARLY_PRIVATE_MORTGAGE_INSURANCE, year);
    final Float yearlyMortgagePayment = dataController.getValueAsFloat(
        ValueEnum.YEARLY_MORTGAGE_PAYMENT, year);
    final Float fVPropertyTax = dataController.getValueAsFloat(
        ValueEnum.YEARLY_PROPERTY_TAX, year);
    final Float fVMunicipalFees = dataController.getValueAsFloat(
        ValueEnum.YEARLY_MUNICIPAL_FEES, year);
    final Float fVYearlyGeneralExpenses = dataController.getValueAsFloat(
        ValueEnum.YEARLY_GENERAL_EXPENSES, year);
    final Float fVYearlyHomeInsurance = dataController.getValueAsFloat(
        ValueEnum.YEARLY_HOME_INSURANCE, year);
    final Float fVNetYearlyIncome = dataController.getValueAsFloat(
        ValueEnum.YEARLY_INCOME, year);
    final Float yearlyOutlay = dataController.getValueAsFloat(
        ValueEnum.YEARLY_OUTLAY, year);

    final Float actualYearlyOutlay = fVPropertyTax + fVMunicipalFees + 
        yearlyMortgagePayment + fVYearlyGeneralExpenses + 
        fVYearlyHomeInsurance + yearlyPrivateMortgageInsurance;
    final Float actualYearlyBeforeTaxCashFlow = fVNetYearlyIncome - actualYearlyOutlay;
    final Float yearlyBeforeTaxCashFlow = dataController.getValueAsFloat(
        ValueEnum.YEARLY_BEFORE_TAX_CASH_FLOW, year);

    s.append("YEARLY OUTLAY (yO)");
    s.append(String.format("\nYearly Private Mortgage Insurance (yPMI): %.2f +", yearlyPrivateMortgageInsurance));
    s.append(String.format("\nYearly Mortgage Payment (yMP): %.2f +", yearlyMortgagePayment));
    s.append(String.format("\nYearly Property Tax (yPT): %.2f +", fVPropertyTax));
    s.append(String.format("\nYearly Municipal Fees (yMF): %.2f +", fVMunicipalFees));
    s.append(String.format("\nYearly General Expenses (yGE): %.2f +", fVYearlyGeneralExpenses));
    s.append(String.format("\nYearly Home Insurance (yHI): %.2f", fVYearlyHomeInsurance));
    s.append("\n-----------------------------");
    s.append(String.format("\nYearly Outlay (yO): %.2f", yearlyOutlay));
    
    if (Math.abs(actualYearlyOutlay - yearlyOutlay) < EPSILON) {
      s.append(CORRECT);
    } else {
      s.append(INCORRECT);
      s.append(String.format("\nActual answer is: %.2f\n\n", actualYearlyOutlay));
    }

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
    return getATCF();
  }

}
