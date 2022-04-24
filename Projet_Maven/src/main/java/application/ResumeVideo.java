package application;

import java.io.File;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
/**
 * @author Ilyas
 * See the documentation of the resum function for further details
 */

public class ResumeVideo {
	
	/**
	 * This function create a video summary of the original video in which only goals and messkit are displayed
	 * @param PathE A String for the local access path of the original video
	 * @param events An arrayList of the principals events in the video (goals and messkit), obtained via the detecter function form the DetectionEvents class
	 * @return PathR A String for the new video, the video that resume principals action in the original video
	 */
	@SuppressWarnings("static-access")
	public static String resum(String PathE, ArrayList<int[]> events) {

		
		String PathR = PathE.substring(0, PathE.length() -4);
		PathR = PathR +"Resume" + ".mp4";
		
		VideoCapture capture = new VideoCapture();
		capture.open(PathE);
		double fps = capture.get(Videoio.CAP_PROP_FPS);
		double fourcc = capture.get(Videoio.CAP_PROP_FOURCC);
		Size size = new Size();
		size.height = capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
		size.width = capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
		
		VideoWriter Writer = new VideoWriter();
		Writer.open(PathR, (int) fourcc,fps,size);

		for (int i=0; i<events.size();i++) {

			int f = events.get(i)[1];

			// The summarized action begins 120 frames before it's closure
			// This summarized action is composed of 240 frames
			capture.set(1,events.get(i)[0]-120);
			Mat im = new Mat();
			capture.read(im);
			// Black image uses for transition screen between actions
			Mat imt = Mat.zeros(size, im.type()); 
			switch(f) {
			
			case 1: case 2: case 3: case 4: case 5: case 6 :
				for (int j=0; j<60*4 ; j++) {
					capture.read(im);
					Writer.write(im);
				};
				// Add transition screen
				for (int k=0; k<6 ; k++) {
					Writer.write(imt);
				}
				break;
	
			}
		}
		
		capture.release();
		Writer.release();
		return PathR;
		
	}
	
	public static void deleteRes(String PathE) {
		String PathR = PathE.substring(0, PathE.length() -4);
		PathR = PathR +"Resume" + ".mp4";
		
		File myFile = new File(PathR);
		if (myFile.exists()) {
			myFile.delete();
		}
	}
}
