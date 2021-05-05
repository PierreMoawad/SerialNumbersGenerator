package com.pierre.serialnumbersgenerator.view.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetConfiguration extends VerticalLayout {

    private final TextField name;
    private final IntegerField quantity;
    private final Button create;

    public SetConfiguration() {

        name = new TextField("Set Name");
        quantity = new IntegerField("Number of Serials");
        create = new Button("Create");

        name.setValue("");
        name.setPlaceholder("Unique Name");
        quantity.setValue(10);
        quantity.setPlaceholder("1-999");

        add(name, quantity, create);

        setMaxWidth("250px");
    }
}
