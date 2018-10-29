package org.tang.springjavafxm1.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ResourceBundle;

@Getter
@Setter
public abstract class IndexView implements Initializable {
	protected ResourceBundle bundle;
	@FXML
	protected TabPane tabPaneMain;

	@FXML
	protected MenuBar mainMenuBar;
	@FXML
	protected Menu fileMenu;
	@FXML
	protected Menu toolsMenu;
	@FXML
	protected Menu helpMenu;
}
