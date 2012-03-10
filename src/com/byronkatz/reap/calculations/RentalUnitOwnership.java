package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.ValueEnum;

public class RentalUnitOwnership {
  
  private Double marginalTaxRate;
  private Double buildingValue;
  private Double yearlyDepreciation = 0.0d;
  private Double firstDay;
  private Double npvAccumulator;

  private Double municipalFees;
  private Double propertyTax;
  private Double yearlyRealEstateAppreciationRate;
  private Double yearlyInflationRate;
  private Double monthlyInflationRate;
  private Double monthlyRealEstateAppreciationRate;
  private Double yearlyRequiredRateOfReturn;
  private Double atcfAccumulator;
  private Double yearlyNPVSummation;

  private Integer extraYears;
  private Mortgage mortgage;
  private Rental rental;
  private EstateValue estateValue;
  private ModifiedInternalRateOfReturn mirr;
  private EquityReversion equityReversion;
  
  public static final Double RESIDENTIAL_DEPRECIATION_YEARS = 27.5d;

  
  private DataController dataController;
  
  public RentalUnitOwnership(DataController dataController) {
    
    this.dataController = dataController;
    estateValue = new EstateValue(dataController);
    mortgage = new Mortgage(dataController, estateValue.getOriginalEstateValue());
    
    yearlyRequiredRateOfReturn = dataController.getValueAsDouble(ValueEnum.REQUIRED_RATE_OF_RETURN);
    mirr = new ModifiedInternalRateOfReturn( dataController,
        mortgage.getYearlyInterestRate(), yearlyRequiredRateOfReturn);
    equityReversion = new EquityReversion(dataController, estateValue, this, mortgage);

    rental = new Rental(dataController, this);

    
    municipalFees = dataController.getValueAsDouble(ValueEnum.LOCAL_MUNICIPAL_FEES);
    extraYears = dataController.getValueAsDouble(ValueEnum.EXTRA_YEARS).intValue();
    propertyTax = dataController.getValueAsDouble(ValueEnum.PROPERTY_TAX);
    yearlyRealEstateAppreciationRate = dataController.getValueAsDouble(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);
    monthlyRealEstateAppreciationRate = yearlyRealEstateAppreciationRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    yearlyInflationRate = dataController.getValueAsDouble(ValueEnum.INFLATION_RATE);
    monthlyInflationRate = yearlyInflationRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    marginalTaxRate = dataController.getValueAsDouble(ValueEnum.MARGINAL_TAX_RATE);
    buildingValue = dataController.getValueAsDouble(ValueEnum.BUILDING_VALUE);
    yearlyDepreciation = buildingValue / RESIDENTIAL_DEPRECIATION_YEARS;
    
    atcfAccumulator = 0.0d;
    yearlyNPVSummation = 0.0d;
    firstDay = 0.0d;
    npvAccumulator = 0.0d;

  }
  
  public EquityReversion getEquityReversion() {
    return equityReversion;
  }
  
  public ModifiedInternalRateOfReturn getModifiedInternalRateOfReturn() {
    return mirr;
  }
  
  public EstateValue getEstateValue() {
    return estateValue;
  }
  
  public Mortgage getMortgage() {
    return mortgage;
  }
  
  public Rental getRental() {
    return rental;
  }
  
  public Double getYearlyRequiredRateOfReturn() {
    return yearlyRequiredRateOfReturn;
  }
  
  public Double getMonthlyRequiredRateOfReturn() {
    return yearlyRequiredRateOfReturn / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
  }
  
  public Double getYearlyDepreciation() {
    return yearlyDepreciation;
  }
  
  public Double getMarginalTaxRate() {
    return marginalTaxRate;
  }
  
  public Double getYearlyRealEstateAppreciationRate() {
    return yearlyRealEstateAppreciationRate;
  }
  
  public Double getMonthlyRealEstateAppreciationRate() {
    return monthlyRealEstateAppreciationRate;
  }
  
  public Double getPropertyTax() {
    return propertyTax;
  }
  

  
  public Double getFVPropertyTax(int year) {
    
    Integer compoundingPeriodDesired = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    final Double yearlyPropertyTax = getPropertyTax() * getFVRear(compoundingPeriodDesired); 
    dataController.setValueAsDouble(ValueEnum.YEARLY_PROPERTY_TAX, yearlyPropertyTax, year);  
  
    return yearlyPropertyTax;
  }
  
  public Double getFVMunicipalFees(int year) {
	    
	    Integer compoundingPeriodDesired = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
	    final Double yearlyMunicipalFees = municipalFees * getFVMir(compoundingPeriodDesired); 
	    dataController.setValueAsDouble(ValueEnum.YEARLY_MUNICIPAL_FEES, yearlyMunicipalFees, year);
	    return yearlyMunicipalFees;
	  }
  
  public Double getFVRear(int compoundingPeriodDesired) {
    return Math.pow(1 + monthlyRealEstateAppreciationRate, compoundingPeriodDesired);
  }
  
  public Double getFVMir(int compoundingPeriodDesired) {
    return Math.pow(1 + monthlyInflationRate, compoundingPeriodDesired);
  }
  
  public Double calculateYearlyBeforeTaxCashFlow(int year) {
    // cashflowIn - cashflowOut

    final Double yearlyPrivateMortgageInsurance = mortgage.getYearlyPmi(year);
    final Double monthlyMortgagePayment = mortgage.getMonthlyMortgagePayment(year);

    final Double yearlyOperatingExpenses = getFVPropertyTax(year) + getFVMunicipalFees(year) + 
    		 rental.getFVYearlyGeneralExpenses(year) + 
    		rental.getFVYearlyHomeInsurance(year) + yearlyPrivateMortgageInsurance;
    dataController.setValueAsDouble(ValueEnum.YEARLY_OPERATING_EXPENSES, yearlyOperatingExpenses , year);
    
    final Double yearlyOutlay = yearlyOperatingExpenses + (monthlyMortgagePayment * 12);
    
    dataController.setValueAsDouble(ValueEnum.YEARLY_OUTLAY, yearlyOutlay, year);
    
    final Double yearlyNetOperatingIncome = rental.getFVNetYearlyIncome(year) - yearlyOperatingExpenses;
    dataController.setValueAsDouble(ValueEnum.YEARLY_NET_OPERATING_INCOME, yearlyNetOperatingIncome, year);
    final Double yearlyBeforeTaxCashFlow = yearlyNetOperatingIncome - (monthlyMortgagePayment * 12);
    
    final Double capitalizationRateOnPurchaseValue = yearlyNetOperatingIncome / estateValue.getOriginalEstateValue();
    dataController.setValueAsDouble(ValueEnum.CAP_RATE_ON_PURCHASE_VALUE, capitalizationRateOnPurchaseValue, year);
    
    final Double capitalizationRateOnProjectedValue = yearlyNetOperatingIncome / estateValue.getEstateValue(year);
    dataController.setValueAsDouble(ValueEnum.CAP_RATE_ON_PROJECTED_VALUE, capitalizationRateOnProjectedValue, year);
    
    dataController.setValueAsDouble(
        ValueEnum.YEARLY_BEFORE_TAX_CASH_FLOW, yearlyBeforeTaxCashFlow, year);
    return yearlyBeforeTaxCashFlow;
  }
  
  public Double calculateYearlyAfterTaxCashFlow(int year, int monthCPModifier, int prevYearMonthCPModifier) {

    final Double yearlyBeforeTaxCashFlow = calculateYearlyBeforeTaxCashFlow(year);
    final Double taxableIncome = calculateTaxableIncome(year, monthCPModifier, prevYearMonthCPModifier, yearlyBeforeTaxCashFlow);

    final Double yearlyTaxes = taxableIncome * getMarginalTaxRate();
    dataController.setValueAsDouble(ValueEnum.YEARLY_TAX_ON_INCOME, yearlyTaxes, year);
    
    final Double yearlyAfterTaxCashFlow = yearlyBeforeTaxCashFlow - yearlyTaxes;
    dataController.setValueAsDouble(ValueEnum.ATCF, yearlyAfterTaxCashFlow, year);

    return yearlyAfterTaxCashFlow;
  }
  
  public Double calculateTaxableIncome(int year, int monthCPModifier, int prevYearMonthCPModifier, Double yearlyBeforeTaxCashFlow) {

    final Double yearlyPrincipalPaid = mortgage.calculateYearlyPrincipalPaid(year, monthCPModifier, prevYearMonthCPModifier);

    Double taxableIncome = (yearlyBeforeTaxCashFlow + yearlyPrincipalPaid - getYearlyDepreciation());

    // doesn't make sense to tax negative income...but should this be used to offset taxes? hmmm...
    if (taxableIncome <= 0) {
      taxableIncome = 0.0d;
    }

    dataController.setValueAsDouble(ValueEnum.TAXABLE_INCOME, taxableIncome, year);
    return taxableIncome;
  }
  
  public Double calculateYearlyRentalIncomeNPV(int year, int monthCPModifier, int prevYearMonthCPModifier) {


    final Double yearlyAfterTaxCashFlow = calculateYearlyAfterTaxCashFlow(year, monthCPModifier, prevYearMonthCPModifier);
    atcfAccumulator += yearlyAfterTaxCashFlow;
    dataController.setValueAsDouble(ValueEnum.ATCF_ACCUMULATOR, atcfAccumulator, year);

    //mirr operates on yearly values, not monthly
    mirr.calculateMirr(year, yearlyAfterTaxCashFlow, null);


    final Double yearlyDiscountRateDivisor = Math.pow(1 + getYearlyRequiredRateOfReturn(), year);
    yearlyNPVSummation += yearlyAfterTaxCashFlow / yearlyDiscountRateDivisor;

    dataController.setValueAsDouble(ValueEnum.ATCF_NPV, yearlyNPVSummation, year);
    return yearlyNPVSummation;
  }
  
  public void crunchCalculation(int yearsToCalculate) {

    /*note: many of the equations below are calculated using monthly variables.  This is done
     * when the reality of the equation is monthly.  For example, in the final summation of
     * NPV, the equation is (income - outlay) / (1 + discountRate/12)^numberOfMonthsAtPoint.
     * In other situations, such as taxes, they are only assessed yearly so their "reality" is yearly.
     * Sorry if that is a bad terminology, I am writing this at 6:30 in the morning!
     * Think about that a bit before making changes.
     */

    firstDay = mortgage.getDownPayment() + mortgage.getClosingCosts() + estateValue.getFixupCosts();

    mirr.calculateMirr(0, -firstDay, null);

    
//    for (int year = 1; year <=( mortgage.getYearlyNumberOfCompoundingPeriods() + extraYears); year++) {
      for (int year = 1; year <=( yearsToCalculate); year++) {

      final Integer monthCPModifier = year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
      final Integer prevYearMonthCPModifier = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;

      final Double atcfNPVSummation = calculateYearlyRentalIncomeNPV(year, monthCPModifier, prevYearMonthCPModifier);

      final Double adjustedAter = equityReversion.calculatePresentValueAter(year);  

      npvAccumulator = -firstDay + atcfNPVSummation + adjustedAter;
      dataController.setValueAsDouble(ValueEnum.NPV, npvAccumulator, year);

      final Double accumulatedInterest = mortgage.getAccumulatedInterestPaymentsAtPoint(year);

      final Double accumulatedInterestPreviousYear = mortgage.getAccumulatedInterestPaymentsAtPoint(year - 1);
      final Double yearlyInterestPaid = accumulatedInterest - accumulatedInterestPreviousYear;
      dataController.setValueAsDouble(ValueEnum.YEARLY_INTEREST_PAID, yearlyInterestPaid, year);

    }
  }

public Double getMunicipalFees() {
	return municipalFees;
}

public void setMunicipalFees(Double municipalFees) {
	this.municipalFees = municipalFees;
}

}
