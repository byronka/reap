package com.byronkatz.reap.activity;


import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TableRow;
import android.widget.TextView;

import com.byronkatz.reap.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.OnItemSelectedListenerWrapper;
import com.byronkatz.reap.general.RealEstateAnalysisProcessorApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;
import com.byronkatz.reap.general.WrappingSlidingDrawer;
import com.byronkatz.reap.general.ValueEnum.ValueType;

public class GraphActivity extends Activity {

  ValueEnum currentSliderKey;
  EditText currentValueEditText;
  SeekBar valueSlider;
  SeekBar timeSlider;
  TextView yearDisplayAtSeekBar;
  DataTable dataTable;
  public static final String PREFS_NAME = "MyPrefsFile";
  private Boolean isGraphVisible;
  public static final String IS_GRAPH_VISIBLE = "IS_GRAPH_VISIBLE";
  public static final String CURRENT_SLIDER_KEY = "CURRENT_SLIDER_KEY";
  public static final String CURRENT_YEAR_SELECTED = "CURRENT_YEAR_SELECTED";
  private TabHost tabs;
  public static final String MIN_VALUE_NUMERIC      = "MIN_VALUE_NUMERIC";
  public static final String MAX_VALUE_NUMERIC      = "MAX_VALUE_NUMERIC";
  public static final String CURRENT_VALUE_NUMERIC  = "CURRENT_VALUE_NUMERIC";
  public static final String DELTA_VALUE_NUMERIC    = "DELTA_VALUE_NUMERIC";
  public static final String VALUESLIDER_PROGRESS   = "VALUESLIDER_PROGRESS";
  public static final String DRAWER_OPEN            = "DRAWER_OPEN";
  ArrayAdapter<ValueEnum> spinnerArrayAdapter;

  private final DataController dataController = RealEstateAnalysisProcessorApplication
      .getInstance().getDataController();
  Map<ValueEnum, TableRow> valueToDataTableItemCorrespondence;

  Double minValueNumeric;
  Double maxValueNumeric;
  Double deltaValueNumeric;
  Double currentValueNumeric;
  SharedPreferences sp;

  public static final int DIVISIONS_OF_VALUE_SLIDER = 40;

  ValueEnum[] dataTableItems = ValueEnum.values();
  Double percentageSlid;

  @Override
  public boolean onCreateOptionsMenu (Menu menu){
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.graph_page_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected (MenuItem item) {
    super.onOptionsItemSelected(item);

    if (tabs.getVisibility() == View.GONE) {
      isGraphVisible = false;
    } else {
      isGraphVisible = true;
    }

    Utility.switchForMenuItem(item, GraphActivity.this);
    return false;
  }

  private void executeGraphVisibility(Boolean isGraphVisible) {
    if (isGraphVisible) {
      tabs.setVisibility(View.VISIBLE);
    } else {
      tabs.setVisibility(View.GONE);
    }
  }

  /**
   * Algorithm to check what the max year is and change values elsewhere
   * as necessary to reflect the change
   */
  private void checkYearSettingsAtResume() {
    //necessary in case the user switches between loan types (15 vs. 30 year)
    Integer currentYearMaximum = 
        (int) (dataController.getInputValue(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS))/12 + 
        (int) dataController.getInputValue(ValueEnum.EXTRA_YEARS) - 
        1;
    
    Integer currentYearSelected = GraphActivityFunctions.updateTimeSliderAfterChange (timeSlider, currentYearMaximum);
    updateYearDisplayAtSeekBar(currentYearSelected);
    dataController.setCurrentYearSelected(currentYearSelected);
  }
  
  private void getAndApplySharedPreferencesOnResume() {
    
    checkYearSettingsAtResume();
    sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    dataController.setViewableDataTableRows(dataTable.restoreViewableDataTableRows(sp));
    dataTable.makeSelectedRowsVisible( valueToDataTableItemCorrespondence);
    dataTable.setDataTableItems(getCurrentYearSelected(), valueToDataTableItemCorrespondence);

    isGraphVisible = sp.getBoolean(IS_GRAPH_VISIBLE, false);
    executeGraphVisibility(isGraphVisible);
    //set the current current_slider_key, which is shown in the spinner at the top.  If
    //nothing set, then set Closing Costs as the default (it's the first one)
    String temp = sp.getString(CURRENT_SLIDER_KEY, ValueEnum.BUILDING_VALUE.name());
    currentSliderKey = ValueEnum.valueOf(temp);
    setSpinnerSelection(currentSliderKey);
    currentValueNumeric = dataController.getInputValue(currentSliderKey);
  }
  
  @Override
  public void onSaveInstanceState(Bundle outState) {
	  //do nothing!  This damn method has been monkeying
	  //with me.  By default it calls events on all views
	  //in the graph activity to save themselves, but
	  //then when they get restored, all hell breaks loose
	  //because it doesn't restore them in the proper
	  //order.  Better to just halt the stupidity.
  }
  
  @Override
  public void onResume() {
    dataController.calculationsSetValues();
    getAndApplySharedPreferencesOnResume();
    dataTable.colorTheDataTables();
    recalcGraphPage(false);
    //testing line below
    super.onResume();

  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {

    super.onCreate(savedState);
    dataController.calculationsSetValues();
    setContentView(R.layout.graph);
    
    //data table is the table of calculated and input values shown under the graph
    dataTable = new DataTable(this);

    Integer currentYearMaximum = 
        (int) (dataController.getInputValue(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS))/12 + 
        (int) dataController.getInputValue(ValueEnum.EXTRA_YEARS);
    
    //set the current year from storage.  Either use that, or the maximum on the slider.
    //just to make sure, though, let's also make sure that number is kosher before we pop it in.
    sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

    Integer currentYearSelected = sp.getInt(CURRENT_YEAR_SELECTED, currentYearMaximum);
    if (currentYearSelected < 1 || currentYearSelected > currentYearMaximum) {
      currentYearSelected = currentYearMaximum;
    }
    
    setupValueSpinner();
    setupTimeSlider(currentYearMaximum, currentYearSelected);
    setupValueSlider(currentYearMaximum);
    setupGraphs(currentYearMaximum);
    setupCurrentValueFields();
    valueToDataTableItemCorrespondence = 
        dataTable.createDataTableItems(GraphActivity.this);

    setupGraphTabs();
  }

  @Override
  public void onPause() {
    SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
    dataTable.saveGraphPageData(sharedPreferences, isGraphVisible, currentSliderKey, getCurrentYearSelected());

    //Following saves the data to persistence between onPause / onResume
    dataController.saveFieldValues();
    super.onPause();
  }


  private TabHost tabFactory(TabHost tabs, TabHost.TabSpec spec, String text, int tabId, int stringId) {
	    spec = tabs.newTabSpec(text);
	    spec.setContent(tabId);
	    spec.setIndicator(getText(stringId));
	    tabs.addTab(spec);
	    return tabs;
  }
  
  private void setupGraphTabs() {
    tabs = (TabHost) findViewById(android.R.id.tabhost);        
    tabs.setup();
    TabHost.TabSpec spec = null;
    tabs = tabFactory(tabs, spec, "NPV", R.id.tab1, R.string.netPresentValueTabText );
    tabs = tabFactory(tabs, spec, "ATCF", R.id.tab2, R.string.atcfTabText );
    tabs = tabFactory(tabs, spec, "ATER", R.id.tab3, R.string.aterTabText );
    tabs = tabFactory(tabs, spec, "MIRR", R.id.tab4, R.string.modifiedInternalRateOfReturnTabText );
    tabs = tabFactory(tabs, spec, "CRPV", R.id.tab5, R.string.capRateOnPurchaseValueTabText );
    tabs = tabFactory(tabs, spec, "CRCV", R.id.tab6, R.string.capRateOnProjectedValueTabText );
  }

  private void updateYearDisplayAtSeekBar(Integer year) {
    yearDisplayAtSeekBar.setText("Year:\n" + String.valueOf(year+1));
  }

  private void setupGraphs(Integer currentYearMaximum) {
    GraphActivityFunctions.invalidateGraphs(GraphActivity.this);
    GraphActivityFunctions.highlightCurrentYearOnGraph(currentYearMaximum, GraphActivity.this);
  }

  void recalcGraphPage(boolean resetValueSlider) {
    EditText maxValueEditText = (EditText) findViewById (R.id.maxValueEditText);
    EditText minValueEditText = (EditText) findViewById (R.id.minValueEditText);

    minValueNumeric = GraphActivityFunctions.calculateMinFromCurrent(currentValueNumeric);
    maxValueNumeric = GraphActivityFunctions.calculateMaxFromCurrent(currentValueNumeric);
    deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(minValueNumeric, maxValueNumeric);

    GraphActivityFunctions.displayValue(currentValueEditText, currentValueNumeric, currentSliderKey);
    GraphActivityFunctions.displayValue(minValueEditText, minValueNumeric, currentSliderKey);
    GraphActivityFunctions.displayValue(maxValueEditText, maxValueNumeric, currentSliderKey);
    if (resetValueSlider) {
    	forceValueSliderToMidpoint();
    }
  }

private void forceValueSliderToMidpoint() {
	valueSlider.setProgress(0);
    valueSlider.setProgress(valueSlider.getMax() / 2);
}

  private void setupCurrentValueFields() {

    Button resetButton;
    resetButton          = (Button)   findViewById(R.id.resetButton);
    currentValueEditText = (EditText) findViewById(R.id.currentValueEditText);
    final EditText minValueEditText     = (EditText) findViewById(R.id.minValueEditText);
    final EditText maxValueEditText     = (EditText) findViewById(R.id.maxValueEditText);

    resetButton.setOnClickListener(new OnClickListener() {
    public void onClick(View v) {
    	forceValueSliderToMidpoint();
      }
    });
    currentValueEditText.setOnEditorActionListener(new ValueEditTextOnEditorActionListener(this));
    currentValueEditText.setOnFocusChangeListener(new CurrentValueOnFocusChangeListener(this));
    minValueEditText.setOnEditorActionListener(new ValueEditTextOnEditorActionListener(this));
    minValueEditText.setOnFocusChangeListener(new MinValueOnFocusChangeListener(this));
    maxValueEditText.setOnEditorActionListener(new ValueEditTextOnEditorActionListener(this));
    maxValueEditText.setOnFocusChangeListener(new MaxValueOnFocusChangeListener(this));
  }

  private Double changeCurrentValueBasedOnProgress (int progress, ValueEnum currentSliderKey) {
    if (currentSliderKey.getType() == ValueType.INTEGER) {
      return Math.floor(minValueNumeric + ((progress / (double) DIVISIONS_OF_VALUE_SLIDER) * deltaValueNumeric));
    } else {
      return (minValueNumeric + ((progress / (double) DIVISIONS_OF_VALUE_SLIDER) * deltaValueNumeric));
    }
  }


  private void setupValueSlider(Integer currentYearMaximum) {

    valueSlider = (SeekBar) findViewById(R.id.valueSlider);
    valueSlider.setMax(DIVISIONS_OF_VALUE_SLIDER);
    valueSlider.setProgress(valueSlider.getMax() / 2);

    yearDisplayAtSeekBar = (TextView) findViewById(R.id.yearLabel);
    updateYearDisplayAtSeekBar(currentYearMaximum);


    valueSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

      
      public void onStopTrackingTouch(SeekBar seekBar) {
        //empty - do nothing
      }

      public void onStartTrackingTouch(SeekBar seekBar) {
        //empty - do nothing
      }

      public void onProgressChanged(SeekBar seekBar, int progress,
              boolean fromUser) {

            //set the value in the current value field:
            currentValueNumeric = changeCurrentValueBasedOnProgress(progress, currentSliderKey);
            GraphActivityFunctions.displayValue(currentValueEditText, currentValueNumeric, currentSliderKey);

            dataController.putInputValue(currentValueNumeric, currentSliderKey);

            dataController.calculationsSetValues();
            
            GraphActivityFunctions.invalidateGraphs(GraphActivity.this);
            dataTable.setDataTableItems( getCurrentYearSelected(), valueToDataTableItemCorrespondence);

          }
        });

  }

  private void setupTimeSlider(Integer currentYearMaximum, Integer currentYearSelected) {

    timeSlider = (SeekBar) findViewById(R.id.timeSlider);
    timeSlider.setMax(currentYearMaximum - 1);
    timeSlider.setProgress(currentYearSelected);

    timeSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

      public void onStopTrackingTouch(SeekBar seekBar) {
        //do nothing here
      }

      public void onStartTrackingTouch(SeekBar seekBar) {
        //do nothing here
      }

      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        Integer currentYearSelected = getCurrentYearSelected();
        dataController.setCurrentYearSelected(currentYearSelected);

        updateYearDisplayAtSeekBar(currentYearSelected);

        dataTable.setDataTableItems(currentYearSelected, valueToDataTableItemCorrespondence);
        GraphActivityFunctions.highlightCurrentYearOnGraph(currentYearSelected, GraphActivity.this);
        GraphActivityFunctions.invalidateGraphs(GraphActivity.this);
      }
    });

  }

  private void setupValueSpinner() {

    final Spinner valueSpinner = (Spinner) findViewById(R.id.valueSpinner);
    List<ValueEnum> selectionValues = new ArrayList<ValueEnum>();

    ValueEnum.ValueType vt;
    for (ValueEnum v : ValueEnum.values()) {

      vt = v.getType();

      if (!(vt == ValueType.STRING) && (v.isSavedToDatabase())) {
        selectionValues.add(v);
      }

      selectionValues.remove(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS);
      selectionValues.remove(ValueEnum.EXTRA_YEARS);
    }

    //sort the list
    //this will sort alphabetically
    Utility.sortDataTableValues(this, selectionValues);

    spinnerArrayAdapter = new ArrayAdapter<ValueEnum>(this,
        android.R.layout.simple_spinner_dropdown_item, selectionValues);
    valueSpinner.setAdapter(spinnerArrayAdapter);

    valueSpinner.setOnItemSelectedListener(
        new OnItemSelectedListenerWrapper(new OnItemSelectedListener() {
          public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
              long arg3) {

        	GraphActivityFunctions.graphValuedEditorAction(GraphActivity.this, currentValueEditText);
            currentSliderKey = spinnerArrayAdapter.getItem(pos);
            changeInputFieldBasedOnType();
            currentValueNumeric = dataController.getInputValue(currentSliderKey);
            recalcGraphPage(true);
          }

          public void onNothingSelected(AdapterView<?> arg0) {
            // do nothing with this. This method is necessary to satisfy interface.
          }
        }));

  }
  
  /**
   * This method wraps up changing the max length that can be entered in an
   * EditText field for current, max, and min, based on the type of currentsliderkey
   */
  private void changeInputFieldBasedOnType() {
    
    switch (currentSliderKey.getType()) {
    
    case CURRENCY:
      changeAttributesAllValueEditText(20, ValueType.CURRENCY);
      break;
      
    case INTEGER:
      changeAttributesAllValueEditText(3, ValueType.INTEGER);

      break;
    case PERCENTAGE:
      changeAttributesAllValueEditText(6, ValueType.PERCENTAGE);

      break;
      default:
        break;
        
    }
    
  }
  
  /**
   * this simplifies the setting of max digits that can be 
   * entered for current max and min.  It also sets the type
   * of value that can be entered, either decimal (real number)
   * or not (integer)
   * @param length max length, an integer, for each editText
   */
  private void changeAttributesAllValueEditText(int length, ValueType vt) {
    
    EditText maxValueEditText = (EditText) findViewById (R.id.maxValueEditText);
    EditText minValueEditText = (EditText) findViewById (R.id.minValueEditText);
    
    setMaxLengthEditText(currentValueEditText, length);
    setMaxLengthEditText(minValueEditText, length);
    setMaxLengthEditText(maxValueEditText, length);
    
    switch (vt) {
    case CURRENCY:
      setInputTypeDecimal(minValueEditText);
      setInputTypeDecimal(maxValueEditText);
      setInputTypeDecimal(currentValueEditText);
      break;
    case INTEGER:
      setInputTypeNumeric(minValueEditText);
      setInputTypeNumeric(maxValueEditText);
      setInputTypeNumeric(currentValueEditText);
      break;
      
    case PERCENTAGE:
      setInputTypeDecimal(minValueEditText);
      setInputTypeDecimal(maxValueEditText);
      setInputTypeDecimal(currentValueEditText);
      break;
      default:
        break;
    }
  }
  
  private void setInputTypeNumeric(EditText et ) {
    et.setInputType(InputType.TYPE_CLASS_NUMBER);
  }
  
  private void setInputTypeDecimal(EditText et) {
    et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
  }
  
  /**
   * A bit of a hack, used to set the max length of characters for the field in code
   * @param editText the EditText field in question to set length on
   * @param length the length to use, an integer
   */
  private void setMaxLengthEditText(EditText editText, int length) {
    InputFilter[] FilterArray = new InputFilter[1];
    FilterArray[0] = new InputFilter.LengthFilter(length);
    editText.setFilters(FilterArray);  
  }

  /**
   * This method sets the spinner on the graph page to the correct value
   * per the enumerated item supplied to it.  This will call valueSpinner
   * onItemSelected()
   * @param currentSliderKey the ValueEnum which needs to be set as the current value slider key
   */
  private void setSpinnerSelection(ValueEnum currentSliderKey) {
    Spinner valueSpinner = (Spinner) findViewById(R.id.valueSpinner);
    //This will set the current selection from what was in the sharedPreferences save file
    ValueEnum oldCurrentSliderKey = spinnerArrayAdapter.getItem(valueSpinner.getSelectedItemPosition());

    //only if the new selection is different from what is already there.
    //This avoids firing off the background process multiple times, which messes up data
    if (oldCurrentSliderKey != currentSliderKey) {
      valueSpinner.setSelection(spinnerArrayAdapter.getPosition(currentSliderKey));
    }
  }

  private Integer getCurrentYearSelected() {
    return ((SeekBar) findViewById(R.id.timeSlider)).getProgress();

  }
}
