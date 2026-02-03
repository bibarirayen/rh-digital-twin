package com.RH.rh_digital_twin.controller;

import com.RH.rh_digital_twin.model.Department;
import com.RH.rh_digital_twin.repository.DepartmentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
}
