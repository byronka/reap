package com.byronkatz.reap.activity;

import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

import android.database.Cursor;
import android.view.View;
import android.widget.TextView;


public class SavedDataBrowserViewBinder implements android.widget.SimpleCursorAdapter.ViewBinder {

  @Override
  public boolean setViewValue(View view, Cursor cursor, int columnIndex) {



      String columnName = cursor.getColumnName(columnIndex);

      ValueEnum viewEnum = null;
      //here we try to extract a valueEnum from the string value
      try {
        viewEnum = ValueEnum.valueOf(columnName);
      } catch (IllegalArgumentException e) {
//        Log.d(getClass().getName(), "illegalArgumentException at SavedDataBrowser activity");
//        Log.d(getClass().getName(), e.getMessage());
//        Log.d(getClass().getName(), "This is a debug message, to help in programming the application.  Otherwise, ignore.");
        //do nothing
      }

      //if we successfully got a ValueEnum, we can use it to format the string
      if (viewEnum != null) {

        viewEnum = ValueEnum.valueOf(columnName);
        String stringValue = cursor.getString(columnIndex);
        TextView textView = (TextView) view;
//        Log.d(getClass().getName(), "about to parseAndDisplay stringValue: " + stringValue + " viewEnum: " + viewEnum);
        textView.setText(Utility.parseAndDisplayShortValue(stringValue, viewEnum));
        return true;
      }
      return false;

  }

}
