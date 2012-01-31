package com.byronkatz.reap.calculations;

import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class TotalPurchaseValue {

  private Float totalPurchaseValue;
  
  public TotalPurchaseValue() {
    totalPurchaseValue = RealEstateMarketAnalysisApplication.getInstance().
        getDataController().getValueAsFloat(ValueEnum.TOTAL_PURCHASE_VALUE);
  }

  public Float getTotalPurchaseValue() {
    return totalPurchaseValue;
  }

}
