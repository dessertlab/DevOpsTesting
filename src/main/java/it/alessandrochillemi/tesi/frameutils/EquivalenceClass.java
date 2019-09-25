package it.alessandrochillemi.tesi.frameutils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public interface EquivalenceClass {
	public String generateValue(ArrayList<String> validValues);
	public boolean isValid();
	public boolean isInvalid();
	public boolean isEmpty();
	
	//Genera la lista di tutte le possibili combinazioni di classi di equivalenza tra i gruppi selezionati;
	//i gruppi di classi di equivalenza sono selezionati automaticamente in base al tipo specificato in ingresso
	//per ogni parametro; viene usata la libreria Google Guava in combinazione con Java 8, prendendo spunto dalla risposta
	//seguente indirizzo: https://stackoverflow.com/a/37490796/5863657
	public static List<List<String>> cartesianProduct(TypeParam typeParam1, TypeParam typeParam2, TypeParam typeParam3, TypeParam typeParam4, TypeParam typeParam5, TypeParam typeParam6){
		List<String[]> elements = new LinkedList<String[]>();

		if(typeParam1 != null){
			elements.add(typeParam1.getClasses());
			if(typeParam2 != null){
				elements.add(typeParam2.getClasses());
				if(typeParam3 != null){
					elements.add(typeParam3.getClasses());
					if(typeParam4 != null){
						elements.add(typeParam4.getClasses());
						if(typeParam5 != null){
							elements.add(typeParam5.getClasses());
							if(typeParam6 != null){
								elements.add(typeParam6.getClasses());
							}
						}
					}
				}
			}
		}

		List<ImmutableList<String>> immutableElements = new LinkedList<>();
		elements.forEach(array -> {
			immutableElements.add(ImmutableList.copyOf(array));
		});

		List<List<String>> cartesianProduct = Lists.cartesianProduct(immutableElements);

		return cartesianProduct;	
	}
}
