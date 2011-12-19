package com.byronkatz;

import java.util.HashMap;

public class GraphDataObject {

  private double maxFunctionValue;
  private double minFunctionValue;
  
  private int maxYearValue;
  private int minYearValue;
  
  private HashMap<Float, Float> yearlyFunctionValue;

  private String graphName;




  public GraphDataObject(String graphName) {
    yearlyFunctionValue = new HashMap<Float, Float>();

    maxFunctionValue = Double.NEGATIVE_INFINITY;
    minFunctionValue = Double.POSITIVE_INFINITY;
    
    maxYearValue        = Integer.MIN_VALUE;
    minYearValue        = Integer.MAX_VALUE;
   
    this.graphName = graphName;
  }

  public String getGraphName() {
    return graphName;
  }


  public void setGraphName(String graphName) {
    this.graphName = graphName;
  }
  
  public void addYearlyFunctionValue (int year, double value) {
    Float yearFloat = Float.valueOf(year);
    Float valueFloat = (float) value;
    yearlyFunctionValue.put(yearFloat, valueFloat);
    if (value < minFunctionValue) {
      minFunctionValue = value;
    }
    if (value > maxFunctionValue) {
      maxFunctionValue = value;
    }
    
    if (year < minYearValue) {
      minYearValue = year;
    }
    if (year > maxYearValue) {
      maxYearValue = year;
    }
  }
  

  public HashMap<Float, Float> getYearlyFunctionValue() {
    return yearlyFunctionValue;
  }
  
  
  public double getMaxFunctionValue() {
    return maxFunctionValue;
  }
  
  public double getMinFunctionValue() {
    return minFunctionValue;
  }
  
  public Integer getMinYearValue() {
    return minYearValue;
  }
  
  public Integer getMaxYearValue() {
    return maxYearValue;
  }



}
