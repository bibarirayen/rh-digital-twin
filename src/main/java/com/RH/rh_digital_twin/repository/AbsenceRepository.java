package com.RH.rh_digital_twin.repository;

import com.RH.rh_digital_twin.model.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {
}
