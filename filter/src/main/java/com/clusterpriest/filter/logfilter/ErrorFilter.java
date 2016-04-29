package com.clusterpriest.filter.logfilter;

import com.clusterpriest.filter.log.LogData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    Pattern exceptionPattern = Pattern.compile("\\S+\\s*Exception\\{1\\}");
    if (logData.type == LogData.Type.ERROR) {
      Matcher matcher = exceptionPattern.matcher(logData.message);
      boolean found = matcher.find();
      if (found) {
        logData.rootCause = matcher.group(0);
      }
      return logData;
    }
    return null;
  }
}
