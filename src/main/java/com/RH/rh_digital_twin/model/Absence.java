package com.RH.rh_digital_twin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "absence")
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    private Boolean justifie;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    private Employee employee;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public Boolean getJustifie() { return justifie; }
    public void setJustifie(Boolean justifie) { this.justifie = justifie; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
}
