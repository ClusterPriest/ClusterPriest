package com.clusterpriest.common.utils;

/**
 * Add a class comment here
 */
public class KeyVal {
  public String message;
  public String key;

  public KeyVal() {
  }

  public KeyVal(String message, String key) {
    this.message = message;
    this.key = key;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public String toString() {
    return "KeyVal{" +
        "message='" + message + '\'' +
        ", key='" + key + '\'' +
        '}';
  }
}