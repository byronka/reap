package com.byronkatz;

import java.util.ArrayList;

public class GraphDataObject {

  private double maxNPV;
  private double minNPV;
  private double maxAter;
  private double minAter;
  private double maxAtcf;
  private double minAtcf;
  
  private ArrayList<Double> yearlyNPV;
  private ArrayList<Double> yearlyAter;
  private ArrayList<Double> yearlyAtcf;
  
  public GraphDataObject() {
    yearlyNPV = new ArrayList<Double>();
    yearlyAter = new ArrayList<Double>();
    yearlyAtcf = new ArrayList<Double>();
    maxNPV = Double.MIN_VALUE;
    minNPV = Double.MAX_VALUE;
    maxAter = Double.MIN_VALUE;
    minAter = Double.MAX_VALUE;
    maxAtcf = Double.MIN_VALUE;
    minAtcf = Double.MAX_VALUE;
  }
  
  public void addYearlyNPV(double npv) {
    yearlyNPV.add(npv);
    if (npv < minNPV) {
      minNPV = npv;
    }
    if (npv > maxNPV) {
      maxNPV = npv;
    }
  }
  
  public void addYearlyAter(double ater) {
    yearlyAter.add(ater);
    if (ater < minAter) {
      minAter = ater;
    }
    if (ater > maxAter) {
      maxAter = ater;
    }
  }
  
  public void addYearlyAtcf(double atcf) {
    yearlyAtcf.add(atcf);
    if (atcf < minAtcf) {
      minAtcf = atcf;
    }
    if (atcf > maxAtcf) {
      maxAtcf = atcf;
    }
  }
  
  public ArrayList<Double> getYearlyNPVWithAter() {
    return yearlyNPV;
  }

  public ArrayList<Double> getYearlyAter() {
    return yearlyAter;
  }
  
  public ArrayList<Double> getYearlyAtcf() {
    return yearlyAtcf;
  }

  public double getMaxNPV() {
    return maxNPV;
  }

  public double getMinNPV() {
    return minNPV;
  }

  public double getMaxAter() {
    return maxAter;
  }

  public double getMinAter() {
    return minAter;
  }

  public double getMaxAtcf() {
    return maxAtcf;
  }

  public double getMinAtcf() {
    return minAtcf;
  }


}
