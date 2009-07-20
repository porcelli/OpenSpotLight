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

package org.openspotlight.federation.data;

import java.io.Serializable;

/**
 * Interface to abstract the ConfigurationNode. The configuration node is used
 * on configuration purposes and also to load the artifacts that will be
 * processed by the parsers on OSL.
 * 
 * It has a lot of methods to make possible to discover each node characteristic
 * without using reflection.
 * 
 * This interface has some mandatory methods just to tell the parent and
 * children classes. This methods needs to be used inside the implementation to
 * verify if the parent classes passed on constructor or the children classes
 * are correct or not.
 * 
 * So, why do we need to to a lot of stuff like this instead of just using
 * simple java beans? There's a lot of good reasons to do that. First of all, it
 * contains the listener infrastructure to observe property and node changes.
 * Another reason is that the default implementation is by default thread safe.
 * As these configuration nodes should be used by a lot of parses at the same
 * time, it is mandatory to these classes to be thread safe.
 * 
 * There's a few mandatory conditions to the {@link ConfigurationNode} work
 * well. The first one is that the package should be the same of the current
 * root node. The second one is that the constructors parameters should be in a
 * order that the parent node is the first parameter and the key value is the
 * second one. The third condition is that the node should have an static
 * property named staticMetadata as an public attribute.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public interface ConfigurationNode extends Comparable<ConfigurationNode>,
        Serializable {
    
    /**
     * This instance metadata groups all data needed to save, retrieve and
     * listen modifications on a node.
     * 
     * @return the instance metadata
     */
    public InstanceMetadata getInstanceMetadata();
    
    /**
     * This static metadata defines the behavior of a given node.
     * 
     * @return type metadata
     */
    public StaticMetadata getStaticMetadata();
}
