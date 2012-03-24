package com.byronkatz.reap.general;


import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.byronkatz.R;

//formKey = "dHYzM1FGVl9QZVg5b2F0eDNiRnh6bnc6MQ") points to my google doc


@ReportsCrashes(formKey = "", // will not be used
mailTo = "renaissance.nomad@gmail.com",               
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.crash_toast_text)

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
 // The following line triggers the initialization of ACRA
    ACRA.init(this);

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
