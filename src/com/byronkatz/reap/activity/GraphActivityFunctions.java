package com.byronkatz.reap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;

public class GraphActivityFunctions {

  private static final DataController dataController = RealEstateMarketAnalysisApplication
      .getInstance().getDataController();

  static void switchForMenuItem(MenuItem item, GraphActivity graphActivity) {
    Intent intent = null;
    //which item is selected?
    switch (item.getItemId()) {

    case R.id.configureGraphPageMenuItem:

      intent = new Intent(graphActivity, ConfigureDataTablesActivity.class);
      graphActivity.startActivityForResult(intent, GraphActivity.CONFIGURE_DATA_TABLE_ACTIVITY_REQUEST_CODE);
      break;
    case R.id.editValuesMenuItem:
      intent = new Intent(graphActivity, DataPagesActivity.class);
      graphActivity.startActivity(intent); 
      break;

    case R.id.saveCurrentValuesMenuItem:
      dataController.saveValues();
      Toast toast = Toast.makeText(graphActivity, "Data saved", Toast.LENGTH_SHORT);
      toast.show();
      break;

    case R.id.databaseMenuItem:
      intent = new Intent(graphActivity, SavedDataBrowserActivity.class);
      graphActivity.startActivity(intent); 
      break;
    default:
      //select nothing / do nothing
    }
  }

  
}
