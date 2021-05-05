package com.pierre.serialnumbersgenerator.model;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class Settings {

    private Integer length;
    private boolean numeric;
    private boolean uppercase;
    private boolean lowercase;
    @Nullable
    private String excludedChars;
    private String selectedChars;
    private Integer minQuantity;
    private Integer maxQuantity;
    private Integer defaultLength;
    private Integer minLength;
    private Integer maxLength;
}