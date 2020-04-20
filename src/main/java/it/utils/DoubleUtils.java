package it.alessandrochillemi.tesi.utils;

import java.util.ArrayList;
import java.util.Random;

public class DoubleUtils {

	//Ottiene un Double casuale tra min e max
	public static Double randomInRange(Random random, Double min, Double max) {
		Double range = max - min;
		Double scaled = random.nextDouble() * range;
		Double shifted = scaled + min;
		return shifted; // == (rand.nextDouble() * (max-min)) + min;
	}


	//Ritorna un valore "true" se nell'array passato come argomento è presente almeno un valore negativo
	public static boolean areThereNegatives(ArrayList<Double> doubleList){
		boolean negative = false;
		int i = 0;
		while(!negative && i<doubleList.size()){
			if(doubleList.get(i)<0.0){
				negative = true;
			}
			i++;
		}

		return negative;
	}

	//Normalizza un array di Double
	public static void normalize(ArrayList<Double> doubleList){
		Double sum = 0.0;
		for(Double d : doubleList){
			sum += d;
		}
		for(int i = 0; i<doubleList.size(); i++){
			doubleList.set(i, doubleList.get(i)/sum);
		}
	}
	
	//Pone a 0 tutti i valori negativi
	public static void rectify(ArrayList<Double> doubleList){
		for(int i = 0; i<doubleList.size(); i++){
			if(doubleList.get(i)<0.0){
				doubleList.set(i, 0.0);
			}
		}
	}

}
