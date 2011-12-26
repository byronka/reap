package com.byronkatz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DataPagesActivity extends Activity {

  private Button taxes;
  private Button loan;
  private Button sale;
  private Button financialEnvironment;
  private Button rental;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    setContentView(R.layout.data_pages);

  taxes                  = (Button) findViewById(R.id.taxesButton);
  loan                   = (Button) findViewById(R.id.loanButton);
  sale                   = (Button) findViewById(R.id.saleButton);
  financialEnvironment   = (Button) findViewById(R.id.financialEnvironmentButton);
  rental                 = (Button) findViewById(R.id.rentalButton);
  
  taxes.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(View v) {
      Intent intent = new Intent(DataPagesActivity.this, TaxesActivity.class);
      startActivity(intent); 
    }
  });
  
  loan.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(View v) {
      Intent intent = new Intent(DataPagesActivity.this, LoanActivity.class);
      startActivity(intent); 
    }
  });
  
  sale.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(View v) {
      Intent intent = new Intent(DataPagesActivity.this, SaleActivity.class);
      startActivity(intent); 
    }
  });
  
  financialEnvironment.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(View v) {
      Intent intent = new Intent(DataPagesActivity.this, FinancialEnvironmentActivity.class);
      startActivity(intent); 
    }
  });
  
  rental.setOnClickListener(new OnClickListener() {
    @Override
    public void onClick(View v) {
      Intent intent = new Intent(DataPagesActivity.this, RentalActivity.class);
      startActivity(intent); 
    }
  });

  
  }


}
