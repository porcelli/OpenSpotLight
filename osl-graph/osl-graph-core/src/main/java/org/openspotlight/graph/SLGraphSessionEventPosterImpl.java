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
import org.openspotlight.common.concurrent.LockContainer;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * The Class SLGraphSessionEventPosterImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLGraphSessionEventPosterImpl implements
        SLGraphSessionEventPoster, LockContainer {

	private final Lock lock;

	/** The listeners. */
	private final Collection<SLGraphSessionEventListener> listeners;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Instantiates a new sL graph session event poster impl.
	 * 
	 * @param listeners
	 *            the listeners
	 */
	SLGraphSessionEventPosterImpl(
			final Collection<SLGraphSessionEventListener> listeners,
			final LockContainer parent) {
		this.listeners = listeners;
		lock = parent.getLockObject();
	}

	// @Override
	public Lock getLockObject() {
		return lock;
	}

    /**
     * {@inheritDoc}
     */
	public void post(final SLGraphSessionEvent event) {
		synchronized (lock) {
			for (final SLGraphSessionEventListener listener : listeners) {
				if (event instanceof SLGraphSessionSaveEvent) {
					listener.beforeSave((SLGraphSessionSaveEvent) event);
				} else if (event instanceof SLLinkAddedEvent) {
					listener.linkAdded((SLLinkAddedEvent) event);
				} else if (event instanceof SLLinkRemovedEvent) {
					listener.linkRemoved((SLLinkRemovedEvent) event);
				} else if (event instanceof SLLinkPropertySetEvent) {
					listener.linkPropertySet((SLLinkPropertySetEvent) event);
				} else if (event instanceof SLNodeAddedEvent) {
					listener.nodeAdded((SLNodeAddedEvent) event);
				} else if (event instanceof SLNodePropertySetEvent) {
					if (logger.isDebugEnabled()) {
						logger.debug("Graph node property setted "
								+ ((SLNodePropertyEvent) event)
										.getPropertyName());
					}
					listener.nodePropertySet((SLNodePropertySetEvent) event);
				} else if (event instanceof SLNodePropertyRemovedEvent) {
					if (logger.isDebugEnabled()) {
						logger.debug("Graph property removed "
								+ ((SLNodePropertyEvent) event)
										.getPropertyName());
					}

					listener
							.nodePropertyRemoved((SLNodePropertyRemovedEvent) event);
				} else {
					throw Exceptions.logAndReturn(new IllegalArgumentException(
							"Unhandled event class"));
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void sessionCleaned() {
		synchronized (lock) {
			for (final SLGraphSessionEventListener listener : listeners) {
				listener.sessionCleaned();
			}
		}
	}
}
