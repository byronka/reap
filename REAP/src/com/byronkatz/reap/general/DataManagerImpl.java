package com.byronkatz.reap.general;

import java.util.EnumMap;
import java.util.Map;

import com.byronkatz.reap.calculations.CalcValueGettable;
import com.byronkatz.reap.general.DataManager;
import com.byronkatz.reap.general.ValueEnum;

public class DataManagerImpl implements DataManager {

  private Map<ValueEnum, CalcValueGettable> calcValuePointers;
  private Map<ValueEnum, Double> inputAmounts;
  
  public DataManagerImpl() {
    calcValuePointers = new EnumMap<ValueEnum, CalcValueGettable>(ValueEnum.class);
    inputAmounts = new EnumMap<ValueEnum, Double>(ValueEnum.class);
  }
  
  public void addCalcValuePointers(ValueEnum valueEnum, CalcValueGettable valueStorage) {
    calcValuePointers.put(valueEnum, valueStorage);
  }

  public double getCalcValue(ValueEnum valueEnum, int compoundingPeriod) {
    return calcValuePointers.get(valueEnum).getValue(compoundingPeriod);
  }

  public void putInputValue(double value, ValueEnum valueEnum) {
    inputAmounts.put(valueEnum, value);
    
  }

  public double getInputValue(ValueEnum valueEnum) {
    if (inputAmounts.get(valueEnum) == null) {
      return 0d;
    } else {
      return inputAmounts.get(valueEnum);
    }
  }

}
