package com.edu.uba.support.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

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

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference // to avoid infinite recursion when serializing the object
    private List<Task> tasks;

    @ManyToOne
    @JoinColumn(name = "Priority", insertable = false, updatable = false)
    private Priority priority;

    @ManyToOne
    @JoinColumn(name = "Resource", insertable = false)
    private Resource resource;
}
