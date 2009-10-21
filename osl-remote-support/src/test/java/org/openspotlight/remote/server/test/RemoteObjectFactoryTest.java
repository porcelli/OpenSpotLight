package org.openspotlight.remote.server.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.remote.client.CantConnectException;
import org.openspotlight.remote.client.RemoteObjectFactory;
import org.openspotlight.remote.server.AccessDeniedException;
import org.openspotlight.remote.server.RemoteObjectServerImpl;
import org.openspotlight.remote.server.UserAuthenticator;
import org.openspotlight.remote.server.test.ExampleInterface.NonSerializableInterface;

/**
 * The Class RemoteObjectFactoryTest.
 */
public class RemoteObjectFactoryTest {

    /**
     * The Class AllowUserValidAutenticator.
     */
    private static class AllowUserValidAutenticator implements UserAuthenticator {

        /* (non-Javadoc)
         * @see org.openspotlight.remote.server.UserAuthenticator#canConnect(java.lang.String, java.lang.String, java.lang.String)
         */
        public boolean canConnect( final String userName,
                                   final String password,
                                   final String clientHost ) {
            return "valid".equals(userName);

        }

    }

    /** The server. */
    private static RemoteObjectServerImpl server;

    /**
     * Setup.
     * 
     * @throws Exception the exception
     */
    @BeforeClass
    public static void setup() throws Exception {
        server = new RemoteObjectServerImpl(new AllowUserValidAutenticator(), 7070, 250L);
        server.registerInternalObjectFactory(ExampleInterface.class, new ExampleInterfaceFactory());
    }

    /**
     * Shutdown.
     * 
     * @throws Exception the exception
     */
    @AfterClass
    public static void shutdown() throws Exception {
        if (server != null) {
            server.shutdown();
        }
        server = null;
    }

    /**
     * Should create remote object factory.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldCreateRemoteObjectFactory() throws Exception {
        new RemoteObjectFactory("localhost", 7070, "valid", "password");
    }

    /**
     * Should create remote reference.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldCreateRemoteReference() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
    }

    /**
     * Should get error on method marked as unsupported.
     * 
     * @throws Exception the exception
     */
    @Test( expected = UnsupportedOperationException.class )
    public void shouldGetErrorOnMethodMarkedAsUnsupported() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        proxy.unsupportedMethod();
    }

    @Test
    public void shouldGetListItem() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        final Collection<NonSerializableInterface> remoteList = proxy.getList();
        assertThat(remoteList.size(), is(3));
        assertThat(remoteList.iterator().next().getStuff(), is("1"));
    }

    @Test
    public void shouldGetMapItem() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        final Map<String, NonSerializableInterface> remoteMap = proxy.getMap();
        assertThat(remoteMap.size(), is(3));
        assertThat(remoteMap.get("1").getStuff(), is("1"));
    }

    /**
     * Should get the right exception.
     * 
     * @throws Exception the exception
     */
    @Test( expected = EnumConstantNotPresentException.class )
    public void shouldGetTheRightException() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        proxy.throwAnException();
    }

    /**
     * Should invalidate user when it gets the timeout.
     * 
     * @throws Exception the exception
     */
    @Test( expected = UndeclaredThrowableException.class )
    public void shouldInvalidateUserWhenItGetsTheTimeout() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        Thread.sleep(700);
        proxy.getRemoteResult();
    }

    /**
     * Should invoke an method.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldInvokeAnMethod() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        final Integer result = proxy.returns6Times(6);
        assertThat(result, is(36));
    }

    /**
     * Should not create remote object factory when server is invalid.
     * 
     * @throws Exception the exception
     */
    @Test( expected = CantConnectException.class )
    public void shouldNotCreateRemoteObjectFactoryWhenServerIsInvalid() throws Exception {
        new RemoteObjectFactory("localhost", 666, "userName", "password");
    }

    /**
     * Should not create remote object factory when user is invalid.
     * 
     * @throws Exception the exception
     */
    @Test( expected = AccessDeniedException.class )
    public void shouldNotCreateRemoteObjectFactoryWhenUserIsInvalid() throws Exception {
        new RemoteObjectFactory("localhost", 7070, "invalid", "password");
    }

    @Test
    public void shouldPassNewRemoteReferenceAsParameter() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        final NonSerializableInterface nonSerializableResult = proxy.getRemoteResult();
        nonSerializableResult.setStuff("hooray");
        final NonSerializableInterface anotherResult = proxy.doSomethingWith(nonSerializableResult);
        assertThat(anotherResult.getStuff(), is("AAhooray"));
        assertThat(nonSerializableResult.getStuff(), is("AAhooray"));
    }

    @Test
    public void shouldPassNewRemoteReferenceAsParameterInsideCollection() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        final NonSerializableInterface nonSerializableResult = proxy.getRemoteResult();
        nonSerializableResult.setStuff("hooray");
        final NonSerializableInterface anotherResult = proxy.doSomethingWithCollection(Arrays.asList(nonSerializableResult));
        assertThat(anotherResult.getStuff(), is("hooray"));
        assertThat(proxy.getList().size(), is(1));
        assertThat(proxy.getList().iterator().next().getStuff(), is("hooray"));
    }

    @Test
    public void shouldPassNewRemoteReferenceAsParameterInsideMap() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        final NonSerializableInterface nonSerializableResult = proxy.getRemoteResult();
        nonSerializableResult.setStuff("hooray");
        final HashMap<String, NonSerializableInterface> map = new HashMap<String, NonSerializableInterface>();
        map.put("o", nonSerializableResult);
        final NonSerializableInterface anotherResult = proxy.doSomethingWithMap(map);
        assertThat(anotherResult.getStuff(), is("hooray"));
        assertThat(proxy.getMap().size(), is(1));
        assertThat(proxy.getMap().entrySet().iterator().next().getValue().getStuff(), is("hooray"));
    }

    /**
     * Should retrive information from cache.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldRetriveInformationFromCache() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        final long start1 = System.currentTimeMillis();
        final boolean result = proxy.expensiveMethodWithoutParameter();
        final long finish1 = System.currentTimeMillis();
        final long start2 = System.currentTimeMillis();
        final boolean result2 = proxy.expensiveMethodWithoutParameter();
        final long finish2 = System.currentTimeMillis();
        assertThat(finish1 - start1 > finish2 - start2, is(true));
        assertThat(result, is(result2));
        assertThat(result, is(true));
    }

    /**
     * Should retrive information from cache usim method with parameters.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldRetriveInformationFromCacheUsimMethodWithParameters() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        final long start1 = System.currentTimeMillis();
        final String result = proxy.expensiveMethodWithParameter("A", "B", false);
        final long finish1 = System.currentTimeMillis();
        final long start2 = System.currentTimeMillis();
        final String result2 = proxy.expensiveMethodWithParameter("A", "B", false);
        final long finish2 = System.currentTimeMillis();
        assertThat(finish1 - start1 > finish2 - start2, is(true));
        assertThat(result, is(result2));
        assertThat(result, is("AB"));
    }

    /**
     * Should retrive information from cache usim method with parameters throwing exceptions.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldRetriveInformationFromCacheUsimMethodWithParametersThrowingExceptions() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        Exception e1 = null;
        Exception e2 = null;

        final long start1 = System.currentTimeMillis();
        try {
            proxy.expensiveMethodWithParameter("A", "B", true);
        } catch (final Exception e) {
            e1 = e;
        }
        final long finish1 = System.currentTimeMillis();
        final long start2 = System.currentTimeMillis();
        try {
            proxy.expensiveMethodWithParameter("A", "B", true);
        } catch (final Exception e) {
            e2 = e;
        }
        final long finish2 = System.currentTimeMillis();
        assertThat(finish1 - start1 > finish2 - start2, is(true));
        assertThat(e1, is(e2));
        assertThat(e1, is(notNullValue()));
    }

    /**
     * Should return remote reference for an method invocation.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldReturnRemoteReferenceForAnMethodInvocation() throws Exception {
        final ExampleInterface proxy = new RemoteObjectFactory("localhost", 7070, "valid", "password").createRemoteObject(ExampleInterface.class);
        final NonSerializableInterface nonSerializableResult = proxy.getRemoteResult();
        assertThat(nonSerializableResult.getStuff(), is("damn cool stuff!"));

    }

    //FIXME test collections with null return and so on. Needs to return null on remote and see if it works
}
