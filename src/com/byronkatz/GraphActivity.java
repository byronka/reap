package com.byronkatz;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

public class GraphActivity extends Activity {

  private static final int MINIMUM_X = 0;
  private static final int MAXIMUM_X = 30;
  private static final float CIRCLE_RADIUS = 0.20f;
  private CalculationObject calculationObject;
  private GraphDataObject gdo;
  private ArrayList<Double> netPresentValue;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.graph);
    calculationObject = new CalculationObject();
    gdo = calculationObject.getGdo();
  }
  
  public void buildGraph() {
    netPresentValue = gdo.getYearlyNPVWithAter();
    double minY = gdo.getMinNPV();
    double maxY = gdo.getMaxNPV();

  }
  
  class NPVGraph extends View {

    public NPVGraph(Context context) {
      super(context);
      // TODO Auto-generated constructor stub
    }

    public void onDraw(Canvas canvas) {
      canvas.drawRect(left, top, right, bottom, paint);
      for (int year = MINIMUM_X; year < MAXIMUM_X; year++) {
        canvas.drawCircle((float) year, npv, CIRCLE_RADIUS, new Paint());
      }
      
    }
    
    
  }
}
