package com.byronkatz.reap.activity;

import java.util.HashMap;
import java.util.HashSet;
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
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.ValueEnum;

public class ConfigureDataTablesActivity extends Activity {

  private TableLayout configDataTableLayout;
  private Set<ValueEnum> viewableDataTableRows;
  Map<ValueEnum, TableRow> valueToDataTableItemCorrespondence;
  private Boolean isGraphVisible;
  CheckBox graphVisibilityCheckbox;

  private DataController dataController = RealEstateMarketAnalysisApplication
      .getInstance().getDataController();



  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.configure_data_tables);
    viewableDataTableRows = dataController.getViewableDataTableRows();
    valueToDataTableItemCorrespondence = new HashMap<ValueEnum, TableRow> ();
    configDataTableLayout = (TableLayout) findViewById(R.id.dataTableLayoutConfiguration);
    createDataTableConfiguration();

    graphVisibilityCheckbox = (CheckBox) findViewById (R.id.graphVisibilityCheckbox);
    graphVisibilityCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {


      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isGraphVisible = isChecked;
        Intent data = new Intent();
        data.putExtra(GraphActivity.IS_GRAPH_VISIBLE, isGraphVisible);
        setResult(RESULT_OK, data);
      }
    });

  }

  @Override
  public void onResume() {
    super.onResume();

    isGraphVisible = getIntent().getExtras().getBoolean(GraphActivity.IS_GRAPH_VISIBLE);
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
      tempToggleButton = (ToggleButton) tempTableRow.getChildAt(DataTable.TOGGLE_BUTTON_INDEX);
      if (tempToggleButton.isChecked()) {
        viewableDataTableRows.add(entry.getKey());
      }
    }
    dataController.setViewableDataTableRows(viewableDataTableRows);

    SharedPreferences.Editor editor = getPreferences(0).edit();
    editor.putBoolean(GraphActivity.IS_GRAPH_VISIBLE, isGraphVisible);
    editor.commit();
  }

  private void createDataTableConfiguration() {


    TextView tempDataTablePropertyValue;
    ToggleButton tempDataTableToggleButton;
    TextView dataTablePropertyName;
    ImageButton tempImageButton;

    LayoutInflater inflater = (LayoutInflater)ConfigureDataTablesActivity.this.getSystemService
        (Context.LAYOUT_INFLATER_SERVICE);
    ValueEnum[] dataTableValues = ValueEnum.values();


    //This is where we create the TableLayout
    //set alternate colors by row
    boolean alternateColor = true;
    //main loop to create the data table rows

    for (ValueEnum ve : dataTableValues) {

      //set up the correspondence between the table index and the valueEnums
      TableRow newTableRow = (TableRow) inflater.inflate(R.layout.data_table_tablerow, null);
      valueToDataTableItemCorrespondence.put(ve, newTableRow);

      ToggleButton toggleButton = (ToggleButton) newTableRow.getChildAt(DataTable.TOGGLE_BUTTON_INDEX);
      //set toggle buttons by what is in the Set<ValueEnum> viewableDataTableRows
      if (viewableDataTableRows.contains(ve)) {
        toggleButton.setChecked(true);
      } else {
        toggleButton.setChecked(false);
      }

      if (alternateColor) {
        newTableRow.setBackgroundResource(R.color.data_table_row_color_alternate_a);
        alternateColor = ! alternateColor;
      } else {
        newTableRow.setBackgroundResource(R.color.data_table_row_color_alternate_b);
        alternateColor = ! alternateColor;
      }

      //the property name is always a string
      dataTablePropertyName = (TextView) newTableRow.getChildAt(DataTable.PROPERTY_LABEL_INDEX);
      dataTablePropertyName.setText(ve.toString());

      tempDataTablePropertyValue = (TextView) newTableRow.getChildAt(DataTable.PROPERTY_VALUE_INDEX);
      tempDataTablePropertyValue.setVisibility(View.GONE);

      tempDataTableToggleButton = (ToggleButton) newTableRow.getChildAt(DataTable.TOGGLE_BUTTON_INDEX);
      tempDataTableToggleButton.setVisibility(View.VISIBLE);

      tempImageButton = (ImageButton) newTableRow.getChildAt(DataTable.HELP_BUTTON_INDEX);
      tempImageButton.setVisibility(View.GONE);

      /* set value based on what type of number it is, or string if 
       * applicable if it is saved to database, that 
       * means we only need the first year, or 
       * "getValueAsFloat(key)" rather than "getValueAsFloat(key, year)"
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
      tempToggleButton = (ToggleButton) tempTableRow.getChildAt(DataTable.TOGGLE_BUTTON_INDEX);
      tempToggleButton.setChecked(true);
    }
  }

  public void turnAllButtonsOff(View v) {
    TableRow tempTableRow;
    ToggleButton tempToggleButton;

    for (Entry<ValueEnum, TableRow> entry  : valueToDataTableItemCorrespondence.entrySet()) {
      tempTableRow = entry.getValue();
      tempToggleButton = (ToggleButton) tempTableRow.getChildAt(DataTable.TOGGLE_BUTTON_INDEX);
      tempToggleButton.setChecked(false);
    }
  }




}
