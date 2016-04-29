package com.clusterpriest.filter.logfilter;

import com.clearspring.analytics.util.Lists;
import com.clusterpriest.filter.log.LogData;

import java.util.List;

/**
 * Add a class comment here
 */
public class FilterFactory {
  private List<IFilter> filters = Lists.newArrayList();

  public List<IFilter> getFilters() {
    return filters;
  }

  public void addFilter(IFilter filter) {
    filters.add(filter);
  }

  public LogData filter(LogData logData) {
    LogData filteredLogData = null;
    for (IFilter filter : filters) {
      filteredLogData = filter.doFiltering(logData);
      if (filteredLogData != null) {
        filteredLogData.setFilterType(filter.filterType());
        break;
      }
    }
    return filteredLogData;
  }

  public enum FILTER_TYPE {
    NO,
    ERROR
  }
}
