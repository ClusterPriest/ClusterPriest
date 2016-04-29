package com.clusterpriest.filter.logfilter;

import com.clusterpriest.filter.log.LogData;

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
