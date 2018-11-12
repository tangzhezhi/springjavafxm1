package org.tang.springjavafxm1.controller.tools;

import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.tang.springjavafxm1.service.M1CardOperateService;
import org.tang.springjavafxm1.utils.OpenCvUtils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Lazy
@FXMLController
public class VideoController implements Initializable {

	@FXML
	private ImageView videoImage;

	@FXML
	private Button videoBtn;

	private Mat frame;
	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that realizes the video capture
	private  VideoCapture capture;
	// a flag to change the button behavior
	private static  boolean cameraActive = false;
	// the id of the camera to be used
	private static int cameraId = 0;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if(capture == null){
			capture  = new VideoCapture();
		}
	}


	@FXML
	private void closeVideoAction(ActionEvent event) throws Exception{
			// the camera is not active at this point
			this.cameraActive = false;
			this.stopAcquisition();
			System.out.println("准备关闭摄像头");
	}


	@FXML
	private void openVideoAction(ActionEvent event) throws Exception{
			// start the video capture
			this.capture.open(cameraId);
			// is the video stream available?
			if (this.capture.isOpened())
			{
				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {

					@Override
					public void run()
					{
						// effectively grab and process a single frame
						Mat frame = grabFrame();
						frame.reshape(2);
						// convert and show the frame
						Image imageToShow = OpenCvUtils.mat2Image(frame);
						updateImageView(videoImage, imageToShow);
					}
				};

				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
			}
			else
			{
				System.err.println("Impossible to open the camera connection...");
			}

	}


	/**
	 * Get a frame from the opened video stream (if any)
	 *
	 * @return the {@link Mat} to show
	 */
	private Mat grabFrame()
	{
		// init everything
		frame = new Mat();
		// check if the capture is open
		if (this.capture.isOpened())
		{
			try
			{
				// read the current frame
				this.capture.read(frame);
				// if the frame is not empty, process it
//				if (!frame.empty())
//				{
////					Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
//				}

			}
			catch (Exception e)
			{
				System.err.println("Exception during the image elaboration: " + e);
			}
		}
		return frame;
	}

	/**
	 * Stop the acquisition from the camera and release all the resources
	 */
	private void stopAcquisition()
	{
		if (this.timer!=null && !this.timer.isShutdown())
		{
			try
			{
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}

		if (this.capture!=null || this.capture.isOpened())
		{
			this.capture.release();
			this.frame.release();
		}

	}

	/**
	 * Update the {@link ImageView} in the JavaFX main thread
	 *
	 * @param view
	 *            the {@link ImageView} to update
	 * @param image
	 *            the {@link Image} to show
	 */
	private void updateImageView(ImageView view, Image image)
	{
		OpenCvUtils.onFXThread(view.imageProperty(), image);
	}

	/**
	 * On application close, stop the acquisition from the camera
	 */
	protected void setClosed()
	{
		this.stopAcquisition();
	}



}
