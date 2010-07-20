package org.openspotlight.graph;

import java.math.BigInteger;

/**
 * The BaseIdentifer is a common API that defines common data relative to unique identifiers as well its weight data inside
 * OpenSpotLight Graph.
 * <p>
 * Weight are data related to OpenSpotLight Indexing.
 * 
 * @author porcelli
 * @author feuteston
 */
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
    int getInitialWeightValue();

    /**
     * Returns the actual weight value. The weight is used internally for indexing purpose.
     * 
     * @return the element weight.
     */
    BigInteger getWeightValue();

}
