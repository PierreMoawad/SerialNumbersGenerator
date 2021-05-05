package com.pierre.serialnumbersgenerator.controller;

import com.pierre.serialnumbersgenerator.exception.InvalidSetNameException;
import com.pierre.serialnumbersgenerator.exception.InvalidSetQuantityException;
import com.pierre.serialnumbersgenerator.exception.SetNotFoundException;
import com.pierre.serialnumbersgenerator.exception.SetNotReadyException;
import com.pierre.serialnumbersgenerator.model.SerialNumbersSet;
import com.pierre.serialnumbersgenerator.model.Settings;
import com.pierre.serialnumbersgenerator.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.Future;

@RestController
@RequiredArgsConstructor
@RequestMapping("/API")
public class APIController {

    private static final String SUCCESS_MESSAGE = """
            Serial Numbers Set %s Successfully Created, You Can Download
            the Generated Set as a CSV File Using the Following Link:
            
            localhost:8080/API/%s.csv
            """;

    @Nullable
    private Future<SerialNumbersSet> setFuture;

    private final MainService service;
    private final Settings apiSettings;

    @ResponseBody
    @PutMapping("/{name}")
    public ResponseEntity<String> requestSerialNumbersSet(@PathVariable("name") String name,
                                                          @RequestParam("quantity") Integer quantity) {

        if (!service.doesNameExist(name)) {

            if (quantity >= apiSettings.getMinQuantity() && quantity <= apiSettings.getMaxQuantity()) {

                setFuture = service.generateSerialNumbersSet(apiSettings, name, quantity);

                return ResponseEntity.ok(String.format(SUCCESS_MESSAGE, name, name));

            } else {

                throw new InvalidSetQuantityException(apiSettings.getMinQuantity(), apiSettings.getMaxQuantity());
            }

        } else {

            throw new InvalidSetNameException();
        }
    }

    @GetMapping("/{name}.csv")
    public ResponseEntity<Resource> downloadSerialNumberSet(@PathVariable("name") String name) throws IOException {

        if (service.doesNameExist(name)) {

            if (setFuture != null) {

                if (setFuture.isDone()) {

                    return getCSVFile(name);

                } else {

                    throw new SetNotReadyException();
                }

            } else {

                return getCSVFile(name);
            }

        } else {

            throw new SetNotFoundException();
        }
    }

    ResponseEntity<Resource> getCSVFile(String name) throws IOException {

        File file = service.createCSVFile(name);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}