package it.alessandrochillemi.tesi.frameutils;

import java.util.ArrayList;

import org.apache.commons.csv.CSVRecord;

public abstract class ApplicationFactory {
	
	//FrameMap factory methods
	public abstract FrameMap makeFrameMap();
	public abstract FrameMap makeFrameMap(String path);
	public abstract FrameMap makeFrameMap(String apiDescriptionsCSVFilePath, Double probSelection, Double probFailure, Double probCriticalFailure, Double trueProbSelection, Double trueProbFailure, Double trueProbCriticalFailure);
	
	//ResponseLog factory methods
	public abstract ResponseLog makeResponseLog();
	public abstract ResponseLog makeResponseLog(String frameID, Integer responseCode, String responseMessage, long responseTime, ArrayList<Param> paramList);
	public abstract ResponseLog makeResponseLog(CSVRecord record);
	
	//ResponseLogList factory methods
	public abstract ResponseLogList makeResponseLogList();
	public abstract ResponseLogList makeResponseLogList(String path);

}
