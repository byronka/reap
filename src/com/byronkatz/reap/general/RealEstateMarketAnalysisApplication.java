package com.byronkatz.reap.general;


import android.app.Application;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

public class RealEstateMarketAnalysisApplication extends Application {

  private static RealEstateMarketAnalysisApplication singleton;
  private DataController dataController;
  
  public static final String BASE_VALUES = "base_values";
  private SharedPreferences sharedPreferences;
  
  public static RealEstateMarketAnalysisApplication getInstance() {
    return singleton;
  }
  
  @Override
  public final void onCreate() {
    super.onCreate();
    
    sharedPreferences = getSharedPreferences(BASE_VALUES, ContextWrapper.MODE_PRIVATE);

    setDataController(new DataController(this, sharedPreferences));
    singleton = this;
    
  }

    
  public DataController getDataController() {
    return dataController;
  }

  private void setDataController(DataController dataController) {
    this.dataController = dataController;
  }

  
}
