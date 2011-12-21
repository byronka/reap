package com.byronkatz;

import android.content.Context;
import android.util.AttributeSet;

public class NPVGraph extends AnalysisGraph {

  private static final String KEY_VALUE = DatabaseAdapter.NET_PRESENT_VALUE;
  
  public NPVGraph(Context context, AttributeSet attrs) {
      super(context, attrs, "NPV Graph",KEY_VALUE);
  }


}