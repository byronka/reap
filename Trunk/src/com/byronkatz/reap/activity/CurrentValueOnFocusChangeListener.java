package com.byronkatz.reap.activity;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Toast;

import com.byronkatz.reap.general.Utility;

public class CurrentValueOnFocusChangeListener implements
    OnFocusChangeListener {
  /**
   * 
   */
  private final GraphActivity graphActivity;

  /**
   * @param graphActivity
   */
  CurrentValueOnFocusChangeListener(GraphActivity graphActivity) {
    this.graphActivity = graphActivity;
  }

  @Override
  public void onFocusChange(View v, boolean hasFocus) {

    if (hasFocus) {
      Utility.setSelectionOnView(v, graphActivity.currentSliderKey);

    } else if (! hasFocus) {

      Double tempValueNumeric = GraphActivityFunctions.parseEditText(
          graphActivity.currentValueEditText, graphActivity.currentSliderKey);
 
        graphActivity.currentValueNumeric = tempValueNumeric;
        graphActivity.recalcGraphPage();

      GraphActivityFunctions.displayValue(
          graphActivity.currentValueEditText, graphActivity.currentValueNumeric, graphActivity.currentSliderKey);

    }
  }
}