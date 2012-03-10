package com.byronkatz.reap.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.OnFocusChangeListenerWrapper;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.TitleTextOnClickListenerWrapper;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ServerManagedPolicy;

public class SplashScreenActivity extends Activity {

  private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMI" +
  		"IBCgKCAQEAtMcU2P+xWvORzMLw5bVrP5OCFoj1zFznap/DPgvs9+xAWFO82VXRTbGjznr6pUfl5x1R" +
  		"52Jtxxy8rYefvAOh6ITixKQBonHt5U48FHxVn9c0gqNtPSE/9BpefY3seAutA9dXLSxQB+mbupJYaG" +
  		"y7Vc9lMU6i73PuYq6Fw5I4e1nAYpq1rS/CPPnBp4cB7M8nuB0lBiQkfEne8go57OqYAhEryEJrATLz" +
  		"A0v2gPYJitppgDJolxpRo9EVlmnNc/iIo+DlGdoysKaOnWLX916rC9pKvfS76WinAC6FTxAMFrwrxjm" +
  		"jyqjZ/QQJ+VbUnVKOQ0ce5cXB4MoD9jxwY2VRVQIDAQAB";

  // Generate your own 20 random bytes, and put them here.
  private static final byte[] SALT = new byte[] {
    -12, 65, 30, -128, -103, -58, 74, -64, 51, 88, -95, 23, 
    77, -117, -24, -113, -11, 32, -64,
    93
  };

  private Boolean licensed = false;
  private LicenseCheckerCallback mLicenseCheckerCallback;
  private LicenseChecker mChecker;
  // A handler on the UI thread.
  private Handler mHandler;
  
  ArrayAdapter<CharSequence> adapter;
  
  private EditText yearlyInterestRate;
  private Double yearlyInterestRateValue;
  private EditText totalPurchasePrice;
  private Double totalPurchasePriceValue;
  private Spinner loanTerm;
  private Double loanTermValue;
  private EditText estimatedRentPayments;
  private Double estimatedRentPaymentsValue;
  private CheckBox mCheckBox;


  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();

  public void setLicensed(Boolean licensed) {
    this.licensed = licensed;
  }
  
  /**
   * This gets values from the three input fields.  The spinner sets its value differently.
   */
  private void obtainValues() {
    totalPurchasePriceValue = Utility.parseCurrency(totalPurchasePrice.getText().toString());
    yearlyInterestRateValue = Utility.parsePercentage(yearlyInterestRate.getText().toString());
    estimatedRentPaymentsValue = Utility.parseCurrency(estimatedRentPayments.getText().toString());
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    setContentView(R.layout.splash_screen);
    mHandler = new Handler();


    yearlyInterestRate = (EditText)findViewById(R.id.yearlyInterestRateEditText);
    loanTerm           = (Spinner) findViewById(R.id.numOfCompoundingPeriodsSpinner);
    totalPurchasePrice = (EditText)findViewById(R.id.splashScreenValueEntry);
    estimatedRentPayments         = (EditText)findViewById(R.id.estimatedRentPaymentsEditText);
    mCheckBox          = (CheckBox) findViewById(R.id.splashScreenRentCheckBox);

    adapter = ArrayAdapter.createFromResource(
        this, R.array.numOfCompoundingPeriodsArray, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    loanTerm.setAdapter(adapter);
    
    
    mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          findViewById(R.id.estimatedRentRow).setVisibility(View.VISIBLE);
        } else if (!isChecked) {
          findViewById(R.id.estimatedRentRow).setVisibility(View.INVISIBLE);
          
        }
        
      }
    });
    
    
    totalPurchasePrice.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.TOTAL_PURCHASE_VALUE));

    ((TextView)findViewById(R.id.totalPurchaseValueSplashTitle)).setOnClickListener(
        new TitleTextOnClickListenerWrapper(ValueEnum.TOTAL_PURCHASE_VALUE));
   
   
    yearlyInterestRate.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.YEARLY_INTEREST_RATE));

    ((TextView)findViewById(R.id.yearlyInterestRateTitle)).setOnClickListener(
        new TitleTextOnClickListenerWrapper(ValueEnum.YEARLY_INTEREST_RATE));
    
    
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
          loanTermValue = 360.0d;
        } else if (pos == FIFTEEN_YEARS) {
          loanTermValue = 180.0d;
        } else if (pos == TWENTY_YEARS) {
          loanTermValue = 240.0d;
        } else if (pos == TWENTYFIVE_YEARS) {
          loanTermValue = 300.0d;
        } 

      }


      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
        // Do nothing.
      }
    });

    
    estimatedRentPayments.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.ESTIMATED_RENT_PAYMENTS));

    ((TextView)findViewById(R.id.estimatedRentPaymentsTitle)).setOnClickListener(
        new TitleTextOnClickListenerWrapper(ValueEnum.ESTIMATED_RENT_PAYMENTS));
    
    
    
    
    

    Button splashScreenGoButton = 
        (Button)findViewById(R.id.splashScreenGoButton);
    splashScreenGoButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        if (licensed) {
            obtainValues();
            setAssumedValues();
            setViewableRows();
            DataController.setDataChanged(true);

            Intent intent = new Intent(SplashScreenActivity.this, GraphActivity.class);
            startActivity(intent); 

        }
      }
    });

    Button splashScreenSkipButton = 
        (Button)findViewById(R.id.splashScreenSkipButton);
    splashScreenSkipButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        if (licensed) {

          DataController.setDataChanged(true);
          Intent intent = new Intent(SplashScreenActivity.this, GraphActivity.class);
          startActivity(intent); 
        }
      }
    });

    //    //below code section handles licensing

    // Try to use more data here. ANDROID_ID is a single point of attack.
    String deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

    // Library calls this when it's done.
    mLicenseCheckerCallback = new MyLicenseCheckerCallback();
    // Construct the LicenseChecker with a policy.
    mChecker = new LicenseChecker(
        this, new ServerManagedPolicy(this,
            new AESObfuscator(SALT, getPackageName(), deviceId)),
            BASE64_PUBLIC_KEY);
    doCheck();
  }
  
  

  protected Dialog onCreateDialog(int id) {
    // We have two dialogs - one for when it clearly is not licensed, and one for when we failed on retry.
    //This first one is when failed retry 
    if (id == Policy.RETRY) {
    return new AlertDialog.Builder(this)
    .setTitle(R.string.unlicensed_dialog_title)
    .setMessage(R.string.failed_retry_dialog_body)
    .setNegativeButton(R.string.quit_button, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        finish();
      }
    })
    .create();
    
    } else {
      
    //This second one is for clearly not licensed
    return new AlertDialog.Builder(this)
    .setTitle(R.string.unlicensed_dialog_title)
    .setMessage(R.string.unlicensed_dialog_body)
    .setPositiveButton(R.string.buy_button, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
            "http://market.android.com/details?id=" + getPackageName()));
        startActivity(marketIntent);
      }
    })
    .setNegativeButton(R.string.quit_button, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        finish();
      }
    })
    .create();
    }
  }

  
  private void doCheck() {
    //first thing is grey out the entry values
    deactivateInterface();
    
    setProgressBarIndeterminateVisibility(true);
    setTitle(R.string.titleBarTextWhileCheckLicense);
    mChecker.checkAccess(mLicenseCheckerCallback);
  }

  private void deactivateInterface() {
    
  
    ((Button)findViewById(R.id.splashScreenGoButton)).setEnabled(false);
    ((Button)findViewById(R.id.splashScreenSkipButton)).setEnabled(false);
    ((CheckBox) findViewById(R.id.splashScreenRentCheckBox)).setEnabled(false);
    yearlyInterestRate.setEnabled(false);
    totalPurchasePrice.setEnabled(false);
    loanTerm.setEnabled(false);
    estimatedRentPayments.setEnabled(false);
  }
  
  private void activateInterface() {
    
    ((Button)findViewById(R.id.splashScreenGoButton)).setEnabled(true);
    ((Button)findViewById(R.id.splashScreenSkipButton)).setEnabled(true);
    ((CheckBox) findViewById(R.id.splashScreenRentCheckBox)).setEnabled(true);
    yearlyInterestRate.setEnabled(true);
    totalPurchasePrice.setEnabled(true);
    loanTerm.setEnabled(true);
    estimatedRentPayments.setEnabled(true);
  }

  /**
   * This method only gets called if the license check comes back to allow use.
   * Otherwise, a dialog is presented to purchase the app.
   * @param result
   */
  private void displayResult(final String result) {
    mHandler.post(new Runnable() {
        public void run() {
          activateInterface();
            setTitle(getString(R.string.realEstateMarketAnalysisSplashPageDescription));
            setProgressBarIndeterminateVisibility(false);
        }
    });
  }
    
  private void setViewableRows() {

    SharedPreferences sp = getSharedPreferences(GraphActivity.PREFS_NAME, MODE_PRIVATE);

    SharedPreferences.Editor editor = sp.edit();
    if (((CheckBox) findViewById (R.id.splashScreenRentCheckBox)).isChecked()) {

      //don't clear - if they want this info, these are the entries that need to be on.
      editor.clear();
      editor.putBoolean(ValueEnum.NPV.name(), true);   
      editor.putBoolean(ValueEnum.ATCF.name(), true);             
      editor.putBoolean(ValueEnum.ATCF_ACCUMULATOR.name(), true);       
      editor.putBoolean(ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN.name(), true);       
      editor.putBoolean(ValueEnum.CAP_RATE_ON_PROJECTED_VALUE.name(), true);         
      editor.putBoolean(ValueEnum.CAP_RATE_ON_PURCHASE_VALUE.name(), true);       
      editor.putBoolean(ValueEnum.PROJECTED_HOME_VALUE.name(), true);      
      editor.putBoolean(ValueEnum.YEARLY_INCOME.name(), true);      
      editor.putBoolean(ValueEnum.YEARLY_INTEREST_RATE.name(), true);      
      editor.putBoolean(ValueEnum.REQUIRED_RATE_OF_RETURN.name(), true);      
      editor.putBoolean(ValueEnum.PROJECTED_HOME_VALUE.name(), true);      
      editor.putBoolean(ValueEnum.MONTHLY_RENT_FV.name(), true);
      editor.putBoolean(ValueEnum.ATER.name(), true);  

      editor.putBoolean(GraphActivity.IS_GRAPH_VISIBLE, true);

      editor.commit();

    } else {

      //don't clear - if they want this info, these are the entries that need to be on.
      editor.clear();

      editor.putBoolean(ValueEnum.MONTHLY_MORTGAGE_PAYMENT.name(), true);   
      editor.putBoolean(ValueEnum.ACCUM_INTEREST.name(), true);             
      editor.putBoolean(ValueEnum.TOTAL_PURCHASE_VALUE.name(), true);       
      editor.putBoolean(ValueEnum.YEARLY_INTEREST_RATE.name(), true);       
      editor.putBoolean(ValueEnum.BROKER_CUT_OF_SALE.name(), true);         
      editor.putBoolean(ValueEnum.PROJECTED_HOME_VALUE.name(), true);       
      editor.putBoolean(ValueEnum.YEARLY_PRINCIPAL_PAID.name(), true);      
      editor.putBoolean(ValueEnum.YEARLY_INTEREST_PAID.name(), true);       

      editor.putBoolean(GraphActivity.IS_GRAPH_VISIBLE, false);

      editor.commit();
    }
  }

  private void setAssumedValues() {
    
    dataController.setValueAsDouble(ValueEnum.TOTAL_PURCHASE_VALUE, totalPurchasePriceValue);
    dataController.setValueAsDouble(ValueEnum.YEARLY_INTEREST_RATE, yearlyInterestRateValue);
    dataController.setValueAsDouble(ValueEnum.PRIVATE_MORTGAGE_INSURANCE, 100d);
    dataController.setValueAsDouble(ValueEnum.DOWN_PAYMENT, Math.ceil(totalPurchasePriceValue * 0.20d));
    dataController.setValueAsDouble(ValueEnum.CLOSING_COSTS, 1000d);
    dataController.setValueAsDouble(ValueEnum.MARGINAL_TAX_RATE, 0.25d);
    dataController.setValueAsDouble(ValueEnum.BUILDING_VALUE, Math.ceil(totalPurchasePriceValue * 0.80d));
    dataController.setValueAsDouble(ValueEnum.PROPERTY_TAX, totalPurchasePriceValue * 0.01d);
    dataController.setValueAsDouble(ValueEnum.LOCAL_MUNICIPAL_FEES, 0d);
    dataController.setValueAsDouble(ValueEnum.GENERAL_SALE_EXPENSES, 2000d);
    dataController.setValueAsDouble(ValueEnum.SELLING_BROKER_RATE, 0.06d);
    dataController.setValueAsDouble(ValueEnum.INFLATION_RATE, 0.03d);
    dataController.setValueAsDouble(ValueEnum.REAL_ESTATE_APPRECIATION_RATE, 0.04d);

    if (((CheckBox) findViewById (R.id.splashScreenRentCheckBox)).isChecked()) {
      dataController.setValueAsDouble(ValueEnum.ESTIMATED_RENT_PAYMENTS, estimatedRentPaymentsValue);
    } else {
      dataController.setValueAsDouble(ValueEnum.ESTIMATED_RENT_PAYMENTS, 0d);
    }
    dataController.setValueAsDouble(ValueEnum.INITIAL_HOME_INSURANCE, 1000d);
    dataController.setValueAsDouble(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE, 0.03d);
    dataController.setValueAsDouble(ValueEnum.FIX_UP_COSTS, 0d);
    dataController.setValueAsDouble(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES, 1000d);
    dataController.setValueAsDouble(ValueEnum.REQUIRED_RATE_OF_RETURN, yearlyInterestRateValue);
    dataController.setValueAsDouble(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS, loanTermValue);
    dataController.setValueAsDouble(ValueEnum.MONTHS_UNTIL_RENT_STARTS, 0d);
    dataController.setValueAsDouble(ValueEnum.EXTRA_YEARS, 0d);

  }


  private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
    
    @Override
    public void allow(int reason) {
      if (isFinishing()) {
        // Don't update UI if Activity is finishing.
        return;
      }
      
      setLicensed(true);
      displayResult("");
      // Should allow user access.
    }

    @Override
    public void dontAllow(int reason) {
      if (isFinishing()) {
        // Don't update UI if Activity is finishing.
        return;
      }

      // Should not allow access. In most cases, the app should assume
      // the user has access unless it encounters this. If it does,
      // the app should inform the user of their unlicensed ways
      // and then either shut down the app or limit the user to a
      // restricted set of features.
      // In this example, we show a dialog that takes the user to Market.
      setLicensed(false);

      showDialog(reason);
    }

    public void applicationError(int errorCode) {
      if (isFinishing()) {
        // Don't update UI if Activity is finishing.
        return;
      }
      // This is a polite way of saying the developer made a mistake
      // while setting up or calling the license checker library.
      // Please examine the error code and fix the error.
      String result = String.format(getString(R.string.application_error), errorCode);
      Log.d(getClass().getName(), "result is " + result);
      setLicensed(true);
      displayResult(result);
    }

  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mChecker.onDestroy();
  dataController.nullifyNumericCache();
  System.runFinalizersOnExit(true);
  }



}
