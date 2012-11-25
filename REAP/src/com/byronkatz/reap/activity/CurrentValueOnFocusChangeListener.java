package com.byronkatz.reap.activity;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Toast;

import com.byronkatz.reap.general.Utility;

public class CurrentValueOnFocusChangeListener implements
    OnFocusChangeListener {
  private final GraphActivity ga;
  CurrentValueOnFocusChangeListener(GraphActivity graphActivity) {
    this.ga = graphActivity;
  }

	public void onFocusChange(View v, boolean hasFocus) {

		if (hasFocus) {
			Utility.setSelectionOnView(v, ga.currentSliderKey);
		} else if (!hasFocus) {
			Double tempValueNumeric = GraphActivityFunctions.parseEditText(
					ga.currentValueEditText,
					ga.currentSliderKey);
			ga.currentValueNumeric = tempValueNumeric;
			ga.recalcGraphPage();
			GraphActivityFunctions.displayValue(
					ga.currentValueEditText,
					ga.currentValueNumeric,
					ga.currentSliderKey);
		}
	}
}