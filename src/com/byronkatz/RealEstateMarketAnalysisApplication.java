package com.byronkatz;

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
    setDataController(new DataController());
    singleton = this;
  }

  public DataController getDataController() {
    return dataController;
  }

  public void setDataController(DataController dataController) {
    this.dataController = dataController;
  }
  
}
