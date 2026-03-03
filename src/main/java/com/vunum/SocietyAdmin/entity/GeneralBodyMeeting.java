package com.vunum.SocietyAdmin.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class GeneralBodyMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @OneToMany
    @JoinTable(name = "general_body_meetings_attendees")
    @JoinColumn(name = "attendees_id", foreignKey = @ForeignKey(name = "fk_attendees"))
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<Users> attendees;

    private String baseAgenda;

    @Column(nullable = true)
    @ElementCollection
    private Map<String, String> agenda;

    private String description;
    private String document;

    @Column(nullable = false)
    private LocalDateTime scheduledTime;

    @Column(nullable = false)
    private String location;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Poll> Polls = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private type type;

    @OneToOne
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Admin syndic;

    public enum type {
        ANNUAL,
        AD_HOC
    }
}
