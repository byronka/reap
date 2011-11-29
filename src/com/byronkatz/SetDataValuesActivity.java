package com.byronkatz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SetDataValuesActivity extends Activity {
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.set_data_values);
  }
  
  public void genericDefaultValues(View view) {
    //TODO: set up the values with generics
    Intent intent = new Intent(this, DataPagesActivity.class);
    startActivity(intent);   
  }
  
  public void loadSavedValuesFromDatabase(View view) {
    Intent intent = new Intent(this, LoadSavedValuesActivity.class);
    startActivity(intent);   
  }
  
  public void editValuesWithNoDefaultsSet(View view) {
    Intent intent = new Intent(this, DataPagesActivity.class);
    startActivity(intent);   
  }
  
  public void defaultValuesFromAddress(View view) {
    Intent intent = new Intent(this, AddressActivity.class);
    startActivity(intent);   
  }
}
