package com.pierre.serialnumbersgenerator.view.component;

import com.pierre.serialnumbersgenerator.model.SerialNumbersSet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;

@Getter
@Setter
public class SetView extends VerticalLayout {

    private final Grid<SerialNumbersSet> serialNumbersSetGrid;
    private final SetButtons setButtons;

    public SetView() {

        serialNumbersSetGrid = new Grid<>(SerialNumbersSet.class);
        setButtons = new SetButtons();

        serialNumbersSetGrid.setColumns("name", "created", "quantity");

        GridSortOrder<SerialNumbersSet> order = new GridSortOrder<>(
                serialNumbersSetGrid.getColumns().get(1),
                SortDirection.DESCENDING
        );

        serialNumbersSetGrid.sort(Collections.singletonList(order));

        add(serialNumbersSetGrid, setButtons);

        setWidth("750px");
    }
}
