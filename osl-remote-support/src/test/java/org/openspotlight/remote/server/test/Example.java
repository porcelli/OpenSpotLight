/**
 * 
 */
package org.openspotlight.remote.server.test;

import java.io.Serializable;

public interface Example {
    public static class StrangeReturn implements Serializable {
        private final int i;

        public StrangeReturn(
                              final int i ) {
            this.i = i;
        }

        public int getI() {
            return i;
        }

    }

    public StrangeReturn add3( int arg );
}
