package it.alessandrochillemi.tesi.frameutils;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.lang3.Validate;

public class Param implements Serializable{
	
	public enum Position{BODY,PATH,QUERY};
	
	protected String keyParam;													//Nome del parametro
	protected TypeParam typeParam;												//Tipo del parametro
	protected Position position;												//Posizione del parametro nella richiesta HTTP
	protected EquivalenceClass classParam;										//Classe di equivalenza del parametro
	protected ResourceType resourceType;										//Tipo della risorsa
	protected boolean required;													//Parametro obbligatorio o meno
	protected ArrayList<String> validValues;									//Eventuale elenco di valori validi per enumerativi
	protected String value;														//Valore del parametro
	
	private static final long serialVersionUID = 1L;
	
	public Param(String keyParam, TypeParam typeParam, Position position, EquivalenceClass classParam, ResourceType resourceType, boolean required, ArrayList<String> validValues){
		//Check that classParam is not null
		Validate.notNull(classParam, "classParam can't be null!");
		
		this.keyParam = keyParam;
		this.typeParam = typeParam;
		this.position = position;
		this.classParam = classParam;
		this.resourceType = resourceType;
		this.required = required;
		this.validValues = validValues;
		this.generateValue();
	}
	
	public Param(String keyParam, TypeParam typeParam, Position position, EquivalenceClass classParam, ResourceType resourceType, boolean required){
		//Check that classParam is not null
		Validate.notNull(classParam, "classParam can't be null!");
		
		this.keyParam = keyParam;
		this.typeParam = typeParam;
		this.position = position;
		this.classParam = classParam;
		this.resourceType = resourceType;
		this.required = required;
		this.validValues = new ArrayList<String>();
		this.generateValue();
	}
	
	public Param(String keyParam, TypeParam typeParam, Position position, ResourceType resourceType, boolean required, ArrayList<String> validValues){
		this.keyParam = keyParam;
		this.typeParam = typeParam;
		this.position = position;
		this.resourceType = resourceType;
		this.required = required;
		this.validValues = validValues;
		this.generateValue();
	}
	
	public Param(String keyParam, TypeParam typeParam, Position position, ResourceType resourceType, boolean required){
		this.keyParam = keyParam;
		this.typeParam = typeParam;
		this.position = position;
		this.resourceType = resourceType;
		this.required = required;
		this.validValues = new ArrayList<String>();
		this.generateValue();
	}
	
	public Param(Param param){
		this.keyParam = param.getKeyParam();
		this.typeParam = param.getTypeParam();
		this.position = param.getPosition();
		this.classParam = param.getClassParam();
		this.resourceType = param.getResourceType();
		this.required = param.isRequired();
		this.validValues = param.getValidValues();
		this.value = param.getValue();
	}

	public String getKeyParam() {
		return keyParam;
	}

	public void setKeyParam(String keyParam) {
		this.keyParam = keyParam;
	}

	public TypeParam getTypeParam(){
		return typeParam;
	}

	public void setTypeParam(TypeParam typeParam) {
		this.typeParam = typeParam;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public EquivalenceClass getClassParam() {
		return classParam;
	}

	public void setClassParam(EquivalenceClass classParam) {
		this.classParam = classParam;
	}
	
	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public ArrayList<String> getValidValues() {
		return validValues;
	}

	public void setValidValues(ArrayList<String> validValues) {
		this.validValues = validValues;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}
	
	public void generateValue(){
		if(this.classParam != null){
			this.value = this.classParam.generateValue(validValues);
		}
	}
	
	public void generateValueWithPreConditions(String baseURL, String apiUsername, String apiKey, boolean forceNewPreConditions) {
		if(this.classParam.isValid()){
			this.value = resourceType.generatePreConditionValue(baseURL,apiUsername,apiKey,forceNewPreConditions);
		}
		if(this.value == null){
			this.generateValue();
		}
	}
	
	public void print(){
		System.out.print("\nKey: " + keyParam);
		System.out.print("\nPosition:  " + position);
		System.out.print("\nClass:  " + classParam);
		System.out.print("\nResource Type:  " + resourceType);
		System.out.print("\nValue:  " + value);
	}
	
}
