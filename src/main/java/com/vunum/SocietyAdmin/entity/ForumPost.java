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

@Entity
@Getter
@Setter
public class ForumPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @JsonIgnore
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Building building;

    private String forumTitle;
    private String photo;

    @Column(columnDefinition = "TEXT")
    private String postContent;
    private Integer upvoteCount = 0;
    private Integer downvoteCount = 0;

    @JsonIgnore
    @ElementCollection
    private List<Long> upvoteList = new ArrayList<>();
    @JsonIgnore
    @ElementCollection
    private List<Long> downvoteList = new ArrayList<>();

    private LocalDateTime createdPostAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ForumPost parent;


}
