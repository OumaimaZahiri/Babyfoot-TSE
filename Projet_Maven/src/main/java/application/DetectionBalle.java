package application;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * @author Clement Bourlet
 * This class provides the necessary functions to detect a ball in a given frame and draw a circle where the ball is detected.
 */
public class DetectionBalle {
	
	/**
	 * This function detect the objects within the right range of intensity in all three planes of the HSV spaces.
	 * @param frame The frame in which the ball is to be detected
	 * @param res The result frame in which objects have been detected, ready from the next step
	 * @return The res input Mat is returned once modified
	 */
	public static Mat detectionParPlan(Mat frame,Mat res) {
		ArrayList<Mat> Plans = new ArrayList<>(3);
		Core.split(frame, Plans);
		Mat H = Plans.get(0);
		Mat S = Plans.get(1);
		Mat V = Plans.get(2);
		
		Imgproc.threshold(H, H, 30, 255, Imgproc.THRESH_TOZERO_INV);
		Imgproc.threshold(H, H, 0, 255, Imgproc.THRESH_BINARY);
		
		Imgproc.threshold(S, S, 140, 255, Imgproc.THRESH_TOZERO);
		Imgproc.threshold(S, S, 0, 255, Imgproc.THRESH_BINARY);
		
		Imgproc.threshold(V, V, 90, 255, Imgproc.THRESH_TOZERO);
		Imgproc.threshold(V, V, 180, 255, Imgproc.THRESH_TOZERO_INV);
		Imgproc.threshold(V, V, 0, 255, Imgproc.THRESH_BINARY);
		
		Core.merge(Plans,res);
		
		Imgproc.cvtColor(res, res, Imgproc.COLOR_RGB2GRAY);
		Imgproc.threshold(res, res, 200, 255, Imgproc.THRESH_BINARY);
		
		Mat kernel = Mat.zeros(15,15,0);
		Point center = new Point(8,8);
		Scalar color = new Scalar(255);
		Imgproc.circle(kernel, center, 2, color , -1);
		
		Imgproc.morphologyEx(res, res, Imgproc.MORPH_OPEN, kernel);
		
		H.release();
		S.release();
		V.release();
		kernel.release();
		return res;
	}
	
	/**
	 * This function is used to find the coordinates and size of the last object detected inside the playing field
	 * @param Marker  The result matrix of the detectionParPlan function in which valid objects have been detected
	 * @param NBorders  An array containing the coordinates of the extended borders of the playing field in which 
	 * we want to detect the ball
	 * @param circle  An array in which the coordinates of the found object are to be stored
	 * @return  The circle array is returned once modified
	 */
	public static int[] findCircle(Mat Marker,int[] circle,int[] NBorders){
		Mat labels = new Mat();
		Mat Stats = new Mat();
		Mat Centroids = new Mat();
		Boolean In = true;
		int i = 1;
		
		Imgproc.connectedComponentsWithStats(Marker, labels, Stats, Centroids);
		
		double[] dataCx = null;
		double[] dataCy = null;
		double[] dataSh = null;
		double[] dataSv = null;
		
		while(In) {
			dataCx=Centroids.get(i,0);
			dataCy=Centroids.get(i,1);
			dataSh=Stats.get(i,2);
			dataSv=Stats.get(i,3);
			if(dataCx!=null) {
				In = !(dataCx[0]>NBorders[0] && dataCx[0]<NBorders[1] && dataCy[0]>NBorders[2] && dataCy[0]<NBorders[3]);
			}
			else {
				In = false;
			}
			i++;
		}
		
		
		if(dataCx!=null) {
			circle[0]= (int) Math.round(dataCx[0]);
			circle[1]= (int) Math.round(dataCy[0]);
			circle[2]= (int) Math.round(Math.max(dataSh[0], dataSv[0]));
		}
		else {
			circle[0]= 0;
			circle[1]= 0;
			circle[2]= 0;
		}
		
		labels.release();
		Stats.release();
		Centroids.release();
		return circle;
	}
	
	/**
	 * This function draws a circle on a frame given the detected coordinates of the ball.
	 * @param original  The extracted raw frame on which we want to draw the circle
	 * @param circle  The coordinates and size of the detected circular object
	 * @return  The original Mat is returned once drawn on
	 */
	public static Mat drawCircle(Mat original,int[] circle){

		Scalar color = new Scalar(255,0,0);
		Point center = new Point(circle[0],circle[1]);
		Imgproc.circle(original, center, 1, color , 2);
		Imgproc.circle(original, center, circle[2], color , 2);	

		
		return original;
	}
}
