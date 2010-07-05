package org.openspotlight.graph;

/**
 * Created by IntelliJ IDEA.
 * User: porcelli
 * Date: 05/07/2010
 * Time: 10:29:47
 * To change this template use File | Settings | File Templates.
 */
public interface SLGraph {

    public SLSimpleGraphSession direct();

    public SLFullGraphSession location(SLGraphLocation location);

    public void shutdown();

}