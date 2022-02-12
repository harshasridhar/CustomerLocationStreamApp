package com.example.kf.demo.serde;

import com.example.kf.demo.schema.Customer;
import com.example.kf.demo.schema.CustomerLocation;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class CustomerSerde<T>  {

    public static Serde<CustomerLocation> customerLocationSerde(){
        JsonSerializer<CustomerLocation> customerLocationJsonSerializer = new JsonSerializer<>();
        JsonDeserializer<CustomerLocation> customerLocationJsonDeserializer = new JsonDeserializer<>(CustomerLocation.class);
        return Serdes.serdeFrom(customerLocationJsonSerializer, customerLocationJsonDeserializer);
    }

    public static Serde<Customer> customerSerde(){
        JsonSerializer<Customer> customerLocationJsonSerializer = new JsonSerializer<>();
        JsonDeserializer<Customer> customerLocationJsonDeserializer = new JsonDeserializer<>(Customer.class);
        return Serdes.serdeFrom(customerLocationJsonSerializer, customerLocationJsonDeserializer);
    }
}
