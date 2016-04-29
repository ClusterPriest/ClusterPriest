package com.clusterpriest.analyze.filter;

import com.clusterpriest.analyze.Parser.LogData;

/**
 * Add a class comment here
 */
public interface IFilter {
  public FilterFactory.FILTER_TYPE filterType();

  public LogData doFiltering(LogData data);
}
