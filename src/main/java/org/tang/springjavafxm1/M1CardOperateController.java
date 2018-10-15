package org.tang.springjavafxm1;

import de.felixroske.jfxsupport.FXMLController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.ResourceBundle;


@FXMLController
public class M1CardOperateController implements Initializable {

    @FXML
    private HBox hBox;

    @FXML
    private WebView myWeb;


    @FXML
    private Label cardLabel;

    @FXML
    private TextField physicCardField;


    // Be aware: This is a Spring bean. So we can do the following:
    @Autowired
    private M1CardOperateService m1CardOperateService;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        myWeb.getEngine().load("https://www.baidu.com");
    }

    @FXML
    private void readCard(final Event event) {
        physicCardField.setText("");
        m1CardOperateService.readPhysicCardNo(physicCardField);
    }
}

