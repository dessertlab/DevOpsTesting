package it.alessandrochillemi.tesi.frameutils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class ResponseLogList{
	private enum header{
		FRAME_ID,RESPONSE_CODE,RESPONSE_MESSAGE,RESPONSE_TIME,
		P1_KEY,P1_TYPE,P1_CLASS,P1_POSITION,P1_RESOURCE_TYPE,P1_IS_REQUIRED,P1_VALID_VALUES,
		P2_KEY,P2_TYPE,P2_CLASS,P2_POSITION,P2_RESOURCE_TYPE,P2_IS_REQUIRED,P2_VALID_VALUES,
		P3_KEY,P3_TYPE,P3_CLASS,P3_POSITION,P3_RESOURCE_TYPE,P3_IS_REQUIRED,P3_VALID_VALUES,
		P4_KEY,P4_TYPE,P4_CLASS,P4_POSITION,P4_RESOURCE_TYPE,P4_IS_REQUIRED,P4_VALID_VALUES,
		P5_KEY,P5_TYPE,P5_CLASS,P5_POSITION,P5_RESOURCE_TYPE,P5_IS_REQUIRED,P5_VALID_VALUES,
		P6_KEY,P6_TYPE,P6_CLASS,P6_POSITION,P6_RESOURCE_TYPE,P6_IS_REQUIRED,P6_VALID_VALUES;
	};

	private ApplicationSpecifics applicationSpecifics;
	private ArrayList<ResponseLog> responseLogList;

	public ResponseLogList(ApplicationSpecifics applicationSpecifics){
		this.applicationSpecifics = applicationSpecifics;
		this.responseLogList = new ArrayList<ResponseLog>();
	}

	public ResponseLogList(String path,ApplicationSpecifics applicationSpecifics){
		this.applicationSpecifics = applicationSpecifics;
		responseLogList = new ArrayList<ResponseLog>();
		readFromCSVFile(path);
	}

	public int size(){
		return this.responseLogList.size();
	}

	public void add(ResponseLog responseLog){
		this.responseLogList.add(responseLog);
	}

	public ResponseLog get(int index){
		return this.responseLogList.get(index);
	}

	public void readFromCSVFile(String path){
		if(Files.exists(Paths.get(path))){
			Reader in;
			try {
				//Read the CSV file
				in = new FileReader(path);
				Iterable<CSVRecord> records = CSVFormat.RFC4180.withDelimiter(';').withFirstRecordAsHeader().parse(in);
				for (CSVRecord record : records) {
					//Create a new response log from the record
					ResponseLog responseLog = new ResponseLog(record,applicationSpecifics);

					//Add the frame to the map
					this.responseLogList.add(responseLog);

				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			System.out.println("File does not exist!");
		}
	}

	public void writeToCSVFile(String path) {
		BufferedWriter writer;
		try {
			writer = Files.newBufferedWriter(Paths.get(path));

			CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.RFC4180.withDelimiter(';').withHeader(header.class));

			Iterator<ResponseLog> iter = this.responseLogList.iterator();
			while (iter.hasNext()) {
				ResponseLog responseLog = iter.next();
				responseLog.writeToCSVRow(csvPrinter);
			}

			csvPrinter.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//Numero di risposte riferite al frame specificato
	public int count(String frameID){
		int count = 0;
		for(ResponseLog r : this.responseLogList){
			if(r.getFrameID().equals(frameID)){
				count++;
			}
		}
		return count;
	}

	//Ritorna il numero totale di fallimenti nella lista
	public int getTotalNumberOfFailures(){
		//Conto tutti i fallimenti
		int failuresCount = 0;
		//Analizzo ogni responseLog per capire se si tratta di un fallimento o meno
		for(int i = 0; i<responseLogList.size(); i++){
			ResponseLog responseLog = responseLogList.get(i);

			//Applico l'oracolo (dipendente dall'applicazione a cui questa risposta si riferisce) per sapere se si tratta di un fallimento o meno
			if(responseLog.isFailure()){
				failuresCount++;
			}
		}
		return failuresCount;
	}

	//Ritorna il numero totale di fallimenti critici nella lista
	public int getTotalNumberOfCriticalFailures(){
		//Conto i fallimenti critici
		int criticalFailuresCount = 0;
		//Analizzo ogni responseLog per capire se si tratta di un fallimento critico o meno
		for(int i = 0; i<responseLogList.size(); i++){
			ResponseLog responseLog = responseLogList.get(i);

			//Applico l'oracolo (dipendente dall'applicazione a cui questa risposta si riferisce) per sapere se si tratta di un fallimento critico o meno
			if(responseLog.isCriticalFailure()){
				criticalFailuresCount++;
			}
		}

		//Ritorno il numero di fallimenti critici
		return criticalFailuresCount;
	}

	//Ritorna il numero di fallimenti per il frame con Frame ID specificato
	public int getFrameFailures(String frameID){
		//Creo una response log list nella quale aggiungo solo le risposte relative al frame specificato
		ResponseLogList frameResponseLogList = new ResponseLogList(applicationSpecifics);

		for(int i = 0; i<responseLogList.size(); i++){
			ResponseLog responseLog = responseLogList.get(i);
			if(responseLog.getFrameID().equals(frameID)){
				frameResponseLogList.add(responseLog);
			}
		}

		//Ritorno il numero di fallimenti presenti nella nuova response log list, che sono relativi solo al frame specificato
		return frameResponseLogList.getTotalNumberOfFailures();
	}

	//Ritorna il numero di fallimenti critici per il frame con Frame ID specificato
	public int getFrameCriticalFailures(String frameID){
		//Creo una response log list nella quale aggiungo solo le risposte relative al frame specificato
		ResponseLogList frameResponseLogList = new ResponseLogList(applicationSpecifics);

		for(int i = 0; i<responseLogList.size(); i++){
			ResponseLog responseLog = responseLogList.get(i);
			if(responseLog.getFrameID().equals(frameID)){
				frameResponseLogList.add(responseLog);
			}
		}

		//Ritorno il numero di fallimenti critici presenti nella nuova response log list, che sono relativi solo al frame specificato
		return frameResponseLogList.getTotalNumberOfCriticalFailures();
	}

}
