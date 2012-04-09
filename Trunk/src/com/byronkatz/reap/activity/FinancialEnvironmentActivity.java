package com.byronkatz.reap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.byronkatz.reap.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.OnFocusChangeListenerWrapper;
import com.byronkatz.reap.general.RealEstateAnalysisProcessorApplication;
import com.byronkatz.reap.general.TitleTextOnClickListenerWrapper;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

public class FinancialEnvironmentActivity extends Activity {

  private EditText inflationRate;
  private EditText realEstateAppreciationRate;
  private final DataController dataController = 
      RealEstateAnalysisProcessorApplication.getInstance().getDataController();
  
  
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
    setContentView(R.layout.financial_environment);

    //Hook up the components from the GUI to some variables here
    inflationRate               = (EditText)findViewById(R.id.inflationRateEditText);
    realEstateAppreciationRate  = (EditText)findViewById(R.id.realEstateAppreciationRateEditText);
    
   
    inflationRate.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.INFLATION_RATE));
    
    ((TextView)findViewById(R.id.inflationRateTitle)).setOnClickListener(
        new TitleTextOnClickListenerWrapper(ValueEnum.INFLATION_RATE));
    
    
    realEstateAppreciationRate.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.REAL_ESTATE_APPRECIATION_RATE));
    
    ((TextView)findViewById(R.id.realEstateAppreciationRateTitle)).setOnClickListener(
        new TitleTextOnClickListenerWrapper(ValueEnum.REAL_ESTATE_APPRECIATION_RATE));
    
  }
  
  public void callCalculator(View v) {
    Utility.callCalc(this);
  }
  
  
  private void assignValuesToFields() {

    Double ir = dataController.getInputValue(ValueEnum.INFLATION_RATE);
    inflationRate.setText(Utility.displayPercentage(ir));
    
    Double rear = dataController.getInputValue(ValueEnum.REAL_ESTATE_APPRECIATION_RATE);
    realEstateAppreciationRate.setText(Utility.displayPercentage(rear));
    
  }
  
  private void saveValuesToCache() {
    ValueEnum key = ValueEnum.INFLATION_RATE;
    Double value = Utility.parsePercentage(inflationRate.getText().toString());
    dataController.putInputValue(value, key );
    
    key = ValueEnum.REAL_ESTATE_APPRECIATION_RATE;
    value = Utility.parsePercentage(realEstateAppreciationRate.getText().toString());   
    dataController.putInputValue(value, key ); 
  }
  
  @Override
  public void onPause() {
    saveValuesToCache();
    dataController.saveFieldValues();
    super.onPause();
  }
}
