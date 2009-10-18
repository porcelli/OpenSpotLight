package org.openspotlight.remote.internal;

import static org.openspotlight.common.util.Arrays.andOf;
import static org.openspotlight.common.util.Arrays.of;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Equals.eachEquality;
import static org.openspotlight.common.util.HashCodes.hashOf;

import java.io.Serializable;

import org.openspotlight.remote.server.RemoteObjectServer;

/**
 * The Class RemoteReference will be used to locate the remote reference on actions like sending messages to the
 * {@link RemoteObjectServer} asking for some method invocation.
 * 
 * @param <T>
 */
public class RemoteReference<T> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1738168629624338103L;

    /** The remote type. */
    private final Class<T>    remoteType;

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
     */
    public RemoteReference(
                            final Class<T> remoteType, final String remoteReferenceId, final UserToken userToken ) {
        checkNotNull("remoteType", remoteType);
        checkNotEmpty("remoteReferenceId", remoteReferenceId);
        checkNotNull("userToken", userToken);
        this.remoteType = remoteType;
        this.remoteReferenceId = remoteReferenceId;
        this.userToken = userToken;
        this.hashcode = hashOf(this.remoteType, this.remoteReferenceId, this.userToken);
    }

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

    public String getRemoteReferenceId() {
        return this.remoteReferenceId;
    }

    public Class<T> getRemoteType() {
        return this.remoteType;
    }

    public UserToken getUserToken() {
        return this.userToken;
    }

    @Override
    public int hashCode() {
        return this.hashcode;
    }
}
