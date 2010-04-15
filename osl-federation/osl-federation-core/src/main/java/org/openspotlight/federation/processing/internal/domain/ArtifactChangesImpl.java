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
package org.openspotlight.federation.processing.internal.domain;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.processing.ArtifactChanges;

/**
 * The Class ArtifactChangesImpl.
 */
public class ArtifactChangesImpl<T extends Artifact> implements
        ArtifactChanges<T> {

    /** The changed artifacts. */
    private Set<T> changedArtifacts    = new LinkedHashSet<T>();

    /** The excluded artifacts. */
    private Set<T> excludedArtifacts   = new LinkedHashSet<T>();

    /** The included artifacts. */
    private Set<T> includedArtifacts   = new LinkedHashSet<T>();

    /** The not changed artifacts. */
    private Set<T> notChangedArtifacts = new LinkedHashSet<T>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openspotlight.federation.processing.BundleProcessor.ArtifactChanges
     * #getChangedArtifacts()
     */
    public Set<T> getChangedArtifacts() {
        return this.changedArtifacts;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openspotlight.federation.processing.BundleProcessor.ArtifactChanges
     * #getExcludedArtifacts()
     */
    public Set<T> getExcludedArtifacts() {
        return this.excludedArtifacts;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openspotlight.federation.processing.BundleProcessor.ArtifactChanges
     * #getIncludedArtifacts()
     */
    public Set<T> getIncludedArtifacts() {
        return this.includedArtifacts;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openspotlight.federation.processing.BundleProcessor.ArtifactChanges
     * #getNotChangedArtifacts()
     */
    public Set<T> getNotChangedArtifacts() {
        return this.notChangedArtifacts;
    }

    /**
     * Sets the changed artifacts.
     * 
     * @param changedArtifacts the new changed artifacts
     */
    public void setChangedArtifacts( final Set<T> changedArtifacts ) {
        this.changedArtifacts = Collections.unmodifiableSet(changedArtifacts);
    }

    /**
     * Sets the excluded artifacts.
     * 
     * @param excludedArtifacts the new excluded artifacts
     */
    public void setExcludedArtifacts( final Set<T> excludedArtifacts ) {
        this.excludedArtifacts = Collections.unmodifiableSet(excludedArtifacts);
    }

    /**
     * Sets the included artifacts.
     * 
     * @param includedArtifacts the new included artifacts
     */
    public void setIncludedArtifacts( final Set<T> includedArtifacts ) {
        this.includedArtifacts = Collections.unmodifiableSet(includedArtifacts);
    }

    /**
     * Sets the not changed artifacts.
     * 
     * @param notChangedArtifacts the new not changed artifacts
     */
    public void setNotChangedArtifacts( final Set<T> notChangedArtifacts ) {
        this.notChangedArtifacts = Collections
                                              .unmodifiableSet(notChangedArtifacts);
    }
}
