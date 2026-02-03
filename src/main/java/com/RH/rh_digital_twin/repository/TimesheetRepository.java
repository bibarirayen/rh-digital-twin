package com.RH.rh_digital_twin.repository;

import com.RH.rh_digital_twin.model.Timesheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {
}
