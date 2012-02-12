package com.byronkatz.reap.general;

import com.byronkatz.reap.activity.MathCheckActivity;

import android.view.View;
import android.view.View.OnClickListener;

public class CheckMathOnClickWrapper implements OnClickListener {

  private ValueEnum ve;

  public CheckMathOnClickWrapper(ValueEnum ve) {
    this.ve = ve;
  }

  @Override
  public void onClick(View v) {
    
    
    Utility.showHelpDialog(
        MathCheckActivity.getRentalIncome(), 
        ve.getTitleText(), 
        v.getContext());

  }




}
