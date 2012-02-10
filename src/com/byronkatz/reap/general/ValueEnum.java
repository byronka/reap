package com.byronkatz.reap.general;

import com.byronkatz.R.string;

public enum ValueEnum {

  ACCUM_INTEREST ("Accumulated interest paid", ValueType.CURRENCY, false, true, string.accumulatedInterestPaidHelpText, string.accumulatedInterestPaidTitleText), 
  ATCF ("After tax cash flow", ValueType.CURRENCY, false, true, string.atcfHelpText , string.atcfTitleText),
  ATCF_ACCUMULATOR ("After tax cash flow accumulator", ValueType.CURRENCY, false, true, string.atcfAccumulatorHelpText, string.atcfAccumulatorTitleText),
  ATCF_NPV ("After tax cash flow NPV", ValueType.CURRENCY, false, true, string.atcfNetPresentValueHelpText, string.atcfNetPresentValueTitleText),
  ATER ("After tax equity reversion", ValueType.CURRENCY, false, true, string.aterHelpText, string.aterTitleText),
  ATER_PV ("Present value ATER", ValueType.CURRENCY, false, true, string.aterPresentValueHelpText, string.aterPresentValueTitleText),
  BROKER_CUT_OF_SALE ("Broker cut at sale", ValueType.CURRENCY, false, true, string.brokerCutHelpText, string.brokerCutTitleText), 
  BUILDING_VALUE("Building value", ValueType.CURRENCY, true, false, string.buildingValueHelpText, string.buildingValueTitleText),
  CITY("City", ValueType.STRING, true, false, string.cityHelpText, string.cityTitleText),
  CLOSING_COSTS("Closing costs", ValueType.CURRENCY, true, false, string.closingCostsHelpText, string.closingCostsTitleText), 
  COMMENTS("Comments", ValueType.STRING, true, false, string.commentsHelpText, string.commentsTitleText),
  CURRENT_AMOUNT_OUTSTANDING ("Current amount outstanding", ValueType.CURRENCY, false, true, string.currentAmountOutstandingHelpText, string.currentAmountOutstandingTitleText), 
  DOWN_PAYMENT("Down payment", ValueType.CURRENCY, true, false, string.downPaymentHelpText, string.downPaymentTitleText),
  ESTIMATED_RENT_PAYMENTS("Estimated rent payments", ValueType.CURRENCY, true, false, string.estimatedRentPaymentsHelpText, string.estimatedRentPaymentsTitleText),
  FIX_UP_COSTS("Fix-up costs", ValueType.CURRENCY, true, false, string.fixupCostsHelpText, string.fixupCostsTitleText),
  GENERAL_SALE_EXPENSES("General sale expenses", ValueType.CURRENCY, true, false, string.generalSaleExpensesHelpText, string.generalSaleExpensesTitleText),
  INFLATION_RATE("Inflation rate", ValueType.PERCENTAGE, true, false, string.inflationRateHelpText, string.inflationRateTitleText),
  INITIAL_YEARLY_GENERAL_EXPENSES("Initial yearly general expenses", ValueType.CURRENCY, true, false, string.initialYearlyGeneralExpensesHelpText, string.initialYearlyGeneralExpensesTitleText),
  LOCAL_MUNICIPAL_FEES("Initial local municipal fees", ValueType.CURRENCY, true, false, string.localMunicipalFeesHelpText, string.localMunicipalFeesTitleText),
  MARGINAL_TAX_RATE("Marginal tax rate", ValueType.PERCENTAGE, true, false, string.marginalTaxRateHelpText, string.marginalTaxRateTitleText),
  MODIFIED_INTERNAL_RATE_OF_RETURN("Modified internal rate of return", ValueType.PERCENTAGE, false, true, string.modifiedInternalRateOfReturnHelpText, string.modifiedInternalRateOfReturnTitleText),
  MONTHLY_MORTGAGE_PAYMENT ("Monthly mortgage payment", ValueType.CURRENCY, false, true, string.monthlyMortgagePaymentHelpText, string.monthlyMortgagePaymentTitleText),  
  NPV  ("Net present value", ValueType.CURRENCY, false, true, string.netPresentValueHelpText, string.netPresentValueTitleText),
  NUMBER_OF_COMPOUNDING_PERIODS("Number of compounding periods", ValueType.INTEGER, true, false, string.numberOfCompoundingPeriodsHelpText, string.numberOfCompoundingPeriodsTitleText),
  PRIVATE_MORTGAGE_INSURANCE ("Private mortgage insurance", ValueType.CURRENCY, true, false, string.privateMortgageInsuranceHelpText, string.privateMortgageInsuranceTitleText),
  PROJECTED_HOME_VALUE ("Projected home value at sale time", ValueType.CURRENCY, false, true, string.projectedHomeValueHelpText, string.projectedHomeValueTitleText), 
  PROPERTY_TAX("Initial property tax", ValueType.CURRENCY, true, false, string.propertyTaxHelpText, string.propertyTaxTitleText),
  REAL_ESTATE_APPRECIATION_RATE("Real estate appreciation rate", ValueType.PERCENTAGE, true, false, string.realEstateAppreciationRateHelpText, string.realEstateAppreciationRateTitleText),
  REQUIRED_RATE_OF_RETURN("Required rate of return", ValueType.PERCENTAGE, true, false, string.requiredRateOfReturnHelpText, string.requiredRateOfReturnTitleText),
  SELLING_BROKER_RATE("Selling broker rate", ValueType.PERCENTAGE, true, false, string.sellingBrokerRateHelpText, string.sellingBrokerRateTitleText),
  SELLING_EXPENSES ("Selling expenses", ValueType.CURRENCY, false, true, string.sellingExpensesHelpText, string.sellingExpensesTitleText), 
  STATE_INITIALS("State", ValueType.STRING, true, false, string.stateInitialsHelpText, string.stateInitialsTitleText),
  STREET_ADDRESS("street address", ValueType.STRING, true, false, string.streetAddressHelpText, string.streetAddressTitleText),
  TAXABLE_INCOME ("Yearly taxable income", ValueType.CURRENCY, false, true, string.taxableIncomeHelpText, string.taxableIncomeTitleText), 
  TAXES_DUE_AT_SALE ("Taxes due at sale", ValueType.CURRENCY, false, true, string.taxesDueAtSaleHelpText, string.taxesDueAtSaleTitleText), 
  TOTAL_PURCHASE_VALUE ("Total purchase value", ValueType.CURRENCY, true, false, string.totalPurchaseValueHelpText, string.totalPurchaseValueTitleText),
  VACANCY_AND_CREDIT_LOSS_RATE("Vacancy and credit loss rate", ValueType.PERCENTAGE, true, false, string.vacancyAndCreditLossRateHelpText, string.vacancyAndCreditLossRateTitleText),
  YEARLY_GENERAL_EXPENSES ("Yearly general expenses", ValueType.CURRENCY, false, true, string.yearlyGeneralExpensesHelpText, string.yearlyGeneralExpensesTitleText), 
  YEARLY_HOME_INSURANCE("Yearly home insurance", ValueType.CURRENCY, true, false, string.yearlyHomeInsuranceHelpText, string.yearlyHomeInsuranceTitleText),
  YEARLY_INCOME ("Yearly rent income", ValueType.CURRENCY, false, true, string.yearlyIncomeHelpText, string.yearlyIncomeTitleText),  
  YEARLY_INTEREST_PAID ("Yearly interest paid", ValueType.CURRENCY, false, true, string.yearlyInterestPaidHelpText, string.yearlyInterestPaidTitleText),
  YEARLY_INTEREST_RATE("Yearly interest rate", ValueType.PERCENTAGE, true, false, string.yearlyInterestRateHelpText, string.yearlyInterestRateTitleText),
  YEARLY_MORTGAGE_PAYMENT ("Yearly mortgage payment", ValueType.CURRENCY, false, true, string.yearlyMortgagePaymentHelpText, string.yearlyMortgagePaymentTitleText), 
  YEARLY_PRIVATE_MORTGAGE_INSURANCE ("Yearly private mortgage insurance", ValueType.CURRENCY, false, true, string.yearlyPrivateMortgageInsuranceHelpText, string.yearlyPrivateMortgageInsuranceTitleText),
  YEARLY_PRINCIPAL_PAID ("Yearly principal paid", ValueType.CURRENCY, false, true, string.yearlyPrincipalPaidHelpText, string.yearlyPrincipalPaidTitleText), 
  YEARLY_PROPERTY_TAX ("Yearly property tax", ValueType.CURRENCY, false, true, string.yearlyPropertyTaxHelpText, string.yearlyPropertyTaxTitleText);
  
  private ValueEnum(String valueText, ValueType valueType, 
      Boolean isSavedToDatabase, Boolean isVaryingByYear,
      Integer helpText, Integer titleText) {
    this.valueText = valueText;
    this.valueType = valueType;
    this.isSavedToDatabase = isSavedToDatabase;
    this.isVaryingByYear = isVaryingByYear;
    this.helpText = helpText;
    this.titleText = titleText;
  }
  
  public String toString() {
    return valueText;
  }
  
  //the following should work but is crashing.  Need to test.  Uncomment when all the title texts are correct, then remove the old toString()
//  public String toString() {
//    return Resources.getSystem().getString(titleText);
//  }
  
  public ValueType getType() {
    return valueType;
  }

  public Integer getTitleText() {
    return titleText;
  }
  
  public Integer getHelpText() {
    return helpText;
  }
  
  public Boolean isSavedToDatabase() {
    return isSavedToDatabase;
  }

  public void setIsSavedToDatabase(Boolean isSavedToDatabase) {
    this.isSavedToDatabase = isSavedToDatabase;
  }

  public Boolean isVaryingByYear() {
    return isVaryingByYear;
  }

  private Integer helpText;
  private Integer titleText;
  private String valueText;
  private ValueType valueType;
  private Boolean isSavedToDatabase;
  private Boolean isVaryingByYear;
  
  public enum ValueType {
    PERCENTAGE,
    CURRENCY,
    INTEGER,
    STRING
  }

}
