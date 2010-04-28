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

import org.openspotlight.common.concurrent.Lock;
import org.openspotlight.graph.event.SLGraphSessionEventPoster;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.util.ProxyUtil;

/**
 * The Class SLContextImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLContextImpl implements SLContext {

    /** The Constant serialVersionUID. */
    private static final long    serialVersionUID = 1L;

    private final Lock           lock;

    /** The session. */
    private final SLGraphSession session;

    /** The root node. */
    private final SLNode         rootNode;

    /**
     * Instantiates a new sL context impl.
     * 
     * @param session the session
     * @param contextRootPersistentNode the context root persistent node
     * @param eventPoster the event poster
     */
    public SLContextImpl(
                          final SLGraphSession session, final SLPersistentNode contextRootPersistentNode,
                          final SLGraphSessionEventPoster eventPoster ) {
        this.session = session;
        rootNode = new SLNodeImpl(this, null, contextRootPersistentNode, eventPoster);
        lock = session.getLockObject();
    }

    /**
     * {@inheritDoc}
     */
    public String getID() {
        return rootNode.getName();
    }

    /**
     * {@inheritDoc}
     */
    public String getCaption() {
        return rootNode.getCaption();
    }

    /**
     * {@inheritDoc}
     */
    public void setCaption( String caption ) {
        rootNode.setCaption(caption);
    }

    public Lock getLockObject() {
        return lock;
    }

    /**
     * {@inheritDoc}
     */
    public SLNode getRootNode() {
        synchronized (lock) {
            return ProxyUtil.createNodeProxy(SLNode.class, rootNode);
        }

    }

    /**
     * {@inheritDoc}
     */
    public SLGraphSession getSession() {
        return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( final Object obj ) {
        synchronized (lock) {
            if (!(obj instanceof SLContext)) {
                return false;
            }
            final SLContext context = (SLContext)obj;
            return getID().equals(context.getID());
        }
    }
}
