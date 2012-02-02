package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class Rental {

  private Float estimatedRentPayments;
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  private Float grossYearlyIncome;
  private Float vacancyAndCreditLossRate;
  private Float netYearlyIncome;
  private RentalUnitOwnership rentalUnitOwnership;
  private Float initialYearlyGeneralExpenses;

  
  public Rental(RentalUnitOwnership rentalUnitOwnership) {
    vacancyAndCreditLossRate = dataController.getValueAsFloat(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE);
    estimatedRentPayments = dataController.getValueAsFloat(ValueEnum.ESTIMATED_RENT_PAYMENTS);
    initialYearlyGeneralExpenses = dataController.getValueAsFloat(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES);

    grossYearlyIncome = estimatedRentPayments * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    netYearlyIncome = (1 - vacancyAndCreditLossRate) * grossYearlyIncome;
    this.rentalUnitOwnership = rentalUnitOwnership;
  }

  public Float getEstimatedRentPayments() {
    return estimatedRentPayments;
  }
  
  public Float getInitialYearlyGeneralExpenses() {
    return initialYearlyGeneralExpenses;  
  }
  
  public Float getFVYearlyGeneralExpenses(int year) {

    Integer compoundingPeriodDesired = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    Float fvYearlyGeneralExpenses = 0.0f;
    fvYearlyGeneralExpenses = initialYearlyGeneralExpenses * rentalUnitOwnership.getFVMir(compoundingPeriodDesired);
    dataController.setValueAsFloat(ValueEnum.YEARLY_GENERAL_EXPENSES, fvYearlyGeneralExpenses, year);
    return fvYearlyGeneralExpenses;
  }
  
  public Float getNetYearlyIncome() {
    return netYearlyIncome;
  }
  
  public Float getFVNetYearlyIncome(int year) {
    
    Integer compoundingPeriodDesired = (year - 1) * GeneralCalculations.NUM_OF_MONTHS_IN_YEAR;
    Float fvNetYearlyIncome = 0.0f;
    fvNetYearlyIncome = netYearlyIncome * rentalUnitOwnership.getFVRear(compoundingPeriodDesired);
    dataController.setValueAsFloat(ValueEnum.YEARLY_INCOME, fvNetYearlyIncome, year);
    return fvNetYearlyIncome;
  }

}
