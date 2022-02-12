package com.example.kf.demo.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.sql.Timestamp;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Customer {

    @JsonProperty
    private Integer CustomerID;

    @JsonProperty
    private String Gender;

    @JsonProperty
    private Integer Age;

    @JsonProperty
    private Long AnnualIncome;

    @JsonProperty
    private Integer SpendingScore;

    @JsonProperty
    private String updateTime;

    @JsonProperty
    private Integer lastSeenRegion;
}
