package com.byronkatz.reap.activity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.HelpButtonOnClickWrapper;
import com.byronkatz.reap.general.RealEstateMarketAnalysisApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

public class DataTable {

  public static final int PROPERTY_LABEL_INDEX = 0;
  public static final int HELP_BUTTON_INDEX = 1;
  public static final int PROPERTY_VALUE_INDEX = 2;
  public static final int TOGGLE_BUTTON_INDEX = 3;
  
  GraphActivity graphActivity;
  private final DataController dataController = RealEstateMarketAnalysisApplication
      .getInstance().getDataController();
  
  public DataTable(GraphActivity graphActivity) {
    this.graphActivity = graphActivity;
  }
  
  public void makeSelectedRowsVisible(Set<ValueEnum> values, Map<ValueEnum, TableRow> valueToDataTableItemCorrespondence) {
    TableRow tempTableRow;

    for (Entry<ValueEnum, TableRow> entry : valueToDataTableItemCorrespondence.entrySet()) {
      tempTableRow = entry.getValue();
      if (values.contains(entry.getKey())) {
        tempTableRow.setVisibility(View.VISIBLE);
      } else if (! values.contains(entry.getKey())) {
        tempTableRow.setVisibility(View.GONE);
      }
    }
    
    colorTheDataTables();

  }
  
  public void colorTheDataTables() {
    TableLayout dataTableLayout = (TableLayout) graphActivity.findViewById(R.id.dataTableLayout);
    Set<ValueEnum> viewableDataTableRows = dataController.getViewableDataTableRows();

    setColorDataTableRows(dataTableLayout, viewableDataTableRows);
  }
  
  public Map<ValueEnum, TableRow> createDataTableItems(GraphActivity graphActivity) {



    ImageButton helpButton;
    TextView dataTablePropertyName;
    Map<ValueEnum, TableRow> valueToDataTableItemCorrespondence = new HashMap<ValueEnum, TableRow> ();

    LayoutInflater inflater = (LayoutInflater) graphActivity.getSystemService
        (Context.LAYOUT_INFLATER_SERVICE);
    ValueEnum[] dataTableValues = ValueEnum.values();

    //get variable
    Set<ValueEnum> viewableDataTableRows = dataController.getViewableDataTableRows();
    TableLayout dataTableLayout = (TableLayout) graphActivity.findViewById(R.id.dataTableLayout);      
    //This is where we create the TableLayout
    //main loop to create the data table rows

    for (ValueEnum ve : dataTableValues) {

      //set up the correspondence between the table index and the valueEnums
      TableRow newTableRow = (TableRow) inflater.inflate(R.layout.data_table_tablerow, null);
      
      //make it invisible to start
      newTableRow.setVisibility(View.GONE);
      
      valueToDataTableItemCorrespondence.put(ve, newTableRow);

      dataTablePropertyName = (TextView) newTableRow.getChildAt(PROPERTY_LABEL_INDEX);

      //the property name is always a string
      dataTablePropertyName.setText(ve.toString());

      helpButton = (ImageButton) newTableRow.getChildAt(HELP_BUTTON_INDEX);
      helpButton.setOnClickListener(new HelpButtonOnClickWrapper(ve));
      
      dataTableLayout.addView(newTableRow);
    } //end of main for loop to set dataTableItems

    setColorDataTableRows(dataTableLayout, viewableDataTableRows);
    return valueToDataTableItemCorrespondence;

  }
  
  public void setColorDataTableRows(TableLayout dataTableLayout, Set<ValueEnum> viewableDataTableRows) {

    boolean alternateColor = true;
    TableRow tempRow = null;

    for (int i = 0; i < dataTableLayout.getChildCount(); i++) {

      tempRow = (TableRow) dataTableLayout.getChildAt(i);

      //Get the ValueEnum associated with the label string
      TextView tempTextView = (TextView) tempRow.getChildAt(PROPERTY_LABEL_INDEX);
      String tempString = tempTextView.getText().toString();

      for (ValueEnum ve : viewableDataTableRows) {
        if (ve.toString() == tempString) {
          if (alternateColor) {
            tempRow.setBackgroundResource(R.color.data_table_row_color_alternate_a);
            tempRow.setVisibility(View.VISIBLE);
            alternateColor = ! alternateColor;
          } else {
            tempRow.setBackgroundResource(R.color.data_table_row_color_alternate_b);
            tempRow.setVisibility(View.VISIBLE);
            alternateColor = ! alternateColor;
          }
        }
      }
    }
  }
  
  
  public void setDataTableValueByInteger(TextView t, ValueEnum ve, Integer year) {
    if (ve.isVaryingByYear()) {
      t.setText(String.valueOf(dataController.getValueAsFloat(ve, year).intValue()));
    } else if (! ve.isVaryingByYear()) {
      t.setText(String.valueOf(dataController.getValueAsFloat(ve).intValue()));
    }
  }

  public void setDataTableValueByCurrency(TextView t, ValueEnum ve, Integer year) {
    if (ve.isVaryingByYear()) {
      t.setText(Utility.displayCurrency(dataController.getValueAsFloat(ve, year)));
    } else if (! ve.isVaryingByYear()) {
      t.setText(Utility.displayCurrency(dataController.getValueAsFloat(ve)));
    }
  }

  public void setDataTableValueByPercentage(TextView t, ValueEnum ve, Integer year) {
    if (ve.isVaryingByYear()) {
      t.setText(Utility.displayPercentage(dataController.getValueAsFloat(ve, year)));
    } else if (! ve.isVaryingByYear()) {
      t.setText(Utility.displayPercentage(dataController.getValueAsFloat(ve)));
    }
  }

  
  //NEW STUFF BELOW!!!
  public void saveViewableDataTableRows(Bundle b) {

    Set<ValueEnum> vdtr = dataController.getViewableDataTableRows(); 
    String[] stringArray = new String[vdtr.size()];

    int i = 0;
    for (ValueEnum ve : vdtr) {
      stringArray[i] = ve.name();
      i++;
    }

    b.putStringArray("viewableDataTableRows", stringArray);

  }
  
  public void saveViewableDataTableRows (SharedPreferences sp) {
    
    SharedPreferences.Editor editor = sp.edit();
    
    Set<ValueEnum> vdtr = dataController.getViewableDataTableRows(); 
    
    for (ValueEnum ve : vdtr) {
      editor.putBoolean(ve.name(), true);
    }
    editor.commit();
  }

  public Set<ValueEnum> restoreViewableDataTableRows(SharedPreferences sp) {
    
    Set<ValueEnum> vdtr = new HashSet<ValueEnum>();
    Map<String, ?> entries = sp.getAll();
    
    for (ValueEnum ve : ValueEnum.values()) {
      if (entries.containsKey(ve.name())) {
        vdtr.add(ve);
      }
    }

    return vdtr;
  }
  
  public Set<ValueEnum> restoreViewableDataTableRows(Bundle b) {

    Set<ValueEnum> tempSet = new HashSet<ValueEnum>();

    String[] stringArray = b.getStringArray("viewableDataTableRows");

    for (String s : stringArray) {
      tempSet.add(ValueEnum.valueOf(s));
    }

    return tempSet;
  }
  
  public void setDataTableItems(ValueEnum[] items, Integer year, Map<ValueEnum, TableRow> valueToDataTableItemCorrespondence ) {


    ValueEnum ve;
    TableRow tempTableRow;
    TextView tempDataTablePropertyValue;

    for (Entry<ValueEnum, TableRow> entry : valueToDataTableItemCorrespondence.entrySet()) {

      ve = entry.getKey();
      tempTableRow = entry.getValue();
      tempDataTablePropertyValue = (TextView) tempTableRow.getChildAt(PROPERTY_VALUE_INDEX);


      switch (ve.getType()) {
      case CURRENCY:

        setDataTableValueByCurrency(tempDataTablePropertyValue, ve, year);
        break;

      case PERCENTAGE:

        setDataTableValueByPercentage(tempDataTablePropertyValue, ve, year);
        break;

      case STRING:

        tempDataTablePropertyValue.setText(dataController.getValueAsString(ve));
        break;

      case INTEGER:
        setDataTableValueByInteger(tempDataTablePropertyValue, ve, year);

        break;        
      default:
        break;
      }
    }

  }

  
}