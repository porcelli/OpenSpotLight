package org.openspotlight.graph;

import java.math.BigInteger;

public interface BaseIdentifer extends PropertyContainer {

    /**
     * Returns the unique identifier of the element.
     * 
     * @return the unique identifier
     */
    String getId();

    /**
     * Returns the initial weight value (set during element creation). The weight is used internally for indexing purpose.
     * 
     * @return the element weight.
     */
    int getInitialWeight();

    /**
     * Returns the actual weight value. The weight is used internally for indexing purpose.
     * 
     * @return the element weight.
     */
    BigInteger getWeight();

}
