package org.openspotlight.graph;

import java.util.Collection;

/**
 * Created by User: feu - Date: Jun 29, 2010 - Time: 4:29:33 PM
 */
public interface SLEntireGraphSession extends SLSimpleGraphSession {

    TODO review transients


    TODO DO NOT FORGET TO USE THE ARTIFACT_ID DURRING CREATE METHODS
    /**
     * Adds the link.
     *
     * @param linkClass the link class
     * @param source    the source
     * @param target    the target
     * @return the l
     */
    public <L extends SLLink> L createLink(Class<L> linkClass,
                                        SLNode source,
                                        SLNode target);

    /**
     * Adds the link.
     *
     * @param linkClass the link class
     * @param source    the source
     * @param target    the target
     * @return the l
     */
    public <L extends SLLink> L createBidirectionalLink(Class<L> linkClass,
                                                     SLNode source,
                                                     SLNode target);


    /**
     * Save.
     */
    public void save();


    /**
     * Adds the node.
     *
     * @param clazz the clazz
     * @param name  the name
     * @return the t
     */
    public <T extends SLNode> T createChildNode(SLNode parent, Class<T> clazz,
                                             String name);

    /**
     * Adds the node.
     *
     * @param clazz                          the clazz
     * @param name                           the name
     * @param linkTypesForLinkDeletion       the link types for link deletion
     * @param linkTypesForLinkedNodeDeletion the link types for linked node deletion
     * @return the t
     */
    public <T extends SLNode> T createChildNode(SLNode parent, Class<T> clazz,
                                             String name,
                                             Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
                                             Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion);

    /**
     * Sets the caption.
     *
     * @param caption the caption
     */
    public void setNodeCaption(SLNode node, String caption);

    /**
     * Sets the caption.
     *
     * @param caption the caption
     */
    public void setContextCaption(SLContext context, String caption);

}
