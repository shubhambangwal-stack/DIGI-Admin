package com.vunum.SocietyAdmin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private Users user;

    private String billingMonth;

    @Enumerated(EnumType.STRING)
    private type type;

    private String billName;
    private String billId;

    @Enumerated(EnumType.STRING)
    private status status;

    private Double TotalAmount;

    private LocalDate DueDate;
    private LocalDate PaidOn;
    private String transactionId;

    @JsonIgnore
    @OneToOne
    @JoinColumn
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Consumption consumption;

    public enum type {
        PROVISIONS,
        UTILITIES,
        RESERVE_FUND
    }

    public enum status {
        PAID,
        UNPAID,
        DUE,
        OVERDUE
    }
}
