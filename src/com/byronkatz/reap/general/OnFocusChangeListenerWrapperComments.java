package com.byronkatz.reap.general;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import com.byronkatz.R;

import android.app.Dialog;
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
        ((EditText) v).setText("");
        Dialog helpDialog = new Dialog(v.getContext());
        helpDialog.setContentView(R.layout.thanks);
        Window window = helpDialog.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, 
            WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        helpDialog.setTitle("Thanks");
        TextView dialogText = (TextView) helpDialog.findViewById(R.id.textThanksTextView);
        

        String thanksText = uncompressText();
        
        dialogText.setText(thanksText);
        helpDialog.setCanceledOnTouchOutside(true);
        helpDialog.show();

      } else {
        Utility.parseThenDisplayValue(v, ve);
      }
    } 

  }
  
  
  private String uncompressText() {
    
    byte[] outputBuffer;
    
    Inflater inflater = new Inflater();
    byte[] compressedInputBuffer = {
        120, -100, -123, 84, -55, 110, -36, 48, 12, -67, -21, 43, -40, 91, 2,
         76, -25, 31, -118, -92, 72, -73, -100, -46, 34, -56, 81, -74, 105, -101,
         29, 89, 116, 37, -39, -82, -5, -11, 125, -76, -89, 89, -70, -96, -105,
         76, 32, -119, -28, -37, -24, -9, -76, -24, 20, 26, 10, 114, 98, 42, 74,
         -91, -9, -15, -124, -65, 76, -83, -122, -96, -117, -60, -114, 70, -42,
         49, -40, 65, -78, 11, 73, -44, 115, 24, 73, -94, 27, -4, -55, -18, 75,
         47, -103, 122, 63, -114, 28, 15, -76, -12, -116, 71, -119, -86, -11, -4,
         -72, 112, -35, 71, -87, 125, 64, 69, -106, -82, 47, 7, -14, -51, 44, 53,
         31, 8, 13, 59, -114, -100, 112, -105, -89, 113, -44, 84, -114, -50, -35,
         77, -39, -57, -56, -12, -47, -105, 31, -121, 109, 104, -59, 65, 120,
         -74, 73, 18, 105, 0, -52, 62, -23, -44, -11, 54, -73, 62, -111, -113,
         -115, -3, 23, -113, 68, -97, 13, 124, -90, -54, 87, 124, 116, 87, 126,
         -32, -92, -15, 89, -97, 85, -89, 68, 117, -49, -100, -38, 41, 80, 35,
         121, -44, 44, 69, -44, 74, -17, 123, 95, 72, 90, 96, -93, -92, -11,
         -119, 11, -75, -127, 23, -102, -14, 46, 10, -45, -96, 120, 119, 60, -70,
         107, 63, 75, -13, 123, -45, -42, 120, 25, -64, 60, 74, -110, -78, 97,
         26, 36, 99, -42, 44, -118, 30, 13, 126, 3, 117, 9, -102, 93, 105, -30,
         -11, 5, -71, -83, 110, -86, 123, -116, 94, 56, -124, -41, 21, -9, 126,
         -26, -58, 104, -84, 64, -10, -96, 19, -60, 77, 77, 88, -87, 78, -21, 43,
         -70, 88, -39, -89, 3, 37, 27, 121, -71, -85, -75, -75, -93, -117, 91,
         29, 46, -1, -82, -104, -31, 25, -109, 2, -71, -99, 117, -119, 65, 118,
         55, -63, -35, -6, -44, 9, -45, -115, 106, 51, 120, -8, 55, -84, -72,
         -57, -5, 65, -51, 70, -13, 83, 41, -14, 12, 71, 27, -99, -86, 2, 92, 3,
         -100, -77, -122, 53, -89, -30, 37, -122, -43, -7, -112, -11, -79, -80,
         -11, -101, -1, -97, 116, 66, 42, 30, -37, 90, -101, -106, 67, -39, -76,
         -52, -80, -122, 22, -65, -102, -37, 35, -41, 2, -1, -53, 110, 29, -60,
         -66, 43, -104, 70, -17, -90, 88, 108, -68, -79, 25, 125, 17, -114, 5, 2,
         52, -20, -125, 49, 88, -92, -12, 24, -24, -76, -54, -100, -77, -32, 125,
         100, 32, 67, 117, -91, 41, -23, 66, -106, -56, -79, -41, -56, 123, 7,
         -2, 94, 67, 89, -12, 64, 26, -77, 25, -75, 31, -17, 66, -76, 40, -83,
         -4, 57, 73, -18, 73, -90, 8, -99, 49, -45, -121, -80, -17, -61, 20, 107,
         11, 75, 38, 109, 45, 37, -4, 109, -110, -60, 3, -102, 102, 84, -6, -80,
         -26, -78, -21, -14, -121, -85, 29, 68, -96, 54, -127, 67, -29, -36, 7,
         4, 62, 103, -1, -126, -32, -106, -95, 39, -116, -75, 6, 28, 102, -114,
         121, 119, 110, -16, 9, -127, -76, -122, 103, -49, -18, -118, -113, 1,
         41, -70, 43, -113, 12, -57, 4, -53, 7, -119, 62, -83, -65, 56, -38, 10,
         122, -77, -67, 53, -115, 20, 16, -119, 107, -115, -118, 96, 22, 119,
         -51, 17, 18, -67, -103, 27, -87, -9, 6, 25, -26, 98, -55, 27, -58, -114,
         -58, -13, 32, -72, 38, 96, 16, 54, -45, 77, -35, 5, -31, -26, 77, 13,
         -32, 4, 123, -121, -91, 39, -75, 112, 104, -60, 115, -5, 64, 44, -88,
         59, -70, -73, 33, 72, -34, -64, 35, 95, 95, 21, -36, -1, -67, -49, -18,
         -30, 75, -84, 3, 95, -46, -75, 127, -66, -83, -65, 125, 25, -98, -66,
         26, 47, 37, -55, 83, -41, 25, 95, 24, 115, -80, -27, 100, 122, -32, -77,
         38, -26, 13, 70, -101, 110, 22, 87, 109, 24, -82, -51, -62, -117, -69,
         -105, 80, 33, -67, 74, 55, 62, 33, 126, -1, 121, -2, 19, -124, -5, -42,
         83

    };
    inflater.setInput(compressedInputBuffer);
    inflater.finished();
    
    outputBuffer = new byte[1316];
    try {
      inflater.inflate(outputBuffer);
    } catch (DataFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    

    return new String(outputBuffer);
  }
  

}
