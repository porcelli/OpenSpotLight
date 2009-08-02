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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentPropertyNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

/**
 * The Class SLMetaNodeImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLMetaNodeImpl implements SLMetaNode {
	
	/** The metadata. */
	private SLMetadata metadata;
	
	/** The p meta node. */
	private SLPersistentNode pMetaNode;
	
	/**
	 * Instantiates a new sL meta node impl.
	 * 
	 * @param metadata the metadata
	 * @param pNode the node
	 */
	SLMetaNodeImpl(SLMetadata metadata, SLPersistentNode pNode) {
		this.metadata = metadata;
		this.pMetaNode = pNode;
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaElement#getMetadata()
	 */
	public SLMetadata getMetadata() throws SLGraphSessionException {
		return metadata;
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaNode#getType()
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends SLNode> getType() throws SLGraphSessionException {
		try {
			return (Class<? extends SLNode>) Class.forName(pMetaNode.getName());
		}
		catch (Exception e) {
			throw new SLGraphSessionException("Error on attempt to retrieve node type.", e);
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaNode#getMetaProperties()
	 */
	public Collection<SLMetaNodeProperty> getMetaProperties() throws SLGraphSessionException {
		try {
			Collection<SLMetaNodeProperty> metaProperties = new HashSet<SLMetaNodeProperty>();
			Collection<SLPersistentProperty<Serializable>> pProperties = pMetaNode.getProperties(SLConsts.PROPERTY_PREFIX_USER.concat(".*"));
			for (SLPersistentProperty<Serializable> pProperty : pProperties) {
				SLMetaNodeProperty metaProperty = new SLMetaNodePropertyImpl(metadata, this, pProperty);
				metaProperties.add(metaProperty);
			}
			return metaProperties;
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node properties.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaNode#getMetaProperty(java.lang.String)
	 */
	public SLMetaNodeProperty getMetaProperty(String name) throws SLGraphSessionException {
		try {
			String propName = SLCommonSupport.toUserPropertyName(name);
			SLPersistentProperty<Serializable> pProperty = null;
			try {
				pProperty = pMetaNode.getProperty(Serializable.class, propName);	
			}
			catch (SLPersistentPropertyNotFoundException e) {}
			SLMetaNodeProperty metaProperty = null;
			if (pProperty != null) {
				metaProperty = new SLMetaNodePropertyImpl(metadata, this, pProperty);
			}
			return metaProperty;
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node property.", e);
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaNode#getMetaNode(java.lang.Class)
	 */
	public SLMetaNode getMetaNode(Class<? extends SLNode> nodeClass) throws SLGraphSessionException {
		try {
			SLMetaNode metaNode = null;
			SLPersistentNode pChildMetaNode = pMetaNode.getNode(nodeClass.getName());
			if (pChildMetaNode != null) {
				metaNode = new SLMetaNodeImpl(metadata, pChildMetaNode);
			}
			return metaNode;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaNode#getMetaNodes()
	 */
	public Collection<SLMetaNode> getMetaNodes() throws SLGraphSessionException {
		try {
			Collection<SLMetaNode> metaNodes = new ArrayList<SLMetaNode>();
			Collection<SLPersistentNode> pMetaNodes = pMetaNode.getNodes();
			for (SLPersistentNode pMetaNode : pMetaNodes) {
				SLMetaNode metaNode = new SLMetaNodeImpl(metadata, pMetaNode);
				metaNodes.add(metaNode);
			}
			return metaNodes;
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta nodes.", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaNode#getMetaRenderHint(java.lang.String)
	 */
	public SLMetaRenderHint getMetaRenderHint(String name) throws SLGraphSessionException {
		try {
			SLMetaRenderHint renderHint = null;
			String pattern = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_RENDER_HINT + "." + name);
			SLPersistentProperty<Serializable> pProperty = SLCommonSupport.getProperty(pMetaNode, Serializable.class, pattern);
			if (pProperty != null) {
				renderHint = new SLMetaRenderHintImpl(this, pProperty);
			}
			return renderHint;
		} 
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta render hint.", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaNode#getMetaRenderHints()
	 */
	public Collection<SLMetaRenderHint> getMetaRenderHints() throws SLGraphSessionException {
		try {
			Collection<SLMetaRenderHint> renderHints = new ArrayList<SLMetaRenderHint>();
			String pattern = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_RENDER_HINT) + ".*";
			Set<SLPersistentProperty<Serializable>> pProperties = pMetaNode.getProperties(pattern);
			for (SLPersistentProperty<Serializable> pProperty : pProperties) {
				SLMetaRenderHint renderHint = new SLMetaRenderHintImpl(this, pProperty);
				renderHints.add(renderHint);
			}
			return renderHints;
		} 
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta render hints.", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaNode#getDescription()
	 */
	public String getDescription() throws SLGraphSessionException {
		try {
			String propName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_DESCRIPTION);
			SLPersistentProperty<String> prop = SLCommonSupport.getProperty(pMetaNode, String.class, propName);
			return prop == null ? null : prop.getValue();
		} 
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node description.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof SLMetaNodeImpl)) return false;
		SLMetaNodeImpl metaNode = (SLMetaNodeImpl) obj;
		return pMetaNode.equals(metaNode);
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return pMetaNode.hashCode();
	}

}


