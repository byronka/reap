package com.byronkatz.reap.general;

import android.view.View;
import android.view.View.OnClickListener;

public class HelpButtonOnClickWrapper implements OnClickListener {

  private ValueEnum ve;
  
  public HelpButtonOnClickWrapper(ValueEnum ve) {
    this.ve = ve;
  }
  
  public void onClick(View v) {
    Utility.showHelpDialog(
        ve.getHelpText(), ve.getTitleText(), v.getContext());
  }


}
