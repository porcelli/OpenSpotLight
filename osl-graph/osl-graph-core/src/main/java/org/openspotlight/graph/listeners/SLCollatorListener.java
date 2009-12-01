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

import static org.openspotlight.graph.SLCommonSupport.toSimplePropertyName;

import java.io.Serializable;
import java.text.Collator;

import org.openspotlight.graph.SLAbstractGraphSessionEventListener;
import org.openspotlight.graph.SLCollatorSupport;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNodePropertyEvent;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving SLCollator events.
 * The class that is interested in processing a SLCollator
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSLCollatorListener<code> method. When
 * the SLCollator event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see SLCollatorEvent
 */
public class SLCollatorListener extends SLAbstractGraphSessionEventListener {
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLAbstractGraphSessionEventListener#nodePropertySet(org.openspotlight.graph.SLNodePropertyEvent)
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void nodePropertySet(SLNodePropertyEvent event) throws SLGraphSessionException {
		try {
			SLPersistentProperty<? extends Serializable> pProperty = event.getPersistentProperty();
			if (pProperty.getValue() instanceof String) {
				String name = toSimplePropertyName(pProperty.getName());
				String value = pProperty.getValue().toString();
				
				String primaryKey = SLCollatorSupport.getCollatorKey(Collator.PRIMARY, value);
				String secondaryKey = SLCollatorSupport.getCollatorKey(Collator.SECONDARY, value);
				String tertiaryKey = SLCollatorSupport.getCollatorKey(Collator.TERTIARY, value);

				String primaryDescription = SLCollatorSupport.getCollatorDescription(Collator.PRIMARY, value);
				String secondaryDescription = SLCollatorSupport.getCollatorDescription(Collator.SECONDARY, value);
				String tertiaryDescription = SLCollatorSupport.getCollatorDescription(Collator.TERTIARY, value);

				String primaryKeyPropName = SLCollatorSupport.getCollatorKeyPropName(name, Collator.PRIMARY);
				String secondaryKeyPropName = SLCollatorSupport.getCollatorKeyPropName(name, Collator.SECONDARY);
				String tertiaryKeyPropName = SLCollatorSupport.getCollatorKeyPropName(name, Collator.TERTIARY);

				String primaryDescriptionPropName = SLCollatorSupport.getCollatorDescriptionPropName(name, Collator.PRIMARY);
				String secondaryDescriptionPropName = SLCollatorSupport.getCollatorDescriptionPropName(name, Collator.SECONDARY);
				String tertiaryDescriptionPropName = SLCollatorSupport.getCollatorDescriptionPropName(name, Collator.TERTIARY);

				SLPersistentNode pNode = pProperty.getNode();
				pNode.setProperty(String.class, primaryKeyPropName, primaryKey);
				pNode.setProperty(String.class, secondaryKeyPropName, secondaryKey);
				pNode.setProperty(String.class, tertiaryKeyPropName, tertiaryKey);
				
				pNode.setProperty(String.class, primaryDescriptionPropName, primaryDescription);
				pNode.setProperty(String.class, secondaryDescriptionPropName, secondaryDescription);
				pNode.setProperty(String.class, tertiaryDescriptionPropName, tertiaryDescription);
			}
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to update callation property data.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLAbstractGraphSessionEventListener#nodePropertyRemoved(org.openspotlight.graph.SLNodePropertyEvent)
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void nodePropertyRemoved(SLNodePropertyEvent event) throws SLGraphSessionException {
		try {
			if (event.isString()) {
				String name = event.getPropertyName();
				String primaryKeyPropName = SLCollatorSupport.getCollatorKeyPropName(name, Collator.PRIMARY);
				String secondaryKeyPropName = SLCollatorSupport.getCollatorKeyPropName(name, Collator.SECONDARY);
				String tertiaryKeyPropName = SLCollatorSupport.getCollatorKeyPropName(name, Collator.TERTIARY);
				String primaryDescriptionPropName = SLCollatorSupport.getCollatorDescriptionPropName(name, Collator.PRIMARY);
				String secondaryDescriptionPropName = SLCollatorSupport.getCollatorDescriptionPropName(name, Collator.SECONDARY);
				String tertiaryDescriptionPropName = SLCollatorSupport.getCollatorDescriptionPropName(name, Collator.TERTIARY);
				SLPersistentNode pNode = event.getPNode();
				pNode.getProperty(String.class, primaryKeyPropName).remove();
				pNode.getProperty(String.class, secondaryKeyPropName).remove();
				pNode.getProperty(String.class, tertiaryKeyPropName).remove();
				pNode.getProperty(String.class, primaryDescriptionPropName).remove();
				pNode.getProperty(String.class, secondaryDescriptionPropName).remove();
				pNode.getProperty(String.class, tertiaryDescriptionPropName).remove();
			}
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to remove callation property data.", e);
		}
	}
}

