package application;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * @author clemb
 * See the documentation of the drawLine function for further details
 */
public class LinePlotter2 {
	private static Scalar ColorRed = new Scalar(0,0,255);
	private static Scalar ColorGreen = new Scalar(0,255,0);
	/**
	 * This function draw the lines inputed by the user on the displayed image once
	 * @param original  The Mat on which the lines are to be drawn
	 * @param lignes  An array containing the coordinates of the lines entered by the user
	 * @param Buts  A Boolean used to differentiate the goals lines from the playing field lines
	 * @return  The modified original Mat is returned
	 */
	public static Mat drawLine(Mat original, int[] lignes, Boolean Buts){
		for(int i = 0; i<2;i++) {
			Point a = new Point();
			Point b = new Point();
			a.x = lignes[0+4*i];
			a.y = lignes[1+4*i];
			b.x = lignes[2+4*i];
			b.y = lignes[3+4*i];
			if(Buts) {
				Imgproc.line(original, a, b, ColorGreen, 2);
			}
			else {
				Imgproc.line(original, a, b, ColorRed, 2);
			}
		}
		return original;		
	}
}