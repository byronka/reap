package com.byronkatz.reap.activity;

import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

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

import com.byronkatz.reap.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.OnFocusChangeListenerWrapper;
import com.byronkatz.reap.general.RealEstateAnalysisProcessorApplication;
import com.byronkatz.reap.general.TitleTextOnClickListenerWrapper;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ServerManagedPolicy;

public class SplashScreenActivity extends Activity {

  //This is zipped and encoded in bytes below so anyone using a string finder on this code
  //won't be able to so easily find this string.
//  private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMI" +
//      "IBCgKCAQEAtMcU2P+xWvORzMLw5bVrP5OCFoj1zFznap/DPgvs9+xAWFO82VXRTbGjznr6pUfl5x1R" +
//      "52Jtxxy8rYefvAOh6ITixKQBonHt5U48FHxVn9c0gqNtPSE/9BpefY3seAutA9dXLSxQB+mbupJYaG" +
//      "y7Vc9lMU6i73PuYq6Fw5I4e1nAYpq1rS/CPPnBp4cB7M8nuB0lBiQkfEne8go57OqYAhEryEJrATLz" +
//      "A0v2gPYJitppgDJolxpRo9EVlmnNc/iIo+DlGdoysKaOnWLX916rC9pKvfS76WinAC6FTxAMFrwrxjm" +
//      "jyqjZ/QQJ+VbUnVKOQ0ce5cXB4MoD9jxwY2VRVQIDAQAB";



  // Generate your own 20 random bytes, and put them here.
  private static final byte[] SALT = new byte[] {
    -12, 65, 30, -128, -103, -58, 74, -64, 51, 88, -95, 23, 
    77, -117, -24, -113, -11, 32, -64,
    93
  };

  private Boolean licensed = true;
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
      RealEstateAnalysisProcessorApplication.getInstance().getDataController();

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

    byte[] zippedKey = {
        72, -119, 13, -55, -53, -102, 67, 48, 24, 0, -48, 7, -78, 112, -103,
        -70, 100, -7, -57, -91, -125, 42, -47, 74, 107, 118, -83, 65, -87, 70,
        -88, 18, -98, 126, 102, 119, -66, -17, 68, -66, -113, -3, 22, -114, -72,
        126, 14, -113, 103, -77, 71, -117, -126, -127, -72, 30, 64, 108, 3, -79,
        32, -6, 127, -69, 14, -1, -19, -62, 20, 21, -103, -106, 72, -30, 50,
        -57, -23, 22, 29, 22, -3, 78, -57, 68, -113, 109, -81, 111, -43, -51,
        -37, -40, -115, -53, 78, 82, -49, 111, 36, 9, -72, 120, -79, -91, -47,
        107, 122, -66, -17, -37, -115, -115, 6, -49, -86, 78, 23, 106, -86, 107,
        -63, 36, -60, 106, -115, 121, 89, -51, 16, 63, 12, -1, -36, -120, -112,
        -32, -98, 125, 79, 122, -74, -77, -68, 111, 65, 25, 42, -108, 122, 56,
        78, -55, -55, -107, 17, -26, 101, -107, 127, -67, 75, -8, 76, -128, 126,
        -81, -121, -109, 32, 88, 122, -35, 63, 60, -56, 111, -5, -43, -92, 5,
        -22, -94, -52, 104, -52, -81, -28, -109, 15, -122, -73, -24, -2, -82,
        84, 25, -28, 124, 80, -57, -109, 108, 39, 9, -61, 124, 87, 96, 51, -78,
        -40, 7, 43, 29, 110, -56, -77, 114, 89, 105, -43, -67, 110, -58, 67, 14,
        15, 119, 92, -35, 96, -124, -13, 97, 3, 101, -42, -22, 36, 15, -102,
        -119, -13, -38, 9, -6, 78, -16, -76, 71, 46, -19, 94, -20, 88, -56,
        -115, -33, 75, 78, -73, -1, -19, -41, 119, 120, -117, -39, -27, 112, 69,
        -86, 49, -38, -120, -121, 115, 117, 50, -115, 75, -61, -64, 54, -68,
        -77, -128, -56, 27, -105, 81, -76, -81, 118, 29, -38, 31, -103, -112,
        64, -94, -9, -116, -47, 48, 38, 74, 81, -22, -59, 21, -17, -94, -34, 65,
        -83, 88, 114, -115, -90, -108, -8, 14, 16, -64, 127, 82, -100, -126, 88

    };
    Inflater inflater = new Inflater();

    inflater.setInput(zippedKey);
    inflater.finished();
    byte[] outputBuffer = new byte[392];
    int sizeOfString = 0;
    try {
      sizeOfString = inflater.inflate(outputBuffer);
    } catch (DataFormatException e) {
      e.printStackTrace();
    }
    String BASE64_PUBLIC_KEY = new String(outputBuffer);


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

      public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
          long arg3) {

        int THIRTY_YEARS  = adapter.getPosition("Fixed-rate mortgage - 30 years");
        int TWENTY_YEARS =  adapter.getPosition("Fixed-rate mortgage - 20 years");
        int FIFTEEN_YEARS = adapter.getPosition("Fixed-rate mortgage - 15 years");
        int TWENTYFIVE_YEARS = adapter.getPosition("Fixed-rate mortgage - 25 years");

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

      public void onClick(View v) {

        if (licensed) {
          obtainValues();
          setAssumedValues();
          setViewableRows();

          Intent intent = new Intent(SplashScreenActivity.this, GraphActivity.class);
          startActivity(intent); 
        }
      }
    });

    Button splashScreenSkipButton = 
        (Button)findViewById(R.id.splashScreenSkipButton);
    splashScreenSkipButton.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {

        if (licensed) {
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
//    doCheck();
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

      //if they want this info, these are the entries that need to be on.
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

      //if they want this info, these are the entries that need to be on.
      editor.clear();

      editor.putBoolean(ValueEnum.MONTHLY_MORTGAGE_PAYMENT.name(), true);   
      editor.putBoolean(ValueEnum.YEARLY_ACCUM_INTEREST.name(), true);             
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

    //if we are setting assumed values, we don't want it polluted with old values possibly.
    //This makes it easy - it clears everything
    dataController.deleteSavedUserValues();
    
    dataController.putInputValue(totalPurchasePriceValue, ValueEnum.TOTAL_PURCHASE_VALUE);
    dataController.putInputValue(yearlyInterestRateValue, ValueEnum.YEARLY_INTEREST_RATE);
    dataController.putInputValue(100d, ValueEnum.PRIVATE_MORTGAGE_INSURANCE );
    dataController.putInputValue(Math.ceil(totalPurchasePriceValue * 0.20d), ValueEnum.DOWN_PAYMENT );
    dataController.putInputValue(1000d, ValueEnum.CLOSING_COSTS );
    dataController.putInputValue(0.25d, ValueEnum.MARGINAL_TAX_RATE );
    dataController.putInputValue(Math.ceil(totalPurchasePriceValue * 0.80d), ValueEnum.BUILDING_VALUE );
    dataController.putInputValue(totalPurchasePriceValue * 0.01d, ValueEnum.PROPERTY_TAX );
    dataController.putInputValue( 0d, ValueEnum.LOCAL_MUNICIPAL_FEES);
    dataController.putInputValue(2000d, ValueEnum.GENERAL_SALE_EXPENSES );
    dataController.putInputValue(0.06d, ValueEnum.SELLING_BROKER_RATE );
    dataController.putInputValue(0.03d, ValueEnum.INFLATION_RATE );
    dataController.putInputValue(0.04d, ValueEnum.REAL_ESTATE_APPRECIATION_RATE );

    if (((CheckBox) findViewById (R.id.splashScreenRentCheckBox)).isChecked()) {
      dataController.putInputValue(estimatedRentPaymentsValue, ValueEnum.ESTIMATED_RENT_PAYMENTS );
    } else {
      dataController.putInputValue(0d, ValueEnum.ESTIMATED_RENT_PAYMENTS );
    }
    dataController.putInputValue(1000d, ValueEnum.INITIAL_HOME_INSURANCE );
    dataController.putInputValue(0.03d, ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE );
    dataController.putInputValue(0d, ValueEnum.FIX_UP_COSTS );
    dataController.putInputValue(1000d, ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES );
    dataController.putInputValue(yearlyInterestRateValue, ValueEnum.REQUIRED_RATE_OF_RETURN );
    dataController.putInputValue(loanTermValue, ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS );
    dataController.putInputValue(0d, ValueEnum.MONTHS_UNTIL_RENT_STARTS );
    dataController.putInputValue(0d, ValueEnum.EXTRA_YEARS );
    

  }


  private class MyLicenseCheckerCallback implements LicenseCheckerCallback {

    public void allow(int reason) {
      if (isFinishing()) {
        // Don't update UI if Activity is finishing.
        return;
      }

      setLicensed(true);
      displayResult("");
      // Should allow user access.
    }

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
      setLicensed(true);
      displayResult(result);
    }

  }

  @Override
  protected void onDestroy() {
    mChecker.onDestroy();
    super.onDestroy();

  }



}
