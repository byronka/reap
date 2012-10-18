package com.byronkatz.reap.general;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class OnItemSelectedListenerWrapper implements OnItemSelectedListener {

  private OnItemSelectedListener listener;
  private Boolean spinnerInitialized = false;

  public OnItemSelectedListenerWrapper(OnItemSelectedListener aListener) {
    listener = aListener;
  }

  public void onItemSelected(AdapterView<?> aParentView, View aView, int aPosition, long anId) {
//    Log.d("OnItemSelectedListener", "spinnerInitializedCount: " + spinnerInitialized);
    //necessary to do this as a hack, because the Android implementation of 
    //onItemSelected fires once off after the widget is instantiated, for no good reason
    if (! spinnerInitialized) {
//    Log.d("OnItemSelectedListener", "biding time...");
//    Thread.dumpStack();
      spinnerInitialized = true;

    } else {
//      Log.d("OnItemSelectedListener", "actually calling onItemSelected this time");
//      Thread.dumpStack();

      listener.onItemSelected(aParentView, aView, aPosition, anId);
    }
  }

  public void onNothingSelected(AdapterView<?> aParentView) {
    listener.onNothingSelected(aParentView);
  }
}
