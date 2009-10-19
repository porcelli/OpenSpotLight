package org.openspotlight.remote.server.test;

public class ExampleInterfaceImplementation implements ExampleInterface {

    private final AnotherNonSerializableClass remoteResult = new AnotherNonSerializableClass("damn cool stuff!");

    public boolean expensiveMethodWithoutParameter() {
        try {
            Thread.sleep(500);
        } catch (final InterruptedException e) {
            // nothing to see here... move along sir.
        }
        return true;

    }

    public String expensiveMethodWithParameter( final String id,
                                                final String anotherStr,
                                                final boolean throwsException ) throws Exception {
        try {
            Thread.sleep(500);
        } catch (final InterruptedException e) {
            // nothing to see here... move along sir.
        }
        if (throwsException) {
            throw new Exception(id + anotherStr);
        }
        return id + anotherStr;

    }

    public AnotherNonSerializableClass getRemoteResult() {
        return this.remoteResult;
    }

    public Integer returns6Times( final Integer another ) {
        return another * 6;
    }

    public boolean returnsTrue() {
        return true;
    }

    public void throwAnException() throws EnumConstantNotPresentException {
        throw new EnumConstantNotPresentException(Enum.class, "stuff");
    }

    public boolean unsupportedMethod() {
        return false;
    }

}
