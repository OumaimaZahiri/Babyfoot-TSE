package application;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * @author clemb
 * The controller class of the screen displayed when the user is asked to draw the lines necessary for the detection of events.
 */
public class LinePlotter2Scene {
	
	Mat frame = new Mat();
	Mat frameC = new Mat();
	int[] lineB = {0,0,0,0,0,0,0,0};
	int[] lineT = {0,0,0,0,0,0,0,0};
	ControllerVideo cont;
	File notice = new File(".\\src\\main\\ressources\\Notice.pdf");
	
    @FXML
    private Button buttonLineBG;
    
    @FXML
    private Button buttonLineBD;
    
    @FXML
    private Button buttonLineTG;
    
    @FXML
    private Button buttonLineTD;
    
    @FXML
    private Button drawbutton;
    
    @FXML
    private Button buttonAide;
    
    @FXML
    private Button done;
    
    @FXML
    private ImageView imageview;

    /**
     * These function handle the reception of the line drawn by the user by redefining what is to be done on the first two clicks
     * after the button to draw a line is pressed. Here for the line of the left goal.
     * @param event An FXML actionEvent
     */
    @FXML
    void tracelineBG(ActionEvent event) {
    	lineB[0]=0;
		lineB[1]=0;
		lineB[2]=0;
		lineB[3]=0;
		frame = frameC.clone();
		drawlines();
    	Scene root = imageview.getScene();
    	root.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				lineB[0] = (int) event.getSceneX();
				lineB[1] = (int) event.getSceneY();
				root.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event) {
						lineB[2] = (int) event.getSceneX();
						lineB[3] = (int) event.getSceneY();
						root.setOnMouseClicked(new EventHandler<MouseEvent>() {
							public void handle(MouseEvent event) {
							}
						});
					}
				});
				
			}
		});
    }
    
    /**
     * Same as tracelineBG but for the line of the right goal.
     * @param event An FXML actionEvent
     */
    @FXML
    void tracelineBD(ActionEvent event) {
    	lineB[4]=0;
		lineB[5]=0;
		lineB[6]=0;
		lineB[7]=0;
		frame = frameC.clone();
		drawlines();
    	Scene root = imageview.getScene();
    	root.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				lineB[4] = (int) event.getSceneX();
				lineB[5] = (int) event.getSceneY();
				root.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event) {
						lineB[6] = (int) event.getSceneX();
						lineB[7] = (int) event.getSceneY();
						root.setOnMouseClicked(new EventHandler<MouseEvent>() {
							public void handle(MouseEvent event) {
							}
						});
					}
				});
				
			}
		});
    }
    
    /**
     * Same as tracelineBG but for the line of the left field border.
     * @param event A detected FXML actionEvent
     */
    @FXML
    void tracelineTG(ActionEvent event) {
    	lineT[0]=0;
		lineT[1]=0;
		lineT[2]=0;
		lineT[3]=0;
		frame = frameC.clone();
		drawlines();
    	Scene root = imageview.getScene();
    	root.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				lineT[0] = (int) event.getSceneX();
				lineT[1] = (int) event.getSceneY();
				root.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event) {
						lineT[2] = (int) event.getSceneX();
						lineT[3] = (int) event.getSceneY();
						root.setOnMouseClicked(new EventHandler<MouseEvent>() {
							public void handle(MouseEvent event) {
							}
						});
					}
				});
				
			}
		});
    }
    
    /**
     * Same as tracelineBG but for the line of the right field border.
     * @param event An FXML actionEvent
     */
    @FXML
    void tracelineTD(ActionEvent event) {
    	lineT[4]=0;
		lineT[5]=0;
		lineT[6]=0;
		lineT[7]=0;
		frame = frameC.clone();
		drawlines();
    	Scene root = imageview.getScene();
    	root.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				lineT[4] = (int) event.getSceneX();
				lineT[5] = (int) event.getSceneY();
				root.setOnMouseClicked(new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event) {
						lineT[6] = (int) event.getSceneX();
						lineT[7] = (int) event.getSceneY();
						root.setOnMouseClicked(new EventHandler<MouseEvent>() {
							public void handle(MouseEvent event) {
							}
						});
					}
				});
				
			}
		});
    }
    
    /**
     * This function check if two points describing the lines are in the expected order, and order them if not.
     */
    private void order() {
    	if(lineT[5]>lineT[7]) {
    		int o =lineT[4];
    		int p =lineT[5];
    		lineT[4]=lineT[6];
    		lineT[5]=lineT[7];
    		lineT[6]=o;
    		lineT[7]=p;
    	}
    	if(lineT[1]>lineT[3]) {
    		int o =lineT[0];
    		int p =lineT[1];
    		lineT[0]=lineT[2];
    		lineT[1]=lineT[3];
    		lineT[2]=o;
    		lineT[3]=p;
    	}
    	if(lineB[5]>lineB[7]) {
    		int o =lineB[4];
    		int p =lineB[5];
    		lineB[4]=lineB[6];
    		lineB[5]=lineB[7];
    		lineB[6]=o;
    		lineB[7]=p;
    	}
    	if(lineB[1]>lineB[3]) {
    		int o =lineB[0];
    		int p =lineB[1];
    		lineB[0]=lineB[2];
    		lineB[1]=lineB[3];
    		lineB[2]=o;
    		lineB[3]=p;
    	}
    }
    
    /**
     * This function handle the press of the drawLines button and call the drawlines() function.
     * @param event An FXML action Event
     */
    @FXML
    void drawlines(ActionEvent event) {
    	drawlines();
    }
    
    /**
     * This function handle the press of the Return button hand close the line drawing screen, transmitting the informations
     * to the controller of the main screen, only if all lines have been drawn.
     * @param event  An FXML actionEvent
     */
    @FXML
    void goback(ActionEvent event) {
    	Boolean alllines = true;
    	for(int i=0;i<2;i++) {
    		if(lineB[4*i+1] == lineB[4*i+2]) {
    			alllines = false;
    		}
    		if(lineT[4*i+1] == lineT[4*i+2]) {
    			alllines = false;
    		}
    	}
    	if(alllines){
    		order();
    		cont.setlineB(lineB);
        	cont.setlineT(lineT);
        	Stage stage = (Stage) done.getScene().getWindow();
        	stage.close();
    	}
    }
    
    /**
     * This function use the LinePlotter2 Class to draw the already entered lines on the displayed frame.
     */
    private void drawlines() {
    	frame = LinePlotter2.drawLine(frame, lineB, true);
    	frame = LinePlotter2.drawLine(frame, lineT, false);
		Image img = mat2Image(frame);
		imageview.setImage(img);
    }
    
    /**
     * This function initialize an object of this class to start drawing lines
     * @param path  The path of the video for which the lines are to be drawn
     * @param controlleurV  The controller of the main screen
     */
    public void initialization(String path,ControllerVideo controlleurV) {
    	cont = controlleurV;
    	VideoCapture capture=new VideoCapture();
		capture.open(path);
		capture.read(frame);
		capture.read(frame);
		capture.read(frame);
		capture.read(frame);
		capture.read(frame);
		capture.release();
		Image img = mat2Image(frame);
		frameC = frame.clone();
		imageview.setImage(img);
    }
    
    /**
     * This function convert an openCV Mat into a displayable image
     * @param frame The Mat to be transformed into a displayable image
     * @return The converted image
     */
    private static Image mat2Image(Mat frame) {
	    // create a temporary buffer
	    MatOfByte buffer = new MatOfByte();
	    // encode the frame in the buffer, according to the PNG format
	    Imgcodecs.imencode(".png", frame, buffer);
	    // build and return an Image created from the image encoded in the
	    // buffer
	    return new Image(new ByteArrayInputStream(buffer.toArray()));
	}
    
    /**
     * A get getter for the goal lines array
     */
    public int[] getButs() {
    	return lineB;
    }
    
    /**
     * A get getter for the field lines array
     */
    public int[] getTerrain() {
    	return lineT;
    }
    
    /**
     * This function handle the press of the help button and display the explanation PDF.
     */
    @FXML
    void help() throws IOException {
    	if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().open(notice);
		} else {
			System.out.println("Awt Desktop is not supported!");
		}
    }
}
