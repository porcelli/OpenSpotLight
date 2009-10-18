package org.openspotlight.remote.server.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.remote.client.CantConnectException;
import org.openspotlight.remote.client.RemoteObjectFactory;
import org.openspotlight.remote.server.AccessDeniedException;
import org.openspotlight.remote.server.RemoteObjectServerImpl;
import org.openspotlight.remote.server.UserAuthenticator;
import org.openspotlight.remote.server.test.ExampleInterface.NonSerializableInterface;

public class RemoteObjectFactoryTest {

    private static class AllowUserValidAutenticator implements UserAuthenticator {

        public boolean canConnect( final String userName,
                                   final String password,
                                   final String clientHost ) {
            return "valid".equals(userName);

        }

    }

    private static RemoteObjectServerImpl server;

    @BeforeClass
    public static void setup() throws Exception {
        server = new RemoteObjectServerImpl(new AllowUserValidAutenticator(), 7070, 1);
        server.registerInternalObjectFactory(ExampleInterface.class, new ExampleInterfaceFactory());
    }

    @AfterClass
    public static void shutdown() throws Exception {
        server.shutdown();
        server = null;
    }

    @Test
    public void shouldCreateRemoteObjectFactory() throws Exception {
        new RemoteObjectFactory("localhost", 7070, "valid", "password");
    }

    @Test
    public void shouldCreateRemoteReference() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
    }

    @Test( expected = UnsupportedOperationException.class )
    public void shouldGetErrorOnMethodMarkedAsUnsupported() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        proxy.unsupportedMethod();
    }

    @Test( expected = EnumConstantNotPresentException.class )
    public void shouldGetTheRightException() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        proxy.throwAnException();
    }

    @Test
    public void shouldInvokeAnMethod() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        final Integer result = proxy.returns6Times(6);
        assertThat(result, is(36));
    }

    @Test( expected = CantConnectException.class )
    public void shouldNotCreateRemoteObjectFactoryWhenServerIsInvalid() throws Exception {
        new RemoteObjectFactory("localhost", 666, "userName", "password");
    }

    @Test( expected = AccessDeniedException.class )
    public void shouldNotCreateRemoteObjectFactoryWhenUserIsInvalid() throws Exception {
        new RemoteObjectFactory("localhost", 7070, "invalid", "password");
    }

    @Test
    public void shouldReturnRemoteReferenceForAnMethodInvocation() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        final NonSerializableInterface nonSerializableResult = proxy.getRemoteResult();
        assertThat(nonSerializableResult.getStuff(), is("damn cool stuff!"));
        //FIXME here, cache is mandatory if on server the return is the same reference

    }

}
