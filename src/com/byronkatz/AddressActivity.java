package com.byronkatz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddressActivity extends Activity {

  private static final int STATE_ARRAY_SIZE = 50;
  String[] statePosition;
  private Button useGPS;
  private EditText streetAddressEditText;
  private EditText cityEditText;
  private Spinner stateSpinner;
  private Button backButton;
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  private ArrayAdapter<CharSequence> adapter;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.address);

    statePosition = new String[STATE_ARRAY_SIZE];
    useGPS = (Button) findViewById(R.id.useGPS);
    streetAddressEditText = (EditText) findViewById(R.id.streetAddressEditText);
    cityEditText = (EditText) findViewById(R.id.cityEditText);
    stateSpinner = (Spinner) findViewById(R.id.stateTitleSpinner);
    backButton = (Button) findViewById(R.id.backButton);

    adapter = ArrayAdapter.createFromResource(this, R.array.states_array, 
        android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    stateSpinner.setAdapter(adapter);

    assignValuesToFields();

    useGPS.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(LOCATION_SERVICE);
        //TODO - this is probably not right at all.  How to do activity for result?
        startActivityForResult(intent, MODE_PRIVATE);
      }
    });

    streetAddressEditText.setOnKeyListener(new OnKeyListener() {

      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.STREET_ADDRESS;
        String value = streetAddressEditText.getText().toString();
        dataController.setValue(key, value);
        return false;
      }
    });

    cityEditText.setOnKeyListener(new OnKeyListener() {

      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = DatabaseAdapter.CITY;
        String value = cityEditText.getText().toString();
        dataController.setValue(key, value);
        return false;
      }
    });

    stateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
          long arg3) {
        String key = DatabaseAdapter.STATE_INITIALS;
        
        String value = adapter.getItem(pos).toString();
        dataController.setValue(key, value);
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
        // do nothing with this.  This method is necessary to satisfy interface.

      }
    });

    backButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        finish();
      }
    });

  }

  private void assignValuesToFields() {

    String sa = dataController.getValue(DatabaseAdapter.STREET_ADDRESS);
    streetAddressEditText.setText(sa);

    String city = dataController.getValue(DatabaseAdapter.CITY);
    cityEditText.setText(city);

    String stateInitials = 
        dataController.getValue(DatabaseAdapter.STATE_INITIALS);

    int statePosition = adapter.getPosition(stateInitials);
    stateSpinner.setSelection(statePosition);

  }
}
