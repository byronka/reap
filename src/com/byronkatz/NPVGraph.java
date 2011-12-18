package com.byronkatz;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class NPVGraph extends AnalysisGraph {

  
//  public NPVGraph(Context context) {
//    super(context);
//    setGraphDataObject(new CalculatedVariables().getNpvGraphDataObject());
//  }


  public NPVGraph(Context context, AttributeSet attrs) {
    super(context, attrs);
    CalculatedVariables cv = new CalculatedVariables();
    cv.crunchCalculation();
    setGraphDataObject(cv.getNpvGraphDataObject());
  }

//  public NPVGraph(Context context, AttributeSet ats, int defaultStyle) {
//    super(context, ats, defaultStyle);
//    setGraphDataObject(new CalculatedVariables().getNpvGraphDataObject());
//  }

}