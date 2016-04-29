package com.clusterpriest.analyze.engine;

import com.clusterpriest.filter.log.LogData;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Add a class comment here
 */
public class Engine {
  private String host;
  private Map<String, LinkedList<LogData>> file2RootCause = new HashMap<String, LinkedList<LogData>>();
  private String prediction;
  private String file;
  private String rootCause;

  private static final Map<String, String> RULES = new HashMap<>();
  static {
    RULES.put("java.net.SocketTimeoutException", "Partitions on Kafka broker might become unavailable.");
    RULES.put("kafka.common.LeaderNotAvailableException", "Broker is not available and might lead to prduce/ cpnsume requests failure.");
  }

  public static final Map<String, String> CAUSE_TO_ENG = new HashMap<>();
  static {
    CAUSE_TO_ENG.put("java.net.SocketTimeoutException", "Network failures");
    CAUSE_TO_ENG.put("kafka.common.LeaderNotAvailableException", "Leader not available errors");
  }

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

  public LinkedList<LogData> getRootCauses(String file) {
    return file2RootCause.get(file);
  }

  public void setRootCause(String rootCause) {
    this.rootCause = rootCause;
  }

  public Map<String, LinkedList<LogData>> getFile2RootCause() {
    return file2RootCause;
  }

  public String getPrediction() {
    return prediction;
  }

  public void setPrediction(String prediction) {
    this.prediction = prediction;
  }

  public void addToMap(String file, LogData rootCause) {
    if (file2RootCause.get(file) == null) {
      file2RootCause.put(file, new LinkedList<LogData>());
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

  public boolean hasPrediction() {
    final Set<String> files = getFile2RootCause().keySet();
    for(String file: files) {
      LinkedList<LogData> causes = file2RootCause.get(file);
      removeDuplicates(causes);
      matchesRule(causes);
    }
    return !prediction.isEmpty();
  }

  private void matchesRule(LinkedList<LogData> causes) {
    for (LogData cause: causes) {
      for (String ruleCause: RULES.keySet()) {
        if (cause.rootCause.contains(ruleCause)) {
          prediction = RULES.get(ruleCause);
        }
      }
    }
  }

  private void removeDuplicates(LinkedList<LogData> causes) {
    LogData prev = null;
    for(LogData cause: causes) {
      if (prev != null && prev.rootCause.equals(cause.rootCause)) {
        causes.remove(prev);
      }
      prev = cause;
    }
  }
}
