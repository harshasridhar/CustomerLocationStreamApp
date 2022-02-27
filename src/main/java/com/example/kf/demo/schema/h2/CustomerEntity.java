package com.example.kf.demo.schema.h2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "customer_location")
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer CustomerID;

    @Column
    private String Gender;

    @Column
    private Integer Age;

    @Column
    private Long AnnualIncome;

    @Column
    private Integer SpendingScore;

    @Column
    private String updateTime;

    @Column
    private Integer lastSeenRegion;
}
