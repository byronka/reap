package com.byronkatz;

public class CalculatedVariables {

  private static final int NUM_OF_MONTHS_IN_YEAR = 12;
  private static final double RESIDENTIAL_DEPRECIATION_YEARS = 27.5;
  private static final double IRR_ACCURACY = 0.001;
  
  private CalculatedVariables() {
  }

  public static double getMortgagePayment(double principalOwed, double monthlyInterestRate,
              int numOfCompoundingPeriods) {
    
    double a = (monthlyInterestRate + 1);
    double b = java.lang.Math.pow(a,numOfCompoundingPeriods);

    double mortgageEquation = (monthlyInterestRate/(1-(1/ b)));

    return principalOwed * mortgageEquation;
  }

  public static double getInterestPaymentAtPoint(double principalOwed, double monthlyInterestRate) {

    return principalOwed * monthlyInterestRate;
  }

  public static double getAccumulatedInterestPaymentsAtPoint (double principalOwed, double monthlyInterestRate, 
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

  public static double getNPV(double estimatedRentPayments, double realEstateAppreciationRate, 
      double vacancyRate,      double yearlyGeneralExpenses, double inflationRate,
      double marginalTaxRate, double principalOwed,      int monthlyCompoundingPeriodDesired, 
      double buildingValue, double requiredRateOfReturn, double yearlyInterestRate, 
      int numOfCompoundingPeriods, double sellingBrokerRate, double generalSaleExpenses, 
      double downPayment, double totalPurchaseValue, double fixupCosts,
      double initialYearlyPropertyTax) {
    
    double firstDay = downPayment + generalSaleExpenses + fixupCosts;
        
    double yearlyNPVSummation = 0.0;
    double yearlyAfterTaxCashFlow = 0.0;
    double yearlyBeforeTaxCashFlow = 0.0;
    double yearlyTaxes = 0.0;
    double yearlyPrincipalPaid = 0.0;
    double yearlyDepreciation = buildingValue / 27.5;
    double monthlyInterestRate = yearlyInterestRate / 12;
    double yearlyMortgagePayment = 12 * getMortgagePayment(principalOwed, monthlyInterestRate, 
        numOfCompoundingPeriods);
    double taxableIncome = 0.0;
    double yearlyCompoundingPeriodDesired = monthlyCompoundingPeriodDesired / 12;
    double yearlyPropertyTax = 0.0;
    for (int n = 1; n < yearlyCompoundingPeriodDesired; n++) {
      // cashflowIn - cashflowOut
      yearlyPropertyTax = initialYearlyPropertyTax * Math.pow(1 + realEstateAppreciationRate,n);
      yearlyBeforeTaxCashFlow = 12 * estimatedRentPayments * (1-vacancyRate) 
          * Math.pow(1 + realEstateAppreciationRate,n)
          - (yearlyGeneralExpenses + yearlyPropertyTax + yearlyMortgagePayment)* Math.pow(1 + inflationRate,n);
      //next year's yearlyAmountOutstanding minus this year's
      yearlyPrincipalPaid = getPrincipalOutstandingAtPoint(
            principalOwed, monthlyInterestRate, numOfCompoundingPeriods, (n - 1) * 12) -
            getPrincipalOutstandingAtPoint(principalOwed, monthlyInterestRate, 
                numOfCompoundingPeriods, n  * 12);
      taxableIncome = (yearlyBeforeTaxCashFlow + yearlyPrincipalPaid - yearlyDepreciation);
      if (taxableIncome <= 0) {
        taxableIncome = 0.0;
      }
      yearlyTaxes = taxableIncome * marginalTaxRate;
      
      yearlyAfterTaxCashFlow = yearlyBeforeTaxCashFlow - yearlyTaxes;
      
      //should the n below be n or n+1?
      yearlyNPVSummation += yearlyAfterTaxCashFlow/Math.pow(1+requiredRateOfReturn, n);
    }
    
    double projectedValueOfHomeAtSale = totalPurchaseValue * 
        Math.pow(1 + realEstateAppreciationRate,yearlyCompoundingPeriodDesired);
    double brokerCut = projectedValueOfHomeAtSale * sellingBrokerRate;
    double inflationAdjustedSellingExpenses = generalSaleExpenses * 
        Math.pow(1 + inflationRate, yearlyCompoundingPeriodDesired);
    double principalOwedAtSale = getPrincipalOutstandingAtPoint(principalOwed, 
        monthlyInterestRate, numOfCompoundingPeriods, monthlyCompoundingPeriodDesired);
    //How many years do I take depreciation?
    double accumulatingDepreciation = yearlyDepreciation * yearlyCompoundingPeriodDesired;
    double taxesDueAtSale = (projectedValueOfHomeAtSale - totalPurchaseValue + accumulatingDepreciation)
        * marginalTaxRate;
    double ater = projectedValueOfHomeAtSale - brokerCut - 
        inflationAdjustedSellingExpenses - principalOwedAtSale - taxesDueAtSale;
        
    double npvAccumulator = firstDay + yearlyNPVSummation + ater;
   
    return npvAccumulator;
  }
  
  public static double getPrincipalPaymentAtPoint (double principalOwed, double monthlyInterestRate, 
      int numOfCompoundingPeriods,int compoundingPeriodDesired) {

    double mp = getMortgagePayment(principalOwed, monthlyInterestRate, numOfCompoundingPeriods);

    double pOutstanding = getPrincipalOutstandingAtPoint(principalOwed, monthlyInterestRate, numOfCompoundingPeriods, compoundingPeriodDesired);
    double principalPaymentAtPoint = mp - (pOutstanding * monthlyInterestRate);
    return principalPaymentAtPoint;
  }

  public static double getPrincipalOutstandingAtPoint (double principalOwed, double monthlyInterestRate, 
      int numOfCompoundingPeriods, int compoundingPeriodDesired) {

    double mp = getMortgagePayment(principalOwed, monthlyInterestRate, numOfCompoundingPeriods);
    double a = monthlyInterestRate+1;
    
    double princpalOutstandingAtPoint =(Math.pow(a,compoundingPeriodDesired) * principalOwed) -
        ( mp *  (((a - Math.pow(a,compoundingPeriodDesired) )/ -monthlyInterestRate) + 1));

    return princpalOutstandingAtPoint;
  }

  public static double getTotalPaymentsMadeAtPoint (double principalOwed, double monthlyInterestRate, 
      int numOfCompoundingPeriods,int compoundingPeriodDesired) {
    
    return compoundingPeriodDesired * getMortgagePayment(principalOwed, monthlyInterestRate, numOfCompoundingPeriods);
  }

  public static double getMonthlyRentalIncomeAtPoint (double initialRent, double inflationRate, int compoundingPeriod) {
    return initialRent * Math.pow((1 + inflationRate),((compoundingPeriod / NUM_OF_MONTHS_IN_YEAR)-1));
  }

  public static double getPropertyTaxAtPoint (double initialPropertyTax, double realEstateAppreciation, int compoundingPeriod) {
    return initialPropertyTax * Math.pow((1 + realEstateAppreciation),(compoundingPeriod-1));
  }

}

