package com.byronkatz.reap.calculations;

import java.util.Vector;

public class ModifiedInternalRateOfReturn {

  private Float aterValueForMirr;
  private Vector<Float> cashFlowVector;

  public ModifiedInternalRateOfReturn() {
    aterValueForMirr = 0.0f;
    cashFlowVector = new Vector<Float>();
  }
  
  public Float calculateMirr(
      int year, Float reinvestmentRate, Float financeRate, Float cashFlow, Float ater) {

    Float mirr = 0.0f;
    int internalYear = 0;
    Float futureValuePositiveCashFlowsAccumulator = 0.0f;
    Float presentValueNegativeCashFlowsAccumulator = 0.0f;
    if (cashFlow != null) {
      cashFlowVector.add(cashFlow);
    }

    aterValueForMirr = 0.0f;
    if (ater != null) {
      aterValueForMirr = ater;
    }


      for (Float flow : cashFlowVector) {

        if (flow < 0.0f) {
          presentValueNegativeCashFlowsAccumulator += flow / (float) Math.pow(1 + financeRate, internalYear);
        } else if (flow > 0.0f) {
          futureValuePositiveCashFlowsAccumulator += flow * (float) Math.pow(1 + reinvestmentRate, year - internalYear);
        }

        internalYear++;
      }


    //special case for ater, since by definition it always happens in last year

      if (aterValueForMirr < 0.0f) {
        presentValueNegativeCashFlowsAccumulator += aterValueForMirr / (float) Math.pow(1 + financeRate, year);
      } else if (aterValueForMirr > 0.0f) {
        futureValuePositiveCashFlowsAccumulator += aterValueForMirr * (float) Math.pow(1 + reinvestmentRate, 0);
      }


    //we don't want divide by zero errors.
    if (!(presentValueNegativeCashFlowsAccumulator == 0f) || !(year == 0)) {
      mirr = (float) Math.pow(futureValuePositiveCashFlowsAccumulator / - presentValueNegativeCashFlowsAccumulator, (1.0f/year)) - 1;
    }

    return mirr;
  }
  
}
