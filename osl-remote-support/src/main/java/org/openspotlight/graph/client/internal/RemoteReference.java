package org.openspotlight.graph.client.internal;

import java.io.Serializable;

public class RemoteReference<T> implements Serializable {

    private Class<T>  remoteType;

    private String    remoteReferenceId;

    private UserToken userToken;

    private T         proxyInstance;

}
