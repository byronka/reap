package com.byronkatz.reap.calculations;

import android.util.Log;

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
  private Integer monthsUntilRentStarts;
  private Integer yearsUntilRentStarts;           // year number where rent starts, starting with year 0
  private Integer monthsRemainderUntilRentStarts; //number of months from beginning of year until rent starts

  
  public Rental(DataController dataController, RentalUnitOwnership rentalUnitOwnership) {
    this.dataController = dataController;
    vacancyAndCreditLossRate = dataController.getValueAsDouble(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE);
    estimatedRentPayments = dataController.getValueAsDouble(ValueEnum.ESTIMATED_RENT_PAYMENTS);
    initialYearlyGeneralExpenses = dataController.getValueAsDouble(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES);
    initialHomeInsurance = dataController.getValueAsDouble(ValueEnum.INITIAL_HOME_INSURANCE);
    monthsUntilRentStarts = (int) Math.round(dataController.getValueAsDouble(ValueEnum.MONTHS_UNTIL_RENT_STARTS));
    yearsUntilRentStarts = monthsUntilRentStarts / GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    monthsRemainderUntilRentStarts = monthsUntilRentStarts % GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    
//    Log.d(getClass().getName(), "monthsUntilRentStarts: " + monthsUntilRentStarts);
//    Log.d(getClass().getName(), "yearsUntilRentStarts: " + yearsUntilRentStarts);
//    Log.d(getClass().getName(), "monthsRemainderUntilRentStarts: " + monthsRemainderUntilRentStarts);

    this.rentalUnitOwnership = rentalUnitOwnership;
  }

//  public Double getEstimatedRentPayments() {
//    return estimatedRentPayments;
//  }
  
//  public Double getInitialYearlyGeneralExpenses() {
//    return initialYearlyGeneralExpenses;  
//  }
  
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
  
//  public Double getNetYearlyIncome() {
//    return netYearlyIncome;
//  }
  
  /**
   * Future value effective gross income (income - vacancy losses)
   * @param year the year for which we are determining the future value of income
   * @return the future value net income
   */
  public Double getFVNetYearlyIncome(int year) {
//    Log.d(getClass().getName(), "entering getFVNetYearlyIncome(int year)");

    Integer monthsInYear = GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    
    Integer compoundingPeriodDesired = (year - 1) * monthsInYear;

    fVmonthlyRent = estimatedRentPayments * rentalUnitOwnership.getFVRear(compoundingPeriodDesired);
    dataController.setValueAsDouble(ValueEnum.MONTHLY_RENT_FV, fVmonthlyRent, year);
    
    //to check if rent is applied per Months Until Rent Charged
//    Log.d(getClass().getName(), "year: " + year);
//    Log.d(getClass().getName(), "if (yearsUntilRentStarts ("+yearsUntilRentStarts+") >= (year ("+ year +") - 1)): ");

    //first we check if even possible it gets applied this year
    if (yearsUntilRentStarts == (year - 1)) {
//      Log.d(getClass().getName(), "true");

      //if that is true, then we multiply the months where we don't 
      //get rent by our rent to find our gross yearly income
      grossYearlyIncome = estimatedRentPayments * (monthsInYear - monthsRemainderUntilRentStarts);
//      Log.d(getClass().getName(), "grossYearlyIncome("+grossYearlyIncome+") = estimatedRentPayments("+estimatedRentPayments+") " +
//      		"* (monthsInYear("+monthsInYear+") - monthsRemainderUntilRentStarts("+monthsRemainderUntilRentStarts+"));");
    } else if (yearsUntilRentStarts > (year - 1)) {
      grossYearlyIncome = 0d;
//      Log.d(getClass().getName(), "grossYearlyIncome = 0d");
    } else {
//      Log.d(getClass().getName(), "false");

//      Log.d(getClass().getName(), "grossYearlyIncome = estimatedRentPayments("+estimatedRentPayments+") * monthsInYear("+monthsInYear+")");
      grossYearlyIncome = estimatedRentPayments * monthsInYear;

    }
    
    netYearlyIncome = (1 - vacancyAndCreditLossRate) * grossYearlyIncome;
    
    Double fvNetYearlyIncome = 0.0d;
    fvNetYearlyIncome = netYearlyIncome * rentalUnitOwnership.getFVRear(compoundingPeriodDesired);
    dataController.setValueAsDouble(ValueEnum.YEARLY_INCOME, fvNetYearlyIncome, year);
//    Log.d(getClass().getName(), "leaving getFVNetYearlyIncome");

    return fvNetYearlyIncome;
  }

public Double getYearlyHomeInsurance() {
	return initialHomeInsurance;
}

public void setYearlyHomeInsurance(Double yearlyHomeInsurance) {
	this.initialHomeInsurance = yearlyHomeInsurance;
}

}
