package it.alessandrochillemi.tesi.frameutils.discourse;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import it.alessandrochillemi.tesi.frameutils.EquivalenceClass;

//Possibili classi di equivalenza per ogni parametro
public enum DiscourseEquivalenceClass implements EquivalenceClass{
	STR_NULL,STR_EMPTY,STR_VERY_LONG,STR_INVALID,STR_VALID,
	COL_EMPTY,COL_INVALID,COL_VALID,
	DATE_EMPTY,DATE_INVALID,DATE_VALID,
	EMAIL_EMPTY,EMAIL_INVALID,EMAIL_VALID,
	NUM_EMPTY,NUM_ABSOLUTE_MINUS_ONE,NUM_ABSOLUTE_ZERO,NUM_VERY_BIG,NUM_INVALID,NUM_VALID,
	LIST_NULL,LIST_EMPTY,LIST_VALID,
	BOOLEAN_EMPTY,BOOLEAN_INVALID,BOOLEAN_VALID,
	ENUM_EMPTY,ENUM_INVALID,ENUM_VALID;
	
	//Massimo numero di caratteri per ogni parametro
	private static final int MAX_LENGTH = 1001;
	
	public boolean isValid(){
		if(this.toString().endsWith("_VALID")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean isInvalid(){
		if(this.toString().endsWith("_INVALID")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean isEmpty(){
		if(this.toString().endsWith("_EMPTY")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public String generateValue(ArrayList<String> validValues){
		String value = null;
		int nextInt = 0;
		long nextLong = 0L;
		String nextString = new String();
		Date date = null;
		StringBuilder stringBuilder = null;
		
		if(this != null){
			switch(this){
				case BOOLEAN_EMPTY:
					value = "";
					break;
				case BOOLEAN_INVALID:
					//Generate a random string of random length from 1 to MAX_LENGTH, different than "true" or "false"
					do{
						nextString = RandomStringUtils.random(RandomUtils.nextInt(1, MAX_LENGTH), true, true);
					} while(nextString.equals("true") || nextString.equals("false"));
					value = nextString;
					break;
				case BOOLEAN_VALID:
					value = String.valueOf(RandomUtils.nextBoolean());
					break;
				case COL_EMPTY:
					value="";
					break;
				case COL_INVALID:
					//Generate random string with random length from 1 to MAX_LENGTH, different than 6 to make the color invalid
					do{
						nextString = RandomStringUtils.random(RandomUtils.nextInt(1, MAX_LENGTH), true, true);
					} while(nextString.length() == 6);
					value = nextString;
					break;
				case COL_VALID:
					//Generate random hexadecimal number from 0x000000 to 0xFFFFFF and save it as a string with leading # and 0s
					nextInt = RandomUtils.nextInt(0, 0xffffff + 1);
					value = String.format("%06x", nextInt);
					break;
				case DATE_EMPTY:
					value="";
					break;
				case DATE_INVALID:
					//Generate a new Date from a random Long representing milliseconds from Jan 1, 1970.
					nextLong = RandomUtils.nextLong(0, 48L * 365 * 24 * 60 * 60 * 1000);
					date = new Date(nextLong);
					value = new SimpleDateFormat("yyyy-MM-dd").format(date);
					
					stringBuilder = new StringBuilder(value);
					
					/* Set the first month character to a random integer from 2 to 9 to make the date invalid;
					  for example, if the random generated date is 2017-10-10 and the random integer is 5,
					  the invalid date will be 2017-50-10 */
					stringBuilder.setCharAt(5, Character.forDigit(RandomUtils.nextInt(2, 10), 10));
					
					value = stringBuilder.toString();
					break;
				case DATE_VALID:
					//Generate a new Date from a random Long representing milliseconds from Jan 1, 1970.
					nextLong = RandomUtils.nextLong(0, 48L * 365 * 24 * 60 * 60 * 1000);
					date = new Date(nextLong);
					value = new SimpleDateFormat("yyyy-MM-dd").format(date);
					break;
				case EMAIL_EMPTY:
					value="";
					break;
				case EMAIL_INVALID:
					//Generate random string with random length from 1 to MAX_LENGTH that doesn't contain a '@'
					do{
						nextString = RandomStringUtils.random(RandomUtils.nextInt(1, MAX_LENGTH), true, true);
					} while(nextString.contains("@"));
					value = nextString;
					break;
				case EMAIL_VALID:
					//Generate random alphanumeric string in the form of x@y.z (where x, y and z are strings of random lengths) of total length up to 2^16
					String firstEmailPart = RandomStringUtils.randomAlphanumeric(1, RandomUtils.nextInt(1,MAX_LENGTH-4));
					String secondEmailPart = RandomStringUtils.randomAlphanumeric(1, (MAX_LENGTH-3-firstEmailPart.length()));
					String thirdEmailPart = RandomStringUtils.randomAlphanumeric(1, (MAX_LENGTH-2-firstEmailPart.length()-secondEmailPart.length()));
					value = firstEmailPart+"@"+secondEmailPart+"."+thirdEmailPart;
					break;
				case LIST_EMPTY:
					value="";
					break;
				case LIST_NULL:
					value="NULL";
					break;
				case LIST_VALID:
					//Generate a list of random length (from 1 to 10) made of random alphanumeric strings with random lengths (from 0 to 10)
					nextInt = RandomUtils.nextInt(1, 11);
					stringBuilder = new StringBuilder();
					for(int i = 0; i<nextInt; i++){
						stringBuilder.append(RandomStringUtils.randomAlphanumeric(1, 11));
						stringBuilder.append(",");
					}
					value = stringBuilder.toString();
					break;
				case NUM_ABSOLUTE_MINUS_ONE:
					value="-1";
					break;
				case NUM_ABSOLUTE_ZERO:
					value="0";
					break;
				case NUM_EMPTY:
					value="";
					break;
				case NUM_INVALID:
					//Generate random alphabetic string with length from 1 to MAX_LENGTH
					value = RandomStringUtils.randomAlphabetic(1, MAX_LENGTH);
					break;
				case NUM_VALID:
					//Generate random integer from Integer.MIN_VALUE to Integer.MAX_VALUE
					value = String.valueOf(String.valueOf(new Random().nextInt()));
					break;
				case NUM_VERY_BIG:
					//Generate a number equal to Integer.MAX_VALUE + r or Integer.MIN_VALUE - r, where "r" is a random number between 1 and Integer.MAX_VALUE
					BigInteger veryBigValue = null;
					nextString = String.valueOf(RandomUtils.nextInt(1, Integer.MAX_VALUE));
					boolean negative = RandomUtils.nextBoolean();
					if(negative){
						veryBigValue = new BigInteger(String.valueOf(Integer.MIN_VALUE)).subtract((new BigInteger(nextString)));
					}
					else{
						veryBigValue = new BigInteger(String.valueOf(Integer.MAX_VALUE)).add((new BigInteger(nextString)));
					}
					value = veryBigValue.toString();
					break;
				case STR_EMPTY:
					value="";
					break;
				case STR_INVALID:
					//Generate random string with length from 1 to MAX_LENGTH with non-printable characters (ASCII code from 0 to 31)
					value = RandomStringUtils.random(RandomUtils.nextInt(1, MAX_LENGTH), 0, 31, false, false);
					break;
				case STR_NULL:
					value="NULL";
					break;
				case STR_VALID:
					//Generate random alphanumeric string as a random UUID
					value = UUID.randomUUID().toString();
					break;
				case STR_VERY_LONG:
//					//Generate random alphanumeric string with length from 2^16 to r, where "r" is a random number between 1 and 2^16-1
//					nextInt = 65536 + RandomUtils.nextInt(1, 65536);
//					value = RandomStringUtils.randomAlphanumeric(65536, nextInt);
					
					//Generate random alphanumeric string with length=MAX_LENGTH
					value = RandomStringUtils.randomAlphanumeric(MAX_LENGTH, MAX_LENGTH+1);
					break;
				case ENUM_EMPTY:
					value="";
					break;
				case ENUM_INVALID:
					//Generate a random string of random length from 1 to MAX_LENGTH, different than any of the valid values
					do{
						nextString = RandomStringUtils.random(RandomUtils.nextInt(1, MAX_LENGTH), true, true);
					} while(validValues.contains(nextString));
					value = nextString;
					break;
				case ENUM_VALID:
					//Pick one of the valid values
					nextInt = RandomUtils.nextInt(0, validValues.size());
					value = validValues.get(nextInt);
					break;
				default:
					break;
			
			}
		}
		return value;
		
	}
}
