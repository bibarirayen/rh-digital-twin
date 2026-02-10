package com.RH.rh_digital_twin.model;

public class TimesheetDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Integer mois;
    private Integer annee;
    private Integer heuresTravaillees;
    private Integer heuresSup;
    private Integer joursAbsence;

    public TimesheetDTO() {}

    public TimesheetDTO(Long id, Long employeeId, String employeeName, Integer mois, Integer annee, Integer heuresTravaillees, Integer heuresSup, Integer joursAbsence) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.mois = mois;
        this.annee = annee;
        this.heuresTravaillees = heuresTravaillees;
        this.heuresSup = heuresSup;
        this.joursAbsence = joursAbsence;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Integer getMois() { return mois; }
    public void setMois(Integer mois) { this.mois = mois; }

    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }

    public Integer getHeuresTravaillees() { return heuresTravaillees; }
    public void setHeuresTravaillees(Integer heuresTravaillees) { this.heuresTravaillees = heuresTravaillees; }

    public Integer getHeuresSup() { return heuresSup; }
    public void setHeuresSup(Integer heuresSup) { this.heuresSup = heuresSup; }

    public Integer getJoursAbsence() { return joursAbsence; }
    public void setJoursAbsence(Integer joursAbsence) { this.joursAbsence = joursAbsence; }
}
