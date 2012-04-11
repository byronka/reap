package com.byronkatz.reap.general;

import android.view.View;
import android.view.View.OnFocusChangeListener;

public class OnFocusChangeListenerWrapper implements OnFocusChangeListener {

  private ValueEnum ve;

  public OnFocusChangeListenerWrapper(ValueEnum ve) {
    this.ve = ve; 
  }

  @Override
  public void onFocusChange(View v, boolean hasFocus) {
    if (hasFocus) {
      Utility.setSelectionOnView(v, ve);
    } else if (!hasFocus) {
      Utility.parseThenDisplayValue(v, ve);
    } 

  }

}
