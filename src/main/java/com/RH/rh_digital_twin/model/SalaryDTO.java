package com.RH.rh_digital_twin.model;

import java.time.LocalDate;

public class SalaryDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Integer salaireBase;
    private Integer prime;
    private LocalDate dateEffet;
    private LocalDate dateFin;
    private String typePrime;
    private Integer bonus;

    public SalaryDTO() {}

    public SalaryDTO(Long id, Long employeeId, String employeeName, Integer salaireBase, Integer prime, LocalDate dateEffet, LocalDate dateFin, String typePrime, Integer bonus) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.salaireBase = salaireBase;
        this.prime = prime;
        this.dateEffet = dateEffet;
        this.dateFin = dateFin;
        this.typePrime = typePrime;
        this.bonus = bonus;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Integer getSalaireBase() { return salaireBase; }
    public void setSalaireBase(Integer salaireBase) { this.salaireBase = salaireBase; }

    public Integer getPrime() { return prime; }
    public void setPrime(Integer prime) { this.prime = prime; }

    public LocalDate getDateEffet() { return dateEffet; }
    public void setDateEffet(LocalDate dateEffet) { this.dateEffet = dateEffet; }

    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }

    public String getTypePrime() { return typePrime; }
    public void setTypePrime(String typePrime) { this.typePrime = typePrime; }

    public Integer getBonus() { return bonus; }
    public void setBonus(Integer bonus) { this.bonus = bonus; }
}
