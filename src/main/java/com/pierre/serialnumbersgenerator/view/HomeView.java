package com.pierre.serialnumbersgenerator.view;

import com.pierre.serialnumbersgenerator.model.SerialNumbersSet;
import com.pierre.serialnumbersgenerator.model.Settings;
import com.pierre.serialnumbersgenerator.service.MainService;
import com.pierre.serialnumbersgenerator.view.component.SerialNumberForm;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Route("")
@PageTitle("Serial Numbers Generator")
@CssImport("./views/serialnumbersgenerator/serial-numbers-generator-view.css")
@EnableConfigurationProperties(HomeView.class)
public class HomeView extends VerticalLayout {

    private final SerialNumberForm serialNumberForm;
    private final Binder<Settings> binder;
    private final Settings uiSettings;

    public HomeView(MainService service, Settings uiSettings) {

        addClassName("serial-numbers-generator-view");

        this.uiSettings = uiSettings;
        Set<SerialNumbersSet> serialNumbersSets = service.getSerialNumbersSets();
        serialNumberForm = new SerialNumberForm();
        binder = new Binder<>(Settings.class);

        serialNumberForm.getSetView().getSerialNumbersSetGrid().setItems(serialNumbersSets);
        serialNumberForm.getSetView().getSerialNumbersSetGrid().getDataProvider().refreshAll();

        setCreateButtonListener(service, serialNumbersSets);
        setGridSelectionListener(service);
        setDeleteButtonListener(service, serialNumbersSets);
        bindConfigComponents();
        constrainConfigComponents();
        initializeCharPoolCheckBoxes();

        add(new H2("Serial Numbers Generator"), new Hr(), serialNumberForm);
    }

    void setCreateButtonListener(MainService service, Set<SerialNumbersSet> serialNumbersSets) {

        serialNumberForm.getSetConfiguration().getCreate().addClickListener(e -> {

            TextField name = serialNumberForm.getSetConfiguration().getName();
            IntegerField quantity = serialNumberForm.getSetConfiguration().getQuantity();

            try {

                binder.writeBean(uiSettings);

            } catch (ValidationException validationException) {

                log.error(validationException.getMessage());
            }

            if (service.validateExcludedChars(uiSettings)) {

               if (!name.getValue().isEmpty()) {

                   if (!service.doesNameExist(name.getValue())){

                       Future<SerialNumbersSet> setFuture =
                               service.generateSerialNumbersSet(uiSettings, name.getValue(), quantity.getValue());

                       try {

                           serialNumbersSets.add(setFuture.get());

                       } catch (InterruptedException | ExecutionException exception) {

                           exception.printStackTrace();
                       }

                       serialNumberForm.getSetView().getSerialNumbersSetGrid().getDataProvider().refreshAll();

                       Notification.show("Serial Numbers Set " + name.getValue() + " Successfully Created");

                   } else {

                       Notification.show("Set Name Already Exists, Please Choose a Unique Name");
                   }

                } else {

                   Notification.show("Set Name is Empty, Please Type In a Valid Name");
               }

            } else {

                Notification.show("Excluded Characters Field Format Not Accepted");
            }
        });
    }

    void setGridSelectionListener(MainService service) {

        serialNumberForm.getSetView().getSerialNumbersSetGrid().getSelectionModel().addSelectionListener(e -> {

            Optional<SerialNumbersSet> selectedSerialNumbersSet =
                    serialNumberForm.getSetView().getSerialNumbersSetGrid().getSelectionModel().getFirstSelectedItem();

            if (selectedSerialNumbersSet.isPresent()) {

                String name = selectedSerialNumbersSet.get().getName();

                StreamResource resource = getStreamResource(name, service);

                serialNumberForm.getSetView().getSetButtons().getDownload().setEnabled(true);
                serialNumberForm.getSetView().getSetButtons().getDownload().setHref(resource);

            } else {

                serialNumberForm.getSetView().getSetButtons().getDownload().setEnabled(false);
            }
        });
    }

    StreamResource getStreamResource(String name, MainService service) {

        try {

            File csvFile = service.createCSVFile(name);

            return new StreamResource(csvFile.getName(), () -> {

                try {

                    return new ByteArrayInputStream(FileUtils.readFileToByteArray(csvFile));

                } catch (IOException ioException) {

                    log.error(ioException.getMessage());
                    Notification.show("Error Downloading the Selected Set");
                    return null;
                }
            });

        } catch (IOException exception) {

            log.error(exception.getMessage());
            Notification.show("Error Downloading the Selected Set");
        }

        return null;
    }

    void setDeleteButtonListener(MainService service, Set<SerialNumbersSet> serialNumbersSets) {

        serialNumberForm.getSetView().getSetButtons().getDelete().addClickListener(e -> {

            Optional<SerialNumbersSet> selectedSerialNumbersSet =
                    serialNumberForm.getSetView().getSerialNumbersSetGrid().getSelectionModel().getFirstSelectedItem();

            if (selectedSerialNumbersSet.isPresent()) {

                service.deleteSerialNumbersSet(selectedSerialNumbersSet.get());
                serialNumbersSets.remove(selectedSerialNumbersSet.get());
                serialNumberForm.getSetView().getSerialNumbersSetGrid().getDataProvider().refreshAll();

                Notification.show(
                        "Serial Numbers Set " + selectedSerialNumbersSet.get().getName() + " Successfully Deleted");

                serialNumberForm.getSetView().getSerialNumbersSetGrid().getSelectionModel().deselectAll();

            } else {

                Notification.show("Please Select a Set to Delete");
            }
        });
    }

    void bindConfigComponents() {

        binder.forField(serialNumberForm.getSerialNumberConfiguration().getNumbers())
                .bind(Settings::isNumeric, Settings::setNumeric);
        binder.forField(serialNumberForm.getSerialNumberConfiguration().getUppercase())
                .bind(Settings::isUppercase, Settings::setUppercase);
        binder.forField(serialNumberForm.getSerialNumberConfiguration().getLowercase())
                .bind(Settings::isLowercase, Settings::setLowercase);
        binder.forField(serialNumberForm.getSerialNumberConfiguration().getExcludedChars())
                .bind(Settings::getExcludedChars, Settings::setExcludedChars);
        binder.forField(serialNumberForm.getSerialNumberConfiguration().getLength())
                .bind(Settings::getLength, Settings::setLength);
        binder.readBean(uiSettings);
    }

    void constrainConfigComponents() {

        Checkbox numbers = serialNumberForm.getSerialNumberConfiguration().getNumbers();
        Checkbox uppercase = serialNumberForm.getSerialNumberConfiguration().getUppercase();
        Checkbox lowercase = serialNumberForm.getSerialNumberConfiguration().getLowercase();
        IntegerField length = serialNumberForm.getSerialNumberConfiguration().getLength();
        IntegerField quantity = serialNumberForm.getSetConfiguration().getQuantity();

        numbers.addValueChangeListener(e -> {

            try {

                binder.writeBean(uiSettings);

            } catch (ValidationException validationException) {

                log.error(validationException.getMessage());
            }

            if (uiSettings.isNumeric()) {

                uppercase.setEnabled(true);
                lowercase.setEnabled(true);

            } else {

                if (uiSettings.isUppercase() && !uiSettings.isLowercase()) {

                    uppercase.setEnabled(false);

                } else if (!uiSettings.isUppercase() && uiSettings.isLowercase()) {

                    lowercase.setEnabled(false);
                }
            }
        });

        uppercase.addValueChangeListener(e -> {

            try {

                binder.writeBean(uiSettings);

            } catch (ValidationException validationException) {

                log.error(validationException.getMessage());
            }

            if (uiSettings.isUppercase()) {

                numbers.setEnabled(true);
                lowercase.setEnabled(true);

            } else {

                if (uiSettings.isNumeric() && !uiSettings.isLowercase()) {

                    numbers.setEnabled(false);

                } else if (!uiSettings.isNumeric() && uiSettings.isLowercase()) {

                    lowercase.setEnabled(false);
                }
            }
        });

        lowercase.addValueChangeListener(e -> {

            try {

                binder.writeBean(uiSettings);

            } catch (ValidationException validationException) {

                log.error(validationException.getMessage());
            }

            if (uiSettings.isLowercase()) {

                numbers.setEnabled(true);
                uppercase.setEnabled(true);

            } else {

                if (uiSettings.isNumeric() && !uiSettings.isUppercase()) {

                    numbers.setEnabled(false);

                } else if (!uiSettings.isNumeric() && uiSettings.isUppercase()) {

                    uppercase.setEnabled(false);
                }
            }
        });

        length.addValueChangeListener(e -> {

            try {

                binder.writeBean(uiSettings);

            } catch (ValidationException validationException) {

                log.error(validationException.getMessage());
            }

            if (length.getValue() == null) {

                length.setValue(uiSettings.getDefaultLength());
            }

            if (length.getValue() < uiSettings.getMinLength()) {

                length.setValue(uiSettings.getMinLength());
            }

            if (length.getValue() > uiSettings.getMaxLength()) {

                length.setValue(uiSettings.getMaxLength());
            }
        });

        quantity.addValueChangeListener(e -> {
            
            if (quantity.getValue() == null || quantity.getValue() < uiSettings.getMinQuantity()) {

                quantity.setValue(uiSettings.getMinQuantity());
            }

            if (quantity.getValue() > uiSettings.getMaxQuantity()) {

                quantity.setValue(uiSettings.getMaxQuantity());
            }
        });
    }

    void initializeCharPoolCheckBoxes() {

        boolean numeric = uiSettings.isNumeric();
        boolean uppercase = uiSettings.isUppercase();
        boolean lowercase = uiSettings.isLowercase();

        serialNumberForm.getSerialNumberConfiguration().getNumbers().setValue(!numeric);
        serialNumberForm.getSerialNumberConfiguration().getUppercase().setValue(!uppercase);
        serialNumberForm.getSerialNumberConfiguration().getLowercase().setValue(!lowercase);
        serialNumberForm.getSerialNumberConfiguration().getNumbers().setValue(numeric);
        serialNumberForm.getSerialNumberConfiguration().getUppercase().setValue(uppercase);
        serialNumberForm.getSerialNumberConfiguration().getLowercase().setValue(lowercase);
    }
}
