package com.RH.rh_digital_twin.controller;

import com.RH.rh_digital_twin.model.Salary;
import com.RH.rh_digital_twin.model.SalaryDTO;
import com.RH.rh_digital_twin.model.Employee;
import com.RH.rh_digital_twin.repository.SalaryRepository;
import com.RH.rh_digital_twin.repository.EmployeeRepository;
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
@RequestMapping("/api/salaries")
public class SalaryController {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;

    public SalaryController(SalaryRepository salaryRepository, EmployeeRepository employeeRepository) {
        this.salaryRepository = salaryRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public List<Salary> getAllSalaries() {
        return salaryRepository.findAll();
    }

    @GetMapping("/with-employee")
    public List<SalaryDTO> getSalariesWithEmployee(){
        List<Salary> all = salaryRepository.findAll();
        List<SalaryDTO> dto = new ArrayList<>();
        for (Salary s : all) {
            Long empId = null;
            String empName = null;
            if (s.getEmployee() != null) {
                empId = s.getEmployee().getId();
                empName = s.getEmployee().getNom() + " " + s.getEmployee().getPrenom();
            }
            dto.add(new SalaryDTO(
                s.getId(), empId, empName,
                s.getSalaireBase(), s.getPrime(), s.getDateEffet(),
                s.getDateFin(), s.getTypePrime(), s.getBonus()
            ));
        }
        return dto;
    }

    @PostMapping
    public Salary createSalary(@RequestBody Salary salary) {
        return salaryRepository.save(salary);
    }

    @PostMapping(value = "/upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<Salary>> uploadCsv(@RequestParam("file") MultipartFile file,
                                                  @RequestParam(value = "hasHeader", defaultValue = "true") boolean hasHeader) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Salary> saved = new ArrayList<>();
        DateTimeFormatter iso = DateTimeFormatter.ISO_LOCAL_DATE;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first && hasHeader) {
                    first = false;
                    continue;
                }
                first = false;
                line = line.trim();
                if (line.isEmpty()) continue;
                // Expect CSV: employeeId,salaireBase,prime,dateEffet (ISO yyyy-MM-dd)
                String[] cols = line.split(",");
                Long employeeId = null;
                try {
                    if (cols.length > 0 && !cols[0].trim().isEmpty()) {
                        employeeId = Long.parseLong(cols[0].trim());
                    }
                } catch (NumberFormatException ignored) {}

                Integer salaireBase = null;
                if (cols.length > 1 && !cols[1].trim().isEmpty()) {
                    try { salaireBase = Integer.parseInt(cols[1].trim()); } catch (NumberFormatException ignored) {}
                }

                Integer prime = null;
                if (cols.length > 2 && !cols[2].trim().isEmpty()) {
                    try { prime = Integer.parseInt(cols[2].trim()); } catch (NumberFormatException ignored) {}
                }

                LocalDate dateEffet = null;
                if (cols.length > 3 && !cols[3].trim().isEmpty()) {
                    String d = cols[3].trim();
                    try { dateEffet = LocalDate.parse(d, iso); }
                    catch (DateTimeParseException e) { /* ignore invalid date */ }
                }

                LocalDate dateFin = null;
                if (cols.length > 4 && !cols[4].trim().isEmpty()) {
                    String d = cols[4].trim();
                    try { dateFin = LocalDate.parse(d, iso); }
                    catch (DateTimeParseException e) { /* ignore */ }
                }

                String typePrime = null;
                if (cols.length > 5) typePrime = cols[5].trim();

                Integer bonus = null;
                if (cols.length > 6 && !cols[6].trim().isEmpty()) {
                    try { bonus = Integer.parseInt(cols[6].trim()); } catch (NumberFormatException ignored) {}
                }

                Salary salary = new Salary();
                salary.setSalaireBase(salaireBase);
                salary.setPrime(prime);
                salary.setDateEffet(dateEffet);
                salary.setDateFin(dateFin);
                salary.setTypePrime(typePrime);
                salary.setBonus(bonus);

                if (employeeId != null) {
                    Optional<Employee> emp = employeeRepository.findById(employeeId);
                    emp.ifPresent(salary::setEmployee);
                }

                saved.add(salaryRepository.save(salary));
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.ok(saved);
    }
}
