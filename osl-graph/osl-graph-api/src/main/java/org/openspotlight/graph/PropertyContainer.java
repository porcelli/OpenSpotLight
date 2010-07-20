package org.openspotlight.graph;

import java.io.Serializable;
import java.util.Set;

import org.openspotlight.common.Pair;
import org.openspotlight.graph.exception.PropertyNotFoundException;

/**
 * Defines a common API for handling properties on {@link Node}, {@link Link} and {@link Context}.
 * <p>
 * Properties are key-value pairs where keys are always strings and values are any serializable object.
 * 
 * @author porcelli
 * @author feuteston
 */
public interface PropertyContainer {

    /**
     * Checks if this element is modified.
     * 
     * @return true is modified, false otherwise
     */
    boolean isDirty();

    /**
     * Sets the property value for the given key. Null is not an accepted property value.
     * 
     * @param key the property key
     * @param value the property value
     * @throws IllegalArgumentException if value is null
     */
    <V extends Serializable> void setProperty( String key,
                                               V value ) throws IllegalArgumentException;

    /**
     * Returns a list of properties pairs (key-value).
     * 
     * @return the key-value properties pairs
     */
    Set<Pair<String, Serializable>> getProperties();

    /**
     * Returns <code>true</code> if this property container has a property accessible through the given key, <code>false</code>
     * otherwise. Null is not an accepted property value.
     * 
     * @param key the property key
     * @return <code>true</code> if this element has a property accessible through the given key, <code>false</code> otherwise
     */
    boolean hasProperty( String key ) throws IllegalArgumentException;

    /**
     * Returns all existing property keys, or an empty iterable if this element has no properties.
     * 
     * @return all property keys on this element
     */
    Iterable<String> getPropertyKeys();

    /**
     * Returns the property value associated with the given key.
     * 
     * @param key the property key
     * @return the property value
     * @throws PropertyNotFoundException if there's no property associated with key
     */
    <V extends Serializable> V getPropertyValue( String key ) throws PropertyNotFoundException;

    /**
     * Returns the property value associated with the given key, or a default value.
     * 
     * @param key the property key
     * @param defaultValue the default value that will be returned if no property value was associated with the given key
     * @return the property value associated with the given key or the default value.
     */
    <V extends Serializable> V getPropertyValue( String key,
                                                 V defaultValue );

    /**
     * Returns the property value as string. (this is just a sugar method)
     * 
     * @param key the property key
     * @return the property value as string
     * @throws PropertyNotFoundException if there's no property associated with key
     */
    String getPropertyValueAsString( String key ) throws PropertyNotFoundException;

    /**
     * Removes the property associated with the given key if exists. If there's no property associated with the key, nothing will
     * happen.
     * 
     * @param key the property key
     */
    void removeProperty( String key );

}
