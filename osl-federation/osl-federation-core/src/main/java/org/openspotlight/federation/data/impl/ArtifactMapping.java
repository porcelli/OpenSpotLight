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

package org.openspotlight.federation.data.impl;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.federation.data.InstanceMetadata.Factory.createWithKeyProperty;

import java.util.Collection;
import java.util.Set;

import net.jcip.annotations.ThreadSafe;

import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.InstanceMetadata;
import org.openspotlight.federation.data.StaticMetadata;

/**
 * An artifact mapping represents a group of included and excluded paterns that
 * should be used to load and verify if an artifact belongs to this patterns.
 * This patterns of inclusion and exclusion are the same as used on apache ant.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("unchecked")
@ThreadSafe
@StaticMetadata(keyPropertyName = "relative", keyPropertyType = String.class, validParentTypes = {
        Bundle.class, Group.class }, validChildrenTypes = { Excluded.class,
        Included.class })
public final class ArtifactMapping implements ConfigurationNode {
    
    /**
     * 
     */
    private static final long serialVersionUID = 4945977241903059466L;
    
    private final InstanceMetadata instanceMetadata;
    
    /**
     * Creates an artifact mapping inside a bundle.
     * 
     * @param bundle
     * @param relative
     */
    public ArtifactMapping(final Bundle bundle, final String relative) {
        this.instanceMetadata = createWithKeyProperty(this, bundle, relative);
        checkCondition("noArtifactMapping", //$NON-NLS-1$
                bundle.getArtifactMappingByName(relative) == null);
        bundle.getInstanceMetadata().addChild(this);
    }
    
    /**
     * Creates an artifact mapping inside a project.
     * 
     * @param project
     * @param relative
     */
    public ArtifactMapping(final Group project, final String relative) {
        this.instanceMetadata = createWithKeyProperty(this, project, relative);
        checkCondition("noArtifactMapping", //$NON-NLS-1$
                project.getArtifactMappingByName(relative) == null);
        project.getInstanceMetadata().addChild(this);
        
    }
    
    /**
     * {@inheritDoc}
     */
    public final int compareTo(final ConfigurationNode o) {
        return this.instanceMetadata.compare(this, o);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object obj) {
        return this.instanceMetadata.equals(obj);
    }
    
    /**
     * 
     * @param name
     * @return a excluded by its name
     */
    public final Excluded getExcludedByName(final String name) {
        return this.instanceMetadata.getChildByKeyValue(Excluded.class, name);
    }
    
    /**
     * 
     * @return all valid names existing excludeds inside this repository
     */
    public final Set<String> getExcludedNames() {
        return (Set<String>) this.instanceMetadata
                .getKeyFromChildrenOfTypes(Excluded.class);
    }
    
    /**
     * 
     * @return all excludeds inside this repository
     */
    public final Collection<Excluded> getExcludeds() {
        return this.instanceMetadata.getChildrensOfType(Excluded.class);
    }
    
    /**
     * 
     * @param name
     * @return a included by its name
     */
    public final Included getIncludedByName(final String name) {
        return this.instanceMetadata.getChildByKeyValue(Included.class, name);
    }
    
    /**
     * 
     * @return all valid names existing includeds inside this repository
     */
    public final Set<String> getIncludedNames() {
        return (Set<String>) this.instanceMetadata
                .getKeyFromChildrenOfTypes(Included.class);
    }
    
    /**
     * 
     * @return all includeds inside this repository
     */
    public final Collection<Included> getIncludeds() {
        return this.instanceMetadata.getChildrensOfType(Included.class);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public final InstanceMetadata getInstanceMetadata() {
        return this.instanceMetadata;
    }
    
    /**
     * 
     * @return the relative initial path for this mapping.
     */
    public String getRelative() {
        return (String) this.instanceMetadata.getKeyPropertyValue();
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return this.instanceMetadata.hashCode();
    }
    
    /**
     * removes a given excluded from this repository.
     * 
     * @param excluded
     */
    public final void removeExcluded(final Excluded excluded) {
        this.instanceMetadata.removeChild(excluded);
    }
    
    /**
     * removes a given included from this repository.
     * 
     * @param included
     */
    public final void removeIncluded(final Included included) {
        this.instanceMetadata.removeChild(included);
    }
    
}
