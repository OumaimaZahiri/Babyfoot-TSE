package application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nu.pattern.OpenCV;

/**
 * @author Enguerran De Larocque Latour Ilyas Tibari Oumaima Zahiri Clement Bourlet Maxime Boichon
 * This class is the controller of the main class of the application.
 */

public class Main extends Application {
	static{ OpenCV.loadShared(); }
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("SceneVideo.fxml"));
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		
		stage.setScene(scene);
		stage.show();
		
		scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
		        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
		            if (mouseEvent.getClickCount() == 2) {
		            	
		            	primaryStage.setFullScreen(true);
		            	
		            }
		       }
		    }
		});
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
}
