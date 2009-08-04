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

import static org.openspotlight.federation.data.InstanceMetadata.Factory.createRoot;

import java.util.Collection;
import java.util.Set;

import net.jcip.annotations.ThreadSafe;

import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.InstanceMetadata;
import org.openspotlight.federation.data.StaticMetadata;

/**
 * This is the root node of the configuration classes that contains the
 * following structure. FIXME re-make javadoc
 * 
 * This structure are used to pass the artifacts to the parser. All the classes
 * are thread save by default.
 * 
 * @see ConfigurationNode
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("unchecked")
@ThreadSafe
@StaticMetadata(validChildrenTypes = Repository.class, propertyNames = "numberOfParallelThreads", propertyTypes = Integer.class)
public final class Configuration implements ConfigurationNode {
    
    /**
     * 
     */
    private static final long serialVersionUID = -5615522050633506216L;
    
    private final InstanceMetadata instanceMetadata;
    
    private static final String NUMBER_OF_PARALLEL_THREADS = "numberOfParallelThreads"; //$NON-NLS-1$
    
    /**
     * Default constructor
     */
    public Configuration() {
        this.instanceMetadata = createRoot(this);
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
     * {@inheritDoc}
     */
    public final InstanceMetadata getInstanceMetadata() {
        return this.instanceMetadata;
    }
    
    /**
     * 
     * @return the number of parallel threads for this repository processing
     */
    public final Integer getNumberOfParallelThreads() {
        return this.instanceMetadata.getProperty(NUMBER_OF_PARALLEL_THREADS);
    }
    
    /**
     * 
     * @return all repositories
     */
    public final Collection<Repository> getRepositories() {
        return this.instanceMetadata.getChildrensOfType(Repository.class);
    }
    
    /**
     * Returns a repository by its name.
     * 
     * @param name
     * @return a repository
     */
    public final Repository getRepositoryByName(final String name) {
        return this.instanceMetadata.getChildByKeyValue(Repository.class, name);
    }
    
    /**
     * 
     * @return all repository names
     */
    public final Set<String> getRepositoryNames() {
        return (Set<String>) this.instanceMetadata
                .getKeyFromChildrenOfTypes(Repository.class);
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
     * Removes a given repository
     * 
     * @param repository
     */
    public final void removeRepository(final Repository repository) {
        this.instanceMetadata.removeChild(repository);
    }
    
    /**
     * Sets the number of parallel threads.
     * 
     * @param numberOfParallelThreads
     */
    public final void setNumberOfParallelThreads(
            final Integer numberOfParallelThreads) {
        this.instanceMetadata.setProperty(NUMBER_OF_PARALLEL_THREADS,
                numberOfParallelThreads);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return this.instanceMetadata.toString();
    }
    
}
