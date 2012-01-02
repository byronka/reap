package com.byronkatz.reap.activity;

import com.byronkatz.R;
import com.byronkatz.R.id;
import com.byronkatz.R.layout;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.DatabaseAdapter;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class SavedDataBrowserActivity extends ListActivity {

  private Cursor cursor;
  private ContentValues contentValues;
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);

    contentValues = new ContentValues();
    cursor = dataController.getAllDatabaseValues();
    startManagingCursor(cursor);

    ListAdapter adapter = new SimpleCursorAdapter(
        this, // Context.
        R.layout.database_item_browser_layout, 
        cursor,                                              // Pass in the cursor to bind to.
        new String[] {ValueEnum.STREET_ADDRESS.name(), 
            ValueEnum.TOTAL_PURCHASE_VALUE.name(),
            ValueEnum.YEARLY_INTEREST_RATE.name(), 
            ValueEnum.DOWN_PAYMENT.name(), 
            ValueEnum.REQUIRED_RATE_OF_RETURN.name()},           // Array of cursor columns to bind to.
            new int[] {R.id.databaseItemTextView1, R.id.databaseItemTextView2, 
            R.id.databaseItemTextView3, R.id.databaseItemTextView4,
            R.id.databaseItemTextView5 });  // Parallel array of which template objects to bind to those columns.



    // Bind to our new adapter.
    setListAdapter(adapter);

    getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //set cursor to row that we clicked on
        cursor.moveToPosition(position);

        DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
        dataController.setCurrentData(contentValues);

        Toast toast = Toast.makeText(SavedDataBrowserActivity.this, "Data loaded", Toast.LENGTH_SHORT);
        toast.show();
        finish();
      }
    });
    
    getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

      @Override
      public boolean onItemLongClick(AdapterView<?> arg0, View view, int position,
          long id) {
        cursor.moveToPosition(position);
        createDialog();

        return true;
      } 
    });
        
  }

  private void createDialog() {
    final Dialog deleteDialog = new Dialog(SavedDataBrowserActivity.this);
    Window window = deleteDialog.getWindow();
    window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, 
        WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
    deleteDialog.setContentView(R.layout.delete_database_item_dialog_view);
    TextView deleteTextView = (TextView)deleteDialog.findViewById(R.id.delete_text);

    deleteTextView.setText("Would you like to delete this entry?");
    deleteDialog.setTitle("Delete database item");
    
    Button deleteButton = (Button)deleteDialog.findViewById(R.id.deleteDatabaseItemButton);
    deleteButton.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        int columnIndex = cursor.getColumnIndex(DatabaseAdapter.KEY_ID);
        int rowId = cursor.getInt(columnIndex);
        dataController.removeDatabaseEntry(rowId);
        
        Toast toast = Toast.makeText(SavedDataBrowserActivity.this, "Data deleted", Toast.LENGTH_SHORT);
        toast.show();
        
        cursor.requery();
        deleteDialog.cancel();
      }
    });
    
    Button cancelButton = (Button)deleteDialog.findViewById(R.id.cancelButton);
    cancelButton.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        deleteDialog.cancel();
        
      }
    });
    
    deleteDialog.show();
  }

}

