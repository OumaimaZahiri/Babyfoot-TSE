package application;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import nu.pattern.OpenCV;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

/**
 * @author Enguerran De Larocque Latour Ilyas Tibari Oumaima Zahiri Clement Bourlet Maxime Boichon
 * This class is the controller of the interface handling the image by image mode.
 */


public class ControllerFrame{
	static{ OpenCV.loadShared();}
	
	ArrayList<int[]> Events = new ArrayList<int[]>();
	
	@FXML
	private ImageView imageView;
	
	@FXML
	private Button playButton, pauseButton, restartButton, chooseFileButton, switchSceneButton, matchHighlightButton;
	
	@FXML
	private Label timeLabel;

	
	@FXML
	private TextField inputNumber;
	
	private ArrayList<Mat> frames;
	private VideoCapture videoCapture;
	
	private int numberOfFrames;
	private int actualFrame;
	private int indexDeplacement;
	
	private double time;
	private int minutes;
	private int seconds;
	@SuppressWarnings("unused")
	private int multiplier;
	
	private String path;
	private String pathMarked;
	private String pathResum;
	private String chosenpath;
	
	private String fileName;
	
	@FXML
	private Label titleLabel;
	
	/**
	 * This method display the next image of the video
	 */
	public void nextImage() {
		if (actualFrame + indexDeplacement < numberOfFrames - 1) {
			indexDeplacement += 1;
			time += 1/videoCapture.get(Videoio.CAP_PROP_FPS);
			if (indexDeplacement > 10) {
				actualFrame += indexDeplacement;
				loadFramesAround();
			}
			updateScene();
		}
	}
	
	/**
	 * This method display the previous image of the video
	 */
	public void previousImage() {
		if (actualFrame + indexDeplacement > 0) {
			indexDeplacement -= 1;
			time -= 1/videoCapture.get(Videoio.CAP_PROP_FPS);
			if (indexDeplacement < -10) {
				actualFrame -= indexDeplacement;
				loadFramesAround();
			}
			updateScene();
		}
	}
	
	/**
	 * This method display the image of the input time
	 */
	public void goToInputImage() {
		splitMinAndSec(inputNumber.getText());
		
		int number = Math.min(Math.max(10, convertTimeToFrameNumber()),numberOfFrames);
		actualFrame = number;
		
		loadFramesAround();
		updateScene();
	}
	
	/**
	 * This method display the last image of the video
	 */
	public void goToEnd() {
		
		actualFrame = numberOfFrames;
		indexDeplacement = 10;
		time = videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT) / videoCapture.get(Videoio.CAP_PROP_FPS);
		loadFramesAround();
		updateScene();
		
	}

	/**
	 * This method display the first image of the video
	 */
	public void goToStart() {
		
		actualFrame = 10;
		time = 0;
		indexDeplacement = -10;
		loadFramesAround();
		updateScene();
		
	}
	
	/**
	 * This method receives the data from the other controller
	 * @param pathe path of the video
	 * @param pathMarkede path of the marked video
	 * @param pathResume path of the resume video
	 * @param chosenpathe path of the video to display
	 * @param videoTime time of the video
	 * @param Event list of the events found in the video
	 * @throws IOException
	 */
	public void receiveDataFromScene(String pathe, String pathMarkede, String pathResume, String chosenpathe, Duration videoTime, ArrayList<int[]> Event) throws IOException {
		if (chosenpathe != null) {
			time = videoTime.toSeconds();
			path = pathe;
			pathMarked = pathMarkede;
			chosenpath = chosenpathe;
			pathResum = pathResume;
			Events = Event;
			Path p = Paths.get(chosenpath);
			String nameWithExt = p.getFileName().toString();
			fileName = nameWithExt.substring(0, nameWithExt.length() - 4);
			if (fileName.contains("Marked")) {
				fileName = fileName.substring(0, fileName.length()-6);
			}
			titleLabel.setText(fileName);
			startCapture(chosenpath);
			loadFramesAround();
			updateScene();
			
			DoubleProperty widthProp = imageView.fitWidthProperty();
			DoubleProperty heightProp = imageView.fitHeightProperty();

			widthProp.bind(Bindings.selectDouble(imageView.sceneProperty(), "width"));
			heightProp.bind(Bindings.selectDouble(imageView.sceneProperty(), "height"));
		}   
	}
	
	/**
	 * This method transmit data to the other controller and switch the scene
	 * @param event 
	 */
	
	public void switchToVideoScene(ActionEvent event) {
		FXMLLoader loader = null;
		Parent root = null;
		
		try {
			 loader = new FXMLLoader(getClass().getResource("SceneVideo.fxml"));
			 root = loader.load();
			 ControllerVideo controller = loader.getController();
			 controller.receiveDataFromScene(path, pathMarked, pathResum, chosenpath, time, Events);
			 Stage stage = (Stage) imageView.getScene().getWindow();
			 stage.setScene(new Scene(root));
			 
		} catch (IOException e) {
			 System.err.println(String.format("Error: %s", e.getMessage()));
		}
	}
	
	/**
	 * This method start the reading frame by frame of the video
	 * @param s path of the video
	 */
	public void startCapture(String s) {
		if (videoCapture == null) {
			videoCapture = new VideoCapture();
			videoCapture.open(s);
			numberOfFrames = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT) - 10;
			actualFrame = Math.min(Math.max(10, convertTimeToFrameNumber()),numberOfFrames);
			indexDeplacement = -10;
		}
		else {
			if (!videoCapture.isOpened()) {
				videoCapture = new VideoCapture();
				videoCapture.open(s);
				numberOfFrames = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT) - 10;
				actualFrame = Math.min(Math.max(10, convertTimeToFrameNumber()),numberOfFrames);
				indexDeplacement = -10;
			}
		}
	}
	
	/**
	 * This method convert matrix into javaFX image
	 * @param frame
	 * @return
	 */
	public static Image mat2Image(Mat frame) {
	    // create a temporary buffer
	    MatOfByte buffer = new MatOfByte();
	    // encode the frame in the buffer, according to the PNG format
	    Imgcodecs.imencode(".png", frame, buffer);
	    // build and return an Image created from the image encoded in the
	    // buffer
	    return new Image(new ByteArrayInputStream(buffer.toArray()));
	}
	
	/**
	 * This method load a list of frames around the frame to display
	 */
	public void loadFramesAround() {
		
		
		if (frames != null) {
			frames.clear();
		}
		else {
			frames = new ArrayList<Mat>();
		}
		
		for (int i = 0; i < 21; i++) {
			if (actualFrame >= 10 && actualFrame <= numberOfFrames) {
				frames.add(selectFrame(videoCapture, actualFrame - 10 + i));
			}
			
		}
		indexDeplacement = 0;
		
		
	}
	
	/**
	 * This method update the scene on the application
	 */
	public void updateScene() {
		imageView.setImage(mat2Image(frames.get(10 + indexDeplacement)));
		
		minutes = 0;
		seconds = (int) Math.floor(time);
		
		while (seconds >= 60) {
			minutes++;
			seconds -= 60;
		}
		
		if (minutes == 0) {
			timeLabel.setText(seconds + " s");
		}
		else {
			timeLabel.setText(minutes + " m " + seconds + " s");
		}
		
	}
	
	/**
	 * This method change the video displayed into the marked one
	 * @param event
	 */
	public void videoWithMarkers(ActionEvent event) {
		File file = new File(pathMarked);
		if(file.exists()) {
			multiplier = 1;
			if (file != null) {
				if (videoCapture != null) {
					videoCapture.release();
				}
				startCapture(pathMarked);
				loadFramesAround();
				updateScene();
			}	
			chosenpath = pathMarked;
		}
	}
	
	/**
	 * This method change the video to display into the original video
	 * @param event
	 */
	public void OriginalVideo(ActionEvent event) {
		File file = new File(path);
		multiplier = 10;
		if (file != null) {
			path = file.getPath();
			
			if (videoCapture != null) {
				videoCapture.release();
			}
			
			startCapture(path);
			loadFramesAround();
			updateScene();
		}
		chosenpath = path;
	}
	
	/**
	 * This method convert the input string into seconds
	 * @param s
	 */
	public void splitMinAndSec(String s) {
		System.out.println(s);
		if (s != null) {
			String[] MinAndSec = s.split("\\."); // match either with dots or commas
			
			if (MinAndSec.length >= 2) {
				minutes = Integer.parseInt(MinAndSec[0]);
				seconds = Integer.parseInt(MinAndSec[1]);
			}
			if (MinAndSec.length == 1) {
				minutes = Integer.parseInt(MinAndSec[0]);
				seconds = 0;
			}
			
		}
		time = 60 * minutes + seconds;
		
	}
	
	/**
	 * This method convert time into a frame number
	 * @return
	 */
	public int convertTimeToFrameNumber() {
		return (int) (Math.floor(time) * videoCapture.get(Videoio.CAP_PROP_FPS));
	}
	
	/**
	 * This method extract the wanted frame from the video
	 * @param cap videoCapture of the selected video
	 * @param ind number of the frame to extract
	 * @return matrix of the frame
	 */
	public static Mat selectFrame(VideoCapture cap, int ind) {
		Mat frame = new Mat();
		cap.set(Videoio.CAP_PROP_POS_FRAMES, ind);
		cap.read(frame);
		return frame;
	}
}
