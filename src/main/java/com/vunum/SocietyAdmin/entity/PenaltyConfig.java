package com.vunum.SocietyAdmin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PenaltyConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String penaltyType;

    @Column(nullable = false)
    private Double interestRate;

    @Column(nullable = false)
    private Double fixedPenaltyAmount;

    @Column(nullable = false)
    private Boolean isPercentage;

    private int gracePeriod;

    private String description;
}
