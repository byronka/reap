package com.byronkatz.reap.activity;

import java.io.File;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.byronkatz.R;
import com.byronkatz.reap.general.CsvAttachment;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.DatabaseAdapter;
import com.byronkatz.reap.general.RealEstateAnalysisProcessorApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;
import com.byronkatz.reap.general.ValueEnum.ValueType;


public class SavedDataBrowserActivity extends ListActivity {

  SimpleCursorAdapter adapter;
  private Cursor cursor;
  private String currentSortString;
  private Boolean isSortAscending;
  private Integer currentSortTypeIndex;
  private static final String SORT_DIRECTION = "SORT_DIRECTION"; 
  private static final String SORTER = "SORTER";
  private static final String SORT_TYPE = "SORT_TYPE";
  CsvAttachment csvAttachment;
  
  boolean mExternalStorageAvailable = false;
  boolean mExternalStorageWriteable = false;

  private String[] from = {
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
      DatabaseAdapter.YEAR_VALUE,
      ValueEnum.INFLATION_RATE.name(),
      ValueEnum.REAL_ESTATE_APPRECIATION_RATE.name(),
      ValueEnum.REQUIRED_RATE_OF_RETURN.name(),
      DatabaseAdapter.MODIFIED_AT
  };

  private int[] to = {
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
      R.id.yearLoadValuesTextView,                
      R.id.inflationRateLoadValuesTextView,
      R.id.rearLoadValuesTextView,
      R.id.rrrLoadValuesTextView,
      R.id.modifiedTimeStampTextView
  };
  
  private final String[] sortTypes = {
      "By Date",
      "By ID",
      "By MIRR",
      "By NPV",
      "By CRCV",
      "By ATCF"
  };

  private final String[] onLongClickFileOptions = {
      "Delete entry",
      "Email entry"
  };

  private ContentValues contentValues;
  private final DataController dataController = 
      RealEstateAnalysisProcessorApplication.getInstance().getDataController();

  @Override
  public void onResume() {
    //by default, sort by id when we start, unless something is saved
    SharedPreferences sp = getPreferences(MODE_PRIVATE);
    currentSortString = sp.getString(SORTER, DatabaseAdapter.KEY_ID);
    currentSortTypeIndex = sp.getInt(SORT_TYPE, 1);
    isSortAscending = sp.getBoolean(SORT_DIRECTION, false);
    cursor = getSortedCursor(currentSortString, sortTypes[currentSortTypeIndex]);
    setupDataBrowser(cursor);
    super.onResume();

  }

  @Override
  public void onPause() {
    SharedPreferences sp = getPreferences(MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();
    editor.clear();
    editor.putString(SORTER, currentSortString);
    editor.putInt(SORT_TYPE, currentSortTypeIndex);
    editor.putBoolean(SORT_DIRECTION, isSortAscending);
    editor.commit();
    super.onPause();

  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.saved_data_browser_container);

    contentValues = new ContentValues();
  }

  private void setupDataBrowser(Cursor cursor) {

    startManagingCursor(cursor);
    adapter = new SimpleCursorAdapter(
        this, R.layout.database_item_browser_layout, cursor, from , to); 
    adapter.setViewBinder(new SavedDataBrowserViewBinder());
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
        displayLongClickOptionsDialog(position);

        return true;
      } 
    });
  }

  @Override
  public boolean onCreateOptionsMenu (Menu menu){
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.saved_data_browser_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected (MenuItem item) {
    super.onOptionsItemSelected(item);

    //the graph visibility parameter does not get used - harmless to include a boolean here
    if (item.getItemId() == R.id.sortByMenuItem) {
      displaySortByDialog();
    }
    return false;
  }

  private void displayLongClickOptionsDialog(final int position) {

    AlertDialog.Builder b = new Builder(this);
    b.setTitle("Entry options");

    b.setItems(onLongClickFileOptions, new AlertDialog.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {

        dialog.dismiss();

        switch(which){
        case 0:    // Delete entry
          createDeleteDialog(position);
          break;
        case 1:    // email entry
          createEmailDialog(position);
          break;
        }
      }

    });

    b.show();
  }

  private void displaySortByDialog() {

    AlertDialog.Builder b = new Builder(this);
    b.setTitle("Sort");

    b.setItems(sortTypes, new AlertDialog.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {

        dialog.dismiss();
        switch(which){
        case 0:
          changeSort(DatabaseAdapter.MODIFIED_AT, which);
          break;
        case 1:
          changeSort(DatabaseAdapter.KEY_ID, which);
          break;
        case 2:
          changeSort(ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN.name(), which);
          break;
        case 3:
          changeSort(ValueEnum.NPV.name(), which);
          break;
        case 4:
          changeSort(ValueEnum.CAP_RATE_ON_PROJECTED_VALUE.name(), which);
          break;
        case 5:
          changeSort(ValueEnum.ATCF.name(), which);
          break;
        }
      }

    });

    b.show();

  }

    private void changeSort(String sorter, Integer index) {

      //if these are already equal to globals, user wants to reverse sort
      if (currentSortString.equals(sorter)) {
        isSortAscending = ! isSortAscending;
      }
      
      //save these values to globals
    currentSortString = sorter;
    currentSortTypeIndex = index;
    
    cursor = getSortedCursor(currentSortString, sortTypes[currentSortTypeIndex]);
    setupDataBrowser(cursor);
    
  }

    /**
     * gets a new sorted cursor.
     * @param sorter the column name in the database which will set the sort in the returned cursor
     * @param nameOfSort The title used to describe the sort, will be set in the activity title
     * @return A sorted cursor
     */
  private Cursor getSortedCursor(String sorter, String nameOfSort) {
    
    Cursor cursor;
    String direction = "";
    
    if (isSortAscending) {
      direction = " ASC";
    } else {
      direction = " DESC";
    }
    
    cursor = dataController.getAllDatabaseValues(sorter + direction);
    String newWindowTitle = "Sort " + nameOfSort;
    setTitle(newWindowTitle);
    
    return cursor;
  }

  private void createEmailDialog(final int position) {
    
    final AlertDialog.Builder emailDialogBuilder = 
        new AlertDialog.Builder(SavedDataBrowserActivity.this);
    cursor.moveToPosition(position);
    final Integer rowNum = cursor.getInt(0);
    String dialogMessage = getString(R.string.emailDialogMessage) + " " + rowNum;
    dialogMessage = addAddressToMessage(dialogMessage);
    emailDialogBuilder.setMessage(dialogMessage + "?");    
    emailDialogBuilder.setTitle(getString(R.string.emailDialogTitle));

    emailDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        emailEntry(position);

      }
    });

    emailDialogBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        //What to do here to cancel?  Nothing?  Yeah I think so.

      }
    });
    emailDialogBuilder.create().show();
  }

  private String createEmailMessage() {

    String body = "";

    ContentValues emailContentValues = new ContentValues();
    DatabaseUtils.cursorRowToContentValues(cursor, emailContentValues);

    
    body += "\n\n";
    if (emailContentValues.getAsString(ValueEnum.STREET_ADDRESS.name()).length() > 0) {
      body += "Address:\n";
      body += "\n";
      body += emailContentValues.getAsString(ValueEnum.STREET_ADDRESS.name()) + "\n";
      body += emailContentValues.getAsString(ValueEnum.CITY.name()) + "\n";
      body += emailContentValues.getAsString(ValueEnum.STATE_INITIALS.name()) + "\n";
      body += "\n\n";
    }
    
    if (emailContentValues.getAsString(ValueEnum.COMMENTS.name()).length() > 0) {

    body += "Comments:";
    body += "\n\n";
    body += emailContentValues.getAsString(ValueEnum.COMMENTS.name()) + "\n\n\n";
    }
    
    String yearValue = emailContentValues.getAsString(DatabaseAdapter.YEAR_VALUE);
    body += "Calculated Values For Year: " + yearValue  + "\n";
 
    body += "\n";
    
    body += getString(ValueEnum.NPV.getTitleText()) + ": " + 
        Utility.parseAndDisplayValue(emailContentValues.getAsString(ValueEnum.NPV.name()), ValueEnum.NPV) + "\n";

    body += getString(ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN.getTitleText()) + ": " +
        Utility.parseAndDisplayValue(emailContentValues.getAsString(ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN.name()), ValueEnum.MODIFIED_INTERNAL_RATE_OF_RETURN) + "\n";
    
    body += getString(ValueEnum.ATCF.getTitleText()) + ": " + 
        Utility.parseAndDisplayValue(emailContentValues.getAsString(ValueEnum.ATCF.name()), ValueEnum.ATCF) + "\n";
    
    body += getString(ValueEnum.ATER.getTitleText()) + ": " + 
        Utility.parseAndDisplayValue(emailContentValues.getAsString(ValueEnum.ATER.name()), ValueEnum.ATER) + "\n";
    
    body += getString(ValueEnum.CAP_RATE_ON_PROJECTED_VALUE.getTitleText()) + ": " + 
        Utility.parseAndDisplayValue(emailContentValues.getAsString(ValueEnum.CAP_RATE_ON_PROJECTED_VALUE.name()), ValueEnum.CAP_RATE_ON_PROJECTED_VALUE) + "\n";
    
    body += getString(ValueEnum.CAP_RATE_ON_PURCHASE_VALUE.getTitleText()) + ": " + 
    Utility.parseAndDisplayValue(emailContentValues.getAsString(ValueEnum.CAP_RATE_ON_PURCHASE_VALUE.name()), ValueEnum.CAP_RATE_ON_PURCHASE_VALUE) + "\n";
    
    body += "\n\n";
    body += "User Input Values\n";
    body += "\n";

    
    for (Map.Entry<String,Object> m : emailContentValues.valueSet()) {

      ValueEnum viewEnum = null;
      //if we can extract a valueEnum, in order to format the value, do so.
      try {
        viewEnum = ValueEnum.valueOf(m.getKey());
      } catch (IllegalArgumentException e) {
        //do nothing - just move on.  This is where we hit things not in valueEnum, like year
      }

      //if we successfully got a ValueEnum, we can use it to format the string
      if ((viewEnum != null) && 
          (viewEnum.getType() != ValueType.STRING) && 
          (! viewEnum.isVaryingByYear())) {
        
        body += getString(viewEnum.getTitleText()) + ": ";
        body += Utility.parseAndDisplayValue(String.valueOf(m.getValue()), viewEnum);
        body += "\n";
        
      }
    }
    
    body += "\n\n";
    
    body += "This data was modified on: " + emailContentValues.getAsString(DatabaseAdapter.MODIFIED_AT);
    body += " and has a REAP Entry id of: " + emailContentValues.getAsString(DatabaseAdapter.KEY_ID);
    
    return body;
  }
  
  /**
   * This is the method that will set up an intent to email the values from the database
   * using the user's standard email program.
   * @param position
   */
  private void emailEntry(int position) {

    final Integer rowNum = cursor.getInt(0);

    //at cursor 9 is the street address
    String subject = "REAP analysis for entry " + rowNum;
    if (cursor.getString(9).length() > 0) {
      subject += " at address " + cursor.getString(9);
    }
    String body = createEmailMessage();

    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    
    emailIntent.setType("multipart/mixed");
    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
    emailIntent = addCsvAttachment(emailIntent, cursor);

    
    startActivity(emailIntent);
    
    

  }
  
  
  private Intent addCsvAttachment(Intent emailIntent, Cursor cursor) {

    createCsvAttachment(cursor);
    Uri uri = Uri.fromFile(new File("/mnt/sdcard/../.."+getFilesDir()+"/"+CsvAttachment.OUTPUT_WORKBOOK));
    emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
    
    return emailIntent;
  }
  
  private void createCsvAttachment(Cursor cursor) {
    csvAttachment = new CsvAttachment(this, cursor);
    csvAttachment.createAttachment();
  }


  private AlertDialog.Builder buildAlertDialog(final int position, int messageText, int titleText) {
    
    final AlertDialog.Builder dialogBuilder = 
        new AlertDialog.Builder(SavedDataBrowserActivity.this);
    cursor.moveToPosition(position);
    final Integer rowNum = cursor.getInt(0);
    String dialogMessage = getString(messageText) + " " + rowNum;
    dialogMessage = addAddressToMessage(dialogMessage);
    dialogBuilder.setMessage(dialogMessage + "?");    
    dialogBuilder.setTitle(getString(titleText));
    
    return dialogBuilder;
  }
  
  private void createDeleteDialog(final int position) {
        
    final AlertDialog.Builder deleteDialogBuilder =
        buildAlertDialog(position, R.string.deleteMessage, R.string.deleteMessageTitle);

    deleteDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        deleteEntry();
      }
    });
    
    deleteDialogBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        //What to do here to cancel?  Nothing?  Yeah I think so.
      }
    });
    deleteDialogBuilder.create().show();
  }
  
  private void deleteEntry() {
    
    final Integer rowNum = cursor.getInt(0);
    int columnIndex = cursor.getColumnIndex(DatabaseAdapter.KEY_ID);
    int rowId = cursor.getInt(columnIndex);
    dataController.removeDatabaseEntry(rowId);

    //if we just deleted the "current" data, then set the current row to an invalid number
    if (rowNum == dataController.getCurrentDatabaseRow()) {
      dataController.setCurrentDatabaseRow(-1);
    }

    String dataDeletedToastText = getString(R.string.dataDeletedToastText);
    Toast toast = Toast.makeText(SavedDataBrowserActivity.this, dataDeletedToastText, Toast.LENGTH_LONG);
    toast.show();

    cursor.requery();
//    deleteDialog.cancel();
  }

  private void createLoadDialog(final int position) {
    
    final AlertDialog.Builder loadDialogBuilder =
        buildAlertDialog(position, R.string.loadTheDataWithId, R.string.loadDataDialogTitle);

    loadDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        loadEntry();
      }
    });
    
    loadDialogBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface dialog, int which) {
        //What to do here to cancel?  Nothing?  Yeah I think so.
      }
    });
    loadDialogBuilder.create().show();    
  }    
//    final Dialog loadDialog = new Dialog(SavedDataBrowserActivity.this);
//
//    Window window = loadDialog.getWindow();
//    window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, 
//        WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
//    loadDialog.setContentView(R.layout.load_database_item_dialog_view);
//    TextView loadTextView = (TextView)loadDialog.findViewById(R.id.load_text);
//
//    cursor.moveToPosition(position);
//    final String rowNum = cursor.getString(0);
//
//    
//    //set the message to ask the user, to confirm they want that row loaded
//    String loadTheDataWithId = getString(R.string.loadTheDataWithId);
//    
//    loadTheDataWithId += " " + rowNum;
//    loadTheDataWithId = addAddressToMessage(loadTheDataWithId);
// 
//
//    loadTextView.setText(loadTheDataWithId + "?");
//
//    String loadDataDialogTitle = getString(R.string.loadDataDialogTitle);
//    loadDialog.setTitle(loadDataDialogTitle);
//
//    Button loadButton = (Button)loadDialog.findViewById(R.id.loadDatabaseItemButton);
//    loadButton.setOnClickListener(new OnClickListener() {
//
//      @Override
//      public void onClick(View v) {
//
////        DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
////        dataController.setCurrentData(contentValues);
////        dataController.setCurrentDatabaseRow(Integer.valueOf(rowNum));
////
////        String dataLoadedToastText = getString(R.string.dataLoadedToastText);
////        Toast toast = Toast.makeText(SavedDataBrowserActivity.this, dataLoadedToastText, Toast.LENGTH_SHORT);
////        toast.show();
//        loadEntry(v);
//        loadDialog.dismiss();
//        finish();
//      }
//    });
//
//    Button cancelButton = (Button)loadDialog.findViewById(R.id.cancelButton);
//    cancelButton.setOnClickListener(new OnClickListener() {
//
//      @Override
//      public void onClick(View v) {
//        loadDialog.cancel();
//
//      }
//    });
//
//    loadDialog.show();
//  }
  
  private void loadEntry() {
    
    final Integer rowNum = cursor.getInt(0);

    DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
    dataController.setCurrentData(contentValues);
    dataController.setCurrentDatabaseRow(Integer.valueOf(rowNum));

    String dataLoadedToastText = getString(R.string.dataLoadedToastText);
    Toast toast = Toast.makeText(SavedDataBrowserActivity.this, dataLoadedToastText, Toast.LENGTH_LONG);
    toast.show();
  }
  
  private String addAddressToMessage(String message) {
    
    //9 is the "address" column
    final String address = cursor.getString(9);
//    Log.d(getClass().getName(), "address: " + address);
    //if this entry has an address, include it
    if (address.length() > 0) {
      message += " at " + address;
      
    }
    
    return message;
  }


}

