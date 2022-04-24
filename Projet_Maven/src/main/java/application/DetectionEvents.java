package application;

import java.util.ArrayList;

/**
 * @author clemb
 * See the documentation of the detecter function for further details
 */
public class DetectionEvents {
	
	/**
	 * This function analyze the position of the ball in each frame to detect the events that happen during the video
	 * @param Circles  An arrayList on the position and size of the detected object in each frame, obtained via the traiterVid
	 * function of the TraitementCV class
	 * @param Buts  An array containing the positions of the lines at the back of the goals
	 * @param Terrain  An array containing the coordinates of the corners of the playing field
	 * @return	Events is an arrayList containing arrays that describe each event in the [frame number,event type] shape
	 */
	public static ArrayList<int[]> detecter(ArrayList<int[]> Circles, int[] Buts, int[] Terrain){
		Boolean CondHT = true;
		Boolean CondBut = false;
		Boolean CondD = false;
		
		int compteurLost = 0;
		int compteurInTer = 0;
		
		ArrayList<int[]> Events = new ArrayList<>();
		int[] circleI = new int[3];
		int[] circleI_1 = new int[3];
		int[] NTerrain = {(Terrain[0]+Terrain[2])/2,(Terrain[4]+Terrain[6])/2,(Terrain[1]+Terrain[5])/2,(Terrain[3]+Terrain[7])/2};
		int[] NButs = {(Buts[0]+Buts[2])/2,Buts[1],Buts[3],(Buts[4]+Buts[6])/2,Buts[5],Buts[7]};
		circleI_1[0]=0;
		circleI_1[1]=0;
		circleI_1[2]=0;
		
		
		for(int i = 0; i < Circles.size(); i++) {
			circleI = Circles.get(i);
			if(!CondHT && !CondBut && !CondD) {
				if (circleI[0]<NTerrain[0] || circleI[0]>NTerrain[1] || circleI[1]<NTerrain[2] || circleI[1]>NTerrain[3]) {
					if (circleI[0]>NButs[0] && circleI[1]>NButs[1] && circleI[1]<NButs[2] && circleI[0]<NTerrain[0]) {
						CondBut = true;
						int[] event = new int[2];
						event[0]=i;
						event[1]=1;
						Events.add(event);
					}
					else if (circleI[0]<NButs[3] && circleI[1]>NButs[4] && circleI[1]<NButs[5] && circleI[0]>NTerrain[1]) {
						CondBut = true;
						int[] event = new int[2];
						event[0]=i;
						event[1]=2;
						Events.add(event);
					}
					else {
						CondHT = true;
						int[] event = new int[2];
						event[0]=i;
						event[1]=3;
						Events.add(event);
					}
				}
				if(circleI[0] == circleI_1[0] && circleI[1] == circleI_1[1] && circleI[2] == circleI_1[2]) {
					compteurLost++;
					if(compteurLost > 30) {
						if(circleI[0]<NTerrain[0]+50 && circleI[1]>NButs[1]-20 && circleI[1]<NButs[2]+20) {
							int[] event = new int[2];
							event[0]=i-30;
							event[1]=6;
							Events.add(event);
							CondD = true;
							CondHT = true;
						}
						else if(circleI[0]>NTerrain[1]-50 && circleI[1]>NButs[4]-20 && circleI[1]<NButs[5]+20) {
							int[] event = new int[2];
							event[0]=i-30;
							event[1]=7;
							Events.add(event);
							CondD = true;
							CondHT = true;
						}
					}
				}
				else {
					compteurLost = 0;
				}
			}
			else {
				if (circleI[0]>NTerrain[0] && circleI[0]<NTerrain[1] && circleI[1]>NTerrain[2] && circleI[1]<NTerrain[3]) {
					compteurInTer++;
					if(compteurInTer>5) {
						if(!Events.isEmpty()) {
							int[] event = new int[2];
							event = Events.get(Events.size()-1);
							if(CondBut && (i-5)-event[0]<25) {
								if(event[1]==1){
									event[1]=4;
								}
								else {
									event[1]=5;
								}
								Events.set(Events.size()-1, event);
							}
						
						}
						CondBut = false;
						CondHT = false;
						compteurInTer = 0;
					}
				}
				else {
					compteurInTer = 0;
				}
			}
			CondD = (circleI[0] == circleI_1[0] && circleI[1] == circleI_1[1] && circleI[2] == circleI_1[2]) && CondD;
			circleI_1[0]=circleI[0];
			circleI_1[1]=circleI[1];
			circleI_1[2]=circleI[2];
		}
		return Events;
	}
}
