package it.alessandrochillemi.tesi.frameutils;

import java.util.ArrayList;

public interface Oracle{
	
	//Determina se una risposta è un fallimento o meno in base alla sua validità (presenza o meno di parametri con classi di equivalenza non valide) e al response code
	public boolean isFailure(ArrayList<Param> paramList, int responseCode);
	
	//Determina se una risposta è un fallimento critico o meno in base alla sua validità (presenza o meno di parametri con classi di equivalenza non valide) e al response code
	public boolean isCriticalFailure(ArrayList<Param> paramList, int responseCode);
	
	//Determina se un parametro è valido o meno
	public boolean isParamValid(Param p);

}
