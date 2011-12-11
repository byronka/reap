package com.byronkatz;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphDataObject {

  private double maxNPV;
  private double minNPV;
  private double maxAter;
  private double minAter;
  private double maxAtcf;
  private double minAtcf;
  private double minNPVYearNumber;
  private double maxNPVYearNumber;
  private double minAterYearNumber;
  private double maxAterYearNumber;
  private double minAtcfYearNumber;
  private double maxAtcfYearNumber;
  
  private HashMap<Float, Float> yearlyNPV;
  private HashMap<Float, Float> yearlyAter;
  private HashMap<Float, Float> yearlyAtcf;

  public GraphDataObject() {
    yearlyNPV  = new HashMap<Float, Float>();
    yearlyAter = new HashMap<Float, Float>();
    yearlyAtcf = new HashMap<Float, Float>();
    maxNPV     = Double.MIN_VALUE;
    minNPV     = Double.MAX_VALUE;
    maxAter    = Double.MIN_VALUE;
    minAter    = Double.MAX_VALUE;
    maxAtcf    = Double.MIN_VALUE;
    minAtcf    = Double.MAX_VALUE;
    maxNPVYearNumber    = Double.MIN_VALUE;
    minNPVYearNumber    = Double.MAX_VALUE;
    maxAtcfYearNumber    = Double.MIN_VALUE;
    minAtcfYearNumber    = Double.MAX_VALUE;
    maxAterYearNumber    = Double.MIN_VALUE;
    minAterYearNumber    = Double.MAX_VALUE;
  }

  public void addYearlyNPV(int year, double npv) {
    Float yearFloat = Float.valueOf(year);
    Float npvFloat = (float) npv;
    yearlyNPV.put(yearFloat, npvFloat);
    if (npv < minNPV) {
      minNPV = npv;
    }
    if (npv > maxNPV) {
      maxNPV = npv;
    }
    
    if (year < minNPVYearNumber) {
      minNPVYearNumber = year;
    }
    if (year > maxNPVYearNumber) {
      maxNPVYearNumber = year;
    }
  }

  public void addYearlyAter(int year, double ater) {
    Float yearFloat = Float.valueOf(year);
    Float aterFloat = (float) ater;
    yearlyNPV.put(yearFloat, aterFloat);
    if (ater < minAter) {
      minAter = ater;
    }
    if (ater > maxAter) {
      maxAter = ater;
    }
    
    if (year < minAterYearNumber) {
      minAterYearNumber = year;
    }
    if (year > maxAterYearNumber) {
      maxAterYearNumber = year;
    }
  }

  public void addYearlyAtcf(int year, double atcf) {
    Float yearFloat = Float.valueOf(year);
    Float atcfFloat = (float) atcf;
    yearlyNPV.put(yearFloat, atcfFloat);
    if (atcf < minAtcf) {
      minAtcf = atcf;
    }
    if (atcf > maxAtcf) {
      maxAtcf = atcf;
    }
    
    if (year < minAtcfYearNumber) {
      minAtcfYearNumber = year;
    }
    if (year > maxAtcfYearNumber) {
      maxAtcfYearNumber = year;
    }
  }

  public HashMap<Float, Float> getYearlyNPVWithAter() {
    return yearlyNPV;
  }

  public HashMap<Float, Float> getYearlyAter() {
    return yearlyAter;
  }

  public HashMap<Float, Float> getYearlyAtcf() {
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

  public double getMinNPVYearNumber() {
    return minNPVYearNumber;
  }

  public double getMaxNPVYearNumber() {
    return maxNPVYearNumber;
  }

  public double getMinAterYearNumber() {
    return minAterYearNumber;
  }

  public double getMaxAterYearNumber() {
    return maxAterYearNumber;
  }

  public double getMinAtcfYearNumber() {
    return minAtcfYearNumber;
  }

  public double getMaxAtcfYearNumber() {
    return maxAtcfYearNumber;
  }



}
