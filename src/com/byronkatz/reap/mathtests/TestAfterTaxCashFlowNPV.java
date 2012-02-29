package com.byronkatz.reap.mathtests;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class TestAfterTaxCashFlowNPV implements ItemTestInterface {


  public TestAfterTaxCashFlowNPV() {
    
  }

  @Override
  public String getValue() {

    StringBuffer s = new StringBuffer();
    final DataController dataController = RealEstateMarketAnalysisApplication
        .getInstance().getDataController();
    Integer year = dataController.getCurrentYearSelected();

    final Double discountRate = dataController.getValueAsDouble(ValueEnum.REQUIRED_RATE_OF_RETURN);
    
    Double pValue = 0d;
    Double calculatedAtcfNPV = 0d;
    
    s.append("YEARLY AFTER TAX CASH FLOW NPV (ATCF-NPV)");
    for (int i = 1; i < year; i++) {
      pValue = dataController.getValueAsDouble(ValueEnum.ATCF, year) / Math.pow((1 + discountRate), i);
      s.append(String.format("\nyear: %d Present value: %.2f", year, pValue));
      calculatedAtcfNPV += pValue;

    }
    
    Double storedAtcfNPV = dataController.getValueAsDouble(ValueEnum.ATCF_NPV, year);
    
    if (Math.abs(calculatedAtcfNPV - storedAtcfNPV) < EPSILON) {
      s.append(CORRECT);
    } else {
      s.append(INCORRECT);
    }

    return s.toString();
  }


}
