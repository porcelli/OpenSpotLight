package org.openspotlight.remote.internal;

import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.HashCodes.hashOf;

import java.io.Serializable;

import org.openspotlight.common.util.Arrays;

/**
 * The Class RemoteReference will be used to locate the remote reference on actions like sending messages to the
 * {@link RemoteObjectServer} asking for some method invocation.
 * 
 * @param <T>
 */
public class RemoteReference<T> implements Serializable {

    /**
     * The Interface ObjectMethods contains commons methods used on Object class. This is necessary for the proxy implementation.
     */
    public interface ObjectMethods {

        /**
         * Equals.
         * 
         * @param o the o
         * @return true, if successful
         */
        public boolean equals( Object o );

        /**
         * Hash code.
         * 
         * @return the int
         */
        public int hashCode();

        /**
         * To string.
         * 
         * @return the string
         */
        public String toString();
    }

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1738168629624338103L;

    /** The remote type. */
    private final Class<T>    remoteType;

    /** The interfaces. */
    private final Class<?>[]  interfaces;

    /** The remote reference id. */
    private final String      remoteReferenceId;

    /** The user token. */
    private final UserToken   userToken;

    /** The hashcode. */
    private final int         hashcode;

    /**
     * Instantiates a new remote reference.
     * 
     * @param remoteType the remote type
     * @param remoteReferenceId the remote reference id
     * @param userToken the user token
     * @param interfaces the interfaces
     */
    public RemoteReference(
                            final Class<T> remoteType, final Class<?>[] interfaces, final String remoteReferenceId,
                            final UserToken userToken ) {
        checkNotEmpty("remoteReferenceId", remoteReferenceId);
        checkNotNull("userToken", userToken);
        this.remoteType = remoteType;
        this.remoteReferenceId = remoteReferenceId;
        this.userToken = userToken;
        this.interfaces = Arrays.unionOf(interfaces, ObjectMethods.class, remoteType);
        this.hashcode = hashOf(this.remoteType, this.remoteReferenceId, this.userToken);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object obj ) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RemoteReference<?>)) {
            return false;
        }
        final RemoteReference<?> that = (RemoteReference<?>)obj;
        return eachEquality(of(this.remoteType, this.remoteReferenceId, this.userToken), andOf(that.remoteType,
                                                                                               that.remoteReferenceId,
                                                                                               that.userToken));
    }

    /**
     * Gets the interfaces.
     * 
     * @return the interfaces
     */
    public Class<?>[] getInterfaces() {
        return this.interfaces;
    }

    /**
     * Gets the remote reference id.
     * 
     * @return the remote reference id
     */
    public String getRemoteReferenceId() {
        return this.remoteReferenceId;
    }

    /**
     * Gets the remote type.
     * 
     * @return the remote type
     */
    public Class<T> getRemoteType() {
        return this.remoteType;
    }

    /**
     * Gets the user token.
     * 
     * @return the user token
     */
    public UserToken getUserToken() {
        return this.userToken;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.hashcode;
    }
}
