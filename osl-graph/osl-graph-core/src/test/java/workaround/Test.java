package workaround;

import java.lang.reflect.Method;

public class Test {
	
	public static void main(String[] args) {
		
		try {
		
			
			Method[] methods = java.util.LinkedHashSet.class.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				System.out.println(methods[i].getName());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}

}
