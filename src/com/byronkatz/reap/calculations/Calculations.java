package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataManager;
import com.byronkatz.reap.general.ValueEnum;

public class Calculations implements ValueSettable {

  private Double propertyTax;
  private Double closingCosts;
  private Double insurance;
  private Double generalExpenses;
  private Double inflationRate;
  private Double municipalExpenses;
  private Double totalPurchaseValue;
  private Double fixupCosts;
  private Double initialRent;
  private Double yearlyRealEstateAppreciationRate;
  private Double vacancyAndCreditLossRate;
  private Integer nocp;
  private Double loanAmount;
  private Double downPayment;
  private Double loanInterestRateMonthly;
  private Double[] interestAccumulator;
  private Double[] interestPayment;
  private Double[] principalPayment;
  private Double[] amountOwed;
  private Double mpValue;
  private Double pmiMonthly;
  private Double pmiEndsValue;
  private Double[] pmiAccumulator;
  public static final Double PMI_BOUNDARY_PERCENTAGE = 0.80D;
  private Integer totalYearsToCalculate;
  private Double requiredRateOfReturn;
  private Double atcfNpvAccumulator;
  private Double atcfAccumulator;
  private Double[] atcfCache;
  private Double yearlyLoanInterestRate;
  private Double mirr;
  private Double futureValuePositiveCashFlowsAccumulator;
  private Double presentValueNegativeCashFlowsAccumulator;
  private Double atcfTempValue;
  private Double aterTempValue;
  private Double brokerRate;
  private Double initialSellingExpenses;
  private Double buildingValue;
  private Double marginalTaxRate;
  private final Double DEPRECIATION_CONSTANT = 27.5d;
  private final Double TAX_ON_CAPITAL_GAINS = 0.15d;

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
  private CalcValueGettable interestPaidAccumulatedValue;
  private CalcValueGettable amountOwedValue;
  private CalcValueGettable loanAmountValue;
  private CalcValueGettable yearlyInterestPaid;
  private CalcValueGettable yearlyPrincipalPaid;
  private CalcValueGettable mortgagePayment;
  private CalcValueGettable yearlyMortgagePayment;
  private CalcValueGettable privateMortgageInsurance;
  private CalcValueGettable privateMortgageInsuranceAccum;
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
    fixupCosts = 0d;
    initialRent = 0d;
    yearlyRealEstateAppreciationRate = 0d;
    vacancyAndCreditLossRate = 0d;
    nocp = 0;
    loanAmount = 0d;
    downPayment = 0d;
    loanInterestRateMonthly = 0d;
    interestAccumulator = null;
    interestPayment =  null;
    principalPayment = null;
    amountOwed = null;
    mpValue = 0d;
    pmiMonthly = 0d;
    pmiEndsValue = 0d;
    pmiAccumulator = null;

    totalYearsToCalculate = 0;
    requiredRateOfReturn = 0d;
    atcfNpvAccumulator = 0d;
    atcfAccumulator = 0d;
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

    mirrObject                              = new MIRR();
    propertyTaxFValue                       = new PropertyTaxFValue();
    insuranceFValue                         = new InsuranceFValue();
    generalExpensesFValue                   = new GeneralExpensesFValue();
    operatingExpensesFValue                 = new OperatingExpensesFValue();
    municipalExpensesFValue                 = new MunicipalExpensesFValue();
    investmentFValue                        = new InvestmentFValue();
    monthlyRentFValue                       = new MonthlyRentFValue();
    yearlyGrossIncomeValue                  = new YearlyGrossIncomeValue();
    yearlyNetIncomeValue                    = new YearlyNetIncomeValue();
    netOperatingIncome                      = new NetOperatingIncome();
    taxableIncome                           = new TaxableIncome();
    interestPaymentValue                    = new InterestPaymentValue();
    interestPaidAccumulatedValue            = new InterestPaidAccumulatedValue();
    amountOwedValue                         = new AmountOwedValue();
    loanAmountValue                         = new LoanAmountValue();
    yearlyInterestPaid                      = new YearlyInterestPaid();
    yearlyPrincipalPaid                     = new YearlyPrincipalPaid();
    mortgagePayment                         = new MortgagePayment();
    yearlyMortgagePayment                   = new YearlyMortgagePayment();
    privateMortgageInsurance                = new PrivateMortgageInsurance();
    privateMortgageInsuranceAccum           = new PrivateMortgageInsuranceAccum();
    afterTaxCashFlow                        = new AfterTaxCashFlow();
    afterTaxCashFlowNPV                     = new AtcfNpv();
    afterTaxCashFlowAccumulator             = new AfterTaxCashFlowAccumulator();
    beforeTaxCashFlow                       = new BeforeTaxCashFlow();
    yearlyOutlay                            = new YearlyOutlay();
    capitalizationRateOnPurchaseValue       = new CapitalizationRateOnPurchaseValue();
    capitalizationRateOnProjectedValue      = new CapitalizationRateOnProjectedValue();
    firstDayCosts                           = new FirstDayCosts();
    npv                                     = new NPV();
    sellingExpensesFValue                   = new SellingExpensesFValue();
    brokerCutOfSale                         = new BrokerCutOfSale();
    afterTaxEquityReversion                 = new AfterTaxEquityReversion();
    aterPv                                  = new AterPV();
    yearlyDepreciation                      = new YearlyDepreciation();
    taxesDueAtSale                          = new TaxesDueAtSale();
    yearlyTaxOnIncome                       = new YearlyTaxOnIncome();
  }

  @Override
  public void setValues(DataManager dataManager) {
    this.dataManager = dataManager;
    assignDataManager(dataManager);
    loadCurrentUserInputValues();
    createAmortizationTable(nocp);
    createAtcfTable();

  }

  private void loadCurrentUserInputValues() {

    closingCosts                         = dataManager.getInputValue(ValueEnum.CLOSING_COSTS);
    propertyTax                          = dataManager.getInputValue(ValueEnum.PROPERTY_TAX                    );
    insurance                            = dataManager.getInputValue(ValueEnum.INITIAL_HOME_INSURANCE          );
    generalExpenses                      = dataManager.getInputValue(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES );
    inflationRate                        = dataManager.getInputValue(ValueEnum.INFLATION_RATE                  );
    yearlyRealEstateAppreciationRate     = dataManager.getInputValue(ValueEnum.REAL_ESTATE_APPRECIATION_RATE   );
    municipalExpenses                    = dataManager.getInputValue(ValueEnum.LOCAL_MUNICIPAL_FEES            );
    totalPurchaseValue                   = dataManager.getInputValue(ValueEnum.TOTAL_PURCHASE_VALUE            );
    fixupCosts                           = dataManager.getInputValue(ValueEnum.FIX_UP_COSTS                    );
    initialRent                          = dataManager.getInputValue(ValueEnum.ESTIMATED_RENT_PAYMENTS         );
    vacancyAndCreditLossRate             = dataManager.getInputValue(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE    );
    nocp                                 = dataManager.getInputValue(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS   ).intValue();
    downPayment                          = dataManager.getInputValue(ValueEnum.DOWN_PAYMENT                    );
    loanInterestRateMonthly              = dataManager.getInputValue(ValueEnum.YEARLY_INTEREST_RATE            )/12;
    totalYearsToCalculate                = dataManager.getInputValue(ValueEnum.EXTRA_YEARS).intValue() + nocp;
    requiredRateOfReturn                 = dataManager.getInputValue(ValueEnum.REQUIRED_RATE_OF_RETURN          );
    yearlyLoanInterestRate               = dataManager.getInputValue(ValueEnum.YEARLY_INTEREST_RATE             );
    brokerRate                           = dataManager.getInputValue(ValueEnum.SELLING_BROKER_RATE              );
    initialSellingExpenses               = dataManager.getInputValue(ValueEnum.GENERAL_SALE_EXPENSES            );
    buildingValue                        = dataManager.getInputValue(ValueEnum.BUILDING_VALUE                   );
    marginalTaxRate                      = dataManager.getInputValue(ValueEnum.MARGINAL_TAX_RATE                );
    pmiMonthly                           = dataManager.getInputValue(ValueEnum.PRIVATE_MORTGAGE_INSURANCE       );

    if (totalPurchaseValue > downPayment) {
      loanAmount = totalPurchaseValue - downPayment;
    } else {
      loanAmount = 0d;
    }

    pmiEndsValue = PMI_BOUNDARY_PERCENTAGE * totalPurchaseValue;

    interestPayment = new Double[nocp];
    amountOwed = new Double[nocp+1];
    interestAccumulator = new Double[nocp];
    principalPayment = new Double[nocp];
    pmiAccumulator = new Double[nocp];
    atcfCache      = new Double[totalYearsToCalculate];
  }

  private void assignDataManager(DataManager dataManager) {
    dataManager.addCalcValuePointers(
        ValueEnum.ATCF, afterTaxCashFlow);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_BEFORE_TAX_CASH_FLOW, beforeTaxCashFlow);    
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_OUTLAY, yearlyOutlay);
    dataManager.addCalcValuePointers(
        ValueEnum.CAP_RATE_ON_PROJECTED_VALUE, capitalizationRateOnProjectedValue);
    dataManager.addCalcValuePointers(
        ValueEnum.CAP_RATE_ON_PURCHASE_VALUE, capitalizationRateOnPurchaseValue);
    dataManager.addCalcValuePointers(
        ValueEnum.FIRST_DAY_COSTS, firstDayCosts);
    dataManager.addCalcValuePointers(
        ValueEnum.NPV, npv);
    dataManager.addCalcValuePointers(
        ValueEnum.ATCF_ACCUMULATOR, afterTaxCashFlowAccumulator);
    dataManager.addCalcValuePointers(
        ValueEnum.ATCF_NPV, afterTaxCashFlowNPV);
    dataManager.addCalcValuePointers(
        ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN, mirrObject);
    dataManager.addCalcValuePointers(
        ValueEnum.BROKER_CUT_OF_SALE, brokerCutOfSale);
    dataManager.addCalcValuePointers(
        ValueEnum.ATER, afterTaxEquityReversion);
    dataManager.addCalcValuePointers(
        ValueEnum.SELLING_EXPENSES, sellingExpensesFValue);    
    dataManager.addCalcValuePointers(
        ValueEnum.ATER_PV, aterPv);
    dataManager.addCalcValuePointers(
        ValueEnum.PROJECTED_HOME_VALUE, investmentFValue);
    dataManager.addCalcValuePointers(
        ValueEnum.MONTHLY_RENT_FV, monthlyRentFValue);
    dataManager.addCalcValuePointers(
        ValueEnum.GROSS_YEARLY_INCOME, yearlyGrossIncomeValue);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_INCOME, yearlyNetIncomeValue);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_NET_OPERATING_INCOME, netOperatingIncome);
    dataManager.addCalcValuePointers(
        ValueEnum.TAXABLE_INCOME, taxableIncome);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_DEPRECIATION, yearlyDepreciation);
    dataManager.addCalcValuePointers(
        ValueEnum.TAXES_DUE_AT_SALE, taxesDueAtSale);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_TAX_ON_INCOME, yearlyTaxOnIncome);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_PROPERTY_TAX, propertyTaxFValue);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_HOME_INSURANCE, insuranceFValue);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_GENERAL_EXPENSES, generalExpensesFValue);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_OPERATING_EXPENSES, operatingExpensesFValue);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_MUNICIPAL_FEES, municipalExpensesFValue);
    dataManager.addCalcValuePointers(
        ValueEnum.CURRENT_AMOUNT_OUTSTANDING, amountOwedValue);
    dataManager.addCalcValuePointers(
        ValueEnum.ACCUM_INTEREST, interestPaidAccumulatedValue);
    dataManager.addCalcValuePointers(
        ValueEnum.INTEREST_PAYMENT, interestPaymentValue);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_INTEREST_PAID, yearlyInterestPaid);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_PRINCIPAL_PAID, yearlyPrincipalPaid);
    dataManager.addCalcValuePointers(
        ValueEnum.MONTHLY_MORTGAGE_PAYMENT, mortgagePayment);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_MORTGAGE_PAYMENT, yearlyMortgagePayment);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_PRIVATE_MORTGAGE_INSURANCE_ACCUM, privateMortgageInsuranceAccum);
    dataManager.addCalcValuePointers(
        ValueEnum.YEARLY_PRIVATE_MORTGAGE_INSURANCE, privateMortgageInsurance);
    dataManager.addCalcValuePointers(
        ValueEnum.LOAN_AMOUNT, loanAmountValue);
  }

  /**
   * Future value of the monthly rent.  Gets incremented by REAR once each 12 months.
   * @author byron
   *
   */
  private class MonthlyRentFValue implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {  
      if (compoundingPeriod < 0 ) {return 0d;}
      return (initialRent * Math.pow(1 + yearlyRealEstateAppreciationRate, compoundingPeriod / MONTHS_IN_YEAR));
    }

  }

  /**
   * The yearly gross income.  The monthly rent times 12.
   * @author byron
   *
   */
  private class YearlyGrossIncomeValue implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0 ) {return 0d;}
      return 12 * (initialRent * Math.pow(1 + yearlyRealEstateAppreciationRate, compoundingPeriod / MONTHS_IN_YEAR));
    }

  }

  /**
   * returns the yearly net income, which is the yearly gross income times (1 minus vacancy)
   * @author byron
   *
   */
  private class YearlyNetIncomeValue implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0 ) {return 0d;}
      return MONTHS_IN_YEAR * (1-vacancyAndCreditLossRate) * 
          (initialRent * Math.pow(1 + yearlyRealEstateAppreciationRate, compoundingPeriod / MONTHS_IN_YEAR));
    }

  }

  private class NetOperatingIncome implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}
      return 
          dataManager.getCalcValue(ValueEnum.YEARLY_INCOME, compoundingPeriod) -
          dataManager.getCalcValue(ValueEnum.YEARLY_OPERATING_EXPENSES, compoundingPeriod);
    }

  }

  private class TaxableIncome implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}
      if (calcValue(compoundingPeriod) < 0) {
        return 0d;
      } else {
        return calcValue(compoundingPeriod);
      }
    }

    private Double calcValue(Integer compoundingPeriod) {
      return 
          dataManager.getCalcValue(ValueEnum.YEARLY_NET_OPERATING_INCOME, compoundingPeriod) -
          dataManager.getCalcValue(ValueEnum.YEARLY_INTEREST_PAID, compoundingPeriod) -
          dataManager.getCalcValue(ValueEnum.YEARLY_DEPRECIATION, compoundingPeriod);
    }
  }


  private class YearlyDepreciation implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}
      return buildingValue / DEPRECIATION_CONSTANT;
    }

  }

  private class YearlyTaxOnIncome implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}

      return 
          dataManager.getCalcValue(ValueEnum.TAXABLE_INCOME, compoundingPeriod) * 
          marginalTaxRate;
    }

  }

  private class TaxesDueAtSale implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}
      if (taxesDueFunction(compoundingPeriod) < 0) {return 0d;}

      else {
        return taxesDueFunction(compoundingPeriod);
      }
    }

    private Double taxesDueFunction(Integer compoundingPeriod) {
      return (dataManager.getCalcValue(ValueEnum.PROJECTED_HOME_VALUE, compoundingPeriod) - 
          dataManager.getCalcValue(ValueEnum.BROKER_CUT_OF_SALE, compoundingPeriod) -
          totalPurchaseValue + 
          (dataManager.getCalcValue(ValueEnum.YEARLY_DEPRECIATION, compoundingPeriod) * 
              ((compoundingPeriod/MONTHS_IN_YEAR)+1))) *
              TAX_ON_CAPITAL_GAINS;
    }

  }

  /**
   * compounding yearly rather than monthly.
   * @author byron
   *
   */
  private class MunicipalExpensesFValue implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}
      return (municipalExpenses * Math.pow(1 + inflationRate, compoundingPeriod / MONTHS_IN_YEAR));
    }

  }


  /**
   * Increments the property tax at the rate of REAR once a year.
   * We assert that this value should be non-null and greater than 0.  Less than zero
   * does not make sense.
   * @author byron
   *
   */
  private class PropertyTaxFValue implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}
      return (propertyTax * Math.pow(1 + yearlyRealEstateAppreciationRate, compoundingPeriod / MONTHS_IN_YEAR));
    }

  }

  /**
   * Increments the insurance value at the rate of inflation once a year.
   * We assert that this value should be non-null and greater than 0.  Less than zero
   * does not make sense.
   * @author byron
   *
   */
  private class InsuranceFValue implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}
      return (insurance * Math.pow(1 + inflationRate, compoundingPeriod / MONTHS_IN_YEAR));
    }

  }

  /**
   * Increments the general expenses value at the rate of inflation once a year.
   * We assert that this value should be non-null and greater than 0.  Less than zero
   * does not make sense.
   * @author byron
   *
   */
  private class GeneralExpensesFValue implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}
      return (generalExpenses * Math.pow(1 + inflationRate, compoundingPeriod / MONTHS_IN_YEAR));
    }

  }

  /**
   * Increments the general expenses value at the rate of inflation once a year.
   * We assert that this value should be non-null and greater than 0.  Less than zero
   * does not make sense.
   * @author byron
   * 
   *
   */
  private class OperatingExpensesFValue implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}
      return ((generalExpenses + insurance + municipalExpenses) * 
          Math.pow(1 + inflationRate, compoundingPeriod / 12)) +
          propertyTax * Math.pow(1+yearlyRealEstateAppreciationRate, compoundingPeriod / MONTHS_IN_YEAR);
    }

  }


  private class InvestmentFValue implements CalcValueGettable {
    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}
      if (compoundingPeriod == 0) {return totalPurchaseValue;}
      else {
        return totalPurchaseValue * Math.pow(1 + yearlyRealEstateAppreciationRate / MONTHS_IN_YEAR, compoundingPeriod);
      }
    }
  }



  private class BrokerCutOfSale implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}

      return (brokerRate * 
          dataManager.getCalcValue(ValueEnum.PROJECTED_HOME_VALUE, compoundingPeriod));
    }

  }

  private class AfterTaxEquityReversion implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}

      return 
          dataManager.getCalcValue(ValueEnum.PROJECTED_HOME_VALUE, compoundingPeriod) -
          dataManager.getCalcValue(ValueEnum.BROKER_CUT_OF_SALE, compoundingPeriod) -
          dataManager.getCalcValue(ValueEnum.SELLING_EXPENSES, compoundingPeriod) -
          dataManager.getCalcValue(ValueEnum.CURRENT_AMOUNT_OUTSTANDING, compoundingPeriod) -
          dataManager.getCalcValue(ValueEnum.TAXES_DUE_AT_SALE, compoundingPeriod);
    }

  }

  private class AterPV implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      return (afterTaxEquityReversion.getValue(compoundingPeriod) / 
          Math.pow((1 + requiredRateOfReturn/MONTHS_IN_YEAR), compoundingPeriod));
    }

  }

  private class SellingExpensesFValue implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}

      return initialSellingExpenses * Math.pow(1 + inflationRate , compoundingPeriod / MONTHS_IN_YEAR);
    }

  }


  private class FirstDayCosts implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {

      return 
          downPayment +
          closingCosts +
          fixupCosts;
    }
  }  

  private class NPV implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}
      return 
          ((- firstDayCosts.getValue(compoundingPeriod)) + 
              afterTaxCashFlowNPV.getValue(compoundingPeriod) +
              dataManager.getCalcValue(ValueEnum.ATER_PV, compoundingPeriod));

    } 
  }

  private class MIRR implements CalcValueGettable {

    @Override
    public Double getValue (Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}

      presentValueNegativeCashFlowsAccumulator = 0d;
      futureValuePositiveCashFlowsAccumulator = 0d;
      atcfTempValue = 0d;
      aterTempValue = 0d;

      //cash flows for mirr:

      //step 1, first day costs:
      presentValueNegativeCashFlowsAccumulator +=
          -firstDayCosts.getValue(compoundingPeriod);

      //step 2: after tax cash flows - different action depending on sign
      for (int year = 0; year < (compoundingPeriod / MONTHS_IN_YEAR); year++) {


        atcfTempValue = dataManager.getCalcValue(ValueEnum.ATCF, year*MONTHS_IN_YEAR);

        if (atcfTempValue < 0d) {
          presentValueNegativeCashFlowsAccumulator +=
              atcfTempValue /  Math.pow(1 + yearlyLoanInterestRate, year);
        } else {
          futureValuePositiveCashFlowsAccumulator +=
              atcfTempValue * 
              Math.pow(1 + requiredRateOfReturn, totalYearsToCalculate - year);
        }

      }

      //step 3: ater
      aterTempValue = dataManager.getCalcValue(ValueEnum.ATER, compoundingPeriod);
      if (aterTempValue < 0.0f) {
        presentValueNegativeCashFlowsAccumulator += 
            aterTempValue / Math.pow(1 + yearlyLoanInterestRate, compoundingPeriod/MONTHS_IN_YEAR);
      } else if (aterTempValue > 0.0f) {
        futureValuePositiveCashFlowsAccumulator += aterTempValue;
      }


      //step 4: division
      //we don't want divide by zero errors.
      if (! (presentValueNegativeCashFlowsAccumulator == 0f)) {
        mirr = Math.pow(
            futureValuePositiveCashFlowsAccumulator / 
            - presentValueNegativeCashFlowsAccumulator, 
            (1.0f/compoundingPeriod)) - 1;
      } else {
        mirr = 0d;
      }

      return mirr;
    }
  }

  private class AtcfNpv implements CalcValueGettable {

    @Override
    public Double getValue (Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}
      atcfNpvAccumulator = 0d;
      for (int i = 0; i < (compoundingPeriod / MONTHS_IN_YEAR); i++) {
        atcfNpvAccumulator += getAtcfNpv(i);
      }
      return atcfNpvAccumulator;
    }

    private Double getAtcfNpv(Integer compoundingPeriod) {
      return (afterTaxCashFlow.getValue(compoundingPeriod / MONTHS_IN_YEAR) / 
          Math.pow((1 + requiredRateOfReturn), compoundingPeriod / MONTHS_IN_YEAR));
    }

  }

  private class AfterTaxCashFlowAccumulator implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}
      atcfAccumulator = 0d;
      for (int i = 0; i < (compoundingPeriod / MONTHS_IN_YEAR); i++) {
        atcfAccumulator += dataManager.getCalcValue(ValueEnum.ATCF, i);
      }
      return atcfAccumulator;
    }

  }

  private class AfterTaxCashFlow implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0 || compoundingPeriod >= (totalYearsToCalculate * 12)) {return 0d;}
      return atcfCache[compoundingPeriod/MONTHS_IN_YEAR];
          
    }

  }
  
  /**
   * builds a table of after tax cash flows, for performance optimization
   * @param nocp Integer value number of compounding periods
   */
  private void createAtcfTable() {
    for (int year = 0; year < totalYearsToCalculate; year++) {
      atcfCache[year] =dataManager.getCalcValue(ValueEnum.YEARLY_BEFORE_TAX_CASH_FLOW, year*12) -
          dataManager.getCalcValue(ValueEnum.YEARLY_TAX_ON_INCOME, year*12);
    }
  }

  private class BeforeTaxCashFlow implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}

      return dataManager.getCalcValue(ValueEnum.YEARLY_NET_OPERATING_INCOME, compoundingPeriod) -
          MONTHS_IN_YEAR * dataManager.getCalcValue(ValueEnum.MONTHLY_MORTGAGE_PAYMENT, compoundingPeriod);
    }
  }

  private class CapitalizationRateOnPurchaseValue implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}

      return dataManager.getCalcValue(ValueEnum.YEARLY_NET_OPERATING_INCOME, compoundingPeriod) /
          dataManager.getInputValue(ValueEnum.TOTAL_PURCHASE_VALUE);
    }
  }

  private class CapitalizationRateOnProjectedValue implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}

      return dataManager.getCalcValue(ValueEnum.YEARLY_NET_OPERATING_INCOME, compoundingPeriod) /
          dataManager.getCalcValue(ValueEnum.PROJECTED_HOME_VALUE, compoundingPeriod);
    }
  }

  private class YearlyOutlay implements CalcValueGettable {

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0) {return 0d;}
      else {
        return dataManager.getCalcValue(ValueEnum.YEARLY_OPERATING_EXPENSES, compoundingPeriod) +
            MONTHS_IN_YEAR * dataManager.getCalcValue(ValueEnum.MONTHLY_MORTGAGE_PAYMENT, compoundingPeriod);
      }
    }
  }



  private void createAmortizationTable(int nocp) {
    amountOwed[0] = loanAmount;

    //pick an arbitrary month for the mortgage payment - in this case, 1.
    mpValue = dataManager.getCalcValue(ValueEnum.MONTHLY_MORTGAGE_PAYMENT, 1);


    //each amount calculated is for the *beginning* of that month.
    for (int i = 0; i < nocp; i++) {

      if (amountOwed[i] >= pmiEndsValue) {
        if (i == 0) {
          pmiAccumulator[i] = pmiMonthly;
        } else {
          pmiAccumulator[i] = pmiMonthly + pmiAccumulator[i-1];
        }
      } else {
        if (i == 0) {
          pmiAccumulator[i] = 0d;
        } else {
          pmiAccumulator[i] = pmiAccumulator[i-1];
        }
      }

      interestPayment[i] = amountOwed[i] * loanInterestRateMonthly;
      if (i == 0) {
        interestAccumulator[i] = interestPayment[i];
      } else {
        interestAccumulator[i] = interestPayment[i] + interestAccumulator[i-1];
      }
      principalPayment[i] = mpValue - interestPayment[i];
      amountOwed[i+1] = amountOwed[i] - principalPayment[i];
    }
  }

  private class PrivateMortgageInsuranceAccum implements CalcValueGettable {

    public PrivateMortgageInsuranceAccum() {
    }

    @Override
    public Double getValue(Integer compoundingPeriod) {

      if (compoundingPeriod < 0 ) {return 0d;}
      if (compoundingPeriod >= nocp) {return pmiAccumulator[nocp-1];}
      return pmiAccumulator[compoundingPeriod];
    }

  }

  private class PrivateMortgageInsurance implements CalcValueGettable {


    public PrivateMortgageInsurance() {
    }

    @Override
    public Double getValue(Integer compoundingPeriod) {

      if (compoundingPeriod < 0 || compoundingPeriod >= nocp) {return 0d;}
      //notice we divide by 12 and multiply by 12.  We are using the fact that an integer
      //truncates the fraction to our advantage.  We want the first month for each year.
      //for example, ( 15 / 12 ) * 12 = 12.
      return 
          dataManager.getCalcValue(ValueEnum.YEARLY_PRIVATE_MORTGAGE_INSURANCE_ACCUM,
              (MONTHS_IN_YEAR * ((compoundingPeriod/MONTHS_IN_YEAR)+1))) -
              dataManager.getCalcValue(ValueEnum.YEARLY_PRIVATE_MORTGAGE_INSURANCE_ACCUM,
                  (compoundingPeriod / MONTHS_IN_YEAR) * MONTHS_IN_YEAR);
    }
  }

  private class AmountOwedValue implements CalcValueGettable {

    public AmountOwedValue() {
    }

    @Override
    public Double getValue(Integer compoundingPeriod) {

      if (compoundingPeriod < 0 || compoundingPeriod >= nocp) {return 0d;}
      if (compoundingPeriod == 0) { return loanAmount; }
      return amountOwed[compoundingPeriod];
    }
  }

  private class LoanAmountValue implements CalcValueGettable {

    public LoanAmountValue() {
    }

    @Override
    public Double getValue (Integer compoundingPeriod) {

      if (compoundingPeriod < 0) {return 0d;}
      return loanAmount;
    }
  }

  private class MortgagePayment implements CalcValueGettable {

    public MortgagePayment() {
    }

    @Override
    public Double getValue(Integer compoundingPeriod) {

      if (compoundingPeriod < 0 || compoundingPeriod >= nocp) {return 0d;}
      return loanAmount * (loanInterestRateMonthly 
          / (1 - (1 / Math.pow((1 + (loanInterestRateMonthly)),
              nocp.doubleValue()))));
    } 
  }

  private class YearlyMortgagePayment implements CalcValueGettable {

    public YearlyMortgagePayment() {
    }

    @Override
    public Double getValue(Integer compoundingPeriod) {

      if (compoundingPeriod < 0 || compoundingPeriod >= nocp) {return 0d;}
      return mortgagePayment.getValue(compoundingPeriod) * MONTHS_IN_YEAR;
    }
  }


  private class InterestPaidAccumulatedValue implements CalcValueGettable {

    public InterestPaidAccumulatedValue() {
    }

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0 ) {return 0d;}
      //if we go past the end of the table, then interest accumulator remains the last number
      if (compoundingPeriod >= nocp) {
        return interestAccumulator[nocp-1];
      } else {
        return interestAccumulator[compoundingPeriod];
      }

    }
  }


  private class InterestPaymentValue implements CalcValueGettable {

    public InterestPaymentValue() {
    }

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0 || compoundingPeriod >= nocp ) {return 0d;}

      return interestPayment[compoundingPeriod];    
    }

  }


  private class YearlyPrincipalPaid implements CalcValueGettable {

    public YearlyPrincipalPaid() {
    }

    @Override
    public Double getValue (Integer compoundingPeriod) {
      if (compoundingPeriod < 0 || compoundingPeriod > nocp ) {return 0d;}
      //        System.out.println(getClass().getName() + " calls: " + numberOfCalls++);

      //we divide by 12 then multiply by 12 to get the month at the beginning of the year.
      //This works because in integer division, it truncates the remainder value.
      // so for example, 6 / 12 = 0, and 0 * 12 = 0.  25 / 12 = 2, and 2 * 12 = 24
      return
          dataManager.getCalcValue(ValueEnum.CURRENT_AMOUNT_OUTSTANDING, MONTHS_IN_YEAR * (compoundingPeriod / MONTHS_IN_YEAR)) -
          dataManager.getCalcValue(ValueEnum.CURRENT_AMOUNT_OUTSTANDING, MONTHS_IN_YEAR * (compoundingPeriod / MONTHS_IN_YEAR)+MONTHS_IN_YEAR);

    }
  }

  private class YearlyInterestPaid implements CalcValueGettable {



    public YearlyInterestPaid() {
    }

    @Override
    public Double getValue(Integer compoundingPeriod) {
      if (compoundingPeriod < 0 || compoundingPeriod > nocp ) {return 0d;}
      //        System.out.println(getClass().getName() + " calls: " + numberOfCalls++);

      //we divide by 12 then multiply by 12 to get the month at the beginning of the year.
      //This works because in integer division, it truncates the remainder value.
      // so for example, 6 / 12 = 0, and 0 * 12 = 0.  25 / 12 = 2, and 2 * 12 = 24
      return dataManager.getCalcValue(ValueEnum.ACCUM_INTEREST, MONTHS_IN_YEAR * (compoundingPeriod / MONTHS_IN_YEAR)+MONTHS_IN_YEAR) -
          dataManager.getCalcValue(ValueEnum.ACCUM_INTEREST, MONTHS_IN_YEAR * (compoundingPeriod / MONTHS_IN_YEAR));

    }

  }



}
