package com.byronkatz.reap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.byronkatz.reap.general.TitleTextOnClickListenerWrapper;

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

  
  @Override
  public boolean onCreateOptionsMenu (Menu menu){
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.edit_data_page_menu, menu);
    return true;
  }
  
  /**
   * This gets called every time the menu is called
   */
  @Override
  public boolean onPrepareOptionsMenu (Menu menu) {
    super.onPrepareOptionsMenu(menu);
    saveValuesToCache();

    return true;
  }

  @Override
  public boolean onOptionsItemSelected (MenuItem item) {
    super.onOptionsItemSelected(item);

    Utility.switchForMenuItem(item, this);
    return false;
  }
  
  
  @Override
  public void onResume() {
    super.onResume();
    assignValuesToFields();

  }
  
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

    pmiButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Double totalPurchasevalue = 
            Utility.parseCurrency(totalPurchasePrice.getText().toString());
        Double pmiDownPayment = totalPurchasevalue * Mortgage.PMI_PERCENTAGE;
        String pmiDownPaymentText = Utility.displayCurrency(pmiDownPayment);
        downPayment.setText(pmiDownPaymentText);

      }
    });

    yearlyInterestRate.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.YEARLY_INTEREST_RATE));

        ((TextView)findViewById(R.id.yearlyInterestRateTitle)).setOnClickListener(
            new TitleTextOnClickListenerWrapper(ValueEnum.YEARLY_INTEREST_RATE));
        


    downPayment.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.DOWN_PAYMENT));
    
    ((TextView)findViewById(R.id.downPaymentTitle)).setOnClickListener(
        new TitleTextOnClickListenerWrapper(ValueEnum.DOWN_PAYMENT));
    
    extraYears.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.EXTRA_YEARS));

    ((TextView)findViewById(R.id.extraYearsTitle)).setOnClickListener(
        new TitleTextOnClickListenerWrapper(ValueEnum.EXTRA_YEARS));

    
    totalPurchasePrice.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.TOTAL_PURCHASE_VALUE));


        ((TextView)findViewById(R.id.totalPurchasePriceTitle)).setOnClickListener(
            new TitleTextOnClickListenerWrapper(ValueEnum.TOTAL_PURCHASE_VALUE));
       

    closingCosts.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.CLOSING_COSTS));

        ((TextView)findViewById(R.id.closingCostsTitle)).setOnClickListener(
            new TitleTextOnClickListenerWrapper(ValueEnum.CLOSING_COSTS));
     

    loanTerm.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
          long arg3) {

        int THIRTY_YEARS  = adapter.getPosition("Fixed-rate mortgage - 30 years");
        int TWENTY_YEARS =  adapter.getPosition("Fixed-rate mortgage - 20 years");
        int FIFTEEN_YEARS = adapter.getPosition("Fixed-rate mortgage - 15 years");
        int TWENTYFIVE_YEARS = adapter.getPosition("Fixed-rate mortgage - 25 years");
        Double value = null;

        if (pos == THIRTY_YEARS) {
          value = 360.0d;
        } else if (pos == FIFTEEN_YEARS) {
          value = 180.0d;
        } else if (pos == TWENTY_YEARS) {
          value = 240.0d;
        } else if (pos == TWENTYFIVE_YEARS) {
          value = 300.0d;
        } 
        ValueEnum key = ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS;
        dataController.setValueAsDouble(key, value);

      }


      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing.
      }
    });
    
    privateMortgageInsurance.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.PRIVATE_MORTGAGE_INSURANCE));
    
        ((TextView)findViewById(R.id.privateMortgageInsuranceTitle)).setOnClickListener(
            new TitleTextOnClickListenerWrapper(ValueEnum.PRIVATE_MORTGAGE_INSURANCE));

  }

  public void callCalculator(View v) {
    Utility.callCalc(this);
  }
  
  private void assignValuesToFields() {

    //Have to do the following in order to pick item in array by number - see setSelection()
    int THIRTY_YEARS  = adapter.getPosition("Fixed-rate mortgage - 30 years");
    int TWENTYFIVE_YEARS = adapter.getPosition("Fixed-rate mortgage - 25 years");
    
    int TWENTY_YEARS  = adapter.getPosition("Fixed-rate mortgage - 20 years");
    int FIFTEEN_YEARS = adapter.getPosition("Fixed-rate mortgage - 15 years");
    
    Double numOfCompoundingPeriods = 
        dataController.getValueAsDouble(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS);

    if (numOfCompoundingPeriods.intValue() == 360) {
      loanTerm.setSelection(THIRTY_YEARS);
    } else if (numOfCompoundingPeriods.intValue() == 180) {
      loanTerm.setSelection(FIFTEEN_YEARS);
    } else if (numOfCompoundingPeriods.intValue() == 240) {
      loanTerm.setSelection(TWENTY_YEARS);
    } else if (numOfCompoundingPeriods.intValue() == 300) {
      loanTerm.setSelection(TWENTYFIVE_YEARS);
    }
      
    Double tempVariable = null;

    tempVariable = dataController.getValueAsDouble(ValueEnum.YEARLY_INTEREST_RATE);
    yearlyInterestRate.setText(Utility.displayPercentage(tempVariable));

    tempVariable = dataController.getValueAsDouble(ValueEnum.DOWN_PAYMENT);
    downPayment.setText(Utility.displayCurrency(tempVariable));

    tempVariable = dataController.getValueAsDouble(ValueEnum.TOTAL_PURCHASE_VALUE);
    totalPurchasePrice.setText(Utility.displayCurrency(tempVariable));

    tempVariable = dataController.getValueAsDouble(ValueEnum.CLOSING_COSTS);
    closingCosts.setText(Utility.displayCurrency(tempVariable));
    
    tempVariable = dataController.getValueAsDouble(ValueEnum.PRIVATE_MORTGAGE_INSURANCE);
    privateMortgageInsurance.setText(Utility.displayCurrency(tempVariable));
    
    Integer tempInt = dataController.getValueAsDouble(ValueEnum.EXTRA_YEARS).intValue();
    extraYears.setText(tempInt.toString());
  }

  private void saveValuesToCache() {
    ValueEnum key = ValueEnum.TOTAL_PURCHASE_VALUE;
    Double value = Utility.parseCurrency(totalPurchasePrice.getText().toString());
    dataController.setValueAsDouble(key, value);

    key = ValueEnum.DOWN_PAYMENT;
    value = Utility.parseCurrency(downPayment.getText().toString());
    dataController.setValueAsDouble(key, value);

    key = ValueEnum.YEARLY_INTEREST_RATE;
    value = Utility.parsePercentage(yearlyInterestRate.getText().toString());
    dataController.setValueAsDouble(key, value);

    key = ValueEnum.CLOSING_COSTS;
    value = Utility.parseCurrency(closingCosts.getText().toString());
    dataController.setValueAsDouble(key, value);
    
    key = ValueEnum.PRIVATE_MORTGAGE_INSURANCE;
    value = Utility.parseCurrency(privateMortgageInsurance.getText().toString());
    dataController.setValueAsDouble(key, value);
    
    key = ValueEnum.EXTRA_YEARS;
    value = Double.valueOf(extraYears.getText().toString());
    dataController.setValueAsDouble(key, value);
  }
  
  @Override
  public void onPause() {
    saveValuesToCache();
    dataController.saveFieldValues();
    super.onPause();
  }

}
