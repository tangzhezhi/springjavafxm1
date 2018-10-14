package org.tang.springjavafxm1;

import de.felixroske.jfxsupport.FXMLController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;


@FXMLController
public class M1CardOperateController {

    @FXML
    private Label cardLabel;

    @FXML
    private TextField physicCardField;

    // Be aware: This is a Spring bean. So we can do the following:
    @Autowired
    private M1CardOperateService m1CardOperateService;

    @FXML
    private void readCard(final Event event) {
        physicCardField.setText("");
        m1CardOperateService.readPhysicCardNo(physicCardField);
    }
}

