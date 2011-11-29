package com.byronkatz;

import android.app.Activity;

public class CalculationObject {

  
  public static void calculateAndUpdateOutputStrings(Activity activity) {
    double mPayment = calculateMortgagePayment();
    double totalIPayments = calculateTotalIPayments();
    double totalPayments = calculateTotalPayments();
    double npv = calculateNPV();
    activity.formatAndInsertInOutputStrings(mPayment, totalIPayments, totalPayments, npv);
  }
  
  private static double calculateMortgagePayment() {
    double mortgagePayment = 0;

    double purchaseValue = Double.valueOf(inputFields.get("total purchase value").getValue());
    double downPayment = Double.valueOf(inputFields.get("down payment").getValue());
    double principalOwed = purchaseValue - downPayment;
    double monthlyInterestRate = Double.valueOf(inputFields.get("monthly interest rate").getValue());
    int numOfCompoundingPeriods = Integer.valueOf(inputFields.get("number of compounding periods on loan").getValue());
    
    mortgagePayment = CalculatedVariables.getMortgagePayment(principalOwed, monthlyInterestRate, 
        numOfCompoundingPeriods);
    return mortgagePayment;
  }
  
  private static double calculateTotalIPayments() {
    double totalIPayments = 0;

    double purchaseValue = Double.valueOf(inputFields.get("total purchase value").getValue());
    double downPayment = Double.valueOf(inputFields.get("down payment").getValue());
    double principalOwed = purchaseValue - downPayment;
    double monthlyInterestRate = Double.valueOf(inputFields.get("monthly interest rate").getValue());
    int numOfCompoundingPeriods = Integer.valueOf(inputFields.get("number of compounding periods on loan").getValue());
    int compoundingPeriodDesired = numOfCompoundingPeriods;
    
    totalIPayments = CalculatedVariables.getAccumulatedInterestPaymentsAtPoint(principalOwed,
        monthlyInterestRate, numOfCompoundingPeriods, compoundingPeriodDesired);
    return totalIPayments;
  }
  
  private static double calculateTotalPayments() {
    double purchaseValue = Double.valueOf(inputFields.get("total purchase value").getValue());
    double downPayment = Double.valueOf(inputFields.get("down payment").getValue());
    double principalOwed = purchaseValue - downPayment;
    double monthlyInterestRate = Double.valueOf(inputFields.get("monthly interest rate").getValue());
    int numOfCompoundingPeriods = Integer.valueOf(inputFields.get("number of compounding periods on loan").getValue());
    int compoundingPeriodDesired = numOfCompoundingPeriods;
    double totalPayments = 0;

    
    totalPayments = CalculatedVariables.getTotalPaymentsMadeAtPoint(principalOwed, monthlyInterestRate, 
        numOfCompoundingPeriods, compoundingPeriodDesired);
    return totalPayments;
  }
  
  private static double calculateNPV() {
    double npv = 0;
    double totalPurchaseValue = Double.valueOf(inputFields.get("total purchase value").getValue());
    double estimatedRentPayments = Double.valueOf(inputFields.get("estimated rent payments").getValue());
    double realEstateAppreciationRate = Double.valueOf(inputFields.get("real estate appreciation rate").getValue());
    double vacancyRate = Double.valueOf(inputFields.get("vacancy and credit loss rate").getValue());
    double initialYearlyGeneralExpenses = Double.valueOf(inputFields.get("initial yearly general expenses").getValue());
    double inflationRate = Double.valueOf(inputFields.get("inflation rate").getValue());
    double marginalTaxRate = Double.valueOf(inputFields.get("marginal tax rate").getValue());
    double buildingValue = Double.valueOf(inputFields.get("building value").getValue());
    double requiredRateOfReturn = Double.valueOf(inputFields.get("required rate of return").getValue());
    double yearlyInterestRate = Double.valueOf(inputFields.get("yearly interest rate").getValue());
    int numOfCompoundingPeriods = Integer.valueOf(inputFields.get("number of compounding periods on loan").getValue());
    int compoundingPeriodDesired = numOfCompoundingPeriods;
    double sellingBrokerRate = Double.valueOf(inputFields.get("selling broker rate").getValue());
    double generalSaleExpenses = Double.valueOf(inputFields.get("general sale expenses").getValue());
    double downPayment = Double.valueOf(inputFields.get("down payment").getValue());
    double fixupCosts = Double.valueOf(inputFields.get("fix-up costs").getValue());
    double propertyTaxRate = Double.valueOf(inputFields.get("property tax rate").getValue());
    double principalOwed = totalPurchaseValue - downPayment;
    double initialYearlyPropertyTax = totalPurchaseValue * propertyTaxRate;

    npv = CalculatedVariables.getNPV(estimatedRentPayments, realEstateAppreciationRate, 
        vacancyRate, initialYearlyGeneralExpenses, inflationRate, marginalTaxRate, principalOwed, 
        compoundingPeriodDesired, buildingValue, requiredRateOfReturn, yearlyInterestRate, 
        numOfCompoundingPeriods, sellingBrokerRate, generalSaleExpenses, downPayment, 
        totalPurchaseValue, fixupCosts, initialYearlyPropertyTax);
    return npv;
  }
}
