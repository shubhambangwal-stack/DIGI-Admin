package com.vunum.SocietyAdmin.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Admin issuer;

    @Column(nullable = false)
    private String shiftName;

    @Column(nullable = false)
    private String shiftStart;

    @Column(nullable = false)
    private String shiftEnd;

    @Column(nullable = false)
    private String type;

}
