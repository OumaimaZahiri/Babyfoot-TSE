package application;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;


/**
 * @author Clement Bourlet
 * See the traiterVid function for further details
 */
public class TraitementCV {
	
	/**
	 * This function detect the object if found in every frame of the input video, and create a new video on which the object 
	 * and the borders playing field are displayed.
	 * @param PathE  The path String to the Video that is to be analyzed
	 * @param Buts  The coordinates of the lines at the back of the goals
	 * @param Terrain  The coordinates of the corners of the playing field
	 * @param contvid  A pointer to the video controller object handling the display, used to update the progress bar
	 * @param thread  A pointer to the thread in which the function is running, used to handle interruptions
	 * @return  An arrayList of the detected position and size of the object detected in each frame
	 */
	public static ArrayList<int[]> traiterVid(String PathE,int[] Buts,int[] Terrain, ControllerVideo contvid, Thread thread) {
		String PathS = PathE.substring(0, PathE.length() -4);
		PathS = PathS+"Marked"+".mp4";
		VideoCapture capture=new VideoCapture();
		capture.open(PathE);
		double fps = capture.get(Videoio.CAP_PROP_FPS);
		double fourcc = capture.get(Videoio.CAP_PROP_FOURCC);
		double nbframes = capture.get(Videoio.CAP_PROP_FRAME_COUNT);
		Size size = new Size();
		size.height = capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
		size.width = capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
		
		Boolean cont =true;
		Mat frame = new Mat();
		Mat frameB = new Mat();
		int[] circleLastSeen = new int[3];
		circleLastSeen[0] = 0;
		circleLastSeen[1] = 0;
		circleLastSeen[2] = 0;
		ArrayList<int[]> Circles = new ArrayList<int[]>();
		
		int[]NBorders= {(Buts[0]+Buts[2])/2-30,(Buts[4]+Buts[6])/2+30,(Terrain[1]+Terrain[5])/2-30,(Terrain[3]+Terrain[7])/2+30};
		
		VideoWriter Writer = new VideoWriter();
		Writer.open(PathS, (int) fourcc, fps, size);
		
		double k = 0;
		
		while(cont && !thread.isInterrupted()) {
			cont = capture.read(frame);
			if(cont) {
				int[] circle = new int[3];
				Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2HSV);
				frameB = DetectionBalle.detectionParPlan(frame,frameB);
				circle = DetectionBalle.findCircle(frameB,circle,NBorders);
				Imgproc.cvtColor(frame, frame, Imgproc.COLOR_HSV2BGR);
				if(circle[0]!=0 && circle[1]!=0 && circle[2]!=0) {
					frame = DetectionBalle.drawCircle(frame, circle);
					Circles.add(circle);
					circleLastSeen[0] = circle[0];
					circleLastSeen[1] = circle[1];
					circleLastSeen[2] = circle[2];
				}
				else {
					int[] circleLS = new int[3];
					circleLS[0] = circleLastSeen[0];
					circleLS[1] = circleLastSeen[1];
					circleLS[2] = circleLastSeen[2];
					frame = DetectionBalle.drawCircle(frame, circleLS);
					Circles.add(circleLS);
				}
				frame = LinePlotter2.drawLine(frame, Buts, true);
				frame = LinePlotter2.drawLine(frame, Terrain, false);
				Writer.write(frame);
			}
			k=k+1;
			contvid.setProgress(k/nbframes);
		}
		capture.release();
		Writer.release();
		
		return Circles;
	}
}
