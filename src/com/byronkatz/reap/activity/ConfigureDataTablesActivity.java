package com.byronkatz.reap.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.HelpButtonOnClickWrapper;
import com.byronkatz.reap.general.RealEstateAnalysisProcessorApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

public class ConfigureDataTablesActivity extends Activity {

  private TableLayout configDataTableLayout;
  private Set<ValueEnum> viewableDataTableRows;
  Map<ValueEnum, TableRow> valueToDataTableItemCorrespondence;
  private Boolean isGraphVisible;
  CheckBox graphVisibilityCheckbox;
  SharedPreferences graphActivitySharedPrefs;

  private DataController dataController = RealEstateAnalysisProcessorApplication
      .getInstance().getDataController();

  private static final int TOGGLE_BUTTON_INDEX = 1;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.configure_data_tables);
    
    graphActivitySharedPrefs = getSharedPreferences(GraphActivity.PREFS_NAME, 0);
    
    viewableDataTableRows = dataController.getViewableDataTableRows();
    valueToDataTableItemCorrespondence = new EnumMap<ValueEnum, TableRow> (ValueEnum.class);
    configDataTableLayout = (TableLayout) findViewById(R.id.dataTableLayoutConfiguration);
    createDataTableConfiguration();

    graphVisibilityCheckbox = (CheckBox) findViewById (R.id.graphVisibilityCheckbox);
    graphVisibilityCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {


      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isGraphVisible = isChecked;
      }
    });

  }

  @Override
  public void onResume() {
    super.onResume();

    isGraphVisible = graphActivitySharedPrefs.getBoolean(GraphActivity.IS_GRAPH_VISIBLE, true);
    graphVisibilityCheckbox.setChecked(isGraphVisible);
  }

  @Override
  public void onPause() {
    super.onPause();

    TableRow tempTableRow;
    ToggleButton tempToggleButton;
    viewableDataTableRows = new HashSet<ValueEnum>();

    for (Entry<ValueEnum, TableRow> entry  : valueToDataTableItemCorrespondence.entrySet()) {
      tempTableRow = entry.getValue();
      tempToggleButton = (ToggleButton) tempTableRow.getChildAt(TOGGLE_BUTTON_INDEX);
      if (tempToggleButton.isChecked()) {
        viewableDataTableRows.add(entry.getKey());
      }
    }

    packageUpDecisionsFromConfig();
  }

  private void packageUpDecisionsFromConfig() {
    SharedPreferences.Editor editor = graphActivitySharedPrefs.edit();
    editor.clear();
    for (ValueEnum ve : viewableDataTableRows) {
      editor.putBoolean(ve.name(), true);
    }
    editor.putBoolean(GraphActivity.IS_GRAPH_VISIBLE, isGraphVisible);
    editor.commit();
  }
  
  private void createDataTableConfiguration() {

    TextView dataTablePropertyName;

    LayoutInflater inflater = (LayoutInflater)ConfigureDataTablesActivity.this.getSystemService
        (Context.LAYOUT_INFLATER_SERVICE);

    List<ValueEnum> dataTableValues = new ArrayList<ValueEnum>(Arrays.asList(ValueEnum.values()));
    dataTableValues = Utility.removeCertainItemsFromDataTable(dataTableValues);
    dataTableValues = Utility.sortDataTableValues(this, dataTableValues);

    //This is where we create the TableLayout
    //set alternate colors by row
    boolean alternateColor = true;
    //main loop to create the data table rows

    for (ValueEnum ve : dataTableValues) {

      //set up the correspondence between the table index and the valueEnums
      TableRow newTableRow = (TableRow) inflater.inflate(R.layout.data_table_tablerow_configure_activity, null);
      valueToDataTableItemCorrespondence.put(ve, newTableRow);

      ToggleButton toggleButton = (ToggleButton) newTableRow.getChildAt(TOGGLE_BUTTON_INDEX);
      //set toggle buttons by what is in the Set<ValueEnum> viewableDataTableRows
      if (viewableDataTableRows.contains(ve)) {
        toggleButton.setChecked(true);
      } else {
        toggleButton.setChecked(false);
      }

      if (alternateColor) {
        newTableRow.setBackgroundResource(R.color.gray85);
        alternateColor = ! alternateColor;
      } else {
        newTableRow.setBackgroundResource(R.color.gray95);
        alternateColor = ! alternateColor;
      }

      //the property name is always a string
      dataTablePropertyName = (TextView) newTableRow.getChildAt(DataTable.PROPERTY_LABEL_INDEX);
      dataTablePropertyName.setText(ve.toString());
      dataTablePropertyName.setOnClickListener(new HelpButtonOnClickWrapper(ve));


      /* set value based on what type of number it is, or string if 
       * applicable if it is saved to database, that 
       * means we only need the first year, or 
       * "getValueAsDouble(key)" rather than "getValueAsDouble(key, year)"
       */

      //set the map to find these later
      configDataTableLayout.addView(newTableRow);
    } //end of main for loop to set dataTableItems
  }

  public void turnAllButtonsOn(View v) {
    TableRow tempTableRow;
    ToggleButton tempToggleButton;

    for (Entry<ValueEnum, TableRow> entry  : valueToDataTableItemCorrespondence.entrySet()) {
      tempTableRow = entry.getValue();
      tempToggleButton = (ToggleButton) tempTableRow.getChildAt(TOGGLE_BUTTON_INDEX);
      tempToggleButton.setChecked(true);
    }
  }

  public void turnAllButtonsOff(View v) {
    TableRow tempTableRow;
    ToggleButton tempToggleButton;

    for (Entry<ValueEnum, TableRow> entry  : valueToDataTableItemCorrespondence.entrySet()) {
      tempTableRow = entry.getValue();
      tempToggleButton = (ToggleButton) tempTableRow.getChildAt(TOGGLE_BUTTON_INDEX);
      tempToggleButton.setChecked(false);
    }
  }




}
