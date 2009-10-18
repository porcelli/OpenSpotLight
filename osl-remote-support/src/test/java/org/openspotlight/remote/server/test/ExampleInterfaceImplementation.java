package org.openspotlight.remote.server.test;

public class ExampleInterfaceImplementation implements ExampleInterface {

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
