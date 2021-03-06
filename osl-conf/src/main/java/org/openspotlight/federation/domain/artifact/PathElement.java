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
package org.openspotlight.federation.domain.artifact;

import java.io.Serializable;
import java.util.StringTokenizer;

import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.Strings;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.TransientProperty;

/**
 * The Class PathElement.
 */
public class PathElement implements Comparable<PathElement>, Serializable {

    private static final long         serialVersionUID  = -6520096568789344933L;

    private volatile transient String completePathCache = null;

    /** The hashcode. */
    private volatile transient int    hashcode;

    /** The name. */
    private String                    name;

    /** The parent. */
    private PathElement               parent;

    public PathElement() {

    }

    public PathElement(
                        final String name, final PathElement parent) {
        this.name = name;
        this.parent = parent;
    }

    /**
     * Creates the from path string.
     * 
     * @param pathString the path string
     * @return the path element
     */
    public static PathElement createFromPathString(final String pathString) {
        Assertions.checkNotEmpty("pathString", pathString);
        final StringTokenizer tok = new StringTokenizer(pathString, "/");

        if (!tok.hasMoreTokens()) { return null; }

        PathElement lastPath = new PathElement();
        lastPath.setName(tok.nextToken());
        while (tok.hasMoreTokens()) {
            final String nextToken = tok.nextToken();
            if (nextToken.equals(".")) {
                continue;
            }
            if (nextToken.equals("..")) {
                lastPath = lastPath.getParent();
                continue;
            }
            final PathElement newPath = new PathElement(nextToken, lastPath);
            lastPath = newPath;
        }
        return lastPath;

    }

    public static PathElement createRelativePath(final PathElement initialPathElement,
                                                  final String pathString) {
        final String newPathString = pathString.startsWith("/") ? Strings.removeBegginingFrom("/", pathString) : pathString;

        return createFromPathString(initialPathElement.getCompletePath() + Artifact.SEPARATOR + newPathString);

    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final PathElement o) {
        return getCompletePath().compareTo(o.getCompletePath());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equalsTo(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof PathElement)) { return false; }
        final PathElement that = (PathElement) o;
        return Equals.eachEquality(name, that.name) && Equals.eachEquality(parent, that.parent);

    }

    /**
     * Gets the complete path.
     * 
     * @return the complete path
     */
    public String getCompletePath() {
        String path = completePathCache;
        if (path == null) {
            if (isRootElement()) {
                path = "/" + name;
            } else {
                path = getParent().getCompletePath() + Artifact.SEPARATOR + getName();
            }
            completePathCache = path;
        }
        return path;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    @KeyProperty
    public String getName() {
        return name;
    }

    /**
     * Gets the parent.
     * 
     * @return the parent
     */
    @ParentProperty
    public PathElement getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = hashcode;
        if (result == 0) {
            result = 17;
            result = 31 * result + (parent != null ? parent.hashCode() : 0);
            result = 31 * result + (name != null ? name.hashCode() : 0);
            hashcode = result;
        }
        return result;
    }

    /**
     * Checks if is root element.
     * 
     * @return true, if is root element
     */
    @TransientProperty
    public boolean isRootElement() {
        return parent == null;
    }

    public void setCompletePath(final String s) {

    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setParent(final PathElement parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "PathElement: " + getCompletePath();
    }

}
