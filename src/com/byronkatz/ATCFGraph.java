package com.byronkatz;

import android.content.Context;
import android.util.AttributeSet;


public class ATCFGraph extends AnalysisGraph {

  private static final String KEY_VALUE = DatabaseAdapter.AFTER_TAX_CASH_FLOW;
  
  public ATCFGraph(Context context, AttributeSet attrs) {
      super(context, attrs, "ATCF Graph",KEY_VALUE);
  }


}