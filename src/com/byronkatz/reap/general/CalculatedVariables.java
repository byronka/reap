package com.byronkatz.reap.general;



public class CalculatedVariables {

  public static final int NUM_OF_MONTHS_IN_YEAR = 12;
  public static final Float RESIDENTIAL_DEPRECIATION_YEARS = 27.5f;
  public static final int YEARLY = 1;
  public static final int MONTHLY = 2;
  
  
  //// WORK AREA
  
  private static Float firstDay = 0.0f;
  private static Float yearlyNPVSummation = 0.0f;
  private static Float yearlyAfterTaxCashFlow = 0.0f;
  private static Float yearlyBeforeTaxCashFlow = 0.0f;
  private static Float yearlyTaxes = 0.0f;
  private static Float yearlyPrincipalPaid = 0.0f;
  private static Float yearlyDepreciation = 0.0f;
  private static Float monthlyMortgagePayment = 0.0f;
  private static Float yearlyMortgagePayment = 0.0f;
  private static Float taxableIncome = 0.0f;
  private static Integer yearlyCompoundingPeriods = 0;
  private static Float yearlyPropertyTax = 0.0f;
  private static Float currentYearAmountOutstanding = 0.0f;
  private static Float pastYearAmountOutstanding = 0.0f;
  private static Float yearlyIncome = 0.0f;
  private static Float yearlyOutlay = 0.0f;
  private static Float monthlyREARIncrementer = 0.0f;
  private static Float grossYearlyIncome = 0.0f;
  private static Float netYearlyIncome = 0.0f;
  private static Float monthlyIRIncrementer = 0.0f;
  private static Float yearlyGeneralExpenses = 0.0f;
  private static Float monthlyRealEstateAppreciationRate = 0.0f;
  private static Float monthlyRequiredRateOfReturn = 0.0f;
  private static Float monthlyInflationRate = 0.0f;
  private static int monthCPModifier = 0;
  private static Float yearlyDiscountRateDivisor = 0.0f;
  private static int prevYearMonthCPModifier = 0;
  private static Float projectedValueOfHomeAtSale = 0.0f;
  private static Float brokerCut = 0.0f;
  private static Float inflationAdjustedSellingExpenses = 0.0f;
  private static Float accumulatingDepreciation = 0.0f;
  private static Float taxesDueAtSale = 0.0f;
  private static Float ater = 0.0f;
  private static Float adjustedAter = 0.0f;
  private static Float npvAccumulator = 0.0f;
  private static Float accumulatedInterest = 0.0f;
  private static Float accumulatedInterestPreviousYear = 0.0f;
  private static Float yearlyInterestPaid = 0.0f;
  ///WORK AREA END
  
  

  private static final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();

  //calculated variables
//  private static Map<Integer, Map<String, Float>> calculatedValuesMap;
//  private static Map<String, Float> calculatedContentValues;

  //input variables
  private static Float totalPurchaseValue;
  private static Float estimatedRentPayments;
  private static Float realEstateAppreciationRate;
  private static Float vacancyRate;
  private static Float initialYearlyGeneralExpenses;
  private static Float inflationRate;
  private static Float marginalTaxRate;
  private static Float buildingValue;
  private static Float requiredRateOfReturn;
  private static Float yearlyInterestRate;
  private static int numOfCompoundingPeriods;
  private static Float sellingBrokerRate;
  private static Float generalSaleExpenses;
  private static Float downPayment;
  private static Float fixupCosts;
  private static Float propertyTaxRate;
  private static Float principalOwed;
  private static Float initialYearlyPropertyTax;
  private static Float monthlyInterestRate;
  
  

  private static void assignVariables() {
    firstDay = 0.0f;
    yearlyNPVSummation = 0.0f;
    yearlyAfterTaxCashFlow = 0.0f;
    yearlyBeforeTaxCashFlow = 0.0f;
    yearlyTaxes = 0.0f;
    yearlyPrincipalPaid = 0.0f;
    yearlyDepreciation = 0.0f;
    monthlyMortgagePayment = 0.0f;
    yearlyMortgagePayment = 0.0f;
    taxableIncome = 0.0f;
    yearlyCompoundingPeriods = 0;
    yearlyPropertyTax = 0.0f;
    currentYearAmountOutstanding = 0.0f;
    pastYearAmountOutstanding = 0.0f;
    yearlyIncome = 0.0f;
    yearlyOutlay = 0.0f;
    monthlyREARIncrementer = 0.0f;
    grossYearlyIncome = 0.0f;
    netYearlyIncome = 0.0f;
    monthlyIRIncrementer = 0.0f;
    yearlyGeneralExpenses = 0.0f;
    monthlyRealEstateAppreciationRate = 0.0f;
    monthlyRequiredRateOfReturn = 0.0f;
    monthlyInflationRate = 0.0f;
    monthCPModifier = 0;
    yearlyDiscountRateDivisor = 0.0f;
    prevYearMonthCPModifier = 0;
    projectedValueOfHomeAtSale = 0.0f;
    brokerCut = 0.0f;
    inflationAdjustedSellingExpenses = 0.0f;
    accumulatingDepreciation = 0.0f;
    taxesDueAtSale = 0.0f;
    ater = 0.0f;
    adjustedAter = 0.0f;
    npvAccumulator = 0.0f;
    accumulatedInterest = 0.0f;
    accumulatedInterestPreviousYear = 0.0f;
    yearlyInterestPaid = 0.0f;
    
    totalPurchaseValue = dataController.getValueAsFloat(ValueEnum.TOTAL_PURCHASE_VALUE);
    estimatedRentPayments = dataController.getValueAsFloat(ValueEnum.ESTIMATED_RENT_PAYMENTS);
    realEstateAppreciationRate = dataController.getValueAsFloat(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);
    vacancyRate = dataController.getValueAsFloat(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE);
    initialYearlyGeneralExpenses = dataController.getValueAsFloat(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES);
    inflationRate = dataController.getValueAsFloat(ValueEnum.INFLATION_RATE);
    marginalTaxRate = dataController.getValueAsFloat(ValueEnum.MARGINAL_TAX_RATE);
    buildingValue = dataController.getValueAsFloat(ValueEnum.BUILDING_VALUE);
    requiredRateOfReturn = dataController.getValueAsFloat(ValueEnum.REQUIRED_RATE_OF_RETURN);
    yearlyInterestRate = dataController.getValueAsFloat(ValueEnum.YEARLY_INTEREST_RATE);
    numOfCompoundingPeriods = dataController.getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS).intValue();
    sellingBrokerRate = dataController.getValueAsFloat(ValueEnum.SELLING_BROKER_RATE);
    generalSaleExpenses = dataController.getValueAsFloat(ValueEnum.GENERAL_SALE_EXPENSES);
    downPayment = dataController.getValueAsFloat(ValueEnum.DOWN_PAYMENT);
    fixupCosts = dataController.getValueAsFloat(ValueEnum.FIX_UP_COSTS);
    propertyTaxRate = dataController.getValueAsFloat(ValueEnum.PROPERTY_TAX_RATE);
    principalOwed = totalPurchaseValue - downPayment;
    initialYearlyPropertyTax = totalPurchaseValue * propertyTaxRate;
    monthlyInterestRate = yearlyInterestRate / NUM_OF_MONTHS_IN_YEAR;
    firstDay = downPayment + generalSaleExpenses + fixupCosts;
    yearlyDepreciation = buildingValue / RESIDENTIAL_DEPRECIATION_YEARS;
    monthlyMortgagePayment = getMortgagePayment();
    yearlyMortgagePayment = NUM_OF_MONTHS_IN_YEAR * monthlyMortgagePayment;
    dataController.setValueAsFloat(ValueEnum.MONTHLY_MORTGAGE_PAYMENT, monthlyMortgagePayment);
    dataController.setValueAsFloat(ValueEnum.YEARLY_MORTGAGE_PAYMENT, yearlyMortgagePayment);
    yearlyCompoundingPeriods = numOfCompoundingPeriods / NUM_OF_MONTHS_IN_YEAR;
    monthlyRealEstateAppreciationRate = realEstateAppreciationRate / NUM_OF_MONTHS_IN_YEAR;
    monthlyRequiredRateOfReturn = requiredRateOfReturn / NUM_OF_MONTHS_IN_YEAR;
    monthlyInflationRate = inflationRate / NUM_OF_MONTHS_IN_YEAR;
    grossYearlyIncome = estimatedRentPayments * NUM_OF_MONTHS_IN_YEAR;
    netYearlyIncome = (1 - vacancyRate) * grossYearlyIncome;
  }

  public static void crunchCalculation() {

    /*note: many of the equations below are calculated using monthly variables.  This is done
     * when the reality of the equation is monthly.  For example, in the final summation of
     * NPV, the equation is (income - outlay) / (1 + discountRate/12)^numberOfMonthsAtPoint.
     * In other situations, such as taxes, they are only assessed yearly so their "reality" is yearly.
     * Sorry if that is a bad terminology, I am writing this at 6:30 in the morning!
     * Think about that a bit before making changes.
     */
    assignVariables();

    for (int year = 1; year <= yearlyCompoundingPeriods; year++) {

      
      // cashflowIn - cashflowOut
      monthCPModifier = year * NUM_OF_MONTHS_IN_YEAR;
      prevYearMonthCPModifier = (year - 1) * NUM_OF_MONTHS_IN_YEAR;
      monthlyREARIncrementer = (float) Math.pow(1 + monthlyRealEstateAppreciationRate,prevYearMonthCPModifier);
      yearlyPropertyTax = initialYearlyPropertyTax * monthlyREARIncrementer; 
      dataController.setValueAsFloat(ValueEnum.YEARLY_PROPERTY_TAX, yearlyPropertyTax, year);

      yearlyIncome = netYearlyIncome * monthlyREARIncrementer; 
      dataController.setValueAsFloat(ValueEnum.YEARLY_INCOME, yearlyIncome, year);
      
      monthlyIRIncrementer = (float) Math.pow(1 + monthlyInflationRate, prevYearMonthCPModifier); 
      yearlyGeneralExpenses = initialYearlyGeneralExpenses * monthlyIRIncrementer;
      dataController.setValueAsFloat(ValueEnum.YEARLY_GENERAL_EXPENSES, yearlyGeneralExpenses, year);

      yearlyOutlay = yearlyPropertyTax + yearlyMortgagePayment + yearlyGeneralExpenses;
      yearlyBeforeTaxCashFlow = yearlyIncome - yearlyOutlay;

      //next year's yearlyAmountOutstanding minus this year's
      pastYearAmountOutstanding = getPrincipalOutstandingAtPoint(prevYearMonthCPModifier);
      currentYearAmountOutstanding = getPrincipalOutstandingAtPoint(monthCPModifier);
      dataController.setValueAsFloat(ValueEnum.CURRENT_AMOUNT_OUTSTANDING,
          currentYearAmountOutstanding, year);

      yearlyPrincipalPaid = pastYearAmountOutstanding - currentYearAmountOutstanding;
      dataController.setValueAsFloat(ValueEnum.YEARLY_PRINCIPAL_PAID, yearlyPrincipalPaid, year);

      taxableIncome = (yearlyBeforeTaxCashFlow + yearlyPrincipalPaid - yearlyDepreciation);
      
      // doesn't make sense to tax negative income...but should this be used to offset taxes? hmmm...
      if (taxableIncome <= 0) {
        taxableIncome = 0.0f;
      }
      dataController.setValueAsFloat(ValueEnum.TAXABLE_INCOME, taxableIncome, year);

      yearlyTaxes = taxableIncome * marginalTaxRate;

      yearlyAfterTaxCashFlow = yearlyBeforeTaxCashFlow - yearlyTaxes;

      dataController.setValueAsFloat(ValueEnum.ATCF, yearlyAfterTaxCashFlow, year);

      yearlyDiscountRateDivisor = (float) Math.pow(1 + monthlyRequiredRateOfReturn, monthCPModifier);
      yearlyNPVSummation += yearlyAfterTaxCashFlow / yearlyDiscountRateDivisor;

      //equity reversion portion
      monthlyREARIncrementer = (float) Math.pow(1 + monthlyRealEstateAppreciationRate, monthCPModifier);
      projectedValueOfHomeAtSale = totalPurchaseValue * monthlyREARIncrementer;
      dataController.setValueAsFloat(ValueEnum.PROJECTED_HOME_VALUE, 
          projectedValueOfHomeAtSale, year);

      brokerCut = projectedValueOfHomeAtSale * sellingBrokerRate;
      dataController.setValueAsFloat(ValueEnum.BROKER_CUT_OF_SALE, brokerCut, year);

      monthlyIRIncrementer = (float) Math.pow(1 + monthlyInflationRate, monthCPModifier);
      inflationAdjustedSellingExpenses = generalSaleExpenses * monthlyIRIncrementer;
      dataController.setValueAsFloat(ValueEnum.SELLING_EXPENSES, 
          inflationAdjustedSellingExpenses, year);

      //How many years do I take depreciation?
      accumulatingDepreciation = yearlyDepreciation * year;
      taxesDueAtSale = (projectedValueOfHomeAtSale - totalPurchaseValue + accumulatingDepreciation)
          * marginalTaxRate;
      dataController.setValueAsFloat(ValueEnum.TAXES_DUE_AT_SALE, taxesDueAtSale, year);

      ater = projectedValueOfHomeAtSale - brokerCut - 
          inflationAdjustedSellingExpenses - currentYearAmountOutstanding - taxesDueAtSale;

      adjustedAter = (float) (ater / Math.pow(1 + monthlyRequiredRateOfReturn,monthCPModifier));
      dataController.setValueAsFloat(ValueEnum.ATER, adjustedAter, year);
      npvAccumulator = -firstDay + yearlyNPVSummation + adjustedAter;

      //add this year's NPV to the graph data object
      dataController.setValueAsFloat(ValueEnum.NPV, npvAccumulator, year);
      
      accumulatedInterest = getAccumulatedInterestPaymentsAtPoint(monthCPModifier);
      dataController.setValueAsFloat(ValueEnum.ACCUM_INTEREST, accumulatedInterest, year);
      
      accumulatedInterestPreviousYear = getAccumulatedInterestPaymentsAtPoint(prevYearMonthCPModifier);
      yearlyInterestPaid = accumulatedInterest - accumulatedInterestPreviousYear;
      dataController.setValueAsFloat(ValueEnum.YEARLY_INTEREST_PAID, yearlyInterestPaid, year);

    }

  }

 


  public static Float getMortgagePayment() {
    Float a = (monthlyInterestRate + 1);
    Float b = (float) Math.pow(a, numOfCompoundingPeriods);

    Float mortgageEquation = (monthlyInterestRate/(1-(1/ b)));

    return principalOwed * mortgageEquation;
  }

  public static Float getInterestPaymentAtPoint() {

    return principalOwed * monthlyInterestRate;
  }

  public static Float getAccumulatedInterestPaymentsAtPoint (int compoundingPeriodDesired) {
    Float monthlyInterestRate = yearlyInterestRate / NUM_OF_MONTHS_IN_YEAR;

    Float mp = getMortgagePayment();

    if (principalOwed < 0.0) {
      principalOwed = 0.0f;
    }
    if (monthlyInterestRate < 0.0) {
      monthlyInterestRate = 0.0f;
    }
    if (compoundingPeriodDesired < 0) {
      compoundingPeriodDesired = 0;
    }
    if (compoundingPeriodDesired > numOfCompoundingPeriods) {
      compoundingPeriodDesired = numOfCompoundingPeriods;
    }
    Float c = monthlyInterestRate+1;
    Float d = (float) Math.pow(c, compoundingPeriodDesired);
    Float e = (1-d)/(1-c);
    Float f = mp/monthlyInterestRate;
    Integer g = compoundingPeriodDesired + 1;

    Float accumInterestPaymentAtPoint = monthlyInterestRate*(principalOwed*(e+d)+(f)*(c*g-(e + d))-(mp*g));

    return accumInterestPaymentAtPoint;
  }


  public static Float getPrincipalPaymentAtPoint (int compoundingPeriodDesired) {

    Float mp = getMortgagePayment();

    Float pOutstanding = getPrincipalOutstandingAtPoint(compoundingPeriodDesired);
    Float principalPaymentAtPoint = mp - (pOutstanding * monthlyInterestRate);
    return principalPaymentAtPoint;
  }

  public static Float getPrincipalOutstandingAtPoint (int compoundingPeriodDesired) {

    Float mp = getMortgagePayment();
    Float a = monthlyInterestRate+1;

    Float princpalOutstandingAtPoint = (float) ((Math.pow(a,compoundingPeriodDesired) * principalOwed) -
        ( mp *  (((a - Math.pow(a,compoundingPeriodDesired) )/ -monthlyInterestRate) + 1)));

    return princpalOutstandingAtPoint;
  }

  public static Float getTotalPaymentsMadeAtPoint (int compoundingPeriodDesired) {

    return compoundingPeriodDesired * getMortgagePayment();
  }

  public static Float getMonthlyRentalIncomeAtPoint (int compoundingPeriod) {
    return (float) (estimatedRentPayments * Math.pow((1 + inflationRate),((compoundingPeriod / NUM_OF_MONTHS_IN_YEAR)-1)));
  }

  public static Float getPropertyTaxAtPoint (int compoundingPeriod) {
    return (float) (initialYearlyPropertyTax * Math.pow((1 + realEstateAppreciationRate),(compoundingPeriod-1)));
  }


}

