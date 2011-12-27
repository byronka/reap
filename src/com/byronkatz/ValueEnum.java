package com.byronkatz;

public enum ValueEnum {

  TOTAL_PURCHASE_VALUE ("total purchase value", ValueType.CURRENCY),
  YEARLY_INTEREST_RATE("yearly interest rate", ValueType.PERCENTAGE),
  BUILDING_VALUE("building value", ValueType.CURRENCY),
  NUMBER_OF_COMPOUNDING_PERIODS("number of compounding periods", ValueType.CURRENCY),
  INFLATION_RATE("inflation rate", ValueType.PERCENTAGE),
  PRIMARY_MORTGAGE_INSURANCE_RATE("primary mortgage insurance rate", ValueType.CURRENCY),
  DOWN_PAYMENT("down payment", ValueType.CURRENCY),
  STREET_ADDRESS("street address", ValueType.STRING),
  CITY("city", ValueType.STRING),
  STATE_INITIALS("state initials", ValueType.STRING),
  ESTIMATED_RENT_PAYMENTS("estimated rent payments", ValueType.CURRENCY),
  REAL_ESTATE_APPRECIATION_RATE("real estate appreciation rate", ValueType.PERCENTAGE),
  YEARLY_HOME_INSURANCE("yearly home insurance", ValueType.CURRENCY),
  PROPERTY_TAX_RATE("property tax rate", ValueType.PERCENTAGE),
  LOCAL_MUNICIPAL_FEES("local municipal fees", ValueType.CURRENCY),
  VACANCY_AND_CREDIT_LOSS_RATE("vacancy and credit loss rate", ValueType.PERCENTAGE),
  INITIAL_YEARLY_GENERAL_EXPENSES("initial yearly general expenses", ValueType.CURRENCY),
  MARGINAL_TAX_RATE("marginal tax rate", ValueType.PERCENTAGE),
  SELLING_BROKER_RATE("selling broker rate", ValueType.PERCENTAGE),
  GENERAL_SALE_EXPENSES("general sale expenses", ValueType.CURRENCY),
  REQUIRED_RATE_OF_RETURN("required rate of return", ValueType.PERCENTAGE),
  FIX_UP_COSTS("fix up costs", ValueType.CURRENCY),
  NPV  ("Net present value", ValueType.CURRENCY),
  ATER ("After Tax Equity Reversion", ValueType.CURRENCY),
  ATCF ("After Tax Cash Flow", ValueType.CURRENCY),
  CLOSING_COSTS("closing costs", ValueType.CURRENCY), 

  //new stuff!

  TAXES_DUE_AT_SALE ("taxes due at sale", ValueType.CURRENCY), 
  SELLING_EXPENSES ("inflation adjusted selling expenses", ValueType.CURRENCY), 
  BROKER_CUT_OF_SALE ("Broker cut at sale", ValueType.CURRENCY), 
  PROJECTED_HOME_VALUE ("projected home value at sale time", ValueType.CURRENCY), 
  TAXABLE_INCOME ("yearly taxable income", ValueType.CURRENCY), 
  YEARLY_PRINCIPAL_PAID ("yearly principal paid", ValueType.CURRENCY), 
  CURRENT_AMOUNT_OUTSTANDING ("current amount outstanding", ValueType.CURRENCY), 
  YEARLY_GENERAL_EXPENSES ("yearly general expenses", ValueType.CURRENCY), 
  YEARLY_INCOME ("yearly taxable income", ValueType.CURRENCY),  
  YEARLY_PROPERTY_TAX ("yearly property tax", ValueType.CURRENCY),  
  YEARLY_MORTGAGE_PAYMENT ("yearly mortgage payment", ValueType.CURRENCY),  
  MONTHLY_MORTGAGE_PAYMENT ("monthly mortgage payment", ValueType.CURRENCY),  
  ACCUM_INTEREST ("accumulated interest paid", ValueType.CURRENCY),
  YEARLY_INTEREST_PAID ("yearly interest paid", ValueType.CURRENCY);
  
  private ValueEnum(String valueText, ValueType valueType) {
    this.valueText = valueText;
    this.valueType = valueType;
  }
  
  public String toString() {
    return valueText;
  }
  
  public ValueType getType() {
    return valueType;
  }

  private String valueText;
  private ValueType valueType;
  
  enum ValueType {
    PERCENTAGE,
    CURRENCY,
    STRING
  }
}
