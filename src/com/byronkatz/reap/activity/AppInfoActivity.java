package com.byronkatz.reap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.byronkatz.reap.R;

public class AppInfoActivity extends Activity {


  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.info);
  }

  public void sendEmailToDeveloperMethod(View v) {

    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    
    emailIntent.setType("plain/text");
    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"renaissance.nomad@gmail.com"});

    startActivity(emailIntent);

  }

}
