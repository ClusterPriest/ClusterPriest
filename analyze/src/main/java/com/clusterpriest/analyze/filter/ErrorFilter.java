package com.clusterpriest.analyze.filter;

import com.clusterpriest.analyze.Parser.LogData;

/**
 * Add a class comment here
 */
public class ErrorFilter implements IFilter {
  @Override
  public FilterFactory.FILTER_TYPE filterType() {
    return FilterFactory.FILTER_TYPE.ERROR;
  }

  @Override
  public LogData doFiltering(LogData logData) {
    if (logData.type == LogData.Type.INFO) {
      return logData;
    }
    return null;
  }
}
