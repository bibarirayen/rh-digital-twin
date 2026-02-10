package com.RH.rh_digital_twin.controller;

import com.RH.rh_digital_twin.model.Absence;
import com.RH.rh_digital_twin.repository.AbsenceRepository;
import com.RH.rh_digital_twin.repository.EmployeeRepository;
import com.RH.rh_digital_twin.model.Employee;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/absences")
public class AbsenceController {

    private final AbsenceRepository absenceRepository;
    private final EmployeeRepository employeeRepository;

    public AbsenceController(AbsenceRepository absenceRepository, EmployeeRepository employeeRepository) {
        this.absenceRepository = absenceRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public List<Absence> getAll() {
        return absenceRepository.findAll();
    }

    @GetMapping("/{id}")
    public Absence getById(@PathVariable Long id) {
        return absenceRepository.findById(id).orElse(null);
    }

    @PostMapping
    public ResponseEntity<Absence> create(@RequestBody Absence absence) {
        if (absence.getEmployee() != null && absence.getEmployee().getId() != null) {
            Optional<Employee> e = employeeRepository.findById(absence.getEmployee().getId());
            e.ifPresent(absence::setEmployee);
        }
        return ResponseEntity.ok(absenceRepository.save(absence));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Absence> update(@PathVariable Long id, @RequestBody Absence details) {
        Absence a = absenceRepository.findById(id).orElseThrow();
        a.setType(details.getType());
        a.setDateDebut(details.getDateDebut());
        a.setDateFin(details.getDateFin());
        a.setJustifie(details.getJustifie());
        if (details.getEmployee() != null && details.getEmployee().getId() != null) {
            Optional<Employee> e = employeeRepository.findById(details.getEmployee().getId());
            e.ifPresent(a::setEmployee);
        }
        return ResponseEntity.ok(absenceRepository.save(a));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        absenceRepository.deleteById(id);
    }

    @PostMapping(value = "/upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<Absence>> uploadCsv(@RequestParam("file") MultipartFile file,
                                                   @RequestParam(value = "hasHeader", defaultValue = "true") boolean hasHeader) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Absence> saved = new ArrayList<>();
        DateTimeFormatter iso = DateTimeFormatter.ISO_LOCAL_DATE;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first && hasHeader) { first = false; continue; }
                first = false;
                line = line.trim();
                if (line.isEmpty()) continue;
                // Expect CSV: employeeId,type,date_debut,date_fin,justifiee
                String[] cols = line.split(",");
                Long employeeId = null;
                try { if (cols.length > 0 && !cols[0].trim().isEmpty()) employeeId = Long.parseLong(cols[0].trim()); } catch (NumberFormatException ignored) {}

                String type = cols.length > 1 ? cols[1].trim() : null;

                LocalDate dateDebut = null;
                if (cols.length > 2 && !cols[2].trim().isEmpty()) {
                    try { dateDebut = LocalDate.parse(cols[2].trim(), iso); } catch (DateTimeParseException ignored) {}
                }

                LocalDate dateFin = null;
                if (cols.length > 3 && !cols[3].trim().isEmpty()) {
                    try { dateFin = LocalDate.parse(cols[3].trim(), iso); } catch (DateTimeParseException ignored) {}
                }

                Boolean justifiee = null;
                if (cols.length > 4 && !cols[4].trim().isEmpty()) {
                    String v = cols[4].trim().toLowerCase();
                    justifiee = (v.equals("true") || v.equals("1") || v.equals("yes") || v.equals("y"));
                }

                Absence a = new Absence();
                a.setType(type);
                a.setDateDebut(dateDebut);
                a.setDateFin(dateFin);
                a.setJustifie(justifiee);

                if (employeeId != null) {
                    Optional<Employee> emp = employeeRepository.findById(employeeId);
                    emp.ifPresent(a::setEmployee);
                }

                saved.add(absenceRepository.save(a));
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.ok(saved);
    }
}
