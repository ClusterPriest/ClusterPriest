package com.clusterpriest.analyze.engine;

import java.util.HashMap;
import java.util.Map;

/**
 * Add a class comment here
 */
public class EngineFactory {
  private static EngineFactory instance;
  private Map<String, Engine> host2EngineMap = new HashMap<String, Engine>();

  public void addToEngineMap(String host, Engine engine) {
    if (host2EngineMap.get(host) == null) {
      host2EngineMap.put(host, engine);
    }
  }

  public Engine getFromEngineMap(String host) {
    return host2EngineMap.get(host);
  }

  public synchronized static EngineFactory getInstance() {
    if (instance == null) {
      instance = new EngineFactory();
    }
    return instance;
  }
}
