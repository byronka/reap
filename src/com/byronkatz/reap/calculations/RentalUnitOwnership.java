package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.ValueEnum;

public class RentalUnitOwnership {
  
  private Float marginalTaxRate;
  private Float buildingValue;
  private Float yearlyDepreciation = 0.0f;
  private Float firstDay;
  private Float npvAccumulator;

  private Float municipalFees;
  private Float propertyTax;
  private Float fixupCosts;
  private Float yearlyRealEstateAppreciationRate;
  private Float yearlyInflationRate;
  private Float monthlyInflationRate;
  private Float monthlyRealEstateAppreciationRate;
  private Float yearlyRequiredRateOfReturn;
  private Float atcfAccumulator;
  private Float yearlyNPVSummation;
  
  private Mortgage mortgage;
  private Rental rental;
  private EstateValue estateValue;
  private ModifiedInternalRateOfReturn mirr;
  private EquityReversion equityReversion;
  
  public static final Float RESIDENTIAL_DEPRECIATION_YEARS = 27.5f;

  
  private DataController dataController;
  
  public RentalUnitOwnership(DataController dataController) {
    
    this.dataController = dataController;
    estateValue = new EstateValue(dataController);
    mortgage = new Mortgage(dataController, estateValue.getEstateValue(0));
    
    yearlyRequiredRateOfReturn = dataController.getValueAsFloat(ValueEnum.REQUIRED_RATE_OF_RETURN);
    mirr = new ModifiedInternalRateOfReturn( dataController,
        mortgage.getYearlyInterestRate(), yearlyRequiredRateOfReturn);
    equityReversion = new EquityReversion(dataController, estateValue, this);

    rental = new Rental(dataController, this);

    
    fixupCosts = dataController.getValueAsFloat(ValueEnum.FIX_UP_COSTS);
    municipalFees = dataController.getValueAsFloat(ValueEnum.LOCAL_MUNICIPAL_FEES);
    propertyTax = dataController.getValueAsFloat(ValueEnum.PROPERTY_TAX);
    yearlyRealEstateAppreciationRate = dataController.getValueAsFloat(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);
    monthlyRealEstateAppreciationRate = yearlyRealEstateAppreciationRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    yearlyInflationRate = dataController.getValueAsFloat(ValueEnum.INFLATION_RATE);
    monthlyInflationRate = yearlyInflationRate / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    marginalTaxRate = dataController.getValueAsFloat(ValueEnum.MARGINAL_TAX_RATE);
    buildingValue = dataController.getValueAsFloat(ValueEnum.BUILDING_VALUE);
    yearlyDepreciation = buildingValue / RESIDENTIAL_DEPRECIATION_YEARS;
    
    atcfAccumulator = 0.0f;
    yearlyNPVSummation = 0.0f;
    firstDay = 0.0f;
    npvAccumulator = 0.0f;

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
  
  public Float getYearlyRequiredRateOfReturn() {
    return yearlyRequiredRateOfReturn;
  }
  
  public Float getMonthlyRequiredRateOfReturn() {
    return yearlyRequiredRateOfReturn / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
  }
  
  public Float getYearlyDepreciation() {
    return yearlyDepreciation;
  }
  
  public Float getMarginalTaxRate() {
    return marginalTaxRate;
  }
  
  public Float getYearlyRealEstateAppreciationRate() {
    return yearlyRealEstateAppreciationRate;
  }
  
  public Float getMonthlyRealEstateAppreciationRate() {
    return monthlyRealEstateAppreciationRate;
  }
  
  public Float getPropertyTax() {
    return propertyTax;
  }
  
  public Float getFixupCosts() {
    return fixupCosts;
  }
  
  public Float getFVPropertyTax(int year) {
    
    Integer compoundingPeriodDesired = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    final Float yearlyPropertyTax = getPropertyTax() * getFVRear(compoundingPeriodDesired); 
    dataController.setValueAsFloat(ValueEnum.YEARLY_PROPERTY_TAX, yearlyPropertyTax, year);  
  
    return yearlyPropertyTax;
  }
  
  public Float getFVMunicipalFees(int year) {
	    
	    Integer compoundingPeriodDesired = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
	    final Float yearlyMunicipalFees = municipalFees * getFVMir(compoundingPeriodDesired); 
	    dataController.setValueAsFloat(ValueEnum.YEARLY_MUNICIPAL_FEES, yearlyMunicipalFees, year);
	    return yearlyMunicipalFees;
	  }
  
  public Float getFVRear(int compoundingPeriodDesired) {
    return (float) Math.pow(1 + monthlyRealEstateAppreciationRate, (float) compoundingPeriodDesired);
  }
  
  public Float getFVMir(int compoundingPeriodDesired) {
    return (float) Math.pow(1 + monthlyInflationRate, (float) compoundingPeriodDesired);
  }
  
  public Float calculateYearlyBeforeTaxCashFlow(int year) {
    // cashflowIn - cashflowOut

    final Float yearlyPrivateMortgageInsurance = mortgage.getYearlyPmi(year);
    final Float monthlyMortgagePayment = mortgage.getMonthlyMortgagePayment(year);

    final Float yearlyOutlay = getFVPropertyTax(year) + getFVMunicipalFees(year) + 
    		(monthlyMortgagePayment * 12) + rental.getFVYearlyGeneralExpenses(year) + 
    		rental.getFVYearlyHomeInsurance(year) + yearlyPrivateMortgageInsurance;
    dataController.setValueAsFloat(ValueEnum.YEARLY_OUTLAY, yearlyOutlay, year);
    
    final Float yearlyBeforeTaxCashFlow = rental.getFVNetYearlyIncome(year) - yearlyOutlay;
    
    dataController.setValueAsFloat(
        ValueEnum.YEARLY_BEFORE_TAX_CASH_FLOW, yearlyBeforeTaxCashFlow, year);
    return yearlyBeforeTaxCashFlow;
  }
  
  public Float calculateYearlyAfterTaxCashFlow(int year, int monthCPModifier, int prevYearMonthCPModifier) {

    final Float yearlyBeforeTaxCashFlow = calculateYearlyBeforeTaxCashFlow(year);
    final Float taxableIncome = calculateTaxableIncome(year, monthCPModifier, prevYearMonthCPModifier, yearlyBeforeTaxCashFlow);

    final Float yearlyTaxes = taxableIncome * getMarginalTaxRate();
    final Float yearlyAfterTaxCashFlow = yearlyBeforeTaxCashFlow - yearlyTaxes;
    dataController.setValueAsFloat(ValueEnum.ATCF, yearlyAfterTaxCashFlow, year);

    return yearlyAfterTaxCashFlow;
  }
  
  public Float calculateTaxableIncome(int year, int monthCPModifier, int prevYearMonthCPModifier, Float yearlyBeforeTaxCashFlow) {

    final Float yearlyPrincipalPaid = mortgage.calculateYearlyPrincipalPaid(year, monthCPModifier, prevYearMonthCPModifier);

    Float taxableIncome = (yearlyBeforeTaxCashFlow + yearlyPrincipalPaid - getYearlyDepreciation());

    // doesn't make sense to tax negative income...but should this be used to offset taxes? hmmm...
    if (taxableIncome <= 0) {
      taxableIncome = 0.0f;
    }

    dataController.setValueAsFloat(ValueEnum.TAXABLE_INCOME, taxableIncome, year);
    return taxableIncome;
  }
  
  public Float calculateYearlyRentalIncomeNPV(int year, int monthCPModifier, int prevYearMonthCPModifier) {


    final Float yearlyAfterTaxCashFlow = calculateYearlyAfterTaxCashFlow(year, monthCPModifier, prevYearMonthCPModifier);
    atcfAccumulator += yearlyAfterTaxCashFlow;
    dataController.setValueAsFloat(ValueEnum.ATCF_ACCUMULATOR, atcfAccumulator, year);

    mirr.calculateMirr(year, yearlyAfterTaxCashFlow, null);


    final Float yearlyDiscountRateDivisor = (float) Math.pow(1 + getMonthlyRequiredRateOfReturn(), monthCPModifier);
    yearlyNPVSummation += yearlyAfterTaxCashFlow / yearlyDiscountRateDivisor;

    dataController.setValueAsFloat(ValueEnum.ATCF_NPV, yearlyNPVSummation, year);
    return yearlyNPVSummation;
  }
  
  public void crunchCalculation() {

    /*note: many of the equations below are calculated using monthly variables.  This is done
     * when the reality of the equation is monthly.  For example, in the final summation of
     * NPV, the equation is (income - outlay) / (1 + discountRate/12)^numberOfMonthsAtPoint.
     * In other situations, such as taxes, they are only assessed yearly so their "reality" is yearly.
     * Sorry if that is a bad terminology, I am writing this at 6:30 in the morning!
     * Think about that a bit before making changes.
     */

    firstDay = mortgage.getDownPayment() + mortgage.getClosingCosts() + getFixupCosts();

    mirr.calculateMirr(0, -firstDay, null);

    for (int year = 1; year <= mortgage.getYearlyNumberOfCompoundingPeriods(); year++) {

      final Integer monthCPModifier = year * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
      final Integer prevYearMonthCPModifier = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;

      final Float atcfNPVSummation = calculateYearlyRentalIncomeNPV(year, monthCPModifier, prevYearMonthCPModifier);

      final Float adjustedAter = equityReversion.calculateAter(year);  

      npvAccumulator = -firstDay + atcfNPVSummation + adjustedAter;
      dataController.setValueAsFloat(ValueEnum.NPV, npvAccumulator, year);

      final Float accumulatedInterest = mortgage.getAccumulatedInterestPaymentsAtPoint(year);

      final Float accumulatedInterestPreviousYear = mortgage.getAccumulatedInterestPaymentsAtPoint(year - 1);
      final Float yearlyInterestPaid = accumulatedInterest - accumulatedInterestPreviousYear;
      dataController.setValueAsFloat(ValueEnum.YEARLY_INTEREST_PAID, yearlyInterestPaid, year);

    }
  }

public Float getMunicipalFees() {
	return municipalFees;
}

public void setMunicipalFees(Float municipalFees) {
	this.municipalFees = municipalFees;
}

}
