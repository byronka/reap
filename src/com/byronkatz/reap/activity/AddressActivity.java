package com.byronkatz.reap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.OnFocusChangeListenerWrapper;
import com.byronkatz.reap.general.OnFocusChangeListenerWrapperComments;
import com.byronkatz.reap.general.RealEstateAnalysisProcessorApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

public class AddressActivity extends Activity {

  private static final int STATE_ARRAY_SIZE = 50;
  String[] statePosition;
  private EditText streetAddressEditText;
  private EditText cityEditText;
  private EditText commentsEditText;
  private Spinner stateSpinner;
  private final DataController dataController = 
      RealEstateAnalysisProcessorApplication.getInstance().getDataController();
  private ArrayAdapter<CharSequence> adapter;

  
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
    setContentView(R.layout.address);

    statePosition = new String[STATE_ARRAY_SIZE];
    streetAddressEditText = (EditText) findViewById(R.id.streetAddressEditText);
    cityEditText = (EditText) findViewById(R.id.cityEditText);
    stateSpinner = (Spinner) findViewById(R.id.stateTitleSpinner);
    commentsEditText = (EditText) findViewById (R.id.commentsText);

    adapter = ArrayAdapter.createFromResource(this, R.array.states_array, 
        android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    stateSpinner.setAdapter(adapter);



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

    streetAddressEditText.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.STREET_ADDRESS));

    cityEditText.setOnFocusChangeListener(new OnFocusChangeListenerWrapper(ValueEnum.CITY));

    commentsEditText.setOnFocusChangeListener(new OnFocusChangeListenerWrapperComments(ValueEnum.COMMENTS));
  }
  

  private void saveValuesToCache() {
    ValueEnum streetAddressKey = ValueEnum.STREET_ADDRESS;
    String streetAddressValue = streetAddressEditText.getText().toString();

    ValueEnum cityKey = ValueEnum.CITY;
    String cityValue = cityEditText.getText().toString();

    ValueEnum commentsKey = ValueEnum.COMMENTS;
    String commentsValue = commentsEditText.getText().toString();

  
      dataController.setValueAsString(streetAddressKey, streetAddressValue);
      dataController.setValueAsString(cityKey, cityValue);
      dataController.setValueAsString(commentsKey, commentsValue);
  }


  @Override
  public void onPause() {
    saveValuesToCache();
    dataController.saveFieldValues();
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

    String comments = 
        dataController.getValueAsString(ValueEnum.COMMENTS);
    commentsEditText.setText(comments);

  }


}
