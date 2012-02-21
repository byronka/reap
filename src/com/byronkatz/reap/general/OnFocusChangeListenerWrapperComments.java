package com.byronkatz.reap.general;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class OnFocusChangeListenerWrapperComments implements OnFocusChangeListener {

  private ValueEnum ve;

  public OnFocusChangeListenerWrapperComments(ValueEnum ve) {
    this.ve = ve; 
  }

  @Override
  public void onFocusChange(View v, boolean hasFocus) {
    if (hasFocus) {
      Utility.setSelectionOnView(v, ve);
    } else if (!hasFocus) {

      MessageDigest digester = null;
      try {
        digester = MessageDigest.getInstance("SHA-256");
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }

      String commentsValue = ((EditText) v).getText().toString();
      byte[] bytes = commentsValue.getBytes();
      int byteCount = bytes.length;
      digester.update(bytes, 0, byteCount);
      byte[] digest = digester.digest();

      if (Utility.toHexString(digest).equals("AEF4ED1E649B8B652FBA45EEA28CB0018875B9023406F44947E860F8D195260A")) {

        Dialog helpDialog = new Dialog(v.getContext());
        Window window = helpDialog.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, 
            WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        helpDialog.setTitle("Thanks");
        TextView dialogText = new TextView(v.getContext());
        
        String thanksText = "I would like to thank the following people for their help in" +
        		"\nmaking this happen, whether by their technical insight, advice, or general support." +
        		"\n\nSusanne Katz" +
        		"\nCameron Katz" +
        		"\nDavid Katz" +
        		"\nCorey Katz" +
        		"\nSusan Katz" +
        		"\nSteve Hunter" +
        		"\nStanley Stone" +
        		"\nDenis Avdic" +
        		"\nEllis Katz" +
        		"\nDan Katz" +
        		"\nDave Yee" +
        		"\nWilberto Garcia";
        		
        
        dialogText.setText(thanksText);
        helpDialog.setContentView(dialogText);
        helpDialog.setCanceledOnTouchOutside(true);
        helpDialog.show();

      } else {
        Utility.parseThenDisplayValue(v, ve);
      }
    } 

  }

}
