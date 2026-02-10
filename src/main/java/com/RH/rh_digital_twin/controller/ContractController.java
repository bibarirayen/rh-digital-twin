package com.RH.rh_digital_twin.controller;

import com.RH.rh_digital_twin.model.Contract;
import com.RH.rh_digital_twin.model.Employee;
import com.RH.rh_digital_twin.repository.ContractRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractRepository contractRepository;

    public ContractController(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    @GetMapping
    public List<Contract> all() {
        return contractRepository.findAll();
    }

    @PostMapping
    public Contract create(@RequestBody Contract contract) {
        return contractRepository.save(contract);
    }

    @PostMapping("/upload-csv")
    public ResponseEntity<List<Contract>> uploadCsv(@RequestParam("file") MultipartFile file,
                                                   @RequestParam(value = "hasHeader", defaultValue = "true") boolean hasHeader) {
        List<Contract> saved = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            if (hasHeader) reader.readLine();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                // Expected order: id,type_contrat,temps_travail,date_debut,date_fin,coefficient,employee_id
                String typeContrat = parts.length > 1 ? parts[1].trim() : null;
                String tempsTravail = parts.length > 2 ? parts[2].trim() : null;
                LocalDate dateDebut = null;
                if (parts.length > 3 && !parts[3].trim().isEmpty()) {
                    try { dateDebut = LocalDate.parse(parts[3].trim()); } catch (DateTimeParseException ignored) {}
                }
                LocalDate dateFin = null;
                if (parts.length > 4 && !parts[4].trim().isEmpty()) {
                    try { dateFin = LocalDate.parse(parts[4].trim()); } catch (DateTimeParseException ignored) {}
                }
                Integer coefficient = null;
                if (parts.length > 5 && !parts[5].trim().isEmpty()) {
                    try { coefficient = Integer.parseInt(parts[5].trim()); } catch (NumberFormatException ignored) {}
                }
                Employee employee = null;
                if (parts.length > 6 && !parts[6].trim().isEmpty()) {
                    try {
                        Long empId = Long.parseLong(parts[6].trim());
                        employee = new Employee();
                        employee.setId(empId);
                    } catch (NumberFormatException ignored) {}
                }

                Contract c = new Contract(typeContrat, tempsTravail, dateDebut, dateFin, coefficient, employee);
                saved.add(contractRepository.save(c));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(saved);
    }
}
