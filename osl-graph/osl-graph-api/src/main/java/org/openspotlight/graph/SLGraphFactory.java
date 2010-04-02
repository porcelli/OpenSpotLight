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
package org.openspotlight.graph;

import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.event.SLGraphSessionEventPoster;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.security.authz.PolicyEnforcement;
import org.openspotlight.security.idm.AuthenticatedUser;

import java.io.Serializable;

/**
 * A factory for creating SLGraph objects.
 */
public abstract class SLGraphFactory extends AbstractFactory {

    /**
     * Creates a new SLGraph object.
     * 
     * @param descriptor the descriptor
     * @return the SL graph
     * @throws org.openspotlight.common.exception.ConfigurationException configuration exception
     */
    public abstract SLGraph createGraph( final JcrConnectionDescriptor descriptor );

    /**
     * Creates the graph session.
     * 
     * @param treeSession the tree session
     * @param user the user
     * @param policyEnforcement the policy enforcement
     * @return the SL graph session
     */
    abstract SLGraphSession createGraphSession( SLPersistentTreeSession treeSession,
                                                PolicyEnforcement policyEnforcement,
                                                AuthenticatedUser user );

    /**
     * Creates a new SLGraph object.
     * 
     * @param context the context
     * @param parent the parent
     * @param persistentNode the persistent node
     * @param eventPoster the event poster
     * @return the SL node
     */
    abstract SLNode createNode( SLContext context,
                                SLNode parent,
                                SLPersistentNode persistentNode,
                                SLGraphSessionEventPoster eventPoster );

    /**
     * Creates a new SLGraph object.
     * 
     * @param node the node
     * @param persistentProperty the persistent property
     * @param eventPoster the event poster
     * @return the SL node property< v>
     */
    abstract <V extends Serializable> SLNodeProperty<V> createProperty( SLNode node,
                                                                        SLPersistentProperty<V> persistentProperty,
                                                                        SLGraphSessionEventPoster eventPoster );

}
