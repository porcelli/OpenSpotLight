

package org.openspotlight.graph;

import org.openspotlight.graph.manipulation.GraphWriter;

/**
 * This is the complete session available of the graph. <br>
 * Additionally to {@link SimpleGraphSession} functionallity, this session enables write data ({@link Node}s and {@link Link}s)
 * into into graph server.
 * <p>
 * <b>Important Note</b> its important to execute {@link #shutdown} method at end of its use.
 * 
 * @author feuteston
 * @author porcelli
 */
public interface FullGraphSession extends SimpleGraphSession {

    /**
     * Gives access to graph server writing operations.
     * 
     * @return the graph writer interface
     */
    GraphWriter toServer();

}
