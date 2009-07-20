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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openspotlight.federation.data.ConfigurationNode;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public final class Repository implements ConfigurationNode {
    
    /**
	 * 
	 */
    private static final long serialVersionUID = -3606246260530743008L;
    
    private static final String NUMBER_OF_PARALLEL_THREADS = "numberOfParallelThreads";
    
    private static final String ACTIVE = "active";
    
    @SuppressWarnings("unchecked")
    private static final Map<String, Class<?>> PROPERTY_TYPES = map(ofKeys(
            NUMBER_OF_PARALLEL_THREADS, ACTIVE), andValues(Integer.class,
            Boolean.class));
    
    private static final Set<Class<?>> CHILDREN_CLASSES = new HashSet<Class<?>>();
    
    static {
        CHILDREN_CLASSES.add(Project.class);
    }
    
    public Repository(final String name, final Configuration configuration) {
        super(name, configuration, PROPERTY_TYPES);
    }
    
    public void addProject(final Project project) {
        addChild(project);
    }
    
    public Boolean getActive() {
        return getNodeProperty(ACTIVE);
    }
    
    @Override
    public Set<Class<?>> getChildrenTypes() {
        return CHILDREN_CLASSES;
    }
    
    public Configuration getConfiguration() {
        return getParent();
    }
    
    public Integer getNumberOfParallelThreads() {
        return getNodeProperty(NUMBER_OF_PARALLEL_THREADS);
    }
    
    @Override
    public Class<?> getParentType() {
        return Configuration.class;
    }
    
    public Project getProjectByName(final String name) {
        return super.getChildByKeyValue(Project.class, name);
    }
    
    public Set<String> getProjectNames() {
        return super.getKeysFromChildrenOfType(Project.class);
    }
    
    public Collection<Project> getProjects() {
        return super.getChildrensOfType(Project.class);
    }
    
    public void removeProject(final Project project) {
        removeChild(project);
    }
    
    public void setActive(final Boolean active) {
        setNodeProperty(ACTIVE, active);
    }
    
    public void setNumberOfParallelThreads(final Integer numberOfParallelThreads) {
        setNodeProperty(NUMBER_OF_PARALLEL_THREADS, numberOfParallelThreads);
    }
    
}
