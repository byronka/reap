package com.byronkatz.reap.general;

import com.byronkatz.reap.R.string;


public enum ValueEnum {

  ATCF ( ValueType.CURRENCY, false, true, string.atcfHelpText , string.atcfTitleText),
  ATCF_ACCUMULATOR ( ValueType.CURRENCY, false, true, string.atcfAccumulatorHelpText, string.atcfAccumulatorTitleText),
  ATCF_NPV ( ValueType.CURRENCY, false, true, string.atcfNetPresentValueHelpText, string.atcfNetPresentValueTitleText),
  ATER ( ValueType.CURRENCY, false, true, string.aterHelpText, string.aterTitleText),
  ATER_PV (ValueType.CURRENCY, false, true, string.aterPresentValueHelpText, string.aterPresentValueTitleText),
  BROKER_CUT_OF_SALE ( ValueType.CURRENCY, false, true, string.brokerCutHelpText, string.brokerCutTitleText), 
  BUILDING_VALUE(ValueType.CURRENCY, true, false, string.buildingValueHelpText, string.buildingValueTitleText),
  CAP_RATE_ON_PURCHASE_VALUE (ValueType.PERCENTAGE, false, true, string.capRateOnPurchaseValueHelpText, string.capRateOnPurchaseValueTitleText),
  CAP_RATE_ON_PROJECTED_VALUE (ValueType.PERCENTAGE, false, true, string.capRateOnProjectedValueHelpText, string.capRateOnProjectedValueTitleText),
  CLOSING_COSTS( ValueType.CURRENCY, true, false, string.closingCostsHelpText, string.closingCostsTitleText), 
  COMMENTS( ValueType.STRING, true, false, string.commentsHelpText, string.commentsTitleText),
  DOWN_PAYMENT( ValueType.CURRENCY, true, false, string.downPaymentHelpText, string.downPaymentTitleText),
  ESTIMATED_RENT_PAYMENTS( ValueType.CURRENCY, true, false, string.estimatedRentPaymentsHelpText, string.estimatedRentPaymentsTitleText),
  EXTRA_YEARS (ValueType.INTEGER, true, false, string.extraYearsHelpText, string.extraYearsTitleText),
  FIX_UP_COSTS( ValueType.CURRENCY, true, false, string.fixupCostsHelpText, string.fixupCostsTitleText),
  GENERAL_SALE_EXPENSES( ValueType.CURRENCY, true, false, string.generalSaleExpensesHelpText, string.generalSaleExpensesTitleText),
  FIRST_DAY_COSTS ( ValueType.CURRENCY, false, true, string.firstDayCostsHelpText, string.firstDayCostsTitleText),
  LOAN_AMOUNT ( ValueType.CURRENCY, false, true, string.loanAmountHelpText, string.loanAmountTitleText),
  PRINCIPAL_PAYMENT ( ValueType.CURRENCY, false, true, string.principalPaymentHelpText, string.principalPaymentTitleText),
  INTEREST_PAYMENT ( ValueType.CURRENCY, false, true, string.interestPaymentHelpText, string.interestPaymentTitleText),
  INFLATION_RATE( ValueType.PERCENTAGE, true, false, string.inflationRateHelpText, string.inflationRateTitleText),
  INITIAL_YEARLY_GENERAL_EXPENSES( ValueType.CURRENCY, true, false, string.initialYearlyGeneralExpensesHelpText, string.initialYearlyGeneralExpensesTitleText),
  INITIAL_HOME_INSURANCE( ValueType.CURRENCY, true, false, string.initialHomeInsuranceHelpText, string.initialHomeInsuranceTitleText),
  LOCAL_MUNICIPAL_FEES( ValueType.CURRENCY, true, false, string.localMunicipalFeesHelpText, string.localMunicipalFeesTitleText),
  CURRENT_AMOUNT_OUTSTANDING ( ValueType.CURRENCY, false, true, string.currentAmountOutstandingHelpText, string.currentAmountOutstandingTitleText), 
  MARGINAL_TAX_RATE( ValueType.PERCENTAGE, true, false, string.marginalTaxRateHelpText, string.marginalTaxRateTitleText),
  MODIFIED_INTERNAL_RATE_OF_RETURN( ValueType.PERCENTAGE, false, true, string.modifiedInternalRateOfReturnHelpText, string.modifiedInternalRateOfReturnTitleText),
  MONTHLY_MORTGAGE_PAYMENT ( ValueType.CURRENCY, false, true, string.monthlyMortgagePaymentHelpText, string.monthlyMortgagePaymentTitleText),  
  INITIAL_VALUATION ( ValueType.CURRENCY, true, false, string.valuationHelpText, string.valuationTitleText),
  MONTHLY_AMOUNT_OWED ( ValueType.CURRENCY, false, true, string.monthlyAmountOwedHelpText, string.monthlyAmountOwedTitleText),  
  MONTHLY_INTEREST_ACCUMULATED ( ValueType.CURRENCY, false, true, string.monthlyInterestAccumulatedHelpText, string.monthlyInterestAccumulatedTitleText),  
  
  YEARLY_DEPRECIATION ( ValueType.CURRENCY, false, true, string.yearlyDepreciationHelpText, string.yearlyDepreciationTitleText),  
  MONTHS_UNTIL_RENT_STARTS ( ValueType.INTEGER, true, false, string.monthsUntilRentStartsHelpText, string.monthsUntilRentStartsTitleText),
  MONTHLY_RENT_FV ( ValueType.CURRENCY, false, true, string.monthlyRentFVHelpText, string.monthlyRentFVTitleText),
  NPV  ( ValueType.CURRENCY, false, true, string.netPresentValueHelpText, string.netPresentValueTitleText),
  NUMBER_OF_COMPOUNDING_PERIODS( ValueType.INTEGER, true, false, string.numberOfCompoundingPeriodsHelpText, string.numberOfCompoundingPeriodsTitleText),
  PRIVATE_MORTGAGE_INSURANCE ( ValueType.CURRENCY, true, false, string.privateMortgageInsuranceHelpText, string.privateMortgageInsuranceTitleText),
  PROJECTED_HOME_VALUE ( ValueType.CURRENCY, false, true, string.projectedHomeValueHelpText, string.projectedHomeValueTitleText), 
  PROPERTY_TAX( ValueType.CURRENCY, true, false, string.propertyTaxHelpText, string.propertyTaxTitleText),
  REAL_ESTATE_APPRECIATION_RATE( ValueType.PERCENTAGE, true, false, string.realEstateAppreciationRateHelpText, string.realEstateAppreciationRateTitleText),
  REQUIRED_RATE_OF_RETURN( ValueType.PERCENTAGE, true, false, string.requiredRateOfReturnHelpText, string.requiredRateOfReturnTitleText),
  SELLING_BROKER_RATE( ValueType.PERCENTAGE, true, false, string.sellingBrokerRateHelpText, string.sellingBrokerRateTitleText),
  SELLING_EXPENSES ( ValueType.CURRENCY, false, true, string.sellingExpensesHelpText, string.sellingExpensesTitleText), 
  STREET_ADDRESS( ValueType.STRING, true, false, string.streetAddressHelpText, string.streetAddressTitleText),
  STATE_INITIALS( ValueType.STRING, true, false, string.stateInitialsHelpText, string.stateInitialsTitleText),
  CITY(ValueType.STRING, true, false, string.cityHelpText, string.cityTitleText),
  TAXES_DUE_AT_SALE ( ValueType.CURRENCY, false, true, string.taxesDueAtSaleHelpText, string.taxesDueAtSaleTitleText), 
  TOTAL_PURCHASE_VALUE ( ValueType.CURRENCY, true, false, string.totalPurchaseValueHelpText, string.totalPurchaseValueTitleText),
  VACANCY_AND_CREDIT_LOSS_RATE( ValueType.PERCENTAGE, true, false, string.vacancyAndCreditLossRateHelpText, string.vacancyAndCreditLossRateTitleText),
  YEARLY_BEFORE_TAX_CASH_FLOW( ValueType.CURRENCY, false, true, string.yearlyBeforeTaxCashFlowHelpText, string.yearlyBeforeTaxCashFlowTitleText),
  YEARLY_GENERAL_EXPENSES ( ValueType.CURRENCY, false, true, string.yearlyGeneralExpensesHelpText, string.yearlyGeneralExpensesTitleText), 
  YEARLY_HOME_INSURANCE( ValueType.CURRENCY, false, true, string.yearlyHomeInsuranceHelpText, string.yearlyHomeInsuranceTitleText),
  YEARLY_INTEREST_PAID ( ValueType.CURRENCY, false, true, string.yearlyInterestPaidHelpText, string.yearlyInterestPaidTitleText),
  YEARLY_OPERATING_EXPENSES (ValueType.CURRENCY, false, true, string.yearlyOperatingExpensesHelpText, string.yearlyOperatingExpensesTitleText),
  YEARLY_ACCUM_INTEREST ( ValueType.CURRENCY, false, true, string.accumulatedInterestPaidHelpText, string.accumulatedInterestPaidTitleText), 
  YEARLY_INTEREST_RATE( ValueType.PERCENTAGE, true, false, string.yearlyInterestRateHelpText, string.yearlyInterestRateTitleText),
  YEARLY_MORTGAGE_PAYMENT (ValueType.CURRENCY, false, true, string.yearlyMortgagePaymentHelpText, string.yearlyMortgagePaymentTitleText), 
  YEARLY_MUNICIPAL_FEES (ValueType.CURRENCY, false, true, string.yearlyMunicipalFeesHelpText, string.yearlyMunicipalFeesTitleText),
  YEARLY_NET_OPERATING_INCOME (ValueType.CURRENCY, false, true, string.yearlyNetOperatingIncomeHelpText, string.yearlyNetOperatingIncomeTitleText),
  YEARLY_OUTLAY ( ValueType.CURRENCY, false, true, string.yearlyOutlayHelpText, string.yearlyOutlayTitleText),
  YEARLY_PRIVATE_MORTGAGE_INSURANCE (ValueType.CURRENCY, false, true, string.yearlyPrivateMortgageInsuranceHelpText, string.yearlyPrivateMortgageInsuranceTitleText),
  YEARLY_PRIVATE_MORTGAGE_INSURANCE_ACCUM (ValueType.CURRENCY, false, true, string.yearlyPrivateMortgageInsuranceAccumHelpText, string.yearlyPrivateMortgageInsuranceAccumTitleText),
  YEARLY_PRINCIPAL_PAID ( ValueType.CURRENCY, false, true, string.yearlyPrincipalPaidHelpText, string.yearlyPrincipalPaidTitleText), 
  YEARLY_PROPERTY_TAX ( ValueType.CURRENCY, false, true, string.yearlyPropertyTaxHelpText, string.yearlyPropertyTaxTitleText),
  YEARLY_INCOME ( ValueType.CURRENCY, false, true, string.yearlyIncomeHelpText, string.yearlyIncomeTitleText),
  GROSS_YEARLY_INCOME ( ValueType.CURRENCY, false, true, string.grossYearlyIncomeHelpText, string.grossYearlyIncomeTitleText),
  TAXABLE_INCOME ( ValueType.CURRENCY, false, true, string.taxableIncomeHelpText, string.taxableIncomeTitleText),
  YEARLY_TAX_ON_INCOME (ValueType.CURRENCY, false, true, string.yearlyTaxOnIncomeHelpText, string.yearlyTaxOnIncomeTitleText);

  private ValueEnum( ValueType valueType, 
      Boolean isSavedToDatabase, Boolean isVaryingByYear,
      Integer helpText, Integer titleText) {
    this.valueType = valueType;
    this.isSavedToDatabase = isSavedToDatabase;
    this.isVaryingByYear = isVaryingByYear;
    this.helpText = helpText;
    this.titleText = titleText;
  }
  
  public String toString() {
    
    return RealEstateAnalysisProcessorApplication.getAppResources().getString(titleText);
  }
  
  /**
   * This method returns the assigned type of value, such as integer, currency, percentage, or string
   * @return returns the kind of number this is
   */
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
