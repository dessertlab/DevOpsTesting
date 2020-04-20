package it.alessandrochillemi.tesi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang3.RandomUtils;

import it.alessandrochillemi.tesi.frameutils.ApplicationFactory;
import it.alessandrochillemi.tesi.frameutils.Frame;
import it.alessandrochillemi.tesi.frameutils.FrameMap;
import it.alessandrochillemi.tesi.frameutils.Param;
import it.alessandrochillemi.tesi.frameutils.ResponseLogList;
import it.alessandrochillemi.tesi.frameutils.discourse.DiscourseFactory;
import it.alessandrochillemi.tesi.testingstrategies.FirstTestingStrategy;
import it.alessandrochillemi.tesi.testingstrategies.SecondTestingStrategy;
import it.alessandrochillemi.tesi.testingstrategies.TestingStrategy;
import it.alessandrochillemi.tesi.utils.DoubleUtils;
import it.alessandrochillemi.tesi.utils.TrueProbSelectionGenerator;

@SuppressWarnings("unused")
public class Test {
	//Percorso nel quale si trova il file con le variabili di ambiente
	public static String ENVIRONMENT_FILE_PATH = "/Users/alessandrochillemi/Desktop/Universita/Magistrale/Tesi/environment.properties";

	private static String frameMapFilePath;
	private static String baseURL;
	private static String apiUsername;
	private static String apiKey;

	private static void loadEnvironment(){

		//Carico le variabili d'ambiente (path della lista di testframe, api_key, api_username, ecc.)
		Properties environment = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(ENVIRONMENT_FILE_PATH);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			environment.load(is);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		//Leggo le variabili d'ambiente
		frameMapFilePath = environment.getProperty("frame_map_file_path");
		baseURL = environment.getProperty("base_url");
		apiUsername = environment.getProperty("api_username");
		apiKey = environment.getProperty("api_key");
	}

	public static FrameMap generateFramesForFirstStrategy(ApplicationFactory appFactory, String frameMapFilePath){

		//Carico la frame map con le f_vere già calcolate preliminarmente
		FrameMap frameMap = appFactory.makeFrameMap(frameMapFilePath);

		//Carico le distribuzioni di probabilità attuali, che saranno modificate
		ArrayList<Double> probSelectionDistribution = frameMap.getProbSelectionDistribution();
		ArrayList<Double> probFailureDistribution = frameMap.getProbFailureDistribution();
		ArrayList<Double> probCriticalFailureDistribution = frameMap.getProbCriticalFailureDistribution();
		ArrayList<Double> trueProbSelectionDistribution = frameMap.getTrueProbSelectionDistribution();
		
		//Carico la distribuzione di probabilità di fallimento per il calcolo della reliability vera
		ArrayList<Double> trueProbCriticalFailureDistribution = frameMap.getTrueProbCriticalFailureDistribution();

		TestingStrategy testingStrategy = new FirstTestingStrategy(frameMap);

		int NFrames = frameMap.size();
		System.out.println("Numero di frame: " + NFrames);

		Double failProb = 0.0;
		Double reliabilityForCriticalFailures = 0.0;
		Double trueReliabilityForCriticalFailures = 0.0;

		Locale currentLocale = Locale.ITALY;
		NumberFormat numberFormatter = NumberFormat.getNumberInstance(currentLocale);
		numberFormatter.setMinimumFractionDigits(16);

		//Generazione probabilità di selezione iniziale stimata (casuale)
		for(int i = 0; i<probSelectionDistribution.size(); i++){
			probSelectionDistribution.set(i, RandomUtils.nextDouble(0,1.0));
		}
		DoubleUtils.normalize(probSelectionDistribution);
		
//		//Generazione probabilità di selezione iniziale stimata (uniforme)
//		Double uniformValue = 1.0/(new Double(NFrames));
//		for(int i = 0; i<probSelectionDistribution.size(); i++){
//			probSelectionDistribution.set(i, uniformValue);
//		}
		
		frameMap.setProbSelectionDistribution(probSelectionDistribution);

		//Generazione probabilità di fallimento iniziale stimata (tutte pari a 0)
		for(int i = 0; i<probFailureDistribution.size(); i++){
			probFailureDistribution.set(i, 0.0);
		}
		frameMap.setProbFailureDistribution(probFailureDistribution);

		//Generazione probabilità di fallimento critica iniziale stimata (tutte pari a 0)
		for(int i = 0; i<probCriticalFailureDistribution.size(); i++){
			probCriticalFailureDistribution.set(i, 0.0);
		}
		frameMap.setProbCriticalFailureDistribution(probCriticalFailureDistribution);

		//Generazione probabilità di selezione vera (variazione della probabilità di selezione stimata)
		trueProbSelectionDistribution = TrueProbSelectionGenerator.generateNewProbDistribution(frameMap, 0.3);
		frameMap.setTrueProbSelectionDistribution(trueProbSelectionDistribution);

		//Calcolo reliability critica stimata (p(i)*f_critica_vera(i) perché non ho le f_stimate(i) e non voglio fare un ciclo di test)
		failProb = 0.0;
		for(int i = 0; i<NFrames; i++){
			failProb += probSelectionDistribution.get(i)*trueProbCriticalFailureDistribution.get(i);
		}	
		reliabilityForCriticalFailures = 1d - failProb;

		//Calcolo reliability critica vera
		trueReliabilityForCriticalFailures = testingStrategy.getTrueReliabilityForCriticalFailures();

		System.out.println("TRUE RELIABILITY FOR CRITICAL FAILURES: " + numberFormatter.format(trueReliabilityForCriticalFailures));
		System.out.println("ESTIMATED RELIABILITY FOR CRITICAL FAILURES: " + numberFormatter.format(reliabilityForCriticalFailures));
		System.out.println("RELIABILITY OFFSET: " + numberFormatter.format(Math.abs(reliabilityForCriticalFailures-trueReliabilityForCriticalFailures)));

		return frameMap;
	}

	public static FrameMap generateFramesForSecondStrategy(ApplicationFactory appFactory, String frameMapFilePath){

		//Carico la frame map con le f_vere già calcolate preliminarmente
		FrameMap frameMap = appFactory.makeFrameMap(frameMapFilePath);

		//Carico le distribuzioni di probabilità attuali, che saranno modificate
		ArrayList<Double> probSelectionDistribution = frameMap.getProbSelectionDistribution();
		ArrayList<Double> probFailureDistribution = frameMap.getProbFailureDistribution();
		ArrayList<Double> probCriticalFailureDistribution = frameMap.getProbCriticalFailureDistribution();
		ArrayList<Double> trueProbSelectionDistribution = frameMap.getTrueProbSelectionDistribution();
		ArrayList<Double> trueProbCriticalFailureDistribution = frameMap.getTrueProbCriticalFailureDistribution();

		TestingStrategy testingStrategy = new SecondTestingStrategy(frameMap);

		int NFrames = frameMap.size();
		System.out.println("Numero di frame: " + NFrames);

		Double failProb = 0.0;
		Double reliabilityForCriticalFailures = 0.0;
		Double trueReliabilityForCriticalFailures = 0.0;

		Locale currentLocale = Locale.ITALY;
		NumberFormat numberFormatter = NumberFormat.getNumberInstance(currentLocale);
		numberFormatter.setMinimumFractionDigits(16);

		//Generazione probabilità di selezione iniziale stimata (casuale)
		for(int i = 0; i<probSelectionDistribution.size(); i++){
			probSelectionDistribution.set(i, RandomUtils.nextDouble(0,1.0));
		}
		DoubleUtils.normalize(probSelectionDistribution);
		
//		//Generazione probabilità di selezione iniziale stimata (uniforme)
//		Double uniformValue = 1.0/(new Double(NFrames));
//		for(int i = 0; i<probSelectionDistribution.size(); i++){
//			probSelectionDistribution.set(i, uniformValue);
//		}
		
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
		trueProbSelectionDistribution = TrueProbSelectionGenerator.generateNewProbDistribution(frameMap, 0.3);
		frameMap.setTrueProbSelectionDistribution(trueProbSelectionDistribution);

		//Calcolo reliability critica stimata (p(i)*f_critica_vera(i))
		failProb = 0.0;
		for(int i = 0; i<NFrames; i++){
			failProb += probSelectionDistribution.get(i)*trueProbCriticalFailureDistribution.get(i);
		}	
		reliabilityForCriticalFailures = 1d - failProb;

		//Calcolo reliability critica vera
		trueReliabilityForCriticalFailures = testingStrategy.getTrueReliabilityForCriticalFailures();

		System.out.println("TRUE RELIABILITY FOR CRITICAL FAILURES: " + numberFormatter.format(trueReliabilityForCriticalFailures));
		System.out.println("ESTIMATED RELIABILITY FOR CRITICAL FAILURES: " + numberFormatter.format(reliabilityForCriticalFailures));
		System.out.println("RELIABILITY OFFSET: " + numberFormatter.format(Math.abs(reliabilityForCriticalFailures-trueReliabilityForCriticalFailures)));

		return frameMap;
	}

	public static void main(String[] args) {
		//Carico le variabili d'ambiente
		loadEnvironment();

		ApplicationFactory appFactory = new DiscourseFactory();
		
		//Metto i tag delle versioni in una lista
		List<String> versionsList = Arrays.asList("2.1.6", "2.1.7", "2.1.8", "2.2.0", "2.2.1");
		
		
		ArrayList<Integer> nFailuresOT = new ArrayList<Integer>();
		ArrayList<Integer> nFailuresWOT = new ArrayList<Integer>();
		
		System.out.println("\n+++++ TUTTI I FALLIMENTI +++++");
		
		for(int i = 0; i<versionsList.size(); i++){
			nFailuresOT = new ArrayList<Integer>();
			nFailuresWOT = new ArrayList<Integer>();
			
			for(int j = 1; j<=20; j++){
				String path = "/Users/alessandrochillemi/Desktop/Universita/Magistrale/Tesi/esperimenti_discourse/experiment1_repetition"+j+"/test_responses/test_response_log_list_cycle1_v"+versionsList.get(i)+".csv";
				ResponseLogList responseLogList = appFactory.makeResponseLogList(path);
				nFailuresOT.add(responseLogList.getTotalNumberOfFailures());
			}
			
			for(int j = 1; j<=20; j++){
				String path = "/Users/alessandrochillemi/Desktop/Universita/Magistrale/Tesi/esperimenti_discourse/experiment3_repetition"+j+"/test_responses/test_response_log_list_cycle1_v"+versionsList.get(i)+".csv";
				ResponseLogList responseLogList = appFactory.makeResponseLogList(path);
				nFailuresWOT.add(responseLogList.getTotalNumberOfFailures());
			}
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
		
		ArrayList<Integer> nCriticalFailuresOT = new ArrayList<Integer>();
		ArrayList<Integer> nCriticalFailuresWOT = new ArrayList<Integer>();
		
		System.out.println("\n+++++ FALLIMENTI CRITICI +++++");
		
		for(int i = 0; i<versionsList.size(); i++){
			nCriticalFailuresOT = new ArrayList<Integer>();
			nCriticalFailuresWOT = new ArrayList<Integer>();
			
			for(int j = 1; j<=20; j++){
				String path = "/Users/alessandrochillemi/Desktop/Universita/Magistrale/Tesi/esperimenti_discourse/experiment1_repetition"+j+"/test_responses/test_response_log_list_cycle1_v"+versionsList.get(i)+".csv";
				ResponseLogList responseLogList = appFactory.makeResponseLogList(path);
				nCriticalFailuresOT.add(responseLogList.getTotalNumberOfCriticalFailures());
			}
			
			for(int j = 1; j<=20; j++){
				String path = "/Users/alessandrochillemi/Desktop/Universita/Magistrale/Tesi/esperimenti_discourse/experiment2_repetition"+j+"/test_responses/test_response_log_list_cycle1_v"+versionsList.get(i)+".csv";
				ResponseLogList responseLogList = appFactory.makeResponseLogList(path);
				nCriticalFailuresWOT.add(responseLogList.getTotalNumberOfCriticalFailures());
			}
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
		
//		String path = "/Users/alessandrochillemi/Desktop/Universita/Magistrale/Tesi/esperimenti_discourse/experiment1_repetition1/test_responses/test_response_log_list_cycle1_v2.2.1.csv";

		
//		FrameMap frameMap = generateFramesForFirstStrategy(appFactory, path);
		
//		FrameMap frameMap = appFactory.makeFrameMap(path);
		
//		int NFrames = frameMap.size();
//		
//		ArrayList<Double> trueProbSelection = frameMap.getTrueProbSelectionDistribution();
//		ArrayList<Double> trueProbFailure = frameMap.getTrueProbFailureDistribution();
//		ArrayList<Double> trueProbCriticalFailure = frameMap.getTrueProbCriticalFailureDistribution();
//		
//		//Calcolo reliability vera (p_vera(i)*f_vera(i)) e conteggio f==1
//		Double failProb = 0.0;
//		int fCount = 0;
//		for(int i = 0; i<NFrames; i++){
//			failProb += trueProbSelection.get(i)*trueProbFailure.get(i);
//			if(trueProbFailure.get(i).equals(new Double(1))){
//				fCount++;
//			}
//		}	
//		Double trueReliability = 1d - failProb;
//		
//		//Calcolo reliability critica vera (p_vera(i)*f_critica_vera(i))
//		failProb = 0.0;
//		int fCriticalCount = 0;
//		for(int i = 0; i<NFrames; i++){
//			failProb += trueProbSelection.get(i)*trueProbCriticalFailure.get(i);
//			if(trueProbCriticalFailure.get(i).equals(new Double(1))){
//				fCriticalCount++;
//			}
//		}	
//		Double trueReliabilityForCriticalFailures = 1d - failProb;
//		
//		Locale currentLocale = Locale.ITALY;
//		NumberFormat numberFormatter = NumberFormat.getNumberInstance(currentLocale);
//		numberFormatter.setMinimumFractionDigits(16);
//		
//		System.out.println("|F_CRITICA_VERA == 1|: " + fCriticalCount);
//		System.out.println("RELIABILITY CRITICA VERA: " + numberFormatter.format(trueReliabilityForCriticalFailures));
//		System.out.println("|F_VERA == 1|: " + fCount);
//		System.out.println("RELIABILITY VERA: " + numberFormatter.format(trueReliability));
		
//		frameMap.writeToCSVFile(frameMapFilePath);
	}

}
