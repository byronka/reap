package com.byronkatz.test;

import com.byronkatz.reap.general.CalculatedVariables;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class CalculatedVariablesTest {

  public CalculatedVariablesTest() {
    super();
  }
  
  DataController dc;
  
  @Before
  public void setUp() throws Exception {
    dc = new DataController();
  }

  @Test
  public void testGetMortgagePayment() {
    //typical values
    double principalOwed = 350000.00;
    double monthlyInterestRate = 0.05/12;
    int numOfCompoundingPeriods = 360;
    double mPayment = CalculatedVariables.getMortgagePayment(principalOwed, monthlyInterestRate,
        numOfCompoundingPeriods);
    assertEquals("Mortgage Payment equation is wrong", 1878.88, mPayment, 0.01);

    //negative values
    principalOwed = -100000.00;
    monthlyInterestRate = 0.05/12;
    numOfCompoundingPeriods = 360;
    
    mPayment = CalculatedVariables.getMortgagePayment(principalOwed, monthlyInterestRate, numOfCompoundingPeriods);
    assertEquals("Mortgage Payment equation is wrong", -536.82, mPayment, 0.01);
    
    //interest rate = 0
    principalOwed = 350000.00;
    monthlyInterestRate = 0.00/12;
    numOfCompoundingPeriods = 360;

    mPayment = CalculatedVariables.getMortgagePayment(principalOwed, monthlyInterestRate, numOfCompoundingPeriods);
    assertEquals("Mortgage Payment equation is wrong", Double.NaN, mPayment, 0.01);
  }
  
  @Test
  public void testGetAccumulatedInterestPaymentsAtPoint() {
    double principalOwed = 350000.00;
    double monthlyInterestRate = 0.05/12;
    int numOfCompoundingPeriods = 360;
    int compoundingPeriodDesired = 360;
    //test at final payment
    double accumInterest = CalculatedVariables.getAccumulatedInterestPaymentsAtPoint(principalOwed, monthlyInterestRate, numOfCompoundingPeriods, compoundingPeriodDesired);
    assertEquals("Accumulated interest equation is wrong", 326395.24, accumInterest, 0.01);
    //test at first payment
    compoundingPeriodDesired = 0;
    accumInterest = CalculatedVariables.getAccumulatedInterestPaymentsAtPoint(principalOwed, monthlyInterestRate, numOfCompoundingPeriods, compoundingPeriodDesired);
    assertEquals("Accumulated interest equation is wrong",1458.33, accumInterest, 0.01);
    //test at one past final payment
    compoundingPeriodDesired = 361;
    accumInterest = CalculatedVariables.getAccumulatedInterestPaymentsAtPoint(principalOwed, monthlyInterestRate, numOfCompoundingPeriods, compoundingPeriodDesired);
    assertEquals("Accumulated interest equation is wrong",326395.24, accumInterest, 0.01);
    //test at one before first payment
    compoundingPeriodDesired = -1;
    accumInterest = CalculatedVariables.getAccumulatedInterestPaymentsAtPoint(principalOwed, monthlyInterestRate, numOfCompoundingPeriods, compoundingPeriodDesired);
    assertEquals("Accumulated interest equation is wrong",1458.33, accumInterest, 0.01);
    //test in the middel
    compoundingPeriodDesired = 150;
    accumInterest = CalculatedVariables.getAccumulatedInterestPaymentsAtPoint(principalOwed, monthlyInterestRate, numOfCompoundingPeriods, compoundingPeriodDesired);
    assertEquals("Accumulated interest equation is wrong", 195538.05, accumInterest, 0.01);
  }

}

