package org.openspotlight.remote.server.test;

import org.openspotlight.remote.annotation.CachedInvocation;
import org.openspotlight.remote.annotation.ReturnsRemoteReference;
import org.openspotlight.remote.annotation.UnsupportedRemoteMethod;

public interface ExampleInterface {

    public static class AnotherNonSerializableClass implements NonSerializableInterface {

        private final String stuff;

        public AnotherNonSerializableClass(
                                            final String stuff ) {
            this.stuff = stuff;
        }

        public String getStuff() {
            return this.stuff;
        }
    }

    public interface NonSerializableInterface {
        public String getStuff();
    }

    @CachedInvocation
    public boolean expensiveMethodWithoutParameter();

    @CachedInvocation
    public String expensiveMethodWithParameter( String id,
                                                String anotherStr,
                                                boolean throwsException ) throws Exception;

    @ReturnsRemoteReference
    public NonSerializableInterface getRemoteResult();

    public Integer returns6Times( Integer another );

    public void throwAnException() throws EnumConstantNotPresentException;

    @UnsupportedRemoteMethod
    public boolean unsupportedMethod();
}
