package com.byronkatz.reap.activity;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.byronkatz.reap.general.Utility;

public class MinValueOnFocusChangeListener implements OnFocusChangeListener {
  /**
   * 
   */
  private final GraphActivity graphActivity;
  private final EditText minValueEditText;

  public MinValueOnFocusChangeListener(GraphActivity graphActivity, EditText minValueEditText) {
    this.graphActivity = graphActivity;
    this.minValueEditText = minValueEditText;
  }

  @Override
  public void onFocusChange(View v, boolean hasFocus) {

    if (hasFocus) {
      Utility.setSelectionOnView(v, this.graphActivity.currentSliderKey);
    } else if (! hasFocus) {

      Double tempMinValue = GraphActivityFunctions.parseEditText(minValueEditText, this.graphActivity.currentSliderKey);
      if (tempMinValue < this.graphActivity.currentValueNumeric) {

        this.graphActivity.minValueNumeric = tempMinValue;
        this.graphActivity.deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(this.graphActivity.minValueNumeric, this.graphActivity.maxValueNumeric);
        GraphActivityFunctions.displayValue(minValueEditText, this.graphActivity.minValueNumeric, this.graphActivity.currentSliderKey);
      }  else {
        Toast toast = Toast.makeText(this.graphActivity, "new Minimum must be less than Current value: " + this.graphActivity.currentValueEditText.getText().toString(), Toast.LENGTH_LONG);
        toast.show();
      }

      GraphActivityFunctions.displayValue(minValueEditText, this.graphActivity.minValueNumeric, this.graphActivity.currentSliderKey);

    }
  }
}