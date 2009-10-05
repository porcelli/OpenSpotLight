package org.openspotlight.common.util.test;

public class ExampleClass {

    public ExampleClass() {

    }

    public void dangerousExpensiveMethod() throws Exception {
        Thread.sleep(500);
        throw new Exception();
    }

    public Object expensiveNonVoidMethod() throws Exception {
        Thread.sleep(500);

        return new Object();
    }

    public void expensiveVoidMethod() throws Exception {

        Thread.sleep(500);

    }

    public Object someExpensiveMethodWithParameter( final boolean throwsException,
                                                    final String id ) throws Exception {
        Thread.sleep(500);
        if (throwsException) {
            throw new Exception();
        }
        return new Object();
    }

}
