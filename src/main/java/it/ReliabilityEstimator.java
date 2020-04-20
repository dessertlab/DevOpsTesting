package it.alessandrochillemi.tesi;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import it.alessandrochillemi.tesi.frameutils.ResponseLogList;
import it.alessandrochillemi.tesi.testingstrategies.TestingStrategy;

public class ReliabilityEstimator {
	
	private enum header{
		ESTIMATED_RELIABILITY,ESTIMATED_RELIABILITY_FOR_CRITICAL_FAILURES,
		TRUE_RELIABILITY,TRUE_RELIABILITY_FOR_CRITICAL_FAILURES
	};
	
	//Strategy design pattern
	private TestingStrategy testingStrategy;
	
	private Double estimatedReliability;
	private Double estimatedReliabilityForCriticalFailures;
	private Double trueReliability;
	private Double trueReliabilityForCriticalFailures;

	public ReliabilityEstimator(TestingStrategy testingStrategy){
		this.testingStrategy = testingStrategy;
	}

	public Double getEstimatedReliability() {
		return estimatedReliability;
	}

	public void setEstimatedReliability(Double estimatedReliability) {
		this.estimatedReliability = estimatedReliability;
	}

	public Double getEstimatedReliabilityForCriticalFailures() {
		return estimatedReliabilityForCriticalFailures;
	}

	public void setEstimatedReliabilityForCriticalFailures(Double estimatedReliabilityForCriticalFailures) {
		this.estimatedReliabilityForCriticalFailures = estimatedReliabilityForCriticalFailures;
	}

	public Double getTrueReliability() {
		return trueReliability;
	}

	public void setTrueReliability(Double trueReliability) {
		this.trueReliability = trueReliability;
	}

	public Double getTrueReliabilityForCriticalFailures() {
		return trueReliabilityForCriticalFailures;
	}

	public void setTrueReliabilityForCriticalFailures(Double trueReliabilityForCriticalFailures) {
		this.trueReliabilityForCriticalFailures = trueReliabilityForCriticalFailures;
	}

	public Double computeReliability(ResponseLogList testResponseLogList){
		this.estimatedReliability = testingStrategy.getReliability(testResponseLogList);
		return estimatedReliability;
	}
	
	public Double computeReliabilityForCriticalFailures(ResponseLogList testResponseLogList){
		this.estimatedReliabilityForCriticalFailures = testingStrategy.getReliabilityForCriticalFailures(testResponseLogList);
		return estimatedReliabilityForCriticalFailures;
	}
	
	public Double computeTrueReliability(){
		this.trueReliability = testingStrategy.getTrueReliability();
		return trueReliability;
	}
	
	public Double computeTrueReliabilityForCriticalFailures(){		
		this.trueReliabilityForCriticalFailures = testingStrategy.getTrueReliabilityForCriticalFailures();
		return trueReliabilityForCriticalFailures;
	}
	
	//Aggiunge le reliability a un file
	@SuppressWarnings("resource")
	public void appendToFile(String CSVFilePath){
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
			
			csvPrinter.printRecord(estimatedReliability,estimatedReliabilityForCriticalFailures,trueReliability,trueReliabilityForCriticalFailures);
			
			csvPrinter.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
