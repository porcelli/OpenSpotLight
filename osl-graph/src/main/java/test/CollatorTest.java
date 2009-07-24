package test;

import java.text.Collator;
import java.util.Locale;

public class CollatorTest {

	public static void main(String[] args) {
		
		Locale l = new Locale("en_US");
		
		 //Get the Collator for US English and set its strength to PRIMARY
		 Collator usCollator = Collator.getInstance(l);
		 usCollator.setStrength(Collator.PRIMARY);
		 if( usCollator.compare("seleção", "selecao") == 0 ) {
		     System.out.println("Strings are equivalent");
		 }
		 else {
			 System.out.println("Strings are NOT equivalent");
		 }

	}
}
