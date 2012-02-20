package com.byronkatz.reap.calculations;

import java.util.Vector;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.ValueEnum;

public class ModifiedInternalRateOfReturn {

  private Vector<Double> cashFlowVector;
  private Double yearlyInterestRate;
  private Double yearlyRequiredRateOfReturn;
  private Double mirrValueAccumulator;

  private DataController dataController;

  public ModifiedInternalRateOfReturn(DataController dataController,
      Double yearlyInterestRate, 
      Double yearlyRequiredRateOfReturn) {
    this.dataController = dataController;
    mirrValueAccumulator = 0.0d;
    cashFlowVector = new Vector<Double>();
    this.yearlyInterestRate = yearlyInterestRate;
    this.yearlyRequiredRateOfReturn = yearlyRequiredRateOfReturn;
  }

  private void saveValue(int year) {
    dataController.setValueAsDouble(ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN, mirrValueAccumulator, year);
  }

  public void calculateMirr(int year, Double cashFlow, Double ater) {

    Double mirr = 0.0d;
    int internalYear = 0;
    Double futureValuePositiveCashFlowsAccumulator = 0.0d;
    Double presentValueNegativeCashFlowsAccumulator = 0.0d;
    if (cashFlow != null) {
      cashFlowVector.add(cashFlow);
    }

    for (Double flow : cashFlowVector) {

      if (flow < 0.0f) {
        presentValueNegativeCashFlowsAccumulator += 
            flow / Math.pow(1 + yearlyInterestRate, internalYear);
      } else if (flow > 0.0f) {
        futureValuePositiveCashFlowsAccumulator += 
            flow * Math.pow(1 + yearlyRequiredRateOfReturn, year - internalYear);
      }

      internalYear++;
    }


    //special case for ater, since by definition it always happens in last year
    if (ater != null) {
      if (ater < 0.0f) {
        presentValueNegativeCashFlowsAccumulator += 
            ater / Math.pow(1 + yearlyInterestRate, year);
      } else if (ater > 0.0f) {
        futureValuePositiveCashFlowsAccumulator += 
            ater *  Math.pow(1 + yearlyRequiredRateOfReturn, 0);
      }
    }



    //we don't want divide by zero errors.
    if (!(presentValueNegativeCashFlowsAccumulator == 0f) || !(year == 0)) {
      mirr = Math.pow(futureValuePositiveCashFlowsAccumulator / - presentValueNegativeCashFlowsAccumulator, (1.0f/year)) - 1;
    }

    mirrValueAccumulator += mirr;

    //save value if we are calculating ater
    if (ater != null) {
      saveValue(year);
      
      //reset the accumulator after a save
      mirrValueAccumulator = 0d;
    }
  }

}
