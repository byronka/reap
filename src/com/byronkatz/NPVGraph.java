package com.byronkatz;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

class NPVGraph extends View {


  public static final float GRAPH_MARGIN = 0.05f;
  public static final int GRAPH_MIN_X = 0;
  public static final int GRAPH_MIN_Y = 0;
  public static final float CIRCLE_RADIUS = 1.0f;

  private int graphMaxY;
  private int graphMaxX;
  private CalculationObject calculationObject;
  private HashMap<Float, Float> netPresentValueGraphMap;
  private GraphDataObject gdo;

  public NPVGraph(Context context) {
    super(context);
    //if we are not looking at the view in Eclipse...
    if (! isInEditMode()) {
      initView();
    }
  }

  public NPVGraph(Context context, AttributeSet attrs) {
    super(context, attrs);
    //if we are not looking at the view in Eclipse...
    if (! isInEditMode()) {
      initView();
    }
  }

  public NPVGraph(Context context, 
      AttributeSet ats, 
      int defaultStyle) {
    super(context, ats, defaultStyle);
    //if we are not looking at the view in Eclipse...
    if (! isInEditMode()) {
      initView();
    }
  }

  private void initView() {
    setFocusable(true);

    calculationObject = new CalculationObject();
    netPresentValueGraphMap = new HashMap<Float, Float>();
    gdo = calculationObject.getGdo();
  }

  private void buildNPVGraph() {
    double functionMinY = gdo.getMinNPV();
    double functionMaxY = gdo.getMaxNPV();
    double functionMinX = gdo.getMinNPVYearNumber();
    double functionMaxX = gdo.getMaxNPVYearNumber();
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

  public void onDraw(Canvas canvas) {
//    //if we are not looking at the view in Eclipse...
    if (! isInEditMode()) {

      graphMaxY = getMeasuredHeight();
      graphMaxX = getMeasuredWidth();
      Paint defaultPaint = new Paint();
      defaultPaint.setColor(Color.BLUE);
      defaultPaint.setStrokeWidth(5.0f);
      defaultPaint.setStyle(Paint.Style.STROKE);
      buildNPVGraph();

      Rect graphFrameRect = new Rect(GRAPH_MIN_X,GRAPH_MIN_Y, 
          graphMaxX, graphMaxY);
      canvas.drawRect(graphFrameRect, defaultPaint);
      for (HashMap.Entry<Float, Float> entry : netPresentValueGraphMap.entrySet()) {
        Float xValue = entry.getKey();
        Float yValue = entry.getValue();
        canvas.drawCircle(xValue, yValue, CIRCLE_RADIUS, defaultPaint);
      }
    }
  }


}