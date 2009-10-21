package org.openspotlight.remote.internal;

import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.HashCodes.hashOf;

import java.io.Serializable;

/**
 * The Class RemoteObjectInvocation is used to transfer data to invoke objects on the server.
 * 
 * @param <T>
 */
public class RemoteObjectInvocation<T> implements Serializable {

    /**
     * 
     */
    private static final long        serialVersionUID = -4821171830502625526L;

    /** The user token. */
    private final UserToken          userToken;

    /** The return type. */
    private final Class<?>           returnType;

    /** The parameter types. */
    private final Class<?>[]         parameterTypes;

    /** The parameters. */
    private final Object[]           parameters;

    /** The method name. */
    private final String             methodName;

    /** The remote reference. */
    private final RemoteReference<T> remoteReference;

    /** The hashcode. */
    private final int                hashcode;

    /**
     * Instantiates a new remote object invocation.
     * 
     * @param userToken the user token
     * @param returnType the return type
     * @param parameterTypes the parameter types
     * @param parameters the parameters
     * @param methodName the method name
     * @param remoteReference the remote reference
     */
    public RemoteObjectInvocation(
                                   final Class<?> returnType, final Class<?>[] parameterTypes, final Object[] parameters,
                                   final String methodName, final RemoteReference<T> remoteReference ) {
        checkNotNull("returnType", returnType);
        checkNotNull("parameterTypes", parameterTypes);
        checkNotNull("parameters", parameters);
        checkNotEmpty("methodName", methodName);
        checkNotNull("remoteReference", remoteReference);
        checkCondition("correctNumberOfArguments", parameters.length == parameterTypes.length);

        this.userToken = remoteReference.getUserToken();
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
        this.methodName = methodName;
        this.remoteReference = remoteReference;
        this.hashcode = hashOf(this.userToken, this.returnType, this.parameterTypes, this.methodName, this.remoteReference,
                               this.parameters);
    }

    @Override
    public boolean equals( final Object obj ) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RemoteObjectInvocation<?>)) {
            return false;
        }
        final RemoteObjectInvocation<?> that = (RemoteObjectInvocation<?>)obj;
        return eachEquality(of(this.userToken, this.returnType, this.parameterTypes, this.methodName, this.remoteReference,
                               this.parameters), andOf(that.userToken, that.returnType, that.parameterTypes, that.methodName,
                                                       that.remoteReference, that.parameters));
    }

    public String getMethodName() {
        return this.methodName;
    }

    public Object[] getParameters() {
        return this.parameters;
    }

    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }

    public RemoteReference<T> getRemoteReference() {
        return this.remoteReference;
    }

    public final Class<?> getReturnType() {
        return this.returnType;
    }

    public UserToken getUserToken() {
        return this.userToken;
    }

    @Override
    public int hashCode() {
        return this.hashcode;
    }

}
