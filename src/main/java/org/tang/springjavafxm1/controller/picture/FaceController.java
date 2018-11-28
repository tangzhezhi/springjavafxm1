package org.tang.springjavafxm1.controller.picture;

import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.tang.springjavafxm1.model.PictureFileTableEntity;
import hu.computertechnika.paginationfx.control.PaginationTableView;
import hu.computertechnika.paginationfx.data.ComparableCollectionDataProvider;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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

//	@FXML
//	private TextArea resultArea;

	@FXML
	private Label selectFileDirLabel;

	@FXML
	private Label saveFileDirLabel;

	@FXML
	protected TableView<PictureFileTableEntity> tableViewMain;
	@FXML
	protected TableColumn<PictureFileTableEntity, String> fileNoTableColumn;
	@FXML
	protected TableColumn<PictureFileTableEntity, String> fileNameTableColumn;

	@FXML
	private PaginationTableView<PictureFileTableEntity> paginationTableView ;

	private ObservableList<PictureFileTableEntity> tableData = FXCollections.observableArrayList();
	AtomicInteger nums = new AtomicInteger(0);


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		fileNoTableColumn.setCellValueFactory(c->c.getValue().fileNoProperty());
		fileNameTableColumn.setCellValueFactory(c->c.getValue().fileNameProperty());
//		tableViewMain.setItems(tableData);
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

		ArrayList<PictureFileTableEntity> data = new ArrayList<PictureFileTableEntity>();
		System.out.println("deal picture");
		nums.set(0);
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

				faceDetections.toList().parallelStream().forEach(rect->{
					Mat imgROI = new Mat(tmp, new Rect(rect.x, rect.y, rect.width, rect.height));
					String tmpStr = saveFileDir.getAbsolutePath()+File.separator+f.getName();
					Imgcodecs.imwrite(tmpStr,imgROI);
					nums.getAndAdd(1);
					PictureFileTableEntity pictureFileTableEntity = new PictureFileTableEntity(String.valueOf(nums.get()),tmpStr);
//					tableData.add(pictureFileTableEntity);
					data.add(pictureFileTableEntity);

				});
			});

			paginationTableView.setDataProvider(new ComparableCollectionDataProvider<>(data));
			paginationTableView.setPageSize(100);
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
