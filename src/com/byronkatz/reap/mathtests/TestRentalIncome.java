package com.byronkatz.reap.mathtests;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class TestRentalIncome implements ItemTestInterface {

  public TestRentalIncome() {
    
  }
  
  private String getRentalIncome() {
    StringBuffer s = new StringBuffer();
    final DataController dataController = RealEstateMarketAnalysisApplication
        .getInstance().getDataController();
    Integer year = dataController.getCurrentYearSelected();
    
    Double fvMonthlyRent = dataController.getValueAsDouble(ValueEnum.MONTHLY_RENT_FV, year);
    Double rentalIncome = dataController.getValueAsDouble(ValueEnum.YEARLY_INCOME, year);
    Double vacancyRate = dataController.getValueAsDouble(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE);

    s.append("RENTAL INCOME");
    s.append(String.format("\nMonthly Rent (MR): %.2f", fvMonthlyRent));
    s.append(String.format("\nVacancy rate (VR): %.4f", vacancyRate));
    s.append(String.format("\nRental Income (RI): %.2f", rentalIncome));
    s.append("\nCheck:\n  MR * (1-VR) * 12 months = RI");
    s.append(String.format("\nCheck:\n  %.2f * (1 - %.4f) * 12 = %.2f", fvMonthlyRent, vacancyRate, rentalIncome));
    Double actualRI = fvMonthlyRent * (1 - vacancyRate) * 12;

    if (Math.abs(actualRI - rentalIncome) < EPSILON) {
      s.append(CORRECT);
    } else {
      s.append(INCORRECT);
      s.append("\nActual answer is:");
      s.append(String.format("\n  %.2f * (1 - %.2f) * 12 = %.2f\n\n", fvMonthlyRent, vacancyRate, actualRI));
    }

    return s.toString();
  }

  @Override
  public String getValue() {
    return getRentalIncome();
  }

}
