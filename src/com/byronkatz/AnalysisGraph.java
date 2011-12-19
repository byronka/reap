package com.byronkatz;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

class AnalysisGraph extends View {


  public static final float GRAPH_MARGIN = 0.20f;
  public static final int GRAPH_MIN_X = 0;
  public static final int GRAPH_MIN_Y = 0;
  public static final float CIRCLE_RADIUS = 3.0f;
  public static final float TEXT_SIZE = 10.0f;
  public static final float DEFAULT_GRAPH_STROKE_WIDTH = 1.5f;
  public static final String X_AXIS_STRING = "0";

  public static final int NPV_GRAPH = 1;
  public static final int ATCF_GRAPH = 2;
  public static final int ATER_GRAPH = 3;

  private int graphMaxY;
  private int graphMaxX;
  private HashMap<Float, Float> graphMap;
  private GraphDataObject gdo;
  private int graphType;

  public AnalysisGraph(Context context, AttributeSet attrs, int graphType) {
    super(context, attrs);
    graphMap = new HashMap<Float, Float>();
    this.graphType = graphType;
    initView();
  }

  private void initView() {
    setFocusable(true);
  }

  public void onDraw(Canvas canvas) {
    
    graphMaxY = getMeasuredHeight();
    graphMaxX = getMeasuredWidth();
    
    //crunch the numbers
    CalculatedVariables calculatedVariables = new CalculatedVariables();
    calculatedVariables.crunchCalculation();
    switch (graphType) {
    case NPV_GRAPH:
      this.gdo = calculatedVariables.getNpvGraphDataObject();
      break;
    case ATER_GRAPH:
      this.gdo = calculatedVariables.getAterGraphDataObject();
      break;
    case ATCF_GRAPH:
      this.gdo = calculatedVariables.getAtcfGraphDataObject();
      break;
    default:
      System.err.println("You should not get to the default case statement" + 
          " in the AnalysisGraph class, yet you have");
        break;
    }
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
    
    //set up defaults for the drawing - canvas size, paint color, stroke width.
    Paint defaultPaint = new Paint();
    defaultPaint.setColor(Color.BLUE);
    defaultPaint.setStrokeWidth(DEFAULT_GRAPH_STROKE_WIDTH);
    defaultPaint.setStyle(Paint.Style.STROKE);

    //draw the frame
    Rect graphFrameRect = new Rect(GRAPH_MIN_X,GRAPH_MIN_Y, 
        graphMaxX, graphMaxY);
    canvas.drawRect(graphFrameRect, defaultPaint);
    
    //draw the points
    for (HashMap.Entry<Float, Float> entry : graphMap.entrySet()) {
      Float xValue = entry.getKey();
      Float yValue = entry.getValue();
      canvas.drawCircle(xValue, yValue, CIRCLE_RADIUS, defaultPaint);
    }
    
    //draw the 0 X-axis if the graph passes it.
    //if the x-axis is between function max and min
    if (functionMaxY > 0 && functionMinY < 0) {
      float distFromMarginToXAxis = (float) (marginWidthY + (yGraphCoefficient * functionMaxY));
      float startX = (float) GRAPH_MIN_X;
      float stopX  = (float) graphMaxX;
      canvas.drawLine(startX, distFromMarginToXAxis, stopX, 
          distFromMarginToXAxis, defaultPaint);
      canvas.drawText(X_AXIS_STRING, startX, distFromMarginToXAxis, defaultPaint);
    }
    
    //draw the max and min values text on left side
    Paint textPaint = new Paint();
    textPaint.setColor(Color.WHITE);
    textPaint.setStrokeWidth(0);
    textPaint.setStyle(Paint.Style.STROKE);
    textPaint.setTextSize(TEXT_SIZE);
    
    //draw top number text
    String maxYString = displayCurrency (functionMaxY);
    float maxX = 0;
    float maxY = (float) marginWidthY;
    canvas.drawText(maxYString, maxX, maxY, textPaint);
    
    //draw bottom number text
    String minYString = displayCurrency (functionMinY);
    float minX = 0;
    float minY = (float) (marginWidthY + betweenMarginsOnY);
    canvas.drawText(minYString, minX, minY, textPaint);
    
    //draw GraphName
    String graphName = gdo.getGraphName();
    float widthOfGraph = graphMaxX - GRAPH_MIN_X;
    float halfwayPoint = widthOfGraph / 2;
    float bottom = (float) (marginWidthY + betweenMarginsOnY + (marginWidthY/2));
    canvas.drawText(graphName, halfwayPoint, bottom, textPaint);
  }

   private String displayCurrency(Double value) {
      NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
      return currencyFormatter.format(value);
    }


}