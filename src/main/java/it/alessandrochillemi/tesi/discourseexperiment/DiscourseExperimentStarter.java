package it.alessandrochillemi.tesi.discourseexperiment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

import it.alessandrochillemi.tesi.Monitor;
import it.alessandrochillemi.tesi.ReliabilityEstimator;
import it.alessandrochillemi.tesi.TestGenerator;
import it.alessandrochillemi.tesi.WorkloadGenerator;
import it.alessandrochillemi.tesi.frameutils.ApplicationFactory;
import it.alessandrochillemi.tesi.frameutils.FrameMap;
import it.alessandrochillemi.tesi.frameutils.ResponseLogList;
import it.alessandrochillemi.tesi.frameutils.discourse.DiscourseFactory;
import it.alessandrochillemi.tesi.testingstrategies.FirstTestingStrategy;
import it.alessandrochillemi.tesi.testingstrategies.TestingStrategy;

public class DiscourseExperimentStarter {
	
	//Numero di cicli di test da effettuare (parametro obbligatorio definito dall'utente)
	public static int NCYCLES;
	
	//Numero di test da eseguire a ogni ciclo (parametro obbligatorio definito dall'utente)
	public static int NTESTS;
	
	//Numero di richieste da inviare a ogni ciclo (parametro obbligatorio definito dall'utente)
	public static int NREQUESTS;
	
	//Learning rate per l'aggiornamento delle distribuzioni di probabilità (parametro obbligatorio definito dall'utente)
	public static Double LEARNING_RATE = 0.5;
	
	//Percorso nel quale si trova il file con le variabili di ambiente (parametro obbligatorio definito dall'utente)
	public static String EXPERIMENT_DIRECTORY_PATH;
	
	//Versione dell'applicazione che si sta testando (parametro opzionale definito dall'utente)
	public static String VERSION = "vUNSPECIFIED";

	private static String frameMapFilePath;
	private static String newFrameMapDirectoryString;
	private static String testResponseDirectoryString;
	private static String userResponseDirectoryString;
	
	private static String baseURL;
	private static String apiUsername;
	private static String apiKey;

	private static int loadEnvironment(String[] args){
		
		//Carico i parametri inseriti dall'utente
		NCYCLES = Integer.valueOf(args[0]);
		NTESTS = Integer.valueOf(args[1]);
		NREQUESTS = Integer.valueOf(args[2]);
		LEARNING_RATE = Double.valueOf(args[3]);
		EXPERIMENT_DIRECTORY_PATH = args[4];
		if(args[5] != null){
			VERSION = args[5];
		}
		
		//Carico le variabili d'ambiente (path della lista di testframe, api_key, api_username, ecc.)
		String environmentFilePath = Paths.get(EXPERIMENT_DIRECTORY_PATH,"env.properties").toString();
		Properties environment = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(environmentFilePath);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			environment.load(is);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//Leggo le variabili d'ambiente
		baseURL = environment.getProperty("base_url");
		apiUsername = environment.getProperty("api_username");
		apiKey = environment.getProperty("api_key");
		
		//Ricavo il path per la Frame Map; se non esiste, chiudo il programma
		frameMapFilePath = Paths.get(EXPERIMENT_DIRECTORY_PATH,"frames.csv").toString();
		if(!Files.exists(Paths.get(frameMapFilePath))){
			System.out.println("\nFrame Map non trovata!");
			return -1;
		}
		
		//Ricavo il path per la directory nella quale salvare le nuove frame map; se non esiste, chiudo il programma
		newFrameMapDirectoryString = Paths.get(EXPERIMENT_DIRECTORY_PATH, "frameMaps").toString();
		if(!Files.exists(Paths.get(newFrameMapDirectoryString))){
			System.out.println("\nDirectory \"frameMaps\" non trovata!");
			return -1;
		}
		
		//Ricavo il path per la directory nella quale salvare le risposte ai test; se non esiste, chiudo il programma
		testResponseDirectoryString = Paths.get(EXPERIMENT_DIRECTORY_PATH, "test_responses").toString();
		if(!Files.exists(Paths.get(testResponseDirectoryString))){
			System.out.println("\nDirectory \"test_responses\" non trovata!");
			return -1;
		}
		
		//Ricavo il path per la directory nella quale salvare le risposte alle richieste; se non esiste, chiudo il programma
		userResponseDirectoryString = Paths.get(EXPERIMENT_DIRECTORY_PATH, "user_responses").toString();
		if(!Files.exists(Paths.get(userResponseDirectoryString))){
			System.out.println("\nDirectory \"user_responses\" non trovata!");
			return -1;
		}
		
		return 0;
	}

	public static void main(String[] args) {
		
		if(args.length < 5){
			System.err.println("\nInserire tutti i parametri!");
			return;
		}
		
		//Carico le variabili d'ambiente
		if(loadEnvironment(args)>=0){
			System.out.println("\nParametri caricati!");
		}
		else{
			return;
		}
		
		//Creo una ApplicationFactory per l'applicazione desiderata
		ApplicationFactory applicationFactory = new DiscourseFactory();
		
		//Carico la frame map
		FrameMap frameMap = applicationFactory.makeFrameMap(frameMapFilePath);
		
		//Scelgo la strategia di testing
		TestingStrategy testingStrategy = new FirstTestingStrategy(frameMap);
		
		//Creo un test generator
		TestGenerator testGenerator = new TestGenerator(testingStrategy);
		
		//Creo un workload generator
		WorkloadGenerator workloadGenerator = new WorkloadGenerator(testingStrategy);
		
		//Creo uno stimatore della reliability
		ReliabilityEstimator reliabilityEstimator = new ReliabilityEstimator(testingStrategy);
		
		//Calcolo la reliability vera
		reliabilityEstimator.computeTrueReliability();
		reliabilityEstimator.computeTrueReliabilityForCriticalFailures();
		
		//Creo un monitor
		Monitor monitor = new Monitor();
		
		for(int i = 0; i<NCYCLES; i++){
			System.out.println("\nCiclo " + (i+1) + " avviato");
			
			//Salvo la frameMap relativa a questo ciclo
			String newFrameMapFileName = "frameMap_cycle"+(i+1)+"_"+VERSION+".csv";
			String newFrameMapFilePath = Paths.get(newFrameMapDirectoryString, newFrameMapFileName).toString();
			frameMap.writeToCSVFile(newFrameMapFilePath);
			
			//Eseguo NTESTS test selezionando i frame dalla frame map ottenuta e ottengo le risposte
			ResponseLogList testResponseLogList = testGenerator.generateTests(baseURL, apiUsername, apiKey, frameMap, NTESTS, applicationFactory);
		
			//Salvo le risposte ai test su un file
			String testResponseLogListFileName = "test_response_log_list_cycle"+(i+1)+"_"+VERSION+".csv";
			String testResponseLogListFilePath = Paths.get(testResponseDirectoryString, testResponseLogListFileName).toString();
			testResponseLogList.writeToCSVFile(testResponseLogListFilePath);			
			System.out.println("\nTest eseguiti");

			//Calcolo le reliability
			reliabilityEstimator.computeReliability(testResponseLogList);
			reliabilityEstimator.computeReliabilityForCriticalFailures(testResponseLogList);
			System.out.println("\nReliability calcolata");
			
			//Aggiorno il file contenente le reliability calcolate finora
			reliabilityEstimator.appendToFile(Paths.get(EXPERIMENT_DIRECTORY_PATH,"reliability.csv").toString());
			
			//Eseguo NREQUESTS richieste selezionando i frame dalla frame map e ottengo le risposte
			ResponseLogList userResponseLogList = workloadGenerator.generateRequests(baseURL, apiUsername, apiKey, frameMap, NREQUESTS, applicationFactory);
			
			//Salvo le risposte alle richieste su un file
			String userResponseLogListFileName = "user_response_log_list_cycle"+(i+1)+"_"+VERSION+".csv";
			String userResponseLogListFilePath = Paths.get(userResponseDirectoryString, userResponseLogListFileName).toString();
			userResponseLogList.writeToCSVFile(userResponseLogListFilePath);			
			System.out.println("\nRichieste eseguite");
			
			//Ottengo le attuali probSelection, probFailure e probCriticalFailure
			ArrayList<Double> oldProbSelectionDistribution = frameMap.getProbSelectionDistribution();
			ArrayList<Double> oldProbFailureDistribution = frameMap.getProbFailureDistribution();
			ArrayList<Double> oldProbCriticalFailureDistribution = frameMap.getProbCriticalFailureDistribution();
			
			//Ottengo le probSelection, probFailure e probCriticalFailure per il ciclo successivo
			ArrayList<Double> newProbSelectionDistribution = monitor.updateProbSelectionDistribution(oldProbSelectionDistribution, userResponseLogList, LEARNING_RATE);
			ArrayList<Double> newProbFailureDistribution = monitor.updateProbFailureDistribution(oldProbFailureDistribution, userResponseLogList, LEARNING_RATE);
			ArrayList<Double> newProbCriticalFailureDistribution = monitor.updateProbCriticalFailureDistribution(oldProbCriticalFailureDistribution, userResponseLogList, LEARNING_RATE);
			
			//Aggiorno la frame map con le nuove distribuzioni
			frameMap.setProbSelectionDistribution(newProbSelectionDistribution);
			frameMap.setProbFailureDistribution(newProbFailureDistribution);
			frameMap.setProbCriticalFailureDistribution(newProbCriticalFailureDistribution);
			
			//Salvo la Frame Map con le nuove distribuzioni
			frameMap.writeToCSVFile(frameMapFilePath);
		}
		
		System.out.println("\nTesting terminato!");

	}

}
