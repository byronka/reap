package com.byronkatz;

import android.content.Context;
import android.util.AttributeSet;

public class ATERGraph extends AnalysisGraph {
  
  private static final String KEY_VALUE = DatabaseAdapter.AFTER_TAX_EQUITY_REVERSION;
  
  public ATERGraph(Context context, AttributeSet attrs) {
      super(context, attrs, "ATER Graph",KEY_VALUE);
  }

}