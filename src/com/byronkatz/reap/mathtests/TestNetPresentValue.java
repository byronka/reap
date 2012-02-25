package com.byronkatz.reap.mathtests;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class TestNetPresentValue implements ItemTestInterface {

  Double totalInitialCosts;
  Double atcfNPV;
  Double aterPV;
  
  final DataController dataController = RealEstateMarketAnalysisApplication
      .getInstance().getDataController();
  
  public TestNetPresentValue() {
    
  }

  @Override
  public String getValue() {
    StringBuffer s = new StringBuffer();
    Integer year = dataController.getCurrentYearSelected();
    


    s = getInitialNegativeCashflow(s);

    s = getAtcfNpv(s, year);

    s = getAter(s, year);
    
    s.append("\n\nNET PRESENT VALUE");
    Double NPV = -totalInitialCosts + atcfNPV + aterPV;
    s.append(String.format("\nTotal NPV: %.2f", NPV));

    Double storedNPV = dataController.getValueAsDouble(ValueEnum.NPV, year);
    if (storedNPV.equals(NPV)) {
      s.append(CORRECT);
    } else {
      s.append(INCORRECT);
      s.append(String.format("\nNPV calculated here, %.2f", NPV));
      s.append(String.format(" does not equal stored NPV: %.2f", storedNPV));

    }
    
    return s.toString();
  }
  
  private StringBuffer getInitialNegativeCashflow(StringBuffer s) {
    s.append("INITIAL CASH FLOW");

    Double downPayment  = dataController.getValueAsDouble(ValueEnum.DOWN_PAYMENT);
    s.append(String.format("\nDown payment: %.2f", downPayment));
    
    Double fixupCosts   = dataController.getValueAsDouble(ValueEnum.FIX_UP_COSTS);
    s.append(String.format("\nRenovation cost: %.2f", fixupCosts));

    Double closingCosts = dataController.getValueAsDouble(ValueEnum.CLOSING_COSTS);
    s.append(String.format("\nClosing cost: %.2f", closingCosts));
    
    totalInitialCosts = downPayment + fixupCosts + closingCosts;
    s.append(String.format("\nTotal: %.2f", totalInitialCosts));

    return s;
  }
  
  private StringBuffer getAtcfNpv(StringBuffer s, int year) {
    s.append("\n\nAFTER TAX CASH FLOW NET PRESENT VALUE");

    atcfNPV = dataController.getValueAsDouble(ValueEnum.ATCF_NPV, year);
    
    s.append(String.format("\natcf NPV: %.2f", atcfNPV));
    return s;
  }
  
  private StringBuffer getAter(StringBuffer s, int year) {
    s.append("\n\nAFTER TAX EQUITY REVERSION PRESENT VALUE");

    aterPV = dataController.getValueAsDouble(ValueEnum.ATER_PV, year);
    s.append(String.format("\nater PV: %.2f", aterPV));
    
    return s;
  }
  
  
  //JUNK.
//private StringBuffer getYearlyAtcfs(StringBuffer s, int year) {
//s.append("\nYEARLY ATCF'S");
//Double rrr = dataController.getValueAsDouble(ValueEnum.REQUIRED_RATE_OF_RETURN);
//Double monthlyRRR = rrr / 12;
//Double atcfNPV = 0d;
//Double atcf = 0d;
//Double atcfPV = 0d;
////Double[] atcfValues = new Double[year + 1];
////Double[] atcfPVValues = new Double[year + 1];
////Double[] atcfNPVValues = new Double[year + 1];
//
//for(int i = 0; i <= (year * 12); i++ ) {
////  atcfValues[i] = dataController.getValueAsDouble(ValueEnum.ATCF, i);
////  atcfPVValues[i] = atcfValues[i] / Math.pow((1 + rrr), i);
////  atcfNPV += atcfPVValues[i];
////  atcfNPVValues[i] = atcfNPV;
//  
//atcf = dataController.getValueAsDouble(ValueEnum.ATCF, i);
//atcfPV = atcf / Math.pow((1 + rrr), i);
//atcfNPV += atcfPV;
//
//}
//
////s.append("\n\natcf: ");
////for (int i = 0; i <= year; i++) {s.append(String.format("%d: %.0f ", i, atcfValues[i]));}
////
////s.append("\n\natcfPV: ");
////for (int i = 0; i <= year; i++) {s.append(String.format("%d: %.0f ", i, atcfPVValues[i]));}
////
////s.append("\n\natcfNPV: ");
////for (int i = 0; i <= year; i++) {s.append(String.format("%d: %.0f ", i, atcfNPVValues[i]));}
//
//s.append(String.format("\ncalculated atcfNPV: %.2f",atcfNPV));
//
//Double storedAtcfNPV = dataController.getValueAsDouble(ValueEnum.ATCF_NPV, year);
//s.append(String.format("\nstored atcfNPV: %.2f",storedAtcfNPV));
//if (atcfNPV.equals(storedAtcfNPV)) {
//  s.append(CORRECT);
//} else {
//  s.append(INCORRECT);
//}
//  
//
//
//return s;
//}
  
}
