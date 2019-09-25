package it.alessandrochillemi.tesi;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import it.alessandrochillemi.tesi.frameutils.ApplicationFactory;
import it.alessandrochillemi.tesi.frameutils.FrameMap;
import it.alessandrochillemi.tesi.frameutils.ResponseLogList;
import it.alessandrochillemi.tesi.frameutils.discourse.DiscourseFactory;

//Classe per calcolare i tempi di risposta stimati e reali dell'applicazione
public class PerformanceEstimator {
	
	private enum header{
		ESTIMATED_RESPONSE_TIME,TRUE_RESPONSE_TIME
	};
	
	private static Double estimatedResponseTime;
	private static Double trueResponseTime;

	public static void main(String[] args) {
		
		String experimentBaseDirectoryString = "/Users/alessandrochillemi/Desktop/Universita/Magistrale/Tesi/scripting/experiment1_repetition";
		String experimentDirectoryString = null;
		
		ArrayList<String> versionsList = new ArrayList<String>();
		versionsList.add("2.1.6");
		versionsList.add("2.1.7");
		versionsList.add("2.1.8");
		versionsList.add("2.2.0");
		versionsList.add("2.2.1");
		
		ApplicationFactory applicationFactory = new DiscourseFactory();
		
		for(int i = 0; i<20; i++){
			//Ricavo il percorso della cartella relativa all'esperimento corrente
			experimentDirectoryString = new String(experimentBaseDirectoryString+(i+1));
			
			estimatePerformance(experimentDirectoryString,versionsList, applicationFactory);
			
			System.out.println("\nPerformance calcolate per la ripetizione " + (i+1));
		}
	}
	
	//WARNING: funziona solo per esperimenti con un solo ciclo per ogni versione
	public static void estimatePerformance(String experimentPathString, ArrayList<String> versionsList, ApplicationFactory applicationFactory){
		String frameMapsDirectoryString = Paths.get(experimentPathString, "frameMaps").toString();
		String userResponsesDirectoryString = Paths.get(experimentPathString, "user_responses").toString();
		String testResponsesDirectoryString = Paths.get(experimentPathString, "test_responses").toString();
		
		for(int i = 0; i<versionsList.size(); i++){
			Double newEstimatedResponseTime = 0.0;
			Double newTrueResponseTime = 0.0;
			
			//Ottengo la FrameMap relativa alla versione corrente
			String frameMapFilePathString = Paths.get(frameMapsDirectoryString, "frameMap_cycle1_v"+versionsList.get(i)+".csv").toString();
			FrameMap frameMap = applicationFactory.makeFrameMap(frameMapFilePathString);
			
			//Ottengo la ResponseLogList relativa alle risposte alle richieste utente per la versione corrente
			String userResponseLogListPathString = Paths.get(userResponsesDirectoryString, "user_response_log_list_cycle1_v"+versionsList.get(i)+".csv").toString();
			ResponseLogList userResponseLogList = applicationFactory.makeResponseLogList(userResponseLogListPathString);
			
			//Ottengo la ResponseLogList relativa alle risposte ai test per la versione corrente
			String testResponseLogListPathString = Paths.get(testResponsesDirectoryString, "test_response_log_list_cycle1_v"+versionsList.get(i)+".csv").toString();
			ResponseLogList testResponseLogList = applicationFactory.makeResponseLogList(testResponseLogListPathString);
			
			//Calcolo il tempo di risposta stimato e vero per la versione corrente considerando sia le risposte alle richieste utente sia le risposte ai test
			for(int j=0;j<userResponseLogList.size();j++){
				Integer frameID = Integer.parseInt(userResponseLogList.get(j).getFrameID());
				long responseTime = userResponseLogList.get(j).getResponseTime();
				Double estimatedFrameProbSelection = frameMap.readByKey(frameID).getProbSelection();
				Double trueFrameProbSelection = frameMap.readByKey(frameID).getTrueProbSelection();
				
				newEstimatedResponseTime += new Double(estimatedFrameProbSelection*responseTime);
				newTrueResponseTime += new Double(trueFrameProbSelection*responseTime);
			}
			
			for(int j=0;j<testResponseLogList.size();j++){
				Integer frameID = Integer.parseInt(testResponseLogList.get(j).getFrameID());
				long responseTime = testResponseLogList.get(j).getResponseTime();
				Double estimatedFrameProbSelection = frameMap.readByKey(frameID).getProbSelection();
				Double trueFrameProbSelection = frameMap.readByKey(frameID).getTrueProbSelection();
				
				newEstimatedResponseTime += new Double(estimatedFrameProbSelection*responseTime);
				newTrueResponseTime += new Double(trueFrameProbSelection*responseTime);
			}
			
			//Memorizzo i risultati ottenuti
			estimatedResponseTime = newEstimatedResponseTime;
			trueResponseTime = newTrueResponseTime;
			
			//Scrivo i risultati ottenuti su un file
			String performanceFilePathString = Paths.get(experimentPathString, "performance.csv").toString();
			appendToFile(performanceFilePathString);
		}
	}

	@SuppressWarnings("resource")
	public static void appendToFile(String CSVFilePath){
		BufferedWriter writer;
		try {
			CSVPrinter csvPrinter = null;
			if(Files.exists(Paths.get(CSVFilePath))){
				writer = Files.newBufferedWriter(Paths.get(CSVFilePath), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
				csvPrinter = new CSVPrinter(writer, CSVFormat.RFC4180.withDelimiter(';'));
			}
			else{
				writer = Files.newBufferedWriter(Paths.get(CSVFilePath), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
				csvPrinter = new CSVPrinter(writer, CSVFormat.RFC4180.withDelimiter(';').withHeader(header.class));
			}
			
			//Formatto i risultati in modo da stamparli con le virgole come separatori decimali
			Locale currentLocale = Locale.ITALY;
			NumberFormat numberFormatter = NumberFormat.getNumberInstance(currentLocale);
			
			String estimatedResponseTimeFormatted = numberFormatter.format(estimatedResponseTime);
			String trueResponseTimeFormatted = numberFormatter.format(trueResponseTime);
			
			//Stampo i risultati sul file CSV
			csvPrinter.printRecord(estimatedResponseTimeFormatted,trueResponseTimeFormatted);
			
			csvPrinter.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
