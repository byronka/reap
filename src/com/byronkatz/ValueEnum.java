package com.byronkatz;

public enum ValueEnum {

  //saved to database
  TOTAL_PURCHASE_VALUE ("total purchase value", ValueType.CURRENCY, true),
  YEARLY_INTEREST_RATE("yearly interest rate", ValueType.PERCENTAGE, true),
  BUILDING_VALUE("building value", ValueType.CURRENCY, true),
  NUMBER_OF_COMPOUNDING_PERIODS("number of compounding periods", ValueType.CURRENCY, true),
  INFLATION_RATE("inflation rate", ValueType.PERCENTAGE, true),
  DOWN_PAYMENT("down payment", ValueType.CURRENCY, true),
  STREET_ADDRESS("street address", ValueType.STRING, true),
  CITY("city", ValueType.STRING, true),
  STATE_INITIALS("state initials", ValueType.STRING, true),
  ESTIMATED_RENT_PAYMENTS("estimated rent payments", ValueType.CURRENCY, true),
  REAL_ESTATE_APPRECIATION_RATE("real estate appreciation rate", ValueType.PERCENTAGE, true),
  YEARLY_HOME_INSURANCE("yearly home insurance", ValueType.CURRENCY, true),
  PROPERTY_TAX_RATE("property tax rate", ValueType.PERCENTAGE, true),
  LOCAL_MUNICIPAL_FEES("local municipal fees", ValueType.CURRENCY, true),
  VACANCY_AND_CREDIT_LOSS_RATE("vacancy and credit loss rate", ValueType.PERCENTAGE, true),
  INITIAL_YEARLY_GENERAL_EXPENSES("initial yearly general expenses", ValueType.CURRENCY, true),
  MARGINAL_TAX_RATE("marginal tax rate", ValueType.PERCENTAGE, true),
  SELLING_BROKER_RATE("selling broker rate", ValueType.PERCENTAGE, true),
  GENERAL_SALE_EXPENSES("general sale expenses", ValueType.CURRENCY, true),
  REQUIRED_RATE_OF_RETURN("required rate of return", ValueType.PERCENTAGE, true),
  FIX_UP_COSTS("fix up costs", ValueType.CURRENCY, true),
  CLOSING_COSTS("closing costs", ValueType.CURRENCY, true), 
  
  //not saved to database
  NPV  ("Net present value", ValueType.CURRENCY, false),
  ATER ("After Tax Equity Reversion", ValueType.CURRENCY, false),
  ATCF ("After Tax Cash Flow", ValueType.CURRENCY, false),
  TAXES_DUE_AT_SALE ("taxes due at sale", ValueType.CURRENCY, false), 
  SELLING_EXPENSES ("inflation adjusted selling expenses", ValueType.CURRENCY, false), 
  BROKER_CUT_OF_SALE ("Broker cut at sale", ValueType.CURRENCY, false), 
  PROJECTED_HOME_VALUE ("projected home value at sale time", ValueType.CURRENCY, false), 
  TAXABLE_INCOME ("yearly taxable income", ValueType.CURRENCY, false), 
  YEARLY_PRINCIPAL_PAID ("yearly principal paid", ValueType.CURRENCY, false), 
  CURRENT_AMOUNT_OUTSTANDING ("current amount outstanding", ValueType.CURRENCY, false), 
  YEARLY_GENERAL_EXPENSES ("yearly general expenses", ValueType.CURRENCY, false), 
  YEARLY_INCOME ("yearly taxable income", ValueType.CURRENCY, false),  
  YEARLY_PROPERTY_TAX ("yearly property tax", ValueType.CURRENCY, false),  
  YEARLY_MORTGAGE_PAYMENT ("yearly mortgage payment", ValueType.CURRENCY, false),  
  MONTHLY_MORTGAGE_PAYMENT ("monthly mortgage payment", ValueType.CURRENCY, false),  
  ACCUM_INTEREST ("accumulated interest paid", ValueType.CURRENCY, false),
  YEARLY_INTEREST_PAID ("yearly interest paid", ValueType.CURRENCY, false);
  
  private ValueEnum(String valueText, ValueType valueType, boolean isSavedToDatabase) {
    this.valueText = valueText;
    this.valueType = valueType;
    this.isSavedToDatabase = isSavedToDatabase;
  }
  
  public String toString() {
    return valueText;
  }
  
  public ValueType getType() {
    return valueType;
  }

  public Boolean isSavedToDatabase() {
    return isSavedToDatabase;
  }

  public void setIsSavedToDatabase(Boolean isSavedToDatabase) {
    this.isSavedToDatabase = isSavedToDatabase;
  }

  private String valueText;
  private ValueType valueType;
  private Boolean isSavedToDatabase;
  
  enum ValueType {
    PERCENTAGE,
    CURRENCY,
    STRING
  }
}
