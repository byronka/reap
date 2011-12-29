package com.byronkatz;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class Utility {

public static void showHelpDialog(int helpText, int helpTitle, Context context) {
    Dialog helpDialog = new Dialog(context);
    Window window = helpDialog.getWindow();
    window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, 
        WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
    helpDialog.setContentView(R.layout.help_dialog_view);
    TextView helpTextView = (TextView)helpDialog.findViewById(R.id.help_text);
    helpTextView.setText(helpText);
    helpDialog.setTitle(helpTitle);
    helpDialog.show();
  }


}
