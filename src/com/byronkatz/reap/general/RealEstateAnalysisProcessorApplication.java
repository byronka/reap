package com.byronkatz.reap.general;


import android.app.Application;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Resources;

public class RealEstateAnalysisProcessorApplication extends Application {

  private static RealEstateAnalysisProcessorApplication singleton;
  private DataController dataController;
  private static Resources resources;
  
  public static final String BASE_VALUES = "base_values";
  private SharedPreferences sharedPreferences;
  
  public static RealEstateAnalysisProcessorApplication getInstance() {
    return singleton;
  }
  
  @Override
  public final void onCreate() {
    
    sharedPreferences = getSharedPreferences(BASE_VALUES, ContextWrapper.MODE_PRIVATE);
    setDataController(new DataController(this, sharedPreferences));
    RealEstateAnalysisProcessorApplication.resources = getResources();
    singleton = this;
    super.onCreate();
  }

    
  public DataController getDataController() {
    return dataController;
  }

  private void setDataController(DataController dataController) {
    this.dataController = dataController;
  }
  
  /**
   * This method provides, to the whole application, an easy way of accessing the resources
   * for the application, such as Strings, xml files, drawable, and so on.
   * @return the Resources object for this application
   */
  public static Resources getAppResources() {
    return resources;
  } 

  
}
