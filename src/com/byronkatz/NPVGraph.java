package com.byronkatz;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class NPVGraph extends AnalysisGraph {

  public NPVGraph(Context context, AttributeSet attrs) {
    super(context, attrs, AnalysisGraph.NPV_GRAPH);
  }


}