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

import java.io.Serializable;

import org.openspotlight.common.concurrent.Lock;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

/**
 * The Class SLMetaRenderHintImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLMetaRenderHintImpl implements SLMetaRenderHint {

	private final Lock lock;

	/** The meta node. */
	private final SLMetaNodeType metaNode;

	/** The property. */
	private final SLPersistentProperty<Serializable> property;

	/**
	 * Instantiates a new sL meta render hint impl.
	 * 
	 * @param metaNode
	 *            the meta node
	 * @param property
	 *            the property
	 */
	SLMetaRenderHintImpl(final SLMetaNodeType metaNode,
			final SLPersistentProperty<Serializable> property) {
		this.metaNode = metaNode;
		this.property = property;
		lock = property.getLockObject();
	}

	public Lock getLockObject() {
		return lock;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetaElement#getMetadata()
	 */
	public SLMetadata getMetadata() throws SLGraphSessionException {
		return metaNode.getMetadata();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetaRenderHint#getMetaNode()
	 */
	public SLMetaNodeType getMetaNode() throws SLGraphSessionException {
		return metaNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetaRenderHint#getName()
	 */
	public String getName() throws SLGraphSessionException {
		synchronized (lock) {

			try {
				return SLCommonSupport.toSimplePropertyName(property.getName());
			} catch (final SLPersistentTreeSessionException e) {
				throw new SLGraphSessionException(
						"Error on attempt to retrieve render hint name.", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetaRenderHint#getValue()
	 */
	public String getValue() throws SLGraphSessionException {
		try {
			return property.getValue().toString();
		} catch (final SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException(
					"Error on attempt to retrieve render hint value.", e);
		}
	}

}
