package com.RH.rh_digital_twin.controller;

import com.RH.rh_digital_twin.model.Timesheet;
import com.RH.rh_digital_twin.model.TimesheetDTO;
import com.RH.rh_digital_twin.model.Employee;
import com.RH.rh_digital_twin.repository.TimesheetRepository;
import com.RH.rh_digital_twin.repository.EmployeeRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/timesheets")
public class TimesheetController {

    private final TimesheetRepository timesheetRepository;
    private final EmployeeRepository employeeRepository;

    public TimesheetController(TimesheetRepository timesheetRepository, EmployeeRepository employeeRepository) {
        this.timesheetRepository = timesheetRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public List<Timesheet> getAll() {
        return timesheetRepository.findAll();
    }

    @GetMapping("/with-employee")
    public List<TimesheetDTO> getTimesheetsWithEmployee(){
        List<Timesheet> all = timesheetRepository.findAll();
        List<TimesheetDTO> dto = new ArrayList<>();
        for (Timesheet t : all) {
            Long empId = null;
            String empName = null;
            if (t.getEmployee() != null) {
                empId = t.getEmployee().getId();
                empName = t.getEmployee().getNom() + " " + t.getEmployee().getPrenom();
            }
            dto.add(new TimesheetDTO(t.getId(), empId, empName, t.getMois(), t.getAnnee(), t.getHeuresTravaillees(), t.getHeuresSup(), t.getJoursAbsence()));
        }
        return dto;
    }

    @GetMapping("/{id}")
    public Timesheet getById(@PathVariable Long id) {
        return timesheetRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Timesheet create(@RequestBody Timesheet timesheet) {
        return timesheetRepository.save(timesheet);
    }

    @PutMapping("/{id}")
    public Timesheet update(@PathVariable Long id, @RequestBody Timesheet timesheetDetails) {
        Timesheet timesheet = timesheetRepository.findById(id).orElseThrow();
        timesheet.setMois(timesheetDetails.getMois());
        timesheet.setHeuresTravaillees(timesheetDetails.getHeuresTravaillees());
        timesheet.setHeuresSup(timesheetDetails.getHeuresSup());
        timesheet.setEmployee(timesheetDetails.getEmployee());
        return timesheetRepository.save(timesheet);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        timesheetRepository.deleteById(id);
    }

    @PostMapping(value = "/upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<Timesheet>> uploadCsv(@RequestParam("file") MultipartFile file,
                                                     @RequestParam(value = "hasHeader", defaultValue = "true") boolean hasHeader) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Timesheet> saved = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first && hasHeader) { first = false; continue; }
                first = false;
                line = line.trim();
                if (line.isEmpty()) continue;
                // Expect CSV: id,mois,annee,heures_travaillees,heures_sup,jours_absence,employee_id
                String[] cols = line.split(",");
                Integer mois = null;
                if (cols.length > 1 && !cols[1].trim().isEmpty()) {
                    try { mois = Integer.parseInt(cols[1].trim()); } catch (NumberFormatException ignored) {}
                }

                Integer annee = null;
                if (cols.length > 2 && !cols[2].trim().isEmpty()) {
                    try { annee = Integer.parseInt(cols[2].trim()); } catch (NumberFormatException ignored) {}
                }

                Integer heuresTravaillees = null;
                if (cols.length > 3 && !cols[3].trim().isEmpty()) {
                    try { heuresTravaillees = Integer.parseInt(cols[3].trim()); } catch (NumberFormatException ignored) {}
                }

                Integer heuresSup = null;
                if (cols.length > 4 && !cols[4].trim().isEmpty()) {
                    try { heuresSup = Integer.parseInt(cols[4].trim()); } catch (NumberFormatException ignored) {}
                }

                Integer joursAbsence = null;
                if (cols.length > 5 && !cols[5].trim().isEmpty()) {
                    try { joursAbsence = Integer.parseInt(cols[5].trim()); } catch (NumberFormatException ignored) {}
                }

                Long employeeId = null;
                try { if (cols.length > 6 && !cols[6].trim().isEmpty()) employeeId = Long.parseLong(cols[6].trim()); } catch (NumberFormatException ignored) {}

                Timesheet t = new Timesheet();
                t.setMois(mois);
                t.setAnnee(annee);
                t.setHeuresTravaillees(heuresTravaillees);
                t.setHeuresSup(heuresSup);
                t.setJoursAbsence(joursAbsence);

                if (employeeId != null) {
                    Optional<Employee> emp = employeeRepository.findById(employeeId);
                    emp.ifPresent(t::setEmployee);
                }

                saved.add(timesheetRepository.save(t));
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.ok(saved);
    }
}
