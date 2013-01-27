package com.byronkatz.reap.activity;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import com.byronkatz.reap.general.Utility;

public class MaxValueOnFocusChangeListener implements OnFocusChangeListener {
    private final GraphActivity ga;
    public MaxValueOnFocusChangeListener(GraphActivity ga) {
      this.ga = ga;
    }

    public void onFocusChange(View v, boolean hasFocus) {

      if (hasFocus) {
        Utility.setSelectionOnView(v, ga.currentSliderKey);
      } else if (! hasFocus)
		GraphActivityFunctions.parseAndDisplayMaxValue(ga, ((EditText)v));
    }

	
  }