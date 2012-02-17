package com.byronkatz.reap.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.OnFocusChangeListenerWrapper;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

public class SplashScreenActivity extends Activity {

  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.splash_screen);

    final EditText splashScreenValueEntry = (EditText) findViewById (R.id.splashScreenValueEntry);
    splashScreenValueEntry.setOnFocusChangeListener(
        new OnFocusChangeListenerWrapper(ValueEnum.TOTAL_PURCHASE_VALUE));
    
    TextView totalPurchaseValueSplashTitle = 
        (TextView)findViewById(R.id.totalPurchaseValueSplashTitle);
    totalPurchaseValueSplashTitle.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Utility.showHelpDialog(
            R.string.totalPurchaseValueSplashHelpText, 
            R.string.totalPurchaseValueSplashTitleText,
            SplashScreenActivity.this);
      }
    });

    Button splashScreenGoButton = 
        (Button)findViewById(R.id.splashScreenGoButton);
    splashScreenGoButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        Float enteredValue = Utility.parseCurrency(splashScreenValueEntry.getText().toString());

        if (enteredValue == 0) {
          Utility.showToast(SplashScreenActivity.this, "Must enter a value greater than 0");
        } else {

          setAssumedValues(enteredValue);
          setViewableRows();
          DataController.setDataChanged(true);
          Intent intent = new Intent(SplashScreenActivity.this, GraphActivity.class);
          startActivity(intent); 
          finish();
        }
      }
    });

    Button splashScreenSkipButton = 
        (Button)findViewById(R.id.splashScreenSkipButton);
    splashScreenSkipButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        DataController.setDataChanged(true);
        Intent intent = new Intent(SplashScreenActivity.this, GraphActivity.class);
        startActivity(intent); 
        finish();
      }
    });
  }

  private void setViewableRows() {
    
    SharedPreferences sp = getSharedPreferences(GraphActivity.PREFS_NAME, MODE_PRIVATE);

    SharedPreferences.Editor editor = sp.edit();
    Log.d(getClass().getName(), "getting editor");
    
    if (((CheckBox) findViewById (R.id.splashScreenRentCheckBox)).isChecked()) {
      
      Log.d(getClass().getName(), "rental unit");

      editor.clear();
      editor.putBoolean(ValueEnum.NPV.name(), true);   
      editor.putBoolean(ValueEnum.ATCF.name(), true);             
      editor.putBoolean(ValueEnum.ATCF_ACCUMULATOR.name(), true);       
      editor.putBoolean(ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN.name(), true);       
      editor.putBoolean(ValueEnum.CAP_RATE_ON_PROJECTED_VALUE.name(), true);         
      editor.putBoolean(ValueEnum.CAP_RATE_ON_PURCHASE_VALUE.name(), true);       
      editor.putBoolean(ValueEnum.PROJECTED_HOME_VALUE.name(), true);      
      editor.putBoolean(ValueEnum.ATER.name(), true);  

      editor.putBoolean("IS_GRAPH_VISIBLE", true);

      editor.commit();
      
    } else {
      
      Log.d(getClass().getName(), "non-rental unit");

      editor.clear();

      editor.putBoolean(ValueEnum.MONTHLY_MORTGAGE_PAYMENT.name(), true);   
      editor.putBoolean(ValueEnum.ACCUM_INTEREST.name(), true);             
      editor.putBoolean(ValueEnum.TOTAL_PURCHASE_VALUE.name(), true);       
      editor.putBoolean(ValueEnum.YEARLY_INTEREST_RATE.name(), true);       
      editor.putBoolean(ValueEnum.BROKER_CUT_OF_SALE.name(), true);         
      editor.putBoolean(ValueEnum.PROJECTED_HOME_VALUE.name(), true);       
      editor.putBoolean(ValueEnum.YEARLY_PRINCIPAL_PAID.name(), true);      
      editor.putBoolean(ValueEnum.YEARLY_INTEREST_PAID.name(), true);       
      
      editor.putBoolean("IS_GRAPH_VISIBLE", false);

      editor.commit();
    }
  }
  
  private void setAssumedValues(final Float totalValue) {

    final Float yearlyInterestRate = 0.055f;
    dataController.setValueAsFloat(ValueEnum.TOTAL_PURCHASE_VALUE, totalValue);
    dataController.setValueAsFloat(ValueEnum.YEARLY_INTEREST_RATE, yearlyInterestRate);
    dataController.setValueAsFloat(ValueEnum.PRIVATE_MORTGAGE_INSURANCE, 100f);
    dataController.setValueAsFloat(ValueEnum.DOWN_PAYMENT, totalValue * 0.20f);
    dataController.setValueAsFloat(ValueEnum.CLOSING_COSTS, 1000f);
    dataController.setValueAsFloat(ValueEnum.MARGINAL_TAX_RATE, 0.25f);
    dataController.setValueAsFloat(ValueEnum.BUILDING_VALUE, totalValue * 0.80f);
    dataController.setValueAsFloat(ValueEnum.PROPERTY_TAX, totalValue * 0.01f);
    dataController.setValueAsFloat(ValueEnum.LOCAL_MUNICIPAL_FEES, 0f);
    dataController.setValueAsFloat(ValueEnum.GENERAL_SALE_EXPENSES, 2000f);
    dataController.setValueAsFloat(ValueEnum.SELLING_BROKER_RATE, 0.06f);
    dataController.setValueAsFloat(ValueEnum.INFLATION_RATE, 0.03f);
    dataController.setValueAsFloat(ValueEnum.REAL_ESTATE_APPRECIATION_RATE, 0.04f);

    if (((CheckBox) findViewById (R.id.splashScreenRentCheckBox)).isChecked()) {
      dataController.setValueAsFloat(ValueEnum.ESTIMATED_RENT_PAYMENTS, totalValue * 0.005f);
    } else {
      dataController.setValueAsFloat(ValueEnum.ESTIMATED_RENT_PAYMENTS, 0f);
    }
    dataController.setValueAsFloat(ValueEnum.INITIAL_HOME_INSURANCE, 1000f);
    dataController.setValueAsFloat(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE, 0.03f);
    dataController.setValueAsFloat(ValueEnum.FIX_UP_COSTS, 0f);
    dataController.setValueAsFloat(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES, 1000f);
    dataController.setValueAsFloat(ValueEnum.REQUIRED_RATE_OF_RETURN, yearlyInterestRate);

  }
}
