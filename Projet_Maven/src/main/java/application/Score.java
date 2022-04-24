package application;

import java.util.ArrayList;
/**
 * @author Ilyas
 * See the documentation of the score and scoreLive function for further details
 */
public class Score {
	
	/**
	 * This function analyze the events of the video in order to adjust the score of both team according to the rules of table soccer.
	 * @param events  An arrayList of the principals events in the video (goals and messkit), obtained via the detecter function form the DetectionEvents class
	 * @return score A String that give the final score of the match
	 */
	
	public static String score(ArrayList<int[]> events) {

		
		String score ="0 : 0";
		String score2 = "0 : 0";
		String newscore;

		for (int i=0; i<events.size();i++) {
			char up;
			int s;
			int f = events.get(i)[1];
			switch(f) {
			
			case 1 :
				// Goal from the right team
				up=score.charAt(4);
				s=up-'0'; // Convert up into int
				s=s+1; // Increment the score
				newscore=Integer.toString(s);
				score2=score.substring(0, 4).concat(newscore);
				score=score2;
				score2="";
				break;
			case 2:
				// Goal from the left team
				up=score.charAt(0);
				s=up-'0'; // Convert up into int
				s=s+1; // Increment the score
				newscore=Integer.toString(s);
				score2=newscore.concat(score.substring(1, 5));
				score=score2;
				score2="";
				break;
			case 5:
				// messkit from left team
				up=score.charAt(0);
				s=up-'0';
				if (s>0) {
					s=s-1;
				}
				newscore=Integer.toString(s);
				score2=newscore.concat(score.substring(1, 5));
				score=score2;
				score2="";
				break;
			case 4:
				// messkit from right team
				up=score.charAt(4);
				s=up-'0';
				if (s>0) {
					s=s-1;
				}
				newscore=Integer.toString(s);
				score2=score.substring(0, 4).concat(newscore);
				score=score2;
				score2="";
				break;
			
			case 6:
				// Goal from right team (uncertain)
				up=score.charAt(4); 
				s=up-'0'; // Convert up into int
				s=s+1; // Increment the score
				newscore=Integer.toString(s);
				score2=score.substring(0, 4).concat(newscore);
				score=score2;
				score2="";
				break;
			case 7:
				// Goal from left team (unceretain)
				up=score.charAt(0);
				s=up-'0'; // Convert up into int
				s=s+1; // Increment the score
				newscore=Integer.toString(s);
				score2=newscore.concat(score.substring(1, 5));
				score=score2;
				score2="";
				break;
			
			}
		}

		return score;
		
	}
	
	
	/**
	 * This function analyze the events of the video in "live" in order to adjust the score.
	 * @param events  An arrayList of the principals events in the video (goals and messkit), obtained via the detecter function form the DetectionEvents class
	 * @param tps A double referring to the actual timing of the video
	 * @param contvid  A pointer to the video controller object handling the display, used to update the progress bar
	 */
	public static void scoreLive(ArrayList<int[]> events , double tps, ControllerVideo contvid) {

		
		String score = "0 : 0";
		ArrayList<int[]> eventsLive = new ArrayList<int[]>();
		tps = tps * 60;
		
		for(int i = 0; i < events.size();i++) {
			if(events.get(i)[0] < (int)tps) {
				eventsLive.add(events.get(i));
			}
		}
		
		score = score(eventsLive);
		contvid.afficherScore(score);
	}
	
}



