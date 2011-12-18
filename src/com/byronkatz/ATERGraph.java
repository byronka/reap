package com.byronkatz;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class ATERGraph extends AnalysisGraph {

  
//  public ATERGraph(Context context) {
//    super(context);
//    setGraphDataObject(new CalculatedVariables().getAterGraphDataObject());
//  }


  public ATERGraph(Context context, AttributeSet attrs) {
    super(context, attrs);
    CalculatedVariables cv = new CalculatedVariables();
    cv.crunchCalculation();
    setGraphDataObject(cv.getAterGraphDataObject());
    }

//  public ATERGraph(Context context, AttributeSet ats, int defaultStyle) {
//    super(context, ats, defaultStyle);
//    setGraphDataObject(new CalculatedVariables().getAterGraphDataObject());
//  }
  

}