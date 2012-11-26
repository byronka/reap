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
    private final GraphActivity ga;
    private final EditText mvet;

    public MaxValueOnFocusChangeListener(GraphActivity ga, 
        EditText mvet) {
      this.ga = ga;
      this.mvet = mvet;
    }

    public void onFocusChange(View v, boolean hasFocus) {

      if (hasFocus) {
        Utility.setSelectionOnView(v, ga.currentSliderKey);
      } else if (! hasFocus)
		parseAndDisplayMaxValue();
    }

	private void parseAndDisplayMaxValue() {
		{

		    Double tempMaxValue = GraphActivityFunctions.parseEditText(
		        mvet, ga.currentSliderKey);

		    if (tempMaxValue > ga.currentValueNumeric) {
		      
		      ga.maxValueNumeric = tempMaxValue;

		      ga.deltaValueNumeric = GraphActivityFunctions.
		          calculateMinMaxDelta(ga.minValueNumeric, ga.maxValueNumeric);
		    
		      
		      double currentValueProgressDivisor = ga.
		          currentValueNumeric - ga.minValueNumeric;
		      
		      double newProgress = (currentValueProgressDivisor / 
		          ga.deltaValueNumeric) * GraphActivity.DIVISIONS_OF_VALUE_SLIDER;
		      
		      GraphActivityFunctions.displayValue(
		          mvet, ga.maxValueNumeric, ga.currentSliderKey);
		   
		      ga.valueSlider.setProgress(0);
		      ga.valueSlider.setProgress((int) Math.round(newProgress));
		    
		    } else {
		      //set displayed value to what is in memory for max value
		      GraphActivityFunctions.displayValue(mvet, ga.maxValueNumeric, ga.currentSliderKey);
		      
		      Toast toast = Toast.makeText(
		          ga, "new Maximum must be greater than Current value: " + ga.currentValueEditText.getText().toString(), Toast.LENGTH_LONG);
		      toast.show();
		    }
		  }
	}
  }