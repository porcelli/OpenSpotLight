/**
 * 
 */
package org.openspotlight.graph.server.test;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Example extends Remote {
    public int add3( int arg ) throws RemoteException;
}