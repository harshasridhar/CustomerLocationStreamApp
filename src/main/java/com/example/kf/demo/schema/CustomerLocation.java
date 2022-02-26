package com.example.kf.demo.schema;

import com.example.kf.demo.schema.Customer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CustomerLocation extends Customer {

//    @JsonProperty
//    private Integer customerId;

    @JsonProperty
    private String timestamp;

//    @JsonProperty
//    private Double latitude;
//
//    @JsonProperty
//    private Double longitude;

    @JsonProperty
    private Integer region;

    @JsonProperty
    private Double discount;
}
