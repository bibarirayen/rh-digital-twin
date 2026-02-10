package com.RH.rh_digital_twin.repository;

import com.RH.rh_digital_twin.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByDepartmentNomAndRoleAndDateEmbaucheBetween(
        String departmentNom, String role, LocalDate startDate, LocalDate endDate);
}
