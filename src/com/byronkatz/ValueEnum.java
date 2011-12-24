package com.byronkatz;

public enum ValueEnum {

  TOTAL_PURCHASE_VALUE ("total purchase value"),
  YEARLY_INTEREST_RATE("yearly interest rate"),
  MONTHLY_INTEREST_RATE("monthly interest rate"),
  BUILDING_VALUE("building value"),
  NUMBER_OF_COMPOUNDING_PERIODS("number of compounding periods"),
  INFLATION_RATE("inflation rate"),
  PRIMARY_MORTGAGE_INSURANCE_RATE("primary mortgage insurance rate"),
  DOWN_PAYMENT("down payment"),
  STREET_ADDRESS("street address"),
  CITY("city"),
  STATE_INITIALS("state initials"),
  ESTIMATED_RENT_PAYMENTS("estimated rent payments"),
  REAL_ESTATE_APPRECIATION_RATE("real estate appreciation rate"),
  YEARLY_ALTERNATE_INVESTMENT_RETURN("yearly alternate investment return"),
  YEARLY_HOME_INSURANCE("yearly home insurance"),
  PROPERTY_TAX_RATE("property tax rate"),
  LOCAL_MUNICIPAL_FEES("local municipal fees"),
  VACANCY_AND_CREDIT_LOSS_RATE("vacancy and credit loss rate"),
  INITIAL_YEARLY_GENERAL_EXPENSES("initial yearly general expenses"),
  MARGINAL_TAX_RATE("marginal tax rate"),
  SELLING_BROKER_RATE("selling broker rate"),
  GENERAL_SALE_EXPENSES("general sale expenses"),
  REQUIRED_RATE_OF_RETURN("required rate of return"),
  FIX_UP_COSTS("fix up costs"),
  NPV  ("Net present value"),
  ATER ("After Tax Equity Reversion"),
  ATCF ("After Tax Cash Flow"),
  CLOSING_COSTS("closing costs");
  
  private ValueEnum(String valueText) {
    this.valueText = valueText;
  }
  
  public String toString() {
    return valueText;
  }

  private String valueText;
}
