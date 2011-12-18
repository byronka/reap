package com.byronkatz;

import android.content.Context;
import android.util.AttributeSet;

public class ATCFGraph extends AnalysisGraph {

  
  public ATCFGraph(Context context) {
    super(context);
    setGraphDataObject(new CalculatedVariables().getAtcfGraphDataObject());
  }


  public ATCFGraph(Context context, AttributeSet attrs) {
    super(context, attrs);
    setGraphDataObject(new CalculatedVariables().getAtcfGraphDataObject());
  }

  public ATCFGraph(Context context, AttributeSet ats, int defaultStyle) {
    super(context, ats, defaultStyle);
    setGraphDataObject(new CalculatedVariables().getAtcfGraphDataObject());
  }

}