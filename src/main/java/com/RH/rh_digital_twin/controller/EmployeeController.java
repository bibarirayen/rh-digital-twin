package com.RH.rh_digital_twin.controller;

import com.RH.rh_digital_twin.model.Employee;
import com.RH.rh_digital_twin.repository.EmployeeRepository;
import com.RH.rh_digital_twin.service.DigitalTwinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee Management", description = "APIs for managing employees in the Digital Twin")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DigitalTwinService digitalTwinService;

    @GetMapping
    @Operation(summary = "Get all employees")
    public List<Employee> getAllEmployees() {
        return digitalTwinService.getAllEmployees();
    }

    @PostMapping
    @Operation(summary = "Create a new employee")
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeRepository.save(employee);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeRepository.findById(id)
                .map(employee -> ResponseEntity.ok(employee))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setNom(employeeDetails.getNom());
                    employee.setPrenom(employeeDetails.getPrenom());
                    employee.setRole(employeeDetails.getRole());
                    employee.setDateEmbauche(employeeDetails.getDateEmbauche());
                    return ResponseEntity.ok(employeeRepository.save(employee));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/import")
    @Operation(summary = "Import employees from CSV")
    public ResponseEntity<String> importEmployees(@RequestParam("file") MultipartFile file) {
        try {
            digitalTwinService.importEmployeesFromCSV(file);
            return ResponseEntity.ok("Employees imported successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error importing employees: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search employees by name, first name, or role")
    public List<Employee> searchEmployees(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String role) {
        return digitalTwinService.searchEmployees(nom, prenom, role);
    }
}
