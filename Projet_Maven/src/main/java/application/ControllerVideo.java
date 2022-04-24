package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import nu.pattern.OpenCV;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;

import java.util.ArrayList;

/**
 * @author Enguerran De Larocque Latour Ilyas Tibari Oumaima Zahiri Clement Bourlet Maxime Boichon
 * This class is the controller of the main screen of the application.
 */

public class ControllerVideo{
	static{ OpenCV.loadShared();}
	
	int[] lineB = {0,0,0,0,0,0,0,0}; /* The array containing the coordinates of the back of the goals. */
	int[] lineT = {0,0,0,0,0,0,0,0}; /* The array containing the coordinates of the field's corners. */
	
	ArrayList<int[]> Circles = new ArrayList<int[]>();
	ArrayList<int[]> Events = new ArrayList<int[]>();
	
	ArrayList<Boolean> allConfirmed = new ArrayList<Boolean>();
	ArrayList<Boolean> allTrue = new ArrayList<Boolean>();
	
	Thread myThread, ThreadScore, ThreadResum;
	ControllerVideo contvid = this;
	
	@FXML
	private MediaView mediaView;
	
	@FXML
	private Button playButton, pauseButton, restartButton, chooseFileButton, switchSceneButton, matchHighlightButton, buttontraiter, buttonredraw;
	
	@FXML
	private MenuButton Markers, eventList, confirmEvent;
	
	@FXML
	private Label playTime, AffScore;
	
	@FXML
	private Slider videoNavigation;
	
	private Media media;
	private MediaPlayer mediaPlayer;
	private InvalidationListener sliderChangeListener = o-> {
		updateValuesVideo();
	};
	private InvalidationListener videoChangeListener = o -> {
		updateValuesSlider();
	};
	
	private boolean flagVideoTreated;
	private String chosenpath;
	private String pathMarked;
	private String path;
	private String pathResum;
	private String fileName;
	
	@FXML
	private Label titleLabel;
	
	@FXML
	private ProgressBar TraitementBar;
	
	/**
	 * This function handles the action on the play button. It therefore either plays the video if on pause or switches
	 * to the image mode if on play.
	 */
	
	public void playMedia() {
		Status status = mediaPlayer.getStatus();
		 
        if (status == Status.UNKNOWN  || status == Status.HALTED)
        {
           // don't do anything in these states
           return;
        }
 
          if ( status == Status.PAUSED
             || status == Status.READY
             || status == Status.STOPPED)
          {
             // rewind the movie if we're sitting at the end
             if (mediaPlayer.getMedia().getDuration().compareTo(mediaPlayer.getCurrentTime()) <= 0 ) {
                mediaPlayer.seek(mediaPlayer.getStartTime());
             }
             mediaPlayer.play();
             } else {
               pauseMedia();
             }
         }
	
	/**
	 * This method verifies whether or not all the events are confirmed by the referee or not.
	 * If so, it recreates the synthesis video and file by launching a thread.
	 */
	
	public void eventsConfirmed() {
		allTrue.clear();
		for (int i=0; i<Events.size(); i++) {
			allTrue.add(true);
		}
		if (allConfirmed.equals(allTrue)) {
			newThreadResum();
		}
		confirmEvent.setVisible(false);
	}
		
	/**
	 * This function is called when the open video button is clicked and open the system explorer to select a video file, then
	 * check if the video has already been processed and act accordingly by asking to draw line or displaying the processed video
	 */
	public void openSystemExplorer() throws IOException {
		/**
		 * When the "Ouvrir une video" button is clicked, open the system explorer in order to select a mp4 video
		 * This video is then displayed in the application
		 */
		if(myThread!=null){
			myThread.interrupt();
			ThreadScore.interrupt();
			TraitementBar.setVisible(false);
		}
		
		FileChooser filechooser= new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("mp4 files","*.mp4");
		filechooser.getExtensionFilters().add(filter);
		File file = filechooser.showOpenDialog(null);
	        Duration start = new Duration(0);
       
		if (file != null) {
			chosenpath = file.getPath();
			Path p = Paths.get(chosenpath);
			String nameWithExt = p.getFileName().toString();
			fileName = nameWithExt.substring(0, nameWithExt.length() - 4);
			titleLabel.setText(fileName);
			path = chosenpath;
			pathMarked = path.substring(0,path.length() -4);
			pathMarked = pathMarked + "Marked" + ".mp4";
			pathResum = path.substring(0, path.length() -4);
			pathResum = pathResum +"Resume" + ".mp4";
			buttontraiter.setVisible(true);
			buttonredraw.setVisible(true);
			
			File file2 = new File(pathMarked);
			if(file2.exists()) {
				flagVideoTreated = true;
				Events = ReadFile.getExistingEventFile(p);
				loadVideo(file2, start);
				chosenpath = pathMarked;
				buttontraiter.setVisible(false);
				
				
			}
			else {
				loadVideo(file, start);
				flagVideoTreated = false;
				popup();
			}
		}
	}
	
	/**
	 * This function is called when the pause button is press and switch to frame by frame mode 
	 */
	public void pauseMedia() {
		mediaPlayer.pause();
		if (flagVideoTreated) {
			switchToFrameByFrameScene();
		}
		
	}

	/**
	 * This function rewind the video at the beginning of the video
	 */
	public void restartMedia() {
		if(mediaPlayer.getStatus() != MediaPlayer.Status.STOPPED) {
			mediaPlayer.seek(Duration.seconds(0.0));
		};
		
		mediaPlayer.pause();
	}
	
	/**
	 * This function is used to go to then end of the video 
	 */
	public void endMedia() {
		if(mediaPlayer.getStatus() != MediaPlayer.Status.STOPPED) {
			mediaPlayer.seek(mediaPlayer.getMedia().getDuration());
		};
		
	}
	
	public void receiveDataFromScene(String pathE, String pathMarkedE, String pathResumE, String chosenpathE, double time, ArrayList<int[]> Event) throws IOException {
		if (chosenpathE != null) {
			path = pathE;
			chosenpath = chosenpathE;
			pathMarked = pathMarkedE;
			pathResum = pathResumE;
			flagVideoTreated = true;
			Duration seekTime = new Duration(time * 1000);
			loadVideo(new File(chosenpath), seekTime);
			Events = Event;
			Path p = Paths.get(chosenpath);
			String nameWithExt = p.getFileName().toString();
			fileName = nameWithExt.substring(0, nameWithExt.length() - 4);
			if (fileName.contains("Marked")) {
				fileName = fileName.substring(0, fileName.length()-6);
			}
			titleLabel.setText(fileName);
		}
		else {
			System.out.println("le path transmit est nul !");
		}
		
		buttonredraw.setVisible(true);
		newThreadScore();
	}
	
	/**
	 * This function handles the switching of the interface from the video mode to the image by image mode. It is basically
	 * called when the video is paused or when the button (Passer en mode image par image) is pressed.
	 */
	public void switchToFrameByFrameScene() {
		

		FXMLLoader loader = null;
		Parent root = null;
	
		try {
			loader = new FXMLLoader(getClass().getResource("SceneFrame.fxml"));
			root = loader.load();
			ControllerFrame controller = loader.getController();
			controller.receiveDataFromScene(path, pathMarked, pathResum, chosenpath, mediaPlayer.getCurrentTime(),Events);
			Stage stage = (Stage) mediaView.getScene().getWindow();
			stage.setScene(new Scene(root));
			if(ThreadScore != null){
				ThreadScore.interrupt();
			}
		 
		} catch (IOException e) {
			System.err.println(String.format("Error: %s", e.getMessage()));
		}
	}
	
	@FXML
	void redrawlines() throws IOException {
		popup();
		buttontraiter.setVisible(true);
	}
	
	/**
	 * This function is called when the "traiter video" button is pressed and start the processing of the video if 
	 * it is not already in progress
	 */
	@FXML
	void traitervid() throws IOException {
		if(myThread!=null) {
			if(!myThread.isAlive()) {
				newThreadTraitement();
			}
		}
		else {
			newThreadTraitement();
		}
		
	}
	
	/**
	 * fillEventList() update the list of events to include all the important events of the match with their time. For this matter, it 
	 * uses the ArrayList events.
	 */
	

	private void fillEventList() {
		
		clearEvents();
		allConfirmed.clear();
		int count = 0;
		for (int[] event : Events) {
			count++;
			allConfirmed.add(false);
			MenuItem eventItem = new MenuItem();
			Duration seekTime = new Duration(event[0] * 16.6);
			switch (event[1]) {
			case 1:
				eventItem.setText("But de l'equipe droite : " + formatTime(seekTime, mediaPlayer.getMedia().getDuration()));
				break;
			
			case 2:
				eventItem.setText("But de l'equipe gauche : " + formatTime(seekTime, mediaPlayer.getMedia().getDuration()));
				break;
			
			case 3:
				eventItem.setText("Sortie de balle : " + formatTime(seekTime, mediaPlayer.getMedia().getDuration()));
				break;
				
			case 4:
				eventItem.setText("Gamelle de l'equipe droite : " + formatTime(seekTime, mediaPlayer.getMedia().getDuration()));
				break;
				
			case 5:
				eventItem.setText("Gamelle de l'equipe gauche : " + formatTime(seekTime, mediaPlayer.getMedia().getDuration()));
				break;

			default:
				eventItem.setText("Cas douteux : " + formatTime(seekTime, mediaPlayer.getMedia().getDuration()));
				break;
			}

			EventHandler<ActionEvent> hnd = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) { // Duration conversion for 60 FPS video
					mediaPlayer.seek(seekTime);
					mediaPlayer.pause();
					if (!allConfirmed.equals(allTrue) && !allConfirmed.get(Events.indexOf(event))) {
						confirmEvent.setVisible(true);
						initialize(eventItem, event);
					}
				};
			};
			eventItem.setOnAction(hnd);
			fillEvents(eventItem);
		}
		if (count > 0) {
			eventList.setDisable(false);
		}
		else {
			eventList.setDisable(true);
		}
	}
	
	/**
	 * This function is called in the previous function (fillEventList) to initialize the confirm button : it clears the menu
	 * button that allows the referee to either confirm or delete the current event and adds the "confirm" and "delete" items 
	 * to it. The confirm item confirms the event and therefore launch the eventsConfirmed function to check if all events 
	 * have been confirmed. The delete item deletes the event from the eventList and Events, it calls the eventsConfirmed function
	 * too for the same cited reason.
	 * @param eventItem the current item from the eventList
	 * @param event the current event
	 */
	public void initialize(MenuItem eventItem, int[] event) {
		confirmEvent.getItems().clear();
		
		MenuItem confirm = new MenuItem();
		MenuItem delete = new MenuItem();
		
	    confirm.setText("Confirmer cet evenement");
	    delete.setText("Supprimer cet evenement");
	    
	    confirmEvent.getItems().add(confirm);
	    confirmEvent.getItems().add(delete);
	    
	    EventHandler<ActionEvent> confHandler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				System.out.println("evenement confirme !");
				allConfirmed.set(Events.indexOf(event), true);
				eventsConfirmed();
			};
		};
		EventHandler<ActionEvent> delHandler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				allConfirmed.remove(Events.indexOf(event));
				eventList.getItems().remove(eventItem);
				Events.remove(event);
				eventsConfirmed();
			};
		};
		
		confirm.setOnAction(confHandler);
		delete.setOnAction(delHandler);
	}

	/**
	 * This function create and start the new thread which handle the processing of a source video
	 */
	private void newThreadTraitement() {
		TraitementBar.setVisible(true);
		setProgress(0);
		myThread = new Thread() {
			public void run() {
				Circles = TraitementCV.traiterVid(path, lineB, lineT,contvid,this);
				Events = DetectionEvents.detecter(Circles, lineB, lineT);
				File file = new File(pathMarked);
				Duration time = new Duration(0);
				loadVideo(file, time);
				flagVideoTreated = true;
				setListener();
				chosenpath = pathMarked;
				newThreadScore();
				fillEventList();
				TraitementBar.setVisible(false);
			}
		};
		myThread.start();
	}

	
	/**
	 * This function handles the opening of the popup window that is used to draw the necessary lines for the processing.
	 */
	private void popup() throws IOException {	
		FXMLLoader loader2 = new FXMLLoader(getClass().getResource("LinePlotter2Scene.fxml"));
		Parent root2 = loader2.load();
		Stage stage2 = new Stage();
        Scene scene2 = new Scene(root2);
        stage2.setScene(scene2);
        stage2.show();
		LinePlotter2Scene controller = loader2.getController();			
		controller.initialization(path,this);
	}
	
	public void setListener() {
		videoNavigation.valueProperty().removeListener(sliderChangeListener);
		
		videoNavigation.valueProperty().addListener(sliderChangeListener);
		
		mediaPlayer.currentTimeProperty().addListener(videoChangeListener);
	}
	
	protected void updateValuesSlider() {
		Platform.runLater(new Runnable() {
			public void run() {
				Duration currentTime = mediaPlayer.getCurrentTime();
				playTime.setText(formatTime(currentTime, mediaPlayer.getMedia().getDuration()));
				videoNavigation.setDisable(mediaPlayer.getMedia().getDuration().isUnknown());
	          
				if (!videoNavigation.isValueChanging()) {
					videoNavigation.setValue(currentTime.divide(mediaPlayer.getMedia().getDuration().toMillis()).toMillis()
							* 100.0);
				}
			}
		});	          
	}
	
	protected void updateValuesVideo() {
		Platform.runLater(new Runnable() {
			public void run() {
				Duration seekTo = mediaPlayer.getMedia().getDuration().multiply(videoNavigation.getValue() / 100.0);
			    
			    if (videoNavigation.isValueChanging()) {
			    	mediaPlayer.seek(seekTo);
			    }
			}
		});
	}
	
	private static String formatTime(Duration elapsed, Duration duration) {
		   int intElapsed = (int)Math.floor(elapsed.toSeconds());
		   int elapsedHours = intElapsed / (60 * 60);
		   if (elapsedHours > 0) {
		       intElapsed -= elapsedHours * 60 * 60;
		   }
		   int elapsedMinutes = intElapsed / 60;
		   int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 
		                           - elapsedMinutes * 60;
		 
		   if (duration.greaterThan(Duration.ZERO)) {
		      int intDuration = (int)Math.floor(duration.toSeconds());
		      int durationHours = intDuration / (60 * 60);
		      if (durationHours > 0) {
		         intDuration -= durationHours * 60 * 60;
		      }
		      int durationMinutes = intDuration / 60;
		      int durationSeconds = intDuration - durationHours * 60 * 60 - 
		          durationMinutes * 60;
		      if (durationHours > 0) {
		         return String.format("%d:%02d:%02d/%d:%02d:%02d", 
		            elapsedHours, elapsedMinutes, elapsedSeconds,
		            durationHours, durationMinutes, durationSeconds);
		      } else {
		          return String.format("%02d:%02d/%02d:%02d",
		            elapsedMinutes, elapsedSeconds,durationMinutes, 
		                durationSeconds);
		      }
		      } else {
		          if (elapsedHours > 0) {
		             return String.format("%d:%02d:%02d", elapsedHours, 
		                    elapsedMinutes, elapsedSeconds);
		            } else {
		                return String.format("%02d:%02d",elapsedMinutes, 
		                    elapsedSeconds);
		            }
		        }
		    }
	
	/** 
	 * This function allows the mediaView to play the corresponding video
	 * @param f the file containing the video
	 */
	public void loadVideo(File f, Duration time) {
		media = new Media(f.toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		mediaView.setMediaPlayer(mediaPlayer);
		
		mediaPlayer.setOnReady(new Runnable() {
			public void run() {
				mediaPlayer.seek(time);
				videoNavigation.setValue(mediaPlayer.getCurrentTime().divide(mediaPlayer.getMedia().getDuration().toMillis()).toMillis()
						* 100.0);
				mediaPlayer.play();
				setListener();
				fillEventList();
			}
		});
		newThreadScore();
		
		DoubleProperty widthProp = mediaView.fitWidthProperty();
		DoubleProperty heightProp = mediaView.fitHeightProperty();
    
		widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
		heightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
		



	}
	    
	/**
	 * This function handles the change of displayed video to the marked video when asked by the user
	 * @param event An FXML Action event
	 */
	public void videoWithMarkers(ActionEvent event) {
		File file = new File(pathMarked);
		Duration time = mediaPlayer.getCurrentTime();
		if(file.exists()) {
			loadVideo(file, time);
			chosenpath = pathMarked;
			newThreadScore();
		}
	}
	
	/**
	 * This function handles the change of displayed video to the original video when asked by the user
	 * @param event An FXML Action event
	 */
	public void OriginalVideo(ActionEvent event) {
		File file = new File(path);
		Duration time = mediaPlayer.getCurrentTime();
		loadVideo(file, time);
		chosenpath = path;
		newThreadScore();
	}
	
	/**
	 * This function handles the change of displayed video to the resume video when asked by the user
	 * @param event An FXML Action event
	 */
	public void videoResume(ActionEvent event){
		File file = new File(pathResum);
		Duration time = mediaPlayer.getCurrentTime();
		if(file.exists()) {
			loadVideo(file, time);
			setListener();
			chosenpath = pathResum;
			newThreadScore();
		}
	}
	
	/**
	 * This function is used to externally update the array containing the coordinates of back of the goals.
	 * @param Buts An array containing the updated values.
	 */
	public void setlineB(int[] Buts) {
		lineB[0]=Buts[0];
		lineB[1]=Buts[1];
		lineB[2]=Buts[2];
		lineB[3]=Buts[3];
		lineB[4]=Buts[4];
		lineB[5]=Buts[5];
		lineB[6]=Buts[6];
		lineB[7]=Buts[7];
	}
	
	/**
	 * This function is used to externally update the array containing the coordinates of the field's corners.
	 * @param Terrain An array containing the updated values.
	 */
	public void setlineT(int[] Terrain) {
		lineT[0]=Terrain[0];
		lineT[1]=Terrain[1];
		lineT[2]=Terrain[2];
		lineT[3]=Terrain[3];
		lineT[4]=Terrain[4];
		lineT[5]=Terrain[5];
		lineT[6]=Terrain[6];
		lineT[7]=Terrain[7];
	}
	
	/**
	 * This function is used to externally update the value displayed in the progress bar.
	 * @param P A double containing the updated value.
	 */
	public void setProgress(double P) {
		TraitementBar.setProgress(P);
	}
	
	/**
	 * This function is used to set the actual score of the match in the mediaPlayer
	 * @param s A String containing the score
	 */
	public void afficherScore(String s) {
		Platform.runLater(new Runnable() {
		    @Override public void run() {
		    	AffScore.setText(s);
		    }
		});
	}
	
	public void clearEvents() {
		Platform.runLater(new Runnable() {
		    @Override public void run() {
		    	eventList.getItems().clear();
		    }
		});
	}
	
	public void fillEvents(MenuItem eventItem) {
		Platform.runLater(new Runnable() {
		    @Override public void run() {
		    	eventList.getItems().add(eventItem);
		    }
		});
	}
	
	/**
	 * This function create and start the new thread which handles the actualization of the score regarding the events 
	 * that happened at this time of the video
	 */
	private void newThreadScore() {
		long fpsInterval = 1000/15;
		if(ThreadScore != null){
			ThreadScore.interrupt();
		}
		ThreadScore = new Thread() {
			public void run() {
				long then,now,untilnext;
				while(!this.isInterrupted()) {
					then = System.currentTimeMillis();
					try {
						Score.scoreLive(Events, mediaPlayer.getCurrentTime().toSeconds(), contvid);
					} catch (java.lang.NullPointerException e) {
						System.out.println("interrupting thread at application closure");
						this.interrupt();
					}
					now = System.currentTimeMillis();
					untilnext = fpsInterval-(now-then);
					try {
						sleep(untilnext);
					} catch (InterruptedException e) {
						System.out.println("thread interrupted while spleeping");
						this.interrupt();
					}
				}
			}
		};
		ThreadScore.start();
	}
	
	/**
	 * This function create and start the new thread which handles the deletion and recreation of the synthesis video and 
	 * file whenever the referee re-confirms the events.
	 */
	
	private void newThreadResum() {
		if(ThreadResum != null){
			ThreadResum.interrupt();
		}
		ThreadResum = new Thread() {
			public void run() {
				ResumeVideo.deleteRes(path);
				if(!Events.isEmpty()) {
					pathResum = ResumeVideo.resum(path,Events);
				}
				else {
					System.out.println("Aucun evenement, pas de resume");
				}
				try {
					FichierSynthese.deleteFile(path);
					FichierSynthese.createFile(Events, path);
				} catch (IOException e) {
					System.out.println("Coudn't create synthesis file");
				}
			}
		};
		ThreadResum.start();
	}
}
