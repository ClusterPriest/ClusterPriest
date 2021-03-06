package com.clusterpriest.filter.logfilter;

import com.clusterpriest.filter.log.LogData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Add a class comment here
 */
public class SuspiciousFilter implements IFilter {
  @Override
  public FilterFactory.FILTER_TYPE filterType() {
    return FilterFactory.FILTER_TYPE.SUSPICIOUS;
  }

  @Override
  public LogData doFiltering(LogData logData) {
    if (logData.message.contains("Exception")) {
      Pattern exceptionPattern = Pattern.compile("[^:|^\\s]+Exception{1}");
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