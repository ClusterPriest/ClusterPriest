/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.clusterpriest.filter.log;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogStringParser {
    private static LogStringParser instance;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LogStringParser.class);

    private LogStringParser() {
    }

    public synchronized static LogStringParser getInstance() {
        if (instance == null) {
            instance = new LogStringParser();
        }

        return instance;
    }

    public LogData parse(String logString) throws ParseException {
        Pattern logPattern = Pattern.compile("(\\S+ \\S+) +(\\S+) +\\s*(\\S+)\\s*\\: +(.*)");
        Matcher matcher = logPattern.matcher(logString);

        boolean found = matcher.find();
        LOG.info("Log string: " + logString + ", matcher found: " + found);
        if (found) {
            return new LogData(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
        }
        return null;
    }
}
