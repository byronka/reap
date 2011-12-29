package com.byronkatz;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

  public static final Float GRAPH_MARGIN = 0.20f;
  public static final int GRAPH_MIN_X = 0;
  public static final int GRAPH_MIN_Y = 0;
  public static final Float CIRCLE_RADIUS = 3.0f;
  public static final Float TEXT_SIZE = 10.0f;
  public static final Float DEFAULT_GRAPH_STROKE_WIDTH = 1.8f;
  public static final String X_AXIS_STRING = "0";

  public static final CalculatedVariables calculatedVariables = new CalculatedVariables();

  //local graph math variables
  private int graphMaxY;
  private int graphMaxX;
  private Map<Integer, Float> dataPoints;

  private Integer graphTypeAttribute;
  private ValueEnum graphKeyValue;

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

      
      
      dataPoints = new HashMap<Integer, Float>();
      initView(attrs);
      crunchData();
    }
  }

  private void initView(AttributeSet attrs) {

    TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AnalysisGraph);
    graphTypeAttribute = a.getInt(R.styleable.AnalysisGraph_graphType, 0);

    switch (graphTypeAttribute) {
    case NPV:
      graphKeyValue = ValueEnum.NPV;
      break;
    case ATER:
      graphKeyValue = ValueEnum.ATER;
      break;
    case ATCF:
      graphKeyValue = ValueEnum.ATCF;
      break;
    default:
      System.err.println("You should not get here, in initView, in AnalysisGraph");
    }
    setFocusable(true);
  }

  private void crunchData() {
    CalculatedVariables.crunchCalculation();
    createDataPoints();
  }

  public void onDraw(Canvas canvas) {
    if (! isInEditMode()) {
      graphMaxY = getMeasuredHeight();
      graphMaxX = getMeasuredWidth();

      Collection<Float> tempCollection = dataPoints.values();
      
      Float functionMinY = Collections.min(tempCollection);
      Float functionMaxY = Collections.max(tempCollection);
      Integer functionMinX = Collections.min(dataPoints.keySet());
      Integer functionMaxX = Collections.max(dataPoints.keySet());
      Integer deltaGraphY = graphMaxY - GRAPH_MIN_Y;
      Integer deltaGraphX = graphMaxX - GRAPH_MIN_X;
      Float deltaFunctionY = functionMaxY - functionMinY;
      Integer deltaFunctionX = functionMaxX - functionMinX;
      Float marginWidthX = deltaGraphX * GRAPH_MARGIN;
      Float marginWidthY = deltaGraphY * GRAPH_MARGIN;
      //margin on left and right
      Float twiceXMargin = marginWidthX * 2;
      //for margin on top and bottom
      Float twiceYMargin = marginWidthY * 2;
      Float betweenMarginsOnX = deltaGraphX - twiceXMargin;
      Float betweenMarginsOnY = deltaGraphY - twiceYMargin;
      Float xGraphCoefficient = betweenMarginsOnX / deltaFunctionX;
      Float yGraphCoefficient = betweenMarginsOnY / deltaFunctionY;

      Float xGraphValue = 0.0f;
      Float yGraphValue = 0.0f;

      //start the listIterator on the second element, which has an index of "1"
      for (Entry<Integer, Float> entry : dataPoints.entrySet()) {  
        Integer xValue = entry.getKey();
        Float yValue = entry.getValue();
        xGraphValue = (marginWidthX +  xGraphCoefficient * (xValue - functionMinX));
        yGraphValue = (marginWidthY + yGraphCoefficient * (functionMaxY - yValue));

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
//        xAxisAppears = true;
        Float distFromMarginToXAxis = (marginWidthY + (yGraphCoefficient * functionMaxY));
        Float startX = (float) GRAPH_MIN_X;
        Float stopX  = (float) graphMaxX;
        canvas.drawLine(startX, distFromMarginToXAxis, stopX, 
            distFromMarginToXAxis, defaultPaint);
        canvas.drawText(X_AXIS_STRING, (Float) (marginWidthX / 8), 
            distFromMarginToXAxis, textPaint);
      } else {
//        xAxisAppears = false;
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
      Float maxX = (Float) marginWidthX / 4;
      Float maxY = (Float) marginWidthY;
      canvas.drawText(maxYString, maxX, maxY, textPaint);

      //draw bottom number text
      String minYString = CalculatedVariables.displayCurrency (functionMinY);
      Float minX = (Float) marginWidthX / 4;
      Float minY = (Float) (marginWidthY + betweenMarginsOnY);
      canvas.drawText(minYString, minX, minY, textPaint);

      //draw GraphName
      Integer widthOfGraph = graphMaxX - GRAPH_MIN_X;
      Float halfwayPoint = widthOfGraph / 2.0f;
      Float bottom = marginWidthY + betweenMarginsOnY + (marginWidthY/2);
      canvas.drawText(graphKeyValue.toString(), halfwayPoint, bottom, textPaint);
    }
  }

  /**
   * This function creates a Collection local to this class so that it can efficiently render
   * the graph.  It also allows the use of Collections.min() and max() for the onDraw() function.
   * @return
   */
  public void createDataPoints() {

    int yearsOfCompounding = dataController.
        getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS).intValue() / CalculatedVariables.NUM_OF_MONTHS_IN_YEAR;

    for (int year = 1; year <= yearsOfCompounding; year++) {
      Float tempVal = dataController.getValueAsFloat(graphKeyValue, year);
      dataPoints.put(year, tempVal);
    }

  }

  public int getCurrentYearHighlighted() {
    return currentYearHighlighted;
  }

  public void setCurrentYearHighlighted(int currentYearHighlighted) {
    this.currentYearHighlighted = currentYearHighlighted;
  }

}