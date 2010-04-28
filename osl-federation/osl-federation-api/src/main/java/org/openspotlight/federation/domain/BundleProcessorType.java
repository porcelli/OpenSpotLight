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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.BundleProcessorGlobalPhase;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

/**
 * The Class BundleProcessorType.
 */
@Name( "bundle_processor_type" )
public class BundleProcessorType implements SimpleNodeType, Serializable {

    private volatile transient String                                       uniqueName       = null;

    private String                                                          name;

    private Map<String, String>                                             bundleProperties = new HashMap<String, String>();

    private static final long                                               serialVersionUID = -8305990807194729295L;

    /** The type. */
    private Class<? extends BundleProcessorGlobalPhase<? extends Artifact>> globalPhase;

    private List<Class<? extends BundleProcessorArtifactPhase<?>>>          artifactPhases   = new ArrayList<Class<? extends BundleProcessorArtifactPhase<?>>>();

    /** The active. */
    private boolean                                                         active;

    /** The group. */
    private transient Group                                                 group;

    /** The sources. */
    private Set<BundleSource>                                               sources          = new HashSet<BundleSource>();

    /** The hash code. */
    private volatile transient int                                          hashCode;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( final Object o ) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BundleProcessorType)) {
            return false;
        }
        final BundleProcessorType that = (BundleProcessorType)o;
        final boolean result = Equals.eachEquality(Arrays.of(group, globalPhase, name), Arrays.andOf(that.group,
                                                                                                     that.globalPhase, that.name));
        return result;
    }

    public List<Class<? extends BundleProcessorArtifactPhase<?>>> getArtifactPhases() {
        return artifactPhases;
    }

    public Map<String, String> getBundleProperties() {
        return bundleProperties;
    }

    @KeyProperty
    public Class<? extends BundleProcessorGlobalPhase<? extends Artifact>> getGlobalPhase() {
        return globalPhase;
    }

    /**
     * Gets the artifact source.
     * 
     * @return the artifact source
     */
    @ParentProperty
    public Group getGroup() {
        return group;
    }

    @KeyProperty
    public String getName() {
        return name;
    }

    /**
     * Gets the sources.
     * 
     * @return the sources
     */
    public Set<BundleSource> getSources() {
        return sources;
    }

    @TransientProperty
    public String getUniqueName() {
        String temp = uniqueName;
        if (temp == null) {
            temp = getGroup().getUniqueName() + "/" + getName();
            uniqueName = temp;
        }
        return temp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = HashCodes.hashOf(group, globalPhase, name);
            hashCode = result;
        }
        return result;
    }

    /**
     * Checks if is active.
     * 
     * @return true, if is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active.
     * 
     * @param active the new active
     */
    public void setActive( final boolean active ) {
        this.active = active;
    }

    public void setArtifactPhases( final List<Class<? extends BundleProcessorArtifactPhase<?>>> artifactPhases ) {
        this.artifactPhases = artifactPhases;
    }

    public void setBundleProperties( final Map<String, String> bundleProperties ) {
        this.bundleProperties = bundleProperties;
    }

    public void setGlobalPhase( final Class<? extends BundleProcessorGlobalPhase<? extends Artifact>> globalPhase ) {
        this.globalPhase = globalPhase;
    }

    /**
     * Sets the group.
     * 
     * @param group the new group
     */
    public void setGroup( final Group group ) {
        this.group = group;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    /**
     * Sets the sources.
     * 
     * @param sources the new sources
     */
    public void setSources( final Set<BundleSource> sources ) {
        this.sources = sources;
    }

}
