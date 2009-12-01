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

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.graph.persistence.SLInvalidPersistentPropertyTypeException;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

/**
 * The Class SLNodePropertyImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLNodePropertyImpl<V extends Serializable> implements SLNodeProperty<V> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The node. */
	private SLNode node;
	
	/** The persistent property. */
	private SLPersistentProperty<V> pProperty;
	
	/** The event poster. */
	private SLGraphSessionEventPoster eventPoster;
	
	/**
	 * Instantiates a new sL node property impl.
	 * 
	 * @param node the node
	 * @param persistentProperty the persistent property
	 */
	public SLNodePropertyImpl(SLNode node, SLPersistentProperty<V> persistentProperty, SLGraphSessionEventPoster eventPoster) {
		this.node = node;
		this.pProperty = persistentProperty;
		this.eventPoster = eventPoster;
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLNodeProperty#getNode()
	 */
	public SLNode getNode() {
		return node;
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLNodeProperty#getName()
	 */
	public String getName() throws SLGraphSessionException {
		try {
			return SLCommonSupport.toSimplePropertyName(pProperty.getName());
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve the property name.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLNodeProperty#getValue()
	 */
	public V getValue() throws SLInvalidNodePropertyTypeException, SLGraphSessionException {
		try {
			return pProperty.getValue();
		}
		catch (SLInvalidPersistentPropertyTypeException e) {
			throw new SLInvalidNodePropertyTypeException(e);
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve the property value.", e);
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLNodeProperty#getValueAsString()
	 */
	public String getValueAsString() throws SLGraphSessionException {
		return getValue().toString();
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLNodeProperty#setValue(java.io.Serializable)
	 */
	public void setValue(V value) throws SLGraphSessionException {
		try {
			pProperty.setValue(value);
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to set the property value.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLNodeProperty#remove()
	 */
	public void remove() throws SLGraphSessionException {
		try {
			SLPersistentNode pNode = pProperty.getNode();
			String name = SLCommonSupport.toSimplePropertyName(getName());
			boolean string = pProperty.getValue() instanceof String;
			pProperty.remove();
			SLNodePropertyEvent event = new SLNodePropertyEvent(SLNodePropertyEvent.TYPE_NODE_PROPERTY_REMOVED, this, pProperty);
			event.setPropertyName(name);
			event.setString(string);
			event.setPNode(pNode);
			eventPoster.post(event);
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to remove property.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	//@Override
	public boolean equals(Object obj) {
		try {
			if (obj == null) return false;
			SLNodeProperty property = (SLNodeProperty) obj;
			String name1 = property.getNode().getID() + ":" + getName();
			String name2 = getNode().getID() + ":" + getName();
			return name1.equals(name2);
		}
		catch (SLGraphSessionException e) {
			throw new SLRuntimeException("Error on attempt to execute property equals method.");
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		try {
			return (getNode().getID() + ":" + getName()).hashCode();
		}
		catch (SLGraphSessionException e) {
			throw new SLRuntimeException("Error on attempt to calculate property hash code.");
		}
	}
}
