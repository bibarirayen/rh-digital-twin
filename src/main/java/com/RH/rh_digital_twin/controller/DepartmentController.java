package com.RH.rh_digital_twin.controller;

import com.RH.rh_digital_twin.model.Department;
import com.RH.rh_digital_twin.repository.DepartmentRepository;
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

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public List<Department> getAll() {
        return departmentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Department getById(@PathVariable Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Department create(@RequestBody Department department) {
        return departmentRepository.save(department);
    }

    @PutMapping("/{id}")
    public Department update(@PathVariable Long id, @RequestBody Department departmentDetails) {
        Department department = departmentRepository.findById(id).orElseThrow();
        department.setNom(departmentDetails.getNom());
        return departmentRepository.save(department);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        departmentRepository.deleteById(id);
    }

    @PostMapping(value = "/upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<Department>> uploadCsv(@RequestParam("file") MultipartFile file,
                                                      @RequestParam(value = "hasHeader", defaultValue = "true") boolean hasHeader) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Department> saved = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            String[] headers = null;
            int nomIndex = 0; // Default to first column
            
            while ((line = reader.readLine()) != null) {
                if (first && hasHeader) {
                    // Parse header to find 'nom' column
                    headers = line.split(",");
                    for (int i = 0; i < headers.length; i++) {
                        if (headers[i].trim().equalsIgnoreCase("nom")) {
                            nomIndex = i;
                            break;
                        }
                    }
                    first = false;
                    continue;
                }
                first = false;
                line = line.trim();
                if (line.isEmpty()) continue;
                
                String[] cols = line.split(",");
                String nom = cols.length > nomIndex ? cols[nomIndex].trim() : "";
                
                // Skip if nom is empty or is just a number (likely an ID)
                if (nom.isEmpty() || nom.matches("^\\d+$")) continue;
                
                Department d = new Department();
                d.setNom(nom);
                saved.add(departmentRepository.save(d));
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.ok(saved);
    }
}
