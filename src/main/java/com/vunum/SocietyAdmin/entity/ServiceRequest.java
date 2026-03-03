package com.vunum.SocietyAdmin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Entity
@Getter
@Setter
public class ServiceRequest {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String description;

    @Getter
    private String image;

    @Getter
    private String status = "Pending";

    @Getter
    @ManyToOne
    @JoinColumn(name = "resident_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users resident;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "subcategory_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private SubCategory subCategory;

    private Date createdDate = new Date();


}
