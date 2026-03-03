package com.vunum.SocietyAdmin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Getter
@Setter
@Entity
public class Helpdesk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users user;

    private String ticketSubject;
    private String ticketDescription;
    private String ticketID;

    @Enumerated(EnumType.STRING)
    private TicketStatus ticketStatus;

//    @Enumerated(EnumType.STRING)
//    private TicketPriority priority;


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ElementCollection
    private List<String> imageUrl;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(nullable = true)
    private Users admin;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(nullable = true)
    private Admin administrator;

    private String ticketResponse;


    public enum TicketStatus {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }


}
