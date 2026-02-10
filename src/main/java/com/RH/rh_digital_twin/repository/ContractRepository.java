package com.RH.rh_digital_twin.repository;

import com.RH.rh_digital_twin.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
}
