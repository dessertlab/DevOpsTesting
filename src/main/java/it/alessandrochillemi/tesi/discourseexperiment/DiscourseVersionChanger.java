package it.alessandrochillemi.tesi.discourseexperiment;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import it.alessandrochillemi.tesi.frameutils.ApplicationFactory;
import it.alessandrochillemi.tesi.frameutils.FrameMap;
import it.alessandrochillemi.tesi.frameutils.discourse.DiscourseFactory;

//Questa classe carica i valori di p_vera e f_vera da una FrameMap a un'altra; può essere usata per caricare i parametri di una nuova versione
//dell'applicazione in una FrameMap esistente
public class DiscourseVersionChanger {

	public static void main(String[] args) {
		if(args.length != 2){
			System.err.println("\nInserire tutti i parametri!");
			return;
		}
		
		String currentFrameMapFilePath = args[0];
		
		String newVersionFrameMapFilePath = args[1];
		
		//Creo una ApplicationFactory per l'applicazione desiderata
		ApplicationFactory applicationFactory = new DiscourseFactory();
		
		//Carico la Frame Map della nuova versione
		FrameMap newVersionFrameMap = applicationFactory.makeFrameMap(newVersionFrameMapFilePath);
		
		//Se la Frame Map corrente non esiste, copio semplicemente la Frame Map della nuova versione in quella corrente e chiudo il programma
		if(!Files.exists(Paths.get(currentFrameMapFilePath))){
			System.out.println("\nLa FrameMap corrente non esiste, ne creo una nuova!");
			newVersionFrameMap.writeToCSVFile(currentFrameMapFilePath);
			return;
		}
		
		//Carico la Frame Map corrente
		FrameMap currentFrameMap = applicationFactory.makeFrameMap(currentFrameMapFilePath);
		
		//Carico le distribuzioni di fallimento vere della nuova versione
		ArrayList<Double> newTrueProbFailureDistribution = newVersionFrameMap.getTrueProbFailureDistribution();
		ArrayList<Double> newTrueProbCriticalFailureDistribution = newVersionFrameMap.getTrueProbCriticalFailureDistribution();
		
		//Imposto le distribuzioni di fallimento vere della versione corrente pari a quelle della nuova versione
		currentFrameMap.setTrueProbFailureDistribution(newTrueProbFailureDistribution);
		currentFrameMap.setTrueProbCriticalFailureDistribution(newTrueProbCriticalFailureDistribution);
		
		//Salvo la Frame Map corrente
		currentFrameMap.writeToCSVFile(currentFrameMapFilePath);
	}

}
