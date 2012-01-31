package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class DownPayment {

  private Float downPayment;
  
  public DownPayment() {
    downPayment = (RealEstateMarketAnalysisApplication.getInstance().
        getDataController().getValueAsFloat(ValueEnum.DOWN_PAYMENT));
  }

  public Float getDownPayment() {
    return downPayment;
  }

}
