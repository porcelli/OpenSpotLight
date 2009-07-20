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

import static org.openspotlight.common.util.Arrays.andValues;
import static org.openspotlight.common.util.Arrays.map;
import static org.openspotlight.common.util.Arrays.ofKeys;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openspotlight.federation.data.AbstractConfigurationNode;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public final class Bundle extends AbstractConfigurationNode {
    
    /**
	 * 
	 */
    private static final long serialVersionUID = 1092283780730455977L;
    
    private static final String ACTIVE = "active";
    
    private static final String TYPE = "type";
    
    private static final String INITIAL_LOOKUP = "initialLookup";
    
    @SuppressWarnings("unchecked")
    private static final Map<String, Class<?>> PROPERTY_TYPES = map(ofKeys(
            ACTIVE, TYPE, INITIAL_LOOKUP), andValues(Boolean.class,
            String.class, String.class));
    
    private static final Set<Class<?>> CHILDREN_CLASSES = new HashSet<Class<?>>();
    
    static {
        CHILDREN_CLASSES.add(ArtifactMapping.class);
        CHILDREN_CLASSES.add(Artifact.class);
    }
    
    public Bundle(final String name, final Project project) {
        super(name, project, PROPERTY_TYPES);
    }
    
    public void addArtifact(final Artifact Artifact) {
        this.addChild(Artifact);
    }
    
    public Artifact addArtifact(final String ArtifactName) {
        checkNotEmpty("ArtifactName", ArtifactName);
        Artifact Artifact = this.getArtifactByName(ArtifactName);
        if (Artifact != null) {
            return Artifact;
        }
        Artifact = new Artifact(ArtifactName, this);
        return Artifact;
    }
    
    public void addArtifactMapping(final ArtifactMapping Artifact) {
        this.addChild(Artifact);
    }
    
    public Boolean getActive() {
        return this.getProperty(ACTIVE);
    }
    
    public Artifact getArtifactByName(final String name) {
        return super.getChildByName(Artifact.class, name);
    }
    
    public ArtifactMapping getArtifactMappingByName(final String name) {
        return super.getChildByName(ArtifactMapping.class, name);
    }
    
    public Set<String> getArtifactMappingNames() {
        return super.getKeysFromChildrenOfType(ArtifactMapping.class);
    }
    
    public Collection<ArtifactMapping> getArtifactMappings() {
        return super.getChildrensOfType(ArtifactMapping.class);
    }
    
    public Set<String> getArtifactNames() {
        return super.getKeysFromChildrenOfType(Artifact.class);
    }
    
    public Collection<Artifact> getArtifacts() {
        return super.getChildrensOfType(Artifact.class);
    }
    
    @Override
    public Set<Class<?>> getChildrenTypes() {
        return CHILDREN_CLASSES;
    }
    
    public String getInitialLookup() {
        return this.getProperty(INITIAL_LOOKUP);
    }
    
    @Override
    public Class<?> getParentType() {
        return Project.class;
    }
    
    public Project getProject() {
        return this.getParent();
    }
    
    public String getType() {
        return this.getProperty(TYPE);
    }
    
    public void removeArtifact(final Artifact Artifact) {
        this.removeChild(Artifact);
    }
    
    public void removeArtifactMapping(final ArtifactMapping Artifact) {
        this.removeChild(Artifact);
    }
    
    public void setActive(final Boolean active) {
        this.setProperty(ACTIVE, active);
    }
    
    public void setInitialLookup(final String initialLookup) {
        this.setProperty(INITIAL_LOOKUP, initialLookup);
    }
    
    public void setType(final String type) {
        this.setProperty(TYPE, type);
    }
    
}
