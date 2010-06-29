package org.openspotlight.graph;

/**
 * Created by User: feu - Date: Jun 29, 2010 - Time: 4:56:14 PM
 */
public interface SLGraphService {

    public SLSimpleGraphSession openSimpleSession();

    public SLEntireGraphSession openEntireSession();

    public void shutdown();


}
