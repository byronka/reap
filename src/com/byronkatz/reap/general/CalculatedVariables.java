package com.byronkatz.reap.general;

import com.byronkatz.reap.calculations.AmortizationSchedule;
import com.byronkatz.reap.calculations.EstateValue;
import com.byronkatz.reap.calculations.GeneralCalculations;
import com.byronkatz.reap.calculations.InvestmentSale;
import com.byronkatz.reap.calculations.ModifiedInternalRateOfReturn;
import com.byronkatz.reap.calculations.MortgagePayment;



public class CalculatedVariables {

  public static final Float RESIDENTIAL_DEPRECIATION_YEARS = 27.5f;
  public static final Float TAX_ON_CAPITAL_GAINS = 0.15f;
  public static final int YEARLY = 1;
  public static final int MONTHLY = 2;
  public static final Float PMI_PERCENTAGE = 0.20f;
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
  private static InvestmentSale investmentSale;
  private static AmortizationSchedule as;
  private static GeneralCalculations gc;


  private static final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();

  //input variables
  private static Float monthlyPrivateMortgageInsurance;
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
  private static Float generalSaleExpenses;
  private static Float downPayment;
  private static Float fixupCosts;
  private static Float propertyTax;




  private static void assignVariables() {
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

    inflationRate = dataController.getValueAsFloat(ValueEnum.INFLATION_RATE);

    gc = new GeneralCalculations(inflationRate, monthlyRealEstateAppreciationRate);
    mirr = new ModifiedInternalRateOfReturn();
    mp = new MortgagePayment();
    estateValue = new EstateValue();
    as = new AmortizationSchedule(estateValue, mp);
    investmentSale = new InvestmentSale(estateValue, gc, as);
    
    monthlyPrivateMortgageInsurance = dataController.getValueAsFloat(ValueEnum.PRIVATE_MORTGAGE_INSURANCE);
    totalPurchaseValue = dataController.getValueAsFloat(ValueEnum.TOTAL_PURCHASE_VALUE);
    estimatedRentPayments = dataController.getValueAsFloat(ValueEnum.ESTIMATED_RENT_PAYMENTS);
    realEstateAppreciationRate = dataController.getValueAsFloat(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);
    vacancyRate = dataController.getValueAsFloat(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE);
    initialYearlyGeneralExpenses = dataController.getValueAsFloat(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES);
    marginalTaxRate = dataController.getValueAsFloat(ValueEnum.MARGINAL_TAX_RATE);
    buildingValue = dataController.getValueAsFloat(ValueEnum.BUILDING_VALUE);
    requiredRateOfReturn = dataController.getValueAsFloat(ValueEnum.REQUIRED_RATE_OF_RETURN);
    yearlyInterestRate = dataController.getValueAsFloat(ValueEnum.YEARLY_INTEREST_RATE);
    numOfCompoundingPeriods = dataController.getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS).intValue();
    generalSaleExpenses = dataController.getValueAsFloat(ValueEnum.GENERAL_SALE_EXPENSES);
    downPayment = dataController.getValueAsFloat(ValueEnum.DOWN_PAYMENT);
    fixupCosts = dataController.getValueAsFloat(ValueEnum.FIX_UP_COSTS);
    propertyTax = dataController.getValueAsFloat(ValueEnum.PROPERTY_TAX);

    yearlyDepreciation = buildingValue / RESIDENTIAL_DEPRECIATION_YEARS;

    yearlyCompoundingPeriods = numOfCompoundingPeriods / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    monthlyRealEstateAppreciationRate = realEstateAppreciationRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    monthlyRequiredRateOfReturn = requiredRateOfReturn / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    monthlyInflationRate = inflationRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    grossYearlyIncome = estimatedRentPayments * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
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

    final Float firstDay = downPayment + generalSaleExpenses + fixupCosts;

    modifiedInternalRateOfReturn = mirr.calculateMirr(0, requiredRateOfReturn, yearlyInterestRate, -firstDay, null);

    for (int year = 1; year <= yearlyCompoundingPeriods; year++) {

      netCashOutValue = 0.0f;
      final Integer monthCPModifier = year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
      final Integer prevYearMonthCPModifier = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;

      final Float atcfNPVSummation = calculateYearlyRentalIncomeNPV(year, monthCPModifier, prevYearMonthCPModifier);
      dataController.setValueAsFloat(ValueEnum.ATCF_NPV, atcfNPVSummation, year);

      final Float adjustedAter = calculateAter(year, monthCPModifier);  
      dataController.setValueAsFloat(ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN, modifiedInternalRateOfReturn, year);

      npvAccumulator = -firstDay + atcfNPVSummation + adjustedAter;
      dataController.setValueAsFloat(ValueEnum.NPV, npvAccumulator, year);

      final Float accumulatedInterest = as.getAccumulatedInterestPaymentsAtPoint(monthCPModifier);
      dataController.setValueAsFloat(ValueEnum.ACCUM_INTEREST, accumulatedInterest, year);

      final Float accumulatedInterestPreviousYear = as.getAccumulatedInterestPaymentsAtPoint(prevYearMonthCPModifier);
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


  private static Float calculateAter(int year, int monthCPModifier) {
    
    final Float ater = investmentSale.calculateValueOfAter(year);
    netCashOutValue += ater;
    dataController.setValueAsFloat(ValueEnum.NET_CASH_OUT_VALUE, netCashOutValue, year);

    mirr.calculateMirr(year, requiredRateOfReturn, yearlyInterestRate, null, ater);

    final Float adjustedAter = (float) (ater / Math.pow(1 + monthlyRequiredRateOfReturn,monthCPModifier));
    dataController.setValueAsFloat(ValueEnum.ATER_PV, adjustedAter, year);

    return adjustedAter;
  }

  private static Float calculateTaxableIncome(int year, int monthCPModifier, int prevYearMonthCPModifier, Float yearlyBeforeTaxCashFlow) {

    final Float yearlyPrincipalPaid = as.calculateYearlyPrincipalPaid(year, monthCPModifier, prevYearMonthCPModifier);

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

    final Float yearlyPrivateMortgageInsurance = getYearlyPmi(year, totalPurchaseValue);
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

  private static Float getYearlyPmi(int year, Float totalPurchaseValue) {
    //TODO - come up with a function for this rather than a loop.

    Float pmiThisYear = 0.0f;
    int begOfYear = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    int endOfYear = year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;

    for (int i = begOfYear; i < endOfYear; i++) {

      //if principal outstanding is greater than Loan To Value (LTV) ratio (usually 80%), apply pmi.
      if (as.getPrincipalOutstandingAtPoint(i) > ((1 - PMI_PERCENTAGE) * totalPurchaseValue)) {
        pmiThisYear += monthlyPrivateMortgageInsurance;
      }
    }

    return pmiThisYear;
  }


}

