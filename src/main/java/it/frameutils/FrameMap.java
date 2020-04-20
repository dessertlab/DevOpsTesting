package it.alessandrochillemi.tesi.frameutils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.EnumUtils;

import it.alessandrochillemi.tesi.frameutils.Param.Position;

public class FrameMap{
	
	private enum header{
		METHOD,ENDPOINT,
		P1_KEY,P1_TYPE,P1_CLASS,P1_POSITION,P1_RESOURCE_TYPE,P1_IS_REQUIRED,P1_VALID_VALUES,
		P2_KEY,P2_TYPE,P2_CLASS,P2_POSITION,P2_RESOURCE_TYPE,P2_IS_REQUIRED,P2_VALID_VALUES,
		P3_KEY,P3_TYPE,P3_CLASS,P3_POSITION,P3_RESOURCE_TYPE,P3_IS_REQUIRED,P3_VALID_VALUES,
		P4_KEY,P4_TYPE,P4_CLASS,P4_POSITION,P4_RESOURCE_TYPE,P4_IS_REQUIRED,P4_VALID_VALUES,
		P5_KEY,P5_TYPE,P5_CLASS,P5_POSITION,P5_RESOURCE_TYPE,P5_IS_REQUIRED,P5_VALID_VALUES,
		P6_KEY,P6_TYPE,P6_CLASS,P6_POSITION,P6_RESOURCE_TYPE,P6_IS_REQUIRED,P6_VALID_VALUES,
		PROB_SELECTION,PROB_FAILURE,PROB_CRITICAL_FAILURE,TRUE_PROB_SELECTION,TRUE_PROB_FAILURE,TRUE_PROB_CRITICAL_FAILURE;
	};
	
	private ApplicationSpecifics applicationSpecifics;
	private TreeMap<Integer, Frame> map;
	
	public FrameMap(ApplicationSpecifics applicationSpecifics){
		this.applicationSpecifics = applicationSpecifics;
		this.map = new TreeMap<Integer, Frame>();
	}
	
	public FrameMap(String path, ApplicationSpecifics applicationSpecifics){
		this.applicationSpecifics = applicationSpecifics;
		this.map = new TreeMap<Integer, Frame>();
		readFromCSVFile(path);
	}
	
	public FrameMap(ApplicationSpecifics applicationSpecifics, String apiDescriptionsCSVFilePath, Double probSelection, Double probFailure, Double probCriticalFailure, Double trueProbSelection, Double trueProbFailure, Double trueProbCriticalFailure){
		this.applicationSpecifics = applicationSpecifics;
		this.map = new TreeMap<Integer, Frame>();
		this.append(generateFromCSV(apiDescriptionsCSVFilePath,probSelection,probFailure,probCriticalFailure,trueProbSelection,trueProbFailure,trueProbCriticalFailure));
	}
	
	public ApplicationSpecifics getApplicationSpecifics() {
		return applicationSpecifics;
	}

	public int size(){
		return this.map.size();
	}
	
	public Frame readByKey(Integer key){
		return this.map.get(key);
	}
	
	public void put(Integer key, Frame frame){
		this.map.put(key, frame);
	}
	
	public Iterator<Map.Entry<Integer, Frame>> iterator(){
		return this.map.entrySet().iterator();
	}
	
	public void append(ArrayList<Frame> list){
		for(Frame frame : list){
			this.map.put(this.map.isEmpty() ? 0 : this.map.lastKey()+1, frame);
		}
	}
	
	//Get the probability selection for every entry in the FrameMap; the order is preserved, because the underlying Map is a TreeMap.
	public ArrayList<Double> getProbSelectionDistribution(){
		ArrayList<Double> ret = new ArrayList<Double>();

		for(Map.Entry<Integer, Frame> entry : this.map.entrySet()){
			ret.add(entry.getValue().getProbSelection());
		}
		return ret;
	}

	//Set the probability selection for every entry in the FrameMap
	public void setProbSelectionDistribution(ArrayList<Double> probSelectionDistribution){
		Iterator<Map.Entry<Integer, Frame>> iter = this.map.entrySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			Entry<Integer, Frame> entry = iter.next();
			entry.getValue().setProbSelection(probSelectionDistribution.get(i));
			i++;
		}
	}
	
	//Get the probability failure for every entry in the FrameMap; the order is preserved, because the underlying Map is a TreeMap.
	public ArrayList<Double> getProbFailureDistribution(){
		ArrayList<Double> ret = new ArrayList<Double>();

		for(Map.Entry<Integer, Frame> entry : this.map.entrySet()){
			ret.add(entry.getValue().getProbFailure());
		}
		return ret;
	}
	
	//Set the probability failure for every entry in the FrameMap
	public void setProbFailureDistribution(ArrayList<Double> probFailureDistribution){
		Iterator<Map.Entry<Integer, Frame>> iter = this.map.entrySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			Entry<Integer, Frame> entry = iter.next();
			entry.getValue().setProbFailure(probFailureDistribution.get(i));
			i++;
		}
	}
	
	//Get the probability of critical failure for every entry in the FrameMap; the order is preserved, because the underlying Map is a TreeMap.
	public ArrayList<Double> getProbCriticalFailureDistribution(){
		ArrayList<Double> ret = new ArrayList<Double>();

		for(Map.Entry<Integer, Frame> entry : this.map.entrySet()){
			ret.add(entry.getValue().getProbCriticalFailure());
		}
		return ret;
	}

	//Set the probability of critical failure for every entry in the FrameMap
	public void setProbCriticalFailureDistribution(ArrayList<Double> probCriticalFailureDistribution){
		Iterator<Map.Entry<Integer, Frame>> iter = this.map.entrySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			Entry<Integer, Frame> entry = iter.next();
			entry.getValue().setProbCriticalFailure(probCriticalFailureDistribution.get(i));
			i++;
		}
	}

	//Get the true probability selection for every entry in the FrameMap; the order is preserved, because the underlying Map is a TreeMap.
	public ArrayList<Double> getTrueProbSelectionDistribution(){
		ArrayList<Double> ret = new ArrayList<Double>();

		for(Map.Entry<Integer, Frame> entry : this.map.entrySet()){
			ret.add(entry.getValue().getTrueProbSelection());
		}
		return ret;
	}

	//Set the true probability selection for every entry in the FrameMap
	public void setTrueProbSelectionDistribution(ArrayList<Double> trueProbSelectionDistribution){
		Iterator<Map.Entry<Integer, Frame>> iter = this.map.entrySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			Entry<Integer, Frame> entry = iter.next();
			entry.getValue().setTrueProbSelection(trueProbSelectionDistribution.get(i));
			i++;
		}
	}

	//Get the true probability failure for every entry in the FrameMap; the order is preserved, because the underlying Map is a TreeMap.
	public ArrayList<Double> getTrueProbFailureDistribution(){
		ArrayList<Double> ret = new ArrayList<Double>();

		for(Map.Entry<Integer, Frame> entry : this.map.entrySet()){
			ret.add(entry.getValue().getTrueProbFailure());
		}
		return ret;
	}

	//Set the true probability failure for every entry in the FrameMap
	public void setTrueProbFailureDistribution(ArrayList<Double> trueProbFailureDistribution){
		Iterator<Map.Entry<Integer, Frame>> iter = this.map.entrySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			Entry<Integer, Frame> entry = iter.next();
			entry.getValue().setTrueProbFailure(trueProbFailureDistribution.get(i));
			i++;
		}
	}
	
	//Get the true probability for critical failure for every entry in the FrameMap; the order is preserved, because the underlying Map is a TreeMap.
	public ArrayList<Double> getTrueProbCriticalFailureDistribution(){
		ArrayList<Double> ret = new ArrayList<Double>();

		for(Map.Entry<Integer, Frame> entry : this.map.entrySet()){
			ret.add(entry.getValue().getTrueProbCriticalFailure());
		}
		return ret;
	}

	//Set the true probability for critical failure for every entry in the FrameMap
	public void setTrueProbCriticalFailureDistribution(ArrayList<Double> trueProbCriticalFailureDistribution){
		Iterator<Map.Entry<Integer, Frame>> iter = this.map.entrySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			Entry<Integer, Frame> entry = iter.next();
			entry.getValue().setTrueProbCriticalFailure(trueProbCriticalFailureDistribution.get(i));
			i++;
		}
	}
	

	//Get all the frames that have the specified endpoint
	public ArrayList<Frame> getFramesByEndpoint(HTTPMethod method, String endpoint){
		ArrayList<Frame> ret = new ArrayList<Frame>();

		for(Map.Entry<Integer, Frame> entry : this.map.entrySet()){
			if(entry.getValue().getMethod().equals(method) && entry.getValue().getEndpoint().equals(endpoint)){
				ret.add(entry.getValue());
			}
		}
		return ret;
	}

	//Update all the frames that have the specified endpoint to the specified frame list;
	//if the specified list has a different number of elements than the ones already present, a message is shown and the operation is not performed.
	public void updateFramesByEndpoint(HTTPMethod method, String endpoint, ArrayList<Frame> framesList){
		ArrayList<Frame> oldFrameList = getFramesByEndpoint(method,endpoint);

		if(framesList.size() != oldFrameList.size()){
			System.out.println("The specified frame list has a different number of elements than the existing one!");
		}
		else{
			Iterator<Map.Entry<Integer, Frame>> iter = this.map.entrySet().iterator();
			int i = 0;
			while (iter.hasNext()) {
				Entry<Integer, Frame> entry = iter.next();
				if(entry.getValue().getMethod().equals(method) && entry.getValue().getEndpoint().equals(endpoint)){
					entry.setValue(framesList.get(i));
					i++;
				}
			}
		}

	}

	//	public void changeClass(){
	//	Iterator<Map.Entry<Integer, FrameBean>> iter = map.entrySet().iterator();
	//	FrameBean newFrameBean = null;
	//	FrameBean oldFrameBean = null;
	//	while (iter.hasNext()) {
	//	    Entry<Integer, FrameBean> entry = iter.next();
	//	    oldFrameBean = entry.getValue();
	//	    newFrameBean = new FrameBean(oldFrameBean);
	//	    ArrayList<DiscourseParam> discourseParamList = new ArrayList<DiscourseParam>();
	//	    for(Param p : oldFrameBean.getParamList()){
	//	    	DiscourseParam discourseParam = new DiscourseParam(p);
	//	    	discourseParamList.add(discourseParam);
	//	    }
	//	    newFrameBean.setParamList(discourseParamList);
	//	    entry.setValue(newFrameBean);
	//	}
	//}

	public void deleteFrames(HTTPMethod method, String endpoint){
		Iterator<Map.Entry<Integer, Frame>> iter = this.map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, Frame> entry = iter.next();
			if(entry.getValue().getMethod().equals(method) && entry.getValue().getEndpoint().equals(endpoint)){
				iter.remove();
			}
		}
	}
	
	public void readFromCSVFile(String path){
		if(Files.exists(Paths.get(path))){
			Reader in;
			try {
				//Read the CSV file
				in = new FileReader(path);
				Iterable<CSVRecord> records = CSVFormat.RFC4180.withDelimiter(';').withFirstRecordAsHeader().parse(in);
				for (CSVRecord record : records) {
					//Create a new frame from the record
					Frame frame = new Frame(record,applicationSpecifics);

					//Add the frame to the map
					this.map.put(this.map.isEmpty() ? 0 : this.map.lastKey()+1, frame);

				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			System.out.println("File does not exist!");
		}
	}
	
	//Generate a list of Frames from a CSV containing the API descriptions; probSelection and probFailure are constant initial values assigned to every Frame;
	//they can be manually modified later.
	private ArrayList<Frame> generateFromCSV(String apiDescriptionsCSVFilePath, Double probSelection, Double probFailure, Double probCriticalFailure, Double trueProbSelection, Double trueProbFailure, Double trueProbCriticalFailure){
		if(Files.exists(Paths.get(apiDescriptionsCSVFilePath))) {
			ArrayList<Frame> ret = new ArrayList<Frame>();
			Reader in;
			try {
				//Read the CSV file
				in = new FileReader(apiDescriptionsCSVFilePath);
				Iterable<CSVRecord> records = CSVFormat.RFC4180.withDelimiter(';').withFirstRecordAsHeader().parse(in);

				//Iterate over rows
				for (CSVRecord record : records) {
					//Create a list of DiscourseParam from the values of the row
					ArrayList<Param> paramList = new ArrayList<Param>();

					//Read API method and endpoint
					HTTPMethod method = HTTPMethod.valueOf(record.get("METHOD"));
					String endpoint = record.get("ENDPOINT");

					//Read the parameter's features for each of the 6 parameters (at most) on a row
					for(int i = 1; i<=6; i++){
						String keyString = record.get("P"+i+"_KEY");
						String typeString = record.get("P"+i+"_TYPE");
						String positionString = record.get("P"+i+"_POSITION");
						String resourceTypeString = record.get("P"+i+"_RESOURCE_TYPE");
						String isRequiredString = record.get("P"+i+"_IS_REQUIRED");
						String validValuesString = record.get("P"+i+"_VALID_VALUES");

						//If P_KEY!=null and P_KEY!="/", create a new parameter and add it to the list; otherwise, it means that there are no more parameters
						if(keyString != null && !keyString.equals("/")){
							TypeParam typeParam = applicationSpecifics.getTypeParamEnum(typeString);
							Position position = EnumUtils.getEnumIgnoreCase(Position.class, positionString);
							ResourceType resourceType = applicationSpecifics.getResourceTypeEnum(resourceTypeString);
							boolean isRequired = Boolean.parseBoolean(isRequiredString);
							ArrayList<String> validValues = new ArrayList<String>();
							if(!validValuesString.equals("/")){
								validValues.addAll(Arrays.asList(validValuesString.split(",")));
							}

							Param p = new Param(keyString,typeParam,position,resourceType,isRequired,validValues);
							paramList.add(p);
						}
					}
					//Get the list of Frames and add it to the return array
					ArrayList<Frame> frameList = applicationSpecifics.generateFrames(method, endpoint, paramList, probSelection, probFailure, probCriticalFailure, trueProbSelection, trueProbFailure, trueProbCriticalFailure);
					ret.addAll(frameList);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ret;
		}
		else{
			System.out.println("File does not exist!");
			return null;
		}
	}
	
	public void writeToCSVFile(String path){
		BufferedWriter writer;
		try {
			writer = Files.newBufferedWriter(Paths.get(path));

			CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.RFC4180.withDelimiter(';').withHeader(header.class));
			
			Iterator<Map.Entry<Integer, Frame>> iter = this.map.entrySet().iterator();
			
			while (iter.hasNext()) {
				Entry<Integer, Frame> entry = iter.next();
				
				//Write every frame to a new row of the CSV file
				entry.getValue().writeToCSVRow(csvPrinter);
			}

			csvPrinter.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void print(){
		if(this.map != null){
			for(Entry<Integer, Frame> entry : this.map.entrySet()){
				System.out.print(entry.getKey() + " ");
				entry.getValue().print();
				System.out.print("\n");
			}
		}
	}
	
	//Print the entries with the specified endpoint only
	public void print(HTTPMethod method, String endpoint){
		if(this.map != null){
			for(Entry<Integer, Frame> entry : this.map.entrySet()){
				if(entry.getValue().getMethod().equals(method) && entry.getValue().getEndpoint().equals(endpoint)){
					System.out.print(entry.getKey() + " ");
					entry.getValue().print();
					System.out.print("\n");
				}
			}
		}
	}

}
