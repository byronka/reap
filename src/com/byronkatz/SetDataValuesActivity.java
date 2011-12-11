package com.byronkatz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SetDataValuesActivity extends Activity {

  Button genericDefaultValues;
  Button loadSavedValuesFromDatabase;
  Button editValuesWithNoDefaultsSet;
  Button defaultValuesFromAddress;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.set_data_values);

    genericDefaultValues        = (Button) findViewById(R.id.genericDefaultValues);
    loadSavedValuesFromDatabase = (Button) findViewById(R.id.loadSavedValuesButton);
    editValuesWithNoDefaultsSet = (Button) findViewById(R.id.editWithoutDefaultValuesButton);
    defaultValuesFromAddress    = (Button) findViewById(R.id.defaultValuesFromAddress);

    genericDefaultValues.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(SetDataValuesActivity.this, DataPagesActivity.class);
        startActivity(intent); 
      }
    });

    loadSavedValuesFromDatabase.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(SetDataValuesActivity.this, LoadSavedValuesActivity.class);
        startActivity(intent); 
      }
    });

    editValuesWithNoDefaultsSet.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(SetDataValuesActivity.this, DataPagesActivity.class);
        startActivity(intent); 
      }
    });

    defaultValuesFromAddress.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(SetDataValuesActivity.this, AddressActivity.class);
        startActivity(intent); 
      }
    });


  }

}
