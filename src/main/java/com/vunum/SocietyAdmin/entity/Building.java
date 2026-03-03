package com.vunum.SocietyAdmin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String buildingName;
    private String streetName;

    @Column(unique = true)
    private Long buildingNumber;

    private Long pincode;
    private String city;
    private String syndicName;
    private String syndicAddress;
    private String syndicBTW;
    private String syndicAdminEmail;
    private String syndicWebsite;
    private String syndicLogo;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Asset> assets = new ArrayList<>();

    public String getFullAddress() {
        return streetName + ", " + city + " - " + pincode;
    }

}