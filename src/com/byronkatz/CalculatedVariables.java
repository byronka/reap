package com.byronkatz;

public class CalculatedVariables {

  private static final int NUM_OF_MONTHS_IN_YEAR = 12;
  private static final double RESIDENTIAL_DEPRECIATION_YEARS = 27.5;
  public static final int YEARLY = 1;
  public static final int MONTHLY = 2;
  
  private GraphDataObject npvGraphDataObject;
  private GraphDataObject aterGraphDataObject;
  private GraphDataObject atcfGraphDataObject;

  //input variables
  private DataController dataController;
  private double totalPurchaseValue;
  private double estimatedRentPayments;
  private double realEstateAppreciationRate;
  private double vacancyRate;
  private double initialYearlyGeneralExpenses;
  private double inflationRate;
  private double marginalTaxRate;
  private double buildingValue;
  private double requiredRateOfReturn;
  private double yearlyInterestRate;
  private int numOfCompoundingPeriods;
  private double sellingBrokerRate;
  private double generalSaleExpenses;
  private double downPayment;
  private double fixupCosts;
  private double propertyTaxRate;
  private double principalOwed;
  private double initialYearlyPropertyTax;

  public CalculatedVariables() {
    npvGraphDataObject  = new GraphDataObject("NPV Graph");
    aterGraphDataObject = new GraphDataObject("ATER Graph");
    atcfGraphDataObject = new GraphDataObject("ATCF Graph");
    //Get the singleton dataController
    dataController = RealEstateMarketAnalysisApplication.getInstance().getDataController();
    assignVariables();
  }

  private void assignVariables() {
    totalPurchaseValue = Double.valueOf(
        dataController.getValue(DatabaseAdapter.TOTAL_PURCHASE_VALUE));
    estimatedRentPayments = Double.valueOf(
        dataController.getValue(DatabaseAdapter.ESTIMATED_RENT_PAYMENTS));
    realEstateAppreciationRate = Double.valueOf(
        dataController.getValue(DatabaseAdapter.REAL_ESTATE_APPRECIATION_RATE));
    vacancyRate = Double.valueOf(
        dataController.getValue(DatabaseAdapter.VACANCY_AND_CREDIT_LOSS_RATE));
    initialYearlyGeneralExpenses = Double.valueOf(
        dataController.getValue(DatabaseAdapter.INITIAL_YEARLY_GENERAL_EXPENSES));
    inflationRate = Double.valueOf(
        dataController.getValue(DatabaseAdapter.INFLATION_RATE));
    marginalTaxRate = Double.valueOf(
        dataController.getValue(DatabaseAdapter.MARGINAL_TAX_RATE));
    buildingValue = Double.valueOf(
        dataController.getValue(DatabaseAdapter.BUILDING_VALUE));
    requiredRateOfReturn = Double.valueOf(
        dataController.getValue(DatabaseAdapter.REQUIRED_RATE_OF_RETURN));
    yearlyInterestRate = Double.valueOf(
        dataController.getValue(DatabaseAdapter.YEARLY_INTEREST_RATE));
    numOfCompoundingPeriods = Integer.valueOf(
        dataController.getValue(DatabaseAdapter.NUMBER_OF_COMPOUNDING_PERIODS));
    sellingBrokerRate = Double.valueOf(
        dataController.getValue(DatabaseAdapter.SELLING_BROKER_RATE));
    generalSaleExpenses = Double.valueOf(
        dataController.getValue(DatabaseAdapter.GENERAL_SALE_EXPENSES));
    downPayment = Double.valueOf(
        dataController.getValue(DatabaseAdapter.DOWN_PAYMENT));
    fixupCosts = Double.valueOf(
        dataController.getValue(DatabaseAdapter.FIX_UP_COSTS));
    propertyTaxRate = Double.valueOf(
        dataController.getValue(DatabaseAdapter.PROPERTY_TAX_RATE));
    principalOwed = totalPurchaseValue - downPayment;
    initialYearlyPropertyTax = totalPurchaseValue * propertyTaxRate;

  }


  public void crunchCalculation() {

    /*note: many of the equations below are calculated using monthly variables.  This is done
     * when the reality of the equation is monthly.  For example, in the final summation of
     * NPV, the equation is (income - outlay) / (1 + discountRate/12)^numberOfMonthsAtPoint.
     * In other situations, such as taxes, they are only assessed yearly so their "reality" is yearly.
     * Sorry if that is a bad terminology, I am writing this at 6:30 in the morning!
     * Think about that a bit before making changes.
     */

    double firstDay = downPayment + generalSaleExpenses + fixupCosts;

    double yearlyNPVSummation = 0.0;
    double yearlyAfterTaxCashFlow = 0.0;
    double yearlyBeforeTaxCashFlow = 0.0;
    double yearlyTaxes = 0.0;
    double yearlyPrincipalPaid = 0.0;
    double yearlyDepreciation = buildingValue / RESIDENTIAL_DEPRECIATION_YEARS;
    double monthlyInterestRate = yearlyInterestRate / NUM_OF_MONTHS_IN_YEAR;
    double yearlyMortgagePayment = NUM_OF_MONTHS_IN_YEAR * getMortgagePayment(principalOwed, monthlyInterestRate, 
        numOfCompoundingPeriods);
    double taxableIncome = 0.0;
    double yearlyCompoundingPeriods = numOfCompoundingPeriods / NUM_OF_MONTHS_IN_YEAR;
    double yearlyPropertyTax = 0.0;
    double currentYearAmountOutstanding = 0.0;
    double pastYearAmountOutstanding = 0.0;
    double yearlyIncome = 0;
    double yearlyOutlay = 0;
    double monthlyREARIncrementer = 0.0;
    double grossYearlyIncome = 0.0;
    double netYearlyIncome = 0.0;
    double monthlyIRIncrementer = 0.0;
    double yearlyGeneralExpenses = 0.0;
    double monthlyRealEstateAppreciationRate = realEstateAppreciationRate / NUM_OF_MONTHS_IN_YEAR;
    double monthlyRequiredRateOfReturn = requiredRateOfReturn / NUM_OF_MONTHS_IN_YEAR;
    double monthlyInflationRate = inflationRate / NUM_OF_MONTHS_IN_YEAR;
    int monthCPModifier = 0;
    double yearlyDiscountRateDivisor = 0.0;
    int prevYearMonthCPModifier = 0;


    for (int year = 1; year < yearlyCompoundingPeriods; year++) {

      // cashflowIn - cashflowOut
      monthCPModifier = year * NUM_OF_MONTHS_IN_YEAR;
      prevYearMonthCPModifier = (year - 1) * NUM_OF_MONTHS_IN_YEAR;
      monthlyREARIncrementer = Math.pow(1 + monthlyRealEstateAppreciationRate,prevYearMonthCPModifier);
      yearlyPropertyTax = initialYearlyPropertyTax * monthlyREARIncrementer; 
      grossYearlyIncome = estimatedRentPayments * NUM_OF_MONTHS_IN_YEAR;
      netYearlyIncome = (1 - vacancyRate) * grossYearlyIncome;
      yearlyIncome = netYearlyIncome * monthlyREARIncrementer; 
      monthlyIRIncrementer = Math.pow(1 + monthlyInflationRate, prevYearMonthCPModifier); 
      yearlyGeneralExpenses = initialYearlyGeneralExpenses * monthlyIRIncrementer;
      yearlyOutlay = yearlyPropertyTax + yearlyMortgagePayment + yearlyGeneralExpenses;
      yearlyBeforeTaxCashFlow = yearlyIncome - yearlyOutlay;
      
      //next year's yearlyAmountOutstanding minus this year's
      pastYearAmountOutstanding = getPrincipalOutstandingAtPoint(
          principalOwed, monthlyInterestRate, numOfCompoundingPeriods, prevYearMonthCPModifier);
      currentYearAmountOutstanding = getPrincipalOutstandingAtPoint(principalOwed, monthlyInterestRate, 
          numOfCompoundingPeriods, monthCPModifier);
      yearlyPrincipalPaid = pastYearAmountOutstanding - currentYearAmountOutstanding;
      taxableIncome = (yearlyBeforeTaxCashFlow + yearlyPrincipalPaid - yearlyDepreciation);
      // doesn't make sense to tax negative income...but should this be used to offset taxes? hmmm...
      if (taxableIncome <= 0) {
        taxableIncome = 0.0;
      }
      yearlyTaxes = taxableIncome * marginalTaxRate;

      yearlyAfterTaxCashFlow = yearlyBeforeTaxCashFlow - yearlyTaxes;
      //add this year's atcf to the graph data object
      atcfGraphDataObject.addYearlyFunctionValue(year, yearlyAfterTaxCashFlow);
      yearlyDiscountRateDivisor = Math.pow(1 + monthlyRequiredRateOfReturn, monthCPModifier);
      yearlyNPVSummation += yearlyAfterTaxCashFlow / yearlyDiscountRateDivisor;

      //equity reversion portion
      monthlyREARIncrementer = Math.pow(1 + monthlyRealEstateAppreciationRate, monthCPModifier);
      double projectedValueOfHomeAtSale = totalPurchaseValue * monthlyREARIncrementer;
      double brokerCut = projectedValueOfHomeAtSale * sellingBrokerRate;
      monthlyIRIncrementer = Math.pow(1 + monthlyInflationRate, monthCPModifier);
      double inflationAdjustedSellingExpenses = generalSaleExpenses * monthlyIRIncrementer;
      double principalOwedAtSale = getPrincipalOutstandingAtPoint(principalOwed, 
          monthlyInterestRate, numOfCompoundingPeriods, monthCPModifier);
      //How many years do I take depreciation?
      double accumulatingDepreciation = yearlyDepreciation * year;
      double taxesDueAtSale = (projectedValueOfHomeAtSale - totalPurchaseValue + accumulatingDepreciation)
          * marginalTaxRate;
      double ater = projectedValueOfHomeAtSale - brokerCut - 
          inflationAdjustedSellingExpenses - principalOwedAtSale - taxesDueAtSale;
      //add this year's ater to the graph data object
      aterGraphDataObject.addYearlyFunctionValue(year, ater);
      double adjustedAter = ater / Math.pow(1 + monthlyRequiredRateOfReturn,monthCPModifier);
      double npvAccumulator = -firstDay + yearlyNPVSummation + adjustedAter;
      //add this year's NPV to the graph data object
      npvGraphDataObject.addYearlyFunctionValue(year, npvAccumulator);

    }
  }

  public GraphDataObject getNpvGraphDataObject() {
    return npvGraphDataObject;
  }

  public GraphDataObject getAterGraphDataObject() {
    return aterGraphDataObject;
  }

  public GraphDataObject getAtcfGraphDataObject() {
    return atcfGraphDataObject;
  }

  
  
  
  
  private static double getMortgagePayment(double principalOwed, double monthlyInterestRate,
      int numOfCompoundingPeriods) {

    double a = (monthlyInterestRate + 1);
    double b = java.lang.Math.pow(a,numOfCompoundingPeriods);

    double mortgageEquation = (monthlyInterestRate/(1-(1/ b)));

    return principalOwed * mortgageEquation;
  }

  private static double getInterestPaymentAtPoint(double principalOwed, double monthlyInterestRate) {

    return principalOwed * monthlyInterestRate;
  }

  private static double getAccumulatedInterestPaymentsAtPoint (double principalOwed, double monthlyInterestRate, 
      int numOfCompoundingPeriods, int compoundingPeriodDesired) {

    double mp = getMortgagePayment(principalOwed, monthlyInterestRate, numOfCompoundingPeriods);

    if (principalOwed < 0.0) {
      principalOwed = 0.0;
    }
    if (monthlyInterestRate < 0.0) {
      monthlyInterestRate = 0.0;
    }
    if (compoundingPeriodDesired < 0) {
      compoundingPeriodDesired = 0;
    }
    if (compoundingPeriodDesired > numOfCompoundingPeriods) {
      compoundingPeriodDesired = numOfCompoundingPeriods;
    }
    double c = monthlyInterestRate+1;
    double d = Math.pow(c, compoundingPeriodDesired);
    double e = (1-d)/(1-c);
    double f = mp/monthlyInterestRate;
    double g = compoundingPeriodDesired + 1;

    double accumInterestPaymentAtPoint = monthlyInterestRate*(principalOwed*(e+d)+(f)*(c*g-(e + d))-(mp*g));

    return accumInterestPaymentAtPoint;
  }

  private static double getNPV(double estimatedRentPayments, double realEstateAppreciationRate, 
      double vacancyRate,      double initialYearlyGeneralExpenses, double inflationRate,
      double marginalTaxRate, double principalOwed,      int monthlyCompoundingPeriodDesired, 
      double buildingValue, double requiredRateOfReturn, double yearlyInterestRate, 
      int numOfCompoundingPeriods, double sellingBrokerRate, double generalSaleExpenses, 
      double downPayment, double totalPurchaseValue, double fixupCosts,
      double initialYearlyPropertyTax) {

    /*note: many of the equations below are calculated using monthly variables.  This is done
     * when the reality of the equation is monthly.  For example, in the final summation of
     * NPV, the equation is (income - outlay) / (1 + discountRate/12)^numberOfMonthsAtPoint.
     * In other situations, such as taxes, they are only assessed yearly so their "reality" is yearly.
     * Sorry if that is a bad terminology, I am writing this at 6:30 in the morning!
     * Think about that a bit before making changes.
     */

    double firstDay = downPayment + generalSaleExpenses + fixupCosts;

    double yearlyNPVSummation = 0.0;
    double yearlyAfterTaxCashFlow = 0.0;
    double yearlyBeforeTaxCashFlow = 0.0;
    double yearlyTaxes = 0.0;
    double yearlyPrincipalPaid = 0.0;
    double yearlyDepreciation = buildingValue / RESIDENTIAL_DEPRECIATION_YEARS;
    double monthlyInterestRate = yearlyInterestRate / NUM_OF_MONTHS_IN_YEAR;
    double yearlyMortgagePayment = NUM_OF_MONTHS_IN_YEAR * getMortgagePayment(principalOwed, monthlyInterestRate, 
        numOfCompoundingPeriods);
    double taxableIncome = 0.0;
    double yearlyCompoundingPeriodDesired = monthlyCompoundingPeriodDesired / NUM_OF_MONTHS_IN_YEAR;
    double yearlyPropertyTax = 0.0;
    double currentYearAmountOutstanding = 0.0;
    double pastYearAmountOutstanding = 0.0;
    double yearlyIncome = 0;
    double yearlyOutlay = 0;
    double monthlyREARIncrementer = 0.0;
    double grossYearlyIncome = 0.0;
    double netYearlyIncome = 0.0;
    double monthlyIRIncrementer = 0.0;
    double yearlyGeneralExpenses = 0.0;
    double monthlyRealEstateAppreciationRate = realEstateAppreciationRate / NUM_OF_MONTHS_IN_YEAR;
    double monthlyRequiredRateOfReturn = requiredRateOfReturn / NUM_OF_MONTHS_IN_YEAR;
    double monthlyInflationRate = inflationRate / NUM_OF_MONTHS_IN_YEAR;
    int monthCPModifier = 0;
    double yearlyDiscountRateDivisor = 0.0;
    int prevYearMonthCPModifier = 0;

    for (int n = 1; n < yearlyCompoundingPeriodDesired; n++) {
      // cashflowIn - cashflowOut
      monthCPModifier = n * NUM_OF_MONTHS_IN_YEAR;
      prevYearMonthCPModifier = (n - 1) * NUM_OF_MONTHS_IN_YEAR;
      monthlyREARIncrementer = Math.pow(1 + monthlyRealEstateAppreciationRate,prevYearMonthCPModifier);
      yearlyPropertyTax = initialYearlyPropertyTax * monthlyREARIncrementer; 
      grossYearlyIncome = estimatedRentPayments * NUM_OF_MONTHS_IN_YEAR;
      netYearlyIncome = (1 - vacancyRate) * grossYearlyIncome;
      yearlyIncome = netYearlyIncome * monthlyREARIncrementer; 
      monthlyIRIncrementer = Math.pow(1 + monthlyInflationRate, prevYearMonthCPModifier); 
      yearlyGeneralExpenses = initialYearlyGeneralExpenses * monthlyIRIncrementer;
      yearlyOutlay = yearlyPropertyTax + yearlyMortgagePayment + yearlyGeneralExpenses;
      yearlyBeforeTaxCashFlow = yearlyIncome - yearlyOutlay;
      //next year's yearlyAmountOutstanding minus this year's
      pastYearAmountOutstanding = getPrincipalOutstandingAtPoint(
          principalOwed, monthlyInterestRate, numOfCompoundingPeriods, prevYearMonthCPModifier);
      currentYearAmountOutstanding = getPrincipalOutstandingAtPoint(principalOwed, monthlyInterestRate, 
          numOfCompoundingPeriods, monthCPModifier);
      yearlyPrincipalPaid = pastYearAmountOutstanding - currentYearAmountOutstanding;
      taxableIncome = (yearlyBeforeTaxCashFlow + yearlyPrincipalPaid - yearlyDepreciation);
      // doesn't make sense to tax negative income...but should this be used to offset taxes? hmmm...
      if (taxableIncome <= 0) {
        taxableIncome = 0.0;
      }
      yearlyTaxes = taxableIncome * marginalTaxRate;

      yearlyAfterTaxCashFlow = yearlyBeforeTaxCashFlow - yearlyTaxes;
      yearlyDiscountRateDivisor = Math.pow(1 + monthlyRequiredRateOfReturn, monthCPModifier);
      yearlyNPVSummation += yearlyAfterTaxCashFlow / yearlyDiscountRateDivisor;
    }

    monthlyREARIncrementer = Math.pow(1 + monthlyRealEstateAppreciationRate, monthlyCompoundingPeriodDesired);
    double projectedValueOfHomeAtSale = totalPurchaseValue * monthlyREARIncrementer;
    double brokerCut = projectedValueOfHomeAtSale * sellingBrokerRate;
    monthlyIRIncrementer = Math.pow(1 + monthlyInflationRate, monthlyCompoundingPeriodDesired);
    double inflationAdjustedSellingExpenses = generalSaleExpenses * monthlyIRIncrementer;
    double principalOwedAtSale = getPrincipalOutstandingAtPoint(principalOwed, 
        monthlyInterestRate, numOfCompoundingPeriods, monthlyCompoundingPeriodDesired);
    //How many years do I take depreciation?
    double accumulatingDepreciation = yearlyDepreciation * yearlyCompoundingPeriodDesired;
    double taxesDueAtSale = (projectedValueOfHomeAtSale - totalPurchaseValue + accumulatingDepreciation)
        * marginalTaxRate;
    double ater = projectedValueOfHomeAtSale - brokerCut - 
        inflationAdjustedSellingExpenses - principalOwedAtSale - taxesDueAtSale;
    double adjustedAter = ater / Math.pow(1 + monthlyRequiredRateOfReturn,monthlyCompoundingPeriodDesired);
    double npvAccumulator = -firstDay + yearlyNPVSummation + adjustedAter;

    return npvAccumulator;
  }


  private static double getPrincipalPaymentAtPoint (double principalOwed, double monthlyInterestRate, 
      int numOfCompoundingPeriods,int compoundingPeriodDesired) {

    double mp = getMortgagePayment(principalOwed, monthlyInterestRate, numOfCompoundingPeriods);

    double pOutstanding = getPrincipalOutstandingAtPoint(principalOwed, monthlyInterestRate, numOfCompoundingPeriods, compoundingPeriodDesired);
    double principalPaymentAtPoint = mp - (pOutstanding * monthlyInterestRate);
    return principalPaymentAtPoint;
  }

  private static double getPrincipalOutstandingAtPoint (double principalOwed, double monthlyInterestRate, 
      int numOfCompoundingPeriods, int compoundingPeriodDesired) {

    double mp = getMortgagePayment(principalOwed, monthlyInterestRate, numOfCompoundingPeriods);
    double a = monthlyInterestRate+1;

    double princpalOutstandingAtPoint =(Math.pow(a,compoundingPeriodDesired) * principalOwed) -
        ( mp *  (((a - Math.pow(a,compoundingPeriodDesired) )/ -monthlyInterestRate) + 1));

    return princpalOutstandingAtPoint;
  }

  private static double getTotalPaymentsMadeAtPoint (double principalOwed, double monthlyInterestRate, 
      int numOfCompoundingPeriods,int compoundingPeriodDesired) {

    return compoundingPeriodDesired * getMortgagePayment(principalOwed, monthlyInterestRate, numOfCompoundingPeriods);
  }

  private static double getMonthlyRentalIncomeAtPoint (double initialRent, double inflationRate, int compoundingPeriod) {
    return initialRent * Math.pow((1 + inflationRate),((compoundingPeriod / NUM_OF_MONTHS_IN_YEAR)-1));
  }

  private static double getPropertyTaxAtPoint (double initialPropertyTax, double realEstateAppreciation, int compoundingPeriod) {
    return initialPropertyTax * Math.pow((1 + realEstateAppreciation),(compoundingPeriod-1));
  }


}

