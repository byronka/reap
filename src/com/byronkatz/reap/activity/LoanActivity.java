package com.byronkatz.reap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.byronkatz.R;
import com.byronkatz.reap.calculations.Mortgage;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.OnFocusChangeListenerWrapper;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

public class LoanActivity extends Activity {


  private EditText yearlyInterestRate;
  private EditText privateMortgageInsurance;
  private EditText extraYears;
  private EditText downPayment;
  private Spinner loanTerm;
  private EditText totalPurchasePrice;
  private EditText closingCosts;
  private Button pmiButton;
  ArrayAdapter<CharSequence> adapter;


  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.loan);

    //Hook up the components from the GUI to some variables here
    yearlyInterestRate = (EditText)findViewById(R.id.yearlyInterestRateEditText);
    downPayment        = (EditText)findViewById(R.id.downPaymentEditText);
    loanTerm           = (Spinner) findViewById(R.id.numOfCompoundingPeriodsSpinner);
    totalPurchasePrice = (EditText)findViewById(R.id.totalPurchasePriceEditText);
    privateMortgageInsurance = (EditText) findViewById(R.id.privateMortgageInsuranceEditText);
    extraYears         = (EditText)findViewById(R.id.extraYearsEditText);
    closingCosts       = (EditText)findViewById(R.id.closingCostsEditText);
    pmiButton          = (Button)findViewById(R.id.calcPMIDownPaymentButton);


    adapter = ArrayAdapter.createFromResource(
        this, R.array.numOfCompoundingPeriodsArray, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    loanTerm.setAdapter(adapter);

    assignValuesToFields();

    pmiButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Float totalPurchasevalue = 
            Utility.parseCurrency(totalPurchasePrice.getText().toString());
        Float pmiDownPayment = totalPurchasevalue * Mortgage.PMI_PERCENTAGE;
        String pmiDownPaymentText = Utility.displayCurrency(pmiDownPayment);
        downPayment.setText(pmiDownPaymentText);

      }
    });

    yearlyInterestRate.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.YEARLY_INTEREST_RATE));

    TextView yearlyInterestRateTitle = 
        (TextView)findViewById(R.id.yearlyInterestRateTitle);
    yearlyInterestRateTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.yearlyInterestRateDescriptionText, 
            R.string.yearlyInterestRateTitleText,
            LoanActivity.this);
      }
    });

    downPayment.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.DOWN_PAYMENT));

    TextView downPaymentTitle = 
        (TextView)findViewById(R.id.downPaymentTitle);

    downPaymentTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.downPaymentDescriptionText, 
            R.string.downPaymentTitleText, LoanActivity.this);
      }
    });
    
    extraYears.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.EXTRA_YEARS));

    TextView extraYearsTitle = 
        (TextView)findViewById(R.id.extraYearsTitle);
      extraYearsTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.extraYearsHelpText, 
            R.string.extraYearsTitleText, LoanActivity.this);
      }
    });
    
    totalPurchasePrice.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.TOTAL_PURCHASE_VALUE));

    TextView totalPurchasePriceTitle = 
        (TextView)findViewById(R.id.totalPurchasePriceTitle);
      totalPurchasePriceTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.totalPurchaseValueDescriptionText, 
            R.string.totalPurchasePriceTitleText, LoanActivity.this);
      }
    });

    closingCosts.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.CLOSING_COSTS));

    TextView closingCostsTitle = 
        (TextView)findViewById(R.id.closingCostsTitle);
      closingCostsTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.closingCostsDescriptionText, 
            R.string.closingCostsTitleText, LoanActivity.this);
      }
    });

    loanTerm.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
          long arg3) {

        int THIRTY_YEARS  = adapter.getPosition("Fixed-rate mortgage - 30 years");
        int TWENTY_YEARS =  adapter.getPosition("Fixed-rate mortgage - 20 years");
        int FIFTEEN_YEARS = adapter.getPosition("Fixed-rate mortgage - 15 years");
        Float value = null;

        if (pos == THIRTY_YEARS) {
          value = 360.0f;
        } else if (pos == FIFTEEN_YEARS) {
          value = 180.0f;
        } else if (pos == TWENTY_YEARS) {
          value = 240.0f;
        }
        ValueEnum key = ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS;
        dataController.setValueAsFloat(key, value);

      }


      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing.
      }
    });
    
    privateMortgageInsurance.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.PRIVATE_MORTGAGE_INSURANCE));
    
    TextView privateMortgageInsuranceTitle = 
        (TextView)findViewById(R.id.privateMortgageInsuranceTitle);

      privateMortgageInsuranceTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.privateMortgageInsuranceDescriptionText, 
            R.string.privateMortgageInsuranceTitleText, LoanActivity.this);
      }
    });

  }

  public void callCalculator(View v) {
    Utility.callCalc(this);
  }
  
  private void assignValuesToFields() {

    //Have to do the following in order to pick item in array by number - see setSelection()
    int THIRTY_YEARS  = adapter.getPosition("Fixed-rate mortgage - 30 years");
    int TWENTY_YEARS  = adapter.getPosition("Fixed-rate mortgage - 20 years");
    int FIFTEEN_YEARS = adapter.getPosition("Fixed-rate mortgage - 15 years");
    
    Float numOfCompoundingPeriods = 
        dataController.getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS);

    if (numOfCompoundingPeriods.intValue() == 360) {
      loanTerm.setSelection(THIRTY_YEARS);
    } else if (numOfCompoundingPeriods.intValue() == 180) {
      loanTerm.setSelection(FIFTEEN_YEARS);
    } else if (numOfCompoundingPeriods.intValue() == 240) {
      loanTerm.setSelection(TWENTY_YEARS);
    }
    Float tempVariable = null;

    tempVariable = dataController.getValueAsFloat(ValueEnum.YEARLY_INTEREST_RATE);
    yearlyInterestRate.setText(Utility.displayPercentage(tempVariable));

    tempVariable = dataController.getValueAsFloat(ValueEnum.DOWN_PAYMENT);
    downPayment.setText(Utility.displayCurrency(tempVariable));

    tempVariable = dataController.getValueAsFloat(ValueEnum.TOTAL_PURCHASE_VALUE);
    totalPurchasePrice.setText(Utility.displayCurrency(tempVariable));

    tempVariable = dataController.getValueAsFloat(ValueEnum.CLOSING_COSTS);
    closingCosts.setText(Utility.displayCurrency(tempVariable));
    
    tempVariable = dataController.getValueAsFloat(ValueEnum.PRIVATE_MORTGAGE_INSURANCE);
    privateMortgageInsurance.setText(Utility.displayCurrency(tempVariable));
    
    Integer tempInt = dataController.getValueAsFloat(ValueEnum.EXTRA_YEARS).intValue();
    extraYears.setText(tempInt.toString());
  }

  @Override
  protected void onPause() {
    super.onPause();

    ValueEnum key = ValueEnum.TOTAL_PURCHASE_VALUE;
    Float value = Utility.parseCurrency(totalPurchasePrice.getText().toString());
    dataController.setValueAsFloat(key, value);

    key = ValueEnum.DOWN_PAYMENT;
    value = Utility.parseCurrency(downPayment.getText().toString());
    dataController.setValueAsFloat(key, value);

    key = ValueEnum.YEARLY_INTEREST_RATE;
    value = Utility.parsePercentage(yearlyInterestRate.getText().toString());
    dataController.setValueAsFloat(key, value);

    key = ValueEnum.CLOSING_COSTS;
    value = Utility.parseCurrency(closingCosts.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.PRIVATE_MORTGAGE_INSURANCE;
    value = Utility.parseCurrency(privateMortgageInsurance.getText().toString());
    dataController.setValueAsFloat(key, value);
    
    key = ValueEnum.EXTRA_YEARS;
    value = Float.valueOf(extraYears.getText().toString());
    dataController.setValueAsFloat(key, value);
  }

  @Override
  protected void onResume() {
    // TODO Auto-generated method stub
    super.onResume();
  }
}
