package com.RH.rh_digital_twin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "timesheet")
public class Timesheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    private Employee employee;

    private String mois;

    @Column(name = "heures_travaillees")
    private Double heuresTravaillees;

    @Column(name = "heures_sup")
    private Double heuresSup;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getMois() { return mois; }
    public void setMois(String mois) { this.mois = mois; }

    public Double getHeuresTravaillees() { return heuresTravaillees; }
    public void setHeuresTravaillees(Double heuresTravaillees) { this.heuresTravaillees = heuresTravaillees; }

    public Double getHeuresSup() { return heuresSup; }
    public void setHeuresSup(Double heuresSup) { this.heuresSup = heuresSup; }
}
