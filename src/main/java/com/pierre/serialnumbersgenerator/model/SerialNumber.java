package com.pierre.serialnumbersgenerator.model;

import lombok.*;
import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "serial_number")
public class SerialNumber implements Persistable<String> {

    @Id
    private String number;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_set_name", referencedColumnName = "name")
    private SerialNumbersSet serialNumbersSet;

    @Nullable
    @Override
    public String getId() {

        return number;
    }

    @Override
    public boolean isNew() {

        return true;
    }

    public void setSerialNumbersSet(SerialNumbersSet serialNumbersSet) {

        SerialNumbersSet oldSerialNumbersSet = this.serialNumbersSet;

        if (Objects.equals(serialNumbersSet, oldSerialNumbersSet))
            return;

        this.serialNumbersSet = serialNumbersSet;

        if (oldSerialNumbersSet!=null)
            oldSerialNumbersSet.removeSerialNumber(this);

        if (serialNumbersSet!=null)
            serialNumbersSet.addSerialNumber(this);
    }
}
