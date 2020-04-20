package it.alessandrochillemi.tesi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import it.alessandrochillemi.tesi.frameutils.ApplicationFactory;
import it.alessandrochillemi.tesi.frameutils.ResponseLogList;
import it.alessandrochillemi.tesi.frameutils.discourse.DiscourseFactory;

public class NumberOfFailures {

	//Percorso nel quale si trova il file con le variabili di ambiente
	public static String ENVIRONMENT_FILE_PATH = "/Users/alessandrochillemi/Desktop/Universita/Magistrale/Tesi/environment.properties";

	public static void main(String[] args) {

		ApplicationFactory appFactory = new DiscourseFactory();

		//Metto i tag delle versioni in una lista
		List<String> versionsList = Arrays.asList("2.1.6", "2.1.7", "2.1.8", "2.2.0", "2.2.1");

		//Creo due array di interi, uno per la tecnica OT e uno per la tecnica WOT, che, fissata una versione, conterranno il numero di fallimenti "comuni" (ovvero, tutti i fallimenti) di quella versione in ognuna delle 20 ripetizioni effettuate
		ArrayList<Integer> nFailuresOT = new ArrayList<Integer>();
		ArrayList<Integer> nFailuresWOT = new ArrayList<Integer>();

		//Calcolo il numero di fallimenti "non critici" (ovvero, tutti i fallimenti)
		System.out.println("\n+++++ TUTTI I FALLIMENTI +++++");

		//Effettuo un ciclo sulle 5 versioni testate: per ognuna di esse, calcolerò quanti fallimenti sono avvenuti nelle 20 ripetizioni effettuate
		for(int i = 0; i<versionsList.size(); i++){
			nFailuresOT = new ArrayList<Integer>();
			nFailuresWOT = new ArrayList<Integer>();

			//Effettuo un ciclo sulle 20 ripetizioni dell'esperimento 1 (tecnica OT)
			for(int j = 1; j<=20; j++){
				//Ricavo il path del file nel quale sono salvati i dati
				String path = "/Users/alessandrochillemi/Desktop/Universita/Magistrale/Tesi/esperimenti_discourse/experiment1_repetition"+j+"/test_responses/test_response_log_list_cycle1_v"+versionsList.get(i)+".csv";
				//Creo una ResponseLogList a partire dal path
				ResponseLogList responseLogList = appFactory.makeResponseLogList(path);
				//Aggiungo all'array per la tecnica OT il numero di fallimenti (considerando tutti i fallimenti) contenuti nella ResponseLogList
				nFailuresOT.add(responseLogList.getTotalNumberOfFailures());
			}

			//Effettuo un ciclo sulle 20 ripetizioni dell'esperimento 3 (tecnica WOT)
			for(int j = 1; j<=20; j++){
				//Ricavo il path del file nel quale sono salvati i dati
				String path = "/Users/alessandrochillemi/Desktop/Universita/Magistrale/Tesi/esperimenti_discourse/experiment3_repetition"+j+"/test_responses/test_response_log_list_cycle1_v"+versionsList.get(i)+".csv";
				//Creo una ResponseLogList a partire dal path
				ResponseLogList responseLogList = appFactory.makeResponseLogList(path);
				//Aggiungo all'array per la tecnica WOT il numero di fallimenti (considerando tutti i fallimenti) contenuti nella ResponseLogList
				nFailuresWOT.add(responseLogList.getTotalNumberOfFailures());
			}
			
			//Stampo i due array costruiti nei due cicli appena effettuati (entrambi contenenti 20 valori, uno per ognuna delle ripetizioni), relativi alla versione corrente
			System.out.println("\n\n ----- Versione " + versionsList.get(i) + " ------:");

			System.out.println("\nOT: ");
			for(Integer nFailures : nFailuresOT){
				System.out.print(nFailures + " ");
			}

			System.out.println("\nWOT: ");
			for(Integer nFailures : nFailuresWOT){
				System.out.print(nFailures + " ");
			}

		}

		//Creo due array di interi, uno per la tecnica OT e uno per la tecnica WOT, che, fissata una versione, conterranno il numero di fallimenti "critici" (ovvero, con codice di risposta 500) di quella versione in ognuna delle 20 ripetizioni effettuate
		ArrayList<Integer> nCriticalFailuresOT = new ArrayList<Integer>();
		ArrayList<Integer> nCriticalFailuresWOT = new ArrayList<Integer>();

		//Calcolo il numero di fallimenti "critici" (ovvero, con codice di risposta 500)
		System.out.println("\n\n+++++ FALLIMENTI CRITICI +++++");

		//Effettuo un ciclo sulle 5 versioni testate: per ognuna di esse, calcolerò quanti fallimenti sono avvenuti nelle 20 ripetizioni effettuate
		for(int i = 0; i<versionsList.size(); i++){
			nCriticalFailuresOT = new ArrayList<Integer>();
			nCriticalFailuresWOT = new ArrayList<Integer>();

			//Effettuo un ciclo sulle 20 ripetizioni dell'esperimento 1 (tecnica OT)
			for(int j = 1; j<=20; j++){
				//Ricavo il path del file nel quale sono salvati i dati
				String path = "/Users/alessandrochillemi/Desktop/Universita/Magistrale/Tesi/esperimenti_discourse/experiment1_repetition"+j+"/test_responses/test_response_log_list_cycle1_v"+versionsList.get(i)+".csv";
				//Creo una ResponseLogList a partire dal path
				ResponseLogList responseLogList = appFactory.makeResponseLogList(path);
				//Aggiungo all'array per la tecnica OT il numero di fallimenti (considerando solo i fallimenti "critici") contenuti nella ResponseLogList
				nCriticalFailuresOT.add(responseLogList.getTotalNumberOfCriticalFailures());
			}

			//Effettuo un ciclo sulle 20 ripetizioni dell'esperimento 2 (tecnica WOT)
			for(int j = 1; j<=20; j++){
				//Ricavo il path del file nel quale sono salvati i dati
				String path = "/Users/alessandrochillemi/Desktop/Universita/Magistrale/Tesi/esperimenti_discourse/experiment2_repetition"+j+"/test_responses/test_response_log_list_cycle1_v"+versionsList.get(i)+".csv";
				//Creo una ResponseLogList a partire dal path
				ResponseLogList responseLogList = appFactory.makeResponseLogList(path);
				//Aggiungo all'array per la tecnica WOT il numero di fallimenti (considerando solo i fallimenti "critici") contenuti nella ResponseLogList
				nCriticalFailuresWOT.add(responseLogList.getTotalNumberOfCriticalFailures());
			}
			
			//Stampo i due array costruiti nei due cicli appena effettuati (entrambi contenenti 20 valori, uno per ognuna delle ripetizioni), relativi alla versione corrente
			System.out.println("\n\n ----- Versione " + versionsList.get(i) + " ------:");

			System.out.println("\nOT: ");
			for(Integer nCriticalFailures : nCriticalFailuresOT){
				System.out.print(nCriticalFailures + " ");
			}

			System.out.println("\nWOT: ");
			for(Integer nCriticalFailures : nCriticalFailuresWOT){
				System.out.print(nCriticalFailures + " ");
			}

		}

	}

}
