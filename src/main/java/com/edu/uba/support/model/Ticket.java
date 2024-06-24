package com.edu.uba.support.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @Column
    private String title;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column
    private String status;

    @Column
    private String type;

    @Lob
    private String description;

    @Column
    private String priority;

    @ManyToOne
    @JoinColumn(name = "product_version_id")
    private ProductVersion productVersion;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    @ToString.Exclude
    private List<Task> tasks;

    @ManyToOne
    @JoinColumn(name = "Resource", insertable = false)
    @ToString.Exclude
    private Resource resource;
}
