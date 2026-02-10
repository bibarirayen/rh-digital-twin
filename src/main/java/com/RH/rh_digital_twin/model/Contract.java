package com.RH.rh_digital_twin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "contract")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_contrat")
    private String typeContrat;

    @Column(name = "temps_travail")
    private String tempsTravail;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    private Integer coefficient;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    private Employee employee;

    public Contract() {}

    public Contract(String typeContrat, String tempsTravail, LocalDate dateDebut, LocalDate dateFin, Integer coefficient, Employee employee) {
        this.typeContrat = typeContrat;
        this.tempsTravail = tempsTravail;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.coefficient = coefficient;
        this.employee = employee;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTypeContrat() { return typeContrat; }
    public void setTypeContrat(String typeContrat) { this.typeContrat = typeContrat; }

    public String getTempsTravail() { return tempsTravail; }
    public void setTempsTravail(String tempsTravail) { this.tempsTravail = tempsTravail; }

    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public Integer getCoefficient() { return coefficient; }
    public void setCoefficient(Integer coefficient) { this.coefficient = coefficient; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
}
