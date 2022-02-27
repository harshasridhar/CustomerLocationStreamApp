package com.example.kf.demo;

import com.example.kf.demo.schema.Customer;
import com.example.kf.demo.schema.CustomerLocation;
import com.example.kf.demo.schema.h2.CustomerEntity;
import com.example.kf.demo.serde.CustomerSerde;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.*;

//confluent-hub install confluentinc/kafka-connect-jdbc:latest

//kafka-console-producer --bootstrap-server localhost:9092 --topic customer-location --property "parse.key=true" --property "key.separator=:"
//confluent local services connect connector status JdbcSinkConnectorConnector_0
@Component
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class StreamsApp {

    private static final Logger logger = LoggerFactory.getLogger(StreamsApp.class);
    private static Customer centroid0, centroid1, centroid2, centroid3, centroid4;

    private  CustomerRepository customerRepository;

    /*
    Centroids - [
       [ 0, 43, 55, 49],
       [ 0, 45, 25, 20],
       [ 0, 32, 86, 82],
       [ 0, 40, 87, 17],
       [ 0, 25, 26, 78]]
     */
    @PostConstruct
    public  void init(){
        centroid0 = new Customer(null, "Female",25,26L,78, "",null);//low-income high-spenders
        centroid1 = new Customer(null, "Female",32,86L,82, "",null);//High-income high-spenders
        centroid2 = new Customer(null, "Female",43,55L,49, "",null);//med-income med-spenders
        centroid3 = new Customer(null, "Female",45,25L,20, "",null);//Low-income low-spenders
        centroid4 = new Customer(null, "Female",40,87L,17, "",null);//High-income low-spenders

        logger.info("Initialization Done");
    }

    private Double getDistance(Customer customer, Customer centroid){
        Double distance= 0.0;
        if (!customer.getGender().equals(centroid.getGender())) {
            distance += 1.0;
        }
        distance += Math.pow(customer.getAge() - centroid.getAge(), 2);
        distance += Math.pow(customer.getAnnualIncome() - centroid.getAnnualIncome(), 2);
        distance += Math.pow(customer.getSpendingScore() - centroid.getSpendingScore(), 2);
        return Math.sqrt(distance);
    }

    private int getClusterForCustomer(CustomerLocation customerLocation){
        List<Double> distances = new ArrayList<>();
        distances.add(getDistance(customerLocation,centroid0));
        distances.add(getDistance(customerLocation,centroid1));
        distances.add(getDistance(customerLocation,centroid2));
        distances.add(getDistance(customerLocation,centroid3));
        distances.add(getDistance(customerLocation,centroid4));
        return distances.indexOf(Collections.min(distances));
    }

    private String getReferralCodeForCustomer(CustomerLocation customerLocation){
        String referralCode = "PEPPYC";
        referralCode += String.valueOf(customerLocation.getCustomerID());
        referralCode += String.valueOf(customerLocation.getGender().charAt(0));
        referralCode += String.valueOf(customerLocation.getAge());
        return referralCode+"M22";
    }

    private String getOfferForCustomer(CustomerLocation customerLocation){
        int clusterId = getClusterForCustomer(customerLocation);
        String offer = "";
        switch (clusterId){
            case 0:
            case 1: offer="Buy the new peppy pizza using your credit card and get flat 15% off"; break;
            case 2: offer="Offers on the new peppy pizza! Buy 2 and get flat 10% off. Buy 3 and get flat 15% off"; break;
            case 3: offer="Buy the new peppy pizza and get 10% off on membership and get member special discounts throughout the year.Use your referral code "+getReferralCodeForCustomer(customerLocation)+" to refer someone and get 10% offer on next peppy pizza order*. *Valid until March 31 2022"; break;
            case 4: offer="Buy the new peppy pizza and get 20% off on membership and get member special discounts throughout the year."; break;
        }
        logger.info("Customer {} offered  - {}",customerLocation.getCustomerID(), offer);
        return offer;
    }

    private Topology getTopology(){
        StreamsBuilder builder = new StreamsBuilder();
        KTable<String, Customer> customerTable = builder.table("pizza_customers",Consumed.with(Serdes.String(), CustomerSerde.customerSerde()));
        builder.stream("customer-location", Consumed.with(Serdes.String(), CustomerSerde.customerLocationSerde()))
                        .filter((name, customerLocation) -> !customerLocation.getRegion().equals(0))
                        .leftJoin(customerTable,(location, customer)->{
                            location.setAge(customer.getAge());
                            location.setAnnualIncome(customer.getAnnualIncome());
                            location.setSpendingScore(customer.getSpendingScore());
                            location.setGender(customer.getGender());
                            try {
                                CustomerEntity customerEntity = new CustomerEntity();
                                BeanUtils.copyProperties(location, customerEntity);
                                customerEntity.setCustomerID(location.getCustomerID());
                                customerEntity.setLastSeenRegion(location.getRegion());
                                customerEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()).toString());
                                customerRepository.save(customerEntity);
                            }catch (Exception ex){
                                logger.error("Exception occurred while saving entity {}",location,ex);
                            }
                            return location;
                        })
                    .to("customer-enriched",Produced.with(Serdes.String(), CustomerSerde.customerLocationSerde()));
        builder.stream("customer-enriched", Consumed.with(Serdes.String(), CustomerSerde.customerLocationSerde()))
                .mapValues((customer)->{
                    customer.setOffer(getOfferForCustomer(customer));
                    return customer;
                })
                .to("customer-offers", Produced.with(Serdes.String(), CustomerSerde.customerLocationSerde()));
        return builder.build();

    }

    private Properties getProperties(){
        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG,"my-streams-app3.4");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        return properties;
    }

    public void start(){
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
