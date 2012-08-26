package com.byronkatz.reap.general;

import android.view.View;
import android.view.View.OnClickListener;


public class TitleTextOnClickListenerWrapper implements OnClickListener {
  
  ValueEnum valueEnum;

  public TitleTextOnClickListenerWrapper (ValueEnum valueEnum) {

      this.valueEnum = valueEnum;

  }
    
    public void onClick(View v) {
      Utility.showHelpDialog(
          valueEnum.getHelpText(),
          valueEnum.getTitleText(), 
          v.getContext());
    }
      
  }
