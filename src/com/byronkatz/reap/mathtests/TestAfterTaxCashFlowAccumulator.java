package com.byronkatz.reap.mathtests;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class TestAfterTaxCashFlowAccumulator implements ItemTestInterface {


  @Override
  public String getValue() {

    StringBuffer s = new StringBuffer();
    final DataController dataController = RealEstateMarketAnalysisApplication
        .getInstance().getDataController();
    Integer year = dataController.getCurrentYearSelected();
    
    s.append("AFTER TAX CASH FLOW ACCUMULATOR (ATCFA)");

    Double storedATCFAccum = dataController.getValueAsDouble(ValueEnum.ATCF_ACCUMULATOR, year);
    s.append(String.format("\n\nstored ATCFA: %.2f", storedATCFAccum));
    
    Double calculatedATCFAccum = 0d;
    for(int i = 0; i <= year; i++) {
      calculatedATCFAccum += dataController.getValueAsDouble(ValueEnum.ATCF, i);
    }
    s.append(String.format("\ntest calculated ATCFA: %.2f", calculatedATCFAccum));

    
    if (Math.abs(storedATCFAccum - calculatedATCFAccum) < EPSILON) {
      s.append(CORRECT);
    } else {
      s.append(INCORRECT);
    }

    return s.toString();
  }

}
