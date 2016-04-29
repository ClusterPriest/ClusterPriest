package com.clusterpriest.filter.engine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Add a class comment here
 */
public class Engine {
  private String host;
  private Map<String, LinkedList<String>> file2RootCause = new HashMap<String, LinkedList<String>>();
  private String prediction;
  private String file;
  private String rootCause;

  public Engine() {
  }

  public Engine(String host, String file, String rootCause) {
    this.host = host;
    this.file = file;
    this.rootCause = rootCause;
  }

  public Engine(String host) {
    this.host = host;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

  public LinkedList<String> getRootCauses(String file) {
    return file2RootCause.get(file);
  }

  public void setRootCause(String rootCause) {
    this.rootCause = rootCause;
  }

  public Map<String, LinkedList<String>> getFile2RootCause() {
    return file2RootCause;
  }

  public String getPrediction() {
    return prediction;
  }

  public void setPrediction(String prediction) {
    this.prediction = prediction;
  }

  public void addToMap(String file, String rootCause) {
    if (file2RootCause.get(file) == null) {
      file2RootCause.put(file, new LinkedList<String>());
    }
    file2RootCause.get(file).add(rootCause);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Engine engine = (Engine) o;

    return host != null ? host.equals(engine.host) : engine.host == null;

  }

  @Override
  public int hashCode() {
    return host != null ? host.hashCode() : 0;
  }
}
