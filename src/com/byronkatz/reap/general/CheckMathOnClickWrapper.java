package com.byronkatz.reap.general;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.byronkatz.reap.mathtests.ItemTestInterface;
import com.byronkatz.reap.mathtests.MathCheckImpl;
import com.byronkatz.reap.mathtests.MathCheckInterface;

public class CheckMathOnClickWrapper implements OnClickListener {

  private ValueEnum ve;
  private MathCheckInterface mct;
  private Class<? extends ItemTestInterface> tempClass;

  public CheckMathOnClickWrapper(ValueEnum ve) {
    this.ve = ve;
    mct = new MathCheckImpl();
  }

  @Override
  public void onClick(View v) {

    String testResults = "";

    tempClass = mct.getTestResults(ve);
    if (tempClass == null) {
      Log.d(getClass().getName(), "tempClass was null");
    } else {
      try {
        testResults = tempClass.newInstance().getValue(); 
      } catch (Exception e) {
        Log.d(getClass().getName(), e.toString());
      } 
    }
    Utility.showHelpDialog(
        testResults,
        ve.getTitleText(), 
        v.getContext());


  }




}
