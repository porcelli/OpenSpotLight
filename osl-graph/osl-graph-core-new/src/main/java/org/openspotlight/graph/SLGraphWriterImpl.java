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

import static com.google.common.collect.Lists.newLinkedList;

import java.util.Collection;
import java.util.List;

import org.openspotlight.graph.internal.SLNodeFactory;
import org.openspotlight.graph.manipulation.SLGraphReader;
import org.openspotlight.graph.manipulation.SLGraphWriter;
import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STStorageSession;

import com.google.inject.Provider;

public class SLGraphWriterImpl implements SLGraphWriter {

    private final SLGraphReader              graphReader;
    private final Provider<STStorageSession> sessionProvider;
    private final STPartitionFactory         factory;
    private final String                     artifactId;
    private final List<SLNode>               dirtyNodes = newLinkedList();

    public SLGraphWriterImpl( STPartitionFactory factory,
                              Provider<STStorageSession> sessionProvider, String artifactId,
                              SLGraphReader graphReader ) {
        this.artifactId = artifactId;
        this.factory = factory;
        this.sessionProvider = sessionProvider;
        this.graphReader = graphReader;
    }

    @Override
    public <L extends SLLink> L createBidirectionalLink( Class<L> linkClass,
                                                         SLNode source,
                                                         SLNode target ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <L extends SLLink> L createLink( Class<L> linkClass,
                                            SLNode source,
                                            SLNode target ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends SLNode> T createNode( SLNode parent,
                                            Class<T> clazz,
                                            String name ) {
        return createNode(parent, clazz, name, null, null);
    }

    @Override
    public <T extends SLNode> T createNode( SLNode parent,
                                            Class<T> clazz,
                                            String name,
                                            Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
                                            Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion ) {
        STStorageSession session = sessionProvider.get();
        return SLNodeFactory.createNode(factory, session,
                                        parent.getContextId(), parent.getId(), clazz, name,
                                        linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion);
    }

    @Override
    public void removeContext( SLContext context ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeLink( SLLink link ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeNode( SLNode node ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save() {

    }

    @Override
    public void setContextCaption( SLContext context,
                                   String caption ) {
        throw new UnsupportedOperationException();
    }

}
