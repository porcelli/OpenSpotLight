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
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;

/**
 * The Class BundleSource.
 */
@Name( "bundle_source" )
public class BundleSource implements SimpleNodeType, Serializable {

    private static final long             serialVersionUID = -5266436076638737597L;

    /** The relative. */
    private String                        relative;

    /** The source. */
    private transient BundleProcessorType bundleProcessorType;

    /** The excludeds. */
    private Set<String>                   excludeds        = new HashSet<String>();

    /** The includeds. */
    private Set<String>                   includeds        = new HashSet<String>();

    /** The hash code. */
    private volatile transient int        hashCode;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( final Object o ) {
        if (!(o instanceof BundleSource)) {
            return false;
        }
        final BundleSource that = (BundleSource)o;
        final boolean result = Equals.eachEquality(Arrays.of(bundleProcessorType, relative),
                                                   Arrays.andOf(that.bundleProcessorType, that.relative));
        return result;
    }

    /**
     * Gets the bundle processor type.
     * 
     * @return the bundle processor type
     */
    @ParentProperty
    public BundleProcessorType getBundleProcessorType() {
        return bundleProcessorType;
    }

    /**
     * Gets the excludeds.
     * 
     * @return the excludeds
     */
    public Set<String> getExcludeds() {
        return excludeds;
    }

    /**
     * Gets the includeds.
     * 
     * @return the includeds
     */
    public Set<String> getIncludeds() {
        return includeds;
    }

    /**
     * Gets the relative.
     * 
     * @return the relative
     */
    @KeyProperty
    public String getRelative() {
        return relative;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = HashCodes.hashOf(bundleProcessorType, relative);
            hashCode = result;
        }
        return result;
    }

    /**
     * Sets the bundle processor type.
     * 
     * @param bundleProcessorType the new bundle processor type
     */
    public void setBundleProcessorType( final BundleProcessorType bundleProcessorType ) {
        this.bundleProcessorType = bundleProcessorType;
    }

    /**
     * Sets the excludeds.
     * 
     * @param excludeds the new excludeds
     */
    public void setExcludeds( final Set<String> excludeds ) {
        this.excludeds = excludeds;
    }

    /**
     * Sets the includeds.
     * 
     * @param includeds the new includeds
     */
    public void setIncludeds( final Set<String> includeds ) {
        this.includeds = includeds;
    }

    /**
     * Sets the relative.
     * 
     * @param relative the new relative
     */
    public void setRelative( final String relative ) {
        this.relative = relative;
    }

}