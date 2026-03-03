package com.vunum.SocietyAdmin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
public class ChargeConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Building building;

    @Column(nullable = false)
    private String residentType;

    @Column(nullable = false)
    private String flatType;

    @Enumerated(EnumType.STRING)
    private type type;

    @Column(nullable = false)
    private Double baseRate;
    private Double maintenanceRate;

    @ElementCollection
    @CollectionTable(name = "custom_utilities", joinColumns = @JoinColumn(name = "charge_config_id"))
    @MapKeyColumn(name = "utility_name")
    @Column(name = "charge_amount")
    private Map<String, Double> customUtilities = new HashMap<>();

    //Building Name_ User type_ Residence type_Charges type _ Bill Type
    @SuppressWarnings("all")
    public String getName() {
        return this.building.getBuildingName() + " - " + this.residentType + " - " + this.getFlatType() + " - " +
                (this.getType().equals(type.RESERVE) ? "RESERVE" : "PROVISION") + " - " + this.getType();
    }

    public enum type {
        HEATING,
        ELECTRICITY,
        WATER,
        CUSTOM,
        RESERVE
    }

}
