package com.vunum.SocietyAdmin.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String serviceType;
    private String availability;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Building building;

    @Enumerated(EnumType.STRING)
    private type type;

    public enum type {
        EMERGENCY,
        SYNDIC,
        OTHER
    }
}
