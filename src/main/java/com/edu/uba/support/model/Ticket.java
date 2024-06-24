package com.edu.uba.support.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    @JsonManagedReference // to avoid infinite recursion when serializing the object
    @JsonIgnoreProperties({"ticket"})
    @ToString.Exclude // Exclude from Lombok's toString() to avoid circular reference
    private List<Task> tasks;

    @ManyToOne
    @JoinColumn(name = "Priority", insertable = false, updatable = false)
    @ToString.Exclude // Exclude from Lombok's toString() to avoid circular reference
    private Priority priority;

    @ManyToOne
    @JoinColumn(name = "Resource", insertable = false)
    @ToString.Exclude // Exclude from Lombok's toString() to avoid circular reference
    private Resource resource;
}
