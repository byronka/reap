package com.byronkatz.reap.activity;

import com.byronkatz.reap.R;

import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ValueEditTextOnEditorActionListener implements
    OnEditorActionListener {
  private final GraphActivity ga;
  ValueEditTextOnEditorActionListener(GraphActivity ga) {
    this.ga = ga;
  }

  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	  GraphActivityFunctions.graphValuedEditorAction(ga, ((EditText)v));
    return false;
  }


}