# Setup
## 1. Download confluent kafka bundle
I have used confluent v7.1.0 <br/>
Download from [here](https://www.confluent.io/installation/) <br/>
OR
```shell
wget https://packages.confluent.io/archive/7.0/confluent-7.0.1.tar.gz
```
## 2. Untar files
```shell
tar -xvf confluent-7.0.1.tar.gz
```
## 3. Set CONFLUENT_HOME and PATH
```shell
cd confluent-7.0.1
export CONFLUENT_HOME=`pwd`
export PATH=$PATH:$CONFLUENT_HOME/bin 
```
You can add these exports to your .bashrc file so that you do not have to do it everytime
Note: In case you are adding to bashrc file make sure you provide the full path inplace of \`pwd\`
## 4. Start services
```shell
confluent local services start
```
Upon successful execution you should see the following lines
```shell
The local commands are intended for a single-node development environment only,
NOT for production usage. https://docs.confluent.io/current/cli/index.html

Using CONFLUENT_CURRENT: /tmp/confluent.503001
Starting ZooKeeper
ZooKeeper is [UP]
Starting Kafka
Kafka is [UP]
Starting Schema Registry
Schema Registry is [UP]
Starting Kafka REST
Kafka REST is [UP]
Starting Connect
Connect is [UP]
Starting ksqlDB Server
ksqlDB Server is [UP]
Starting Control Center
Control Center is [UP]
```
## 5. Install JDBC connector
```shell
confluent-hub install confluentinc/kafka-connect-jdbc:latest
```
## 6. Create topics
```shell
kafka-topics --bootstrap-server localhost:9092 --create --paritiions 1 --replication-factor 1 --topic customer-location
kafka-topics --bootstrap-server localhost:9092 --create --paritiions 1 --replication-factor 1 --topic customer-enriched
kafka-topics --bootstrap-server localhost:9092 --create --paritiions 1 --replication-factor 1 --topic customer-offers
```
## 7. Stop services
```shell
confluent local services stop
```
Upon successful execution you should see the following lines
```shell
The local commands are intended for a single-node development environment only,
NOT for production usage. https://docs.confluent.io/current/cli/index.html

Using CONFLUENT_CURRENT: /tmp/confluent.503001
Stopping Control Center
Control Center is [DOWN]
Stopping ksqlDB Server
ksqlDB Server is [DOWN]
Stopping Connect
Connect is [DOWN]
Stopping Kafka REST
Kafka REST is [DOWN]
Stopping Schema Registry
Schema Registry is [DOWN]
Stopping Kafka
Kafka is [DOWN]
Stopping ZooKeeper
ZooKeeper is [DOWN]
```
