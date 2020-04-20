package it.alessandrochillemi.tesi.frameutils.discourse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.io.ByteStreams;

import it.alessandrochillemi.tesi.APIRequest;
import it.alessandrochillemi.tesi.frameutils.HTTPMethod;
import it.alessandrochillemi.tesi.frameutils.Param;
import it.alessandrochillemi.tesi.frameutils.ResourceType;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public enum DiscourseResourceType implements ResourceType{
	CATEGORY_ID,
	POST_ID,
	TOPIC_ID,
	TOPIC_SLUG,
	USERNAME1,
	USER_ID1,
	USERNAME2,
	USER_ID2,
	USERNAME_LIST,
	TAG,
	TAG_GROUP_ID,
	GROUP,
	GROUP_ID,
	UPLOAD_AVATAR_ID,
	NO_RESOURCE;

	private static String categoryIDValue;
	private static String topicIDValue;
	private static String topicSlugValue;
	private static String userID1Value;
	private static String username1Value;
	private static String userID2Value;
	private static String username2Value;
	private static String usernameListValue;
	private static String tagGroupIDValue;
	private static String tagValue;
	private static String uploadAvatarIDValue;
	private static String groupIDValue;
	private static String groupValue;
	private static String postIDValue;


	public String generatePreConditionValue(String baseURL, String apiUsername, String apiKey, boolean forceNewPreConditions) {
		String value = null;
		//Se devo forzare nuove precondizioni, metto i valori di tutte le variabili a null
		if(forceNewPreConditions){
			categoryIDValue = null;
			topicIDValue = null;
			topicSlugValue = null;
			userID1Value = null;
			username1Value = null;
			userID2Value = null;
			username2Value = null;
			usernameListValue = null;
			tagGroupIDValue = null;
			tagValue = null;
			uploadAvatarIDValue = null;
			groupIDValue = null;
			groupValue = null;
			postIDValue = null;
		}
		if(this != null){
			switch(this){
			case CATEGORY_ID:
				if(categoryIDValue == null){
					generateCategoryDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
				}
				value = categoryIDValue;
				break;
			case GROUP:
				if(groupValue == null){
					generateGroupDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
				}
				value = groupValue;
				break;
			case GROUP_ID:
				if(groupIDValue == null){
					generateGroupDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
				}
				value = groupIDValue;
				break;
			case NO_RESOURCE:
				value = null;
				break;
			case POST_ID:
				if(postIDValue == null){
					//Generate pre-conditions required for creating a new post
					if(topicIDValue == null){
						//Generate pre-conditions required for creating a new topic
						if(categoryIDValue == null){
							generateCategoryDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
						}
						generateTopicDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
					}
					generatePostDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
				}
				value = postIDValue;
				break;
			case TAG:
				if(tagValue == null){
					generateTagDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
				}
				value = tagValue;
				break;
			case TAG_GROUP_ID:
				if(tagGroupIDValue == null){
					generateTagDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
				}
				value = tagGroupIDValue;
				break;
			case TOPIC_ID:
				if(topicIDValue == null){
					//Generate pre-conditions required for creating a new topic
					if(categoryIDValue == null){
						generateCategoryDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
					}
					generateTopicDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
				}
				value = topicIDValue;
				break;
			case TOPIC_SLUG:
				if(topicSlugValue == null){
					//Generate pre-conditions required for creating a new topic
					if(categoryIDValue == null){
						generateCategoryDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
					}
					generateTopicDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
				}
				value = topicSlugValue;
				break;
			case UPLOAD_AVATAR_ID:
				if(uploadAvatarIDValue == null){
					//Generate pre-conditions required for uploading an avatar
					if(userID1Value == null){
						generateUsersDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
					}
					generateUploadAvatarIDDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
				}
				value = uploadAvatarIDValue;
				break;
			case USERNAME1:
				if(username1Value == null){
					generateUsersDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
				}
				value = username1Value;
				break;
			case USERNAME2:
				if(username2Value == null){
					generateUsersDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
				}
				value = username2Value;
				break;
			case USERNAME_LIST:
				if(usernameListValue == null){
					generateUsersDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
				}
				value = usernameListValue;
				break;
			case USER_ID1:
				if(userID1Value == null){
					generateUsersDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
				}
				value = userID1Value;
				break;
			case USER_ID2:
				if(userID2Value == null){
					generateUsersDiscoursePreConditionValues(baseURL,apiUsername,apiKey);
				}
				value = userID2Value;
				break;
			default:
				break;

			}
		}
		return value;
	}

	private void generateCategoryDiscoursePreConditionValues(String baseURL, String apiUsername, String apiKey){				
		//Create new category
		HTTPMethod method = HTTPMethod.POST;
		String endpoint = "/categories.json";

		ArrayList<Param> paramList = new ArrayList<Param>();
		Param p1 = new Param("name", DiscourseTypeParam.STRING, Param.Position.BODY, DiscourseEquivalenceClass.STR_VALID, DiscourseResourceType.NO_RESOURCE,true);
		paramList.add(p1);
		Param p2 = new Param("color", DiscourseTypeParam.COLOR, Param.Position.BODY, DiscourseEquivalenceClass.COL_VALID, DiscourseResourceType.NO_RESOURCE,true);
		paramList.add(p2);
		Param p3 = new Param("text_color", DiscourseTypeParam.COLOR, Param.Position.BODY, DiscourseEquivalenceClass.COL_VALID, DiscourseResourceType.NO_RESOURCE,true);
		paramList.add(p3);

		APIRequest apiRequest = new APIRequest(method,endpoint,paramList);
		apiRequest.setBaseURL(baseURL);
		apiRequest.setApiUsername(apiUsername);
		apiRequest.setApiKey(apiKey);

		Response response = apiRequest.sendRequest();

		//Il valore è impostato a null di default; se l'API è andata a buon fine, viene sovrascritto con il valore giusto
		categoryIDValue = null;
		if(response != null){
			String stringResponseBody = null;
			try {
				stringResponseBody = response.body().string();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			response.close();

			JSONObject jsonResponseBody = null;
			try {
				jsonResponseBody = new JSONObject(stringResponseBody);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if(jsonResponseBody != null){
				try{
					categoryIDValue = jsonResponseBody.getJSONObject("category").get("id").toString();
				} catch (JSONException e){
					categoryIDValue = null;
				}
			}
		}
	}

	private void generateTopicDiscoursePreConditionValues(String baseURL, String apiUsername, String apiKey){
		//Se categoryIDValue (necessario per creare un nuovo topic) è ancora null a questo punto, significa che la creazione di categoryIDValue è fallita, quindi rinuncio
		//a creare un nuovo topic.
		if(categoryIDValue == null){
			topicIDValue = null;
			topicSlugValue = null;
			return;
		}

		//Create new topic
		HTTPMethod method = HTTPMethod.POST;
		String endpoint = "/posts.json";

		String raw = UUID.randomUUID().toString();

		//Set parameters
		ArrayList<Param> paramList = new ArrayList<Param>();
		Param p1 = new Param("title", DiscourseTypeParam.STRING, Param.Position.BODY, DiscourseEquivalenceClass.STR_VALID, DiscourseResourceType.NO_RESOURCE,true);
		paramList.add(p1);
		Param p2 = new Param("raw", DiscourseTypeParam.STRING, Param.Position.BODY, DiscourseEquivalenceClass.STR_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p2.setValue(raw+" "+raw);
		paramList.add(p2);
		Param p3 = new Param("category", DiscourseTypeParam.NUMBER, Param.Position.BODY, DiscourseEquivalenceClass.NUM_VALID, DiscourseResourceType.CATEGORY_ID,true);
		p3.setValue(categoryIDValue);
		paramList.add(p3);
		Param p4 = new Param("created_at", DiscourseTypeParam.DATE, Param.Position.BODY, DiscourseEquivalenceClass.DATE_VALID, DiscourseResourceType.NO_RESOURCE,true);
		paramList.add(p4);

		//Create an API request
		APIRequest apiRequest = new APIRequest(method,endpoint,paramList);
		apiRequest.setBaseURL(baseURL);
		apiRequest.setApiUsername(apiUsername);
		apiRequest.setApiKey(apiKey);

		//Send the request
		Response response = apiRequest.sendRequest();

		//I valori sono impostati a null di default; se l'API è andata a buon fine, vengono sovrascritti con i valori giusti
		topicIDValue = null;
		topicSlugValue = null;
		if(response != null){
			String stringResponseBody = null;
			try {
				stringResponseBody = response.body().string();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			response.close();

			JSONObject jsonResponseBody = null;
			try {
				jsonResponseBody = new JSONObject(stringResponseBody);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if(jsonResponseBody != null){
				try{
					topicIDValue = jsonResponseBody.get("topic_id").toString();
					topicSlugValue = jsonResponseBody.get("topic_slug").toString();
				} catch (JSONException e){
					topicIDValue = null;
					topicSlugValue = null;
				}
			}
		}
	}

	private void generateUsersDiscoursePreConditionValues(String baseURL, String apiUsername, String apiKey){		
		//Create user1
		HTTPMethod method = HTTPMethod.POST;
		String endpoint = "/users";

		String username1 = RandomStringUtils.randomAlphanumeric(10,16);

		//Set params
		ArrayList<Param> paramList = new ArrayList<Param>();
		Param p1 = new Param("name", DiscourseTypeParam.STRING, Param.Position.BODY, DiscourseEquivalenceClass.STR_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p1.setValue(username1);
		paramList.add(p1);
		Param p2 = new Param("email", DiscourseTypeParam.STRING, Param.Position.BODY, DiscourseEquivalenceClass.STR_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p2.setValue(username1+"@unina.it");
		paramList.add(p2);
		Param p3 = new Param("password", DiscourseTypeParam.STRING, Param.Position.BODY, DiscourseEquivalenceClass.STR_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p3.setValue(UUID.randomUUID().toString());
		paramList.add(p3);
		Param p4 = new Param("username", DiscourseTypeParam.STRING, Param.Position.BODY, DiscourseEquivalenceClass.STR_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p4.setValue(username1);
		paramList.add(p4);
		Param p5 = new Param("active", DiscourseTypeParam.BOOLEAN, Param.Position.BODY, DiscourseEquivalenceClass.BOOLEAN_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p5.setValue("true");
		paramList.add(p5);
		Param p6 = new Param("approved", DiscourseTypeParam.BOOLEAN, Param.Position.BODY, DiscourseEquivalenceClass.BOOLEAN_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p6.setValue("true");
		paramList.add(p6);

		//Create an API Request
		APIRequest apiRequest = new APIRequest(method,endpoint,paramList);
		apiRequest.setBaseURL(baseURL);
		apiRequest.setApiUsername(apiUsername);
		apiRequest.setApiKey(apiKey);

		//Send the request
		Response response = apiRequest.sendRequest();

		//I valori sono impostati a null di default; se l'API è andata a buon fine, vengono sovrascritti con i valori giusti
		userID1Value = null;
		username1Value = null;
		if(response != null){
			String stringResponseBody = null;
			try {
				stringResponseBody = response.body().string();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			response.close();

			JSONObject jsonResponseBody = null;
			try {
				jsonResponseBody = new JSONObject(stringResponseBody);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if(jsonResponseBody != null){
				try{
					userID1Value = jsonResponseBody.get("user_id").toString();
					username1Value = username1;
				} catch (JSONException e){
					userID1Value = null;
					username1Value = null;
				}
			}
		}

		//Create user2
		String username2 = RandomStringUtils.randomAlphanumeric(10,16);

		//Set params
		paramList = new ArrayList<Param>();
		p1 = new Param("name", DiscourseTypeParam.STRING, Param.Position.BODY, DiscourseEquivalenceClass.STR_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p1.setValue(username2);
		paramList.add(p1);
		p2 = new Param("email", DiscourseTypeParam.STRING, Param.Position.BODY, DiscourseEquivalenceClass.STR_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p2.setValue(username2+"@unina.it");
		paramList.add(p2);
		p3 = new Param("password", DiscourseTypeParam.STRING, Param.Position.BODY, DiscourseEquivalenceClass.STR_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p3.setValue(UUID.randomUUID().toString());
		paramList.add(p3);
		p4 = new Param("username", DiscourseTypeParam.STRING, Param.Position.BODY, DiscourseEquivalenceClass.STR_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p4.setValue(username2);
		paramList.add(p4);
		p5 = new Param("active", DiscourseTypeParam.BOOLEAN, Param.Position.BODY, DiscourseEquivalenceClass.BOOLEAN_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p5.setValue("true");
		paramList.add(p5);
		p6 = new Param("approved", DiscourseTypeParam.BOOLEAN, Param.Position.BODY, DiscourseEquivalenceClass.BOOLEAN_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p6.setValue("true");
		paramList.add(p6);

		//Create an API Request
		apiRequest = new APIRequest(method,endpoint,paramList);
		apiRequest.setBaseURL(baseURL);
		apiRequest.setApiUsername(apiUsername);
		apiRequest.setApiKey(apiKey);

		//Send the request
		response = apiRequest.sendRequest();

		//I valori sono impostati a null di default; se l'API è andata a buon fine, vengono sovrascritti con i valori giusti
		userID2Value = null;
		username2Value = null;
		if(response != null){
			String stringResponseBody = null;
			try {
				stringResponseBody = response.body().string();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			response.close();

			JSONObject jsonResponseBody = null;
			try {
				jsonResponseBody = new JSONObject(stringResponseBody);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if(jsonResponseBody != null){
				try{
					userID2Value = jsonResponseBody.get("user_id").toString();
					username2Value = username2;
				} catch(JSONException e){
					userID2Value = null;
					username2Value = null;
				}
			}
		}

		if((username1Value != null) && (username2Value != null)){
			usernameListValue = username1+","+username2;
		}
		else{
			usernameListValue = null;
		}
	}

	private void generateTagDiscoursePreConditionValues(String baseURL, String apiUsername, String apiKey){
		//Enable tagging
		HTTPMethod method = HTTPMethod.PUT;
		String endpoint = "/admin/site_settings/tagging_enabled";

		//Set params
		ArrayList<Param> paramList = new ArrayList<Param>();
		Param p1 = new Param("tagging_enabled", DiscourseTypeParam.BOOLEAN, Param.Position.BODY, DiscourseEquivalenceClass.BOOLEAN_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p1.setValue("true");
		paramList.add(p1);

		//Create an API Request
		APIRequest apiRequest = new APIRequest(method,endpoint,paramList);
		apiRequest.setBaseURL(baseURL);
		apiRequest.setApiUsername(apiUsername);
		apiRequest.setApiKey(apiKey);

		//Send the request
		Response response1 = apiRequest.sendRequest();

		//Create tag group
		method = HTTPMethod.POST;
		endpoint = "/tag_groups.json";

		String tagGroupName = RandomStringUtils.randomAlphanumeric(5,11);
		String tag1 = RandomStringUtils.randomAlphanumeric(4, 11);
		String tag2 = RandomStringUtils.randomAlphanumeric(4, 11);
		String tagList = tag1+","+tag2;

		//Set params
		paramList = new ArrayList<Param>();
		p1 = new Param("name", DiscourseTypeParam.STRING, Param.Position.BODY, DiscourseEquivalenceClass.STR_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p1.setValue(tagGroupName);
		paramList.add(p1);
		Param p2 = new Param("tag_names[]", DiscourseTypeParam.LIST, Param.Position.BODY, DiscourseEquivalenceClass.LIST_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p2.setValue(tagList);
		paramList.add(p2);

		//Create an API Request
		apiRequest = new APIRequest(method,endpoint,paramList);
		apiRequest.setBaseURL(baseURL);
		apiRequest.setApiUsername(apiUsername);
		apiRequest.setApiKey(apiKey);

		//Send the request
		Response response2 = apiRequest.sendRequest();

		//I valori sono impostati a null di default; se l'API è andata a buon fine, vengono sovrascritti con i valori giusti
		tagGroupIDValue = null;
		tagValue = null;
		if((response1 != null) && (response2 != null)){
			String stringResponseBody = null;
			try {
				stringResponseBody = response2.body().string();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			response2.close();

			JSONObject jsonResponseBody = null;
			try {
				jsonResponseBody = new JSONObject(stringResponseBody);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if(jsonResponseBody != null){
				try{
					tagGroupIDValue = jsonResponseBody.getJSONObject("tag_group").get("id").toString();
					tagValue = tag1;
				} catch(JSONException e){
					tagGroupIDValue = null;
					tagValue = null;
				}
			}
		}
	}

	private void generateUploadAvatarIDDiscoursePreConditionValues(String baseURL, String apiUsername, String apiKey){
		//Se userID1Value (necessario per fare l'upload di un avatar) è ancora null a questo punto, significa che la creazione di userID1Value è fallita, quindi rinuncio
		//a fare l'upload di un avatar.
		if(userID1Value == null){
			uploadAvatarIDValue = null;
			return;
		}

		String endpoint = "/uploads.json";

		InputStream url = getClass().getResourceAsStream("/mario_rossi.jpeg");
		byte[] avatarBytes = null;
		try {
			avatarBytes = ByteStreams.toByteArray(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Build complete URL, adding api_key and api_username
		HttpUrl.Builder completeURLBuilder = HttpUrl.parse(baseURL+endpoint).newBuilder()
				.addQueryParameter("api_key", apiKey)
				.addQueryParameter("api_username", apiUsername);

		HttpUrl completeURL = completeURLBuilder.build();

		OkHttpClient client = new OkHttpClient();

		//Costruisco il body della richiesta HTTP
		RequestBody requestBody = null;
		MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("user_id", userID1Value)
				.addFormDataPart("type", "avatar")
				.addFormDataPart("files[]", "mario_rossi.jpeg",RequestBody.create(MediaType.parse("image/jpeg"), avatarBytes))
				.addFormDataPart("synchronous", "true");

		requestBody = requestBodyBuilder.build();

		Request.Builder requestBuilder = new Request.Builder()
				.url(completeURL)
				.post(requestBody);

		Request request = requestBuilder
				.addHeader("cache-control", "no-cache")
				.build();

		Response response = null;
		try {
			response = client.newCall(request).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//I valori sono impostati a null di default; se l'API è andata a buon fine, vengono sovrascritti con i valori giusti
		uploadAvatarIDValue = null;
		if(response != null){
			String stringResponseBody = null;
			try {
				stringResponseBody = response.body().string();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			response.close();

			JSONObject jsonResponseBody = null;
			try {
				jsonResponseBody = new JSONObject(stringResponseBody);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if(jsonResponseBody != null){
				try{
					uploadAvatarIDValue = jsonResponseBody.get("id").toString();
				} catch(JSONException e){
					uploadAvatarIDValue = null;
				}
			}
		}
	}

	private void generateGroupDiscoursePreConditionValues(String baseURL, String apiUsername, String apiKey){		
		//Create new group
		HTTPMethod method = HTTPMethod.POST;
		String endpoint = "/admin/groups";
		String group_name = RandomStringUtils.randomAlphanumeric(5,16);

		//Set params
		ArrayList<Param> paramList = new ArrayList<Param>();
		Param p1 = new Param("group[name]", DiscourseTypeParam.STRING, Param.Position.BODY, DiscourseEquivalenceClass.STR_VALID, DiscourseResourceType.NO_RESOURCE,true);
		p1.setValue(group_name);
		paramList.add(p1);

		//Create an API Request
		APIRequest apiRequest = new APIRequest(method,endpoint,paramList);
		apiRequest.setBaseURL(baseURL);
		apiRequest.setApiUsername(apiUsername);
		apiRequest.setApiKey(apiKey);

		//Send the request
		Response response = apiRequest.sendRequest();

		//I valori sono impostati a null di default; se l'API è andata a buon fine, vengono sovrascritti con i valori giusti
		groupIDValue = null;
		groupValue = null;
		if(response != null){
			String stringResponseBody = null;
			try {
				stringResponseBody = response.body().string();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			response.close();

			JSONObject jsonResponseBody = null;
			try {
				jsonResponseBody = new JSONObject(stringResponseBody);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if(jsonResponseBody != null){
				try{
					groupIDValue = jsonResponseBody.getJSONObject("basic_group").get("id").toString();
					groupValue = group_name;
				} catch(JSONException e){
					groupIDValue = null;
					groupValue = null;
				}
			}
		}
	}

	private void generatePostDiscoursePreConditionValues(String baseURL, String apiUsername, String apiKey){		
		//Se topicIDValue (necessario per creare un nuovo post) è ancora null a questo punto, significa che la creazione di topicIDValue è fallita, quindi rinuncio
		//a creare un nuovo post.
		if(topicIDValue == null){
			postIDValue = null;
			return;
		}

		//Create new post
		HTTPMethod method = HTTPMethod.POST;
		String endpoint = "/posts.json";

		//Set params
		ArrayList<Param> paramList = new ArrayList<Param>();
		Param p1 = new Param("topic_id", DiscourseTypeParam.NUMBER, Param.Position.BODY, DiscourseEquivalenceClass.NUM_VALID, DiscourseResourceType.TOPIC_ID,true);
		p1.setValue(topicIDValue);
		paramList.add(p1);
		Param p2 = new Param("raw", DiscourseTypeParam.STRING, Param.Position.BODY, DiscourseEquivalenceClass.STR_VALID, DiscourseResourceType.NO_RESOURCE,true);
		paramList.add(p2);
		Param p4 = new Param("created_at", DiscourseTypeParam.DATE, Param.Position.BODY, DiscourseEquivalenceClass.DATE_VALID, DiscourseResourceType.NO_RESOURCE,true);
		paramList.add(p4);

		//Create an API Request
		APIRequest apiRequest = new APIRequest(method,endpoint,paramList);
		apiRequest.setBaseURL(baseURL);
		apiRequest.setApiUsername(apiUsername);
		apiRequest.setApiKey(apiKey);

		//Send the request
		Response response = apiRequest.sendRequest();

		//I valori sono impostati a null di default; se l'API è andata a buon fine, vengono sovrascritti con i valori giusti
		postIDValue = null;
		if(response != null){
			String stringResponseBody = null;
			try {
				stringResponseBody = response.body().string();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			response.close();

			JSONObject jsonResponseBody = null;
			try {
				jsonResponseBody = new JSONObject(stringResponseBody);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if(jsonResponseBody != null){
				try{
					postIDValue = jsonResponseBody.get("id").toString();
				} catch(JSONException e){
					postIDValue = null;
				}
			}
		}
	}

}
