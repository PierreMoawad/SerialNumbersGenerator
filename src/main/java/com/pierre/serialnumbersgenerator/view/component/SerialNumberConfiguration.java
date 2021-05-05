package com.pierre.serialnumbersgenerator.view.component;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SerialNumberConfiguration extends VerticalLayout {

    private final H3 header;
    private final Checkbox numbers;
    private final Checkbox uppercase;
    private final Checkbox lowercase;
    private final TextField excludedChars;
    private final IntegerField length;

    public SerialNumberConfiguration() {

        header = new H3("Configuration");
        numbers = new Checkbox("Numbers");
        uppercase = new Checkbox("Uppercase Chars");
        lowercase = new Checkbox("Lowercase Chars");
        excludedChars = new TextField("Exclude Characters");
        length = new IntegerField("Serial Length");

        excludedChars.setPlaceholder("e.g. H,r,8,I,i etc..");

        add(header, numbers, uppercase, lowercase, excludedChars, length);

        setMaxWidth("250px");
    }
}
