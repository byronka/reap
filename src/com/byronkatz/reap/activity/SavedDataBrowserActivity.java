package com.byronkatz.reap.activity;

import java.util.Arrays;
import java.util.List;

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
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.DatabaseAdapter;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.Utility;
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

    String[] from = {
        DatabaseAdapter.KEY_ID,
        ValueEnum.STREET_ADDRESS.name(), 
        ValueEnum.TOTAL_PURCHASE_VALUE.name(),
        ValueEnum.YEARLY_INTEREST_RATE.name(), 
        ValueEnum.DOWN_PAYMENT.name(), 
        ValueEnum.ESTIMATED_RENT_PAYMENTS.name(),
        ValueEnum.COMMENTS.name(),
        ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN.name(),
        ValueEnum.NPV.name(),
        ValueEnum.ATCF.name(),
        ValueEnum.ATER.name(),
        ValueEnum.CAP_RATE_ON_PROJECTED_VALUE.name(),
        ValueEnum.CAP_RATE_ON_PURCHASE_VALUE.name(),
        DatabaseAdapter.YEAR_VALUE,
        ValueEnum.INFLATION_RATE.name(),
        ValueEnum.REAL_ESTATE_APPRECIATION_RATE.name(),
        ValueEnum.REQUIRED_RATE_OF_RETURN.name()
    };

    int[] to = {
        R.id.dataRowLoadValuesTextView,             
        R.id.streetAddressLoadValuesTextView,       
        R.id.totalPurchaseLoadValuesTextView,       
        R.id.yearlyInterestRateLoadValuesTextView,  
        R.id.downPaymentLoadValuesTextView,         
        R.id.estimatedRentLoadValuesTextView,       
        R.id.commentsLoadValuesTextView,            
        R.id.mirrLoadValuesTextView,                
        R.id.npvLoadValuesTextView,                 
        R.id.atcfLoadValuesTextView,                
        R.id.aterLoadValuesTextView,                
        R.id.crcvLoadValuesTextView,                
        R.id.crpvLoadValuesTextView,                
        R.id.yearLoadValuesTextView,                
        R.id.inflationRateLoadValuesTextView,
        R.id.rearLoadValuesTextView,
        R.id.rrrLoadValuesTextView
    };

    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.database_item_browser_layout, cursor, from , to); 

    adapter.setViewBinder(new ViewBinder() {


      @Override
      public boolean setViewValue(View view, Cursor cursor, int columnIndex) {


        String columnName = cursor.getColumnName(columnIndex);

        ValueEnum viewEnum = null;
        //here we try to extract a valueEnum from the string value
        try {
          viewEnum = ValueEnum.valueOf(columnName);
        } catch (IllegalArgumentException e) {
//          Log.d(getClass().getName(), "illegalArgumentException at SavedDataBrowser activity");
//          Log.d(getClass().getName(), e.getMessage());
//          Log.d(getClass().getName(), "This is a debug message, to help in programming the application.  Otherwise, ignore.");
          //do nothing
        }

        //if we successfully got a ValueEnum, we can use it to format the string
        if (viewEnum != null) {

          viewEnum = ValueEnum.valueOf(columnName);
          String stringValue = cursor.getString(columnIndex);
          TextView textView = (TextView) view;
//          Log.d(getClass().getName(), "about to parseAndDisplay stringValue: " + stringValue + " viewEnum: " + viewEnum);
          textView.setText(Utility.parseAndDisplayShortValue(stringValue, viewEnum));
          return true;
        }
        return false;
      }
    });

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
    final Integer rowNum = cursor.getInt(0);

    deleteTextView.setText("Delete the data with entry id: " + rowNum + " ?");
    deleteDialog.setTitle("Delete database item");

    Button deleteButton = (Button)deleteDialog.findViewById(R.id.deleteDatabaseItemButton);
    deleteButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        int columnIndex = cursor.getColumnIndex(DatabaseAdapter.KEY_ID);
        int rowId = cursor.getInt(columnIndex);
        dataController.removeDatabaseEntry(rowId);
        
        //if we just deleted the "current" data, then set the current row to an invalid number
        if (rowNum == dataController.getCurrentDatabaseRow()) {
        dataController.setCurrentDatabaseRow(-1);
        }

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

