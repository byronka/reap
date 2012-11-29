package com.byronkatz.reap.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byronkatz.reap.R;
import com.byronkatz.reap.customview.AnalysisGraph;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

public class GraphActivityFunctions {

  private static final double INCREASE_PERCENTAGE = 1.5d;
  private static final double DECREASE_PERCENTAGE = 0.5d;

  static void graphValuedEditorAction(GraphActivity ga, EditText v) {
		  switch (v.getId()) {
		  case R.id.minValueEditText:
			  parseAndDisplayMinValue(ga, v);
			  break;
		  case R.id.maxValueEditText:
			  parseAndDisplayMaxValue(ga, v);
			  break;
		  case R.id.currentValueEditText:
			  parseAndDisplayCurrentValue(ga);
			  break;
			  default:
				  Log.e("REAP", "Have gotten into the default case of onEditorAction, should not be possible");
		  }
	}
  
  static void parseAndDisplayMaxValue(GraphActivity ga, EditText mvet ) {
		{

			Double tempMaxValue = parseEditText(mvet, ga.currentSliderKey);
			if (tempMaxValue > ga.currentValueNumeric) {
				ga.maxValueNumeric = tempMaxValue;
				ga.deltaValueNumeric = calculateMinMaxDelta(ga.minValueNumeric,
						ga.maxValueNumeric);
				double currentValueProgressDivisor = ga.currentValueNumeric
						- ga.minValueNumeric;
				double newProgress = (currentValueProgressDivisor / ga.deltaValueNumeric)
						* GraphActivity.DIVISIONS_OF_VALUE_SLIDER;
				displayValue(mvet, ga.maxValueNumeric, ga.currentSliderKey);
				ga.valueSlider.setProgress(0);
				ga.valueSlider.setProgress((int) Math.round(newProgress));
			} else {
				// set displayed value to what is in memory for max value
				displayValue(mvet, ga.maxValueNumeric, ga.currentSliderKey);
				Toast toast = Toast.makeText(ga,
						"new Maximum must be greater than Current value: "
								+ ga.currentValueEditText.getText().toString(),
						Toast.LENGTH_LONG);
				toast.show();
			}
		}
	}
  
	static void parseAndDisplayMinValue(GraphActivity ga, EditText mvet) {
		{
			Double tempMinValue = parseEditText(mvet,
					ga.currentSliderKey);
			if (tempMinValue < ga.currentValueNumeric) {
				ga.minValueNumeric = tempMinValue;
				ga.deltaValueNumeric = calculateMinMaxDelta(ga.minValueNumeric, ga.maxValueNumeric);
				double currentValueProgressDivisor = ga.currentValueNumeric - ga.minValueNumeric;
				double newProgress = (currentValueProgressDivisor / ga.deltaValueNumeric)
						* GraphActivity.DIVISIONS_OF_VALUE_SLIDER;
				displayValue(mvet, ga.minValueNumeric, ga.currentSliderKey);
				ga.valueSlider.setProgress(0);
				ga.valueSlider.setProgress((int) Math.round(newProgress));
			} else {
				// set displayed value to what is in memory for min value
				displayValue(mvet, ga.minValueNumeric, ga.currentSliderKey);

				Toast toast = Toast.makeText(ga,
						"new Minimum must be less than Current value: "
								+ ga.currentValueEditText.getText().toString(),
						Toast.LENGTH_LONG);
				toast.show();
			}
		}
	}
  
  static void parseAndDisplayCurrentValue(GraphActivity ga) {
		{
			Double tempValueNumeric = parseEditText(ga.currentValueEditText,ga.currentSliderKey);
			ga.currentValueNumeric = tempValueNumeric;
			ga.recalcGraphPage(true);
			displayValue(ga.currentValueEditText,ga.currentValueNumeric,ga.currentSliderKey);
		}
	}
  
  /**
   * manually tells each AnalysisGraph object that it is invalid.  This will cause
   * it to redraw itself.
   * @param activity
   */
  static void invalidateGraphs(Activity activity) {

    AnalysisGraph graph;
    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.atcfFrameLayout);
    graph.invalidate();

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.npvFrameLayout);
    graph.invalidate();

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.aterFrameLayout);
    graph.invalidate();

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.mirrFrameLayout);
    graph.invalidate();

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.crpvFrameLayout);
    graph.invalidate();

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.crcvFrameLayout);
    graph.invalidate();
  }

  static void highlightCurrentYearOnGraph(Integer currentYearHighlight, Activity activity) {

    AnalysisGraph graph;

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.atcfFrameLayout);
    graph.setCurrentYearHighlighted(currentYearHighlight);

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.npvFrameLayout);
    graph.setCurrentYearHighlighted(currentYearHighlight);

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.aterFrameLayout);
    graph.setCurrentYearHighlighted(currentYearHighlight);

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.mirrFrameLayout);
    graph.setCurrentYearHighlighted(currentYearHighlight);

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.crpvFrameLayout);
    graph.setCurrentYearHighlighted(currentYearHighlight);

    graph = (com.byronkatz.reap.customview.AnalysisGraph) activity.findViewById(R.id.crcvFrameLayout);
    graph.setCurrentYearHighlighted(currentYearHighlight);
  }


  static Double calculateMaxFromCurrent(Double currentValueNumeric) {

    return currentValueNumeric * INCREASE_PERCENTAGE;

  }

  static Double calculateMinFromCurrent(Double currentValueNumeric) {

    return currentValueNumeric * DECREASE_PERCENTAGE;

  }

  static Double calculateCurrentFromMax(Double maxValueNumeric) {

    return maxValueNumeric / INCREASE_PERCENTAGE; 
  }

  static Double calculateCurrentFromMin(Double minValueNumeric) {
    return minValueNumeric / DECREASE_PERCENTAGE;
  }

  static Double calculateMinMaxDelta(Double minValueNumeric, Double maxValueNumeric) {
    return maxValueNumeric - minValueNumeric;
  }


  static Double parseEditText(EditText editText, ValueEnum currentSliderKey) {
    Double returnValue = 0.0d;

    try {
    switch (currentSliderKey.getType()) {
    case CURRENCY:
      returnValue = Utility.parseCurrency(editText.getText().toString());
      break;
    case PERCENTAGE:
      returnValue = Utility.parsePercentage(editText.getText().toString());
      break;
    case INTEGER:
      returnValue = Double.valueOf(editText.getText().toString());
    default:
      break;
    }
    } catch (Exception e) {
      returnValue = 0.0d;
    }

    return returnValue;
  }

  /**
   * Takes the value of the (min | max | current)ValueNumeric, parses it correctly
   * by its type of value, and sets that as a string into the edittext
   * @param editText the EditText in question
   * @param valueNumeric the Double value to parse and set as a string
   * @param currentSliderKey the type of value (see ValueEnum for types)
   */
  static void displayValue(EditText editText, Double valueNumeric, 
      ValueEnum currentSliderKey) {

    switch (currentSliderKey.getType()) {
    case CURRENCY:

      editText.setText(Utility.displayCurrency(valueNumeric));
      break;
    case PERCENTAGE:

      editText.setText(Utility.displayPercentage(valueNumeric));

      break;
    case INTEGER:

      editText.setText(String.valueOf((int) Math.floor(valueNumeric)));
      break;
    default:
      //do nothing
      break;  
    }
  }

  
  static Integer updateTimeSliderAfterChange(SeekBar timeSlider, Integer currentYearMaximum) {

    //set the new max value on the progress bar
    Integer oldCurrentValue = timeSlider.getProgress();

    timeSlider.setMax(currentYearMaximum);
    Integer newValue = 0;

    //here we decide - do we need to change the currentYearSelected?  only if the new max
    //is less than the old currentYearSelected
    if (currentYearMaximum < oldCurrentValue) {
      newValue = currentYearMaximum;
    } else {
      newValue = oldCurrentValue;
    }

    //necessary to setprogress twice here.  Bug in Android code.
    timeSlider.setProgress(0);
    timeSlider.setProgress(newValue);
    
    return newValue;
  }

}
