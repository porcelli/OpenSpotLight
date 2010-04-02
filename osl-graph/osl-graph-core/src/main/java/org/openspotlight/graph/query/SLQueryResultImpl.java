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
package org.openspotlight.graph.query;

import org.openspotlight.common.concurrent.Lock;
import org.openspotlight.common.concurrent.LockContainer;
import org.openspotlight.common.concurrent.LockedCollections;
import org.openspotlight.common.concurrent.NeedsSyncronizationList;
import org.openspotlight.graph.SLNode;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class SLQueryResultImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLQueryResultImpl implements SLQueryResult {
	private final Lock lockObject;
	/** The nodes. */
	private final NeedsSyncronizationList<SLNode> nodes;

	/** The query id. */
	private final String queryId;

	/**
	 * Instantiates a new sL query result impl.
	 * 
	 * @param nodes
	 *            the nodes
	 */
	public SLQueryResultImpl(final LockContainer parent,
			final List<SLNode> nodes, final String queryId) {
		lockObject = parent.getLockObject();
		this.queryId = queryId;
		if (nodes == null) {
			this.nodes = LockedCollections.createListWithLock(this,
					new ArrayList<SLNode>(0));
		} else {
			this.nodes = LockedCollections.createListWithLock(this, nodes);
		}
	}

	/**
	 * Instantiates a new sL query result impl.
	 * 
	 * @param nodes
	 *            the nodes
	 */
	public SLQueryResultImpl(final LockContainer parent, final SLNode[] nodes,
			final String queryId) {
		lockObject = parent.getLockObject();
		this.queryId = queryId;
		if (nodes == null) {
			this.nodes = LockedCollections.createListWithLock(this,
					new ArrayList<SLNode>(0));
		} else {
			this.nodes = LockedCollections.createListWithLock(this,
					new ArrayList<SLNode>(nodes.length));
			for (final SLNode slNode : nodes) {
				this.nodes.add(slNode);
			}
		}
	}

	public Lock getLockObject() {
		return lockObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.query.SLQueryResult#getNodes()
	 */
	public NeedsSyncronizationList<SLNode> getNodes() throws SLQueryException {
		return nodes;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getQueryId() {
		return queryId;
	}
}
