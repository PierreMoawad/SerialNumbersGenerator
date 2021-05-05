package com.pierre.serialnumbersgenerator.view.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetButtons extends HorizontalLayout {

    private final Button delete;
    private final Anchor download;

    public SetButtons() {

        delete = new Button("Delete");
        download = new Anchor();
        download.getElement().setAttribute("download", true);
        download.add(new Button("Download"));
        download.setEnabled(false);

        add(download, delete);
    }
}
