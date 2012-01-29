package com.byronkatz.reap.general;

import com.byronkatz.reap.calculations.EstateValue;
import com.byronkatz.reap.calculations.GeneralCalculations;
import com.byronkatz.reap.calculations.ModifiedInternalRateOfReturn;
import com.byronkatz.reap.calculations.MortgagePayment;



public class CalculatedVariables {

  public static final int NUM_OF_MONTHS_IN_YEAR = 12;
  public static final Float RESIDENTIAL_DEPRECIATION_YEARS = 27.5f;
  public static final Float TAX_ON_CAPITAL_GAINS = 0.15f;
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
  private static Float sellingBrokerRate;
  private static Float generalSaleExpenses;
  private static Float downPayment;
  private static Float fixupCosts;
  private static Float propertyTax;
  private static Float principalOwed;
  private static Float monthlyInterestRate;



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
    mp = new MortgagePayment();
    estateValue = new EstateValue();

    monthlyPrivateMortgageInsurance = dataController.getValueAsFloat(ValueEnum.PRIVATE_MORTGAGE_INSURANCE);
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
    propertyTax = dataController.getValueAsFloat(ValueEnum.PROPERTY_TAX);
    principalOwed = totalPurchaseValue - downPayment;
    monthlyInterestRate = yearlyInterestRate / NUM_OF_MONTHS_IN_YEAR;
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

      final Float accumulatedInterest = getAccumulatedInterestPaymentsAtPoint(monthCPModifier);
      dataController.setValueAsFloat(ValueEnum.ACCUM_INTEREST, accumulatedInterest, year);

      final Float accumulatedInterestPreviousYear = getAccumulatedInterestPaymentsAtPoint(prevYearMonthCPModifier);
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

//  private static Float futureValue (final Float numberOfCompoundingPeriods, 
//      final Float rate, final Float originalValue) {
//
//    return (float) (originalValue * Math.pow((1 + rate), numberOfCompoundingPeriods));
//  }

//  private static Float calculateHomeFutureValue (final int year,
//      final Float rate, final Float originalValue) {
//    final Float homeFutureValue = futureValue(((float) year * NUM_OF_MONTHS_IN_YEAR), (rate / NUM_OF_MONTHS_IN_YEAR), originalValue);
//    dataController.setValueAsFloat(ValueEnum.PROJECTED_HOME_VALUE, homeFutureValue, year);
//    return homeFutureValue;
//  }

  private static Float calculateBrokerCutOfSale(final Float projectedValueOfHomeAtSale, 
      final Float sellingBrokerRate, final int year) {

    final Float brokerCut = projectedValueOfHomeAtSale * sellingBrokerRate;
    dataController.setValueAsFloat(ValueEnum.BROKER_CUT_OF_SALE, brokerCut, year);

    return brokerCut;
  }

  private static Float calculateSellingExpensesFutureValue(final int year,
      final Float rate, final Float originalValue) {

    final Float sellingExpensesFutureValue = 
        GeneralCalculations.futureValue((float) year * NUM_OF_MONTHS_IN_YEAR, rate / NUM_OF_MONTHS_IN_YEAR, originalValue);
    dataController.setValueAsFloat(ValueEnum.SELLING_EXPENSES, 
        sellingExpensesFutureValue, year);

    return sellingExpensesFutureValue;
  }

  private static Float calculateTaxesDueAtSale(final Float projectedValueOfHomeAtSale, 
      final Float totalPurchaseValue, final Float accumulatingDepreciation, final int year) {
    final Float taxesDueAtSale = (projectedValueOfHomeAtSale - totalPurchaseValue + accumulatingDepreciation)
        * TAX_ON_CAPITAL_GAINS;
    dataController.setValueAsFloat(ValueEnum.TAXES_DUE_AT_SALE, taxesDueAtSale, year);

    return taxesDueAtSale;
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
    
    final Float brokerCut = calculateBrokerCutOfSale(estateValue.getEstateValue(year), sellingBrokerRate, year);
    final Float inflationAdjustedSellingExpenses = calculateSellingExpensesFutureValue(
        year, inflationRate, generalSaleExpenses);

    final Float taxesDueAtSale = calculateTaxesDueAtSale(
        estateValue.getEstateValue(year), totalPurchaseValue, (yearlyDepreciation * year), year);
    final Float principalOutstandingAtSale = getPrincipalOutstandingAtPoint(monthCPModifier);
//    final Float ater = projectedValueOfHomeAtSale - brokerCut - 
//        inflationAdjustedSellingExpenses - getPrincipalOutstandingAtPoint(monthCPModifier) - taxesDueAtSale;
//    dataController.setValueAsFloat(ValueEnum.ATER, ater, year);
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

    final Float yearlyPrincipalPaid = calculateYearlyPrincipalPaid(year, monthCPModifier, prevYearMonthCPModifier);

    Float taxableIncome = (yearlyBeforeTaxCashFlow + yearlyPrincipalPaid - yearlyDepreciation);

    // doesn't make sense to tax negative income...but should this be used to offset taxes? hmmm...
    if (taxableIncome <= 0) {
      taxableIncome = 0.0f;
    }

    dataController.setValueAsFloat(ValueEnum.TAXABLE_INCOME, taxableIncome, year);
    return taxableIncome;
  }

  private static Float calculateYearlyPrincipalPaid(int year, int monthCPModifier, int prevYearMonthCPModifier) {
    //next year's yearlyAmountOutstanding minus this year's
    final Float pastYearAmountOutstanding = getPrincipalOutstandingAtPoint(prevYearMonthCPModifier);

    final Float currentYearAmountOutstanding = getPrincipalOutstandingAtPoint(monthCPModifier);
    dataController.setValueAsFloat(ValueEnum.CURRENT_AMOUNT_OUTSTANDING,
        currentYearAmountOutstanding, year);

    final Float yearlyPrincipalPaid = pastYearAmountOutstanding - currentYearAmountOutstanding;
    dataController.setValueAsFloat(ValueEnum.YEARLY_PRINCIPAL_PAID, yearlyPrincipalPaid, year);

    return yearlyPrincipalPaid;
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
    int begOfYear = (year - 1) * NUM_OF_MONTHS_IN_YEAR;
    int endOfYear = year * NUM_OF_MONTHS_IN_YEAR;

    for (int i = begOfYear; i < endOfYear; i++) {

      //if principal outstanding is greater than Loan To Value (LTV) ratio (usually 80%), apply pmi.
      if (getPrincipalOutstandingAtPoint(i) > ((1 - PMI_PERCENTAGE) * totalPurchaseValue)) {
        pmiThisYear += monthlyPrivateMortgageInsurance;
      }
    }

    return pmiThisYear;
  }

//  private static Float presentValue(final int numberOfCompoundingPeriods, 
//      final Float rate, final Float futureValue) {
//    
//    return (float) (futureValue / Math.pow(1 + rate, numberOfCompoundingPeriods));
//    
//  }

  private static Float getAccumulatedInterestPaymentsAtPoint (int compoundingPeriodDesired) {
    Float monthlyInterestRate = yearlyInterestRate / NUM_OF_MONTHS_IN_YEAR;

    Float f = 0.0f;
    
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
    if (monthlyInterestRate == 0) {
      f = 0.0f;
    } else {
    f = mp.getMonthlyMortgagePayment()/monthlyInterestRate;
    }
    Integer g = compoundingPeriodDesired + 1;

    Float accumInterestPaymentAtPoint = monthlyInterestRate*(principalOwed*(e+d)+(f)*(c*g-(e + d))-(mp.getMonthlyMortgagePayment()*g));

    return accumInterestPaymentAtPoint;
  }

  public static Float getPrincipalOutstandingAtPoint (int compoundingPeriodDesired) {

    Float a = monthlyInterestRate+1;

    Float princpalOutstandingAtPoint = (float) ((Math.pow(a,compoundingPeriodDesired) * principalOwed) -
        ( mp.getMonthlyMortgagePayment() *  (((a - Math.pow(a,compoundingPeriodDesired) )/ -monthlyInterestRate) + 1)));

    return princpalOutstandingAtPoint;
  }

}

