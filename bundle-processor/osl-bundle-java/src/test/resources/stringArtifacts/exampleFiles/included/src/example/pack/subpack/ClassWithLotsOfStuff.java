package example.pack.subpack;

import example.pack.AnotherExampleAnnotation;
import example.pack.AnotherExampleClass;
import example.pack.AnotherExampleInterface;

@AnotherExampleAnnotation(2)
public class ClassWithLotsOfStuff extends
		AnotherExampleClass<ClassWithLotsOfStuff> implements
		AnotherExampleInterface {
	public String stuff;

	/*
	 * public void doAnotherStuff() throws ExampleException {
	 * 
	 * }
	 */
	public AnotherExampleClass.InnerClass doSomething() {
		final ClassWithLotsOfStuff stuff = new ClassWithLotsOfStuff();
		final AnotherExampleClass.InnerClass inner = new InnerClass();
		final InnerClass inner2 = new InnerClass();
		inner2.parent.parent.parent = null;
		this.stuff = "";
		final AnotherExampleClass<?> clazz = null;
		clazz.doSomethingElse();
		doSomething();
		doStuff(0, null, 2.0d);
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
