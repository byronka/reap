package com.byronkatz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LoadSavedValuesActivity extends Activity {

  private Button browseTheSavedData;
  private Button searchTheDatabase;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.load_saved_values);
    
    browseTheSavedData = (Button) findViewById(R.id.browseDatabaseButton);
    searchTheDatabase  = (Button) findViewById(R.id.searchDatabaseButton);
    
    browseTheSavedData.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        //TODO - fill in the action below
        Intent intent = new Intent("what is the action here??");
        startActivity(intent);
        
      }
    });
    
    searchTheDatabase.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
      //TODO - fill in the action below
        Intent intent = new Intent("what is the action here??");
        startActivity(intent);
        
      }
    });
  }
}
