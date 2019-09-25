package it.alessandrochillemi.tesi.testingstrategies;

import java.util.ArrayList;

import org.apache.commons.lang3.RandomUtils;

import it.alessandrochillemi.tesi.frameutils.FrameMap;
import it.alessandrochillemi.tesi.frameutils.ResponseLogList;

public class FirstTestingStrategy extends TestingStrategy {
	
	public FirstTestingStrategy(FrameMap frameMap){
		super(frameMap);
	}

	//Algoritmo per la selezione di un frame secondo la distribuzione di probabilità specificata
	public int selectFrame(boolean testingProfile) {	
		ArrayList<Double> probSelectionDistribution = null;
		
		//Se testingProfile == true, uso la distribuzione di probabilità stimata (ovvero il profilo di testing)
		if(testingProfile){
			probSelectionDistribution = frameMap.getProbSelectionDistribution();
		}
		//Se testingProfile == false, uso la distribuzione di probabilità vera (ovvero il profilo utente)
		else{
			probSelectionDistribution = frameMap.getTrueProbSelectionDistribution();
		}
		
    	ArrayList<Double> cumulativePVector = new ArrayList<Double>();
    	
    	cumulativePVector.add(probSelectionDistribution.get(0));
    	
    	for(int i = 1; i<probSelectionDistribution.size(); i++){
    		Double d = cumulativePVector.get(i-1)+probSelectionDistribution.get(i);
    		cumulativePVector.add(d);
    	}
    	
    	double rand = RandomUtils.nextDouble(0, 1);
    	
    	int selectedFrame=-1;		
		for(int index =0; index<probSelectionDistribution.size(); index++){
			if (rand <= cumulativePVector.get(index)) {
				selectedFrame = index;
				break;
			}
		}
		return selectedFrame;
	}

	//Calcolo della reliability
	public Double getReliability(ResponseLogList responseLogList) {
		Double failProb = (new Double(responseLogList.getTotalNumberOfFailures()))/(new Double(responseLogList.size()));
		Double reliability = 1d - failProb;
		return reliability;
	}

	//Calcolo della reliability per i fallimenti critici
	public Double getReliabilityForCriticalFailures(ResponseLogList responseLogList) {
		Double criticalFailProb = (new Double(responseLogList.getTotalNumberOfCriticalFailures()))/(new Double(responseLogList.size()));
		Double reliabilityForCriticalFailures = 1d - criticalFailProb;
		return reliabilityForCriticalFailures;
	}

	//In questa strategia non c'è bisogno di calcolare una nuova distribuzione perché la selezione si basa unicamente sulla probabilità di selezione della FrameMap
	public void computeNewProbSelectionDistribution(boolean testingProfile) {
		return;
	}

}
