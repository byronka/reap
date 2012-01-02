package com.byronkatz.reap.general;

public enum ValueEnum {

  //saved to database
  TOTAL_PURCHASE_VALUE ("total purchase value", ValueType.CURRENCY, true, false),
  YEARLY_INTEREST_RATE("yearly interest rate", ValueType.PERCENTAGE, true, false),
  BUILDING_VALUE("building value", ValueType.CURRENCY, true, false),
  NUMBER_OF_COMPOUNDING_PERIODS("number of compounding periods", ValueType.INTEGER, true, false),
  INFLATION_RATE("inflation rate", ValueType.PERCENTAGE, true, false),
  DOWN_PAYMENT("down payment", ValueType.CURRENCY, true, false),
  STREET_ADDRESS("street address", ValueType.STRING, true, false),
  CITY("city", ValueType.STRING, true, false),
  STATE_INITIALS("State", ValueType.STRING, true, false),
  ESTIMATED_RENT_PAYMENTS("estimated rent payments", ValueType.CURRENCY, true, false),
  REAL_ESTATE_APPRECIATION_RATE("real estate appreciation rate", ValueType.PERCENTAGE, true, false),
  YEARLY_HOME_INSURANCE("yearly home insurance", ValueType.CURRENCY, true, false),
  PROPERTY_TAX_RATE("property tax rate", ValueType.PERCENTAGE, true, false),
  LOCAL_MUNICIPAL_FEES("local municipal fees", ValueType.CURRENCY, true, false),
  VACANCY_AND_CREDIT_LOSS_RATE("vacancy and credit loss rate", ValueType.PERCENTAGE, true, false),
  INITIAL_YEARLY_GENERAL_EXPENSES("initial yearly general expenses", ValueType.CURRENCY, true, false),
  MARGINAL_TAX_RATE("marginal tax rate", ValueType.PERCENTAGE, true, false),
  SELLING_BROKER_RATE("selling broker rate", ValueType.PERCENTAGE, true, false),
  GENERAL_SALE_EXPENSES("general sale expenses", ValueType.CURRENCY, true, false),
  REQUIRED_RATE_OF_RETURN("required rate of return", ValueType.PERCENTAGE, true, false),
  FIX_UP_COSTS("fix up costs", ValueType.CURRENCY, true, false),
  CLOSING_COSTS("closing costs", ValueType.CURRENCY, true, false), 
  
  //not saved to database
  NPV  ("Net present value", ValueType.CURRENCY, false, true),
  ATER ("After Tax Equity Reversion", ValueType.CURRENCY, false, true),
  ATCF ("After Tax Cash Flow", ValueType.CURRENCY, false, true),
  TAXES_DUE_AT_SALE ("taxes due at sale", ValueType.CURRENCY, false, true), 
  SELLING_EXPENSES ("inflation adjusted selling expenses", ValueType.CURRENCY, false, true), 
  BROKER_CUT_OF_SALE ("Broker cut at sale", ValueType.CURRENCY, false, true), 
  PROJECTED_HOME_VALUE ("projected home value at sale time", ValueType.CURRENCY, false, true), 
  TAXABLE_INCOME ("yearly taxable income", ValueType.CURRENCY, false, true), 
  YEARLY_PRINCIPAL_PAID ("yearly principal paid", ValueType.CURRENCY, false, true), 
  CURRENT_AMOUNT_OUTSTANDING ("current amount outstanding", ValueType.CURRENCY, false, true), 
  YEARLY_GENERAL_EXPENSES ("yearly general expenses", ValueType.CURRENCY, false, true), 
  YEARLY_INCOME ("yearly taxable income", ValueType.CURRENCY, false, true),  
  YEARLY_PROPERTY_TAX ("yearly property tax", ValueType.CURRENCY, false, true),  
  YEARLY_MORTGAGE_PAYMENT ("yearly mortgage payment", ValueType.CURRENCY, false, false),  
  MONTHLY_MORTGAGE_PAYMENT ("monthly mortgage payment", ValueType.CURRENCY, false, false),  
  ACCUM_INTEREST ("accumulated interest paid", ValueType.CURRENCY, false, true),
  YEARLY_INTEREST_PAID ("yearly interest paid", ValueType.CURRENCY, false, true);
  
  private ValueEnum(String valueText, ValueType valueType, 
      Boolean isSavedToDatabase, Boolean isVaryingByYear) {
    this.valueText = valueText;
    this.valueType = valueType;
    this.isSavedToDatabase = isSavedToDatabase;
    this.isVaryingByYear = isVaryingByYear;
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

  public Boolean isVaryingByYear() {
    return isVaryingByYear;
  }

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
