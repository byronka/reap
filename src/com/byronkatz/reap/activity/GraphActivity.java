package com.byronkatz.reap.activity;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.text.InputFilter;
import android.text.InputType;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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

import com.byronkatz.R;
import com.byronkatz.reap.general.DataController;
import com.byronkatz.reap.general.OnItemSelectedListenerWrapper;
import com.byronkatz.reap.general.RealEstateAnalysisProcessorApplication;
import com.byronkatz.reap.general.Utility;
import com.byronkatz.reap.general.ValueEnum;
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

  ArrayAdapter<ValueEnum> spinnerArrayAdapter;

  private final DataController dataController = RealEstateAnalysisProcessorApplication
      .getInstance().getDataController();
  Map<ValueEnum, TableRow> valueToDataTableItemCorrespondence;

  Double minValueNumeric;
  Double maxValueNumeric;
  Double deltaValueNumeric;
  Double currentValueNumeric;
  Double originalCurrentValueNumeric; //for the reset button

  private int test = 0;

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

  private void checkYearSettingsAtResume() {
    //necessary in case the user switches between loan types (15 vs. 30 year)
    Integer currentYearMaximum = 
        (dataController.getInputValue(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS).intValue())/12 + 
        dataController.getInputValue(ValueEnum.EXTRA_YEARS).intValue();
    
    Integer currentYearSelected = GraphActivityFunctions.updateTimeSliderAfterChange (timeSlider, currentYearMaximum);
    updateYearDisplayAtSeekBar(currentYearSelected);
    dataController.setCurrentYearSelected(currentYearSelected);
  }
  
  private void getAndApplySharedPreferencesOnResume() {
    
    sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    dataController.setViewableDataTableRows(dataTable.restoreViewableDataTableRows(sp));
    dataTable.makeSelectedRowsVisible( valueToDataTableItemCorrespondence);
    dataTable.setDataTableItems(getCurrentYearSelected(), valueToDataTableItemCorrespondence);

    isGraphVisible = sp.getBoolean(IS_GRAPH_VISIBLE, false);
    executeGraphVisibility(isGraphVisible);
    //set the current current_slider_key, which is shown in the spinner at the top.  If
    //nothing set, then set Closing Costs as the default (it's the first one)
    String temp = sp.getString(CURRENT_SLIDER_KEY, ValueEnum.CLOSING_COSTS.name());
    currentSliderKey = ValueEnum.valueOf(temp);
    setSpinnerSelection(currentSliderKey);
    currentValueNumeric = dataController.getInputValue(currentSliderKey);

    checkYearSettingsAtResume();
    //following is for the reset button
    originalCurrentValueNumeric = currentValueNumeric;

  }
  
  @Override
  public void onResume() {

    dataController.calculationsSetValues();
    getAndApplySharedPreferencesOnResume();
    dataTable.colorTheDataTables();
    recalcGraphPage();
    //testing line below
    super.onResume();

  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {

    super.onCreate(savedState);
    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    setContentView(R.layout.graph);
    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.my_title);

    //data table is the table of calculated and input values shown under the graph
    dataTable = new DataTable(this);

    Integer currentYearMaximum = 
        (dataController.getInputValue(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS).intValue())/12 + 
        dataController.getInputValue(ValueEnum.EXTRA_YEARS).intValue();
    
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


  @Override
  public void onDestroy() {
    super.onDestroy();
    System.runFinalizersOnExit(true);
  }

  private void setupGraphTabs() {
    tabs = (TabHost) findViewById(android.R.id.tabhost);        
    tabs.setup();

    TabHost.TabSpec spec = tabs.newTabSpec("NPV");
    spec.setContent(R.id.tab1);
    spec.setIndicator(getText(R.string.netPresentValueTabText));
    tabs.addTab(spec);

    spec = tabs.newTabSpec("ATCF");
    spec.setContent(R.id.tab2);
    spec.setIndicator(getText(R.string.atcfTabText));
    tabs.addTab(spec);

    spec = tabs.newTabSpec("ATER");
    spec.setContent(R.id.tab3);
    spec.setIndicator(getText(R.string.aterTabText));
    tabs.addTab(spec);

    spec = tabs.newTabSpec("MIRR");
    spec.setContent(R.id.tab4);
    spec.setIndicator(getText(R.string.modifiedInternalRateOfReturnTabText));
    tabs.addTab(spec);

    spec = tabs.newTabSpec("CRPV");
    spec.setContent(R.id.tab5);
    spec.setIndicator(getText(R.string.capRateOnPurchaseValueTabText));
    tabs.addTab(spec);

    spec = tabs.newTabSpec("CRCV");
    spec.setContent(R.id.tab6);
    spec.setIndicator(getText(R.string.capRateOnProjectedValueTabText));
    tabs.addTab(spec);
  }

  private void updateYearDisplayAtSeekBar(Integer year) {
    yearDisplayAtSeekBar.setText("Year:\n" + String.valueOf(year));

  }


  private void setupGraphs(Integer currentYearMaximum) {

    GraphActivityFunctions.invalidateGraphs(GraphActivity.this);
    GraphActivityFunctions.highlightCurrentYearOnGraph(currentYearMaximum, GraphActivity.this);

  }

  void recalcGraphPage() {
    EditText maxValueEditText = (EditText) findViewById (R.id.maxValueEditText);
    EditText minValueEditText = (EditText) findViewById (R.id.minValueEditText);

    minValueNumeric = GraphActivityFunctions.calculateMinFromCurrent(currentValueNumeric);
    maxValueNumeric = GraphActivityFunctions.calculateMaxFromCurrent(currentValueNumeric);
    deltaValueNumeric = GraphActivityFunctions.calculateMinMaxDelta(minValueNumeric, maxValueNumeric);

    GraphActivityFunctions.displayValue(currentValueEditText, currentValueNumeric, currentSliderKey);
    GraphActivityFunctions.displayValue(minValueEditText, minValueNumeric, currentSliderKey);
    GraphActivityFunctions.displayValue(maxValueEditText, maxValueNumeric, currentSliderKey);
    
    valueSlider.setProgress(0);
    valueSlider.setProgress(valueSlider.getMax() / 2);
  }

  void sendFocusToJail() {
    findViewById(R.id.focusJail).requestFocus();
  }

  private void setupCurrentValueFields() {

    Button resetButton;

    resetButton          = (Button)   findViewById(R.id.resetButton);
    currentValueEditText = (EditText) findViewById(R.id.currentValueEditText);
    final EditText minValueEditText     = (EditText) findViewById(R.id.minValueEditText);
    final EditText maxValueEditText     = (EditText) findViewById(R.id.maxValueEditText);

    resetButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        currentValueNumeric = originalCurrentValueNumeric;
        recalcGraphPage();
      }
    });


    currentValueEditText.setOnEditorActionListener(new ValueEditTextOnEditorActionListener(this));

    currentValueEditText.setOnFocusChangeListener(new CurrentValueOnFocusChangeListener(this));

    minValueEditText.setOnEditorActionListener(new ValueEditTextOnEditorActionListener(this));

    minValueEditText.setOnFocusChangeListener(new MinValueOnFocusChangeListener(this, minValueEditText));

    maxValueEditText.setOnEditorActionListener(new ValueEditTextOnEditorActionListener(this));


    maxValueEditText.setOnFocusChangeListener(new MaxValueOnFocusChangeListener(this, maxValueEditText));

  }

  private Double changeCurrentValueBasedOnProgress (int progress) {

    return (minValueNumeric + ((progress / (double) DIVISIONS_OF_VALUE_SLIDER) * deltaValueNumeric));
  }


  private void setupValueSlider(Integer currentYearMaximum) {

    valueSlider = (SeekBar) findViewById(R.id.valueSlider);
    valueSlider.setMax(DIVISIONS_OF_VALUE_SLIDER);
    valueSlider.setProgress(valueSlider.getMax() / 2);

    yearDisplayAtSeekBar = (TextView) findViewById(R.id.yearLabel);
    updateYearDisplayAtSeekBar(currentYearMaximum);


    valueSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

      
      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        //empty - do nothing
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        //empty - do nothing
      }

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress,
          boolean fromUser) {

        //set the value in the current value field:
        currentValueNumeric = changeCurrentValueBasedOnProgress(progress);

        dataController.putInputValue(currentValueNumeric, currentSliderKey );
        dataController.calculationsSetValues();
        GraphActivityFunctions.displayValue(currentValueEditText, currentValueNumeric, currentSliderKey);
        GraphActivityFunctions.invalidateGraphs(GraphActivity.this);
        dataTable.setDataTableItems( getCurrentYearSelected(), valueToDataTableItemCorrespondence);

      }
    });

  }

  private void setupTimeSlider(Integer currentYearMaximum, Integer currentYearSelected) {

    timeSlider = (SeekBar) findViewById(R.id.timeSlider);
    timeSlider.setMax(currentYearMaximum - 1);
    timeSlider.setProgress(currentYearSelected - 1);

    timeSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        //do nothing here
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        //do nothing here
      }

      @Override
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

          @Override
          public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
              long arg3) {

            //sending focus to jail will make the current value save
            sendFocusToJail();
            currentSliderKey = spinnerArrayAdapter.getItem(pos);
            
            //let's change the number of digits able to enter per the valueEnum type
            changeInputFieldBasedOnType();
            currentValueNumeric = dataController.getInputValue(currentSliderKey);
            //following is for the reset button.  It stores the new current value.
            originalCurrentValueNumeric = currentValueNumeric;

            recalcGraphPage();
          }

          @Override
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
    et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
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
    return ((SeekBar) findViewById(R.id.timeSlider)).getProgress() + 1;

  }
}
