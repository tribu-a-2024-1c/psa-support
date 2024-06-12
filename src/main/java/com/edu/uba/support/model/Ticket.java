package com.edu.uba.support.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String title;

    @Column(length = 255)
    private String severity;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(length = 255)
    private String status;

    @Column(length = 255)
    private String type;

    @Lob
    private String description;

    @Column
    private Long priorityId;

    @Column
    private Long clientId;

    @Column
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "Priority", insertable = false, updatable = false)
    private Priority priority;
}