package com.byronkatz.reap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

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

        DataController.setDataChanged(true);
        Intent intent = new Intent(SplashScreenActivity.this, GraphActivity.class);
        startActivity(intent); 
      }
    });
  }

  private void setAssumedValues(final Float totalValue) {

    final Float yearlyInterestRate = 0.055f;
    dataController.setValueAsFloat(ValueEnum.TOTAL_PURCHASE_VALUE, totalValue);
    dataController.setValueAsFloat(ValueEnum.YEARLY_INTEREST_RATE, yearlyInterestRate);
    dataController.setValueAsFloat(ValueEnum.PRIVATE_MORTGAGE_INSURANCE, 100f);
    dataController.setValueAsFloat(ValueEnum.DOWN_PAYMENT, totalValue * 0.20f);
    dataController.setValueAsFloat(ValueEnum.CLOSING_COSTS, 0f);
    dataController.setValueAsFloat(ValueEnum.MARGINAL_TAX_RATE, 0.25f);
    dataController.setValueAsFloat(ValueEnum.BUILDING_VALUE, totalValue * 0.80f);
    dataController.setValueAsFloat(ValueEnum.PROPERTY_TAX, totalValue * 0.01f);
    dataController.setValueAsFloat(ValueEnum.LOCAL_MUNICIPAL_FEES, 0f);
    dataController.setValueAsFloat(ValueEnum.GENERAL_SALE_EXPENSES, 2000f);
    dataController.setValueAsFloat(ValueEnum.SELLING_BROKER_RATE, 0.06f);
    dataController.setValueAsFloat(ValueEnum.INFLATION_RATE, 0.03f);
    dataController.setValueAsFloat(ValueEnum.REAL_ESTATE_APPRECIATION_RATE, 0.04f);
    dataController.setValueAsFloat(ValueEnum.ESTIMATED_RENT_PAYMENTS, 0f);
    dataController.setValueAsFloat(ValueEnum.INITIAL_HOME_INSURANCE, 1000f);
    dataController.setValueAsFloat(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE, 0.03f);
    dataController.setValueAsFloat(ValueEnum.FIX_UP_COSTS, 0f);
    dataController.setValueAsFloat(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES, 1000f);
    dataController.setValueAsFloat(ValueEnum.REQUIRED_RATE_OF_RETURN, yearlyInterestRate);

  }
}
