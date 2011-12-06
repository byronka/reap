package com.byronkatz;

import android.app.Activity;

public class CalculationObject {

  public static final int YEARLY = 1;
  public static final int MONTHLY = 2;
  
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
  private double monthlyInterestRate;
  private int numOfCompoundingPeriods;
  private int compoundingPeriodDesired;
  private double sellingBrokerRate;
  private double generalSaleExpenses;
  private double downPayment;
  private double fixupCosts;
  private double propertyTaxRate;
  private double principalOwed;
  private double initialYearlyPropertyTax;
  
  public CalculationObject() {
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
    monthlyInterestRate = yearlyInterestRate / 12;
    numOfCompoundingPeriods = Integer.valueOf(
        dataController.getValue(DatabaseAdapter.NUMBER_OF_COMPOUNDING_PERIODS));
    compoundingPeriodDesired = Integer.valueOf(
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

  public void calculateAndUpdateOutputStrings(Activity activity) {
    double mPayment = calculateMortgagePayment();
    double totalIPayments = calculateTotalIPayments();
    double totalPayments = calculateTotalPayments();
    double npv = calculateNPV();
    NPVGraphDataObject npvgdo = calculateNPVgdo();

  }

  private double calculateMortgagePayment() {
    double mortgagePayment = 0;

    mortgagePayment = CalculatedVariables.getMortgagePayment(principalOwed, monthlyInterestRate, 
        numOfCompoundingPeriods);
    return mortgagePayment;
  }

  private double calculateTotalIPayments() {
    double totalIPayments = 0;

    int compoundingPeriodDesired = numOfCompoundingPeriods;

    totalIPayments = CalculatedVariables.getAccumulatedInterestPaymentsAtPoint(principalOwed,
        monthlyInterestRate, numOfCompoundingPeriods, compoundingPeriodDesired);
    return totalIPayments;
  }

  private double calculateTotalPayments() {
    double totalPayments = 0;


    totalPayments = CalculatedVariables.getTotalPaymentsMadeAtPoint(principalOwed, monthlyInterestRate, 
        numOfCompoundingPeriods, compoundingPeriodDesired);
    return totalPayments;
  }

  private double calculateNPV() {
    double npv = 0;


    npv = CalculatedVariables.getNPV(estimatedRentPayments, realEstateAppreciationRate, 
        vacancyRate, initialYearlyGeneralExpenses, inflationRate, marginalTaxRate, principalOwed, 
        compoundingPeriodDesired, buildingValue, requiredRateOfReturn, yearlyInterestRate, 
        numOfCompoundingPeriods, sellingBrokerRate, generalSaleExpenses, downPayment, 
        totalPurchaseValue, fixupCosts, initialYearlyPropertyTax);
    return npv;
  }
  
  private NPVGraphDataObject calculateNPVgdo() {
    NPVGraphDataObject npvgdo;


    npvgdo = CalculatedVariables.getNPVGraphDataObject(estimatedRentPayments, realEstateAppreciationRate, 
        vacancyRate, initialYearlyGeneralExpenses, inflationRate, marginalTaxRate, principalOwed,
        buildingValue, requiredRateOfReturn, yearlyInterestRate, 
        numOfCompoundingPeriods, sellingBrokerRate, generalSaleExpenses, downPayment, 
        totalPurchaseValue, fixupCosts, initialYearlyPropertyTax);
    return npvgdo;
  }
}
