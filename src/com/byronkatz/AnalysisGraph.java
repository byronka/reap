package com.byronkatz;

import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

class AnalysisGraph extends View {

  private static final int NPV = 0;
  private static final int ATER = 1;
  private static final int ATCF = 2;

  public static final float GRAPH_MARGIN = 0.20f;
  public static final int GRAPH_MIN_X = 0;
  public static final int GRAPH_MIN_Y = 0;
  public static final float CIRCLE_RADIUS = 3.0f;
  public static final float TEXT_SIZE = 10.0f;
  public static final float DEFAULT_GRAPH_STROKE_WIDTH = 1.8f;
  public static final String X_AXIS_STRING = "0";

  public static final CalculatedVariables calculatedVariables = new CalculatedVariables();

  //local graph math variables
  private int graphMaxY;
  private int graphMaxX;
  private HashMap<Integer, Float> dataPointsMap;
  private TreeMap<Float, Float> graphMap;
  private boolean xAxisAppears;

  private Integer graphTypeAttribute;
  private String graphKeyValue;

  private Paint defaultPaint;
  private Paint textPaint;
  private Paint borderPaint;
  private Paint highlightPaint;
  private int currentYearHighlighted;

  //singleton with data
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();


  public AnalysisGraph(Context context, AttributeSet attrs) {
    super(context, attrs);
    if (! isInEditMode()) {

      //set up defaults for the drawing - canvas size, paint color, stroke width.
      defaultPaint = new Paint();
      defaultPaint.setColor(Color.BLUE);
      defaultPaint.setStrokeWidth(DEFAULT_GRAPH_STROKE_WIDTH);
      defaultPaint.setStyle(Paint.Style.STROKE);

      //draw the max and min values text on left side
      textPaint = new Paint();
      textPaint.setColor(Color.WHITE);
      textPaint.setStrokeWidth(0);
      textPaint.setStyle(Paint.Style.STROKE);
      textPaint.setTextSize(TEXT_SIZE);

      borderPaint = new Paint();
      borderPaint.setColor(Color.GRAY);
      borderPaint.setStrokeWidth(3.0f);
      borderPaint.setStyle(Paint.Style.STROKE);

      highlightPaint = new Paint();
      highlightPaint.setColor(Color.YELLOW);
      highlightPaint.setStrokeWidth(4.0f);
      highlightPaint.setStyle(Paint.Style.STROKE);

      graphMap = new TreeMap<Float, Float>();
      dataPointsMap = new HashMap<Integer, Float>();
      initView(attrs);
      crunchData();
    }
  }

  private void initView(AttributeSet attrs) {

    TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AnalysisGraph);
    graphTypeAttribute = a.getInt(R.styleable.AnalysisGraph_graphType, 0);

    switch (graphTypeAttribute) {
    case NPV:
      graphKeyValue = GraphType.NPV.getGraphName();
      break;
    case ATER:
      graphKeyValue = GraphType.ATER.getGraphName();
      break;
    case ATCF:
      graphKeyValue = GraphType.ATCF.getGraphName();
      break;
    default:
      System.err.println("You should not get here, in initView, in AnalysisGraph");
    }
    setFocusable(true);
  }

  private void crunchData() {
    calculatedVariables.crunchCalculation();
    createDataPoints();
  }

  public void onDraw(Canvas canvas) {
    if (! isInEditMode()) {
      graphMaxY = getMeasuredHeight();
      graphMaxX = getMeasuredWidth();


      double functionMinY = Collections.min(dataPointsMap.values());
      double functionMaxY = Collections.max(dataPointsMap.values());
      double functionMinX = Collections.min(dataPointsMap.keySet());
      double functionMaxX = Collections.max(dataPointsMap.keySet());
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

      for (HashMap.Entry<Integer, Float> entry : dataPointsMap.entrySet()) {
        Integer xValue = entry.getKey();
        Float yValue = entry.getValue();
        xGraphValue = (float) (marginWidthX +  xGraphCoefficient * (xValue - functionMinX));
        yGraphValue = (float) (marginWidthY + yGraphCoefficient * (functionMaxY - yValue));
        
        //draw the points on the graph
        if (xValue == currentYearHighlighted) {
          canvas.drawCircle(xGraphValue, yGraphValue, CIRCLE_RADIUS, highlightPaint);
        }
        canvas.drawCircle(xGraphValue, yGraphValue, CIRCLE_RADIUS, defaultPaint);
      }

      //draw the frame
      Rect graphFrameRect = new Rect(GRAPH_MIN_X,GRAPH_MIN_Y, 
          graphMaxX, graphMaxY);
      canvas.drawRect(graphFrameRect, borderPaint);


      //draw the 0 X-axis if the graph passes it.
      //if the x-axis is between function max and min
      if (functionMaxY > 0 && functionMinY < 0) {
        xAxisAppears = true;
        float distFromMarginToXAxis = (float) (marginWidthY + (yGraphCoefficient * functionMaxY));
        float startX = (float) GRAPH_MIN_X;
        float stopX  = (float) graphMaxX;
        canvas.drawLine(startX, distFromMarginToXAxis, stopX, 
            distFromMarginToXAxis, defaultPaint);
        canvas.drawText(X_AXIS_STRING, (float) (marginWidthX / 4), 
            distFromMarginToXAxis, textPaint);
      } else {
        xAxisAppears = false;
      }

      //      //draw a short vertical line where the graph and the x-axis intersect
      //      if (xAxisAppears) {
      //        //determine where the intersection is
      //        Collection<Float> values = graphMap.values();
      //        for (Float f : values) {
      //          if (f > 0) {
      //            
      //          }
      //        }
      //      }

      //draw top number text
      String maxYString = CalculatedVariables.displayCurrency (functionMaxY);
      float maxX = (float) marginWidthX / 4;
      float maxY = (float) marginWidthY;
      canvas.drawText(maxYString, maxX, maxY, textPaint);

      //draw bottom number text
      String minYString = CalculatedVariables.displayCurrency (functionMinY);
      float minX = (float) marginWidthX / 4;
      float minY = (float) (marginWidthY + betweenMarginsOnY);
      canvas.drawText(minYString, minX, minY, textPaint);

      //draw GraphName
      float widthOfGraph = graphMaxX - GRAPH_MIN_X;
      float halfwayPoint = widthOfGraph / 2;
      float bottom = (float) (marginWidthY + betweenMarginsOnY + (marginWidthY/2));
      canvas.drawText(graphKeyValue, halfwayPoint, bottom, textPaint);
    }
  }


  public void createDataPoints() {
    HashMap<Integer, HashMap<String, Float>> calculatedValuesHashMap = dataController.getCalculatedValuesHashMap();
    for (HashMap.Entry<Integer, HashMap<String, Float>> entry : calculatedValuesHashMap.entrySet()) {

      //each year is the key
      Integer key = entry.getKey();
      HashMap<String, Float> values = entry.getValue();

      //unpack the contentValues per year
      Float dataValue = values.get(graphKeyValue);

      //map of year to value for this graph
      dataPointsMap.put(key, dataValue);

    }
  }

  public int getCurrentYearHighlighted() {
    return currentYearHighlighted;
  }

  public void setCurrentYearHighlighted(int currentYearHighlighted) {
    this.currentYearHighlighted = currentYearHighlighted;
  }

  public enum GraphType {

    NPV  ("Net present value"),
    ATER ("After Tax Equity Reversion"),
    ATCF ("After Tax Cash Flow");

    private GraphType (String graphName) {
      this.setGraphName(graphName);
    }

    public String getGraphName() {
      return graphName;
    }

    public void setGraphName(String graphName) {
      this.graphName = graphName;
    }

    private String graphName;
  }

}