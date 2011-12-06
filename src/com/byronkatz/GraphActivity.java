package com.byronkatz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

public class GraphActivity extends Activity {

  private static final int MINIMUM_X = 0;
  private static final int MAXIMUM_X = 30;
  private static final float CIRCLE_RADIUS = 0.20f;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.graph);
  }
  
  public void buildGraph() {
    Double[] netPresentValue;
    netPresentValue = CalculationObject.getNPVArray(30, CalculationObject.YEARLY);
    double minY = netPresentValue.getLowestValue();
    double maxY = netPresentValue.getGreatestValue();

  }
  
  public void plotValue(float x, float y) {
    Canvas canvas = new Canvas();
    canvas.drawCircle(x, y, CIRCLE_RADIUS, new Paint());
  }
  
  class NPVGraph extends View {

    public NPVGraph(Context context) {
      super(context);
      // TODO Auto-generated constructor stub
    }

    public void onDraw(Canvas canvas) {
      for (int year = MINIMUM_X; year < MAXIMUM_X; year++) {
        canvas.drawCircle((float) year, npv, CIRCLE_RADIUS, new Paint());
      }
      
    }
    
    
  }
}
