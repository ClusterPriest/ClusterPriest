package com.clusterpriest.analyze.engine;

import com.clearspring.analytics.util.Lists;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Add a class comment here
 */
public class State {
  public Set<String> CAUSES = Sets.newHashSet();

  public State() {
    initialize();
  }

  private void initialize() {
    CAUSES.add("java.net.SocketTimeoutException");
  }

  public String predict(Set<String> current) {
    Sets.SetView<String> difference = Sets.difference(current, CAUSES);
    int size = difference.size();
    switch (size) {
      case 0:
        return "Health is good";
      case 1:
        return "Broker Failure Exception";
      case 2:
        return "ZooKeeper Failure Exception";
      default:
        return "Cluster Memory Failure";
    }
  }
}
