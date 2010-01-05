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

import java.util.Collection;

import org.openspotlight.graph.persistence.SLPersistentNode;

/**
 * The Class SLNodeEvent.
 * 
 * @author Vitor Hugo Chagas
 */
public abstract class SLNodeEvent extends SLGraphSessionEvent {

	/** The node. */
	private final SLNode node;

	/** The p node. */
	private final SLPersistentNode pNode;

	/** The persistent mode. */
	private final SLPersistenceMode persistentMode;

	/** The link types for link deletion. */
	private final Collection<Class<? extends SLLink>> linkTypesForLinkDeletion;

	/** The link types for linked node deletion. */
	private final Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion;

	/**
	 * Instantiates a new sL node event.
	 * 
	 * @param type
	 *            the type
	 * @param node
	 *            the node
	 * @param pNode
	 *            the node
	 * @param persistentMode
	 *            the persistent mode
	 * @param linkTypesForLinkDeletion
	 *            the link types for link deletion
	 * @param linkTypesForLinkedNodeDeletion
	 *            the link types for linked node deletion
	 */
	public SLNodeEvent(
			final SLNode node,
			final SLPersistentNode pNode,
			final SLPersistenceMode persistentMode,
			final Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
			final Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion) {
		super(node.getSession());
		this.node = node;
		this.pNode = pNode;
		this.persistentMode = persistentMode;
		this.linkTypesForLinkDeletion = linkTypesForLinkDeletion;
		this.linkTypesForLinkedNodeDeletion = linkTypesForLinkedNodeDeletion;
	}

	/**
	 * Gets the link types for link deletion.
	 * 
	 * @return the link types for link deletion
	 */
	public Collection<Class<? extends SLLink>> getLinkTypesForLinkDeletion() {
		return linkTypesForLinkDeletion;
	}

	/**
	 * Gets the link types for linked nodes deletion.
	 * 
	 * @return the link types for linked nodes deletion
	 */
	public Collection<Class<? extends SLLink>> getLinkTypesForLinkedNodesDeletion() {
		return linkTypesForLinkedNodeDeletion;
	}

	/**
	 * Gets the node.
	 * 
	 * @return the node
	 */
	public SLNode getNode() {
		return node;
	}

	/**
	 * Gets the persistence mode.
	 * 
	 * @return the persistence mode
	 */
	public SLPersistenceMode getPersistenceMode() {
		return persistentMode;
	}

	/**
	 * Gets the persistent node.
	 * 
	 * @return the persistent node
	 */
	public SLPersistentNode getPersistentNode() {
		return pNode;
	}
}
