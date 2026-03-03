package com.vunum.SocietyAdmin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Consumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Building building;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

    @Column(nullable = true)
    private Double unitsConsumed;

    private String billingMonth;

    @Enumerated(EnumType.STRING)
    private source source;

    private Boolean billGenerated;

    @Enumerated(EnumType.STRING)
    private type type;

    public String getName() {
        return this.user.getFirstName() + "_" + this.getSource() + "_" + this.getType() + "_" + this.getBillingMonth();
    }

    public enum source {
        GARAGE,
        RESIDENCE
    }

    public enum type {
        HEATING,
        ELECTRICITY,
        WATER,
        CUSTOM
    }
}
