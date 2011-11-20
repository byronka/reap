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

  public static double getNPV(double estimatedRentPayments, double realEstateAppreciationRate, double vacancyRate,
      double yearlyGeneralExpenses, double inflationRate, double marginalTaxRate, double principalOwed,
      int compoundingPeriodDesired, double buildingValue, double requiredRateOfReturn, double monthlyInterestRate, 
      int numOfCompoundingPeriods, double sellingBrokerRate, double generalSaleExpenses, double downPayment) {

    //all variables should be yearly - this equation designed to work for yearly only - have not tested on monthly
    int yCpd = compoundingPeriodDesired / NUM_OF_MONTHS_IN_YEAR; //yearly cpd
    double yErp = estimatedRentPayments * NUM_OF_MONTHS_IN_YEAR;
    double yip = 0; //yearly interest paid
    double mp = getMortgagePayment(principalOwed, monthlyInterestRate, numOfCompoundingPeriods);
    double depr = buildingValue / RESIDENTIAL_DEPRECIATION_YEARS;
    double noi; // net operating income - temporary variable
    double atcf = 0.0; //after tax cash flow
    double tax = 0.0;  //tax
    double npv = 0.0;  //net present value
    double ater = 0.0; //after-tax equity reversion
        
    for (int period = 0; period < yCpd; period++) {
      noi = yErp * (1 - vacancyRate) * Math.pow(1 + realEstateAppreciationRate, period) - (yearlyGeneralExpenses * Math.pow(1+inflationRate, period));
      compoundingPeriodDesired = (period + 1) * 12; //used in getAccumulatedInterestPaymentsAtPoint
      yip = getAccumulatedInterestPaymentsAtPoint(principalOwed, monthlyInterestRate, numOfCompoundingPeriods, compoundingPeriodDesired) - yip;
      tax = marginalTaxRate * (noi - yip - depr);
      atcf = noi - (mp * NUM_OF_MONTHS_IN_YEAR) - tax;
      npv += atcf / Math.pow(1 + requiredRateOfReturn, period + 1);
    }
    
    double salePrice = buildingValue * Math.pow(1 + realEstateAppreciationRate, yCpd);
    double sellingExpenses = (sellingBrokerRate * salePrice) + (generalSaleExpenses * Math.pow(1+inflationRate,yCpd));
    double netSalePrice = salePrice - sellingExpenses;
    double accumulatedDepreciation = yCpd * depr; //check with tax accountant on this - how many years do I take depr
    double aterTax = marginalTaxRate * (netSalePrice - buildingValue + accumulatedDepreciation);
    double amountOutstanding = getPrincipalOutstandingAtPoint(principalOwed, monthlyInterestRate, numOfCompoundingPeriods, compoundingPeriodDesired);
    ater = salePrice - sellingExpenses - amountOutstanding - aterTax;

    npv = npv + (ater/ Math.pow(1 + requiredRateOfReturn, yCpd)) - downPayment;
    return npv;
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

