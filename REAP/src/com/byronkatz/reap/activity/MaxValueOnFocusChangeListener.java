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

    public MaxValueOnFocusChangeListener(GraphActivity graphActivity, 
        EditText maxValueEditText) {
      this.graphActivity = graphActivity;
      this.maxValueEditText = maxValueEditText;
    }

    public void onFocusChange(View v, boolean hasFocus) {

      if (hasFocus) {
        Utility.setSelectionOnView(v, this.graphActivity.currentSliderKey);
      } else if (! hasFocus) {

        Double tempMaxValue = GraphActivityFunctions.parseEditText(
            maxValueEditText, this.graphActivity.currentSliderKey);

        if (tempMaxValue > this.graphActivity.currentValueNumeric) {
          
          this.graphActivity.maxValueNumeric = tempMaxValue;

          this.graphActivity.deltaValueNumeric = GraphActivityFunctions.
              calculateMinMaxDelta(this.graphActivity.minValueNumeric, this.graphActivity.maxValueNumeric);
        
          
          double currentValueProgressDivisor = this.graphActivity.
              currentValueNumeric - this.graphActivity.minValueNumeric;
          
          double newProgress = (currentValueProgressDivisor / 
              this.graphActivity.deltaValueNumeric) * GraphActivity.DIVISIONS_OF_VALUE_SLIDER;
          
          GraphActivityFunctions.displayValue(
              maxValueEditText, this.graphActivity.maxValueNumeric, this.graphActivity.currentSliderKey);
       
          this.graphActivity.valueSlider.setProgress(0);
          this.graphActivity.valueSlider.setProgress((int) Math.round(newProgress));
        
        } else {
          //set displayed value to what is in memory for max value
          GraphActivityFunctions.displayValue(maxValueEditText, this.graphActivity.maxValueNumeric, this.graphActivity.currentSliderKey);
          
          Toast toast = Toast.makeText(
              this.graphActivity, "new Maximum must be greater than Current value: " + this.graphActivity.currentValueEditText.getText().toString(), Toast.LENGTH_LONG);
          toast.show();
        }
      }
    }
  }