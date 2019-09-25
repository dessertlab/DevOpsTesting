package it.alessandrochillemi.tesi.frameutils.discourse;

import java.util.ArrayList;

import it.alessandrochillemi.tesi.frameutils.Oracle;
import it.alessandrochillemi.tesi.frameutils.Param;

public class DiscourseOracle implements Oracle {

	@Override
	public boolean isFailure(ArrayList<Param> paramList, int responseCode){
		boolean ret = false;
		
		//Determino la "validità" della lista dei parametri: se ne è stato utilizzato almeno un non valido, considero la lista non valida
		boolean valid = true;
		int j = 0;
		//Scorro i parametri finché non ne trovo almeno uno non valido o termina la lista
		while(valid && j<paramList.size()){
			valid = this.isParamValid(paramList.get(j));
			j++;
		}
		
		//Se il codice di risposta è maggiore o uguale di 500, si tratta sicuramente di un errore
		if(responseCode >= 500){
			ret = true;
		}
		//Se il codice di risposta è compreso tra 400 e 500, si tratta di un fallimento solo se tutti gli input sono validi
		else if(responseCode>= 400){
			//Se tutti i valori sono validi, si tratta di un fallimento
			if(valid){
				ret = true;
			}
		}
		//Se il codice di risposta è compreso tra 200 e 300, si tratta di un fallimento solo se almeno un input è invalido
		else if((responseCode >= 200) && (responseCode < 300)){
			//Se almeno un valore è invalido, si tratta di un fallimento
			if(!valid){
				ret = true;
			}
		}
		return ret;
	}

	@Override
	public boolean isCriticalFailure(ArrayList<Param> paramList, int responseCode) {
		boolean ret = false;
		
		//In questo caso conta il fallimento è critico se il response code è maggiore o uguale di 500, a prescindere dalla validità della richiesta
		if(responseCode >= 500){
			ret = true;
		}
		
		return ret;
	}
	
	//Ritorna true se il parametro specificato è valido; la validità del parametro dipende dalla sua classe di equivalenza, dalla sua obbligatorietà e dal tipo
	//di risorsa che rappresenta
	public boolean isParamValid(Param p){		
		//Se il parametro non rappresenta alcuna risorsa, la sua validità dipende solo dalla sua obbligatorietà e dal fatto che sia vuoto o meno
		if(p.getResourceType().equals(DiscourseResourceType.NO_RESOURCE)){
			//Se la classe di equivalenza è "invalid", il parametro è sicuramente invalido
			if(p.getClassParam().isInvalid()){
				return false;
			}
			//Se la classe di equivalenza è "empty" e il parametro è obbligatorio, il parametro è invalido
			if(p.getClassParam().isEmpty() && p.isRequired()){
				return false;
			}
		}
		//Se il parametro rappresenta una risorsa, è valido solo se la sua classe di equivalenza è strettamente "valida"; se non è "valida", il valore del parametro
		//non rappresenta una risorsa e quindi, ai fini del riconoscimento di un fallimento, va considerato invalido.
		else{
			if(!p.getClassParam().isValid()){
				return false;
			}
		}
		//In tutti i restanti casi, il parametro è valido
		return true;
	}

}
