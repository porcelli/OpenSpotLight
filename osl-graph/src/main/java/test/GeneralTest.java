package test;

import java.lang.reflect.Method;

import org.openspotlight.graph.annotation.SLProperty;

public class GeneralTest {
	
	public static void main(String[] args) {
		try {
			Method m = Material.class.getMethod("nothingMoreMethod", new Class<?>[] {});
			System.out.println(m.getAnnotation(SLProperty.class) != null);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}

abstract class Computer implements Part {
	
}

abstract class Notebook extends Computer {
	
}

interface Part extends Material {
	
}

interface Material extends Nothing {
	
}

interface Nothing {
	@SLProperty
	void nothingMethod();
	void nothingMoreMethod();
}