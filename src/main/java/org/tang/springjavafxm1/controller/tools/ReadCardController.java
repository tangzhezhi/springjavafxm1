package org.tang.springjavafxm1.controller.tools;

import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.tang.springjavafxm1.service.M1CardOperateService;

import java.net.URL;
import java.util.ResourceBundle;

@Lazy
@FXMLController
public class ReadCardController implements Initializable {
	@FXML
	private TextField cardNoTextField;

	@FXML
	private TextField cardKeyTextField;

	@FXML
	private TextArea cardContentTextArea;


	@Autowired
	private M1CardOperateService m1CardOperateService;



	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}


	@FXML
	private void readCardAction(ActionEvent event) throws Exception{
		cardNoTextField.setText("");
		m1CardOperateService.readPhysicCardNo(cardNoTextField,cardKeyTextField,cardContentTextArea);
	}



}
