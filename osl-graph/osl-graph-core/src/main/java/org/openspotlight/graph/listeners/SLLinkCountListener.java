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
package org.openspotlight.graph.listeners;

import org.openspotlight.common.concurrent.LockContainer;
import org.openspotlight.graph.*;
import org.openspotlight.graph.event.SLAbstractGraphSessionEventListener;
import org.openspotlight.graph.event.SLLinkAddedEvent;
import org.openspotlight.graph.event.SLLinkRemovedEvent;
import org.openspotlight.graph.exception.SLGraphSessionException;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving SLLinkCount events. The class that is
 * interested in processing a SLLinkCount event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's <code>addSLLinkCountListener<code> method. When
 * the SLLinkCount event occurs, that object's appropriate
 * method is invoked.
 * 
 */
public class SLLinkCountListener extends SLAbstractGraphSessionEventListener {

	public SLLinkCountListener(final LockContainer parent) {
		super(parent);
	}

	/**
	 * Adds the source count.
	 * 
	 * @param pNode
	 *            the node
	 * @param linkType
	 *            the link type
	 * @param n
	 *            the n
	 * 
	 * @throws SLPersistentTreeSessionException
	 *             the SL persistent tree session exception
	 */
	private void addSourceCount(final SLPersistentNode pNode,
			final Class<? extends SLLink> linkType, final int n)
			throws SLPersistentTreeSessionException {
		synchronized (lock) {
			final String sourceLinkCountName = SLCommonSupport
					.toInternalPropertyName(SLConsts.PROPERTY_NAME_SOURCE_COUNT
							+ "." + linkType.getName().hashCode());
			final SLPersistentProperty<Integer> prop = SLCommonSupport
					.getProperty(pNode, Integer.class, sourceLinkCountName);
			Integer sourceLinkCount = prop == null ? null : prop.getValue();
			sourceLinkCount = sourceLinkCount == null ? 1 : sourceLinkCount + n;
			if (sourceLinkCount <= 0) {
				prop.remove();
			} else {
				pNode.setProperty(Integer.class, sourceLinkCountName,
						sourceLinkCount);
			}
		}
	}

	/**
	 * Adds the target count.
	 * 
	 * @param pNode
	 *            the node
	 * @param linkType
	 *            the link type
	 * @param n
	 *            the n
	 * 
	 * @throws SLPersistentTreeSessionException
	 *             the SL persistent tree session exception
	 */
	private void addTargetCount(final SLPersistentNode pNode,
			final Class<? extends SLLink> linkType, final int n)
			throws SLPersistentTreeSessionException {
		synchronized (lock) {
			final String targetLinkCountName = SLCommonSupport
					.toInternalPropertyName(SLConsts.PROPERTY_NAME_TARGET_COUNT
							+ "." + linkType.getName().hashCode());
			final SLPersistentProperty<Integer> prop = SLCommonSupport
					.getProperty(pNode, Integer.class, targetLinkCountName);
			Integer targetLinkCount = prop == null ? null : prop.getValue();
			targetLinkCount = targetLinkCount == null ? 1 : targetLinkCount + n;
			if (targetLinkCount <= 0) {
				prop.remove();
			} else {
				pNode.setProperty(Integer.class, targetLinkCountName,
						targetLinkCount);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void linkAdded(final SLLinkAddedEvent event) {
		synchronized (lock) {
			try {
				final SLLink link = event.getLink();
				final Class<? extends SLLink> linkType = SLCommonSupport
						.getLinkType(link);
				final SLPersistentNode sourceNode = SLCommonSupport
						.getPNode(event.getLink().isBidirectional() ? event
								.getSides()[0] : event.getSource());
				final SLPersistentNode targetNode = SLCommonSupport
						.getPNode(event.getLink().isBidirectional() ? event
								.getSides()[1] : event.getTarget());
				if (event.isNewLink()) {
					if (link.isBidirectional()) {
						addSourceCount(sourceNode, linkType, 1);
						addSourceCount(targetNode, linkType, 1);
						addTargetCount(sourceNode, linkType, 1);
						addTargetCount(targetNode, linkType, 1);
					} else {
						addSourceCount(sourceNode, linkType, 1);
						addTargetCount(targetNode, linkType, 1);
					}
				} else if (event.isChangedToBidirectional()) {
					addSourceCount(sourceNode, linkType, 1);
					addTargetCount(targetNode, linkType, 1);
				}
			} catch (final SLPersistentTreeSessionException e) {
				throw new SLGraphSessionException(
						"Error on attempt to add link count data.", e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
    @Override
	public void linkRemoved(final SLLinkRemovedEvent event) {
		synchronized (lock) {
			try {
				final SLLink link = event.getLink();
				final Class<? extends SLLink> linkType = link.getLinkType();
				if (event.isBidirectional()) {
					final SLPersistentNode sideNode1 = SLCommonSupport
							.getPNode(event.getSides()[0]);
					final SLPersistentNode sideNode2 = SLCommonSupport
							.getPNode(event.getSides()[1]);
					addSourceCount(sideNode1, linkType, -1);
					addSourceCount(sideNode2, linkType, -1);
					addTargetCount(sideNode1, linkType, -1);
					addTargetCount(sideNode2, linkType, -1);
				} else {
					final SLPersistentNode sourceNode = SLCommonSupport
							.getPNode(event.getSource());
					final SLPersistentNode targetNode = SLCommonSupport
							.getPNode(event.getTarget());
					addSourceCount(sourceNode, linkType, -1);
					addTargetCount(targetNode, linkType, -1);
				}
			} catch (final SLPersistentTreeSessionException e) {
				throw new SLGraphSessionException(
						"Error on attempt to remove link count data.", e);
			}
		}
	}
}
