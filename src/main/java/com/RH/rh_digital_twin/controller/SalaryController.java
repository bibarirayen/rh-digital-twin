package com.RH.rh_digital_twin.controller;

import com.RH.rh_digital_twin.model.Salary;
import com.RH.rh_digital_twin.repository.SalaryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salaries")
public class SalaryController {

    private final SalaryRepository salaryRepository;

    public SalaryController(SalaryRepository salaryRepository) {
        this.salaryRepository = salaryRepository;
    }

    @GetMapping
    public List<Salary> getAllSalaries() {
        return salaryRepository.findAll();
    }

    @PostMapping
    public Salary createSalary(@RequestBody Salary salary) {
        return salaryRepository.save(salary);
    }
}
