package test;

public class BitTest {
	
	public static final int DIRECTION_AB = Integer.parseInt("100", 2);

	public static void main(String[] args)  {
		
		int DIRECTION_AB = Integer.parseInt("100", 2);
		int DIRECTION_BA = Integer.parseInt("010", 2);
		int DIRECTION_BOTH = Integer.parseInt("001", 2);
		
		int value = DIRECTION_BA | DIRECTION_AB;
		
		if (value == (value | DIRECTION_AB)) {
			System.out.println("DIRECTION_AB");
		}

		if (value == (value | DIRECTION_BA)) {
			System.out.println("DIRECTION_BA");
		}

		if (value == (value | DIRECTION_BOTH)) {
			System.out.println("DIRECTION_BOTH");
		}
		
		System.out.println(DIRECTION_AB);
		System.out.println(DIRECTION_BA);
		System.out.println(DIRECTION_BOTH);

	}
}
