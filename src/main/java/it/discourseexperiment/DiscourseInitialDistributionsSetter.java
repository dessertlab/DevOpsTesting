package it.alessandrochillemi.tesi.discourseexperiment;

import java.util.ArrayList;

import org.apache.commons.lang3.RandomUtils;

import it.alessandrochillemi.tesi.frameutils.ApplicationFactory;
import it.alessandrochillemi.tesi.frameutils.Frame;
import it.alessandrochillemi.tesi.frameutils.FrameMap;
import it.alessandrochillemi.tesi.frameutils.Param;
import it.alessandrochillemi.tesi.frameutils.discourse.DiscourseFactory;
import it.alessandrochillemi.tesi.utils.DoubleUtils;
import it.alessandrochillemi.tesi.utils.TrueProbSelectionGenerator;

//Questa classe imposta i valori iniziali delle distribuzioni di probabilità così come specificato dall'utente
public class DiscourseInitialDistributionsSetter {

	public static void main(String[] args) {
		if(args.length != 3){
			System.err.println("\nInserire tutti i parametri!");
			return;
		}
		
		String frameMapFilePath = args[0];
		String strategyID = args[1];		
		Double variation = Double.parseDouble(args[2]);
		
		ApplicationFactory appFactory = new DiscourseFactory();
		FrameMap frameMap = null;
		
		if(strategyID.equals("1")){
			frameMap = generateDistributionsForFirstStrategy(appFactory,frameMapFilePath, variation);
		}
		else if(strategyID.equals("2")){
			frameMap = generateDistributionsForSecondStrategy(appFactory,frameMapFilePath, variation);
		}
		else{
			System.err.println("\nStrategia non riconosciuta!");
			return;
		}
		
		if(frameMap != null){
			frameMap.writeToCSVFile(frameMapFilePath);
		}

	}
	
	public static FrameMap generateDistributionsForFirstStrategy(ApplicationFactory appFactory, String frameMapFilePath, Double variation){

		//Carico la frame map con le f_vere già calcolate preliminarmente
		FrameMap frameMap = appFactory.makeFrameMap(frameMapFilePath);

		//Carico le distribuzioni di probabilità attuali, che saranno modificate
		ArrayList<Double> probSelectionDistribution = frameMap.getProbSelectionDistribution();
		ArrayList<Double> probFailureDistribution = frameMap.getProbFailureDistribution();
		ArrayList<Double> probCriticalFailureDistribution = frameMap.getProbCriticalFailureDistribution();
		ArrayList<Double> trueProbSelectionDistribution = frameMap.getTrueProbSelectionDistribution();

		int NFrames = frameMap.size();

		//Generazione probabilità di selezione iniziale stimata (casuale)
		for(int i = 0; i<NFrames; i++){
			probSelectionDistribution.set(i, RandomUtils.nextDouble(0,1.0));
		}
		DoubleUtils.normalize(probSelectionDistribution);	
		frameMap.setProbSelectionDistribution(probSelectionDistribution);

		//Generazione probabilità di fallimento iniziale stimata (tutte pari a 0)
		for(int i = 0; i<NFrames; i++){
			probFailureDistribution.set(i, 0.0);
		}
		frameMap.setProbFailureDistribution(probFailureDistribution);

		//Generazione probabilità di fallimento critica iniziale stimata (tutte pari a 0)
		for(int i = 0; i<NFrames; i++){
			probCriticalFailureDistribution.set(i, 0.0);
		}
		frameMap.setProbCriticalFailureDistribution(probCriticalFailureDistribution);

		//Generazione probabilità di selezione vera (variazione della probabilità di selezione stimata)
		trueProbSelectionDistribution = TrueProbSelectionGenerator.generateNewProbDistribution(frameMap, variation);
		frameMap.setTrueProbSelectionDistribution(trueProbSelectionDistribution);

		return frameMap;
	}
	
	public static FrameMap generateDistributionsForSecondStrategy(ApplicationFactory appFactory, String frameMapFilePath, Double variation){

		//Carico la frame map con le f_vere già calcolate preliminarmente
		FrameMap frameMap = appFactory.makeFrameMap(frameMapFilePath);

		//Carico le distribuzioni di probabilità attuali, che saranno modificate
		ArrayList<Double> probSelectionDistribution = frameMap.getProbSelectionDistribution();
		ArrayList<Double> probFailureDistribution = frameMap.getProbFailureDistribution();
		ArrayList<Double> probCriticalFailureDistribution = frameMap.getProbCriticalFailureDistribution();
		ArrayList<Double> trueProbSelectionDistribution = frameMap.getTrueProbSelectionDistribution();

		int NFrames = frameMap.size();

		//Generazione probabilità di selezione iniziale stimata (casuale)
		for(int i = 0; i<NFrames; i++){
			probSelectionDistribution.set(i, RandomUtils.nextDouble(0,1.0));
		}
		DoubleUtils.normalize(probSelectionDistribution);
		frameMap.setProbSelectionDistribution(probSelectionDistribution);

		//Generazione probabilità di fallimento iniziale stimata, sia normale che critica, pari a |classi invalide|/|classi|
		for(int i = 0; i<NFrames; i++){
			Frame frame = frameMap.readByKey(i);
			Double nClasses = new Double(frame.getParamList().size());
			
			Double nInvalidClasses = 0.0;
			for(Param p : frame.getParamList()){
				if(!frameMap.getApplicationSpecifics().getOracle().isParamValid(p)){
					nInvalidClasses += 1.0;
				}
			}
			
			Double invalidClassesProportion = 0.0;
			if(nClasses != 0.0){
				invalidClassesProportion = nInvalidClasses/nClasses;
			}
			
			probFailureDistribution.set(i, invalidClassesProportion);
			probCriticalFailureDistribution.set(i, invalidClassesProportion);
		}
		frameMap.setProbFailureDistribution(probFailureDistribution);
		frameMap.setProbCriticalFailureDistribution(probCriticalFailureDistribution);

		//Generazione probabilità di selezione vera (variazione della probabilità di selezione stimata)
		trueProbSelectionDistribution = TrueProbSelectionGenerator.generateNewProbDistribution(frameMap, variation);
		frameMap.setTrueProbSelectionDistribution(trueProbSelectionDistribution);

		return frameMap;
	}

}
