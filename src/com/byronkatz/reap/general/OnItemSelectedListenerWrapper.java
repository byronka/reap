package com.byronkatz.reap.general;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class OnItemSelectedListenerWrapper implements OnItemSelectedListener {

  private OnItemSelectedListener listener;
  private static final int SPINNER_COUNT = 1;
  private int spinnerInitializedCount = 0;

  public OnItemSelectedListenerWrapper(OnItemSelectedListener aListener) {
    listener = aListener;
  }

  @Override
  public void onItemSelected(AdapterView<?> aParentView, View aView, int aPosition, long anId) {

    //necessary to do this as a hack, because the Android implementation of 
    //onItemSelected fires once off after the widget is instantiated, for no good reason
    if (spinnerInitializedCount < SPINNER_COUNT) {

      spinnerInitializedCount++;

    } else {
      listener.onItemSelected(aParentView, aView, aPosition, anId);
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> aParentView) {
    listener.onNothingSelected(aParentView);
  }
}
