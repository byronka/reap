package com.byronkatz.reap.activity;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.byronkatz.reap.general.Utility;

public class MaxValueOnFocusChangeListener implements OnFocusChangeListener {
    /**
     * 
     */
    private final GraphActivity graphActivity;
    private final EditText maxValueEditText;

    public MaxValueOnFocusChangeListener(GraphActivity graphActivity, EditText maxValueEditText) {
      this.graphActivity = graphActivity;
      this.maxValueEditText = maxValueEditText;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

      if (hasFocus) {
        Utility.setSelectionOnView(v, this.graphActivity.currentSliderKey);
      } else if (! hasFocus) {

        Double tempMaxValue = GraphActivityFunctions.parseEditText(maxValueEditText, this.graphActivity.currentSliderKey);

        if (tempMaxValue.equals(this.graphActivity.maxValueNumeric)) {
          Toast toast = Toast.makeText(this.graphActivity, "You entered the same value as already existed for Maximum", Toast.LENGTH_LONG);
          toast.show();
        } else if (tempMaxValue > this.graphActivity.currentValueNumeric) {
          //            Log.d("Tag 001", "currentValueNumeric is " + currentValueNumeric);
          this.graphActivity.maxValueNumeric = tempMaxValue;

          this.graphActivity.deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(this.graphActivity.minValueNumeric, this.graphActivity.maxValueNumeric);
          //          Log.d("", "deltaValueNumeric is " + deltaValueNumeric);
          GraphActivityFunctions.displayValue(maxValueEditText, this.graphActivity.maxValueNumeric, this.graphActivity.currentSliderKey);
//            executeCalculationBackgroundTask();
        } else {
          Toast toast = Toast.makeText(this.graphActivity, "new Maximum must be greater than Current value: " + this.graphActivity.currentValueEditText.getText().toString(), Toast.LENGTH_LONG);
          toast.show();
        }


        GraphActivityFunctions.displayValue(maxValueEditText, this.graphActivity.maxValueNumeric, this.graphActivity.currentSliderKey);
      }
    }
  }