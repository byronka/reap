package com.byronkatz;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SaveCurrentValuesActivity extends Activity {

  Button saveButton;
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.save_current_values);

    saveButton = (Button) findViewById(R.id.saveButton);
    
    saveButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        dataController.saveValues();
        Toast toast = Toast.makeText(SaveCurrentValuesActivity.this, "Data saved", Toast.LENGTH_SHORT);
        toast.show();
      }
    });

  }

}
