package com.clusterpriest.filter.logfilter;

import com.clusterpriest.filter.log.LogData;

/**
 * Add a class comment here
 */
public interface IFilter {
  public FilterFactory.FILTER_TYPE filterType();

  public LogData doFiltering(LogData data);
}
