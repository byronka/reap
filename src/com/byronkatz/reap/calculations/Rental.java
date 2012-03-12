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
  Double fvNetYearlyIncome;
  Double fvGrossYearlyIncome;
  Integer monthsInYear;
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
    
    monthsInYear = GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    fvNetYearlyIncome = 0.0d;
    fvGrossYearlyIncome = 0.0d;
    
    Log.d(getClass().getName(), "monthsUntilRentStarts: " + monthsUntilRentStarts);
    Log.d(getClass().getName(), "yearsUntilRentStarts: " + yearsUntilRentStarts);
    Log.d(getClass().getName(), "monthsRemainderUntilRentStarts: " + monthsRemainderUntilRentStarts);

    this.rentalUnitOwnership = rentalUnitOwnership;
  }
  
  public Double getFVYearlyGeneralExpenses(int year) {

    Double fvYearlyGeneralExpenses = 0.0d;
    fvYearlyGeneralExpenses = initialYearlyGeneralExpenses * rentalUnitOwnership.getFVIr(year - 1);
    dataController.setValueAsDouble(ValueEnum.YEARLY_GENERAL_EXPENSES, fvYearlyGeneralExpenses, year);
    return fvYearlyGeneralExpenses;
  }
  
  public Double getFVYearlyHomeInsurance(int year) {

	    Double fvYearlyHomeInsurance = 0.0d;
	    fvYearlyHomeInsurance = initialHomeInsurance * rentalUnitOwnership.getFVIr(year - 1);
	    dataController.setValueAsDouble(ValueEnum.YEARLY_HOME_INSURANCE, fvYearlyHomeInsurance, year);
	    
	    return fvYearlyHomeInsurance;
	  }

  
  /**
   * Future value effective gross income (income - vacancy losses)
   * @param year the year for which we are determining the future value of income
   * @return the future value net income
   */
  public Double getFVNetYearlyIncome(int year) {
    
    fVmonthlyRent = estimatedRentPayments * rentalUnitOwnership.getFVRear(year - 1);
    dataController.setValueAsDouble(ValueEnum.MONTHLY_RENT_FV, fVmonthlyRent, year);
    
    //to check if rent is applied per Months Until Rent Charged
    //first we check if even possible it gets applied this year
    if (yearsUntilRentStarts == (year - 1)) {

      //if that is true, then we multiply the months where we don't 
      //get rent by our rent to find our gross yearly income
      grossYearlyIncome = estimatedRentPayments * (monthsInYear - monthsRemainderUntilRentStarts);
    
      //check if, in the current year, none of the months get any rent income
    } else if (yearsUntilRentStarts > (year - 1)) {
      grossYearlyIncome = 0d;
      
      //check if, in the current year, all the months get rent income
    } else if (yearsUntilRentStarts < (year - 1)) {
      grossYearlyIncome = estimatedRentPayments * monthsInYear;
    }

      fvGrossYearlyIncome = grossYearlyIncome * rentalUnitOwnership.getFVRear(year - 1);
      dataController.setValueAsDouble(ValueEnum.GROSS_YEARLY_INCOME, fvGrossYearlyIncome, year);

    
    fvNetYearlyIncome = (1 - vacancyAndCreditLossRate) * fvGrossYearlyIncome;
    
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
