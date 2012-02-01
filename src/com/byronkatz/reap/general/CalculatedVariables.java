package com.byronkatz.reap.general;

import com.byronkatz.reap.calculations.EquityReversion;
import com.byronkatz.reap.calculations.EstateValue;
import com.byronkatz.reap.calculations.ModifiedInternalRateOfReturn;
import com.byronkatz.reap.calculations.Mortgage;
import com.byronkatz.reap.calculations.MortgagePayment;



public class CalculatedVariables {

  public static final int NUM_OF_MONTHS_IN_YEAR = 12;
  public static final Float RESIDENTIAL_DEPRECIATION_YEARS = 27.5f;
  public static final int YEARLY = 1;
  public static final int MONTHLY = 2;
  public static final Float PMI_PERCENTAGE = 0.20f;
  private static Float firstDay = 0.0f;
  private static Float yearlyNPVSummation = 0.0f;
  private static Float yearlyDepreciation = 0.0f;
  private static Integer yearlyCompoundingPeriods = 0;
  private static Float grossYearlyIncome = 0.0f;
  private static Float netYearlyIncome = 0.0f;
  private static Float monthlyRealEstateAppreciationRate = 0.0f;
  private static Float monthlyRequiredRateOfReturn = 0.0f;
  private static Float monthlyInflationRate = 0.0f;
  private static Float npvAccumulator = 0.0f;
  private static Float atcfAccumulator = 0.0f;
  private static Float netCashOutValue = 0.0f;

  private static Float modifiedInternalRateOfReturn = 0.0f;

  private static ModifiedInternalRateOfReturn mirr;
  private static MortgagePayment mp;
  private static EstateValue estateValue;
  private static Mortgage mortgage;
  private static EquityReversion equityReversion;


  private static final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();

  //input variables
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
  private static Float generalSaleExpenses;
  private static Float downPayment;
  private static Float fixupCosts;
  private static Float propertyTax;



  private static void assignVariables() {
    firstDay = 0.0f;
    yearlyNPVSummation = 0.0f;
    yearlyDepreciation = 0.0f;
    yearlyCompoundingPeriods = 0;
    grossYearlyIncome = 0.0f;
    netYearlyIncome = 0.0f;
    monthlyRealEstateAppreciationRate = 0.0f;
    monthlyRequiredRateOfReturn = 0.0f;
    monthlyInflationRate = 0.0f;
    npvAccumulator = 0.0f;

    modifiedInternalRateOfReturn = 0.0f;
    atcfAccumulator = 0.0f;

    mirr = new ModifiedInternalRateOfReturn();
    estateValue = new EstateValue();
    mortgage = new Mortgage(estateValue);
    equityReversion = new EquityReversion(estateValue);
    yearlyInterestRate = mortgage.getYearlyInterestRate();
    downPayment = mortgage.getDownPayment();
    numOfCompoundingPeriods = mortgage.getNumberOfCompoundingPeriods();
    mp = mortgage.getMortgagePayment();

    
    estimatedRentPayments = dataController.getValueAsFloat(ValueEnum.ESTIMATED_RENT_PAYMENTS);
    realEstateAppreciationRate = dataController.getValueAsFloat(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);
    vacancyRate = dataController.getValueAsFloat(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE);
    initialYearlyGeneralExpenses = dataController.getValueAsFloat(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES);
    inflationRate = dataController.getValueAsFloat(ValueEnum.INFLATION_RATE);
    marginalTaxRate = dataController.getValueAsFloat(ValueEnum.MARGINAL_TAX_RATE);
    buildingValue = dataController.getValueAsFloat(ValueEnum.BUILDING_VALUE);
    requiredRateOfReturn = dataController.getValueAsFloat(ValueEnum.REQUIRED_RATE_OF_RETURN);
    generalSaleExpenses = dataController.getValueAsFloat(ValueEnum.GENERAL_SALE_EXPENSES);
    fixupCosts = dataController.getValueAsFloat(ValueEnum.FIX_UP_COSTS);
    propertyTax = dataController.getValueAsFloat(ValueEnum.PROPERTY_TAX);
    yearlyDepreciation = buildingValue / RESIDENTIAL_DEPRECIATION_YEARS;

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

    firstDay = downPayment + generalSaleExpenses + fixupCosts;

    modifiedInternalRateOfReturn = mirr.calculateMirr(0, requiredRateOfReturn, yearlyInterestRate, -firstDay, null);

    for (int year = 1; year <= yearlyCompoundingPeriods; year++) {

      netCashOutValue = 0.0f;
      final Integer monthCPModifier = year * NUM_OF_MONTHS_IN_YEAR;
      final Integer prevYearMonthCPModifier = (year - 1) * NUM_OF_MONTHS_IN_YEAR;

      final Float atcfNPVSummation = calculateYearlyRentalIncomeNPV(year, monthCPModifier, prevYearMonthCPModifier);
      dataController.setValueAsFloat(ValueEnum.ATCF_NPV, atcfNPVSummation, year);

      final Float adjustedAter = calculateAter(year, monthCPModifier);  
      dataController.setValueAsFloat(ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN, modifiedInternalRateOfReturn, year);

      npvAccumulator = -firstDay + atcfNPVSummation + adjustedAter;
      dataController.setValueAsFloat(ValueEnum.NPV, npvAccumulator, year);

      final Float accumulatedInterest = mortgage.getAccumulatedInterestPaymentsAtPoint(monthCPModifier);
      dataController.setValueAsFloat(ValueEnum.ACCUM_INTEREST, accumulatedInterest, year);

      final Float accumulatedInterestPreviousYear = mortgage.getAccumulatedInterestPaymentsAtPoint(prevYearMonthCPModifier);
      final Float yearlyInterestPaid = accumulatedInterest - accumulatedInterestPreviousYear;
      dataController.setValueAsFloat(ValueEnum.YEARLY_INTEREST_PAID, yearlyInterestPaid, year);

    }
  }

  private static Float calculateYearlyRentalIncomeNPV(int year, int monthCPModifier, int prevYearMonthCPModifier) {


    final Float yearlyAfterTaxCashFlow = calculateYearlyAfterTaxCashFlow(year, monthCPModifier, prevYearMonthCPModifier);
    atcfAccumulator += yearlyAfterTaxCashFlow;
    dataController.setValueAsFloat(ValueEnum.ATCF_ACCUMULATOR, atcfAccumulator, year);

    netCashOutValue += atcfAccumulator;

    modifiedInternalRateOfReturn = mirr.calculateMirr(
        year, requiredRateOfReturn, yearlyInterestRate, yearlyAfterTaxCashFlow, null);


    final Float yearlyDiscountRateDivisor = (float) Math.pow(1 + monthlyRequiredRateOfReturn, monthCPModifier);
    yearlyNPVSummation += yearlyAfterTaxCashFlow / yearlyDiscountRateDivisor;

    return yearlyNPVSummation;
  }

  private static Float calculateYearlyAfterTaxCashFlow(int year, int monthCPModifier, int prevYearMonthCPModifier) {

    final Float yearlyBeforeTaxCashFlow = calculateYearlyBeforeTaxCashFlow(year, prevYearMonthCPModifier);
    final Float taxableIncome = calculateTaxableIncome(year, monthCPModifier, prevYearMonthCPModifier, yearlyBeforeTaxCashFlow);

    final Float yearlyTaxes = taxableIncome * marginalTaxRate;
    final Float yearlyAfterTaxCashFlow = yearlyBeforeTaxCashFlow - yearlyTaxes;
    dataController.setValueAsFloat(ValueEnum.ATCF, yearlyAfterTaxCashFlow, year);

    return yearlyAfterTaxCashFlow;
  }


  private static Float calculateValueOfAter(Float projectedValueOfHomeAtSale, Float brokerCut,
      Float inflationAdjustedSellingExpenses, Float principalOutstandingAtSale, 
      int year, Float taxesDueAtSale) {
    
    Float ater = 0.0f;
    
    ater = projectedValueOfHomeAtSale - brokerCut - 
        inflationAdjustedSellingExpenses - principalOutstandingAtSale - taxesDueAtSale;
    dataController.setValueAsFloat(ValueEnum.ATER, ater, year);
    
    return ater;
  }

  private static Float calculateAter(int year, int monthCPModifier) {
    
    final Float brokerCut = equityReversion.calculateBrokerCutOfSale(year);
    final Float inflationAdjustedSellingExpenses = equityReversion.calculateSellingExpensesFutureValue(
        year, inflationRate);

    final Float taxesDueAtSale = equityReversion.calculateTaxesDueAtSale((yearlyDepreciation * year), year);
    final Float principalOutstandingAtSale = mortgage.getPrincipalOutstandingAtPoint(monthCPModifier);

    final Float ater = calculateValueOfAter(estateValue.getEstateValue(year), brokerCut, 
        inflationAdjustedSellingExpenses, principalOutstandingAtSale, year, taxesDueAtSale);
    netCashOutValue += ater;
    dataController.setValueAsFloat(ValueEnum.NET_CASH_OUT_VALUE, netCashOutValue, year);

    mirr.calculateMirr(year, requiredRateOfReturn, yearlyInterestRate, null, ater);

    final Float adjustedAter = (float) (ater / Math.pow(1 + monthlyRequiredRateOfReturn,monthCPModifier));
    dataController.setValueAsFloat(ValueEnum.ATER_PV, adjustedAter, year);

    return adjustedAter;
  }

  private static Float calculateTaxableIncome(int year, int monthCPModifier, int prevYearMonthCPModifier, Float yearlyBeforeTaxCashFlow) {

    final Float yearlyPrincipalPaid = mortgage.calculateYearlyPrincipalPaid(year, monthCPModifier, prevYearMonthCPModifier);

    Float taxableIncome = (yearlyBeforeTaxCashFlow + yearlyPrincipalPaid - yearlyDepreciation);

    // doesn't make sense to tax negative income...but should this be used to offset taxes? hmmm...
    if (taxableIncome <= 0) {
      taxableIncome = 0.0f;
    }

    dataController.setValueAsFloat(ValueEnum.TAXABLE_INCOME, taxableIncome, year);
    return taxableIncome;
  }

  private static Float calculateYearlyBeforeTaxCashFlow(int year, int prevYearMonthCPModifier) {
    // cashflowIn - cashflowOut

    final Float yearlyPrivateMortgageInsurance = mortgage.getYearlyPmi(year);
    dataController.setValueAsFloat(ValueEnum.YEARLY_PRIVATE_MORTGAGE_INSURANCE, yearlyPrivateMortgageInsurance, year);

    final Float prevYearMonthlyREARIncrementer = (float) Math.pow(1 + monthlyRealEstateAppreciationRate,prevYearMonthCPModifier);

    final Float yearlyPropertyTax = propertyTax * prevYearMonthlyREARIncrementer; 
    dataController.setValueAsFloat(ValueEnum.YEARLY_PROPERTY_TAX, yearlyPropertyTax, year);

    final Float yearlyIncome = netYearlyIncome * prevYearMonthlyREARIncrementer; 
    dataController.setValueAsFloat(ValueEnum.YEARLY_INCOME, yearlyIncome, year);

    final Float monthlyIRIncrementer = (float) Math.pow(1 + monthlyInflationRate, prevYearMonthCPModifier); 
    final Float yearlyGeneralExpenses = initialYearlyGeneralExpenses * monthlyIRIncrementer;
    dataController.setValueAsFloat(ValueEnum.YEARLY_GENERAL_EXPENSES, yearlyGeneralExpenses, year);

    final Float yearlyOutlay = yearlyPropertyTax + mp.getYearlyMortgagePayment() + yearlyGeneralExpenses + yearlyPrivateMortgageInsurance;
    final Float yearlyBeforeTaxCashFlow = yearlyIncome - yearlyOutlay;

    return yearlyBeforeTaxCashFlow;
  }


}

