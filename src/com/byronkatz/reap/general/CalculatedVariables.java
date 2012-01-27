package com.byronkatz.reap.general;

import java.util.Vector;



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
  private static Float monthlyMortgagePayment = 0.0f;
  private static Float yearlyMortgagePayment = 0.0f;
  private static Integer yearlyCompoundingPeriods = 0;
  private static Float grossYearlyIncome = 0.0f;
  private static Float netYearlyIncome = 0.0f;
  private static Float monthlyRealEstateAppreciationRate = 0.0f;
  private static Float monthlyRequiredRateOfReturn = 0.0f;
  private static Float monthlyInflationRate = 0.0f;
  private static Float accumulatingDepreciation = 0.0f;
  private static Float npvAccumulator = 0.0f;
  private static Float atcfAccumulator = 0.0f;
  private static Float netCashOutValue = 0.0f;

  private static Float modifiedInternalRateOfReturn = 0.0f;
  private static Vector<Float> cashFlowVector = new Vector<Float>();
  private static Float aterValueForMirr = 0.0f;


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
    monthlyMortgagePayment = 0.0f;
    yearlyMortgagePayment = 0.0f;
    yearlyCompoundingPeriods = 0;
    grossYearlyIncome = 0.0f;
    netYearlyIncome = 0.0f;
    monthlyRealEstateAppreciationRate = 0.0f;
    monthlyRequiredRateOfReturn = 0.0f;
    monthlyInflationRate = 0.0f;
    npvAccumulator = 0.0f;

    modifiedInternalRateOfReturn = 0.0f;
    atcfAccumulator = 0.0f;
    cashFlowVector = new Vector<Float>();
    aterValueForMirr = 0.0f;

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

    monthlyMortgagePayment = getMortgagePayment();
    yearlyMortgagePayment = NUM_OF_MONTHS_IN_YEAR * monthlyMortgagePayment;

    dataController.setValueAsFloat(ValueEnum.MONTHLY_MORTGAGE_PAYMENT, monthlyMortgagePayment);
    dataController.setValueAsFloat(ValueEnum.YEARLY_MORTGAGE_PAYMENT, yearlyMortgagePayment);

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

    modifiedInternalRateOfReturn = calculateMirr(0, requiredRateOfReturn, yearlyInterestRate, -firstDay, null);

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
    
    modifiedInternalRateOfReturn = calculateMirr(
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
    //equity reversion portion
    final Float thisYearMonthlyREARIncrementer = (float) Math.pow(1 + monthlyRealEstateAppreciationRate, monthCPModifier);
    final Float projectedValueOfHomeAtSale = totalPurchaseValue * thisYearMonthlyREARIncrementer;
    dataController.setValueAsFloat(ValueEnum.PROJECTED_HOME_VALUE, 
        projectedValueOfHomeAtSale, year);

    final Float brokerCut = projectedValueOfHomeAtSale * sellingBrokerRate;
    dataController.setValueAsFloat(ValueEnum.BROKER_CUT_OF_SALE, brokerCut, year);

    final Float monthlyIRIncrementer = (float) Math.pow(1 + monthlyInflationRate, monthCPModifier);
    final Float inflationAdjustedSellingExpenses = generalSaleExpenses * monthlyIRIncrementer;
    dataController.setValueAsFloat(ValueEnum.SELLING_EXPENSES, 
        inflationAdjustedSellingExpenses, year);

    //How many years do I take depreciation?
    accumulatingDepreciation = yearlyDepreciation * year;
    final Float taxesDueAtSale = (projectedValueOfHomeAtSale - totalPurchaseValue + accumulatingDepreciation)
        * TAX_ON_CAPITAL_GAINS;
    dataController.setValueAsFloat(ValueEnum.TAXES_DUE_AT_SALE, taxesDueAtSale, year);

    final Float ater = projectedValueOfHomeAtSale - brokerCut - 
        inflationAdjustedSellingExpenses - getPrincipalOutstandingAtPoint(monthCPModifier) - taxesDueAtSale;
    dataController.setValueAsFloat(ValueEnum.ATER, ater, year);
    
    netCashOutValue += ater;
    dataController.setValueAsFloat(ValueEnum.NET_CASH_OUT_VALUE, netCashOutValue, year);
    
    calculateMirr(year, requiredRateOfReturn, yearlyInterestRate, null, ater);

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

    final Float yearlyOutlay = yearlyPropertyTax + yearlyMortgagePayment + yearlyGeneralExpenses + yearlyPrivateMortgageInsurance;
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

  private static Float getMortgagePayment() {
    Float a = (monthlyInterestRate + 1);
    Float b = (float) Math.pow(a, numOfCompoundingPeriods);

    Float mortgageEquation = (monthlyInterestRate/(1-(1/ b)));

    return principalOwed * mortgageEquation;
  }

  private static Float getAccumulatedInterestPaymentsAtPoint (int compoundingPeriodDesired) {
    Float monthlyInterestRate = yearlyInterestRate / NUM_OF_MONTHS_IN_YEAR;

    Float mp = getMortgagePayment();

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
    Float f = mp/monthlyInterestRate;
    Integer g = compoundingPeriodDesired + 1;

    Float accumInterestPaymentAtPoint = monthlyInterestRate*(principalOwed*(e+d)+(f)*(c*g-(e + d))-(mp*g));

    return accumInterestPaymentAtPoint;
  }

  private static Float getPrincipalOutstandingAtPoint (int compoundingPeriodDesired) {

    Float mp = monthlyMortgagePayment;
    Float a = monthlyInterestRate+1;

    Float princpalOutstandingAtPoint = (float) ((Math.pow(a,compoundingPeriodDesired) * principalOwed) -
        ( mp *  (((a - Math.pow(a,compoundingPeriodDesired) )/ -monthlyInterestRate) + 1)));

    return princpalOutstandingAtPoint;
  }

  private static Float calculateMirr(
      int year, Float reinvestmentRate, Float financeRate, Float cashFlow, Float ater) {

    Float mirr = 0.0f;
    int internalYear = 0;
    Float futureValuePositiveCashFlowsAccumulator = 0.0f;
    Float presentValueNegativeCashFlowsAccumulator = 0.0f;
    if (cashFlow != null) {
      cashFlowVector.add(cashFlow);
    }

    aterValueForMirr = 0.0f;
    if (ater != null) {
      aterValueForMirr = ater;
    }


      for (Float flow : cashFlowVector) {

        if (flow < 0.0f) {
          presentValueNegativeCashFlowsAccumulator += flow / (float) Math.pow(1 + financeRate, internalYear);
        } else if (flow > 0.0f) {
          futureValuePositiveCashFlowsAccumulator += flow * (float) Math.pow(1 + reinvestmentRate, year - internalYear);
        }

        internalYear++;
      }


    //special case for ater, since by definition it always happens in last year

      if (aterValueForMirr < 0.0f) {
        presentValueNegativeCashFlowsAccumulator += aterValueForMirr / (float) Math.pow(1 + financeRate, year);
      } else if (aterValueForMirr > 0.0f) {
        futureValuePositiveCashFlowsAccumulator += aterValueForMirr * (float) Math.pow(1 + reinvestmentRate, 0);
      }


    //we don't want divide by zero errors.
    if (!(presentValueNegativeCashFlowsAccumulator == 0f) || !(year == 0)) {
      mirr = (float) Math.pow(futureValuePositiveCashFlowsAccumulator / - presentValueNegativeCashFlowsAccumulator, (1.0f/year)) - 1;
    }

    return mirr;
  }

}

