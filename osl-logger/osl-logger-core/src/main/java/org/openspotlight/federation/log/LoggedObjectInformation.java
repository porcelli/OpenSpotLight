package org.openspotlight.federation.log;

import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.graph.SLNode;
import org.openspotlight.log.LogableObject;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.SimpleNodeType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The Class LoggedObjectInformation is used to represent objects related to a given log.
 */
@Name("logged_object_information")
public class LoggedObjectInformation implements SimpleNodeType, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2812040814742711306L;

    private static List<LogableObject> getHierarchyFrom(final LogableObject o) {
        final List<LogableObject> result = new LinkedList<LogableObject>();
        result.add(o);
        LogableObject parent = LoggedObjectInformation.getParent(o);
        while (parent != null) {
            result.add(parent);
            parent = LoggedObjectInformation.getParent(parent);
        }
        return result;
    }

    /**
     * Gets the hierarchy from.
     *
     * @param anotherNodes the another nodes
     * @return the hierarchy from
     */
    public static List<LoggedObjectInformation> getHierarchyFrom(final LogableObject... anotherNodes) {
        final List<LogableObject> nodes = new LinkedList<LogableObject>();
        for (final LogableObject o : anotherNodes) {
            nodes.addAll(LoggedObjectInformation.getHierarchyFrom(o));
        }
        Collections.reverse(nodes);
        final List<LoggedObjectInformation> result = new ArrayList<LoggedObjectInformation>(nodes.size());
        for (int i = 0, size = nodes.size(); i < size; i++) {
            result.add(new LoggedObjectInformation(i, nodes.get(i)));
        }
        return result;
    }

    /**
     * Gets the parent.
     *
     * @param o the o
     * @return the parent
     */
    private static LogableObject getParent(final LogableObject o) {
        if (o instanceof SLNode) {
            final SLNode node = (SLNode) o;
            return node.getParent();
        } else {
            return null;// other types have the path information. Now the
            // parent nodes isn't necessary
        }
    }

    private int order;

    /**
     * The unique id.
     */
    private String uniqueId;

    /**
     * The friendly description.
     */
    private String friendlyDescription;

    /**
     * The class name.
     */
    private String typeName;

    public LoggedObjectInformation() {
    }

    /**
     * Instantiates a new logged object information.
     *
     * @param order  the order
     * @param object the object
     */
    LoggedObjectInformation(
            final int order, final LogableObject object) {
        this.order = order;
        if (object instanceof SLNode) {
            final SLNode node = (SLNode) object;
            uniqueId = node.getID().replaceAll("\n", "").replaceAll("\t", "").replaceAll(" ", "");

            friendlyDescription = node.toString();
            typeName = node.getClass().getInterfaces()[0].getName();
        } else if (object instanceof ArtifactSource) {
            final ArtifactSource node = (ArtifactSource) object;
            friendlyDescription = node.getName();
            typeName = node.getClass().getName();
            uniqueId = null;
        } else if (object instanceof Artifact) {
            final Artifact node = (Artifact) object;
            friendlyDescription = node.getArtifactCompleteName();
            typeName = node.getClass().getName();
            uniqueId = null;
        } else {
            throw Exceptions.logAndReturn(new IllegalArgumentException());
        }
        Assertions.checkNotEmpty("friendlyDescription", friendlyDescription);
        Assertions.checkNotEmpty("className", typeName);
    }

    /**
     * Instantiates a new logged object information.
     *
     * @param order               the order
     * @param uniqueId            the unique id
     * @param className           the class name
     * @param friendlyDescription the friendly description
     */
    LoggedObjectInformation(
            final int order, final String uniqueId, final String className, final String friendlyDescription) {
        Assertions.checkNotEmpty("uniqueId", uniqueId);
        Assertions.checkNotEmpty("friendlyDescription", friendlyDescription);
        Assertions.checkNotEmpty("className", className);
        this.order = order;
        this.uniqueId = uniqueId;
        this.friendlyDescription = friendlyDescription;
        typeName = className;

    }

    public String getClassName() {
        return typeName;
    }

    /**
     * Gets the friendly description.
     *
     * @return the friendly description
     */
    public String getFriendlyDescription() {
        return friendlyDescription;
    }

    /**
     * Gets the order.
     *
     * @return the order
     */
    @KeyProperty
    public int getOrder() {
        return order;
    }

    /**
     * Gets the type name.
     *
     * @return the type name
     */
    @KeyProperty
    public String getTypeName() {
        return typeName;
    }

    /**
     * Gets the unique id.
     *
     * @return the unique id
     */
    @KeyProperty
    public String getUniqueId() {
        return uniqueId;
    }

    public void setClassName(final String className) {
        typeName = className;
    }

    public void setFriendlyDescription(final String friendlyDescription) {
        this.friendlyDescription = friendlyDescription;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }

    public void setUniqueId(final String uniqueId) {
        this.uniqueId = uniqueId;
    }

}