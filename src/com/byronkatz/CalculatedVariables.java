package com.byronkatz;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class CalculatedVariables {

  public static final int NUM_OF_MONTHS_IN_YEAR = 12;
  public static final double RESIDENTIAL_DEPRECIATION_YEARS = 27.5;
  public static final int YEARLY = 1;
  public static final int MONTHLY = 2;

  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();

  //calculated variables
  private Map<Integer, Map<String, Float>> calculatedValuesMap;
  private Map<String, Float> calculatedContentValues;

  //input variables
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
  private double monthlyInterestRate;



  public CalculatedVariables() {

    //Get the singleton dataController
    assignVariables();
  }

  private void assignVariables() {
    totalPurchaseValue = dataController.getValueAsFloat(DatabaseAdapter.TOTAL_PURCHASE_VALUE);
    estimatedRentPayments = dataController.getValueAsFloat(DatabaseAdapter.ESTIMATED_RENT_PAYMENTS);
    realEstateAppreciationRate = dataController.getValueAsFloat(DatabaseAdapter.REAL_ESTATE_APPRECIATION_RATE);
    vacancyRate = dataController.getValueAsFloat(DatabaseAdapter.VACANCY_AND_CREDIT_LOSS_RATE);
    initialYearlyGeneralExpenses = dataController.getValueAsFloat(DatabaseAdapter.INITIAL_YEARLY_GENERAL_EXPENSES);
    inflationRate = dataController.getValueAsFloat(DatabaseAdapter.INFLATION_RATE);
    marginalTaxRate = dataController.getValueAsFloat(DatabaseAdapter.MARGINAL_TAX_RATE);
    buildingValue = dataController.getValueAsFloat(DatabaseAdapter.BUILDING_VALUE);
    requiredRateOfReturn = dataController.getValueAsFloat(DatabaseAdapter.REQUIRED_RATE_OF_RETURN);
    yearlyInterestRate = dataController.getValueAsFloat(DatabaseAdapter.YEARLY_INTEREST_RATE);
    numOfCompoundingPeriods = dataController.getValueAsInteger(DatabaseAdapter.NUMBER_OF_COMPOUNDING_PERIODS);
    sellingBrokerRate = dataController.getValueAsFloat(DatabaseAdapter.SELLING_BROKER_RATE);
    generalSaleExpenses = dataController.getValueAsFloat(DatabaseAdapter.GENERAL_SALE_EXPENSES);
    downPayment = dataController.getValueAsFloat(DatabaseAdapter.DOWN_PAYMENT);
    fixupCosts = dataController.getValueAsFloat(DatabaseAdapter.FIX_UP_COSTS);
    propertyTaxRate = dataController.getValueAsFloat(DatabaseAdapter.PROPERTY_TAX_RATE);
    principalOwed = totalPurchaseValue - downPayment;
    initialYearlyPropertyTax = totalPurchaseValue * propertyTaxRate;
    monthlyInterestRate = yearlyInterestRate / NUM_OF_MONTHS_IN_YEAR;

    //calculated variables
    calculatedValuesMap = dataController.getCalculatedValuesList();
  }

  public void crunchCalculation() {

    /*note: many of the equations below are calculated using monthly variables.  This is done
     * when the reality of the equation is monthly.  For example, in the final summation of
     * NPV, the equation is (income - outlay) / (1 + discountRate/12)^numberOfMonthsAtPoint.
     * In other situations, such as taxes, they are only assessed yearly so their "reality" is yearly.
     * Sorry if that is a bad terminology, I am writing this at 6:30 in the morning!
     * Think about that a bit before making changes.
     */
    assignVariables();

    double firstDay = downPayment + generalSaleExpenses + fixupCosts;

    double yearlyNPVSummation = 0.0;
    double yearlyAfterTaxCashFlow = 0.0;
    double yearlyBeforeTaxCashFlow = 0.0;
    double yearlyTaxes = 0.0;
    double yearlyPrincipalPaid = 0.0;
    double yearlyDepreciation = buildingValue / RESIDENTIAL_DEPRECIATION_YEARS;
    double yearlyMortgagePayment = NUM_OF_MONTHS_IN_YEAR * getMortgagePayment();
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


    for (int year = 1; year <= yearlyCompoundingPeriods; year++) {

      calculatedContentValues = new HashMap<String, Float>();

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
      pastYearAmountOutstanding = getPrincipalOutstandingAtPoint(prevYearMonthCPModifier);
      currentYearAmountOutstanding = getPrincipalOutstandingAtPoint(monthCPModifier);
      yearlyPrincipalPaid = pastYearAmountOutstanding - currentYearAmountOutstanding;
      taxableIncome = (yearlyBeforeTaxCashFlow + yearlyPrincipalPaid - yearlyDepreciation);
      // doesn't make sense to tax negative income...but should this be used to offset taxes? hmmm...
      if (taxableIncome <= 0) {
        taxableIncome = 0.0;
      }
      yearlyTaxes = taxableIncome * marginalTaxRate;

      yearlyAfterTaxCashFlow = yearlyBeforeTaxCashFlow - yearlyTaxes;

      //add this year's atcf to the graph data object
      calculatedContentValues.put(AnalysisGraph.GraphType.ATCF.getGraphName(), (float)yearlyAfterTaxCashFlow);

      yearlyDiscountRateDivisor = Math.pow(1 + monthlyRequiredRateOfReturn, monthCPModifier);
      yearlyNPVSummation += yearlyAfterTaxCashFlow / yearlyDiscountRateDivisor;

      //equity reversion portion
      monthlyREARIncrementer = Math.pow(1 + monthlyRealEstateAppreciationRate, monthCPModifier);
      double projectedValueOfHomeAtSale = totalPurchaseValue * monthlyREARIncrementer;
      double brokerCut = projectedValueOfHomeAtSale * sellingBrokerRate;
      monthlyIRIncrementer = Math.pow(1 + monthlyInflationRate, monthCPModifier);
      double inflationAdjustedSellingExpenses = generalSaleExpenses * monthlyIRIncrementer;
      double principalOwedAtSale = getPrincipalOutstandingAtPoint(monthCPModifier);
      //How many years do I take depreciation?
      double accumulatingDepreciation = yearlyDepreciation * year;
      double taxesDueAtSale = (projectedValueOfHomeAtSale - totalPurchaseValue + accumulatingDepreciation)
          * marginalTaxRate;
      double ater = projectedValueOfHomeAtSale - brokerCut - 
          inflationAdjustedSellingExpenses - principalOwedAtSale - taxesDueAtSale;

      //add this year's ater to the graph data object
      calculatedContentValues.put(AnalysisGraph.GraphType.ATER.getGraphName(), (float) ater);

      double adjustedAter = ater / Math.pow(1 + monthlyRequiredRateOfReturn,monthCPModifier);
      double npvAccumulator = -firstDay + yearlyNPVSummation + adjustedAter;

      //add this year's NPV to the graph data object
      calculatedContentValues.put(AnalysisGraph.GraphType.NPV.getGraphName(), (float) npvAccumulator);

      //put this year's data into a wrapper hashMap
      calculatedValuesMap.put(year, calculatedContentValues);
    }

    //put the data in its place in the dataController
    dataController.setCalculatedValuesList(calculatedValuesMap);
  }



  public static String displayCurrency(Double value) {
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    return currencyFormatter.format(value);
  }

  public static String displayCurrency(Float value) {
    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    return currencyFormatter.format(value);
  }

  public double getMortgagePayment() {
    double a = (monthlyInterestRate + 1);
    double b = java.lang.Math.pow(a, numOfCompoundingPeriods);

    double mortgageEquation = (monthlyInterestRate/(1-(1/ b)));

    return principalOwed * mortgageEquation;
  }

  public double getInterestPaymentAtPoint() {

    return principalOwed * monthlyInterestRate;
  }

  public double getAccumulatedInterestPaymentsAtPoint (int compoundingPeriodDesired) {
    double monthlyInterestRate = yearlyInterestRate / NUM_OF_MONTHS_IN_YEAR;

    double mp = getMortgagePayment();

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


  public double getPrincipalPaymentAtPoint (int compoundingPeriodDesired) {

    double mp = getMortgagePayment();

    double pOutstanding = getPrincipalOutstandingAtPoint(compoundingPeriodDesired);
    double principalPaymentAtPoint = mp - (pOutstanding * monthlyInterestRate);
    return principalPaymentAtPoint;
  }

  public double getPrincipalOutstandingAtPoint (int compoundingPeriodDesired) {

    double mp = getMortgagePayment();
    double a = monthlyInterestRate+1;

    double princpalOutstandingAtPoint =(Math.pow(a,compoundingPeriodDesired) * principalOwed) -
        ( mp *  (((a - Math.pow(a,compoundingPeriodDesired) )/ -monthlyInterestRate) + 1));

    return princpalOutstandingAtPoint;
  }

  public double getTotalPaymentsMadeAtPoint (int compoundingPeriodDesired) {

    return compoundingPeriodDesired * getMortgagePayment();
  }

  public double getMonthlyRentalIncomeAtPoint (int compoundingPeriod) {
    return estimatedRentPayments * Math.pow((1 + inflationRate),((compoundingPeriod / NUM_OF_MONTHS_IN_YEAR)-1));
  }

  public double getPropertyTaxAtPoint (int compoundingPeriod) {
    return initialYearlyPropertyTax * Math.pow((1 + realEstateAppreciationRate),(compoundingPeriod-1));
  }


}

