package org.tang.springjavafxm1.controller.picture;

import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.springframework.context.annotation.Lazy;
import org.tang.springjavafxm1.utils.OpenCvUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Lazy
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
	private Button selectFileBtn;

	private File[] waitDealFile ;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}


	public void selectFaceDirAction(ActionEvent actionEvent) {
		File file = directoryChooser.showDialog(selectFileDirBtn.getContextMenu());
		if (file != null && file.isDirectory()) {
			System.out.println(file.getName());
			waitDealFile = file.listFiles();
		}
	}

	public void dealPictureAction(ActionEvent actionEvent) {
		System.out.println("deal picture");

		if(waitDealFile!=null && waitDealFile.length > 0){
			CascadeClassifier faceDetector = new CascadeClassifier("D:\\work\\springjavafxm1\\target\\classes\\config\\lbpcascade_frontalface.xml");
			Arrays.stream(waitDealFile).forEach(f->{
				Mat img = Imgcodecs.imread(f.getAbsolutePath());
				Mat tmp = new Mat();
				img.copyTo(tmp);
				MatOfRect faceDetections = new MatOfRect();
				faceDetector.detectMultiScale(tmp, faceDetections);
				for(Rect rect : faceDetections.toList()){
					Mat imgROI = new Mat(tmp, new Rect(rect.x, rect.y, rect.width, rect.height));
					Imgcodecs.imwrite("C:\\Users\\Administrator\\Desktop\\11\\test.jpg",imgROI);

				}
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
}
