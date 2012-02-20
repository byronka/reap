package com.byronkatz.reap.calculations;

import java.util.Vector;

import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.ValueEnum;

public class ModifiedInternalRateOfReturn {

  private Vector<Float> cashFlowVector;
  private Double yearlyInterestRate;
  private Float yearlyRequiredRateOfReturn;
  private Float mirrValueAccumulator;

  private DataController dataController;

  public ModifiedInternalRateOfReturn(DataController dataController,
      Double yearlyInterestRate, 
      Float yearlyRequiredRateOfReturn) {
    this.dataController = dataController;
    mirrValueAccumulator = 0.0f;
    cashFlowVector = new Vector<Float>();
    this.yearlyInterestRate = yearlyInterestRate;
    this.yearlyRequiredRateOfReturn = yearlyRequiredRateOfReturn;
  }

  private void saveValue(int year) {
    dataController.setValueAsFloat(ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN, mirrValueAccumulator, year);
  }

  public void calculateMirr(int year, Float cashFlow, Float ater) {

    Float mirr = 0.0f;
    int internalYear = 0;
    Float futureValuePositiveCashFlowsAccumulator = 0.0f;
    Float presentValueNegativeCashFlowsAccumulator = 0.0f;
    if (cashFlow != null) {
      cashFlowVector.add(cashFlow);
    }

    for (Float flow : cashFlowVector) {

      if (flow < 0.0f) {
        presentValueNegativeCashFlowsAccumulator += 
            flow / (float) Math.pow(1 + yearlyInterestRate, internalYear);
      } else if (flow > 0.0f) {
        futureValuePositiveCashFlowsAccumulator += 
            flow * (float) Math.pow(1 + yearlyRequiredRateOfReturn, year - internalYear);
      }

      internalYear++;
    }


    //special case for ater, since by definition it always happens in last year
    if (ater != null) {
      if (ater < 0.0f) {
        presentValueNegativeCashFlowsAccumulator += 
            ater / (float) Math.pow(1 + yearlyInterestRate, year);
      } else if (ater > 0.0f) {
        futureValuePositiveCashFlowsAccumulator += 
            ater * (float) Math.pow(1 + yearlyRequiredRateOfReturn, 0);
      }
    }



    //we don't want divide by zero errors.
    if (!(presentValueNegativeCashFlowsAccumulator == 0f) || !(year == 0)) {
      mirr = (float) Math.pow(futureValuePositiveCashFlowsAccumulator / - presentValueNegativeCashFlowsAccumulator, (1.0f/year)) - 1;
    }

    mirrValueAccumulator += mirr;

    //save value if we are calculating ater
    if (ater != null) {
      saveValue(year);
      
      //reset the accumulator after a save
      mirrValueAccumulator = 0f;
    }
  }

}
