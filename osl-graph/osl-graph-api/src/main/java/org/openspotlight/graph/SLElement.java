package org.openspotlight.graph;

import org.openspotlight.common.Pair;
import org.openspotlight.remote.annotation.DisposeMethod;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by User: feu - Date: Jun 29, 2010 - Time: 3:27:29 PM
 */
public interface SLElement {
    /**
     * Gets the iD.
     *
     * @return the iD
     */
    public String getID();

    /**
     * Gets the properties.
     *
     * @return the properties
     */
    public Set<Pair<String, Serializable>> getProperties();

    /**
     * Gets the property.
     *
     * @param name the name
     * @return the property
     */
    public <V extends Serializable> V getPropertyValue(String name);

    /**
     * Gets the property value as string.
     *
     * @param name the name
     * @return the property value as string
     */
    public String getPropertyValueAsString(String name);


    /**
     * Sets the property.
     *
     * @param name  the name
     * @param value the value
     * @return the sL link property< v>
     */
    public <V extends Serializable> void setProperty(String name,
                                                     V value);

    public void removeProperty(String propertyName);

    /**
     * Removes the.
     */
    @DisposeMethod
    public void remove();


    /**
     * Gets line references in tree format.
     *
     * @return the tree line references
     */
    public SLTreeLineReference getTreeLineReferences();

    /**
     * Gets line references in tree format for a specific artifact.
     *
     * @param artifactId the artifact id
     * @return the tree line references
     */
    public SLTreeLineReference getTreeLineReferences(String artifactId);


    /**
     * Adds the line reference.
     *
     * @param startLine       the start line
     * @param endLine         the end line
     * @param startColumn     the start column
     * @param endColumn       the end column
     * @param statement       the statement
     * @param artifactId      the artifact id
     * @param artifactVersion the artifact version
     * @return the sL line reference
     */
    public void addLineReference(int startLine,
                                 int endLine,
                                 int startColumn,
                                 int endColumn,
                                 String statement,
                                 String artifactId,
                                 String artifactVersion);


}
