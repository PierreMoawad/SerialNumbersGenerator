package com.pierre.serialnumbersgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "serial_numbers_set")
public class SerialNumbersSet {

    @Id
    private String name;
    private Integer quantity;
    @Builder.Default
    private final LocalDateTime created = LocalDateTime.now();
    @OneToMany(mappedBy = "serialNumbersSet", fetch = FetchType.EAGER)
    private Set<SerialNumber> serialNumbers;

    public String getName() {

        return name;
    }

    public Integer getQuantity() {

        return quantity;
    }

    public LocalDateTime getCreated() {

        return created;
    }

    public Set<SerialNumber> getSerialNumbers() {

        return new HashSet<>(serialNumbers);
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setQuantity(Integer quantity) {

        this.quantity = quantity;
    }

    public void addSerialNumber(SerialNumber serialNumber) {

        if (serialNumbers.contains(serialNumber))
            return;

        serialNumbers.add(serialNumber);
        serialNumber.setSerialNumbersSet(this);
    }

    public void removeSerialNumber(SerialNumber serialNumber) {

        if (serialNumbers.contains(serialNumber))
            return;

        serialNumbers.remove(serialNumber);
        serialNumber.setSerialNumbersSet(null);
    }
}
