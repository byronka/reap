package com.byronkatz.reap.general;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.byronkatz.reap.R;

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
        String message = "An error occurred - the REAP system attempted" +
        		" to get an encryption algorithm from the device library, " +
        		"and it was not provided.  Please notify the developer.\n" + e.getMessage();

        Utility.showAlertDialog(v.getContext(), message);
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
        76, -25, 31, -118, -92, 72, -73, -100, -46, 34, -56, 81, -74, 105, 91,
        29, 89, 84, -75, -40, 117, -65, -66, -113, -10, 52, 75, 23, -12, -110,
        9, 36, -111, 124, 27, -3, -98, 22, -87, -66, 35, -17, 78, 76, 69, -88,
        -116, 54, -100, -16, -105, -87, 23, -17, 101, 113, 97, -96, -56, 18,
        -67, 30, 36, -67, 112, -119, 70, -10, -111, 92, 48, -109, 61, -23, 125,
        25, 93, -90, -47, -58, -56, -31, 64, -53, -56, 120, -108, -88, 89, -49,
        -113, 11, -73, 99, 112, -83, -11, -88, -56, 110, 24, -53, -127, 108, 55,
        -69, -106, 15, -124, -122, 3, 7, 78, -72, -53, 53, 70, 73, -27, 104,
        -52, 93, -51, 54, 4, -90, -113, -74, -4, 56, 108, 67, 27, -10, -114,
        103, -99, -28, 2, 77, -128, 57, 38, -87, -61, -88, 115, -37, 19, -39,
        -48, -23, 127, -31, 72, -12, 89, -63, 103, 106, 108, -61, 71, 115, 101,
        39, 78, 18, -98, -11, 89, -91, 38, 106, 71, -26, -44, 87, 79, -99, -53,
        81, -78, 43, 78, -76, -12, 126, -76, -123, 92, 15, 108, -108, -92, 61,
        113, -95, -34, -13, 66, 53, -17, -94, 48, 77, -126, 119, -57, -93, -71,
        -74, -77, -21, 126, 111, -38, 43, 47, 5, -104, -93, 75, -82, 108, -104,
        38, -105, 49, 107, 118, -126, 30, 29, 126, 61, 13, 9, -102, 93, 73, -30,
        -11, 5, -71, -83, -82, -74, 35, 70, 47, -20, -3, -21, -122, 71, 59, 115,
        -89, 52, 86, 32, 123, -112, 10, 113, 83, -25, 87, -30, 25, -62, -98,
        -104, 35, -35, -54, 52, -83, 84, 35, 89, -17, 41, -24, -12, 87, -69,
        112, 91, 103, -70, -64, -3, -27, -33, -59, 83, 104, 49, 9, 72, -24, -39,
        -112, 24, -68, 119, 63, -52, -83, 77, -125, 99, -70, 17, -23, 38, 11,
        43, 49, 97, 72, 120, 63, -119, 58, -86, -42, 10, -123, 13, 67, 39, -75,
        41, -128, 56, -63, 68, 109, -40, 114, 42, -42, 5, -65, 26, -21, -77, 60,
        22, -10, 118, -117, -62, 39, -87, 8, -56, 99, 91, 109, -45, -77, 47,
        -101, -84, 25, 46, -47, 98, 87, 53, 62, 114, -21, 16, -123, -78, -69, 8,
        -35, -17, 10, -90, -47, -69, 26, -118, -114, 87, 54, -47, 22, -57, -95,
        64, -117, -114, -83, 87, 6, -117, 43, 35, 6, 26, 105, 50, -25, -20, -16,
        62, 48, -112, -95, -70, -111, -108, 100, 33, 13, 103, 28, 37, -16, -34,
        -127, -65, -73, 16, 25, 61, 16, -52, -84, -98, -19, -57, -69, 16, 61,
        74, 27, 123, 14, -107, 121, -110, 41, -80, 77, -104, -87, 98, 111, -85,
        81, 67, -85, -71, -55, 36, -67, 6, -122, -65, 85, -105, 120, 66, -45,
        -116, 74, -21, -41, 92, 118, 93, -2, 48, 120, -128, 8, -44, 39, 112,
        -24, -116, -7, -128, -20, -25, 108, 95, 16, -36, -30, -12, -124, -79,
        21, -113, -61, -52, 33, -17, -50, 77, 54, 33, -101, -38, -16, -20, -39,
        93, -79, -63, 35, 80, 119, -27, -111, 97, 76, -80, 124, 114, -63, -90,
        -11, 23, 71, -35, 70, -85, -74, -9, -86, -111, 0, 34, 113, 43, 65, -112,
        -47, 98, -82, 57, 64, -94, 55, 115, -25, -38, -67, 65, -122, -71, -40,
        -9, -114, -79, -82, -31, 60, 8, -82, 57, 48, -16, -101, -23, -86, -18,
        -126, -100, -13, -90, 6, 112, -126, -67, -63, -2, -109, 104, 56, 36,
        -32, -71, 126, 43, 22, -44, 29, -51, 91, -17, 93, -34, -64, 35, 95, 95,
        5, -36, -1, -67, -38, -26, -30, 75, 104, 61, 95, -46, -75, 125, -66,
        -72, -65, 125, 36, -98, 62, 32, 47, 37, -55, 117, 24, -108, 47, -116,
        57, -24, -98, 50, 61, -16, 89, 19, -11, 6, -93, 85, 55, -115, -85, 116,
        12, -41, 102, -57, -117, -71, 119, -66, 65, 122, -123, 110, 108, 66, -4,
        -2, -13, -4, 39, 93, -67, -38, -73


    };
    inflater.setInput(compressedInputBuffer);
    inflater.finished();
    
    outputBuffer = new byte[1327];
    try {
      inflater.inflate(outputBuffer);
    } catch (DataFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    

    return new String(outputBuffer);
  }
  

}
