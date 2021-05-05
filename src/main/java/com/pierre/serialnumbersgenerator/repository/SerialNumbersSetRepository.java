package com.pierre.serialnumbersgenerator.repository;

import com.pierre.serialnumbersgenerator.model.SerialNumbersSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SerialNumbersSetRepository extends JpaRepository<SerialNumbersSet, String> {

    SerialNumbersSet getSerialNumbersSetByName(String name);
    boolean existsByName(String name);
}
