package com.byronkatz;

import java.util.ArrayList;

public class NPVGraphDataObject {

  ArrayList<Double> yearlyNPV;
  ArrayList<Double> yearlyAter;
  ArrayList<Double> yearlyAtcf;
  
  public NPVGraphDataObject() {
    yearlyNPV = new ArrayList<Double>();
    yearlyAter = new ArrayList<Double>();
    yearlyAtcf = new ArrayList<Double>();
  }
  
  public void addYearlyNPV(double npv) {
    yearlyNPV.add(npv);
  }
  
  public void addYearlyAter(double ater) {
    yearlyAter.add(ater);
  }
  
  public void addYearlyAtcf(double atcf) {
    yearlyAtcf.add(atcf);
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


}
