package it.alessandrochillemi.tesi.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.alessandrochillemi.tesi.frameutils.FrameMap;

//Genera il profilo di selezione vero a partire da quello di testing in base a una certa percentuale di variazione scelta dall'utente
public class TrueProbSelectionGenerator {
	
	private static Random random = new Random();

	public static ArrayList<Double> generateNewProbDistribution(FrameMap frameMap, Double variation) {
		
//		//Calcolo la dimensione dei due set (al netto di errori di round off) che avranno una variazione proporzionale rispettivamente a +VARIATION e -VARIATION;
//		//set1SizeProportion indica la percentuale dell'insieme dei frame che avranno una variazione proporzionale a +VARIATION; la restante parte di frame avrà 
//		//una variazione proporzionale a -VARIATION
//		int set1Size = (int) (set1SizeProportion*estimatedProbSelectionDistribution.size());
//		int set2Size = estimatedProbSelectionDistribution.size() - set1Size;
//		
//		//Calcolo la somma alle quali devono arrivare i valori casuali calcolati per i due set
//		Double targetSumSet = variation/2.0;
//		
//		List<Double> set1 = null;	
//		List<Double> set2 = null;
//		
//		//Ottengo i due set di numeri casuali in modo tale che i valori negativi vengano sommati all'insieme più grande
//		if(set1Size>=set2Size){
//			set1 = randomsToTargetSum(set1Size,-targetSumSet);	
//			set2 = randomsToTargetSum(set2Size,targetSumSet);
//		}
//		else{
//			set1 = randomsToTargetSum(set1Size,targetSumSet);	
//			set2 = randomsToTargetSum(set2Size,-targetSumSet);
//		}
//		
//		//Concateno le due liste ed eseguo uno shuffling
//		List<Double> finalSet = new ArrayList<Double>(set1);
//		finalSet.addAll(set2);
		
		ArrayList<Double> estimatedProbSelectionDistribution = frameMap.getProbSelectionDistribution();
		
		ArrayList<Integer> criticalFailureIndices = new ArrayList<Integer>();
		ArrayList<Integer> notCriticalFailureIndices = new ArrayList<Integer>();
		
		//Salvo separatamente gli indici dei frame che hanno probabilità di fallimento critica vera pari a 1 e quelli che la hanno diversa da 1
		for(int i = 0; i<frameMap.size(); i++){
			if(frameMap.readByKey(i).getTrueProbCriticalFailure() == 1.0){
				criticalFailureIndices.add(i);
			}
			else{
				notCriticalFailureIndices.add(i);
			}
		}
		
		//Ottengo la dimensione dei due set di numeri casuali, che andranno sommati alle probabilità dei frame con gli indici calcolati
		int set1Size = criticalFailureIndices.size();
		int set2Size = notCriticalFailureIndices.size();
		
		//Calcolo la somma alle quali devono arrivare i valori casuali calcolati per i due set
		Double targetSumSet = variation/2.0;
		
		List<Double> set1 = null;	
		List<Double> set2 = null;
		
		//Ottengo i due set di numeri casuali in modo tale che i valori negativi vengano sommati all'insieme più grande
		if(set1Size>=set2Size){
			set1 = randomsToTargetSum(set1Size,-targetSumSet);	
			set2 = randomsToTargetSum(set2Size,targetSumSet);
		}
		else{
			set1 = randomsToTargetSum(set1Size,targetSumSet);	
			set2 = randomsToTargetSum(set2Size,-targetSumSet);
		}
		
		//Sommo gli insiemi di numeri casuali calcolati agli opportuni elementi (quelli con gli indici selezionati sopra) della distribuzione di probabilità di partenza
		ArrayList<Double> trueProbSelectionDistribution = frameMap.getTrueProbSelectionDistribution();
		for(int i = 0; i<set1Size; i++){
			int index = criticalFailureIndices.get(i);
			trueProbSelectionDistribution.set(index,estimatedProbSelectionDistribution.get(index)+set1.get(i));
		}
		
		for(int i = 0; i<set2Size; i++){
			int index = notCriticalFailureIndices.get(i);
			trueProbSelectionDistribution.set(index,estimatedProbSelectionDistribution.get(index)+set2.get(i));
		}
		
//		//Se nella distribuzione di probabilità di selezione vera appena calcolata sono presenti numeri negativi, sommo 1 a tutti gli elementi e normalizzo
//		if(DoubleUtils.areThereNegatives(trueProbSelectionDistribution)){
//			for(int i = 0; i<trueProbSelectionDistribution.size(); i++){
//				trueProbSelectionDistribution.set(i, trueProbSelectionDistribution.get(i)+1.0);
//			}
//			DoubleUtils.normalize(trueProbSelectionDistribution);
//		}
		
		//Metto a 0 gli eventuali elementi negativi e normalizzo
		DoubleUtils.rectify(trueProbSelectionDistribution);
		DoubleUtils.normalize(trueProbSelectionDistribution);
		
//		if(DoubleUtils.areThereNegatives(trueProbSelectionDistribution)){
//			for(int i = 0; i<trueProbSelectionDistribution.size(); i++){
//				trueProbSelectionDistribution.set(i, trueProbSelectionDistribution.get(i)+1.0);
//			}
//			DoubleUtils.normalize(trueProbSelectionDistribution);
//		}
		
		//Ritorno la distribuzione di probabilità di selezione calcolata
		return trueProbSelectionDistribution;

	}
	
	//Ottiene un array di nRandoms Double la cui somma è pari a targetSum
	private static ArrayList<Double> randomsToTargetSum(int nRandoms, Double targetSum){
		ArrayList<Double> ret = new ArrayList<Double>();
		
		//random numbers
	    Double sum = 0.0;
	    for (int i = 0; i < nRandoms; i++) {
	        Double next = DoubleUtils.randomInRange(random,0.0,targetSum);
	        ret.add(next);
	        sum += next;
	    }

	    //scale to the desired target sum
	    double scale = targetSum / sum;
	    for (int i = 0; i < nRandoms; i++) {
	    	ret.set(i, (ret.get(i) * scale));
	    }
	    
	    return ret;
	}

}
