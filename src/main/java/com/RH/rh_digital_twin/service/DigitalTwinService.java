package com.RH.rh_digital_twin.service;

import com.RH.rh_digital_twin.model.Department;
import com.RH.rh_digital_twin.model.Employee;
import com.RH.rh_digital_twin.repository.DepartmentRepository;
import com.RH.rh_digital_twin.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DigitalTwinService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Transactional
    public void importEmployeesFromCSV(MultipartFile file) throws IOException {
        List<Employee> employees = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        boolean firstLine = true;

        while ((line = reader.readLine()) != null) {
            if (firstLine) {
                firstLine = false; // Skip header
                continue;
            }

            String[] fields = line.split(",");
            // CSV format: id,matricule,nom,prenom,role,date_embauche,email_pro,statut,department_id
            if (fields.length >= 9) {
                Employee employee = new Employee();
                employee.setMatricule(fields[1].trim());
                employee.setNom(fields[2].trim());
                employee.setPrenom(fields[3].trim());
                employee.setRole(fields[4].trim());
                employee.setDateEmbauche(LocalDate.parse(fields[5].trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                employee.setEmailPro(fields[6].trim());
                employee.setStatut(fields[7].trim());

                // Handle department by ID
                try {
                    Long departmentId = Long.parseLong(fields[8].trim());
                    Department department = departmentRepository.findById(departmentId).orElse(null);
                    employee.setDepartment(department);
                } catch (NumberFormatException e) {
                    // If department_id is not a valid number, skip it
                }

                employees.add(employee);
            }
        }

        employeeRepository.saveAll(employees);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> searchEmployees(String nom, String prenom, String role) {
        // For simplicity, if all parameters are null, return all employees
        if (nom == null && prenom == null && role == null) {
            return employeeRepository.findAll();
        }
        // Otherwise, use a more flexible search - for now, return all and filter in memory
        // In production, you'd implement a custom repository method with optional parameters
        List<Employee> allEmployees = employeeRepository.findAll();
        return allEmployees.stream()
                .filter(emp -> nom == null || (emp.getNom() != null && emp.getNom().toLowerCase().contains(nom.toLowerCase())))
                .filter(emp -> prenom == null || (emp.getPrenom() != null && emp.getPrenom().toLowerCase().contains(prenom.toLowerCase())))
                .filter(emp -> role == null || (emp.getRole() != null && emp.getRole().toLowerCase().contains(role.toLowerCase())))
                .toList();
    }
}
