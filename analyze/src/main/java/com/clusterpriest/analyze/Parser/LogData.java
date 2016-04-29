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
package com.clusterpriest.analyze.Parser;

import com.clusterpriest.analyze.filter.FilterFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogData {
    public Date date;
    public Type type;
    public String fileName;
    public String message;
    public FilterFactory.FILTER_TYPE filterType;

    public LogData(String dateString, String typeString, String fileName, String message) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS,sss");
        this.date = simpleDateFormat.parse(dateString);
        this.type = Type.fromName(typeString);
        this.fileName = fileName;
        this.message = message;
    }

    @Override
    public String toString() {
        return "LogData{" +
            "date=" + date +
            ", type=" + type +
            ", fileName='" + fileName + '\'' +
            ", message='" + message + '\'' +
            ", filterType=" + filterType +
            '}';
    }

    public FilterFactory.FILTER_TYPE getFilterType() {
        return filterType;
    }

    public void setFilterType(FilterFactory.FILTER_TYPE filterType) {
        this.filterType = filterType;
    }

    public enum Type {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR;

        public static Type fromName(String name) {
            switch(name) {
                case "TRACE":
                    return TRACE;
                case "DEBUG":
                    return DEBUG;
                case "INFO":
                    return INFO;
                case "WARN":
                    return WARN;
                case "ERROR":
                    return ERROR;
            }
            return null;
        }
    }
}
