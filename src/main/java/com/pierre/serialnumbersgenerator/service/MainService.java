package com.pierre.serialnumbersgenerator.service;

import com.pierre.serialnumbersgenerator.model.SerialNumber;
import com.pierre.serialnumbersgenerator.model.SerialNumbersSet;
import com.pierre.serialnumbersgenerator.model.Settings;
import com.pierre.serialnumbersgenerator.repository.SerialNumberRepository;
import com.pierre.serialnumbersgenerator.repository.SerialNumbersSetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainService {

    private static final String NUMBERS = "0123456789";
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String[] HEADERS = {"Number", "Set Name"};
    private static final String NO_CHARS_SELECTED = "One Character Pool Type Must Be Selected";

    @Value("${downloads.path}")
    private String downloadsPath;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final SerialNumberRepository serialNumberRepository;
    private final SerialNumbersSetRepository serialNumbersSetRepository;

    public Set<SerialNumbersSet> getSerialNumbersSets() {

        return new HashSet<>(serialNumbersSetRepository.findAll());
    }

    public Future<SerialNumbersSet> generateSerialNumbersSet(Settings settings, String name, Integer quantity) {

        return executor.submit(() -> {

            Set<SerialNumber> serialNumbers = new HashSet<>();

            SerialNumbersSet serialNumbersSet = SerialNumbersSet.builder()
                    .name(name)
                    .quantity(quantity)
                    .serialNumbers(serialNumbers)
                    .build();

            serialNumbersSetRepository.save(serialNumbersSet);

            selectCharsFromPools(settings);
            removeExcludedChars(settings);

            while (serialNumbers.size() < quantity) {

                SerialNumber serialNumber = SerialNumber.builder()
                        .number(generateSerialNumber(settings))
                        .serialNumbersSet(serialNumbersSet)
                        .build();

                try {

                    serialNumberRepository.save(serialNumber);
                    serialNumbers.add(serialNumber);

                } catch (ConstraintViolationException e) {

                    log.error("Duplicate Serial Number Generated: " + e.getMessage());
                }
            }

            return serialNumbersSet;
        });
    }

    public File createCSVFile(String name) throws IOException {

        File csvFile = new File(downloadsPath + name + ".csv");
        FileWriter writer = new FileWriter(csvFile);
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(HEADERS));

        serialNumbersSetRepository
                .getSerialNumbersSetByName(name)
                .getSerialNumbers()
                .forEach(s -> {
                    try {

                        printer.printRecord(s.getNumber(), s.getSerialNumbersSet().getName());

                    } catch (IOException e) {

                        log.error(e.getMessage());
                    }
                });

        printer.flush();

        return csvFile;
    }

    public void deleteSerialNumbersSet(SerialNumbersSet serialNumbersSet) {

        serialNumbersSet.getSerialNumbers().forEach(serialNumberRepository::delete);
        serialNumbersSetRepository.delete(serialNumbersSet);
    }

    public boolean validateExcludedChars(Settings settings) {

        selectCharsFromPools(settings);

        if (settings.getSelectedChars() != null) {

            if (settings.getExcludedChars() != null) {

                String[] splitChars = settings.getExcludedChars().split(",");

                for (String character : splitChars) {

                    if (character.length() > 1 || !settings.getSelectedChars().contains(character)) {

                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }

    public boolean doesNameExist(String name) {

        return serialNumbersSetRepository.existsByName(name);
    }

    String generateSerialNumber(Settings settings) {

        StringBuilder generatedSerialNumber = new StringBuilder();
        int length = settings.getLength();

        while (length > 0) {

            generatedSerialNumber.append(getRandom(settings.getSelectedChars()));
            length--;
        }

        return generatedSerialNumber.toString();
    }

    void selectCharsFromPools(Settings settings) {

        StringBuilder selectedChars = new StringBuilder();

        if (!(settings.isNumeric() || settings.isUppercase() || settings.isLowercase())) {

            throw new IllegalArgumentException(NO_CHARS_SELECTED);
        }

        if (settings.isNumeric()) {

            selectedChars.append(NUMBERS);
        }

        if (settings.isUppercase()) {

            selectedChars.append(UPPERCASE_CHARS);
        }

        if (settings.isLowercase()) {

            selectedChars.append(LOWERCASE_CHARS);
        }

        settings.setSelectedChars(selectedChars.toString());
    }

    void removeExcludedChars(Settings settings) {

        StringBuilder selectedChars = new StringBuilder(settings.getSelectedChars());
        String excludedChars = settings.getExcludedChars();

        if (excludedChars != null) {

            String[] splitChars = excludedChars.split(",");

            for (String character : splitChars) {

                selectedChars.deleteCharAt(selectedChars.indexOf(character));
            }
        }

        settings.setSelectedChars(selectedChars.toString());
    }

    char getRandom(String selectedChars) {

        return selectedChars.charAt(new SecureRandom().nextInt(selectedChars.length()));
    }
}