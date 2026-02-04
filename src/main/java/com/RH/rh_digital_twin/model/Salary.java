package com.RH.rh_digital_twin.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "salary")
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "salaire_base")
    private Double salaireBase;

    private Double prime;

    @Column(name = "date_effet")
    private LocalDate dateEffet;
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public Double getSalaireBase() { return salaireBase; }
    public void setSalaireBase(Double salaireBase) { this.salaireBase = salaireBase; }

    public Double getPrime() { return prime; }
    public void setPrime(Double prime) { this.prime = prime; }

    public LocalDate getDateEffet() { return dateEffet; }
    public void setDateEffet(LocalDate dateEffet) { this.dateEffet = dateEffet; }
}
