package com.byronkatz.reap.general;

import com.byronkatz.R;
import com.byronkatz.reap.activity.LoanActivity;

import android.view.View;
import android.view.View.OnClickListener;

public class HelpButtonOnClickWrapper implements OnClickListener {

  private ValueEnum ve;
  
  public HelpButtonOnClickWrapper(ValueEnum ve) {
    this.ve = ve;
  }
  
  @Override
  public void onClick(View v) {
    Utility.showHelpDialog(
        R.string.numOfCompoundingPeriodsDescriptionText, 
        R.string.numOfCompoundingPeriodsTitleText, v.getContext());
  }


}
