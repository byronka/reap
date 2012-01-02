package com.byronkatz;

import com.byronkatz.reap.general.DataController;

import android.app.Application;

public class RealEstateMarketAnalysisApplication extends Application {

  private static RealEstateMarketAnalysisApplication singleton;
  private DataController dataController;
  
  public static RealEstateMarketAnalysisApplication getInstance() {
    return singleton;
  }
  
  @Override
  public final void onCreate() {
    super.onCreate();
    setDataController(new DataController(this));
    singleton = this;
    
  }

    
  public DataController getDataController() {
    return dataController;
  }

  private void setDataController(DataController dataController) {
    this.dataController = dataController;
  }

  
}
