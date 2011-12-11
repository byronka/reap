package com.byronkatz;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

public class GraphActivity extends Activity {

  private static final float GRAPH_MIN_X = 0.0f;
  private static final float GRAPH_MAX_X = 40.0f;
  private static final float GRAPH_MIN_Y = 0.0f;
  private static final float GRAPH_MAX_Y = 40.0f;
  private static final float GRAPH_MARGIN = 0.05f;

  private CalculationObject calculationObject;
  private GraphDataObject gdo;
  private HashMap<Float, Float> netPresentValueGraphMap;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.graph);
    calculationObject = new CalculationObject();
    gdo = calculationObject.getGdo();
  }
  
  public void buildNPVGraph() {
    double functionMinY = gdo.getMinNPV();
    double functionMaxY = gdo.getMaxNPV();
    double functionMinX = gdo.getMinNPVYearNumber();
    double functionMaxX = gdo.getMaxNPVYearNumber();
    double deltaGraphY = GRAPH_MAX_Y - GRAPH_MIN_Y;
    double deltaGraphX = GRAPH_MAX_X - GRAPH_MIN_X;
    double deltaFunctionY = functionMaxY - functionMinY;
    double deltaFunctionX = functionMaxX - functionMinX;
    double marginWidthX = deltaGraphX * GRAPH_MARGIN;
    double marginWidthY = deltaGraphY * GRAPH_MARGIN;
    //margin on left and right
    double twiceXMargin = marginWidthX * 2;
    //for margin on top and bottom
    double twiceYMargin = marginWidthY * 2;
    double betweenMarginsOnX = deltaGraphX - twiceXMargin;
    double betweenMarginsOnY = deltaGraphY - twiceYMargin;
    double xGraphCoefficient = betweenMarginsOnX / deltaFunctionX;
    double yGraphCoefficient = betweenMarginsOnY / deltaFunctionY;
    
    float xGraphValue = 0.0f;
    float yGraphValue = 0.0f;
    HashMap<Float, Float> npvDataPoints = gdo.getYearlyNPVWithAter();

    for (HashMap.Entry<Float, Float> entry : npvDataPoints.entrySet()) {
      Float xValue = entry.getKey();
      Float yValue = entry.getValue();
      xGraphValue = (float) (marginWidthX +  xGraphCoefficient * (xValue - functionMinX));
      yGraphValue = (float) (marginWidthY + yGraphCoefficient * (functionMaxY - yValue));
      //load up the real values for graphing
      netPresentValueGraphMap.put(xGraphValue, yGraphValue);
    }
  }
  
  public void buildAterGraph() {
    
  }
  
  public void buildAtcfGraph() {
    
  }
  
  class NPVGraph extends View {

    public NPVGraph(Context context) {
      super(context);
      // TODO Auto-generated constructor stub
    }

    public void onDraw(Canvas canvas) {
      canvas.drawRect(MINIMUM_X + 2, top, MAXIMUM_X + 2, bottom, new Paint());
      for (int year = MINIMUM_X; year < MAXIMUM_X; year++) {
        canvas.drawCircle((float) year, npv, CIRCLE_RADIUS, new Paint());
      }
      
    }
    
    
  }
}
