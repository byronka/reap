package com.byronkatz.reap.customview;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;

import com.byronkatz.R;
import com.byronkatz.reap.activity.GraphActivity;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

public class AnalysisGraph extends View {

  private static final int NPV = 0;
  private static final int ATER = 1;
  private static final int ATCF = 2;
  private static final int MIRR = 3;

  public static final Float GRAPH_MARGIN = 0.20f;
  public static final int GRAPH_MIN_X = 0;
  public static final int GRAPH_MIN_Y = 0;
  public static final Float CIRCLE_RADIUS = 3.0f;
  public static final Float TEXT_SIZE = 10.0f;
  public static final Float DEFAULT_GRAPH_STROKE_WIDTH = 1.8f;
  public static final String X_AXIS_STRING = "0";

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
  
  
  private Float functionMinY;
  private Float functionMaxY;
  private Integer functionMinX;
  private Integer functionMaxX;
  private Integer deltaGraphY;
  private Integer deltaGraphX;
  private Float deltaFunctionY;
  private Float marginWidthX;
  private Float marginWidthY;
  private Float twiceXMargin;
  private Float twiceYMargin;
  private Float betweenMarginsOnX;
  private Float betweenMarginsOnY;
  private Float xGraphCoefficient;
  private Float yGraphCoefficient;
  private Float xGraphValue;
  private Float yGraphValue;
  private Integer xValue;
  private Float yValue;
  private String maxYString;
  private Float maxX;
  private Float maxY;
  private String minYString;
  private Float minX;
  private Float minY;
  private Float bottom;
  private Float distFromMarginToXAxis;
  private Float startX;
  private Float stopX;

  //singleton with data
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();


  public AnalysisGraph(Context context, AttributeSet attrs) {
    super(context, attrs);
    if (! isInEditMode()) {

      //set up defaults for the drawing - canvas size, paint color, stroke width.
      defaultPaint = createPaint(Color.BLUE, DEFAULT_GRAPH_STROKE_WIDTH, Paint.Style.STROKE, TEXT_SIZE);
      textPaint    = createPaint(Color.WHITE, 0.0f, Paint.Style.STROKE, TEXT_SIZE);
      borderPaint = createPaint(Color.GRAY, 3.0f, Paint.Style.STROKE, TEXT_SIZE);
      highlightPaint = createPaint(Color.YELLOW, 4.0f, Paint.Style.STROKE, TEXT_SIZE);
      
      initView(attrs);
    }
  }

  private Paint createPaint (int color, Float strokeWidth, Style styleStroke, Float textSize) {
    
    Paint returnValuePaint = new Paint();
    
    returnValuePaint.setColor(color);
    returnValuePaint.setStrokeWidth(strokeWidth);
    returnValuePaint.setStyle(styleStroke);
    returnValuePaint.setTextSize(textSize);
    
    return returnValuePaint;
  }
  
  private void initView(AttributeSet attrs) {

    //following line gets custom attribute from xml file
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
    case MIRR:
      graphKeyValue = ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN;
      break;
    default:
      System.err.println("You should not get here, in initView, in AnalysisGraph");
    }
    setFocusable(true);
  }
  
  public void onDraw(Canvas canvas) {
    
    AsyncTask.Status threadStatus = GraphActivity.calculateInBackgroundTask.getStatus();
    if (
        (! isInEditMode()) 
      && 
        (threadStatus != AsyncTask.Status.RUNNING)
      ) {
      
      dataPoints = dataController.getPlotPoints(graphKeyValue);
      
      graphMaxY = getMeasuredHeight();
      graphMaxX = getMeasuredWidth();

      Collection<Float> tempCollection = dataPoints.values();
      
      functionMinY = Collections.min(tempCollection);
      functionMaxY = Collections.max(tempCollection);
      functionMinX = Collections.min(dataPoints.keySet());
      functionMaxX = Collections.max(dataPoints.keySet());
      deltaGraphY = graphMaxY - GRAPH_MIN_Y;
      deltaGraphX = graphMaxX - GRAPH_MIN_X;
      deltaFunctionY = functionMaxY - functionMinY;
      Integer deltaFunctionX = functionMaxX - functionMinX;
      marginWidthX = deltaGraphX * GRAPH_MARGIN;
      marginWidthY = deltaGraphY * GRAPH_MARGIN;
      //margin on left and right
      twiceXMargin = marginWidthX * 2;
      //for margin on top and bottom
      twiceYMargin = marginWidthY * 2;
      betweenMarginsOnX = deltaGraphX - twiceXMargin;
      betweenMarginsOnY = deltaGraphY - twiceYMargin;
      xGraphCoefficient = betweenMarginsOnX / deltaFunctionX;
      yGraphCoefficient = betweenMarginsOnY / deltaFunctionY;

      xGraphValue = 0.0f;
      yGraphValue = 0.0f;

      for (Entry<Integer, Float> entry : dataPoints.entrySet()) {  
        xValue = entry.getKey();
        yValue = entry.getValue();
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
        distFromMarginToXAxis = (marginWidthY + (yGraphCoefficient * functionMaxY));
        startX = (float) GRAPH_MIN_X;
        stopX  = (float) graphMaxX;
        canvas.drawLine(startX, distFromMarginToXAxis, stopX, 
            distFromMarginToXAxis, defaultPaint);
        canvas.drawText(X_AXIS_STRING, (Float) (marginWidthX / 8), 
            distFromMarginToXAxis, textPaint);
      } 


      //draw top number text
      maxYString = Utility.displayValue (functionMaxY, graphKeyValue);
      maxX = (Float) marginWidthX / 4;
      maxY = (Float) marginWidthY;
      canvas.drawText(maxYString, maxX, maxY, textPaint);

      //draw bottom number text
      minYString = Utility.displayValue (functionMinY, graphKeyValue);
      minX = (Float) marginWidthX / 4;
      minY = (Float) (marginWidthY + betweenMarginsOnY);
      canvas.drawText(minYString, minX, minY, textPaint);

      //draw GraphName and current value
      bottom = marginWidthY + betweenMarginsOnY + (marginWidthY/2);
      
      String currentValue = graphKeyValue.toString() + ": " + Utility.displayValue(
          dataController.getValueAsFloat(graphKeyValue, currentYearHighlighted), graphKeyValue);
      canvas.drawText(currentValue, minX, bottom, textPaint);
    }
  }

  public int getCurrentYearHighlighted() {
    return currentYearHighlighted;
  }

  public void setCurrentYearHighlighted(int currentYearHighlighted) {
    this.currentYearHighlighted = currentYearHighlighted;
  }

}