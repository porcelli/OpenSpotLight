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
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

/**
 * The Class ArtifactMapping.
 */
@Name( "artifact_source_mapping" )
public class ArtifactSourceMapping implements SimpleNodeType, Serializable {

    private static final long        serialVersionUID = -242895748094958633L;

    /** The to. */
    private String                   to;

    /** The relative. */
    private String                   from;

    /** The source. */
    private transient ArtifactSource source;

    /** The excludeds. */
    private Set<String>              excludeds        = new HashSet<String>();

    /** The includeds. */
    private Set<String>              includeds        = new HashSet<String>();

    /** The hash code. */
    private volatile transient int   hashCode;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equalsTo(java.lang.Object)
     */
    public boolean equals( final Object o ) {
        if (!(o instanceof ArtifactSourceMapping)) {
            return false;
        }
        final ArtifactSourceMapping that = (ArtifactSourceMapping)o;
        final boolean result = Equals.eachEquality(Arrays.of(to, source, from), Arrays.andOf(that.to, that.source, that.from));
        return result;
    }

    /**
     * Gets the excludeds.
     * 
     * @return the excludeds
     */
    public synchronized Set<String> getExcludeds() {
        return excludeds;
    }

    /**
     * Gets the relative.
     * 
     * @return the relative
     */
    @KeyProperty
    public synchronized String getFrom() {
        return from;
    }

    /**
     * Gets the includeds.
     * 
     * @return the includeds
     */
    public synchronized Set<String> getIncludeds() {
        return includeds;
    }

    /**
     * Gets the source.
     * 
     * @return the source
     */
    @ParentProperty
    public synchronized ArtifactSource getSource() {
        return source;
    }

    /**
     * Gets the to.
     * 
     * @return the to
     */
    public synchronized String getTo() {
        return to;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public synchronized int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = HashCodes.hashOf(to, source, from);
            hashCode = result;
        }
        return result;
    }

    /**
     * Sets the excludeds.
     * 
     * @param excludeds the new excludeds
     */
    public synchronized void setExcludeds( final Set<String> excludeds ) {
        this.excludeds = excludeds;
    }

    /**
     * Sets the relative.
     * 
     * @param from the new relative
     */
    public synchronized void setFrom( final String from ) {
        this.from = from;
    }

    /**
     * Sets the includeds.
     * 
     * @param includeds the new includeds
     */
    public synchronized void setIncludeds( final Set<String> includeds ) {
        this.includeds = includeds;
    }

    /**
     * Sets the source.
     * 
     * @param source the new source
     */
    public synchronized void setSource( final ArtifactSource source ) {
        this.source = source;
    }

    /**
     * Sets the to.
     * 
     * @param to the new to
     */
    public synchronized void setTo( final String to ) {
        this.to = to;
    }

}
