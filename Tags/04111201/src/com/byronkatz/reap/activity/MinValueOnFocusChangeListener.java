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
  private final GraphActivity graphActivity;
  private final EditText minValueEditText;

  public MinValueOnFocusChangeListener(GraphActivity graphActivity, 
      EditText minValueEditText) {
    this.graphActivity = graphActivity;
    this.minValueEditText = minValueEditText;
  }

  @Override
  public void onFocusChange(View v, boolean hasFocus) {

    if (hasFocus) {
      Utility.setSelectionOnView(v, this.graphActivity.currentSliderKey);
    } else if (! hasFocus) {

      Double tempMinValue = GraphActivityFunctions.parseEditText(
          minValueEditText, this.graphActivity.currentSliderKey);
      
      if (tempMinValue < this.graphActivity.currentValueNumeric) {

        this.graphActivity.minValueNumeric = tempMinValue;

        this.graphActivity.deltaValueNumeric = GraphActivityFunctions.
            calculateMinMaxDelta(this.graphActivity.minValueNumeric, this.graphActivity.maxValueNumeric);
        
        double currentValueProgressDivisor = this.graphActivity.
            currentValueNumeric - this.graphActivity.minValueNumeric;
        
        double newProgress = (currentValueProgressDivisor / 
            this.graphActivity.deltaValueNumeric) * GraphActivity.DIVISIONS_OF_VALUE_SLIDER;
        
        GraphActivityFunctions.displayValue(
            minValueEditText, this.graphActivity.minValueNumeric, this.graphActivity.currentSliderKey);
        this.graphActivity.valueSlider.setProgress(0);
        this.graphActivity.valueSlider.setProgress((int) Math.round(newProgress));
      }  else {
        //set displayed value to what is in memory for min value
        GraphActivityFunctions.displayValue(minValueEditText, this.graphActivity.minValueNumeric, this.graphActivity.currentSliderKey);
        
        Toast toast = Toast.makeText(this.graphActivity, "new Minimum must be less than Current value: " + 
            this.graphActivity.currentValueEditText.getText().toString(), Toast.LENGTH_LONG);
        toast.show();
      }

    }
  }
}