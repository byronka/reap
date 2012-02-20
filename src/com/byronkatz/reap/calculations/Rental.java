package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.ValueEnum;

public class Rental {

  private Double estimatedRentPayments;
  private DataController dataController;
  private Double fVmonthlyRent;
  private Double grossYearlyIncome;
  private Double vacancyAndCreditLossRate;
  private Double netYearlyIncome;
  private RentalUnitOwnership rentalUnitOwnership;
  private Double initialYearlyGeneralExpenses;
  private Double initialHomeInsurance;

  
  public Rental(DataController dataController, RentalUnitOwnership rentalUnitOwnership) {
    this.dataController = dataController;
    vacancyAndCreditLossRate = dataController.getValueAsDouble(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE);
    estimatedRentPayments = dataController.getValueAsDouble(ValueEnum.ESTIMATED_RENT_PAYMENTS);
    initialYearlyGeneralExpenses = dataController.getValueAsDouble(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES);
    initialHomeInsurance = dataController.getValueAsDouble(ValueEnum.INITIAL_HOME_INSURANCE);

    this.rentalUnitOwnership = rentalUnitOwnership;
  }

  public Double getEstimatedRentPayments() {
    return estimatedRentPayments;
  }
  
  public Double getInitialYearlyGeneralExpenses() {
    return initialYearlyGeneralExpenses;  
  }
  
  public Double getFVYearlyGeneralExpenses(int year) {

    Integer compoundingPeriodDesired = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    Double fvYearlyGeneralExpenses = 0.0d;
    fvYearlyGeneralExpenses = initialYearlyGeneralExpenses * rentalUnitOwnership.getFVMir(compoundingPeriodDesired);
    dataController.setValueAsDouble(ValueEnum.YEARLY_GENERAL_EXPENSES, fvYearlyGeneralExpenses, year);
    return fvYearlyGeneralExpenses;
  }
  
  public Double getFVYearlyHomeInsurance(int year) {

	    Integer compoundingPeriodDesired = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
	    Double fvYearlyHomeInsurance = 0.0d;
	    fvYearlyHomeInsurance = initialHomeInsurance * rentalUnitOwnership.getFVMir(compoundingPeriodDesired);
	    dataController.setValueAsDouble(ValueEnum.YEARLY_HOME_INSURANCE, fvYearlyHomeInsurance, year);
	    
	    return fvYearlyHomeInsurance;
	  }
  
  public Double getNetYearlyIncome() {
    return netYearlyIncome;
  }
  
  /**
   * Future value effective gross income (income - vacancy losses)
   * @param year
   * @return
   */
  public Double getFVNetYearlyIncome(int year) {
    
    Integer compoundingPeriodDesired = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;

    fVmonthlyRent = estimatedRentPayments * rentalUnitOwnership.getFVRear(compoundingPeriodDesired);
    dataController.setValueAsDouble(ValueEnum.MONTHLY_RENT_FV, fVmonthlyRent, year);
    
    grossYearlyIncome = estimatedRentPayments * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    netYearlyIncome = (1 - vacancyAndCreditLossRate) * grossYearlyIncome;
    
    Double fvNetYearlyIncome = 0.0d;
    fvNetYearlyIncome = netYearlyIncome * rentalUnitOwnership.getFVRear(compoundingPeriodDesired);
    dataController.setValueAsDouble(ValueEnum.YEARLY_INCOME, fvNetYearlyIncome, year);
    return fvNetYearlyIncome;
  }

public Double getYearlyHomeInsurance() {
	return initialHomeInsurance;
}

public void setYearlyHomeInsurance(Double yearlyHomeInsurance) {
	this.initialHomeInsurance = yearlyHomeInsurance;
}

}
