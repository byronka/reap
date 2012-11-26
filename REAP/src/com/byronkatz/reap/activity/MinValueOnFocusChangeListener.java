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
  private final EditText mvet;

  public MinValueOnFocusChangeListener(GraphActivity ga, 
      EditText mvet) {
    this.ga = ga;
    this.mvet = mvet;
  }

  public void onFocusChange(View v, boolean hasFocus) {

    if (hasFocus) {
      Utility.setSelectionOnView(v, ga.currentSliderKey);
    } else if (! hasFocus)
		parseAndDisplayMinValue(ga, mvet);
  }

private void parseAndDisplayMinValue(GraphActivity ga, EditText mvet) {
	{

      Double tempMinValue = GraphActivityFunctions.parseEditText(
          mvet, ga.currentSliderKey);
      
      if (tempMinValue < ga.currentValueNumeric) {

        ga.minValueNumeric = tempMinValue;

        ga.deltaValueNumeric = GraphActivityFunctions.
            calculateMinMaxDelta(ga.minValueNumeric, ga.maxValueNumeric);
        
        double currentValueProgressDivisor = ga.
            currentValueNumeric - ga.minValueNumeric;
        
        double newProgress = (currentValueProgressDivisor / 
            ga.deltaValueNumeric) * GraphActivity.DIVISIONS_OF_VALUE_SLIDER;
        
        GraphActivityFunctions.displayValue(
            mvet, ga.minValueNumeric, ga.currentSliderKey);
        ga.valueSlider.setProgress(0);
        ga.valueSlider.setProgress((int) Math.round(newProgress));
      }  else {
        //set displayed value to what is in memory for min value
        GraphActivityFunctions.displayValue(mvet, ga.minValueNumeric, ga.currentSliderKey);
        
        Toast toast = Toast.makeText(ga, "new Minimum must be less than Current value: " + 
            ga.currentValueEditText.getText().toString(), Toast.LENGTH_LONG);
        toast.show();
      }

    }
}
}