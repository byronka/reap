package com.byronkatz.reap.activity;

import com.byronkatz.R;
import com.byronkatz.R.array;
import com.byronkatz.R.id;
import com.byronkatz.R.layout;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;
import com.byronkatz.reap.general.ValueEnum.ValueType;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class AddressActivity extends Activity {

  private static final int STATE_ARRAY_SIZE = 50;
  String[] statePosition;
  private EditText streetAddressEditText;
  private EditText cityEditText;
  private Spinner stateSpinner;
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  private ArrayAdapter<CharSequence> adapter;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.address);

    statePosition = new String[STATE_ARRAY_SIZE];
    streetAddressEditText = (EditText) findViewById(R.id.streetAddressEditText);
    cityEditText = (EditText) findViewById(R.id.cityEditText);
    stateSpinner = (Spinner) findViewById(R.id.stateTitleSpinner);

    adapter = ArrayAdapter.createFromResource(this, R.array.states_array, 
        android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    stateSpinner.setAdapter(adapter);


    assignValuesToFields();

    stateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
          long arg3) {
        ValueEnum key = ValueEnum.STATE_INITIALS;

        String value = adapter.getItem(pos).toString();
        dataController.setValueAsString(key, value);
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
        // do nothing with this.  This method is necessary to satisfy interface.

      }
    });

    streetAddressEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          DataController.setSelectionOnView(v, ValueType.STRING);
        }     
      }
    });
    
    cityEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
      
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
          DataController.setSelectionOnView(v, ValueType.STRING);
        }     
      }
    });

  }

  @Override
  protected void onPause() {
    ValueEnum key = ValueEnum.STREET_ADDRESS;
    String value = streetAddressEditText.getText().toString();
    dataController.setValueAsString(key, value);
    
    key = ValueEnum.CITY;
    value = cityEditText.getText().toString();
    dataController.setValueAsString(key, value);
    
    super.onPause();
  }
  
  private void assignValuesToFields() {

    String sa = dataController.getValueAsString(ValueEnum.STREET_ADDRESS);
    streetAddressEditText.setText(sa);

    String city = dataController.getValueAsString(ValueEnum.CITY);
    cityEditText.setText(city);

    String stateInitials = 
        dataController.getValueAsString(ValueEnum.STATE_INITIALS);

    int statePosition = adapter.getPosition(stateInitials);
    stateSpinner.setSelection(statePosition);

  }

  
}
