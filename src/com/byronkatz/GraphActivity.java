package com.byronkatz;

import java.util.Currency;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.byronkatz.ValueEnum.ValueType;

public class GraphActivity extends Activity {

  ValueEnum currentSliderKey;
  AnalysisGraph npvGraph;
  AnalysisGraph aterGraph;
  AnalysisGraph atcfGraph;
  EditText currentValueEditText;
  SeekBar valueSlider;
  SeekBar timeSlider;
  TextView yearDisplayAtSeekBar;
  EditText minValueEditText;
  EditText maxValueEditText;
  static final DataController dataController = RealEstateMarketAnalysisApplication
      .getInstance().getDataController();
  Spinner valueSpinner;
  ArrayAdapter<ValueEnum> spinnerArrayAdapter;
  Integer currentYearMaximum;
  Integer currentYearSelected;
  Button resetButton;
  Float minValueNumeric;
  Float maxValueNumeric;
  Float deltaValueNumeric;
  Float currentValueNumeric;

  static final int DIVISIONS_OF_VALUE_SLIDER = 100;



  @Override
  public boolean onCreateOptionsMenu (Menu menu){
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.data_pages_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected (MenuItem item) {
    super.onOptionsItemSelected(item);
    Intent intent = null;
    //which item is selected?
    switch (item.getItemId()) {
    case R.id.loanMenuItem:
      intent = new Intent(GraphActivity.this, LoanActivity.class);
      startActivity(intent); 
      break;

    case R.id.addressMenuItem:
      intent = new Intent(GraphActivity.this, AddressActivity.class);
      startActivity(intent); 
      break;

    case R.id.taxesMenuItem:
      intent = new Intent(GraphActivity.this, TaxesActivity.class);
      startActivity(intent); 
      break;

    case R.id.saleMenuItem:
      intent = new Intent(GraphActivity.this, SaleActivity.class);
      startActivity(intent); 
      break;

    case R.id.financialEnvironmentMenuItem:
      intent = new Intent(GraphActivity.this, FinancialEnvironmentActivity.class);
      startActivity(intent); 
      break;

    case R.id.rentalMenuItem:
      intent = new Intent(GraphActivity.this, RentalActivity.class);
      startActivity(intent); 
      break;

    case R.id.saveCurrentValuesMenuItem:
      dataController.saveValues();
      Toast toast = Toast.makeText(GraphActivity.this, "Data saved", Toast.LENGTH_SHORT);
      toast.show();
      break;

    case R.id.databaseMenuItem:
      intent = new Intent(GraphActivity.this, SavedDataBrowserActivity.class);
      startActivity(intent); 
      break;
    default:
      //select nothing / do nothing
      return true;
    }

    return false;
  }

  @Override
  public void onResume() {
    super.onResume();
    //necessary in case the user switches between loan types (15 vs. 30 year)
    getNumOfCompoundingPeriods();

    currentSliderKey = spinnerArrayAdapter.getItem(0);
    currentValueNumeric = dataController.getValueAsFloat(currentSliderKey);
    setMinAndMaxFromCurrent();
    valueSlider.setProgress(valueSlider.getMax() / 2);
    timeSlider.setProgress(timeSlider.getMax());

    CalculatedVariables.crunchCalculation();
    createDataPointsOnGraphs();
    invalidateGraphs();
    
    assignValuesToDataTable(currentYearSelected);
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.graph);

    getNumOfCompoundingPeriods();
    setupValueSpinner();
    setupTimeSlider();
    setupValueSlider();
    setupGraphs();
    setupCurrentValueFields();


  }

  private void createDataPointsOnGraphs() {
    aterGraph.createDataPoints();
    atcfGraph.createDataPoints();
    npvGraph.createDataPoints();
  }

  private void invalidateGraphs() {
    aterGraph.invalidate();
    atcfGraph.invalidate();
    npvGraph.invalidate();
  }

  private void highlightCurrentYearOnGraph(Integer currentYearHighlight) {
    aterGraph.setCurrentYearHighlighted(currentYearHighlight);
    atcfGraph.setCurrentYearHighlighted(currentYearHighlight);
    npvGraph.setCurrentYearHighlighted(currentYearHighlight);
  }

  private void setupGraphs() {

    aterGraph = (com.byronkatz.AnalysisGraph) findViewById(R.id.aterFrameLayout);
    atcfGraph = (com.byronkatz.AnalysisGraph) findViewById(R.id.atcfFrameLayout);
    npvGraph = (com.byronkatz.AnalysisGraph) findViewById(R.id.npvFrameLayout);

    invalidateGraphs();
    highlightCurrentYearOnGraph(currentYearSelected);

  }

  private void getNumOfCompoundingPeriods() {
    Float tempFloatValue = dataController.
        getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS) / CalculatedVariables.NUM_OF_MONTHS_IN_YEAR;
    currentYearMaximum = tempFloatValue.intValue();
    currentYearSelected = currentYearMaximum.intValue();
  }

  private void setupCurrentValueFields() {

    resetButton      = (Button)   findViewById(R.id.resetButton);
    currentValueEditText = (EditText) findViewById(R.id.currentValueEditText);
    minValueEditText = (EditText) findViewById(R.id.minValueEditText);
    maxValueEditText = (EditText) findViewById(R.id.maxValueEditText);

    resetButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        valueSlider.setProgress(valueSlider.getMax() / 2);

      }
    });

    currentValueEditText.setOnEditorActionListener(new OnEditorActionListener() {

      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        findViewById(R.id.focusJail).requestFocus();

        return false;
      }
    });

    currentValueEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        ValueType test = currentSliderKey.getType(); 
        String value = null;

        if (hasFocus) {
          switch (test) {
          case CURRENCY:
            DataController.setSelectionOnView(v, ValueType.CURRENCY);
            break;
          case PERCENTAGE:
            DataController.setSelectionOnView(v, ValueType.PERCENTAGE);
            break;
          default:
            System.err.println("Should not get here in currentValueEditText.setOnFocusChangeListener");
            break;
          }

        } else {
          switch (test) {
          case CURRENCY:
            value = currentValueEditText.getText().toString();
            currentValueNumeric = CalculatedVariables.parseCurrency(value);
            currentValueEditText.setText(CalculatedVariables.displayCurrency(currentValueNumeric));
            break;
          case PERCENTAGE:
            value = currentValueEditText.getText().toString();
            currentValueNumeric = CalculatedVariables.parsePercentage(value);
            currentValueEditText.setText(CalculatedVariables.displayPercentage(currentValueNumeric));
            break;
          default:
            System.err.println("Should not get here in currentValueEditText.setOnFocusChangeListener");
            break;
          }
          setMinAndMaxFromCurrent();
          valueSlider.setProgress(valueSlider.getMax() / 2);
          CalculatedVariables.crunchCalculation();

          createDataPointsOnGraphs();

          invalidateGraphs();

          assignValuesToDataTable(currentYearSelected);
        }



      }
    });

    minValueEditText.setOnEditorActionListener(new OnEditorActionListener() {

      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        //put focus on the invisible View - see graph.xml
        findViewById(R.id.focusJail).requestFocus();

        return false;
      }
    });

    minValueEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        ValueType test = currentSliderKey.getType(); 
        String value = null;

        if (hasFocus) {
          switch (test) {
          case CURRENCY:
            DataController.setSelectionOnView(v, ValueType.CURRENCY);
            break;
          case PERCENTAGE:
            DataController.setSelectionOnView(v, ValueType.PERCENTAGE);
            break;
          default:
            System.err.println("Should not get here in currentValueEditText.setOnFocusChangeListener");
            break;
          }

        } else {
          switch (test) {
          case CURRENCY:
            value = minValueEditText.getText().toString();
            minValueNumeric = CalculatedVariables.parseCurrency(value);
            minValueEditText.setText(CalculatedVariables.displayCurrency(minValueNumeric));
            break;
          case PERCENTAGE:
            value = minValueEditText.getText().toString();
            minValueNumeric = CalculatedVariables.parsePercentage(value);
            minValueEditText.setText(CalculatedVariables.displayPercentage(minValueNumeric));
            break;
          default:
            System.err.println("Should not get here in minValueEditText.setOnFocusChangeListener");
            break;
          }
        }
        deltaValueNumeric = maxValueNumeric - minValueNumeric;
      }
    });

    maxValueEditText.setOnEditorActionListener(new OnEditorActionListener() {

      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        findViewById(R.id.focusJail).requestFocus();

        return false;
      }
    });

    maxValueEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        ValueType test = currentSliderKey.getType(); 
        String value = null;

        if (hasFocus) {
          switch (test) {
          case CURRENCY:
            DataController.setSelectionOnView(v, ValueType.CURRENCY);
            break;
          case PERCENTAGE:
            DataController.setSelectionOnView(v, ValueType.PERCENTAGE);
            break;
          default:
            System.err.println("Should not get here in currentValueEditText.setOnFocusChangeListener");
            break;
          }

        } else {
          switch (test) {
          case CURRENCY:
            value = maxValueEditText.getText().toString();
            maxValueNumeric = CalculatedVariables.parseCurrency(value);
            maxValueEditText.setText(CalculatedVariables.displayCurrency(maxValueNumeric));
            break;
          case PERCENTAGE:
            value = maxValueEditText.getText().toString();
            maxValueNumeric = CalculatedVariables.parsePercentage(value);
            maxValueEditText.setText(CalculatedVariables.displayPercentage(maxValueNumeric));
            break;
          default:
            System.err.println("Should not get here in maxValueEditText.setOnFocusChangeListener");
            break;
          }
        }
        deltaValueNumeric = maxValueNumeric - minValueNumeric;
      }
    });

  }

  private void setupValueSlider() {


    valueSlider = (SeekBar) findViewById(R.id.valueSlider);
    valueSlider.setMax(DIVISIONS_OF_VALUE_SLIDER);
    valueSlider.setProgress(valueSlider.getMax() / 2);

    yearDisplayAtSeekBar = (TextView) findViewById(R.id.yearLabel);
    yearDisplayAtSeekBar.setText("Year:\n" + String.valueOf(currentYearMaximum));


    valueSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress,
          boolean fromUser) {


        Float percentageSlid = (progress / (float) DIVISIONS_OF_VALUE_SLIDER);
        Float newCurrentValue = minValueNumeric + (percentageSlid * deltaValueNumeric);

        ValueType test = currentSliderKey.getType(); 

        switch (test) {
        case CURRENCY:
          currentValueEditText.setText(CalculatedVariables.displayCurrency(newCurrentValue));
          break;
        case PERCENTAGE:
          currentValueEditText.setText(CalculatedVariables.displayPercentage(newCurrentValue));
          break;
        default:
          System.err.println("Should not get here in valueSlider.setOnSeekBarChangeListener");
          break;
        }
        dataController.setValueAsFloat(currentSliderKey, newCurrentValue);

        CalculatedVariables.crunchCalculation();

        createDataPointsOnGraphs();

        invalidateGraphs();

        assignValuesToDataTable(currentYearSelected);
      }
    });

  }

  private void setupTimeSlider() {


    timeSlider = (SeekBar) findViewById(R.id.timeSlider);
    timeSlider.setMax(currentYearMaximum);
    timeSlider.setProgress(timeSlider.getMax());

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
        if ( progress > 0 ) {
          yearDisplayAtSeekBar.setText("Year:\n" + String.valueOf(progress));

          currentYearSelected = progress;
          assignValuesToDataTable(currentYearSelected);
          highlightCurrentYearOnGraph(currentYearSelected);
          invalidateGraphs();
        }
      }
    });
  }

  private void setupValueSpinner() {
    valueSpinner = (Spinner) findViewById(R.id.valueSpinner);

    //  ArrayList<ValueEnum> spinnerValuesArray = new ArrayList<ValueEnum>(Arrays.asList(ValueEnum.values()));
    ValueEnum[] selectionValues = {
        ValueEnum.DOWN_PAYMENT,
        ValueEnum.ESTIMATED_RENT_PAYMENTS,
        ValueEnum.FIX_UP_COSTS,
        ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES,
        ValueEnum.REAL_ESTATE_APPRECIATION_RATE,
        ValueEnum.REQUIRED_RATE_OF_RETURN,
        ValueEnum.TOTAL_PURCHASE_VALUE,
        ValueEnum.YEARLY_INTEREST_RATE
    };

    spinnerArrayAdapter = new ArrayAdapter<ValueEnum>(this,
        android.R.layout.simple_spinner_dropdown_item, selectionValues);
    valueSpinner.setAdapter(spinnerArrayAdapter);


    valueSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
          long arg3) {
        currentSliderKey = spinnerArrayAdapter.getItem(pos);
        currentValueNumeric = dataController.getValueAsFloat(currentSliderKey);

        setMinAndMaxFromCurrent();
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
        // do nothing with this. This method is necessary to satisfy interface.

      }
    });

  }

  private void setMinAndMaxFromCurrent() {
    minValueNumeric = currentValueNumeric / 2;
    maxValueNumeric = currentValueNumeric + (currentValueNumeric - minValueNumeric);
    deltaValueNumeric = maxValueNumeric - minValueNumeric;
    ValueType test = currentSliderKey.getType(); 

    switch (test) {
    case CURRENCY:

      maxValueEditText.setText(CalculatedVariables.displayCurrency(maxValueNumeric));
      minValueEditText.setText(CalculatedVariables.displayCurrency(minValueNumeric));
      currentValueEditText.setText(CalculatedVariables.displayCurrency(currentValueNumeric));
      break;
    case PERCENTAGE:

      maxValueEditText.setText(CalculatedVariables.displayPercentage(maxValueNumeric));
      minValueEditText.setText(CalculatedVariables.displayPercentage(minValueNumeric));
      currentValueEditText.setText(CalculatedVariables.displayPercentage(currentValueNumeric));
      break;
    default:
      System.err.println("Should not get here in maxValueEditText.setOnFocusChangeListener");
      break;
    }

  }

  private void assignValuesToDataTable(Integer year) {

    //get connections to gui elements


    String tempStringValue = "";
    TextView tempTextView = null;

    tempTextView = (TextView) findViewById(R.id.yearGridTextView);
    tempStringValue = String.valueOf(currentYearSelected);
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.aterGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.ATER, year));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.atcfGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.ATCF, year));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.npvGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.NPV, year));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.tpvGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.TOTAL_PURCHASE_VALUE));
    tempTextView.setText(tempStringValue);  

    tempTextView = (TextView) findViewById(R.id.yirGridTextView);
    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.YEARLY_INTEREST_RATE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.bvGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.BUILDING_VALUE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.nocpGridTextView);
    tempStringValue = String.valueOf(dataController.getValueAsFloat(ValueEnum.NUMBER_OF_COMPOUNDING_PERIODS).intValue());
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.irGridTextView);
    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.INFLATION_RATE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.dpGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.DOWN_PAYMENT));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.saGridTextView);
    tempStringValue = dataController.getValueAsString(ValueEnum.STREET_ADDRESS);
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.cityGridTextView);
    tempStringValue = dataController.getValueAsString(ValueEnum.CITY);
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.siGridTextView);
    tempStringValue = dataController.getValueAsString(ValueEnum.STATE_INITIALS);
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.erpGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.ESTIMATED_RENT_PAYMENTS));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.rearGridTextView);
    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.REAL_ESTATE_APPRECIATION_RATE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.yhiGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.YEARLY_HOME_INSURANCE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.ptrGridTextView);
    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.PROPERTY_TAX_RATE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.lmfGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.LOCAL_MUNICIPAL_FEES));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.vaclrGridTextView);
    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.VACANCY_AND_CREDIT_LOSS_RATE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.iygeGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.INITIAL_YEARLY_GENERAL_EXPENSES));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.mtrGridTextView);
    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.MARGINAL_TAX_RATE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.sbrGridTextView);
    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.SELLING_BROKER_RATE));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.gseGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.GENERAL_SALE_EXPENSES));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.rrrGridTextView);
    tempStringValue = CalculatedVariables.displayPercentage(dataController.getValueAsFloat(ValueEnum.REQUIRED_RATE_OF_RETURN));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.fucGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.FIX_UP_COSTS));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.ccGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.CLOSING_COSTS));
    tempTextView.setText(tempStringValue);

    //new stuff
    tempTextView = (TextView) findViewById(R.id.tdasGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.TAXES_DUE_AT_SALE, year));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.seGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.SELLING_EXPENSES, year));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.bcGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.BROKER_CUT_OF_SALE, year));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.phvGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.PROJECTED_HOME_VALUE, year));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.tiGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.TAXABLE_INCOME, year));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.yppGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.YEARLY_PRINCIPAL_PAID, year));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.caoGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.CURRENT_AMOUNT_OUTSTANDING, year));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.ygeGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.YEARLY_GENERAL_EXPENSES, year));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.yiGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.YEARLY_INCOME, year));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.yptGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.YEARLY_PROPERTY_TAX, year));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.ympGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.YEARLY_MORTGAGE_PAYMENT));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.mmpGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.MONTHLY_MORTGAGE_PAYMENT));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.aipGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.ACCUM_INTEREST, year));
    tempTextView.setText(tempStringValue);

    tempTextView = (TextView) findViewById(R.id.yipGridTextView);
    tempStringValue = CalculatedVariables.displayCurrency(dataController.getValueAsFloat(ValueEnum.YEARLY_INTEREST_PAID, year));
    tempTextView.setText(tempStringValue);

  }

}
