package com.byronkatz;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class OnItemSelectedListenerWrapper implements OnItemSelectedListener {

    private int lastPosition;
    private OnItemSelectedListener listener;

    public OnItemSelectedListenerWrapper(OnItemSelectedListener aListener) {
        lastPosition = 0;
        listener = aListener;
    }

    @Override
    public void onItemSelected(AdapterView<?> aParentView, View aView, int aPosition, long anId) {
        if (lastPosition == aPosition) {
            Log.d(getClass().getName(), "Ignoring onItemSelected for same position: " + aPosition);
        } else {
            Log.d(getClass().getName(), "Passing on onItemSelected for different position: " + aPosition);
            listener.onItemSelected(aParentView, aView, aPosition, anId);
        }
        lastPosition = aPosition;
    }

    @Override
    public void onNothingSelected(AdapterView<?> aParentView) {
        listener.onNothingSelected(aParentView);
    }
}
