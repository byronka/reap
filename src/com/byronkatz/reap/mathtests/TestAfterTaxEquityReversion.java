package com.byronkatz.reap.mathtests;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class TestAfterTaxEquityReversion implements ItemTestInterface {

  @Override
  public String getValue() {

    StringBuffer s = new StringBuffer();
    final DataController dataController = RealEstateMarketAnalysisApplication
        .getInstance().getDataController();
    Integer year = dataController.getCurrentYearSelected();
    
    final Double storedAter = dataController.getValueAsDouble(ValueEnum.ATER, year);
    final Double projectedValue = dataController.getValueAsDouble(ValueEnum.PROJECTED_HOME_VALUE, year);
    final Double brokerCut = dataController.getValueAsDouble(ValueEnum.BROKER_CUT_OF_SALE, year);
    final Double taxAtSale =  dataController.getValueAsDouble(ValueEnum.TAXES_DUE_AT_SALE, year);
    final Double principalOutstanding = dataController.getValueAsDouble(ValueEnum.CURRENT_AMOUNT_OUTSTANDING, year);
    final Double futureValueSellingExpenses = dataController.getValueAsDouble(ValueEnum.SELLING_EXPENSES, year);
    final Double calculatedAter = projectedValue - brokerCut - taxAtSale - principalOutstanding - futureValueSellingExpenses;
    s.append("AFTER TAX EQUITY REVERSION (ATER)");
    
    s.append(String.format("\nProjected value of investment: %.2f ", projectedValue));
    s.append(String.format("\nBroker cut: - %.2f", brokerCut));
    s.append(String.format("\nTax at sale: - %.2f", taxAtSale));
    s.append(String.format("\nPrincipal outstanding: - %.2f", principalOutstanding));
    s.append(String.format("\nFuture value selling expenses: - %.2f", futureValueSellingExpenses));
    
    s.append("\n-----------------------------");
    s.append(String.format("\nstored ATER: %.2f", storedAter));
    s.append(String.format("\ncalculated ATER: %.2f", calculatedAter));
        
    if (Math.abs(storedAter - calculatedAter) < EPSILON) {
      s.append(CORRECT);
    } else {
      s.append(INCORRECT);
    }

    return s.toString();
  }

}
