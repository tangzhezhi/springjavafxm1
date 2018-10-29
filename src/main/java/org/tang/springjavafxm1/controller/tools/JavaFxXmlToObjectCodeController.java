package org.tang.springjavafxm1.controller.tools;

import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import org.springframework.context.annotation.Lazy;

import java.net.URL;
import java.util.ResourceBundle;

@Lazy
@FXMLController
public class JavaFxXmlToObjectCodeController implements Initializable {
	@FXML
	private TextArea textArea1;
	@FXML
	private TextArea textArea2;
	@FXML
	private TextArea textArea3;
	@FXML
	private TextArea textArea4;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	@FXML
	private void xmlToCodeOnAction(ActionEvent event) throws Exception{
		String[] string = new String[]{"1","2","3"};
		textArea2.setText(string[0]);
		textArea3.setText(string[1]);
		textArea4.setText(string[2]);
	}

}
