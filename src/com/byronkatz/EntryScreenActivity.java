package com.byronkatz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class EntryScreenActivity extends Activity {

  private Button setDataValues;
  private Button viewGraph;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.entry_screen);
    
    setDataValues = (Button) findViewById(R.id.setDataValuesButton);
    viewGraph     = (Button) findViewById(R.id.viewGraphButton);
    
    setDataValues.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent("SetDataValuesActivity");
        startActivity(intent);   
      }
    });
    
    viewGraph.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        Intent intent = new Intent();
        startActivity(intent);
        
      }
    });
  }

    
}