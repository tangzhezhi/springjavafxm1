package org.tang.springjavafxm1.controller.picture;

import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import javafx.scene.control.TextArea;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

@FXMLController
public class FaceController implements Initializable {

	private final FileChooser fileChooser = new FileChooser();

	private final DirectoryChooser directoryChooser=new DirectoryChooser();

	private final Desktop desktop = Desktop.getDesktop();

	@FXML
	private Button dealPictureBtn;

	@FXML
	private Button selectFileDirBtn;

	@FXML
	private Button saveFileDirBtn;

	@FXML
	private Button selectFileBtn;

	private File[] waitDealFile ;

	private static File saveFileDir ;

	@FXML
	private TextArea resultArea;

	@FXML
	private Label selectFileDirLabel;

	@FXML
	private Label saveFileDirLabel;



	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}


	public void selectFaceDirAction(ActionEvent actionEvent) {
		File file = directoryChooser.showDialog(selectFileDirBtn.getContextMenu());
		if (file != null && file.isDirectory()) {
			System.out.println(file.getName());
			waitDealFile = file.listFiles();
			selectFileDirLabel.setText(file.getAbsolutePath());
		}
	}

	public void dealPictureAction(ActionEvent actionEvent) {
		System.out.println("deal picture");

		if(waitDealFile!=null && waitDealFile.length > 0){
			//			CascadeClassifier faceDetector = new CascadeClassifier("D:\\work\\springjavafxm1\\target\\classes\\config\\lbpcascade_frontalface.xml");
			CascadeClassifier faceDetector = null;
			faceDetector = new CascadeClassifier(	new File(this.getClass().getResource("/").getPath()).getAbsolutePath()+"\\config\\lbpcascade_frontalface.xml");
			CascadeClassifier finalFaceDetector = faceDetector;
			Arrays.stream(waitDealFile).forEach(f->{
				Mat img = Imgcodecs.imread(f.getAbsolutePath());
				Mat tmp = new Mat();
				img.copyTo(tmp);
				MatOfRect faceDetections = new MatOfRect();
				finalFaceDetector.detectMultiScale(tmp, faceDetections);

				resultArea.setPrefRowCount(faceDetections.toList().size());
				resultArea.setPrefRowCount(2);

				faceDetections.toList().parallelStream().forEach(rect->{
					Mat imgROI = new Mat(tmp, new Rect(rect.x, rect.y, rect.width, rect.height));
					String tmpStr = saveFileDir.getAbsolutePath()+File.separator+f.getName();
					Imgcodecs.imwrite(tmpStr,imgROI);
					resultArea.appendText(tmpStr+"\n");
				});


//				for(Rect rect : faceDetections.toList()){
//					Mat imgROI = new Mat(tmp, new Rect(rect.x, rect.y, rect.width, rect.height));
////					Imgcodecs.imwrite("C:\\Users\\Administrator\\Desktop\\11\\test.jpg",imgROI);
//					System.out.println("f.getName:::::::"+f.getName());
//
//					String tmpStr = saveFileDir.getAbsolutePath()+File.separator+f.getName();
//
//					Imgcodecs.imwrite(tmpStr,imgROI);
//					resultArea.appendText(tmpStr+"\n");
//				}
			});
		}
	}

	private static void configureFileChooser(final FileChooser fileChooser){
		fileChooser.setInitialDirectory(
				new File(System.getProperty("user.home"))
		);
	}

	private void openFile(File file) {
		EventQueue.invokeLater(() -> {
			try {
				desktop.open(file);
			} catch (IOException ex) {
				 ex.printStackTrace();
			}
		});
	}

	public void selectFaceAction(ActionEvent actionEvent) {
		configureFileChooser(fileChooser);
		File file = fileChooser.showOpenDialog(selectFileDirBtn.getContextMenu());
		if (file != null) {
			openFile(file);
		}
	}

	/**
	 * 保存到
	 * @param actionEvent
	 */
	public void saveFileDirAction(ActionEvent actionEvent) {
		File file = directoryChooser.showDialog(saveFileDirBtn.getContextMenu());
		if (file != null && file.isDirectory()) {
			saveFileDir = file;
			saveFileDirLabel.setText(file.getAbsolutePath());
		}
	}
}
