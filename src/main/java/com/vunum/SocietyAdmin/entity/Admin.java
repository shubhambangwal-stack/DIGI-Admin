package com.vunum.SocietyAdmin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String email;
    private String password;
    private String IP;
    private LocalDateTime lastLogin;
    private boolean status;
    private String token;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn
    private List<Building> Buildings;

    @JsonIgnore
    private String otp;

    private String syndicName;
    private String syndicAddress;
    private String syndicBTW;
    private String syndicAdminEmail;
    private String syndicWebsite;
    private String syndicLogo;

    @Enumerated(EnumType.STRING)
    private Roles role;

    public enum Roles {
        SUPER_ADMIN,
        SYNDIC_ADMIN
    }
}
