package com.byronkatz;

import java.util.HashMap;

import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;


public class SavedDataBrowserActivity extends ListActivity {

  private Cursor cursor;
  private ContentValues contentValues;
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
//    setContentView(R.layout.saved_data_browser);

    contentValues = new ContentValues();
    cursor = dataController.getAllDatabaseValues();
    startManagingCursor(cursor);

    ListAdapter adapter = new SimpleCursorAdapter(
        this, // Context.
        android.R.layout.two_line_list_item,  
        cursor,                                              // Pass in the cursor to bind to.
        new String[] {DatabaseAdapter.STREET_ADDRESS, DatabaseAdapter.TOTAL_PURCHASE_VALUE},           // Array of cursor columns to bind to.
        new int[] {android.R.id.text1, android.R.id.text2});  // Parallel array of which template objects to bind to those columns.

    // Bind to our new adapter.
    setListAdapter(adapter);
    
    getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //set cursor to row that we clicked on
        cursor.moveToPosition(position);
        
        DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
        dataController.setContentValues(contentValues);
      }
    });
  }
}

