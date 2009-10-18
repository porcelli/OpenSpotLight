/**
 * 
 */
package org.openspotlight.graph.server.test;


public class ExampleServer implements Example {
    public StrangeReturn add3( final int arg ) {
        return new StrangeReturn(3 + arg);
    }
}
