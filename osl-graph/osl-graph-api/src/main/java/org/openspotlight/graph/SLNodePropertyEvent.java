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

import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;

/**
 * The Class SLNodePropertyEvent.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLNodePropertyEvent extends SLGraphSessionEvent {

	/** The Constant TYPE_NODE_PROPERTY_SET. */
	public static final int TYPE_NODE_PROPERTY_SET = 1;

	/** The Constant TYPE_NODE_PROPERTY_REMOVED. */
	public static final int TYPE_NODE_PROPERTY_REMOVED = 2;

	/** The property. */
	private final SLNodeProperty<? extends Serializable> property;

	/** The p property. */
	private final SLPersistentProperty<? extends Serializable> pProperty;

	/** The p node. */
	private SLPersistentNode pNode;

	/** The property name. */
	private String propertyName;

	/** The string. */
	private boolean string;

	/**
	 * Instantiates a new sL node property event.
	 * 
	 * @param type
	 *            the type
	 * @param property
	 *            the property
	 * @param pProperty
	 *            the property
	 */
	public SLNodePropertyEvent(final int type,
			final SLNodeProperty<? extends Serializable> property,
			final SLPersistentProperty<? extends Serializable> pProperty) {
		super(type, property.getNode().getSession());
		this.property = property;
		this.pProperty = pProperty;
	}

	/**
	 * Gets the persistent property.
	 * 
	 * @return the persistent property
	 */
	public SLPersistentProperty<? extends Serializable> getPersistentProperty() {
		return pProperty;
	}

	public SLPersistentNode getPNode() {
		synchronized (lock) {
			return pNode;
		}
	}

	/**
	 * Gets the property.
	 * 
	 * @return the property
	 */
	public SLNodeProperty<? extends Serializable> getProperty() {
		return property;
	}

	public String getPropertyName() {
		synchronized (lock) {
			return propertyName;
		}
	}

	public boolean isString() {
		synchronized (lock) {
			return string;
		}
	}

	public void setPNode(final SLPersistentNode node) {
		synchronized (lock) {
			pNode = node;
		}
	}

	public void setPropertyName(final String propertyName) {
		synchronized (lock) {
			this.propertyName = propertyName;
		}
	}

	public void setString(final boolean string) {
		synchronized (lock) {
			this.string = string;
		}
	}
}
