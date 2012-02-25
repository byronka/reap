package com.byronkatz.reap.customview;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
import com.byronkatz.R.color;
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
  private static final int CRPV = 4;
  private static final int CRCV = 5;
  private static final Float DIVISOR_MODIFIER = 0.75f;
  private static final Integer RIGHT_SIDE_MARGIN_PIXELS = 70;
  private static final Float EPSILON = 0.00001f;

  public static final Float GRAPH_MARGIN = 0.20f;
  public static final int GRAPH_MIN_X = 0;
  public static final int GRAPH_MIN_Y = 0;
  public static final Float CIRCLE_RADIUS = 5.0f;
  public static final Float TEXT_SIZE = 14.0f;
  public static final Float GRAPH_LINE_STROKE_WIDTH = 5f;
  public static final Float DIVISOR_LINE_STROKE_WIDTH = 1f;
  public static final Float CICLE_STROKE_WIDTH = 1.5f;
  public static final Float HIGHLIGHT_STROKE_WIDTH = 4F;
  public static final Float HIGHLIGHT_CIRCLE_RADIUS = 10.0f;
  public static final String X_AXIS_STRING = "0.0";
  public static final Integer MINIMUM_YEAR = 1;

  //local graph math variables
  private int graphMaxY;
  private int graphMaxX;
  private Double[] dataPoints;

  private Integer graphTypeAttribute;
  private ValueEnum graphKeyValue;

  private Paint graphLinePaint;
  private Paint divisorPaint;
  private Paint textPaint;
  private Paint borderPaint;
  private Paint highlightPaint;
  private int currentYearHighlighted;

  private Boolean isFirstPoint;
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
  private Float distFromMarginToDivisor;
  private Float startX;
  private Float stopX;

  //singleton with data
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();


  public AnalysisGraph(Context context, AttributeSet attrs) {
    super(context, attrs);
    if (! isInEditMode()) {

      //set up defaults for the drawing - canvas size, paint color, stroke width.
      graphLinePaint = createPaint(getResources().getColor(R.color.graph_purple), GRAPH_LINE_STROKE_WIDTH, Paint.Style.STROKE, TEXT_SIZE);
      divisorPaint = createPaint(Color.WHITE, DIVISOR_LINE_STROKE_WIDTH, Paint.Style.STROKE, TEXT_SIZE);
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

    setBackgroundResource(R.drawable.graph_background);

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
    case CRPV:
      graphKeyValue = ValueEnum.CAP_RATE_ON_PURCHASE_VALUE;
      break;
    case CRCV:
      graphKeyValue = ValueEnum.CAP_RATE_ON_PROJECTED_VALUE;
      break;
    default:
      System.err.println("You should not get here, in initView, in AnalysisGraph");
    }
    setFocusable(true);
  }

  public void onDraw(Canvas canvas) {

    AsyncTask.Status threadStatus = null;

    if (GraphActivity.calculateInBackgroundTask != null) { 
      threadStatus = GraphActivity.calculateInBackgroundTask.getStatus();


      if (
          (! isInEditMode()) 
          && 
          (threadStatus != AsyncTask.Status.RUNNING)
          ) {

        dataPoints = dataController.getPlotPoints(graphKeyValue);
        graphMaxY = getMeasuredHeight();
        graphMaxX = getMeasuredWidth();


        List<Double> tempList = Arrays.asList(dataPoints);

        functionMinY = Collections.min(tempList).floatValue();
        functionMaxY = Collections.max(tempList).floatValue();
        functionMinX = MINIMUM_YEAR;
        functionMaxX = dataPoints.length;
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

        //following values set ratio between actual (business) values and graph (pixel-based) values
        xGraphCoefficient = betweenMarginsOnX / deltaFunctionX;
        yGraphCoefficient = betweenMarginsOnY / deltaFunctionY;

        xGraphValue = 0.0f;
        yGraphValue = 0.0f;

        isFirstPoint = true;
        Float oldXGraphValue = 0.0f;
        Float oldYGraphValue = 0.0f;

        xValue = 0;
        yValue = 0.0f;

        //actually draw the graphline here
        for (int i = 0; i < dataPoints.length; i++) {  
          xValue = i + 1;

          if ((Math.abs(yValue - dataPoints[i])) > EPSILON) {
            //only if new value and the previous value are essentially different, 
            //otherwise use the old value
            yValue = dataPoints[i].floatValue();
          } 
          xGraphValue = (float) (marginWidthX +  xGraphCoefficient * (xValue - functionMinX));

          //if the difference between the top and bottom is zero, it's an outlier - so we use if statement
          if (Float.isInfinite(yGraphCoefficient)) {
            yGraphValue = (float) (graphMaxY / 2);
          } else {
            yGraphValue = (float) (marginWidthY + yGraphCoefficient * (functionMaxY - yValue));
          }


          //draw the points on the graph
          if (!isFirstPoint) {
            canvas.drawLine(oldXGraphValue, oldYGraphValue, xGraphValue, yGraphValue, graphLinePaint);
          }
          isFirstPoint = false;

          oldXGraphValue = xGraphValue;
          oldYGraphValue = yGraphValue;

        }

        //draw the highlight circle
        //if the difference between the top and bottom is zero, it's an outlier - so we use if statement
        if (Float.isInfinite(yGraphCoefficient)) {
          yGraphValue = (float) (graphMaxY / 2);
        } else {
          yGraphValue = (float) (marginWidthY + yGraphCoefficient * (functionMaxY - dataPoints[currentYearHighlighted - 1].floatValue()));
        }
        
        xGraphValue = (float) (marginWidthX +  xGraphCoefficient * (currentYearHighlighted - functionMinX));
        canvas.drawCircle(xGraphValue, yGraphValue, HIGHLIGHT_CIRCLE_RADIUS, highlightPaint);
        canvas.drawPoint(xGraphValue, yGraphValue, highlightPaint);
        canvas.drawText(String.valueOf(currentYearHighlighted), xGraphValue+15, yGraphValue+15, textPaint);


        //draw the frame
        Rect graphFrameRect = new Rect(GRAPH_MIN_X,GRAPH_MIN_Y, 
            graphMaxX, graphMaxY);
        canvas.drawRect(graphFrameRect, borderPaint);


        //draw the 0 X-axis if the graph passes it.
        //if the x-axis is between function max and min
        if (functionMaxY > 0 && functionMinY < 0) {
          drawZeroAxis(canvas);
          drawPositiveDivisors(canvas);
          drawNegativeDivisors(canvas);
          //if max and min are positive
        }  else if (functionMaxY > 0 && functionMinY > 0) {
          drawPositiveDivisors(canvas);
          //if max and min are negative
        } else if (functionMaxY < 0 && functionMinY < 0) {
          drawNegativeDivisors(canvas);
        }


        //draw top number text
        maxYString = Utility.displayValue ((double)functionMaxY, graphKeyValue);
        maxX =  marginWidthX.floatValue() / 4;
        maxY =  marginWidthY.floatValue();
        canvas.drawText(maxYString, maxX, maxY, textPaint);

        //draw bottom number text
        minYString = Utility.displayValue ((double)functionMinY, graphKeyValue);
        minX = (float) marginWidthX.floatValue() / 4;
        minY = (float) (marginWidthY + betweenMarginsOnY);
        canvas.drawText(minYString, minX, minY, textPaint);

        //draw GraphName and current value
        bottom = marginWidthY + betweenMarginsOnY + (marginWidthY/2);
        String currentValueNumerals = Utility.displayValue(
            dataController.getValueAsDouble(graphKeyValue, currentYearHighlighted), graphKeyValue);
        String currentValue = graphKeyValue.toString() + ": " + currentValueNumerals;
        canvas.drawText(currentValue, minX, bottom, textPaint);
      }
    }
  }

  private void drawPositiveDivisors(Canvas canvas) {

    //    following creates the modulo 10 divisors between the current max and min
    Float divisorRange = deltaFunctionY * DIVISOR_MODIFIER;

    if (divisorRange > 50) {
      Float baseDivisorLine = (float) Math.pow(10, Math.floor(Math.log10(divisorRange)));
      startX = (float) graphMaxX - RIGHT_SIDE_MARGIN_PIXELS;
      stopX  = (float) graphMaxX;


      for (int i = 1; (baseDivisorLine * i) < functionMaxY; i++ ) {

        Float divisorLineValue = baseDivisorLine * i;

        //margin width plus (functionMaxY * graphcoefficient) takes us to the x-axis.  Go up from there.
        distFromMarginToDivisor = (marginWidthY + (yGraphCoefficient * (functionMaxY - divisorLineValue)));

        canvas.drawLine(startX, distFromMarginToDivisor, stopX, 
            distFromMarginToDivisor, divisorPaint);
        canvas.drawText(Utility.displayShortValue((double)divisorLineValue, graphKeyValue), (Float) startX, 
            distFromMarginToDivisor, textPaint);

      }
    }
  }

  private void drawNegativeDivisors(Canvas canvas) {

    //    following creates the modulo 10 divisors between the current max and min
    Float divisorRange = deltaFunctionY * DIVISOR_MODIFIER;

    if (divisorRange > 50) {
      Float baseDivisorLine = (float) Math.pow(10, Math.floor(Math.log10(divisorRange)));
      baseDivisorLine = -baseDivisorLine;
      startX = (float) graphMaxX - RIGHT_SIDE_MARGIN_PIXELS;
      stopX  = (float) graphMaxX;


      for (int i = 1; (baseDivisorLine * i) > functionMinY; i++ ) {

        Float divisorLineValue = baseDivisorLine * i;

        //margin width plus (functionMaxY * graphcoefficient) takes us to the x-axis.  Go down from there.
        distFromMarginToDivisor = (marginWidthY + (yGraphCoefficient * (functionMaxY - divisorLineValue)));

        canvas.drawLine(startX, distFromMarginToDivisor, stopX, 
            distFromMarginToDivisor, divisorPaint);
        canvas.drawText(Utility.displayShortValue((double)divisorLineValue, graphKeyValue), (Float) startX, 
            distFromMarginToDivisor, textPaint);

      }
    }
  }

  private void drawZeroAxis(Canvas canvas) {
    //following is the distance from max value down to the zero line (the x-axis)
    distFromMarginToXAxis = (marginWidthY + (yGraphCoefficient * functionMaxY));
    startX = (float) GRAPH_MIN_X;
    stopX  = (float) graphMaxX;
    canvas.drawLine(startX, distFromMarginToXAxis, stopX, 
        distFromMarginToXAxis, divisorPaint);
    canvas.drawText(X_AXIS_STRING, (Float) (stopX - marginWidthX / 2), 
        distFromMarginToXAxis, textPaint);
  }

  public int getCurrentYearHighlighted() {
    return currentYearHighlighted;
  }

  public void setCurrentYearHighlighted(int currentYearHighlighted) {
    this.currentYearHighlighted = currentYearHighlighted;
  }

}