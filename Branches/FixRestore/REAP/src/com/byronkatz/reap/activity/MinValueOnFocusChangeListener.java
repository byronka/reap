package com.byronkatz.reap.activity;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.byronkatz.reap.general.Utility;

public class MinValueOnFocusChangeListener implements OnFocusChangeListener {
  /**
   * 
   */
  private final GraphActivity ga;

  public MinValueOnFocusChangeListener(GraphActivity ga) {
    this.ga = ga;
  }

  public void onFocusChange(View v, boolean hasFocus) {

    if (hasFocus) {
      Utility.setSelectionOnView(v, ga.currentSliderKey);
    } else if (! hasFocus)
		GraphActivityFunctions.parseAndDisplayMinValue(ga, ((EditText)v));
  }


}