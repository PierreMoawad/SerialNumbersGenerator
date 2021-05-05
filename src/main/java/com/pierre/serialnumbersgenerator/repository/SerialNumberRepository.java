package com.pierre.serialnumbersgenerator.repository;

import com.pierre.serialnumbersgenerator.model.SerialNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SerialNumberRepository extends JpaRepository<SerialNumber, String> {
}
