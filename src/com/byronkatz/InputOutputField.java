package com.byronkatz;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InputOutputField {

  private String key;
  private EditText editText;
  private TextView textView;
  private CheckBox checkBox;
  private LinearLayout outerLinearLayout;
  private LinearLayout innerLinearLayout;
  private String value;
  


  
  private InputOutputField() {}
  
  /**
   * 
   * @param context
   * @param description what this field is, succinctly
   * @param dataObject value of this field
   * @return
   */
  public static InputOutputField getRegularInputField(Context context, 
      String description, String value) {
   
    InputOutputField ioField = new InputOutputField();
    ioField.setValue(value);
    ioField.setKey(description);
    ioField.configureOuterLinearLayout(context);
    ioField.configureInputField(context, value);
    ioField.configureInputLabel(context, description);
    ioField.composeRegularView(context);
    return ioField;
  }
  
/**
 * 
 * @param context
 * @param description text of what this field does
 * @param dataObject value of this field
 * @return
 */
  public static InputOutputField getCheckBoxInputField(Context context, 
      String description, String value) {
    
    InputOutputField ioField = new InputOutputField();
    ioField.setValue(value);
    ioField.setKey(description);
    ioField.configureInputLabel(context, description);
    ioField.configureInputField(context, value);
    ioField.configureOuterLinearLayout(context);
    ioField.configureInnerLinearLayout(context);
    ioField.configureCheckBox(context, description); 
    ioField.composeCheckBoxView(context);
    return ioField;
  }
  
  public LinearLayout getOuterLinearLayout() {
    return outerLinearLayout;
  }
  
  private void composeRegularView(Context context) {
    final int FIRST = 0;
    final int SECOND = 1;
    outerLinearLayout.addView(textView, FIRST);
    outerLinearLayout.addView(editText, SECOND);
  }
  
  private void composeCheckBoxView(Context context) {
    final int FIRST = 0;
    final int SECOND = 1;
    innerLinearLayout.addView(textView, FIRST);
    innerLinearLayout.addView(editText, SECOND);
    outerLinearLayout.addView(checkBox, FIRST);
    outerLinearLayout.addView(innerLinearLayout, SECOND);
}

  private void configureInputField(Context context, String hint) {
    editText = new EditText(context);
    
    this.editText.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT ));
    this.editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
    this.editText.setHint(hint);
  }

  private void configureInputLabel(Context context, String description) {
    textView = new TextView(context);
    
    textView.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT ));
    textView.setText(description);
  }

  private void configureCheckBox(Context context, String description) {
    checkBox = new CheckBox(context);
    checkBox.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT ));
    checkBox.setText("customize the " + description);
    checkBox.setOnCheckedChangeListener(new Listener());
  }

  private void configureOuterLinearLayout(Context context) {
    outerLinearLayout = new LinearLayout(context);
    outerLinearLayout.setOrientation(LinearLayout.VERTICAL);
    outerLinearLayout.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT ));
    outerLinearLayout.setVisibility(View.VISIBLE);
  }
  
  private void configureInnerLinearLayout(Context context) {
    innerLinearLayout = new LinearLayout(context);
    innerLinearLayout.setOrientation(LinearLayout.VERTICAL);
    innerLinearLayout.setLayoutParams(new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT ));
    innerLinearLayout.setVisibility(View.GONE);
  }

  private void setKey(String key) {
    this.key = key;
  }
  
  public String getKey() {
    return key;
  }
  
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
  
  public void storeValuesFromInputs() {
    if (editText.getText().toString().length() > 0) {
      value = editText.getText().toString();
    }
  }

  private class Listener implements CompoundButton.OnCheckedChangeListener {
    
  @Override
  public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
    if (isChecked) {
      innerLinearLayout.setVisibility(View.VISIBLE);
    } else {
      innerLinearLayout.setVisibility(View.GONE);
    }
  }
  }
  
}
