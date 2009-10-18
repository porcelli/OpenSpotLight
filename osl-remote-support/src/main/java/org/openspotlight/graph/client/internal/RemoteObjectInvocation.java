package org.openspotlight.graph.client.internal;

public class RemoteObjectInvocation<T, R> {

    private final R                  returnType;

    private final Class<?>[]         parameterTypes;

    private final Object[]           parameters;

    private final String             methodName;

    private final RemoteReference<T> remoteReference;

    public RemoteObjectInvocation(
                                   final R returnType, final Class<?>[] parameterTypes, final Object[] parameters,
                                   final String methodName, final RemoteReference<T> remoteReference ) {
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
        this.methodName = methodName;
        this.remoteReference = remoteReference;
    }

}
