package com.byronkatz;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class GraphActivity extends Activity {

  private static final int GRAPH_MIN_X = 0;
  private static final int GRAPH_MAX_X = 40;
  private static final int GRAPH_MIN_Y = 0;
  private static final int GRAPH_MAX_Y = 40;
  private static final float GRAPH_MARGIN = 0.05f;
  private static final float CIRCLE_RADIUS = 0.02f;

  private CalculationObject calculationObject;
  private NPVGraph npvGraph;
  private FrameLayout npvFrameLayout;
  private GraphDataObject gdo;
  private HashMap<Float, Float> netPresentValueGraphMap;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.graph);
    calculationObject = new CalculationObject();
    netPresentValueGraphMap = new HashMap<Float, Float>();
    gdo = calculationObject.getGdo();
    buildNPVGraph();
    npvFrameLayout = (FrameLayout) findViewById(R.id.npvFrameLayout);
    npvGraph = new NPVGraph(GraphActivity.this);
    npvFrameLayout.addView(npvGraph); 
  }

  private void buildNPVGraph() {
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
  class NPVGraph extends View {

    public NPVGraph(Context context) {
      super(context);
    }

    public void onDraw(Canvas canvas) {
      Rect graphFrameRect = new Rect(GRAPH_MIN_X, GRAPH_MIN_Y, GRAPH_MAX_X, GRAPH_MAX_Y);
      canvas.drawRect(graphFrameRect, new Paint());
      for (HashMap.Entry<Float, Float> entry : netPresentValueGraphMap.entrySet()) {
        Float xValue = entry.getKey();
        Float yValue = entry.getValue();
        canvas.drawCircle(xValue, yValue, CIRCLE_RADIUS, new Paint());
        this.draw(canvas);
      }
    }


  }

}
