/**
 * 
 */
package org.openspotlight.graph.server.test;

import java.rmi.Remote;
import java.rmi.RemoteException;

public class ExampleServer implements Example, Remote {
    public int add3( final int arg ) throws RemoteException {
        return 3 + arg;
    }
}
