package com.vunum.SocietyAdmin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private Roles role;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String Otphash;
    @JsonIgnore
    private LocalDateTime OtpExpiry;

    private boolean syndicApproved = false;
    private boolean ownerApproved = false;

    private String residence;
    private Integer floor;
    private Integer flatNumber;
    private String flatType;
    private String boxNumber;

    @JsonIgnore
    private String token;

    @ManyToOne
    private Building building;

    public enum Roles {
        user, //TENANT
        syndic_employee,
        owner //FLAT OWNER
    }


}