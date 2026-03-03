package com.vunum.SocietyAdmin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ManagementEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String keyType;
    private String description;
    private String status;


    private LocalDateTime issuedAt;
    private LocalDateTime returnedAt;
    private LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "buildingId")
    private Building building;

}