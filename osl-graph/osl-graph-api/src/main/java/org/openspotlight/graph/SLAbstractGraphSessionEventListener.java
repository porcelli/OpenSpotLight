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

/**
 * The listener interface for receiving SLAbstractGraphSessionEvent events. The
 * class that is interested in processing a SLAbstractGraphSessionEvent event
 * implements this interface, and the object created with that class is
 * registered with a component using the component's
 * <code>SLAbstractGraphSessionEventListener<code> method. When
 * the SLAbstractGraphSessionEventListener event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see SLAbstractGraphSessionEventListener
 */
public abstract class SLAbstractGraphSessionEventListener implements
		SLGraphSessionEventListener {

	protected Lock lock;

	protected SLAbstractGraphSessionEventListener(final LockContainer parent) {
		lock = parent.getLockObject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.graph.SLGraphSessionEventListener#beforeSave(org.
	 * openspotlight.graph.SLGraphSessionEvent)
	 */
	public void beforeSave(final SLGraphSessionSaveEvent event)
			throws SLGraphSessionException, SLInvalidCredentialException {
	}

	public Lock getLockObject() {
		return lock;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.graph.SLGraphSessionEventListener#linkAdded(org.
	 * openspotlight.graph.SLLinkEvent)
	 */
	public void linkAdded(final SLLinkAddedEvent event)
			throws SLGraphSessionException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.graph.SLGraphSessionEventListener#linkPropertySet(org
	 * .openspotlight.graph.SLLinkPropertyEvent)
	 */
	public void linkPropertySet(final SLLinkPropertySetEvent event)
			throws SLGraphSessionException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.graph.SLGraphSessionEventListener#linkRemoved(org.
	 * openspotlight.graph.SLLinkEvent)
	 */
	public void linkRemoved(final SLLinkRemovedEvent event)
			throws SLGraphSessionException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.graph.SLGraphSessionEventListener#nodeAdded(org.
	 * openspotlight.graph.SLNodeEvent)
	 */
	public void nodeAdded(final SLNodeAddedEvent event)
			throws SLGraphSessionException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.graph.SLGraphSessionEventListener#nodePropertyRemoved
	 * (org.openspotlight.graph.SLNodePropertyEvent)
	 */
	public void nodePropertyRemoved(final SLNodePropertyRemovedEvent event)
			throws SLGraphSessionException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.graph.SLGraphSessionEventListener#nodePropertySet(org
	 * .openspotlight.graph.SLNodePropertyEvent)
	 */
	public void nodePropertySet(final SLNodePropertySetEvent event)
			throws SLGraphSessionException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLGraphSessionEventListener#sessionCleaned()
	 */
	public void sessionCleaned() {
	}
}
