package com.example.kf.demo;

import com.example.kf.demo.schema.Customer;
import com.example.kf.demo.serde.CustomerSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
//confluent-hub install confluentinc/kafka-connect-jdbc:latest
public class StreamsApp {

    private static final Logger logger = LoggerFactory.getLogger(StreamsApp.class);

    private static Topology getTopology(){
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Customer> customerStream = builder.stream("pizza_customers",Consumed.with(Serdes.String(), CustomerSerde.customerSerde()));
        builder.stream("customer-location", Consumed.with(Serdes.String(), CustomerSerde.customerLocationSerde()))
                        .filter((name, customerLocation) -> !customerLocation.getRegion().equals(0))
                        .leftJoin(customerStream.toTable(),(location, customer)->{
                            location.setAge(customer.getAge());
                            location.setAnnualIncome(customer.getAnnualIncome());
                            location.setSpendingScore(customer.getSpendingScore());
                            location.setGender(customer.getGender());
                            return location;
                        })
                    .to("customer-enriched",Produced.with(Serdes.String(), CustomerSerde.customerLocationSerde()));
        return builder.build();

    }

    private static Properties getProperties(){
        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,"my-streams-app1");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        return properties;
    }

    public static void start(){
        Topology topology = getTopology();
        logger.info(topology.toString());
        KafkaStreams kafkaStreams = new KafkaStreams(topology, getProperties());
        kafkaStreams.setStateListener(new KafkaStreams.StateListener() {
            @Override
            public void onChange(KafkaStreams.State previousState, KafkaStreams.State newState) {
                logger.info("State changed from {} to {}", previousState, newState);
            }
        });
        kafkaStreams.start();
    }
}
