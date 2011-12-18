package com.byronkatz;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class ATCFGraph extends AnalysisGraph {

  


  public ATCFGraph(Context context, AttributeSet attrs) {
    super(context, attrs);
    CalculatedVariables cv = new CalculatedVariables();
    cv.crunchCalculation();
    setGraphDataObject(cv.getAtcfGraphDataObject());
    }

  
}