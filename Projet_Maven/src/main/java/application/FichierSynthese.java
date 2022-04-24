package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.opencv.videoio.Videoio;
import org.opencv.videoio.VideoCapture;
/**
 * @author Maxime
 * See the documentation of the createFile function for further details
 */
public class FichierSynthese {

	/**
	 * This function generates a summary file of the match containing the score and the important events 
	 * (goals, draws, outings) with their time.
	 * @param events is an arrayList containing arrays that describe each event in the [frame number,event type] shape
	 * @param pathIN The path String to the Video that is to be analyzed
	 * @throws IOException
	 */
	public static void createFile(ArrayList<int[]> events, String pathIN) throws IOException {

		/*
		 * Récupération des noms de chemins et de vidéos
		 */
		Path p = Paths.get(pathIN);
		String nameWithExt = p.getFileName().toString();
		String fileName = nameWithExt.substring(0, nameWithExt.length() - 4);
		String parentPath = p.getParent().toString();

		/*
		 * Création du dossier contenant les fichiers de synthèses (si il n'existe pas
		 * déjà *
		 */
		String pathDossier = parentPath + "/FichiersSynthese";
		File dossier = new File(pathDossier);
		if (!dossier.exists()) {
			dossier.mkdir();
		}

		Charset utf8 = StandardCharsets.UTF_8;
		VideoCapture videoCapture = new VideoCapture();
		videoCapture.open(pathIN);
		double fps = videoCapture.get(Videoio.CAP_PROP_FPS);
		
		if (fileName.contains("Marked")) {
			fileName = fileName.substring(0, fileName.length()-6);
		}
		String path = pathDossier + "/Events_" + fileName + ".txt";
		File myFile = new File(path);

		if (!myFile.exists()) {

			// Create Writer to write a file.
			Writer writer = new FileWriter(myFile, utf8);

			// Create a BufferedWriter with buffer array size of 16384 (32786 bytes = 32
			// KB).
			BufferedWriter bw = new BufferedWriter(writer, 16384);

			bw.write("        SYNTHESE DES EVENEMENTS DU MATCH " + fileName);
			
			bw.newLine();
			bw.newLine();
			
			String score = Score.score(events);
			bw.write(" 		Score du match : 		" + score);
			
			bw.newLine();
			bw.newLine();

			for (int i = 0; i < events.size(); i++) {

				// CONVERTION DU TEMPS EN MINUTES:SECONDES
				double tps = events.get(i)[0]/fps;
				
				int minutes = (int) ((tps % 3600) / 60);
				int seconds = (int) (tps % 60);

				String time = String.format("%02d:%02d", minutes, seconds);
				

				switch (events.get(i)[1]) {
				case 1:
					bw.write(time);
					bw.write(" : But de l'équipe droite");
					break;

				case 2:
					bw.write(time);
					bw.write(" : But de l'équipe gauche");
					break;

				case 3:
					bw.write(time);
					bw.write(" : Sortie de balle");
					break;

				case 4:
					bw.write(time);
					bw.write(" : Gamelle de l'équipe droite");
					break;

				case 5:
					bw.write(time);
					bw.write(" : Gamelle de l'équipe gauche");
					break;

				case 6:
					bw.write(time);
					bw.write(" : But de l'équipe droite (cas douteux)");
					break;
				
				case 7: 
					bw.write(time);
					bw.write(" : But de l'équipe gauche (cas douteux)");
					break;
				}
				
				bw.newLine();
				bw.flush();
			}
			
			bw.close();
		}
	}

	public static void deleteFile(String chosenpath) {
		Path p = Paths.get(chosenpath);
		String nameWithExt = p.getFileName().toString();
		String fileName = nameWithExt.substring(0, nameWithExt.length() - 4);
		String parentPath = p.getParent().toString();
		String pathDossier = parentPath + "/FichiersSynthese";
		String path = pathDossier + "/Events_" + fileName + ".txt";
		
		File myFile = new File(path);
		if (myFile.exists()) {
			myFile.delete();
		}
	}

}
