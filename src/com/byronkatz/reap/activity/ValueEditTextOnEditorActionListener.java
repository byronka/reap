package com.byronkatz.reap.activity;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ValueEditTextOnEditorActionListener implements
    OnEditorActionListener {
  /**
   * 
   */
  private final GraphActivity graphActivity;

  /**
   * @param graphActivity
   */
  ValueEditTextOnEditorActionListener(GraphActivity graphActivity) {
    this.graphActivity = graphActivity;
  }

  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

    //put focus on the invisible View - see graph.xml
    this.graphActivity.sendFocusToJail();
    return false;
  }
}