package org.tang.springjavafxm1.controller.picture;

import com.esotericsoftware.kryo.util.IntArray;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.serde.binary.BinarySerde;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.tang.springjavafxm1.entity.Face;
import org.tang.springjavafxm1.model.PictureFileTableEntity;
import hu.computertechnika.paginationfx.control.PaginationTableView;
import hu.computertechnika.paginationfx.data.ComparableCollectionDataProvider;
import org.tang.springjavafxm1.model.facenet.InceptionResNetV1;
import org.tang.springjavafxm1.service.FaceService;
import org.tang.springjavafxm1.utils.facenet.FaceDetection;
import org.tang.springjavafxm1.utils.facenet.FaceDetector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@FXMLController
public class FacePictureController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(FaceDetection.class);
    private static final NativeImageLoader loader = new NativeImageLoader();
    private static final HashMap<String, INDArray> imageLibMap = new HashMap<String, INDArray>();
    private static FaceDetector detector;
    private static ComputationGraph graph;
    private static double threshold = 1.1;

    private final FileChooser fileChooser = new FileChooser();

    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    private final Desktop desktop = Desktop.getDesktop();

    @FXML
    private Button dealPictureBtn;

    @FXML
    private Button selectFileDirBtn;

    @FXML
    private Button saveFileDirBtn;

    @FXML
    private Button selectFileBtn;

    private File[] waitDealFile;

    private static File saveFileDir;

//	@FXML
//	private TextArea resultArea;

    @FXML
    private Label selectFileDirLabel;

    @FXML
    private Label saveFileDirLabel;

    @FXML
    private Label selectNewFaceLibDirLabel;

    @FXML
    protected TableView<PictureFileTableEntity> tableViewMain;
    @FXML
    protected TableColumn<PictureFileTableEntity, String> fileNoTableColumn;
    @FXML
    protected TableColumn<PictureFileTableEntity, String> fileNameTableColumn;

    @FXML
    protected TableColumn<PictureFileTableEntity, String> humanNameTableColumn;

    @FXML
    private PaginationTableView<PictureFileTableEntity> paginationTableView;

    private ObservableList<PictureFileTableEntity> tableData = FXCollections.observableArrayList();
    AtomicInteger nums = new AtomicInteger(0);

    @Autowired
    private FaceService faceService;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileNoTableColumn.setCellValueFactory(c -> c.getValue().fileNoProperty());
        fileNameTableColumn.setCellValueFactory(c -> c.getValue().fileNameProperty());
        humanNameTableColumn.setCellValueFactory(c -> c.getValue().humanNameProperty());

        paginationTableView.getTableView().setRowFactory(tv -> {
            TableRow<PictureFileTableEntity> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    PictureFileTableEntity rowData = row.getItem();
                    System.out.println(rowData);
                }
            });
            return row;
        });

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                initHumanLib();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executorService.shutdown();

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
        if (waitDealFile != null && waitDealFile.length > 0) {
            //			CascadeClassifier faceDetector = new CascadeClassifier("D:\\work\\springjavafxm1\\target\\classes\\config\\lbpcascade_frontalface.xml");
            CascadeClassifier faceDetector = null;
            faceDetector = new CascadeClassifier(new File(this.getClass().getResource("/").getPath()).getAbsolutePath() + "\\config\\lbpcascade_frontalface.xml");
            CascadeClassifier finalFaceDetector = faceDetector;
            Arrays.stream(waitDealFile).forEach(f -> {
                Mat img = Imgcodecs.imread(f.getAbsolutePath());
                Mat tmp = new Mat();
                img.copyTo(tmp);
                MatOfRect faceDetections = new MatOfRect();
                finalFaceDetector.detectMultiScale(tmp, faceDetections);

                faceDetections.toList().parallelStream().forEach(rect -> {
                    Mat imgROI = new Mat(tmp, new Rect(rect.x, rect.y, rect.width, rect.height));
                    String tmpStr = saveFileDir.getAbsolutePath() + File.separator + f.getName();
                    Imgcodecs.imwrite(tmpStr, imgROI);
                    nums.getAndAdd(1);

                    String humanName = "";

                    humanName = dealPictureGetHumanName(f);

                    System.out.println("humanName:::::" + humanName);

                    PictureFileTableEntity pictureFileTableEntity = new PictureFileTableEntity(String.valueOf(nums.get()), tmpStr, humanName);
//					tableData.add(pictureFileTableEntity);
                    data.add(pictureFileTableEntity);

                });
            });

            paginationTableView.setDataProvider(new ComparableCollectionDataProvider<>(data));
            paginationTableView.setPageSize(50);
        }
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
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
     *
     * @param actionEvent
     */
    public void saveFileDirAction(ActionEvent actionEvent) {
        File file = directoryChooser.showDialog(saveFileDirBtn.getContextMenu());
        if (file != null && file.isDirectory()) {
            saveFileDir = file;
            saveFileDirLabel.setText(file.getAbsolutePath());
        }
    }

    public String dealPictureGetHumanName(File img) {
        String label = "none";
        try {
            System.out.print("Insert image path:");
            File file = img;
            long start = System.currentTimeMillis();
            System.out.println("before" + start);
            INDArray factor = getFaceFactor(file);

            System.out.println("after" + (System.currentTimeMillis() - start));
            if (factor == null) {
                System.out.println("error:cannot detect face.");
            }

            double minVal = Double.MAX_VALUE;

            for (Map.Entry<String, INDArray> entry : imageLibMap.entrySet()) {
                INDArray tmp = factor.sub(entry.getValue());
                tmp = tmp.mul(tmp).sum(1);
                double tmpVal = tmp.getDouble(0);
                if (log.isDebugEnabled()) {
                    log.debug("current factor is {}", factor);
                    log.debug("similarity with {} is {}", entry.getKey(), tmp);
                }
                if (tmpVal < minVal) {
                    minVal = tmpVal;
                    label = entry.getKey();
                }
            }

            if (minVal < threshold) {
                System.out.println("this is " + label + "(" + minVal + ").");
            } else {
                System.out.println("cannot recognize this person, but the similar one is "
                        + label + "(" + minVal + ").");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return label;
    }


    public void initHumanLib() throws Exception {
        detector = new FaceDetector();
        InceptionResNetV1 v1 = new InceptionResNetV1();
        v1.init();
        v1.loadWeightData();
        graph = v1.getGraph();
        File libPath = new File(new File(this.getClass().getResource("/").getPath()).getAbsolutePath() + "\\humanFaceLib");
        if (!libPath.exists() || !libPath.isDirectory()) {
            throw new RuntimeException("path " + libPath + "is not directory!");
        }
        log.info("loading face library from directory {}, threshold is {}", libPath, threshold);

        List<Face> list = faceService.queryAllFace();

        if (list == null || list.size() == 0) {
            for (File file : libPath.listFiles()) {
                if (!file.isFile()) {
                    log.info("skipped directory:{}", file);
                    continue;
                }
                try {
                    String label = file.getName();
                    int labelIdx = label.lastIndexOf('.');
                    if (labelIdx != -1) {
                        label = label.substring(0, labelIdx);
                    }
                    log.info("Loading image {}......", file);
                    INDArray factor = getFaceFactor(file);
                    if (factor == null) {
                        continue;
                    }


                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    String finalLabel = label;
                    executorService.submit(() -> {
                        String jsonString = "";
                        String shapeString = "";
                        try {
                            Gson gson = new Gson();
                            jsonString = gson.toJson(factor.data().asDouble());
                            long[] shape = factor.shape();
                            shapeString = gson.toJson(shape);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            faceService.addFace(Face.builder().label(finalLabel).shape(shapeString).data(jsonString).build());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                    executorService.shutdown();

                    imageLibMap.put(label, factor);
                    log.info("Face of {} loaded.", label);
                    if (log.isDebugEnabled()) {
                        log.debug("factor of {} is {}", label, factor);
                    }
                } catch (IOException e) {
                    log.warn("Exception occured while loading img file:" + file, e);
                }
            }
        } else {
            list.stream().forEach(face -> {

                Gson gson = new Gson();
                double[] datas = gson.fromJson(face.getData(), double[].class);
                long[] shape = gson.fromJson(face.getShape(), long[].class);

                INDArray factor = Nd4j.create(datas, shape);

                String label = face.getLabel();
                imageLibMap.put(label, factor);
            });
        }

        log.info("load faces complete.");
        System.out.println("start detect");
    }


    private static INDArray getFaceFactor(File img) throws IOException {
        INDArray srcImg = loader.asMatrix(img);
        INDArray[] detection = new INDArray[0];
        try {
            detection = detector.getFaceImage(srcImg, 160, 160);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (detection == null) {
            log.warn("no face detected in image file:{}", img);
            return null;
        }
        if (detection.length > 1) {
            log.warn("{} faces detected in image file:{}, the first detected face will be used.",
                    detection.length, img);
        }

        if(detection!=null && detection.length > 0){
            INDArray output[] = graph.output(InceptionResNetV1.prewhiten(detection[0]));
            return output[1];
        }
        return null;
    }

    public void selectNewFaceLibDirAction(ActionEvent actionEvent) {
        File file = directoryChooser.showDialog(selectFileDirBtn.getContextMenu());
        if (file != null && file.isDirectory()) {
            System.out.println(file.getName());
            waitDealFile = file.listFiles();
            selectNewFaceLibDirLabel.setText(file.getAbsolutePath());
        }

    }

    public void dealNewFaceLibDirAction(ActionEvent actionEvent) {
        for (File file : waitDealFile) {
            if (!file.isFile()) {
                log.info("skipped directory:{}", file);
                continue;
            }
            String label = file.getName();
            int labelIdx = label.lastIndexOf('.');
            if (labelIdx != -1) {
                label = label.substring(0, labelIdx);
            }
            log.info("Loading image {}......", file);
            INDArray factor = null;
            try {
                factor = getFaceFactor(file);
            } catch (IOException e) {
//                    e.printStackTrace();
                continue;
            }
            if (factor == null) {
                continue;
            }


            ExecutorService executorService = Executors.newSingleThreadExecutor();
            String finalLabel = label;
            INDArray finalFactor = factor;
            executorService.submit(() -> {
                String jsonString = "";
                String shapeString = "";
                try {
                    Gson gson = new Gson();
                    jsonString = gson.toJson(finalFactor.data().asDouble());
                    long[] shape = finalFactor.shape();
                    shapeString = gson.toJson(shape);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    faceService.addFace(Face.builder().label(finalLabel).shape(shapeString).data(jsonString).build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            executorService.shutdown();

            imageLibMap.put(label, factor);
            log.info("Face of {} loaded.", label);
            if (log.isDebugEnabled()) {
                log.debug("factor of {} is {}", label, factor);
            }
        }
    }
}
