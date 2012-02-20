package com.byronkatz.reap.mathtests;

import java.util.HashMap;
import java.util.Map;

import com.byronkatz.reap.general.ValueEnum;

public class MathCheckImpl implements MathCheckInterface {

  private Map<ValueEnum, Class<? extends ItemTestInterface>> mathTests;
  
  public MathCheckImpl() {
    
    mathTests = new HashMap<ValueEnum, Class<? extends ItemTestInterface>>();
    
    mathTests.put( ValueEnum.YEARLY_INCOME, TestRentalIncome.class);
    mathTests.put(ValueEnum.ATCF, TestAfterTaxCashFlow.class);
    mathTests.put( ValueEnum.YEARLY_OUTLAY, TestYearlyOutlay.class);
    mathTests.put(ValueEnum.YEARLY_INTEREST_PAID, TestYearlyInterestPaid.class);
    mathTests.put(ValueEnum.ACCUM_INTEREST, TestYearlyInterestPaidAccumulator.class);
  }
  
  @Override
  public Class<? extends ItemTestInterface> getTestResults(ValueEnum ve) {

    return mathTests.get(ve);
  }

  
  

}
