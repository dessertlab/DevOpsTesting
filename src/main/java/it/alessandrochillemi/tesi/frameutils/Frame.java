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

//Classe che modella i campi di un Frame che devono essere letti o scritti
public class Frame implements Serializable{
	
	private ApplicationSpecifics applicationSpecifics;									//Dettagli relativi all'applicazione a cui questo frame si riferisce
	private HTTPMethod method;															//Metodo della richiesta HTTP per usare l'API
	private String endpoint;															//Endpoint dell'API
	private ArrayList<Param> paramList;													//Lista di parametri
	private Double probSelection;														//Probabilità di selezione stimata del Frame
	private Double probFailure;															//Probabilità di fallimento stimata del Frame
	private Double probCriticalFailure;													//Probabilità di fallimento critica stimata del Frame
	private Double trueProbSelection;													//Probabilità di selezione reale del Frame
	private Double trueProbFailure;														//Probabilità di fallimento reale del Frame
	private Double trueProbCriticalFailure;												//Probabilità di fallimento critica reale del Frame
		
	private static final long serialVersionUID = 5259280897255194440L;
	
	public Frame(ApplicationSpecifics applicationSpecifics){
		this.applicationSpecifics = applicationSpecifics;
		this.paramList = new ArrayList<Param>();
	}
	
	public Frame(ApplicationSpecifics applicationSpecifics, HTTPMethod method, String endpoint, ArrayList<Param> paramList, Double probSelection,Double probFailure, Double probCriticalFailure, Double trueProbSelection, Double trueProbFailure, Double trueProbCriticalFailure){
		this.applicationSpecifics = applicationSpecifics;
		this.method = method;
		this.endpoint = endpoint;
		this.paramList = paramList;
		this.probSelection = probSelection;
		this.probFailure = probFailure;
		this.probCriticalFailure = probCriticalFailure;
		this.trueProbSelection = trueProbSelection;
		this.trueProbFailure = trueProbFailure;
		this.trueProbCriticalFailure = trueProbCriticalFailure;
	}
	
	public Frame(Frame frame){
		this.applicationSpecifics = frame.getApplicationSpecifics();
		this.method = frame.getMethod();
		this.endpoint = frame.getEndpoint();
		this.paramList = frame.getParamList();
		this.probSelection = frame.getProbSelection();
		this.probFailure = frame.getProbFailure();
		this.trueProbSelection = frame.getTrueProbSelection();
		this.trueProbFailure = frame.getTrueProbFailure();
	}
	
	public Frame(CSVRecord record, ApplicationSpecifics applicationSpecifics){
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

	public HTTPMethod getMethod() {
		return method;
	}

	public void setMethod(HTTPMethod method) {
		this.method = method;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public ArrayList<Param> getParamList() {
		return paramList;
	}

	public void setParamList(ArrayList<Param> paramList) {
		this.paramList = paramList;
	}

	public Double getProbSelection() {
		return probSelection;
	}

	public void setProbSelection(Double probSelection) {
		this.probSelection = probSelection;
	}

	public Double getProbFailure() {
		return probFailure;
	}

	public void setProbFailure(Double probFailure) {
		this.probFailure = probFailure;
	}
	
	public Double getProbCriticalFailure() {
		return probCriticalFailure;
	}

	public void setProbCriticalFailure(Double probCriticalFailure) {
		this.probCriticalFailure = probCriticalFailure;
	}

	public Double getTrueProbSelection() {
		return trueProbSelection;
	}

	public void setTrueProbSelection(Double trueProbSelection) {
		this.trueProbSelection = trueProbSelection;
	}

	public Double getTrueProbFailure() {
		return trueProbFailure;
	}

	public void setTrueProbFailure(Double trueProbFailure) {
		this.trueProbFailure = trueProbFailure;
	}
	
	public Double getTrueProbCriticalFailure() {
		return trueProbCriticalFailure;
	}

	public void setTrueProbCriticalFailure(Double trueProbCriticalFailure) {
		this.trueProbCriticalFailure = trueProbCriticalFailure;
	}

	public void generateParamValuesWithPreConditions(String baseURL, String apiUsername, String apiKey, boolean forceNewPreConditions) {
		for(Param p : this.paramList){
			p.generateValueWithPreConditions(baseURL, apiUsername, apiKey, forceNewPreConditions);
		}
		
	}
	
	public void print() {
		System.out.println(this.method + " " + this.endpoint + ": ");
		for(int i = 0; i<paramList.size(); i++){
			System.out.print((i+1) + ": [");
			paramList.get(i).print();
			System.out.print("]; ");
		}
		//System.out.print("probSel: " + probSelection);
	}

	//	Carica i campi del Frame da una riga di un file CSV; questo metodo dipende dall'applicazione in uso (membro 'appSpecifics')
	public void readFromCSVRow(CSVRecord record){
		//Read API method and endpoint
		HTTPMethod method = HTTPMethod.valueOf(record.get("METHOD"));
		String endpoint = record.get("ENDPOINT");

		//Create a list of Param from the values of the row
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
				Position position = EnumUtils.getEnumIgnoreCase(Position.class, positionString);
				EquivalenceClass eqClass = applicationSpecifics.getEquivalenceClassEnum(eqClassString);
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
		//Read probSelection and probFailure
		Double probSelection = Double.valueOf(record.get("PROB_SELECTION"));
		Double probFailure = Double.valueOf(record.get("PROB_FAILURE"));
		Double probCriticalFailure = Double.valueOf(record.get("PROB_CRITICAL_FAILURE"));
		Double trueProbSelection = Double.valueOf(record.get("TRUE_PROB_SELECTION"));
		Double trueProbFailure = Double.valueOf(record.get("TRUE_PROB_FAILURE"));
		Double trueProbCriticalFailure = Double.valueOf(record.get("TRUE_PROB_CRITICAL_FAILURE"));

		//Load frame fields
		this.method = method;
		this.endpoint = endpoint;
		this.paramList = paramList;
		this.probSelection = probSelection;
		this.probFailure = probFailure;
		this.probCriticalFailure = probCriticalFailure;
		this.trueProbSelection = trueProbSelection;
		this.trueProbFailure = trueProbFailure;
		this.trueProbCriticalFailure = trueProbCriticalFailure;
	}
	
	//	Scrive i campi del Frame su una riga di un file CSV
	public void writeToCSVRow(CSVPrinter csvPrinter){
		String method = getMethod().toString();
		String endpoint = getEndpoint();

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
		
		String probSelection = getProbSelection().toString();
		String probFailure = getProbFailure().toString();
		String probCriticalFailure = getProbCriticalFailure().toString();
		String trueProbSelection = getTrueProbSelection().toString();
		String trueProbFailure = getTrueProbFailure().toString();
		String trueProbCriticalFailure = getTrueProbCriticalFailure().toString();

		try {
			csvPrinter.printRecord(method, endpoint, 
					paramKeys.get(0), paramTypes.get(0), paramClasses.get(0), paramPositions.get(0), paramResourceTypes.get(0), paramIsRequireds.get(0), paramValidValues.get(0),
					paramKeys.get(1), paramTypes.get(1), paramClasses.get(1), paramPositions.get(1), paramResourceTypes.get(1), paramIsRequireds.get(1), paramValidValues.get(1),
					paramKeys.get(2), paramTypes.get(2), paramClasses.get(2), paramPositions.get(2), paramResourceTypes.get(2), paramIsRequireds.get(2), paramValidValues.get(2),
					paramKeys.get(3), paramTypes.get(3), paramClasses.get(3), paramPositions.get(3), paramResourceTypes.get(3), paramIsRequireds.get(3), paramValidValues.get(3),
					paramKeys.get(4), paramTypes.get(4), paramClasses.get(4), paramPositions.get(4), paramResourceTypes.get(4), paramIsRequireds.get(4), paramValidValues.get(4),
					paramKeys.get(5), paramTypes.get(5), paramClasses.get(5), paramPositions.get(5), paramResourceTypes.get(5), paramIsRequireds.get(5), paramValidValues.get(5),
					probSelection,probFailure,probCriticalFailure,trueProbSelection,trueProbFailure,trueProbCriticalFailure);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
