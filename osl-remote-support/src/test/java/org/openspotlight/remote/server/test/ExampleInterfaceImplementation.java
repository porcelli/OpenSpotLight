package org.openspotlight.remote.server.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExampleInterfaceImplementation implements ExampleInterface {

    private final AnotherNonSerializableClass           remoteResult = new AnotherNonSerializableClass("damn cool stuff!");

    private final List<NonSerializableInterface>        list         = new ArrayList<NonSerializableInterface>();

    private final Map<String, NonSerializableInterface> map          = new HashMap<String, NonSerializableInterface>();

    public ExampleInterfaceImplementation() {
        this.map.put("1", new AnotherNonSerializableClass("1"));
        this.map.put("2", new AnotherNonSerializableClass("2"));
        this.map.put("3", new AnotherNonSerializableClass("3"));
        this.list.add(new AnotherNonSerializableClass("1"));
        this.list.add(new AnotherNonSerializableClass("2"));
        this.list.add(new AnotherNonSerializableClass("3"));
    }

    public boolean expensiveMethodWithoutParameter() {
        try {
            Thread.sleep(100);
        } catch (final InterruptedException e) {
            // nothing to see here... move along sir.
        }
        return true;

    }

    public String expensiveMethodWithParameter( final String id,
                                                final String anotherStr,
                                                final boolean throwsException ) throws Exception {
        try {
            Thread.sleep(100);
        } catch (final InterruptedException e) {
            // nothing to see here... move along sir.
        }
        if (throwsException) {
            throw new Exception(id + anotherStr);
        }
        return id + anotherStr;

    }

    public List<NonSerializableInterface> getList() {

        return this.list;
    }

    public Map<String, NonSerializableInterface> getMap() {
        return this.map;
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
