package main;

import java.util.ArrayList;

import dataStructure.TestFrame;

public class Approaches {
	public double[] operationalTesting(ArrayList<TestFrame> tfa, int num){		
		double[] takenProb = new double[tfa.size()];
		int executedTC = 0;
		int failedTC = 0;

		takenProb[0] = tfa.get(0).getOccurrenceProb();

		for(int i=1; i<tfa.size(); i++){
			takenProb[i] =  tfa.get(i).getOccurrenceProb()+takenProb[i-1];
		}

		double rand = 0;
		int k=0;
		boolean outcome;

		for(int i=0; i<num; i++){
			rand = Math.random();
			k=0;

			while(k < takenProb.length-1 && (rand >= takenProb[k])){
				k++;
			}

			outcome = tfa.get(k).extractAndExecuteTestCase();
			executedTC++;
			if(outcome)
				failedTC++;
		}


		double rel = 1-failedTC/(double)executedTC;
		double[] ret = new double[2];

		ret[0] = rel;
		ret[1] = failedTC;

		return ret;

	}

	public double[] weigthedOperationalTesting(ArrayList<TestFrame> tfa, int num){		
		double[] takenProb = new double[tfa.size()];
		double[] newtakenProb = new double[tfa.size()];

		int failedTC = 0;

		takenProb[0] = tfa.get(0).getOccurrenceProb()*tfa.get(0).getFailureProb();

		for(int i=1; i<tfa.size(); i++){
			takenProb[i] =  tfa.get(i).getOccurrenceProb()*tfa.get(i).getFailureProb()+takenProb[i-1];
		}

		double norm = 0;
		for(int i=0; i<tfa.size(); i++){
			newtakenProb[i] =  tfa.get(i).getOccurrenceProb()*tfa.get(i).getFailureProb()/takenProb[takenProb.length-1];
			norm = norm + newtakenProb[i];
		}
		
		double rand = 0;
		double failProb = 0.0;
		int k=0;
		boolean outcome;

		for(int i=0; i<num; i++){
			rand = Math.random()*takenProb[takenProb.length-1];
			k=0;

			while(k < takenProb.length-1 && (rand >= takenProb[k])){
				k++;
			}

			outcome = tfa.get(k).extractAndExecuteTestCase();
			if(outcome){
				failedTC++;
				failProb += tfa.get(k).getOccurrenceProb()/((double)num * newtakenProb[k]);
			}

		}

		double rel = 1-failProb;
		double[] ret = new double[2];

		ret[0] = rel;
		ret[1] = failedTC;

		return ret;

	}
}
