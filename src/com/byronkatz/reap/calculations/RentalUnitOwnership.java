//package com.byronkatz.reap.calculations;
//
//import com.byronkatz.reap.general.DataManager;
//
//public class RentalUnitOwnership {
//
//  private ValueSettable amortizationTable;
//  private ValueSettable cashFlow;
//  private ValueSettable equityReversion;
//  private ValueSettable income;
//  private ValueSettable investmentFValue;
//  private ValueSettable operatingExpenses;
//  private ValueSettable tax;
//  
//  /**
//   * This constructor creates all the objects that it will use.  Thenceforth,
//   * use crunchValues to take values from the input object and crunch them out.
//   */
//  public RentalUnitOwnership() {
//    
//    amortizationTable = new AmortizationTable();
//    cashFlow = new CashFlow();
//    equityReversion = new EquityReversion();
//    income = new Income();
//    investmentFValue = new InvestmentFValue();
//    operatingExpenses = new OperatingExpenses();
//    tax = new Tax();
//    
//  }
//  
//  
//  /**
//   * inserts the datamanager callback info to the calculation objects.  In turn,
//   * they register themselves into the DataManager map.
//   * @param dataManager the input / output manager object for these objects
//   */
//  public void insertUserValues(DataManager dataManager) {
//   
//    amortizationTable.setValues(dataManager);
//    cashFlow.setValues(dataManager);
//    equityReversion.setValues(dataManager);
//    income.setValues(dataManager);
//    investmentFValue.setValues(dataManager);
//    operatingExpenses.setValues(dataManager);
//    amortizationTable.setValues(dataManager);
//    tax.setValues(dataManager);
//  }
//}
