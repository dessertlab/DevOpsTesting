package it.alessandrochillemi.tesi;

import it.alessandrochillemi.tesi.frameutils.ApplicationFactory;
import it.alessandrochillemi.tesi.frameutils.Frame;
import it.alessandrochillemi.tesi.frameutils.FrameMap;
import it.alessandrochillemi.tesi.frameutils.Param;
import it.alessandrochillemi.tesi.frameutils.ResponseLog;
import it.alessandrochillemi.tesi.frameutils.ResponseLogList;
import it.alessandrochillemi.tesi.testingstrategies.TestingStrategy;
import okhttp3.Response;

//Esegue NREQUESTS scegliendo i frame secondo la distribuzione di probabilità di selezione vera
public class WorkloadGenerator {
	
	//Strategy design pattern
	private TestingStrategy testingStrategy;

	public WorkloadGenerator(TestingStrategy testingStrategy){
		this.testingStrategy = testingStrategy;
	}

	public void setTestSelectionStrategy(TestingStrategy testingStrategy) {
		this.testingStrategy = testingStrategy;
	}

	public ResponseLogList generateRequests(String baseURL, String apiUsername, String apiKey, FrameMap frameMap, int NRequests, ApplicationFactory applicationFactory){
    	
    	ResponseLogList responseLogList = applicationFactory.makeResponseLogList();
    	
    	//Calcolo la nuova probabilità di selezione
    	testingStrategy.computeNewProbSelectionDistribution(false);
    	
    	for(int i = 0; i<NRequests; i++){
//    		System.out.println("\nRichiesta " + (i+1) + "...");

    		//Scelgo un frame secondo l'algoritmo selezionato (pattern Strategy)
    		int selectedFrame = testingStrategy.selectFrame(false);	
//    		System.out.println("Frame selezionato: " + selectedFrame);
        	Frame frame = frameMap.readByKey(selectedFrame);

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
    		ResponseLog responseLog = applicationFactory.makeResponseLog(Integer.toString(selectedFrame, 10), responseCode, responseMessage, responseTime, apiRequest.getParamList());

    		responseLogList.add(responseLog);
    		
    		if((i+1)%1000 == 0){
    			System.out.println((i+1) + " richieste eseguite!");
    		}
    	}
    	
		return responseLogList;
	}

}
