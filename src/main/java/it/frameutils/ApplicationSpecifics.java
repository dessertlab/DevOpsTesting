package it.alessandrochillemi.tesi.frameutils;

import java.util.ArrayList;

public abstract class ApplicationSpecifics{
	
	protected Oracle oracle;
	
	public Oracle getOracle() {
		return oracle;
	}

	public abstract ResourceType getResourceTypeEnum(String value);
	public abstract TypeParam getTypeParamEnum(String value);
	public abstract EquivalenceClass getEquivalenceClassEnum(String value);
	
	//Generate a list of Frames from a list of class combinations (useful when creating a FrameMap), given the types of the parameters;
	//'paramList' is the List of Params of the API that the resulting list of Frames refers to.
	public abstract ArrayList<Frame> generateFrames(HTTPMethod method, String endpoint, ArrayList<Param> paramList, Double probSelection, Double probFailure, Double probCriticalFailure, Double trueProbSelection, Double trueProbFailure, Double trueProbCriticalFailure);

}
