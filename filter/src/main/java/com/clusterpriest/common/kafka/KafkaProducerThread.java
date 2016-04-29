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

package com.clusterpriest.common.kafka;

import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaProducerThread extends Thread
{
    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducerThread.class);
    private static final ConcurrentLinkedQueue<ProducerRecord<String, String>> QUEUE = new ConcurrentLinkedQueue<ProducerRecord<String, String>>();
    private static AtomicBoolean isRunning = new AtomicBoolean(false);
    private final KafkaProducer<String, String> producer;
    private final Boolean isAsync;

    public KafkaProducerThread(String brokers, Boolean isAsync)
    {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "SmokesKafkaProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.ACKS_CONFIG, "-1");
        props.put(ProducerConfig.RETRIES_CONFIG, "3");
        if(isAsync) {
            props.put(ProducerConfig.LINGER_MS_CONFIG, "10");
        }

        this.producer = new KafkaProducer<String, String>(props);
        if (this.producer == null)
            LOG.info("Failed to create KafkaProducer");
        this.isAsync = isAsync;
    }

    @Override
    public void run() {
        isRunning.compareAndSet(false, true);
        while(isRunning.get())
        {
            long startTime = System.currentTimeMillis();
            ProducerRecord<String, String> producerRecord = QUEUE.poll();
            if (producerRecord == null)
                continue;
            if (isAsync) { // Send asynchronously
                producer.send(producerRecord, new ProduceCallBack(startTime, producerRecord.key(), producerRecord.value()));
            } else { // Send synchronously
                try {
                    producer.send(producerRecord).get();
                    LOG.info("Sent message: " + producerRecord);
                } catch (InterruptedException e) {
                    LOG.error("Failed to send message.", e);
                    shutdown();
                    break;
                } catch (ExecutionException e) {
                    LOG.error("Failed to send message.", e);
                    shutdown();
                    break;
                }
            }
        }
    }

    public void shutdown() {
        producer.flush();
        isRunning.compareAndSet(true, false);
        producer.close(6000, TimeUnit.MILLISECONDS);
        QUEUE.clear();
    }

    public void addRecord(ProducerRecord<String, String> producerRecord) {
        QUEUE.add(producerRecord);
    }

    public Boolean isEmpty() {
        return QUEUE.isEmpty();
    }

    public static boolean isRunning() {
        return isRunning.get();
    }
}

class ProduceCallBack implements Callback {

    private long startTime;
    private String key;
    private String message;
    private static final Logger LOG = LoggerFactory.getLogger(ProduceCallBack.class);

    public ProduceCallBack(long startTime, String key, String message) {
        this.startTime = startTime;
        this.key = key;
        this.message = message;
    }

    /**
     * A callback method the user can implement to provide asynchronous handling of request completion. This method will
     * be called when the record sent to the server has been acknowledged. Exactly one of the arguments will be
     * non-null.
     *
     * @param metadata  The metadata for the record that was sent (i.e. the partition and offset). Null if an error
     *                  occurred.
     * @param exception The exception thrown during processing of this record. Null if no error occurred.
     */
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (metadata != null) {
            LOG.info(
                    "message(" + key + ", " + message + ") sent to partition(" + metadata.partition() +
                            "), " +
                            "offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
        } else {
            LOG.error("Failed to send message.", exception);
        }
    }
}
