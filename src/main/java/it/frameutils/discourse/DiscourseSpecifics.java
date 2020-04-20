package it.alessandrochillemi.tesi.frameutils.discourse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;

import it.alessandrochillemi.tesi.frameutils.ApplicationSpecifics;
import it.alessandrochillemi.tesi.frameutils.EquivalenceClass;
import it.alessandrochillemi.tesi.frameutils.Frame;
import it.alessandrochillemi.tesi.frameutils.HTTPMethod;
import it.alessandrochillemi.tesi.frameutils.Param;
import it.alessandrochillemi.tesi.frameutils.ResourceType;
import it.alessandrochillemi.tesi.frameutils.TypeParam;

public class DiscourseSpecifics extends ApplicationSpecifics{
	
	public DiscourseSpecifics(){
		this.oracle = new DiscourseOracle();
	}

	@Override
	public ResourceType getResourceTypeEnum(String value) {
		return EnumUtils.getEnumIgnoreCase(DiscourseResourceType.class, value);
	}

	@Override
	public TypeParam getTypeParamEnum(String value){
		return EnumUtils.getEnumIgnoreCase(DiscourseTypeParam.class, value);
	}

	@Override
	public EquivalenceClass getEquivalenceClassEnum(String value) {
		return EnumUtils.getEnumIgnoreCase(DiscourseEquivalenceClass.class, value);
	}

	//Generate a list of Frames from a list of class combinations (useful when creating a FrameMap), given the types of the parameters;
	//'paramList' is the List of Params of the API that the resulting list of Frames refers to.
	public ArrayList<Frame> generateFrames(HTTPMethod method, String endpoint, ArrayList<Param> paramList, Double probSelection, Double probFailure, Double probCriticalFailure, Double trueProbSelection, Double trueProbFailure, Double trueProbCriticalFailure) {
		ArrayList<Frame> framesList = new ArrayList<Frame>();

		ArrayList<TypeParam> types = new ArrayList<TypeParam>();
		for(int k = 0; k<6; k++){
			TypeParam t = null;
			if(k<paramList.size()){
				t = paramList.get(k).getTypeParam();
			}
			types.add(t);
		}

		List<List<String>> classesCombinations = EquivalenceClass.cartesianProduct(types.get(0), types.get(1), types.get(2), types.get(3), types.get(4), types.get(5));

		for(int i = 0; i<classesCombinations.size(); i++){
			Frame frame = new Frame(new DiscourseSpecifics());
			ArrayList<Param> frameParamList = new ArrayList<Param>();
			frame.setMethod(method);
			frame.setEndpoint(endpoint);
			frame.setProbSelection(probSelection);
			frame.setProbFailure(probFailure);
			frame.setProbCriticalFailure(probCriticalFailure);
			frame.setTrueProbSelection(trueProbSelection);
			frame.setTrueProbFailure(trueProbFailure);
			frame.setTrueProbCriticalFailure(trueProbCriticalFailure);
			for(int j = 0; j<paramList.size(); j++){
				Param p1 = new Param(paramList.get(j));
				p1.setClassParam(DiscourseEquivalenceClass.valueOf(classesCombinations.get(i).get(j)));
				frameParamList.add(p1);
			}
			frame.setParamList(frameParamList);
			framesList.add(frame);
		}

		return framesList;
	}

	
	
}
