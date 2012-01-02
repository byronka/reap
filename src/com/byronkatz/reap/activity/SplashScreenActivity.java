package com.byronkatz.reap.activity;

import com.byronkatz.R;
import com.byronkatz.R.id;
import com.byronkatz.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SplashScreenActivity extends Activity {

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.splash_screen);
    
    Button splashScreenButton = new Button(this);
    splashScreenButton = (Button) findViewById(R.id.splashScreenButton);
    splashScreenButton.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(SplashScreenActivity.this, GraphActivity.class);
        startActivity(intent); 
        
      }
    });
    
  }
}
