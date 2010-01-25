package example.pack.subpack;

import example.pack.AnotherExampleAnnotation;
import example.pack.AnotherExampleClass;
import example.pack.AnotherExampleInterface;

@AnotherExampleAnnotation(2)
public class ClassWithLotsOfStuff extends
		AnotherExampleClass<ClassWithLotsOfStuff> implements
		AnotherExampleInterface {
	/*
	 * public void doAnotherStuff() throws ExampleException {
	 * 
	 * }
	 */
	public AnotherExampleClass.InnerClass doSomething() {
		return null;
	}

	public <E extends AnotherExampleClass<?>> E doStuff(final int a,
			final AnotherExampleInterface b, final double c) {
		return null;
	}

	public <E extends AnotherExampleClass<? super AnotherExampleInterface>> E doSuperStuff() {
		return null;
	}
}
