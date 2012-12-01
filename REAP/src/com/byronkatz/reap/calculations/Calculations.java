package com.byronkatz.reap.calculations;

import java.util.Arrays;

import android.util.Log;

import com.byronkatz.reap.general.DataManager;
import com.byronkatz.reap.general.ValueEnum;

/**
 * One very important point to the calculations in this class. That is: All
 * yearly values are intended to be valid as of the last day of the year for
 * which they are calculated. For example, if we want the After Tax Cash Flow
 * for compounding period 5, the result needs to represent the correct value as
 * of December 31st, approximately, of that year. This concept will keep all the
 * values throughout the system accurate if held to.
 * 
 * @author byron
 * 
 */
public class Calculations implements ValueSettable {

  public double getFutureValuePositiveCashFlowsAccumulator() {
    return futureValuePositiveCashFlowsAccumulator;
  }

  public double getPresentValueNegativeCashFlowsAccumulator() {
    return presentValueNegativeCashFlowsAccumulator;
  }

  private double propertyTax;
  private double closingCosts;
  private double insurance;
  private double generalExpenses;
  private double[] operatingExpensesCache;
  private double inflationRate;
  private double municipalExpenses;
  private double totalPurchaseValue;
  private double valuation;
  private double fixupCosts;
  private double initialRent;
  private double yearlyRealEstateAppreciationRate;
  private double vacancyAndCreditLossRate;
  private int nocp;
  private int monthsUntilRentStarts;
  private double loanAmount;
  private double downPayment;
  private double loanInterestRateMonthly;
  private double[] interestAccumulator;
  private double[] interestPayment;
  private double[] principalPayment;
  private double[] amountOwed;
  private double mpValue;
  private double pmiMonthly;
  private double pmiEndsValue;
  private double[] pmiAccumulator;
  public static final double PMI_BOUNDARY_PERCENTAGE = 0.80D;
  private int totalYearsToCalculate;
  private double requiredRateOfReturn;
  private double atcfNpvAccumulator;
  private double[] atcfCache;
  private double[] atcfAccumCache;
  private double[] atcfPvCache;
  private double yearlyLoanInterestRate;
  private double mirr;
  private double futureValuePositiveCashFlowsAccumulator;
  private double presentValueNegativeCashFlowsAccumulator;
  private double atcfTempValue;
  private double aterTempValue;
  private double brokerRate;
  private double initialSellingExpenses;
  private double buildingValue;
  private double marginalTaxRate;
  private final double DEPRECIATION_CONSTANT = 27.5d;
  private final double TAX_ON_CAPITAL_GAINS = 0.15d;
  private static final double EPSILON = 0.00001d;
  private double[] yearlyGrossIncomeCache;
  private double[] yearlyNetOperatingIncomeCache;
  private double[] taxableIncomeCache;

  private DataManager dataManager;

  private CalcValueGettable mirrObject;
  private CalcValueGettable propertyTaxFValue;
  private CalcValueGettable insuranceFValue;
  private CalcValueGettable generalExpensesFValue;
  private CalcValueGettable operatingExpensesFValue;
  private CalcValueGettable municipalExpensesFValue;
  private CalcValueGettable investmentFValue;
  private CalcValueGettable monthlyRentFValue;
  private CalcValueGettable yearlyGrossIncomeValue;
  private CalcValueGettable yearlyNetIncomeValue;
  private CalcValueGettable netOperatingIncome;
  private CalcValueGettable taxableIncome;
  private CalcValueGettable interestPaymentValue;
  private CalcValueGettable principalPaymentValue;
  private CalcValueGettable yearlyInterestPaidAccumulatedValue;
  private CalcValueGettable monthlyInterestPaidAccumulatedValue;
  private CalcValueGettable yearlyAmountOwedValue;
  private CalcValueGettable monthlyAmountOwedValue;
  private CalcValueGettable loanAmountValue;
  private CalcValueGettable yearlyInterestPaid;
  private CalcValueGettable yearlyPrincipalPaid;
  private CalcValueGettable mortgagePayment;
  private CalcValueGettable yearlyMortgagePayment;
  private CalcValueGettable yearlyPrivateMortgageInsurance;
  private CalcValueGettable yearlyPrivateMortgageInsuranceAccum;
  private CalcValueGettable afterTaxCashFlow;
  private CalcValueGettable afterTaxCashFlowNPV;
  private CalcValueGettable afterTaxCashFlowAccumulator;
  private CalcValueGettable beforeTaxCashFlow;
  private CalcValueGettable yearlyOutlay;
  private CalcValueGettable capitalizationRateOnPurchaseValue;
  private CalcValueGettable capitalizationRateOnProjectedValue;
  private CalcValueGettable firstDayCosts;
  private CalcValueGettable npv;
  private CalcValueGettable sellingExpensesFValue;
  private CalcValueGettable brokerCutOfSale;
  private CalcValueGettable afterTaxEquityReversion;
  private CalcValueGettable aterPv;
  private CalcValueGettable yearlyDepreciation;
  private CalcValueGettable taxesDueAtSale;
  private CalcValueGettable yearlyTaxOnIncome;

  public Calculations() {

    propertyTax = 0d;
    insurance = 0d;
    generalExpenses = 0d;
    inflationRate = 0d;
    municipalExpenses = 0d;
    totalPurchaseValue = 0d;
    valuation = 0d;
    fixupCosts = 0d;
    initialRent = 0d;
    yearlyRealEstateAppreciationRate = 0d;
    vacancyAndCreditLossRate = 0d;
    nocp = 0;
    monthsUntilRentStarts = 0;
    loanAmount = 0d;
    downPayment = 0d;
    loanInterestRateMonthly = 0d;
    interestAccumulator = null;
    interestPayment = null;
    principalPayment = null;
    amountOwed = null;
    mpValue = 0d;
    pmiMonthly = 0d;
    pmiEndsValue = 0d;
    pmiAccumulator = null;

    totalYearsToCalculate = 0;
    requiredRateOfReturn = 0d;
    atcfNpvAccumulator = 0d;
    yearlyLoanInterestRate = 0d;
    mirr = 0d;
    futureValuePositiveCashFlowsAccumulator = 0d;
    presentValueNegativeCashFlowsAccumulator = 0d;

    
    atcfTempValue = 0d;
    aterTempValue = 0d;
    brokerRate = 0d;
    initialSellingExpenses = 0d;
    buildingValue = 0d;
    marginalTaxRate = 0d;

    dataManager = null;

    mirrObject = new MIRR();
    propertyTaxFValue = new PropertyTaxFValue();
    insuranceFValue = new InsuranceFValue();
    generalExpensesFValue = new GeneralExpensesFValue();
    operatingExpensesFValue = new OperatingExpensesFValue();
    municipalExpensesFValue = new MunicipalExpensesFValue();
    investmentFValue = new InvestmentFValue();
    monthlyRentFValue = new MonthlyRentFValue();
    yearlyGrossIncomeValue = new YearlyGrossIncomeValue();
    yearlyNetIncomeValue = new YearlyNetIncomeValue();
    netOperatingIncome = new NetOperatingIncome();
    taxableIncome = new TaxableIncome();
    interestPaymentValue = new InterestPaymentValue();
    yearlyInterestPaidAccumulatedValue = new YearlyInterestPaidAccumulatedValue();
    monthlyInterestPaidAccumulatedValue = new MonthlyInterestPaidAccumulatedValue();
    yearlyAmountOwedValue = new YearlyAmountOwedValue();
    monthlyAmountOwedValue = new MonthlyAmountOwedValue();
    principalPaymentValue = new PrincipalPaymentValue();
    loanAmountValue = new LoanAmountValue();
    yearlyInterestPaid = new YearlyInterestPaid();
    yearlyPrincipalPaid = new YearlyPrincipalPaid();
    mortgagePayment = new MortgagePayment();
    yearlyMortgagePayment = new YearlyMortgagePayment();
    yearlyPrivateMortgageInsurance = new YearlyPrivateMortgageInsurance();
    yearlyPrivateMortgageInsuranceAccum = new YearlyPrivateMortgageInsuranceAccum();
    afterTaxCashFlow = new AfterTaxCashFlow();
    afterTaxCashFlowNPV = new AtcfNpv();
    afterTaxCashFlowAccumulator = new AfterTaxCashFlowAccumulator();
    beforeTaxCashFlow = new BeforeTaxCashFlow();
    yearlyOutlay = new YearlyOutlay();
    capitalizationRateOnPurchaseValue = new CapitalizationRateOnPurchaseValue();
    capitalizationRateOnProjectedValue = new CapitalizationRateOnProjectedValue();
    firstDayCosts = new FirstDayCosts();
    npv = new NPV();
    sellingExpensesFValue = new SellingExpensesFValue();
    brokerCutOfSale = new BrokerCutOfSale();
    afterTaxEquityReversion = new AfterTaxEquityReversion();
    aterPv = new AterPV();
    yearlyDepreciation = new YearlyDepreciation();
    taxesDueAtSale = new TaxesDueAtSale();
    yearlyTaxOnIncome = new YearlyTaxOnIncome();

  }

  private boolean isApproximatelyEqual(double value1, double value2) {
    if (Math.abs(value1 - value2) < EPSILON) {
      return true;
    } else {
      return false;
    }
  }
  
  
  public void setValues(DataManager dataManager) {

    this.dataManager = dataManager;
    assignDataManager(dataManager);
    double oldLoanValue = loanAmount;
    double oldYearlyLoanInterestRate = yearlyLoanInterestRate;
    int oldNocp = nocp;

    loadCurrentUserInputValues();

    if (! isApproximatelyEqual(oldLoanValue, loanAmount) || 
        ! isApproximatelyEqual(oldYearlyLoanInterestRate, yearlyLoanInterestRate) ||
        ! isApproximatelyEqual(oldNocp, nocp)) {
      mpValue = calculateMortgagePayment();
      createAmortizationTable(nocp);
    }
    createOperatingExpensesTable();
    createYearlyGrossIncomeTable();
    createYearlyNetOperatingIncomeTable();
    createTaxableIncomeTable();
    createAtcfTable();
    createAtcfPvTable();
    createAtcfAccumTable();

  }

  private void loadCurrentUserInputValues() {

    closingCosts = dataManager.getInputValue(ValueEnum.CLOSING_COSTS);
    propertyTax = dataManager.getInputValue(ValueEnum.PROPERTY_TAX);
    insurance = dataManager.getInputValue(ValueEnum.INITIAL_HOME_INSURANCE);
    generalExpenses = dataManager
        .getInputValue(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES);
    inflationRate = dataManager.getInputValue(ValueEnum.INFLATION_RATE);
    yearlyRealEstateAppreciationRate = dataManager
        .getInputValue(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);
    municipalExpenses = dataManager
        .getInputValue(ValueEnum.LOCAL_MUNICIPAL_FEES);
    totalPurchaseValue = dataManager
        .getInputValue(ValueEnum.TOTAL_PURCHASE_VALUE);
    fixupCosts = dataManager.getInputValue(ValueEnum.FIX_UP_COSTS);
    valuation = dataManager.getInputValue(ValueEnum.INITIAL_VALUATION);
    initialRent = dataManager.getInputValue(ValueEnum.ESTIMATED_RENT_PAYMENTS);
    vacancyAndCreditLossRate = dataManager
        .getInputValue(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE);
    nocp = (int) dataManager
        .getInputValue(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS);
    downPayment = dataManager.getInputValue(ValueEnum.DOWN_PAYMENT);
    loanInterestRateMonthly = dataManager
        .getInputValue(ValueEnum.YEARLY_INTEREST_RATE) / 12;
    totalYearsToCalculate = (int) dataManager
        .getInputValue(ValueEnum.EXTRA_YEARS) + nocp / 12;
    requiredRateOfReturn = dataManager
        .getInputValue(ValueEnum.REQUIRED_RATE_OF_RETURN);
    yearlyLoanInterestRate = dataManager
        .getInputValue(ValueEnum.YEARLY_INTEREST_RATE);
    brokerRate = dataManager.getInputValue(ValueEnum.SELLING_BROKER_RATE);
    initialSellingExpenses = dataManager
        .getInputValue(ValueEnum.GENERAL_SALE_EXPENSES);
    buildingValue = dataManager.getInputValue(ValueEnum.BUILDING_VALUE);
    marginalTaxRate = dataManager.getInputValue(ValueEnum.MARGINAL_TAX_RATE);
    pmiMonthly = dataManager
        .getInputValue(ValueEnum.PRIVATE_MORTGAGE_INSURANCE);
    monthsUntilRentStarts = (int) dataManager
        .getInputValue(ValueEnum.MONTHS_UNTIL_RENT_STARTS);

    if (totalPurchaseValue > downPayment) {
      loanAmount = totalPurchaseValue - downPayment;
    } else {
      loanAmount = 0d;
    }


    pmiEndsValue = PMI_BOUNDARY_PERCENTAGE * totalPurchaseValue;
    
    // only if the size needs to be different do we recreate the arrays.
    if (interestPayment == null || amountOwed == null
        || interestAccumulator == null || principalPayment == null
        || pmiAccumulator == null || interestPayment.length != nocp) {
      interestPayment = new double[nocp];
      amountOwed = new double[nocp];
      interestAccumulator = new double[nocp];
      principalPayment = new double[nocp];
      pmiAccumulator = new double[nocp];
    }
    // only if the size needs to be different do we recreate the arrays.

    if (atcfCache == null || atcfCache.length != totalYearsToCalculate) {

      atcfCache = new double[totalYearsToCalculate];
      atcfAccumCache = new double[totalYearsToCalculate];
      atcfPvCache = new double[totalYearsToCalculate];
      operatingExpensesCache = new double[totalYearsToCalculate];
      yearlyGrossIncomeCache = new double[totalYearsToCalculate];
      yearlyNetOperatingIncomeCache = new double[totalYearsToCalculate];
      taxableIncomeCache = new double[totalYearsToCalculate];
      
    }
  }

  private void assignDataManager(DataManager dataManager) {
    dataManager.addCalcValuePointers(ValueEnum.PRINCIPAL_PAYMENT,
        principalPaymentValue);
    dataManager.addCalcValuePointers(ValueEnum.ATCF, afterTaxCashFlow);
    dataManager.addCalcValuePointers(ValueEnum.YEARLY_BEFORE_TAX_CASH_FLOW,
        beforeTaxCashFlow);
    dataManager.addCalcValuePointers(ValueEnum.YEARLY_OUTLAY, yearlyOutlay);
    dataManager.addCalcValuePointers(ValueEnum.CAP_RATE_ON_PROJECTED_VALUE,
        capitalizationRateOnProjectedValue);
    dataManager.addCalcValuePointers(ValueEnum.CAP_RATE_ON_PURCHASE_VALUE,
        capitalizationRateOnPurchaseValue);
    dataManager.addCalcValuePointers(ValueEnum.FIRST_DAY_COSTS, firstDayCosts);
    dataManager.addCalcValuePointers(ValueEnum.NPV, npv);
    dataManager.addCalcValuePointers(ValueEnum.ATCF_ACCUMULATOR,
        afterTaxCashFlowAccumulator);
    dataManager.addCalcValuePointers(ValueEnum.ATCF_NPV, afterTaxCashFlowNPV);
    dataManager.addCalcValuePointers(
        ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN, mirrObject);
    dataManager.addCalcValuePointers(ValueEnum.BROKER_CUT_OF_SALE,
        brokerCutOfSale);
    dataManager.addCalcValuePointers(ValueEnum.ATER, afterTaxEquityReversion);
    dataManager.addCalcValuePointers(ValueEnum.SELLING_EXPENSES,
        sellingExpensesFValue);
    dataManager.addCalcValuePointers(ValueEnum.ATER_PV, aterPv);
    dataManager.addCalcValuePointers(ValueEnum.PROJECTED_HOME_VALUE,
        investmentFValue);
    dataManager.addCalcValuePointers(ValueEnum.MONTHLY_RENT_FV,
        monthlyRentFValue);
    dataManager.addCalcValuePointers(ValueEnum.GROSS_YEARLY_INCOME,
        yearlyGrossIncomeValue);
    dataManager.addCalcValuePointers(ValueEnum.YEARLY_INCOME,
        yearlyNetIncomeValue);
    dataManager.addCalcValuePointers(ValueEnum.YEARLY_NET_OPERATING_INCOME,
        netOperatingIncome);
    dataManager.addCalcValuePointers(ValueEnum.TAXABLE_INCOME, taxableIncome);
    dataManager.addCalcValuePointers(ValueEnum.YEARLY_DEPRECIATION,
        yearlyDepreciation);
    dataManager.addCalcValuePointers(ValueEnum.TAXES_DUE_AT_SALE,
        taxesDueAtSale);
    dataManager.addCalcValuePointers(ValueEnum.YEARLY_TAX_ON_INCOME,
        yearlyTaxOnIncome);
    dataManager.addCalcValuePointers(ValueEnum.YEARLY_PROPERTY_TAX,
        propertyTaxFValue);
    dataManager.addCalcValuePointers(ValueEnum.YEARLY_HOME_INSURANCE,
        insuranceFValue);
    dataManager.addCalcValuePointers(ValueEnum.YEARLY_GENERAL_EXPENSES,
        generalExpensesFValue);
    dataManager.addCalcValuePointers(ValueEnum.YEARLY_OPERATING_EXPENSES,
        operatingExpensesFValue);
    dataManager.addCalcValuePointers(ValueEnum.YEARLY_MUNICIPAL_FEES,
        municipalExpensesFValue);
    dataManager.addCalcValuePointers(ValueEnum.CURRENT_AMOUNT_OUTSTANDING,
        yearlyAmountOwedValue);
    dataManager.addCalcValuePointers(ValueEnum.MONTHLY_AMOUNT_OWED,
        monthlyAmountOwedValue);
    dataManager.addCalcValuePointers(ValueEnum.YEARLY_ACCUM_INTEREST,
        yearlyInterestPaidAccumulatedValue);
    dataManager.addCalcValuePointers(ValueEnum.MONTHLY_INTEREST_ACCUMULATED, 
        monthlyInterestPaidAccumulatedValue);
    dataManager.addCalcValuePointers(ValueEnum.INTEREST_PAYMENT,
        interestPaymentValue);
    dataManager.addCalcValuePointers(ValueEnum.YEARLY_INTEREST_PAID,
        yearlyInterestPaid);
    dataManager.addCalcValuePointers(ValueEnum.YEARLY_PRINCIPAL_PAID,
        yearlyPrincipalPaid);
    dataManager.addCalcValuePointers(ValueEnum.MONTHLY_MORTGAGE_PAYMENT,
        mortgagePayment);
    dataManager.addCalcValuePointers(ValueEnum.YEARLY_MORTGAGE_PAYMENT,
        yearlyMortgagePayment);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_PRIVATE_MORTGAGE_INSURANCE_ACCUM,
        yearlyPrivateMortgageInsuranceAccum);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_PRIVATE_MORTGAGE_INSURANCE, yearlyPrivateMortgageInsurance);
    dataManager.addCalcValuePointers(ValueEnum.LOAN_AMOUNT, loanAmountValue);
  }

  private class PrincipalPaymentValue implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      return principalPayment[compoundingPeriod];
    }
  }

  /**
   * Future value of the monthly rent. Gets incremented by REAR once each 12
   * months.
   * 
   * @author byron
   * 
   */
  private class MonthlyRentFValue implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      return (initialRent * Math.pow(1 + yearlyRealEstateAppreciationRate,
          compoundingPeriod / MONTHS_IN_YEAR));
    }

  }

  /**
   * The yearly gross income. The monthly rent times 12.
   * 
   * @author byron
   * 
   */
  private class YearlyGrossIncomeValue implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      return yearlyGrossIncomeCache[compoundingPeriod / MONTHS_IN_YEAR];
    }

  }

  /**
   * returns the yearly net income, which is the yearly gross income times (1
   * minus vacancy)
   * 
   * @author byron
   * 
   */
  private class YearlyNetIncomeValue implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      return yearlyGrossIncomeCache[compoundingPeriod / MONTHS_IN_YEAR]
          * (1 - vacancyAndCreditLossRate);

    }

  }

  /**
   * Value is same for entire year
   * 
   * @author byron
   * 
   */
  private void createYearlyGrossIncomeTable() {
    
    Arrays.fill(yearlyGrossIncomeCache, 0d);
    
    for (int year = 0; year < totalYearsToCalculate; year++) {
      if (((year + 1) * 12) <= monthsUntilRentStarts) {
        yearlyGrossIncomeCache[year] = 0;
      } else if ((((year + 1) * 12) > monthsUntilRentStarts)
          && (((year + 1) * 12) - monthsUntilRentStarts) <= 12) {
        yearlyGrossIncomeCache[year] = (((year + 1) * 12) - monthsUntilRentStarts)
            * (initialRent * Math.pow(1 + yearlyRealEstateAppreciationRate,
                year));
      } else if ((((year + 1) * 12) - monthsUntilRentStarts) > 12) {
        yearlyGrossIncomeCache[year] = 12 * (initialRent * Math.pow(
            1 + yearlyRealEstateAppreciationRate, year));
      }

    }
  }

  /**
   * Value is same for entire year
   * 
   * @author byron
   * 
   */
  private class NetOperatingIncome implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }

      return yearlyNetOperatingIncomeCache[compoundingPeriod / MONTHS_IN_YEAR];

    }
  }

  /**
   * Value is same for entire year
   * 
   * @author byron
   * 
   */
  private void createYearlyNetOperatingIncomeTable() {
    
    Arrays.fill(yearlyNetOperatingIncomeCache, 0d);
    
    for (int year = 0; year < totalYearsToCalculate; year++) {
      yearlyNetOperatingIncomeCache[year] = yearlyNetIncomeValue
          .getValue(year * 12) - operatingExpensesFValue.getValue(year * 12);
    }
  }

  /**
   * Value is same for entire year
   * 
   * @author byron
   * 
   */
  private class TaxableIncome implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      if (taxableIncomeCache[compoundingPeriod / MONTHS_IN_YEAR] < 0) {
        return 0d;
      } else {
        return taxableIncomeCache[compoundingPeriod / MONTHS_IN_YEAR];
      }
    }
  }

  private void createTaxableIncomeTable() {
    
    Arrays.fill(taxableIncomeCache, 0d);
    
    for (int year = 0; year < totalYearsToCalculate; year++) {
      taxableIncomeCache[year] = netOperatingIncome.getValue(year * 12)
          - yearlyInterestPaid.getValue(year * 12)
          - yearlyDepreciation.getValue(year * 12);
    }
  }

  /**
   * Value is a constant for all values greater than 0
   * 
   * @author byron
   * 
   */
  private class YearlyDepreciation implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      return buildingValue / DEPRECIATION_CONSTANT;
    }

  }

  /**
   * Value is same for entire year
   * 
   * @author byron
   * 
   */
  private class YearlyTaxOnIncome implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }

      return taxableIncome.getValue(compoundingPeriod) * marginalTaxRate;
    }

  }

  /**
   * Value is same for entire year
   * 
   * @author byron
   * 
   */
  private class TaxesDueAtSale implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      if (taxesDueFunction(compoundingPeriod) < 0) {
        return 0d;
      }

      else {
        return taxesDueFunction(compoundingPeriod);
      }
    }

    private double taxesDueFunction(int compoundingPeriod) {
      return 
          (investmentFValue.getValue(compoundingPeriod) - 
              brokerCutOfSale.getValue(compoundingPeriod) - 
              sellingExpensesFValue.getValue(compoundingPeriod) -
              totalPurchaseValue + 
              (yearlyDepreciation.getValue(compoundingPeriod)  * 
                  ((compoundingPeriod / MONTHS_IN_YEAR) + 1))) * 
                  TAX_ON_CAPITAL_GAINS;
    }

  }

  /**
   * compounding yearly rather than monthly.
   * 
   * @author byron
   * 
   */
  private class MunicipalExpensesFValue implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      return (municipalExpenses * Math.pow(1 + inflationRate, compoundingPeriod
          / MONTHS_IN_YEAR));
    }

  }

  /**
   * Increments the property tax at the rate of REAR once a year. We assert that
   * this value should be non-null and greater than 0. Less than zero does not
   * make sense.
   * 
   * @author byron
   * 
   */
  private class PropertyTaxFValue implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      return (propertyTax * Math.pow(1 + yearlyRealEstateAppreciationRate,
          compoundingPeriod / MONTHS_IN_YEAR));
    }

  }

  /**
   * Increments the insurance value at the rate of inflation once a year. We
   * assert that this value should be non-null and greater than 0. Less than
   * zero does not make sense.
   * 
   * @author byron
   * 
   */
  private class InsuranceFValue implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      return (insurance * Math.pow(1 + inflationRate, compoundingPeriod
          / MONTHS_IN_YEAR));
    }

  }

  /**
   * Increments the general expenses value at the rate of inflation once a year.
   * We assert that this value should be non-null and greater than 0. Less than
   * zero does not make sense.
   * 
   * @author byron
   * 
   */
  private class GeneralExpensesFValue implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      return (generalExpenses * Math.pow(1 + inflationRate, compoundingPeriod
          / MONTHS_IN_YEAR));
    }

  }

  /**
   * Increments the general expenses value at the rate of inflation once a year.
   * We assert that this value should be non-null and greater than 0. Less than
   * zero does not make sense.
   * 
   * @author byron
   * 
   * 
   */
  private class OperatingExpensesFValue implements CalcValueGettable {
    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      return operatingExpensesCache[compoundingPeriod / MONTHS_IN_YEAR];
    }

  }

  /**
   * Value is same for entire year
   * 
   * @author byron
   * 
   */
  private void createOperatingExpensesTable() {
    Arrays.fill(operatingExpensesCache, 0d);
    
    for (int year = 0; year < totalYearsToCalculate; year++) {
      operatingExpensesCache[year] = ((generalExpenses + insurance + municipalExpenses) * Math
          .pow(1 + inflationRate, year))
          + propertyTax
          * Math.pow(1 + yearlyRealEstateAppreciationRate, year);

    }
  }

  /**
   * Value is same for entire year
   * 
   * @author byron
   * 
   */
  private class InvestmentFValue implements CalcValueGettable {
    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      if (Math.abs(valuation - 0d) > EPSILON) {
        return valuation
            * Math.pow(1 + yearlyRealEstateAppreciationRate, compoundingPeriod
                / MONTHS_IN_YEAR);
      } else {
      return (totalPurchaseValue + fixupCosts)
          * Math.pow(1 + yearlyRealEstateAppreciationRate, compoundingPeriod
              / MONTHS_IN_YEAR);
      }
    }
  }

  /**
   * Value is same for entire year
   * 
   * @author byron
   * 
   */
  private class BrokerCutOfSale implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }

      return brokerRate * investmentFValue.getValue(compoundingPeriod);
    }

  }

  /**
   * ATER is for the end of the year, this year
   * 
   * @author byron
   * 
   */
  private class AfterTaxEquityReversion implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }

      return investmentFValue.getValue(compoundingPeriod)
          - brokerCutOfSale.getValue(compoundingPeriod)
          - sellingExpensesFValue.getValue(compoundingPeriod)
          - yearlyAmountOwedValue.getValue(compoundingPeriod)
          - taxesDueAtSale.getValue(compoundingPeriod);

    }

  }

  /**
   * We bring the ATER, which is based on the value at the end of the year, back
   * to the beginning of the year. That is why the equation has a exponent of a
   * year ahead: (compoundingPeriod/MONTHS_IN_YEAR)+1)
   * 
   * @author byron
   * 
   */
  private class AterPV implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      return (afterTaxEquityReversion.getValue(compoundingPeriod) / Math.pow(
          (1 + requiredRateOfReturn), (compoundingPeriod / MONTHS_IN_YEAR) + 1));
    }

  }

  /**
   * Value is same for entire year
   * 
   * @author byron
   * 
   */
  private class SellingExpensesFValue implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }

      return initialSellingExpenses
          * Math.pow(1 + inflationRate, compoundingPeriod / MONTHS_IN_YEAR);
    }

  }

  /**
   * Only valid for the very first day of ownership - the day of closing
   * 
   * @author byron
   * 
   */
  private class FirstDayCosts implements CalcValueGettable {

    /**
     * This equation varies from the norm in that it always provides the same
     * amount, unless you ask for a compounding period below 0. The first day
     * costs are those that are incurred the very first day of ownership. This
     * is not the exact situation in the case of fixup costs, but for our
     * purposes it is a close approimation
     */
    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      return downPayment + closingCosts + fixupCosts;
    }
  }

  private class NPV implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      return (-firstDayCosts.getValue(compoundingPeriod))
          + afterTaxCashFlowNPV.getValue(compoundingPeriod)
          + aterPv.getValue(compoundingPeriod);

    }
  }

  private class MIRR implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }

      presentValueNegativeCashFlowsAccumulator = 0d;
      futureValuePositiveCashFlowsAccumulator = 0d;
      atcfTempValue = 0d;
      aterTempValue = 0d;

      // cash flows for mirr:

      // step 1, first day costs:
      // first day costs are assumed to literally be on the day of closing, for
      // simplification
      presentValueNegativeCashFlowsAccumulator += -firstDayCosts.getValue(0);


      // step 2: after tax cash flows - different action depending on sign
      // the atcf provided is for the end of the year. For example, atcf of
      // month 5
      // provides the atcf at month 12
      for (int year = 0; year < (compoundingPeriod / MONTHS_IN_YEAR); year++) {

        atcfTempValue = afterTaxCashFlow.getValue((year) * MONTHS_IN_YEAR);

        if (atcfTempValue < 0d) {
          presentValueNegativeCashFlowsAccumulator += atcfTempValue
              / Math.pow(1 + yearlyLoanInterestRate, year + 1);
        } else {
          futureValuePositiveCashFlowsAccumulator += atcfTempValue
              * Math.pow(1 + requiredRateOfReturn,
                  (compoundingPeriod / MONTHS_IN_YEAR) - (year + 1));
        }
      }

      // step 3: ater
      // take the ater at the end of the year in question
      aterTempValue = afterTaxEquityReversion.getValue(compoundingPeriod);
      // System.out.println("aterTempValue: " + aterTempValue);

      if (aterTempValue < 0.0d) {
        presentValueNegativeCashFlowsAccumulator += aterTempValue
            / Math.pow(1 + yearlyLoanInterestRate, compoundingPeriod
                / MONTHS_IN_YEAR);
      } else if (aterTempValue > 0.0d) {
        futureValuePositiveCashFlowsAccumulator += aterTempValue;
      }

      // step 4: division
      // we don't want divide by zero errors.
      if (!(presentValueNegativeCashFlowsAccumulator == 0f)) {
        mirr = Math.pow(futureValuePositiveCashFlowsAccumulator
            / -presentValueNegativeCashFlowsAccumulator,
            (1.0d / ((compoundingPeriod / MONTHS_IN_YEAR)+1))) - 1;

      } else {
        mirr = 0d;
      }
      return mirr;
    }
  }

  private class AtcfNpv implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      atcfNpvAccumulator = 0d;
      for (int i = 0; i <= (compoundingPeriod / MONTHS_IN_YEAR); i++) {
        atcfNpvAccumulator += getAtcfPv(i * MONTHS_IN_YEAR);
      }
      return atcfNpvAccumulator;
    }

    private double getAtcfPv(int compoundingPeriod) {
      if (compoundingPeriod < 0
          || compoundingPeriod >= (totalYearsToCalculate * 12)) {
        return 0d;
      }
      return atcfPvCache[compoundingPeriod / MONTHS_IN_YEAR];
    }

  }

  private class AfterTaxCashFlowAccumulator implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0
          || compoundingPeriod >= (totalYearsToCalculate * 12)) {
        return 0d;
      }
      return atcfAccumCache[compoundingPeriod / MONTHS_IN_YEAR];
    }

  }

  private class AfterTaxCashFlow implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0
          || compoundingPeriod >= (totalYearsToCalculate * 12)) {
        return 0d;
      }
      return atcfCache[compoundingPeriod / MONTHS_IN_YEAR];

    }

  }

  /**
   * builds a table of after tax cash flows, for performance optimization
   * 
   * @param nocp
   *          int value number of compounding periods
   */
  private void createAtcfTable() {
    
    Arrays.fill(atcfCache, 0d);
    
    for (int year = 0; year < totalYearsToCalculate; year++) {
      atcfCache[year] = beforeTaxCashFlow.getValue(year * 12)
          - yearlyTaxOnIncome.getValue(year * 12);
    }
  }

  /**
   * builds a table of accumulated after tax cash flows, for performance
   * optimization
   */
  private void createAtcfAccumTable() {
    
    Arrays.fill(atcfAccumCache, 0d);
    
    for (int year = 0; year < totalYearsToCalculate; year++) {
      if (year == 0) {
        atcfAccumCache[year] = atcfCache[year];

      } else {
        atcfAccumCache[year] = atcfAccumCache[year - 1] + atcfCache[year];

      }
    }
  }

  /**
   * builds a table of net present values of after tax cash flows, for
   * performance optimization the exponent needs to be year+1 in order to pull
   * the value for the end of year (atcf) back to the beginning of year
   */
  private void createAtcfPvTable() {
    
    Arrays.fill(atcfPvCache, 0d);
    
    for (int year = 0; year < totalYearsToCalculate; year++) {
      atcfPvCache[year] = atcfCache[year]
          / Math.pow((1 + requiredRateOfReturn), year + 1);
    }
  }

  private class BeforeTaxCashFlow implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }

      return netOperatingIncome.getValue(compoundingPeriod)
          - (MONTHS_IN_YEAR * mortgagePayment.getValue(compoundingPeriod));
    }
  }

  private class CapitalizationRateOnPurchaseValue implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }

      return netOperatingIncome.getValue(compoundingPeriod)
          / totalPurchaseValue;
    }
  }

  private class CapitalizationRateOnProjectedValue implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }

      return netOperatingIncome.getValue(compoundingPeriod)
          / investmentFValue.getValue(compoundingPeriod);
    }
  }

  private class YearlyOutlay implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      } else {
        return operatingExpensesFValue.getValue(compoundingPeriod)
            + (MONTHS_IN_YEAR * mortgagePayment.getValue(compoundingPeriod));
      }
    }
  }

  private void createAmortizationTable(int nocp) {
    
    //clear the values in the array before starting
    Arrays.fill(amountOwed, 0d);
    Arrays.fill(interestAccumulator, 0d);
    Arrays.fill(interestPayment, 0d);
    Arrays.fill(principalPayment, 0d);
    Arrays.fill(pmiAccumulator, 0d);
    
    amountOwed[0] = loanAmount;
    


    // pick an arbitrary month for the mortgage payment - in this case, 1.
    mpValue = mortgagePayment.getValue(1);

    // each amount calculated is for the *end* of that month.
    for (int i = 0; i < nocp; i++) {



      if (i == 0) {
        interestPayment[i] = loanAmount * loanInterestRateMonthly;
      } else {
        interestPayment[i] = amountOwed[i - 1] * loanInterestRateMonthly;
      }

      if (i == 0) {
        interestAccumulator[i] = interestPayment[i];
      } else {
        interestAccumulator[i] = interestPayment[i]
            + interestAccumulator[i - 1];
      }

      principalPayment[i] = mpValue - interestPayment[i];

      if (i == 0) {
        amountOwed[i] = loanAmount - principalPayment[i];

      } else {
        amountOwed[i] = amountOwed[i - 1] - principalPayment[i];

      }
      
      if (amountOwed[i] > pmiEndsValue) {

        if (i == 0) {
          // pmi is determined if, before making that month's
          // payment, you were below the 80% point. That means
          // this is the correct spot for the pmiAccumulator expression
          pmiAccumulator[i] = pmiMonthly;

        } else {
          pmiAccumulator[i] = pmiMonthly + pmiAccumulator[i - 1];

        }
      } else {
        // if pmi does not apply, we still need to carry on
        // pmi accumulator until the end of the amortization table
        // for other algorithms to use
        if (i == 0) {
          pmiAccumulator[i] = 0d;
        } else {
          pmiAccumulator[i] = pmiAccumulator[i - 1];
        }
      }

    }
  }

  /**
   * Modified so this always provides the accumulated value for the end of year
   * 
   * @author byron
   * 
   */
  private class YearlyPrivateMortgageInsuranceAccum implements CalcValueGettable {

    public YearlyPrivateMortgageInsuranceAccum() {
    }

    
    public double getValue(int compoundingPeriod) {

      if (compoundingPeriod < 0) {
        return 0d;
      }
      if (compoundingPeriod >= nocp) {
        return pmiAccumulator[nocp - 1];
      }
      return pmiAccumulator[MONTHS_IN_YEAR * (compoundingPeriod / MONTHS_IN_YEAR) + 11];
    }

  }

  private class YearlyPrivateMortgageInsurance implements CalcValueGettable {

    public YearlyPrivateMortgageInsurance() {
    }

    
    public double getValue(int compoundingPeriod) {

      if (compoundingPeriod < 0 || compoundingPeriod >= nocp) {
        return 0d;
      }
      // notice we divide by 12 and multiply by 12. We are using the fact that
      // an integer
      // truncates the fraction to our advantage. We want the first month for
      // each year.
      // for example, ( 15 / 12 ) * 12 = 12.
      return 
          yearlyPrivateMortgageInsuranceAccum.getValue(MONTHS_IN_YEAR *((compoundingPeriod/MONTHS_IN_YEAR)+1)) -
          yearlyPrivateMortgageInsuranceAccum.getValue(MONTHS_IN_YEAR *(compoundingPeriod/MONTHS_IN_YEAR));
    }
  }

  private class YearlyAmountOwedValue implements CalcValueGettable {


    
    public double getValue(int compoundingPeriod) {

      if (compoundingPeriod < 0 || compoundingPeriod >= nocp) {
        return 0d;
      }
      return amountOwed[(MONTHS_IN_YEAR * (compoundingPeriod/MONTHS_IN_YEAR))+11];
    }
  }
  
  private class MonthlyAmountOwedValue implements CalcValueGettable {


    
    public double getValue(int compoundingPeriod) {

      if (compoundingPeriod < 0 || compoundingPeriod >= nocp) {
        return 0d;
      }
      return amountOwed[compoundingPeriod];
    }
  }

  private class LoanAmountValue implements CalcValueGettable {

    public LoanAmountValue() {
    }

    
    public double getValue(int compoundingPeriod) {

      if (compoundingPeriod < 0) {
        return 0d;
      }
      return loanAmount;
    }
  }

  private class MortgagePayment implements CalcValueGettable {

    public MortgagePayment() {
    }

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0 || compoundingPeriod >= nocp) {
        return 0d;
      }
      return mpValue;
    }
  }

  private double calculateMortgagePayment() {
    return loanAmount
        * (loanInterestRateMonthly / (1 - (1 / Math.pow(
            (1 + (loanInterestRateMonthly)), (double) nocp))));
  }

  private class YearlyMortgagePayment implements CalcValueGettable {

    public YearlyMortgagePayment() {
    }

    
    public double getValue(int compoundingPeriod) {

      if (compoundingPeriod < 0 || compoundingPeriod >= nocp) {
        return 0d;
      }
      return mortgagePayment.getValue(compoundingPeriod) * MONTHS_IN_YEAR;
    }
  }

  private class MonthlyInterestPaidAccumulatedValue implements CalcValueGettable {

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      // if we go past the end of the table, then interest accumulator remains
      // the last number
      if (compoundingPeriod >= nocp) {
        return interestAccumulator[nocp - 1];
      } else {
        return interestAccumulator[compoundingPeriod];
      }

    }
  } 
  
  private class YearlyInterestPaidAccumulatedValue implements CalcValueGettable {

    public YearlyInterestPaidAccumulatedValue() {
    }

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0) {
        return 0d;
      }
      // if we go past the end of the table, then interest accumulator remains
      // the last number
      if (compoundingPeriod >= nocp) {
        return interestAccumulator[nocp - 1];
      } else {
        return interestAccumulator[MONTHS_IN_YEAR * (compoundingPeriod / MONTHS_IN_YEAR)+11];
      }

    }
  }

  private class InterestPaymentValue implements CalcValueGettable {

    public InterestPaymentValue() {
    }

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0 || compoundingPeriod >= nocp) {
        return 0d;
      }

      return interestPayment[compoundingPeriod];
    }

  }

  private class YearlyPrincipalPaid implements CalcValueGettable {

    public YearlyPrincipalPaid() {
    }

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0 || compoundingPeriod > nocp) {
        return 0d;
      }
      // System.out.println(getClass().getName() + " calls: " +
      // numberOfCalls++);

      // we divide by 12 then multiply by 12 to get the month at the beginning
      // of the year.
      // This works because in integer division, it truncates the remainder
      // value.
      // so for example, 6 / 12 = 0, and 0 * 12 = 0. 25 / 12 = 2, and 2 * 12 =
      // 24
      if (compoundingPeriod >= 0 && compoundingPeriod < 12) {
        return
            loanAmount - 
            yearlyAmountOwedValue.getValue(MONTHS_IN_YEAR * (compoundingPeriod / MONTHS_IN_YEAR));
      } else {
      return 
          yearlyAmountOwedValue.getValue(MONTHS_IN_YEAR * ((compoundingPeriod / MONTHS_IN_YEAR)-1)) - 
          yearlyAmountOwedValue.getValue(MONTHS_IN_YEAR * (compoundingPeriod / MONTHS_IN_YEAR));
      }
    }
  }

  private class YearlyInterestPaid implements CalcValueGettable {

    public YearlyInterestPaid() {
    }

    
    public double getValue(int compoundingPeriod) {
      if (compoundingPeriod < 0 || compoundingPeriod > nocp) {
        return 0d;
      }
      // System.out.println(getClass().getName() + " calls: " +
      // numberOfCalls++);

      // we divide by 12 then multiply by 12 to get the month at the beginning
      // of the year.
      // This works because in integer division, it truncates the remainder
      // value.
      // so for example, 6 / 12 = 0, and 0 * 12 = 0. 25 / 12 = 2, and 2 * 12 =
      // 24

      return

      // to subtract Jan from December, we add 11
      yearlyInterestPaidAccumulatedValue.getValue((compoundingPeriod/MONTHS_IN_YEAR)*MONTHS_IN_YEAR)
          - yearlyInterestPaidAccumulatedValue.getValue(MONTHS_IN_YEAR * ((compoundingPeriod/MONTHS_IN_YEAR)-1));

    }

  }

}
