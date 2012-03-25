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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.HelpButtonOnClickWrapper;
import com.byronkatz.reap.general.RealEstateAnalysisProcessorApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;

public class DataTable {

  public static final int PROPERTY_LABEL_INDEX = 0;
  public static final int PROPERTY_VALUE_INDEX = 1;

  GraphActivity graphActivity;
  private final DataController dataController = RealEstateAnalysisProcessorApplication
      .getInstance().getDataController();

  public DataTable(GraphActivity graphActivity) {
    this.graphActivity = graphActivity;
  }

  /**
   * loops through the data table and if the valueEnum which corresponds
   * to that tablerow exists in the viewableDataTableRows, then it is made
   * VISIBLE.  Otherwise, it is made GONE.
   * @param valueToDataTableItemCorrespondence
   */
  public void makeSelectedRowsVisible( Map<ValueEnum, TableRow> valueToDataTableItemCorrespondence) {
    Set<ValueEnum> values = dataController.getViewableDataTableRows();
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

 
  
  /**
   * Simple utility method to get an inflater from an activity
   * @param activity the activity giving us the inflater
   * @return a LayoutInflater all set up
   * 
   */
  private LayoutInflater getLayoutInflater(Activity activity) {
    return (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }
  
  /**
   * This is a factory method of sorts.  It puts together all the parts necessary for
   * the tablerow in code, then adds it, in order, to the parent view in the graph activity layout.
   * @param graphActivity the parent activity which this method will add tablerows to.
   * @return a map of valueEnum to each tablerow so we can access each tablerow by enum later
   */
  public Map<ValueEnum, TableRow> createDataTableItems(GraphActivity graphActivity) {

    Map<ValueEnum, TableRow> valueToDataTableItemCorrespondence = new EnumMap<ValueEnum, TableRow> (ValueEnum.class);

    LayoutInflater inflater = getLayoutInflater(graphActivity);
    List<ValueEnum> dataTableValues = new ArrayList<ValueEnum>(Arrays.asList(ValueEnum.values()));
    dataTableValues = Utility.removeCertainItemsFromDataTable(dataTableValues);
    dataTableValues = Utility.sortDataTableValues(graphActivity, dataTableValues);

    TableLayout dataTableLayout = (TableLayout) graphActivity.findViewById(R.id.dataTableLayout);      
    
    //This is where we create the TableLayout
    //main loop to create the data table rows

    for (ValueEnum ve : dataTableValues) {

      //set up the correspondence between the table index and the valueEnums
      TableRow newTableRow = (TableRow) inflater.inflate(R.layout.data_table_tablerow_graph_activity, null);

      //make it invisible to start
      newTableRow.setVisibility(View.GONE);

      valueToDataTableItemCorrespondence.put(ve, newTableRow);

      TextView dataTablePropertyName = (TextView) newTableRow.getChildAt(PROPERTY_LABEL_INDEX);
      //the property name is always a string
      dataTablePropertyName.setText(ve.toString());
      dataTablePropertyName.setOnClickListener(new HelpButtonOnClickWrapper(ve));
      
      TextView dataTablePropertyValue = (TextView) newTableRow.getChildAt(PROPERTY_VALUE_INDEX); 
      dataTablePropertyValue.setOnClickListener(new HelpButtonOnClickWrapper(ve));

      dataTableLayout.addView(newTableRow);
    } //end of main for loop to set dataTableItems

    Set<ValueEnum> viewableDataTableRows = dataController.getViewableDataTableRows();
    setColorDataTableRows(dataTableLayout, viewableDataTableRows);
    return valueToDataTableItemCorrespondence;

  }

  /**
   * Loops through the currently viewableDataTableRows and alternates coloring each row
   */
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
            tempRow.setBackgroundResource(R.color.gray85);
            tempRow.setVisibility(View.VISIBLE);
            alternateColor = ! alternateColor;
          } else {
            tempRow.setBackgroundResource(R.color.gray95);
            tempRow.setVisibility(View.VISIBLE);
            alternateColor = ! alternateColor;
          }
        }
      }
    }
  }


  public void setDataTableValueByInteger(TextView t, ValueEnum ve, Integer year) {
    if (ve.isVaryingByYear()) {
      t.setText(String.valueOf((int) dataController.getCalcValue(ve, year*12)));
    } else if (! ve.isVaryingByYear()) {
      t.setText(String.valueOf((int) dataController.getInputValue(ve)));
    }
  }
  
  public void setDataTableValueByCurrency(TextView t, ValueEnum ve, Integer year) {
    if (ve.isVaryingByYear()) {
      t.setText(Utility.displayCurrency(dataController.getCalcValue(ve, year*12)));
    } else if (! ve.isVaryingByYear()) {
      t.setText(Utility.displayCurrency(dataController.getInputValue(ve)));
    }
  }
  
  public void setDataTableValueByPercentage(TextView t, ValueEnum ve, Integer year) {
    if (ve.isVaryingByYear()) {
      t.setText(Utility.displayShortPercentage(dataController.getCalcValue(ve, year*12)));
    } else if (! ve.isVaryingByYear()) {
      t.setText(Utility.displayShortPercentage(dataController.getInputValue(ve)));
    }
    

  }

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

  public void saveGraphPageData (SharedPreferences sp, Boolean isGraphVisible,
      ValueEnum currentSliderKey, int currentYearSelected) {

    SharedPreferences.Editor editor = sp.edit();

    editor.clear();
    Set<ValueEnum> vdtr = dataController.getViewableDataTableRows(); 

    for (ValueEnum ve : vdtr) {
      editor.putBoolean(ve.name(), true);
    }

    editor.putBoolean(GraphActivity.IS_GRAPH_VISIBLE, isGraphVisible);
    editor.putString(GraphActivity.CURRENT_SLIDER_KEY, currentSliderKey.name());
    editor.putInt(GraphActivity.CURRENT_YEAR_SELECTED, currentYearSelected);
    editor.commit();
  }

  public Set<ValueEnum> restoreViewableDataTableRows(final SharedPreferences sp) {

    Set<ValueEnum> vdtr = new HashSet<ValueEnum>();
    Map<String, ?> entries = sp.getAll();

    if (entries.size() != 0) {

      for (ValueEnum ve : ValueEnum.values()) {
        if (entries.containsKey(ve.name())) {
          vdtr.add(ve);
        }
      }
      //following are the standards for newbies to the program
    } else {
      vdtr.add(ValueEnum.MONTHLY_MORTGAGE_PAYMENT);
      vdtr.add(ValueEnum.ACCUM_INTEREST);
      vdtr.add(ValueEnum.TOTAL_PURCHASE_VALUE);
      vdtr.add(ValueEnum.YEARLY_INTEREST_RATE);
      vdtr.add(ValueEnum.BROKER_CUT_OF_SALE);
      vdtr.add(ValueEnum.PROJECTED_HOME_VALUE);
      vdtr.add(ValueEnum.YEARLY_PRINCIPAL_PAID);
      vdtr.add(ValueEnum.YEARLY_INTEREST_PAID);
      
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

  /**
   * method which actually sets values to each of the items in the data table shown in the graph page
   * @param year year for values we want to see
   * @param valueToDataTableItemCorrespondence map of valueEnums to tableRows in the data table
   */
  public void setDataTableItems( Integer year, Map<ValueEnum, TableRow>
    valueToDataTableItemCorrespondence ) {


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
