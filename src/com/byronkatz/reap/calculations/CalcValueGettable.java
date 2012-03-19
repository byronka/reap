package com.byronkatz.reap.calculations;

/**
 * This class manages the creation and storage of the mortgage payment
 * @author byron
 *
 */
public interface CalcValueGettable {

  Integer MONTHS_IN_YEAR = 12;
  public Double getValue(Integer compoundingPeriod);
  
}