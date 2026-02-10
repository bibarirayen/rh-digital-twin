package com.RH.rh_digital_twin.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String matricule;

    private String nom;
    private String prenom;
    private String role;

    @Column(name = "date_embauche")
    private LocalDate dateEmbauche;

    @Column(unique = true)
    private String emailPro;

    private String statut;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonManagedReference
    private Department department;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Salary> salaries;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Timesheet> timesheets;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Absence> absences;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Contract> contracts;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDate getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; }

    public String getEmailPro() { return emailPro; }
    public void setEmailPro(String emailPro) { this.emailPro = emailPro; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public List<Salary> getSalaries() { return salaries; }
    public void setSalaries(List<Salary> salaries) { this.salaries = salaries; }

    public List<Timesheet> getTimesheets() { return timesheets; }
    public void setTimesheets(List<Timesheet> timesheets) { this.timesheets = timesheets; }

    public List<Absence> getAbsences() { return absences; }
    public void setAbsences(List<Absence> absences) { this.absences = absences; }

    public List<Contract> getContracts() { return contracts; }
    public void setContracts(List<Contract> contracts) { this.contracts = contracts; }
}
