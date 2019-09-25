package it.alessandrochillemi.tesi.frameutils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import it.alessandrochillemi.tesi.frameutils.Param.Position;

public class ResponseLog implements Serializable{

	private ApplicationSpecifics applicationSpecifics;		//Dettagli relativi all'applicazione a cui questo response log si riferisce
	private String frameID;									//ID del Frame a cui si riferisce questa risposta
	private Integer responseCode;							//Codice di risposta della richiesta HTTP
	private String responseMessage;							//Messaggio di risposta della richiesta HTTP
	private long responseTime;								//Tempo di risposta in ms (istante appena dopo la ricezione - istante appena prima dell'invio)
	private ArrayList<Param> paramList;						//Lista di parametri usati nella richiesta

	private static final long serialVersionUID = 7679179561832569179L;

	public ResponseLog(ApplicationSpecifics applicationSpecifics){
		this.applicationSpecifics = applicationSpecifics;
		this.paramList = new ArrayList<Param>();
	}

	public ResponseLog(ApplicationSpecifics applicationSpecifics, String frameID, Integer responseCode, String responseMessage, long responseTime, ArrayList<Param> paramList) {
		this.applicationSpecifics = applicationSpecifics;
		this.frameID = frameID;
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.responseTime = responseTime;
		this.paramList = paramList;
	}

	public ResponseLog(CSVRecord record, ApplicationSpecifics applicationSpecifics){
		this.applicationSpecifics = applicationSpecifics;
		this.paramList = new ArrayList<Param>();
		readFromCSVRow(record);
	}

	public ApplicationSpecifics getApplicationSpecifics() {
		return applicationSpecifics;
	}

	public void setApplicationSpecifics(ApplicationSpecifics applicationSpecifics) {
		this.applicationSpecifics = applicationSpecifics;
	}

	public String getFrameID() {
		return frameID;
	}
	
	public void setFrameID(String frameID) {
		this.frameID = frameID;
	}
	
	public Integer getResponseCode() {
		return responseCode;
	}
	
	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}
	
	public String getResponseMessage() {
		return responseMessage;
	}
	
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	public ArrayList<Param> getParamList() {
		return paramList;
	}
	public void setParamList(ArrayList<Param> paramList) {
		this.paramList = paramList;
	}
	
	public boolean isFailure(){
		return this.applicationSpecifics.getOracle().isFailure(paramList, responseCode);
	}
	
	public boolean isCriticalFailure(){
		return this.applicationSpecifics.getOracle().isCriticalFailure(paramList, responseCode);
	}

	public void print(){
		System.out.println("FRAME ID: " + frameID);
		System.out.println("RESPONSE CODE: " + responseCode);
		System.out.println("RESPONSE MESSAGE: " + responseMessage);
		System.out.println("RESPONSE TIME: " + responseTime);
		System.out.println("PARAMETERS: ");
		for(int i=0; i<paramList.size(); i++){
			System.out.print("\nPARAMETER " + (i+1) + ":");
			paramList.get(i).print();
		}
	}

	/*	Carica i campi del ResponseLog da una riga di un file CSV; questo metodo dipende dall'implementazione del ResponseLog (ad esempio DiscourseResponseLog, ecc.),
	 *	perché i campi del CSV vanno letti usando l'EquivalenceClass e il TypeParam appropriati.
	 */ 
	public void readFromCSVRow(CSVRecord record){
		//Read Frame ID, Response Code, Response Message and Response Time
		String frameID = record.get("FRAME_ID");
		int responseCode = Integer.parseInt(record.get("RESPONSE_CODE"));
		String responseMessage = record.get("RESPONSE_MESSAGE");
		long responseTime = Long.parseLong(record.get("RESPONSE_TIME"));

		//Create a list of Params from the values of the row
		ArrayList<Param> paramList = new ArrayList<Param>();

		//Read the features for each of the 6 parameters on a row
		for(int i = 1; i<=6; i++){
			String keyString = record.get("P"+i+"_KEY");
			String typeString = record.get("P"+i+"_TYPE");
			String eqClassString = record.get("P"+i+"_CLASS");
			String positionString = record.get("P"+i+"_POSITION");
			String resourceTypeString = record.get("P"+i+"_RESOURCE_TYPE");
			String isRequiredString = record.get("P"+i+"_IS_REQUIRED");
			String validValuesString = record.get("P"+i+"_VALID_VALUES");

			//If P_KEY!=null and P_KEY!="/", create a new parameter and add it to the list; otherwise, it means that there are no more parameters
			if(keyString != null && !keyString.equals("/")){
				TypeParam typeParam = applicationSpecifics.getTypeParamEnum(typeString);
				EquivalenceClass eqClass = applicationSpecifics.getEquivalenceClassEnum(eqClassString);
				Position position = EnumUtils.getEnumIgnoreCase(Position.class, positionString);
				ResourceType resourceType = applicationSpecifics.getResourceTypeEnum(resourceTypeString);
				boolean isRequired = Boolean.parseBoolean(isRequiredString);
				ArrayList<String> validValues = new ArrayList<String>();
				if(!validValuesString.equals("/")){
					validValues.addAll(Arrays.asList(validValuesString.split(",")));
				}
				Param p = new Param(keyString,typeParam,position,eqClass,resourceType,isRequired,validValues);
				paramList.add(p);
			}
		}

		//Load ResponseLog fields
		this.frameID = frameID;
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.responseTime = responseTime;
		this.paramList = paramList;
	}

	//Scrive i campi del ResponseLog su una riga di un file CSV
	public void writeToCSVRow(CSVPrinter csvPrinter){
		String frameID = getFrameID();
		int responseCode = getResponseCode();
		String responseMessage = getResponseMessage();
		long responseTime = getResponseTime();

		ArrayList<String> paramKeys = new ArrayList<String>();
		ArrayList<String> paramTypes = new ArrayList<String>();
		ArrayList<String> paramClasses = new ArrayList<String>();
		ArrayList<String> paramPositions = new ArrayList<String>();
		ArrayList<String> paramResourceTypes = new ArrayList<String>();
		ArrayList<String> paramIsRequireds = new ArrayList<String>();
		ArrayList<String> paramValidValues = new ArrayList<String>();

		for(int i=0;i<6;i++){
			if(i<getParamList().size()){
				paramKeys.add(getParamList().get(i).getKeyParam());
				paramTypes.add(getParamList().get(i).getTypeParam().toString());
				paramClasses.add(getParamList().get(i).getClassParam().toString());
				paramPositions.add(getParamList().get(i).getPosition().toString());
				paramResourceTypes.add(getParamList().get(i).getResourceType().toString());
				paramIsRequireds.add(String.valueOf(getParamList().get(i).isRequired()));
				if(getParamList().get(i).getValidValues().size()>0){
					paramValidValues.add(StringUtils.join(getParamList().get(i).getValidValues(),","));
				}
				else{
					paramValidValues.add("/");
				}
			}
			else{
				paramKeys.add("/");
				paramTypes.add("/");
				paramClasses.add("/");
				paramPositions.add("/");
				paramResourceTypes.add("/");
				paramIsRequireds.add("/");
				paramValidValues.add("/");
			}
		}

		try {
			csvPrinter.printRecord(frameID, responseCode,responseMessage,responseTime,
					paramKeys.get(0), paramTypes.get(0), paramClasses.get(0), paramPositions.get(0), paramResourceTypes.get(0), paramIsRequireds.get(0), paramValidValues.get(0),
					paramKeys.get(1), paramTypes.get(1), paramClasses.get(1), paramPositions.get(1), paramResourceTypes.get(1), paramIsRequireds.get(1), paramValidValues.get(1),
					paramKeys.get(2), paramTypes.get(2), paramClasses.get(2), paramPositions.get(2), paramResourceTypes.get(2), paramIsRequireds.get(2), paramValidValues.get(2),
					paramKeys.get(3), paramTypes.get(3), paramClasses.get(3), paramPositions.get(3), paramResourceTypes.get(3), paramIsRequireds.get(3), paramValidValues.get(3),
					paramKeys.get(4), paramTypes.get(4), paramClasses.get(4), paramPositions.get(4), paramResourceTypes.get(4), paramIsRequireds.get(4), paramValidValues.get(4),
					paramKeys.get(5), paramTypes.get(5), paramClasses.get(5), paramPositions.get(5), paramResourceTypes.get(5), paramIsRequireds.get(5), paramValidValues.get(5)
					);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
