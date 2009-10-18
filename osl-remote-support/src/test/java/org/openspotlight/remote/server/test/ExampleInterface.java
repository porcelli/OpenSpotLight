package org.openspotlight.remote.server.test;

import org.openspotlight.remote.annotation.UnsupportedRemoteMethod;

public interface ExampleInterface {
    public Integer returns6Times( Integer another );

    public void throwAnException() throws EnumConstantNotPresentException;

    @UnsupportedRemoteMethod
    public boolean unsupportedMethod();
}
