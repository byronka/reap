package com.byronkatz;

public class DataItem {
  public static final int NONE = 3;
  public static final int CHECK_BOX = 1;
  public static final int REGULAR = 2;

  private String value;
  private String id;
  private int type;
  
  public DataItem(String id, String value, int type) {
    this.value = value;
    this.id = id;
    this.type = type;
  }
  
  public String getValue() {
    return value;
  }
  
  public String getId() {
    return id;
  }
  
  public int getType() {
    return type;
  }
}