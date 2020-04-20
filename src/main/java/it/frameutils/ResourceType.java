package it.alessandrochillemi.tesi.frameutils;

public interface ResourceType {
	
	//Generate the value for this resource type that will serve as a pre-condition; e.g., if the resource type is CATEGORY_ID, it will generate a value that is an ID
	//for a category.
	public String generatePreConditionValue(String baseURL, String apiUsername, String apiKey, boolean forceNewPreConditions);
}
