package com.byronkatz.reap.general;

import com.byronkatz.reap.calculations.EquityReversion;
import com.byronkatz.reap.calculations.EstateValue;
import com.byronkatz.reap.calculations.ModifiedInternalRateOfReturn;
import com.byronkatz.reap.calculations.Mortgage;
import com.byronkatz.reap.calculations.MortgagePayment;
import com.byronkatz.reap.calculations.Rental;
import com.byronkatz.reap.calculations.RentalUnitOwnership;



public class CalculatedVariables {

  public static final int NUM_OF_MONTHS_IN_YEAR = 12;
  public static final Float RESIDENTIAL_DEPRECIATION_YEARS = 27.5f;
  public static final int YEARLY = 1;
  public static final int MONTHLY = 2;
  private static Float firstDay = 0.0f;
  private static Float yearlyNPVSummation = 0.0f;
  private static Float yearlyDepreciation = 0.0f;
  private static Integer yearlyCompoundingPeriods = 0;
  private static Float monthlyRequiredRateOfReturn = 0.0f;
//  private static Float monthlyInflationRate = 0.0f;
  private static Float npvAccumulator = 0.0f;
  private static Float atcfAccumulator = 0.0f;
  private static Float netCashOutValue = 0.0f;

  private static Float modifiedInternalRateOfReturn = 0.0f;

  private static ModifiedInternalRateOfReturn mirr;
  private static MortgagePayment mp;
  private static EstateValue estateValue;
  private static Mortgage mortgage;
  private static EquityReversion equityReversion;
  private static Rental rental;
  private static RentalUnitOwnership rentalUnitOwnership;


  private static final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();

  //input variables
  private static Float marginalTaxRate;
  private static Float buildingValue;
  private static Float requiredRateOfReturn;
  private static Float yearlyInterestRate;
  private static int numOfCompoundingPeriods;



  private static void assignVariables() {
    firstDay = 0.0f;
    yearlyNPVSummation = 0.0f;
    yearlyDepreciation = 0.0f;
    yearlyCompoundingPeriods = 0;
    monthlyRequiredRateOfReturn = 0.0f;
    npvAccumulator = 0.0f;

    modifiedInternalRateOfReturn = 0.0f;
    atcfAccumulator = 0.0f;

    mirr = new ModifiedInternalRateOfReturn();
    estateValue = new EstateValue();
    mortgage = new Mortgage(estateValue);
    yearlyInterestRate = mortgage.getYearlyInterestRate();
    numOfCompoundingPeriods = mortgage.getNumberOfCompoundingPeriods();
    mp = mortgage.getMortgagePayment();
    rentalUnitOwnership = new RentalUnitOwnership();
    rental = new Rental(rentalUnitOwnership);
    equityReversion = new EquityReversion(estateValue, rentalUnitOwnership);


    
    marginalTaxRate = dataController.getValueAsFloat(ValueEnum.MARGINAL_TAX_RATE);
    buildingValue = dataController.getValueAsFloat(ValueEnum.BUILDING_VALUE);
    requiredRateOfReturn = dataController.getValueAsFloat(ValueEnum.REQUIRED_RATE_OF_RETURN);
    yearlyDepreciation = buildingValue / RESIDENTIAL_DEPRECIATION_YEARS;
    yearlyCompoundingPeriods = numOfCompoundingPeriods / NUM_OF_MONTHS_IN_YEAR;
    monthlyRequiredRateOfReturn = requiredRateOfReturn / NUM_OF_MONTHS_IN_YEAR;


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

    firstDay = mortgage.getDownPayment() + mortgage.getClosingCosts() + rentalUnitOwnership.getFixupCosts();

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

    final Float taxesDueAtSale = equityReversion.calculateTaxesDueAtSale((yearlyDepreciation * year), year);
    final Float principalOutstandingAtSale = mortgage.getPrincipalOutstandingAtPoint(monthCPModifier);

    final Float ater = calculateValueOfAter(estateValue.getEstateValue(year), brokerCut, 
        equityReversion.getFVSellingExpenses(year), principalOutstandingAtSale, year, taxesDueAtSale);
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

    final Float yearlyOutlay = rentalUnitOwnership.getFVPropertyTax(year) + mp.getYearlyMortgagePayment() + rental.getFVYearlyGeneralExpenses(year) + yearlyPrivateMortgageInsurance;
    final Float yearlyBeforeTaxCashFlow = rental.getFVNetYearlyIncome(year) - yearlyOutlay;

    return yearlyBeforeTaxCashFlow;
  }


}

