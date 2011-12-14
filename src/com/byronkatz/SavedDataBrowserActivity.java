package com.byronkatz;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class SavedDataBrowserActivity extends Activity {

  private static final int FIRST_POSITION = 1;
  private Cursor cursor;
  private ListView databaseEntriesListView;
  private int streetAddressColumn;
  private int totalPurchaseValueColumn;
  private final DataController dataController = 
      RealEstateMarketAnalysisApplication.getInstance().getDataController();
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.saved_data_browser);
    
    databaseEntriesListView = (ListView) findViewById(R.id.databaseEntriesListView);
    cursor = dataController.getAllDatabaseValues();
    
    streetAddressColumn = cursor.getColumnIndexOrThrow(DatabaseAdapter.STREET_ADDRESS);
    totalPurchaseValueColumn = cursor.getColumnIndexOrThrow(DatabaseAdapter.TOTAL_PURCHASE_VALUE);

    //move the cursor up by two (to get into the actual data)
    cursor.moveToPosition(FIRST_POSITION);
    while (cursor.getPosition() < cursor.getCount()) {
      String streetAddressText      = cursor.getString(streetAddressColumn);
      String totalPurchaseValueText = cursor.getString(totalPurchaseValueColumn);
      LinearLayout newEntry = createRow(streetAddressText, totalPurchaseValueText);
      databaseEntriesListView.addHeaderView(newEntry);
    }
  }
  
  private LinearLayout createRow(String streetAddress, String totalPurchaseValue) {
    
    LinearLayout databaseBrowseRowLinearLayout;
    
    //standard params for the LinearLayout
    ViewGroup.LayoutParams  linearLayoutParams = new ViewGroup.LayoutParams(
        LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
    
    //create the LinearLayout with standards
    databaseBrowseRowLinearLayout = new LinearLayout(this);
    databaseBrowseRowLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
    databaseBrowseRowLinearLayout.setLayoutParams(linearLayoutParams);

    //standard params for the TextViews
    ViewGroup.LayoutParams textViewParams = new ViewGroup.LayoutParams(
        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    
    //create the TextViews to go in the LinearLayout
    TextView streetItemDatabaseBrowse = new TextView(this);
    streetItemDatabaseBrowse.setLayoutParams(textViewParams);
    streetItemDatabaseBrowse.setText(streetAddress);
    
    TextView valueItemDatabaseBrowse = new TextView(this);
    valueItemDatabaseBrowse.setLayoutParams(textViewParams);
    valueItemDatabaseBrowse.setText(totalPurchaseValue);
    
    //add the TextViews to the LinearLayout
    databaseBrowseRowLinearLayout.addView(streetItemDatabaseBrowse, 0);
    databaseBrowseRowLinearLayout.addView(valueItemDatabaseBrowse, 1);
    return databaseBrowseRowLinearLayout;
  }
}
