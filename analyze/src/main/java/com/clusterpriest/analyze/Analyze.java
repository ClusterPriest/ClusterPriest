/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.clusterpriest.analyze;

import com.clusterpriest.analyze.engine.Engine;
import com.clusterpriest.analyze.engine.EngineFactory;
import com.clusterpriest.analyze.engine.State;
import com.clusterpriest.common.kafka.KafkaProducerThread;
import com.clusterpriest.common.utils.Context;
import com.clusterpriest.filter.log.LogData;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import kafka.serializer.StringDecoder;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Analyze {
  private static final Logger logger = LoggerFactory.getLogger(Analyze.class);

  private static final String KAFKA_BROKERS = "kafka.brokers";
  private static final String KAFKA_INPUT_TOPIC = "kafka.input.topic";

  private static final String SPARK_APP_NAME = "spark.app.name";
  private static final String SPARK_MASTER = "spark.master";
  private static final String SPARK_BATCH_DURATION = "spark.batch.duration";

  private static KafkaProducerThread producerThread = null;

  public static void main(String[] args) {
    String confFile;
    if (args.length != 1) {
      logger.warn("A config file is expected as argument. Using default file, conf/analyzer.conf");
      confFile = "conf/analyze.conf";
    } else {
      confFile = args[0];
    }
    logger.info("Starting analysis");

    Context context;
    try {
      context = new Context(confFile);
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    // Create context
    SparkConf sparkConf = new SparkConf().setAppName(context.getString(SPARK_APP_NAME));
    sparkConf.setMaster(context.getString(SPARK_MASTER));
    JavaStreamingContext javaStreamingContext = new JavaStreamingContext(sparkConf,
        Durations.seconds(Integer.parseInt(context.getString(SPARK_BATCH_DURATION))));

    final String brokers = context.getString(KAFKA_BROKERS);
    final String input_topic = context.getString(KAFKA_INPUT_TOPIC);

    HashSet<String> topicsSet = new HashSet<String>(Arrays.asList(input_topic.split(",")));
    HashMap<String, String> kafkaParams = new HashMap<String, String>();
    kafkaParams.put("metadata.broker.list", brokers);
    kafkaParams.put("auto.offset.reset", "smallest");

    // Create direct kafka stream with brokers and topics
    JavaPairInputDStream<String, String> messages = KafkaUtils.createDirectStream(
        javaStreamingContext,
        String.class,
        String.class,
        StringDecoder.class,
        StringDecoder.class,
        kafkaParams,
        topicsSet
    );

    if (producerThread == null) {
      producerThread = new KafkaProducerThread(brokers, false);
      producerThread.start();
    }

    // Get the json, split them into words, count the words and print
    JavaDStream<String> json = messages.map(new Function<Tuple2<String, String>, String>() {
      @Override
      public String call(Tuple2<String, String> tuple2) {
        String key = tuple2._1().replace('\'', '\"');
        String value = tuple2._2().replace('\'', '\"');
        logger.info("Rule Builder received " + key + ", " + value);

        Gson gson = new Gson();
        LogData filteredLogData = gson.fromJson(value, LogData.class);
        if (filteredLogData != null) {
          int index = key.indexOf('-');
          if (index != -1) {
            String host = key.substring(0,  index);
            String file = key.substring(key.indexOf(":") + 1);
            final EngineFactory engineFactory = EngineFactory.getInstance();
            engineFactory.addToEngineMap(host, new Engine(host));
            Engine engine = engineFactory.getFromEngineMap(host);
            engine.addToMap(file, filteredLogData);
            if (engine.hasPrediction()) {
              producerThread.addRecord(new ProducerRecord<String, String>("notify_" + host,
                      engine.getRootCauses(file).toString(),
                      "Due to " + engine.CAUSE_TO_ENG.get(filteredLogData.rootCause)
                              + ", you might see " + engine.getPrediction()));
              engine.setPrediction("");
            } else {
              producerThread.addRecord(new ProducerRecord<String, String>("notify_" + host,
                      engine.getRootCauses(file).toString(), ""));

            }
          }
        }
        return value;
      }
    });
    json.print();

    // Start the computation
    javaStreamingContext.start();
    javaStreamingContext.awaitTermination();
  }

  public class KeyVal {
    public String message;
    public String key;

    public KeyVal() {
    }

    public KeyVal(String message, String key) {
      this.message = message;
      this.key = key;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    @Override
    public String toString() {
      return "KeyVal{" +
          "message='" + message + '\'' +
          ", key='" + key + '\'' +
          '}';
    }
  }
}
