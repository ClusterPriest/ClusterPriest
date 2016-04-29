# ClusterPriest

Predict future of cluster by looking at its current state.

### Motivation
* Lesser issues => Happier customers/ users
* Better/ more monitoring and taking actions in real time becomes more important while moving towards cloud model
* Batch oriented diagnostics bundle lacks time sensitive data
* Users will be happy to look at there cluster state if easy way to do so is available

### Real time info on cluster state and Issue prediction
* Forecast issues and take preventive measures in time => Lesser issues
* Issues happening and cluster state => Forecast issues and take preventive measures in time
* Real time info from customers/users => Issues happening and cluster state
* Cluster_State = fn(logs, metrics, configs, cluster_health)
* Series => Issue
* Count(Series => Issue) => Confidence(Series => Issue)
* Confidence(Series => Issue) > threshold => Take preventive measures

### Pipeline
* Modified CM agent to capture Cluster_State to Kafka
* Spark Streaming app pulls from Kafka and performs filtering and data enrichment, pushes the data back to Kafka
* Spark Streaming app pulls from Kafka and performs analysis to generate predictions, puushes them to Kafka
* End applications, mailed daemon, CM UI, etc, can pull predictions from Kafka


This application has been tested on CDH 5.7.1 cluster.

## How to build
The application has two modules.

### filter
*filter* runs a spark streaming app that consumes from a kafka topic, provided in conf file, and filters out
*uninteresting* log messages and enriches data. Interesting well formatted log data is then sent to Kafka, in
a topic specified in conf file.
 
## How to run
```
./gradlew :filter:run -Pargs="<PATH_TO_CONF>"
```

If configuration file is not specified as an argument, then the app uses {{conf/filter.conf}} 
as configuration file by default.

### analyze
*analyze* runs a spark streaming app that consumes from a kafka topic, output topic from filter app,
and performs log correlation on various logs received from a cluster and analyzes them to come up with
probable issues on the cluster. It then sends these predictions along with their causes to Kafka, which
can be consumed by end applications like Cloudera Manager or mailer daemon, etc.
 
## How to run
```
./gradlew :analyze:run -Pargs="<PATH_TO_CONF>"
```

If configuration file is not specified as an argument, then the app uses {{conf/analyze.conf}} 
as configuration file by default.
