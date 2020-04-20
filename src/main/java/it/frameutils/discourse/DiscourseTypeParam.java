package it.alessandrochillemi.tesi.frameutils.discourse;

import it.alessandrochillemi.tesi.frameutils.TypeParam;

public enum DiscourseTypeParam implements TypeParam{
	STRING{
		public String[] getClasses(){
			return stringEquivalenceClasses;
		}
	},
	COLOR{
		public String[] getClasses(){
			return colorEquivalenceClasses;
		}
	},
	DATE{
		public String[] getClasses(){
			return dateEquivalenceClasses;
		}
	},
	EMAIL{
		public String[] getClasses(){
			return emailEquivalenceClasses;
		}
	},
	NUMBER{
		public String[] getClasses(){
			return numberEquivalenceClasses;
		}
	},
	LIST{
		public String[] getClasses(){
			return listEquivalenceClasses;
		}
	},
	BOOLEAN{
		public String[] getClasses(){
			return booleanEquivalenceClasses;
		}
	},
	ENUM{
		public String[] getClasses(){
			return enumEquivalenceClasses;
		}
	};
	
	private static String[] stringEquivalenceClasses = new String[] {"STR_NULL","STR_EMPTY","STR_VERY_LONG","STR_INVALID","STR_VALID"};
	private static String[] colorEquivalenceClasses = new String[] {"COL_EMPTY","COL_INVALID","COL_VALID"};
	private static String[] dateEquivalenceClasses = new String[] {"DATE_EMPTY","DATE_INVALID","DATE_VALID"};
	private static String[] emailEquivalenceClasses = new String[] {"EMAIL_EMPTY","EMAIL_INVALID","EMAIL_VALID"};
	private static String[] numberEquivalenceClasses = new String[] {"NUM_EMPTY","NUM_ABSOLUTE_MINUS_ONE","NUM_ABSOLUTE_ZERO","NUM_VERY_BIG","NUM_INVALID","NUM_VALID"};
	private static String[] listEquivalenceClasses = new String[] {"LIST_NULL","LIST_EMPTY","LIST_VALID"};
	private static String[] booleanEquivalenceClasses = new String[] {"BOOLEAN_EMPTY","BOOLEAN_INVALID","BOOLEAN_VALID"};
	private static String[] enumEquivalenceClasses = new String[] {"ENUM_EMPTY","ENUM_INVALID","ENUM_VALID"};
}
