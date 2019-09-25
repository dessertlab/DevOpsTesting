package it.alessandrochillemi.tesi.discourseexperiment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

import it.alessandrochillemi.tesi.APIRequest;
import it.alessandrochillemi.tesi.frameutils.ApplicationFactory;
import it.alessandrochillemi.tesi.frameutils.Frame;
import it.alessandrochillemi.tesi.frameutils.FrameMap;
import it.alessandrochillemi.tesi.frameutils.Param;
import it.alessandrochillemi.tesi.frameutils.ResponseLog;
import it.alessandrochillemi.tesi.frameutils.ResponseLogList;
import it.alessandrochillemi.tesi.frameutils.discourse.DiscourseFactory;
import okhttp3.Response;

//Genera la distribuzione di probabilità di fallimento vera eseguendo N richieste per ogni frame e registrando la proporzione di esse che falliscono
public class DiscourseTrueProbFailureRequestGenerator {

	//Percorso nel quale si trova il file con le variabili di ambiente
	public static String EXPERIMENT_DIRECTORY_PATH = "/Users/alessandrochillemi/Desktop/Universita/Magistrale/Tesi/";

	//Numero di richieste da eseguire per ogni frame
	public static int NREQUESTS = 5;

	private static String frameMapFilePath;
	
	private static String baseURL;
	private static String apiUsername;
	private static String apiKey;

	private static int loadEnvironment(){

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
		
		return 0;
	}

	public static void main(String[] args) {
		//Carico le variabili d'ambiente
		loadEnvironment();

		//Creo una ApplicationFactory per l'applicazione desiderata
		ApplicationFactory applicationFactory = new DiscourseFactory();

		FrameMap frameMap = null;
		//Carico la frame map; se il file non esiste, esco dal programma
		if(Files.exists(Paths.get(frameMapFilePath))){
			frameMap = applicationFactory.makeFrameMap(frameMapFilePath);
		}
		else{
			System.out.println("\nFrame Map non trovata!");
			return;
		}

		//Creo i due array per le nuove distribuzioni di probabilità che sto per calcolare
		ArrayList<Double> newTrueProbFailureDistribution = new ArrayList<Double>();
		ArrayList<Double> newTrueProbCriticalFailureDistribution = new ArrayList<Double>();

		//Scorro tutti i frame
		for(int i = 0; i<frameMap.size(); i++){

			//Leggo il frame con l'indice corrente
			System.out.println("\nFrame corrente: " + (i+1));
			Frame frame = frameMap.readByKey(i);

			//Creo una ResponseLogList per salvare le risposte alle NREQUESTS per il frame corrente
			ResponseLogList responseLogList = applicationFactory.makeResponseLogList();

			//Eseguo le NREQUESTS per il frame corrente e salvo le risposte
			for(int j = 0; j<NREQUESTS; j++){
				
				System.out.println("\nRichiesta " + (j+1) + "...");
				//Genero i valori dei parametri applicando le precondizioni
				for(Param p : frame.getParamList()){
					p.generateValueWithPreConditions(baseURL,apiUsername,apiKey,true);
				}

				//Creo una APIRequest con i campi del Frame estratto
				APIRequest apiRequest = new APIRequest(frame);
				apiRequest.setBaseURL(baseURL);
				apiRequest.setApiUsername(apiUsername);
				apiRequest.setApiKey(apiKey);

				//Invio la richiesta
				Response response = apiRequest.sendRequest();

				int responseCode = 0;
				String responseMessage = "";
				long responseTime = 0;
				//Se la richiesta è andata a buon fine, salvo i risultati e chiudo la risposta
				if(response != null){
					responseCode = response.code();
					responseMessage = response.message();
					responseTime = response.receivedResponseAtMillis() - response.sentRequestAtMillis();
					response.close();  
				}
				//Se non è andata a buon fine, la considero un fallimento dell'applicazione e lascio vuoto il messaggio di risposta
				else{
					responseCode = 500;
					responseMessage = "";
				}

				//Salvo la risposta nella ResponseLogList
				ResponseLog responseLog = applicationFactory.makeResponseLog(Integer.toString(i, 10), responseCode, responseMessage, responseTime, apiRequest.getParamList());

				responseLogList.add(responseLog);
			}

			//Calcolo la proporzione di fallimenti sul numero di richieste NREQUESTS e lo aggiungo al nuovo array
			Double totalNumberOfFailures = new Double(responseLogList.getTotalNumberOfFailures());
			Double newTrueProbFailure = totalNumberOfFailures/(new Double(NREQUESTS));
			newTrueProbFailureDistribution.add(newTrueProbFailure);

			//Calcolo la proporzione di fallimenti critici sul numero di richieste NREQUESTS e lo aggiungo al nuovo array
			Double totalNumberOfCriticalFailures = new Double(responseLogList.getTotalNumberOfCriticalFailures());
			Double newTrueProbCriticalFailure = totalNumberOfCriticalFailures/(new Double(NREQUESTS));
			newTrueProbCriticalFailureDistribution.add(newTrueProbCriticalFailure);

		}

		//Imposto le nuove distribuzioni di probabilità
		frameMap.setTrueProbFailureDistribution(newTrueProbFailureDistribution);
		frameMap.setTrueProbCriticalFailureDistribution(newTrueProbCriticalFailureDistribution);
		
		frameMap.writeToCSVFile(frameMapFilePath);
		
		System.out.println("\nDistribuzioni di probabilità di fallimento aggiornate!");

	}

}
