package com.byronkatz;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

class AnalysisGraph extends View {


  public static final float GRAPH_MARGIN = 0.05f;
  public static final int GRAPH_MIN_X = 0;
  public static final int GRAPH_MIN_Y = 0;
  public static final float CIRCLE_RADIUS = 1.0f;

  private int graphMaxY;
  private int graphMaxX;
  private HashMap<Float, Float> graphMap;
  private GraphDataObject gdo;

  public AnalysisGraph(Context context, AttributeSet attrs) {
    super(context, attrs);
    //if we are not looking at the view in Eclipse...
    if (! isInEditMode()) {
      graphMap = new HashMap<Float, Float>();
      initView();
    }
  }

  public void setGraphDataObject(GraphDataObject graphDataObject) {
    this.gdo = graphDataObject;
  }
  
  private void initView() {
    setFocusable(true);
  }

  private void buildGraph() {
    double functionMinY = gdo.getMinFunctionValue();
    double functionMaxY = gdo.getMaxFunctionValue();
    double functionMinX = gdo.getMinYearValue();
    double functionMaxX = gdo.getMaxYearValue();
    double deltaGraphY = graphMaxY - GRAPH_MIN_Y;
    double deltaGraphX = graphMaxX - GRAPH_MIN_X;
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
    HashMap<Float, Float> dataPoints = gdo.getYearlyFunctionValue();

    for (HashMap.Entry<Float, Float> entry : dataPoints.entrySet()) {
      Float xValue = entry.getKey();
      Float yValue = entry.getValue();
      xGraphValue = (float) (marginWidthX +  xGraphCoefficient * (xValue - functionMinX));
      yGraphValue = (float) (marginWidthY + yGraphCoefficient * (functionMaxY - yValue));
      //load up the real values for graphing
      graphMap.put(xGraphValue, yGraphValue);
    }
  }

  public void onDraw(Canvas canvas) {
//    //if we are not looking at the view in Eclipse...
    if (! isInEditMode()) {

      graphMaxY = getMeasuredHeight();
      graphMaxX = getMeasuredWidth();
      Paint defaultPaint = new Paint();
      defaultPaint.setColor(Color.BLUE);
      defaultPaint.setStrokeWidth(5.0f);
      defaultPaint.setStyle(Paint.Style.STROKE);
      buildGraph();

      Rect graphFrameRect = new Rect(GRAPH_MIN_X,GRAPH_MIN_Y, 
          graphMaxX, graphMaxY);
      canvas.drawRect(graphFrameRect, defaultPaint);
      for (HashMap.Entry<Float, Float> entry : graphMap.entrySet()) {
        Float xValue = entry.getKey();
        Float yValue = entry.getValue();
        canvas.drawCircle(xValue, yValue, CIRCLE_RADIUS, defaultPaint);
      }
    }
  }


}