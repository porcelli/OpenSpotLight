/**
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.federation.log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.domain.ArtifactSource;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.graph.Node;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.SimpleNodeType;

/**
 * The Class LoggedObjectInformation is used to represent objects related to a given log.
 */
@Name("logged_object_information")
public class LoggedObjectInformation implements SimpleNodeType, Serializable {

    /**
	 *
	 */
    private static final long serialVersionUID = 2812040814742711306L;

    /**
     * The friendly description.
     */
    private String            friendlyDescription;

    private int               order;

    /**
     * The class name.
     */
    private String            typeName;

    /**
     * The unique id.
     */
    private String            uniqueId;

    /**
     * Instantiates a new logged object information.
     * 
     * @param order the order
     * @param object the object
     */
    LoggedObjectInformation(final int order, final Object object) {
        this.order = order;
        if (object instanceof Node) {
            final Node node = (Node) object;
            uniqueId = node.getId();

            friendlyDescription = node.toString();
            typeName = node.getClass().getInterfaces()[0].getName();
        } else if (object instanceof ArtifactSource) {
            final ArtifactSource node = (ArtifactSource) object;
            friendlyDescription = node.getUrl();
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
     * @param order the order
     * @param uniqueId the unique id
     * @param className the class name
     * @param friendlyDescription the friendly description
     */
    LoggedObjectInformation(final int order, final String uniqueId,
                            final String className, final String friendlyDescription) {
        Assertions.checkNotEmpty("uniqueId", uniqueId);
        Assertions.checkNotEmpty("friendlyDescription", friendlyDescription);
        Assertions.checkNotEmpty("className", className);
        this.order = order;
        this.uniqueId = uniqueId;
        this.friendlyDescription = friendlyDescription;
        typeName = className;

    }

    public LoggedObjectInformation() {}

    /**
     * Gets the hierarchy from.
     * 
     * @param anotherNodes the another nodes
     * @return the hierarchy from
     */
    public static List<LoggedObjectInformation> getHierarchyFrom(
                                                                 final Object... anotherNodes) {
        final List<Object> nodes = new LinkedList<Object>();
        for (final Object o: anotherNodes) {
            nodes.add(o);
        }
        Collections.reverse(nodes);
        final List<LoggedObjectInformation> result = new ArrayList<LoggedObjectInformation>(
                nodes.size());
        for (int i = 0, size = nodes.size(); i < size; i++) {
            result.add(new LoggedObjectInformation(i, nodes.get(i)));
        }
        return result;
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
