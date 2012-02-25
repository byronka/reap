package com.byronkatz.reap.activity;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
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

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.DatabaseAdapter;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;


public class SavedDataBrowserActivity extends ListActivity {

  private Cursor cursor;
  private ContentValues contentValues;
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.saved_data_browser_container);
    
    
    contentValues = new ContentValues();
    cursor = dataController.getAllDatabaseValues();
    startManagingCursor(cursor);

    ListAdapter adapter = new SimpleCursorAdapter(
        this, // Context.
        R.layout.database_item_browser_layout, 
        cursor,                                              // Pass in the cursor to bind to.
        new String[] {
            DatabaseAdapter.KEY_ID,
            ValueEnum.STREET_ADDRESS.name(), 
            ValueEnum.TOTAL_PURCHASE_VALUE.name(),
            ValueEnum.YEARLY_INTEREST_RATE.name(), 
            ValueEnum.DOWN_PAYMENT.name(), 
            ValueEnum.REQUIRED_RATE_OF_RETURN.name(),
            ValueEnum.ESTIMATED_RENT_PAYMENTS.name(),
            ValueEnum.COMMENTS.name(),
            ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN.name(),
            ValueEnum.NPV.name(),
            ValueEnum.ATCF.name(),
            ValueEnum.ATER.name(),
            ValueEnum.CAP_RATE_ON_PROJECTED_VALUE.name(),
            ValueEnum.CAP_RATE_ON_PURCHASE_VALUE.name(),
            DatabaseAdapter.YEAR_VALUE
            },                                          // Array of cursor columns to bind to.
            new int[] {R.id.dataRowLoadValuesTextView,
                       R.id.streetAddressLoadValuesTextView, 
                       R.id.totalPurchaseLoadValuesTextView, 
                       R.id.yearlyInterestRateLoadValuesTextView, 
                       R.id.downPaymentLoadValuesTextView,
                       R.id.reqRateReturnLoadValuesTextView,
                       R.id.estimatedRentLoadValuesTextView,
                       R.id.commentsLoadValuesTextView,
                       R.id.mirrLoadValuesTextView,
                       R.id.npvLoadValuesTextView,
                       R.id.atcfLoadValuesTextView,
                       R.id.aterLoadValuesTextView,
                       R.id.crcvLoadValuesTextView,
                       R.id.crpvLoadValuesTextView,
                       R.id.yearLoadValuesTextView
                       });  // Parallel array of which template objects to bind to those columns.

    // Bind to our new adapter.
    setListAdapter(adapter);

    getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //set cursor to row that we clicked on

        createLoadDialog(position);
      }
    });
    
    getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

      @Override
      public boolean onItemLongClick(AdapterView<?> arg0, View view, int position,
          long id) {
        createDeleteDialog(position);

        return true;
      } 
    });
        
  }

  private void createDeleteDialog(int position) {
    final Dialog deleteDialog = new Dialog(SavedDataBrowserActivity.this);

    Window window = deleteDialog.getWindow();
    window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, 
        WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
    deleteDialog.setContentView(R.layout.delete_database_item_dialog_view);
    TextView deleteTextView = (TextView)deleteDialog.findViewById(R.id.delete_text);

    cursor.moveToPosition(position);
    String rowNum = cursor.getString(0);

    deleteTextView.setText("Delete the data with entry id: " + rowNum + " ?");
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
  
  private void createLoadDialog(final int position) {
    final Dialog loadDialog = new Dialog(SavedDataBrowserActivity.this);

    Window window = loadDialog.getWindow();
    window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, 
        WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
    loadDialog.setContentView(R.layout.load_database_item_dialog_view);
    TextView loadTextView = (TextView)loadDialog.findViewById(R.id.load_text);

    cursor.moveToPosition(position);
    String rowNum = cursor.getString(0);
    loadTextView.setText("load the data with entry id: " + rowNum + " ?");
    loadDialog.setTitle("Load database item");
    
    Button loadButton = (Button)loadDialog.findViewById(R.id.loadDatabaseItemButton);
    loadButton.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        
        DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
        dataController.setCurrentData(contentValues);
        dataController.setCurrentDatabaseRow(position + 1);
        Toast toast = Toast.makeText(SavedDataBrowserActivity.this, "Data Loaded", Toast.LENGTH_SHORT);
        toast.show();
        loadDialog.dismiss();
        finish();
      }
    });
    
    Button cancelButton = (Button)loadDialog.findViewById(R.id.cancelButton);
    cancelButton.setOnClickListener(new OnClickListener() {
      
      @Override
      public void onClick(View v) {
        loadDialog.cancel();
        
      }
    });
    
    loadDialog.show();
  }

}

