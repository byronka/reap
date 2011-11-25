package com.byronkatz;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RealEstateMarketAnalysisActivity extends Activity {
  
  //The Data controller object - holds the data
  private DataController dataController;
  
  private HashMap<String, InputOutputField> inputFields;
  private ArrayList<DataItem> fieldValues;

  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.main);
    dataController = new DataController(this);
    createFields();
  }

  /**
   * This method gets run when the Analyze button is clicked.
   * @param view
   */
  public void analyzeNow(View view) {

    extractValuesAndSetInDataObjects();   
    calculateAndUpdateOutputStrings();
    View outputValues=(View)findViewById(R.id.outputValues);
    outputValues.setVisibility(TextView.VISIBLE);
  }

  private void createFields() {
    
    InputOutputField ioField;
    inputFields = new HashMap<String, InputOutputField>();
    fieldValues = dataController.getFieldValues();
    
    LinearLayout linearLayoutRegularInputsSection = 
        (LinearLayout)findViewById(R.id.linearLayoutRegularInputsSection);
    LinearLayout linearLayoutCheckBoxInputsSection = 
        (LinearLayout)findViewById(R.id.linearLayoutCheckBoxInputsSection);
    
    for (DataItem entry : fieldValues) {
      String key = entry.getId();
      String value = entry.getValue();
      int type = entry.getType();
      //build a collection of field objects
      if (type == DataItem.CHECK_BOX) {
        ioField = InputOutputField.getCheckBoxInputField(this, key, value);
        linearLayoutCheckBoxInputsSection.addView((LinearLayout)ioField.getOuterLinearLayout());
        inputFields.put(ioField.getKey(), ioField);
      } else if (type == DataItem.REGULAR) {
        ioField = InputOutputField.getRegularInputField(this, key, value);
        linearLayoutRegularInputsSection.addView((LinearLayout)ioField.getOuterLinearLayout());
        inputFields.put(ioField.getKey(), ioField);
      } else if (type == DataItem.NONE) {
        //TODO: do nothing?? unsure.
      }
    }
  }
  
  
  public void calculateAndUpdateOutputStrings() {
    double mPayment = calculateMortgagePayment();
    double totalIPayments = calculateTotalIPayments();
    double totalPayments = calculateTotalPayments();
    double npv = calculateNPV();
    formatAndInsertInOutputStrings(mPayment, totalIPayments, totalPayments, npv);
  }
  
  private double calculateMortgagePayment() {
    double mortgagePayment = 0;

    double purchaseValue = Double.valueOf(inputFields.get("total purchase value").getValue());
    double downPayment = Double.valueOf(inputFields.get("down payment").getValue());
    double principalOwed = purchaseValue - downPayment;
    double monthlyInterestRate = Double.valueOf(inputFields.get("monthly interest rate").getValue());
    int numOfCompoundingPeriods = Integer.valueOf(inputFields.get("number of compounding periods on loan").getValue());
    
    mortgagePayment = CalculatedVariables.getMortgagePayment(principalOwed, monthlyInterestRate, 
        numOfCompoundingPeriods);
    return mortgagePayment;
  }
  
  private double calculateTotalIPayments() {
    double totalIPayments = 0;

    double purchaseValue = Double.valueOf(inputFields.get("total purchase value").getValue());
    double downPayment = Double.valueOf(inputFields.get("down payment").getValue());
    double principalOwed = purchaseValue - downPayment;
    double monthlyInterestRate = Double.valueOf(inputFields.get("monthly interest rate").getValue());
    int numOfCompoundingPeriods = Integer.valueOf(inputFields.get("number of compounding periods on loan").getValue());
    int compoundingPeriodDesired = numOfCompoundingPeriods;
    
    totalIPayments = CalculatedVariables.getAccumulatedInterestPaymentsAtPoint(principalOwed,
        monthlyInterestRate, numOfCompoundingPeriods, compoundingPeriodDesired);
    return totalIPayments;
  }
  
  private double calculateTotalPayments() {
    double purchaseValue = Double.valueOf(inputFields.get("total purchase value").getValue());
    double downPayment = Double.valueOf(inputFields.get("down payment").getValue());
    double principalOwed = purchaseValue - downPayment;
    double monthlyInterestRate = Double.valueOf(inputFields.get("monthly interest rate").getValue());
    int numOfCompoundingPeriods = Integer.valueOf(inputFields.get("number of compounding periods on loan").getValue());
    int compoundingPeriodDesired = numOfCompoundingPeriods;
    double totalPayments = 0;

    
    totalPayments = CalculatedVariables.getTotalPaymentsMadeAtPoint(principalOwed, monthlyInterestRate, 
        numOfCompoundingPeriods, compoundingPeriodDesired);
    return totalPayments;
  }
  
  private double calculateNPV() {
    double npv = 0;
    double totalPurchaseValue = Double.valueOf(inputFields.get("total purchase value").getValue());
    double estimatedRentPayments = Double.valueOf(inputFields.get("estimated rent payments").getValue());
    double realEstateAppreciationRate = Double.valueOf(inputFields.get("real estate appreciation rate").getValue());
    double vacancyRate = Double.valueOf(inputFields.get("vacancy and credit loss rate").getValue());
    double initialYearlyGeneralExpenses = Double.valueOf(inputFields.get("initial yearly general expenses").getValue());
    double inflationRate = Double.valueOf(inputFields.get("inflation rate").getValue());
    double marginalTaxRate = Double.valueOf(inputFields.get("marginal tax rate").getValue());
    double buildingValue = Double.valueOf(inputFields.get("building value").getValue());
    double requiredRateOfReturn = Double.valueOf(inputFields.get("required rate of return").getValue());
    double yearlyInterestRate = Double.valueOf(inputFields.get("yearly interest rate").getValue());
    int numOfCompoundingPeriods = Integer.valueOf(inputFields.get("number of compounding periods on loan").getValue());
    int compoundingPeriodDesired = numOfCompoundingPeriods;
    double sellingBrokerRate = Double.valueOf(inputFields.get("selling broker rate").getValue());
    double generalSaleExpenses = Double.valueOf(inputFields.get("general sale expenses").getValue());
    double downPayment = Double.valueOf(inputFields.get("down payment").getValue());
    double fixupCosts = Double.valueOf(inputFields.get("fix-up costs").getValue());
    double propertyTaxRate = Double.valueOf(inputFields.get("property tax rate").getValue());
    double principalOwed = totalPurchaseValue - downPayment;
    double initialYearlyPropertyTax = totalPurchaseValue * propertyTaxRate;

    npv = CalculatedVariables.getNPV(estimatedRentPayments, realEstateAppreciationRate, 
        vacancyRate, initialYearlyGeneralExpenses, inflationRate, marginalTaxRate, principalOwed, 
        compoundingPeriodDesired, buildingValue, requiredRateOfReturn, yearlyInterestRate, 
        numOfCompoundingPeriods, sellingBrokerRate, generalSaleExpenses, downPayment, 
        totalPurchaseValue, fixupCosts, initialYearlyPropertyTax);
    return npv;
  }

  public void formatAndInsertInOutputStrings(double mPayment, double totalIPayments,
      double totalPayments, double npv) {
    //format and insert in output string
    String mortgagePaymentString = String.format("$%(,.2f",mPayment);
    ((TextView)findViewById(R.id.textViewMonthlyPaymentOutput)).setText(mortgagePaymentString);

    String totalMortgagePaymentString = String.format("$%(,.2f",totalPayments);
    ((TextView)findViewById(R.id.textViewTotalMortgagePaymentsPaidOutput)).setText(totalMortgagePaymentString);

    String totalInterestPaid = String.format("$%(,.2f", totalIPayments);
    ((TextView)findViewById(R.id.textViewTotalInterestPaidOutput)).setText(totalInterestPaid);    
    
    String netPresentValue = String.format("$%(,.2f", npv);
    ((TextView)findViewById(R.id.textViewNetPresentValueOutput)).setText(netPresentValue);   
  }


  /**
   * This is called when the user presses "analyze" to take their input
   */
  private void extractValuesAndSetInDataObjects() {
    for (InputOutputField entry : inputFields.values()) {
      entry.storeValuesFromInputs();
    }
  }
    
}