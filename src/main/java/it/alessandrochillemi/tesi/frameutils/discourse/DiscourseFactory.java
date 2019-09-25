package it.alessandrochillemi.tesi.frameutils.discourse;

import java.util.ArrayList;

import org.apache.commons.csv.CSVRecord;

import it.alessandrochillemi.tesi.frameutils.ApplicationFactory;
import it.alessandrochillemi.tesi.frameutils.FrameMap;
import it.alessandrochillemi.tesi.frameutils.Param;
import it.alessandrochillemi.tesi.frameutils.ResponseLog;
import it.alessandrochillemi.tesi.frameutils.ResponseLogList;

public class DiscourseFactory extends ApplicationFactory {

	//FrameMap factory methods
	@Override
	public FrameMap makeFrameMap() {
		return new FrameMap(new DiscourseSpecifics());
	}

	@Override
	public FrameMap makeFrameMap(String path) {
		return new FrameMap(path,new DiscourseSpecifics());
	}

	@Override
	public FrameMap makeFrameMap(String apiDescriptionsCSVFilePath, Double probSelection, Double probFailure,Double probCriticalFailure, Double trueProbSelection, Double trueProbFailure,Double trueProbCriticalFailure) {
		return new FrameMap(new DiscourseSpecifics(),apiDescriptionsCSVFilePath,probSelection,probFailure,probCriticalFailure,trueProbSelection,trueProbFailure,trueProbCriticalFailure);
	}
	
	//ResponseLog factory methods
	@Override
	public ResponseLog makeResponseLog() {
		return new ResponseLog(new DiscourseSpecifics());
	}

	@Override
	public ResponseLog makeResponseLog(String frameID, Integer responseCode, String responseMessage, long responseTime, ArrayList<Param> paramList) {
		return new ResponseLog(new DiscourseSpecifics(),frameID,responseCode,responseMessage,responseTime,paramList);
	}

	@Override
	public ResponseLog makeResponseLog(CSVRecord record) {
		return new ResponseLog(record,new DiscourseSpecifics());
	}

	//ResponseLogList factory methods
	@Override
	public ResponseLogList makeResponseLogList() {
		return new ResponseLogList(new DiscourseSpecifics());
	}

	@Override
	public ResponseLogList makeResponseLogList(String path) {
		return new ResponseLogList(path,new DiscourseSpecifics());
	}

}
