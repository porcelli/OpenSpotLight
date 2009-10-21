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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentPropertyNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

/**
 * The Class SLMetaLinkImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLMetaLinkImpl implements SLMetaLink {
	
	/** The meta link node. */
	private SLPersistentNode metaLinkNode;
	
	/** The meta link type. */
	private SLMetaLinkType metaLinkType;
	
	/** The source type. */
	private Class<? extends SLNode> sourceType;
	
	/** The target type. */
	private Class<? extends SLNode> targetType;
	
	/** The side types. */
	private List<Class<? extends SLNode>> sideTypes;
	
	/** The bidirectional. */
	private boolean bidirectional;
	
	/**
	 * Instantiates a new sL meta link impl.
	 * 
	 * @param metaLinkNode the meta link node
	 * @param metaLinkType the meta link type
	 * @param sourceType the source type
	 * @param targetType the target type
	 * @param sideTypes the side types
	 * @param bidirectional the bidirectional
	 */
	SLMetaLinkImpl(SLPersistentNode metaLinkNode, SLMetaLinkType metaLinkType, Class<? extends SLNode> sourceType, Class<? extends SLNode> targetType, List<Class<? extends SLNode>> sideTypes, boolean bidirectional) {
		this.metaLinkNode = metaLinkNode;
		this.metaLinkType = metaLinkType;
		this.sourceType = sourceType;
		this.targetType = targetType;
		this.sideTypes = sideTypes;
		this.bidirectional = bidirectional;
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaLink#getMetaLinkType()
	 */
	public SLMetaLinkType getMetaLinkType() throws SLGraphSessionException {
		return metaLinkType;
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaLink#getSourceType()
	 */
	public Class<? extends SLNode> getSourceType() throws SLGraphSessionException {
		if (bidirectional) {
			// this method cannot be used on bidirecional meta links, because source and targets types are relatives.
			// on unidirecional links, source and target types are well defined.
			throw new UnsupportedOperationException("SLMetaLink.getSource() cannot be used on bidirecional meta links.");
		}
		return sourceType;
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaLink#getTargetType()
	 */
	public Class<? extends SLNode> getTargetType() throws SLGraphSessionException {
		if (bidirectional) {
			// this method cannot be used on bidirecional meta links, because source and targets types are relatives.
			// on unidirecional links, source and target types are well defined.
			throw new UnsupportedOperationException("SLMetaLink.getTarget() cannot be used on bidirecional meta links.");
		}
		return targetType;
	}

	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaLink#getOtherSideType(java.lang.Class)
	 */
	public Class<? extends SLNode> getOtherSideType(Class<? extends SLNode> sideType) throws SLInvalidMetaLinkSideTypeException, SLGraphSessionException {
		Class<? extends SLNode> otherSideType = null;
		if (sideType.equals(sourceType)) otherSideType = targetType;
		else if (sideType.equals(targetType)) otherSideType = sourceType;
		else throw new SLInvalidMetaLinkSideTypeException(); 
		return otherSideType;
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaLink#getSideTypes()
	 */
	public List<Class<? extends SLNode>> getSideTypes() throws SLGraphSessionException {
		return sideTypes;
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaLink#isBidirectional()
	 */
	public boolean isBidirectional() throws SLGraphSessionException {
		return bidirectional;
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaElement#getMetadata()
	 */
	public SLMetadata getMetadata() throws SLGraphSessionException {
		return metaLinkType.getMetadata();
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaLink#getMetaProperties()
	 */
	public Collection<SLMetaLinkProperty> getMetaProperties() throws SLGraphSessionException {
		try {
			Collection<SLMetaLinkProperty> metaProperties = new HashSet<SLMetaLinkProperty>();
			Collection<SLPersistentProperty<Serializable>> pProperties = metaLinkNode.getProperties(SLConsts.PROPERTY_PREFIX_USER.concat(".*"));
			for (SLPersistentProperty<Serializable> pProperty : pProperties) {
				SLMetaLinkProperty metaProperty = new SLMetaLinkPropertyImpl(this, pProperty);
				metaProperties.add(metaProperty);
			}
			return metaProperties;
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta link properties.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaLink#getMetaProperty(java.lang.String)
	 */
	public SLMetaLinkProperty getMetaProperty(String name) throws SLGraphSessionException {
		try {
			String propName = SLCommonSupport.toUserPropertyName(name);
			SLPersistentProperty<Serializable> pProperty = null;
			try {
				pProperty = metaLinkNode.getProperty(Serializable.class, propName);	
			}
			catch (SLPersistentPropertyNotFoundException e) {}
			SLMetaLinkProperty metaProperty = null;
			if (pProperty != null) {
				metaProperty = new SLMetaLinkPropertyImpl(this, pProperty);
			}
			return metaProperty;
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta link property.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaLink#getDescription()
	 */
	public String getDescription() throws SLGraphSessionException {
		try {
			String propName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_DESCRIPTION);
			SLPersistentProperty<String> prop = SLCommonSupport.getProperty(metaLinkNode, String.class, propName);
			return prop == null ? null : prop.getValue();
		} 
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node description.", e);
		}
	}

}


