/*
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
package org.openspotlight.federation.domain;

import java.io.Serializable;
import java.util.StringTokenizer;

import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.common.util.Strings;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

/**
 * The Class PathElement.
 */
@Name( "path_element" )
public class PathElement implements Comparable<PathElement>, SimpleNodeType, Serializable {

    private static final long serialVersionUID = -6520096568789344933L;

    /**
     * Creates the from path string.
     * 
     * @param pathString the path string
     * @return the path element
     */
    public static PathElement createFromPathString( final String pathString ) {
        Assertions.checkNotEmpty("pathString", pathString);
        final StringTokenizer tok = new StringTokenizer(pathString, "/");
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
            final PathElement newPath = new PathElement();
            newPath.setParent(lastPath);
            newPath.setName(nextToken);
            lastPath = newPath;
        }
        return lastPath;

    }

    public static PathElement createRelativePath( final PathElement initialPathElement,
                                                  final String pathString ) {
        final String newPathString = pathString.startsWith("/") ? Strings.removeBegginingFrom("/", pathString) : pathString;

        return createFromPathString(initialPathElement.getCompletePath() + Artifact.SEPARATOR + newPathString);

    }

    /** The name. */
    private String       name;

    /** The parent. */
    private PathElement  parent;

    /** The hashcode. */
    private volatile int hashcode;

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo( final PathElement o ) {
        return this.getCompletePath().compareTo(o.getCompletePath());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object o ) {
        if (!(o instanceof PathElement)) {
            return false;
        }
        final PathElement that = (PathElement)o;
        final boolean response = Equals.eachEquality(this.getCompletePath(), that.getCompletePath());
        return response;
    }

    /**
     * Gets the complete path.
     * 
     * @return the complete path
     */
    public String getCompletePath() {
        if (this.isRootElement()) {
            return "/" + this.getName();
        }
        return this.getParent().getCompletePath() + Artifact.SEPARATOR + this.getName();
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    @KeyProperty
    public String getName() {
        return this.name;
    }

    /**
     * Gets the parent.
     * 
     * @return the parent
     */
    @ParentProperty
    public PathElement getParent() {
        return this.parent;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = this.hashcode;
        if (result == 0) {
            result = HashCodes.hashOf(this.getCompletePath());
            this.hashcode = result;
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
        return this.parent == null;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public void setParent( final PathElement parent ) {
        this.parent = parent;
    }

    public String toString() {
        return "PathElement: " + this.getCompletePath();
    }

}
