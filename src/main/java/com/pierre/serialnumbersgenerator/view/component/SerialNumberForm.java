package com.pierre.serialnumbersgenerator.view.component;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SerialNumberForm extends HorizontalLayout {

    private final SetConfiguration setConfiguration;
    private final SerialNumberConfiguration serialNumberConfiguration;
    private final SetView setView;

    public SerialNumberForm() {

        setConfiguration = new SetConfiguration();
        serialNumberConfiguration = new SerialNumberConfiguration();
        setView = new SetView();

        add(setConfiguration, new Divider(), serialNumberConfiguration, setView);

        setWidth("1200px");
    }
}
