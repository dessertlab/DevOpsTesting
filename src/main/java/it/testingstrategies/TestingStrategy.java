package it.alessandrochillemi.tesi.testingstrategies;

import java.util.ArrayList;

import it.alessandrochillemi.tesi.frameutils.FrameMap;
import it.alessandrochillemi.tesi.frameutils.ResponseLogList;

//Strategia di test (da usare nel design pattern Strategy) che definisce il criterio di selezione dei test e l'algoritmo per il calcolo della reliability
public abstract class TestingStrategy {
	
	protected FrameMap frameMap;
	
	public TestingStrategy(FrameMap frameMap){
		this.frameMap = frameMap;
	}
	
	//IMPORTANTE: metodo da chiamare tra un ciclo di test/richieste e l'altro per calcolare la nuova distribuzione di probabilità con cui selezionare i frame
	public abstract void computeNewProbSelectionDistribution(boolean testingProfile);
	
	//Algoritmo per la selezione di un frame secondo la distribuzione di probabilità di testing o vera
	public abstract int selectFrame(boolean testingProfile);

	//Calcolo della reliability
	public abstract Double getReliability(ResponseLogList responseLogList);

	//Calcolo della reliability per i fallimenti critici
	public abstract Double getReliabilityForCriticalFailures(ResponseLogList responseLogList);

	//Calcolo della reliability vera
	public Double getTrueReliability(){
		ArrayList<Double> trueProbSelectionDistribution = frameMap.getTrueProbSelectionDistribution();
		
		ArrayList<Double> trueProbFailureDistribution = frameMap.getTrueProbFailureDistribution();
		
		int NFrames = trueProbSelectionDistribution.size();
		
		Double failProb = 0.0;	
		for(int i = 0; i<NFrames; i++){
			failProb += trueProbSelectionDistribution.get(i)*trueProbFailureDistribution.get(i);
		}
		
		Double reliability = 1d-failProb;
		
		return reliability;
	}

	//Calcolo della reliability vera per i fallimenti critici
	public Double getTrueReliabilityForCriticalFailures(){
		ArrayList<Double> trueProbSelectionDistribution = frameMap.getTrueProbSelectionDistribution();
		
		ArrayList<Double> trueProbCriticalFailureDistribution = frameMap.getTrueProbCriticalFailureDistribution();
		
		int NFrames = trueProbSelectionDistribution.size();
		
		Double failProb = 0.0;	
		for(int i = 0; i<NFrames; i++){
			failProb += trueProbSelectionDistribution.get(i)*trueProbCriticalFailureDistribution.get(i);
		}
		
		Double reliabilityForCriticalFailures = 1d-failProb;
		
		return reliabilityForCriticalFailures;
	}
}
