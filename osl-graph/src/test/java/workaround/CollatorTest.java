package workaround;

import java.text.Collator;
import java.util.Locale;

public class CollatorTest {
	public static void main(String[] args) {
		Collator collator = Collator.getInstance(Locale.US);
		collator.setStrength(Collator.TERTIARY);
		String key1 = new String(collator.getCollationKey("Situação").toByteArray());
		String key2 = new String(collator.getCollationKey("Situacão").toByteArray());
		System.out.println(key1.equals(key2));
	}
}
