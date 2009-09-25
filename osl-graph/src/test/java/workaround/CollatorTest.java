package workaround;

import java.text.Collator;
import java.util.Locale;

public class CollatorTest {
	
	
	public static void main(String[] args) {
		
		Collator collator = Collator.getInstance(Locale.US);
		collator.setStrength(Collator.PRIMARY);
		
		byte[] arr = collator.getCollationKey("java.util.Collaction").toByteArray();
		System.out.println(arr.length);
		System.out.println(new String(collator.getCollationKey("java.util.Collaction").toByteArray()).length());
		
		String s1 = new String(collator.getCollationKey("java.util.List").toByteArray());
		String s2 = new String(collator.getCollationKey(".LIST").toByteArray());
		
		System.out.println(s1.contains(s2));


		
	}

}
