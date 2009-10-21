package org.openspotlight.remote.server.test;

import java.util.Collection;
import java.util.Map;

import org.openspotlight.remote.annotation.CachedInvocation;
import org.openspotlight.remote.annotation.ReturnsRemoteReference;
import org.openspotlight.remote.annotation.UnsupportedRemoteMethod;

public interface ExampleInterface {

    public static class AnotherNonSerializableClass implements NonSerializableInterface {

        private String stuff;

        public AnotherNonSerializableClass(
                                            final String stuff ) {
            this.stuff = stuff;
        }

        public String getStuff() {
            return this.stuff;
        }

        public void setStuff( final String newStuff ) {
            this.stuff = newStuff;
        }
    }

    public interface NonSerializableInterface {
        public String getStuff();

        public void setStuff( final String newStuff );
    }

    @ReturnsRemoteReference
    public NonSerializableInterface doSomethingWith( NonSerializableInterface remoteParameter );

    @ReturnsRemoteReference
    public NonSerializableInterface doSomethingWithCollection( Collection<NonSerializableInterface> collection );

    @ReturnsRemoteReference
    public NonSerializableInterface doSomethingWithMap( Map<String, NonSerializableInterface> map );

    @CachedInvocation
    public boolean expensiveMethodWithoutParameter();

    @CachedInvocation
    public String expensiveMethodWithParameter( String id,
                                                String anotherStr,
                                                boolean throwsException ) throws Exception;

    @ReturnsRemoteReference
    public Collection<NonSerializableInterface> getList();

    @ReturnsRemoteReference
    public Map<String, NonSerializableInterface> getMap();

    @ReturnsRemoteReference
    public NonSerializableInterface getRemoteResult();

    public Integer returns6Times( Integer another );

    public void throwAnException() throws EnumConstantNotPresentException;

    @UnsupportedRemoteMethod
    public boolean unsupportedMethod();

}
